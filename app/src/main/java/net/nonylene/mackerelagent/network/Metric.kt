package net.nonylene.mackerelagent.network

import net.nonylene.mackerelagent.host.metric.MetricVariable
import net.nonylene.mackerelagent.host.metric.MetricsContainer
import kotlin.reflect.memberProperties

/**
 * @param time: epoch time (seconds)
 */
data class Metric(val name: String, val time: Long, val value: Any)

fun createMetrics(metricsContainers: List<MetricsContainer>): List<Metric> {
    val metrics = metricsContainers.map(::createMetricsFromMetricContainer).reduce { acc, items ->
        items.forEach { item ->
            if (acc.any { it.name == item.name }) throw RuntimeException("key ${item.name} is duplicated")
        }
        items + acc
    }
    return metrics
}

private fun createMetricsFromMetricContainer(metricsContainer: MetricsContainer): List<Metric> {
    val kClazz = metricsContainer.javaClass.kotlin

    val customPrefix = when (metricsContainer) {
        is MetricsContainer.Default -> null
        is MetricsContainer.Custom -> "custom"
    }

    /**
     * [MetricsContainer.name] may contains illegal character -> sanitize
     */
    val keyPrefix = listOfNotNull(customPrefix, metricsContainer.prefix, metricsContainer.name?.let(::sanitize)).map { it + "." }.joinToString("")

    val values = kClazz.memberProperties.map { prop ->
        prop to prop.annotations.filterIsInstance(MetricVariable::class.java)
    }.filter { it.second.any() }.mapNotNull {
        val (prop, annotations) = it
        prop.get(metricsContainer)?.let { value ->
            Metric(
                    keyPrefix + annotations[0].key,
                    metricsContainer.timeStamp.time / 1000,
                    value
            )
        }
    }

    return values
}

// https://github.com/mackerelio/mackerel-agent/blob/master/util/sanitize.go

private val sanitizerRegex = Regex("[^A-Za-z0-9_-]")

private fun sanitize(key: String): String {
    return sanitizerRegex.replace(key, "_")
}

