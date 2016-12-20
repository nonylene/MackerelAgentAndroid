package net.nonylene.mackerelagent.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.Index
import net.nonylene.mackerelagent.network.model.Metric
import net.nonylene.mackerelagent.utils.GSON_IGNORE_EXCLUDE_ANNOTATION
import java.util.*
import java.util.concurrent.atomic.AtomicLong

var AUTO_INCREMENT_VAL: AtomicLong? = null

// class for cache metric as json
open class RealmMetricJson : RealmObject() {
//    @PrimaryKey
    open var cacheId: Long = 0
    open var jsonString: String = ""
    @Index
    open var timeStamp = Date(0)

    fun createMetric(): Metric {
        return GSON_IGNORE_EXCLUDE_ANNOTATION.fromJson(jsonString, Metric::class.java)
    }
}

fun Realm.createRealmMetricJson(metric: Metric): RealmMetricJson {
    if (AUTO_INCREMENT_VAL == null) {
        AUTO_INCREMENT_VAL = AtomicLong(where(RealmMetricJson::class.java).max("cacheId")?.toLong() ?: 0 + 1)
    }
    return createObject(RealmMetricJson::class.java).apply {
        cacheId = AUTO_INCREMENT_VAL!!.incrementAndGet()
        jsonString = GSON_IGNORE_EXCLUDE_ANNOTATION.toJson(metric)
        timeStamp = Date()
    }
}
