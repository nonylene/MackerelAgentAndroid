package net.nonylene.mackerelagent.network.model

import com.google.gson.annotations.SerializedName
import net.nonylene.mackerelagent.host.spec.*

class HostSpecRequest(
        @SerializedName("name") val name: String,
        @SerializedName("meta") val meta: Meta) {

    class Meta(
            @SerializedName("agent-version")
            val versionName: String,
            @SerializedName("block_device")
            val blockDevice: BlockDeviceSpec,
            @SerializedName("cpu")
            val cpu: List<CPUCoreSpec>,
            @SerializedName("filesystem")
            val fileSystem: Map<String, FileSystemSpec>,
            @SerializedName("kernel")
            val kernel: KernelSpec,
            @SerializedName("memory")
            val memory: MemorySpec
    )
}

class HostSpecResponse(@SerializedName("id") val hostId: String)
