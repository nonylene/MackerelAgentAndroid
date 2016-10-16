package net.nonylene.mackerelagent.host.spec

import java.io.File

// https://github.com/mackerelio/mackerel-agent/blob/master/spec/linux/block_device.go

fun getBlockDevicesSpecs(): Map<String, BlockDeviceSpec> {
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
        return file.readText()
    } else {
        return null
    }
}

data class BlockDeviceSpec(
        val size: String?,
        val removable: String?,
        val model: String?,
        val rev: String?,
        val state: String?,
        val timeout: String?,
        val vendor: String?
)