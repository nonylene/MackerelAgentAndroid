package net.nonylene.mackerelagent.network

import io.reactivex.Observable
import net.nonylene.mackerelagent.network.model.HostSpecRequest
import net.nonylene.mackerelagent.network.model.HostSpecResponse
import net.nonylene.mackerelagent.network.model.Metric
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @PUT("/api/v0/hosts/{id}")
    fun updateHostSpec(
            @Path("id")
            hostId: String,
            @Body
            spec: HostSpecRequest
    ): Observable<HostSpecResponse>
}
