package net.nonylene.mackerelagent.network.model

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.annotations.SerializedName
import net.nonylene.mackerelagent.host.metric.MetricVariable
import net.nonylene.mackerelagent.host.metric.MetricsContainer
import net.nonylene.mackerelagent.utils.getHostId
import kotlin.reflect.memberProperties

/**
 * https://mackerel.io/ja/api-docs/entry/host-metrics
 * @param time: epoch time (seconds)
 */
data class Metric(
        @SerializedName("hostId")
        val hostId: String?,
        @SerializedName("name")
        val name: String,
        @SerializedName("time")
        val time: Long,
        @SerializedName("value")
        val value: Number
)

fun createMetrics(metricsContainers: List<MetricsContainer>, context: Context): List<Metric> {
    val metrics = metricsContainers
            .map { createMetricsFromMetricContainer(it, context) }
            .reduce { acc, items -> items + acc }
    return metrics
}

private fun createMetricsFromMetricContainer(metricsContainer: MetricsContainer, context: Context): List<Metric> {
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
                    //todo: fail if host id is null
                    PreferenceManager.getDefaultSharedPreferences(context).getHostId(context),
                    keyPrefix + annotations[0].key,
                    metricsContainer.timeStamp.time / 1000,
                    // metric value must be number
                    value as Number
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

