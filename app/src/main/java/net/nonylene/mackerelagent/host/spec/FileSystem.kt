package net.nonylene.mackerelagent.host.spec

import net.nonylene.mackerelagent.host.common.getFileSystemStats

// XXX: IGNORE DUPLICATE OF NAME (e.g. "tmpfs")
fun getFileSystemsSpec(): Map<String, FileSystemSpec> {
    return getFileSystemStats().map {
        it.name to FileSystemSpec(it.kbSize, it.kbUsed, it.kbAvailable, it.percentUsed, it.mount)
    }.toMap()
}

data class FileSystemSpec(
        val kbSize: Long,
        val kbUsed: Long,
        val kbAvailable: Long,
        val percentUsed: Double,
        val mount: String
)