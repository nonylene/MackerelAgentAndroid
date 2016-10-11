package net.nonylene.mackerelagent.metric

import kotlin.reflect.memberProperties

/**
 * empty interface for type-safe
 */

sealed class Metric {
    open class DefaultMetric : Metric()
    open class CustomMetric : Metric()
}

/**
 * metric A.B.C
 * -> A: class key ([MetricPrefix] / [Metric.DefaultMetric])
 * -> B: name key ([MetricName])
 * -> C: variable key ([MetricVariable])
 *
//  @MetricPrefix("prefix")
//  class Example(
//       @MetricName
//       val name: String
//       @MetricVariable("key")
//       val variable: String
//  ): Metric.DefaultMetric()
//
// => {"prefix.#{name}.key": variable}
 *
 * metric A.B
 * -> A: name key ([MetricName]) or prefix key([MetricPrefix])
 * -> A: variable key ([MetricVariable])
 *
//  class Example(
//       @MetricName
//       val name: String
//       @MetricVariable("key")
//       val variable: String
//  ): Metric.DefaultMetric()
//
// => {"key.#{name}": variable}
 *
//  @MetricPrefix("prefix")
//  class Example(
//       @MetricVariable("key")
//       val variable: String
//  ): Metric.DefaultMetric()
//
// => {"prefix.key": variable}
 *
 * metric A
 * -> A: variable key ([MetricVariable])
 *
//  class Example(
//       @MetricVariable("key")
//       val variable: String
//  ): Metric.DefaultMetric()
//
// => {"key": variable}
 *
 * custom metric custom.A.B.C
 * -> A: custom class key ([MetricPrefix] / [Metric.CustomMetric])
 * -> B: name key ([MetricName])
 * -> C: variable key ([MetricVariable])
 *
//  @MetricPrefix("prefix")
//  class Example(
//       @MetricName
//       val name: String
//       @MetricVariable("key")
//       val variable: String
//  ): Metric.CustomMetric()
//
// => {"custom.prefix.#{name}.key": variable}
 */
@Target(AnnotationTarget.PROPERTY)
annotation class MetricVariable(val key: String)

@Target(AnnotationTarget.PROPERTY)
annotation class MetricName

@Target(AnnotationTarget.CLASS)
annotation class MetricPrefix(val prefix: String)

fun createMetricsJsonMap(metrics: List<Metric>): Map<String, String> {
    return metrics.map(::createMetricJsonMap).reduce { result, map ->
        map.forEach { entry ->
            if (result.contains(entry.key)) throw IllegalStateException("key $entry.key is duplicated")
        }
        result + map
    }
}

private fun createMetricJsonMap(metric: Metric): Map<String, String> {
    val kClazz = metric.javaClass.kotlin

    val customPrefix = when (metric) {
        is Metric.DefaultMetric -> null
        is Metric.CustomMetric -> "custom"
    }

    val classPrefix = kClazz.annotations.filterIsInstance(MetricPrefix::class.java).let { annotations ->
        if (annotations.any()) annotations[0].prefix else null
    }

    val nameField = kClazz.memberProperties.singleOrNull { field ->
        field.annotations.filterIsInstance(MetricName::class.java).any()
    }?.let {
        sanitize(it.get(metric).toString())
    }

    val keyPrefix = listOfNotNull(customPrefix, classPrefix, nameField).map { it + "." }.joinToString("")

    val values = kClazz.memberProperties.map { prop ->
        prop to prop.annotations.filterIsInstance(MetricVariable::class.java)
    }.filter { it.second.any() }.map {
        val (prop, annotations) = it
        (keyPrefix + annotations[0].key) to prop.get(metric).toString()
    }.toMap()

    return values
}

// https://github.com/mackerelio/mackerel-agent/blob/master/util/sanitize.go

private val sanitizerRegex = Regex("[^A-Za-z0-9_-]")

private fun sanitize(key: String): String {
    return sanitizerRegex.replace(key, "_")
}
