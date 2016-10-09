package net.nonylene.mackerelagent.metric

/**
 * empty interface for type-safe
 */
interface DefaultMetric
interface CustomMetric

/**
 * metric A.B.C
 * -> A: class key ([MetricPrefix] / [DefaultMetric])
 * -> B: name key ([MetricName])
 * -> C: variable key ([MetricVariable])
 *
//  @MetricPrefix("prefix")
//  class Example(
//       @MetricName
//       val name: String
//       @MetricVariable("key")
//       val variable: String
//  ): DefaultMetric
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
//  ): DefaultMetric
//
// => {"key.#{name}": variable}
 *
//  @MetricPrefix("prefix")
//  class Example(
//       @MetricVariable("key")
//       val variable: String
//  ): DefaultMetric
//
// => {"prefix.key": variable}
 *
 * metric A
 * -> A: variable key ([MetricVariable])
 *
//  class Example(
//       @MetricVariable("key")
//       val variable: String
//  ): DefaultMetric
//
// => {"key": variable}
 *
 * custom metric custom.A.B.C
 * -> A: custom class key ([MetricPrefix] / [CustomMetric])
 * -> B: name key ([MetricName])
 * -> C: variable key ([MetricVariable])
 *
//  @MetricPrefix("prefix")
//  class Example(
//       @MetricName
//       val name: String
//       @MetricVariable("key")
//       val variable: String
//  ): CustomMetric
//
// => {"custom.prefix.#{name}.key": variable}
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class MetricVariable(val key: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class MetricName

@Target(AnnotationTarget.CLASS)
annotation class MetricPrefix(val prefix: String)
