package net.nonylene.mackerelagent.utils

import android.os.Build
import io.reactivex.Observable
import io.reactivex.functions.Function3
import net.nonylene.mackerelagent.BuildConfig
import net.nonylene.mackerelagent.host.metric.*
import net.nonylene.mackerelagent.host.spec.*
import net.nonylene.mackerelagent.network.model.HostSpecRequest

// create observable with take 1 (do not repeat / do not overwrite)
fun createMetricsCombineLatestObservable(take: Long = 1): Observable<List<MetricsContainer>> {
    return Observable.combineLatest(getInterfaceMetricsListObservable().take(1),
            getDiskMetricsListObservable().take(1), getCPUMetricsObservable().take(1),
            // ??? cannot replace with only lambda
            Function3 { interfaceDeltas: List<InterfaceDeltaMetrics>, diskDeltas: List<DiskDeltaMetrics>,
                        cpuPercentage: CPUPercentageMetrics ->
                // ??? cannot replace with no cast expression
                (interfaceDeltas + diskDeltas + cpuPercentage + getFileSystemMetricsList() + getMemoryMetrics() + getLoadAverageMetrics()) as List<MetricsContainer>
            })
            .take(take)
}

fun createHostSpecRequest(): HostSpecRequest {
    return HostSpecRequest(
            "${Build.MANUFACTURER} ${Build.MODEL}",
            HostSpecRequest.Meta(
                    BuildConfig.VERSION_NAME,
                    getBlockDevicesSpec(),
                    getCPUSpec(),
                    getFileSystemsSpec(),
                    getKernelSpec(),
                    getMemorySpec()
            )
    )
}
