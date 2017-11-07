package net.nonylene.mackerelagent.host.spec

import com.google.gson.annotations.SerializedName
import java.io.File

// https://github.com/mackerelio/mackerel-agent/blob/master/spec/linux/block_device.go

fun getBlockDevicesSpec(): Map<String, BlockDeviceSpec> {
    val baseBlockDir = File("/sys/block")
    val blocks = baseBlockDir.list()
    return blocks.map {
        val blockDir = File(baseBlockDir, it)
        val blockSpec = BlockDeviceSpec(
                getBlockContentOrNull(blockDir, "size"),
                getBlockContentOrNull(blockDir, "removable"),
                getBlockContentOrNull(blockDir, "device/model"),
                getBlockContentOrNull(blockDir, "device/rev"),
                getBlockContentOrNull(blockDir, "device/state"),
                getBlockContentOrNull(blockDir, "device/timeout"),
                getBlockContentOrNull(blockDir, "device/vendor")
        )
        it to blockSpec
    }.toMap()
}

private fun getBlockContentOrNull(blockDir: File, fileName: String): String? {
    val file = File(blockDir, fileName)
    if (file.exists()) {
        return file.readLines()[0].trim()
    } else {
        return null
    }
}

data class BlockDeviceSpec(
        @SerializedName("size")
        val size: String?,
        @SerializedName("removable")
        val removable: String?,
        @SerializedName("model")
        val model: String?,
        @SerializedName("rev")
        val rev: String?,
        @SerializedName("state")
        val state: String?,
        @SerializedName("timeout")
        val timeout: String?,
        @SerializedName("vendor")
        val vendor: String?
)