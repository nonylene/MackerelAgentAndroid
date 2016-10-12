package net.nonylene.mackerelagent.metric

import net.nonylene.mackerelagent.network.createMetrics
import java.util.*

/**
 * empty interface for type-safe
 */

sealed class MetricsContainer(val prefix: String?, val name: String?, val timeStamp: Date) {
    abstract class Default(prefix: String?, name: String?, timeStamp: Date) : MetricsContainer(prefix, name, timeStamp)
    abstract class Custom(prefix: String?, name: String?, timeStamp: Date) : MetricsContainer(prefix, name, timeStamp)
}

/**
 * metric A.B.C
 * -> A: class key ([MetricsContainer.prefix] / [MetricsContainer.Default])
 * -> B: name key ([MetricsContainer.name])
 * -> C: variable key ([MetricVariable])
 *
//  class Example(
//       @MetricVariable("key")
//       val variable: String,
//       val name: String,
//       timeStamp: Date
//  ): MetricsContainer.Default("prefix", name, timeStamp)
//
// => {"prefix.#{name}.key": variable}
 *
 * metric A.B
 * -> A: name key ([MetricsContainer.name]) or prefix key([MetricsContainer.prefix])
 * -> A: variable key ([MetricVariable])
 *
//  class Example(
//       @MetricName
//       val name: String,
//       @MetricVariable("key")
//       val variable: String,
//       name: String,
//       timeStamp: Date
//  ): MetricsContainer.Default(null, name, timeStamp)
//
// => {"key.#{name}": variable}
 *
//  class Example(
//       @MetricVariable("key")
//       val variable: String,
//       timeStamp: Date
//  ): MetricsContainer.Default("prefix", null, timeStamp)
//
// => {"prefix.key": variable}
 *
 * metric A
 * -> A: variable key ([MetricVariable])
 *
//  class Example(
//       @MetricVariable("key")
//       val variable: String,
//       timeStamp: Date
//  ): MetricsContainer.Default(null, null, timeStamp)
//
// => {"key": variable}
 *
 * custom metric custom.A.B.C
 * -> A: custom class key ([MetricsContainer.prefix] / [MetricsContainer.Custom])
 * -> B: name key ([MetricsContainer.name])
 * -> C: variable key ([MetricVariable])
 *
//  class Example(
//       @MetricVariable("key")
//       val variable: String,
//       name: String,
//       timeStamp: Date
//  ): MetricsContainer.Custom("prefix", name, timeStamp)
//
// => {"custom.prefix.#{name}.key": variable}
 *
 * @see createMetrics
 */
@Target(AnnotationTarget.PROPERTY)
annotation class MetricVariable(val key: String)
