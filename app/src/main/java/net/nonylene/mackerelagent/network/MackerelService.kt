package net.nonylene.mackerelagent.network

import io.reactivex.Observable
import net.nonylene.mackerelagent.network.model.HostSpecRequest
import net.nonylene.mackerelagent.network.model.HostSpecResponse
import net.nonylene.mackerelagent.network.model.Metric
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MackerelService {

    @POST("/api/v0/tsdb")
    fun postMetrics(
            @Body
            metrics: List<Metric>
    ): Observable<ResponseBody>

    @POST("/api/v0/hosts")
    fun postHostSpec(
            @Body
            spec: HostSpecRequest
    ): Observable<HostSpecResponse>
}
