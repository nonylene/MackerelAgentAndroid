package net.nonylene.mackerelagent.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.Index
import net.nonylene.mackerelagent.network.model.Metric
import net.nonylene.mackerelagent.utils.GSON_IGNORE_EXCLUDE_ANNOTATION
import java.util.*

// class for cache metric as json
open class RealmMetricJson : RealmObject() {
    open var jsonString: String = ""
    @Index
    open var timeStamp = Date(0)

    fun createMetric(): Metric {
        return GSON_IGNORE_EXCLUDE_ANNOTATION.fromJson(jsonString, Metric::class.java)
    }
}

fun Realm.createMetricJson(metric: Metric): RealmMetricJson {
    return createObject(RealmMetricJson::class.java).apply {
        jsonString = GSON_IGNORE_EXCLUDE_ANNOTATION.toJson(metric)
        timeStamp = Date()
    }
}
