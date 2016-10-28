package net.nonylene.mackerelagent.host.spec

import com.google.gson.annotations.SerializedName
import net.nonylene.mackerelagent.host.common.getFileSystemStats

// XXX: IGNORE DUPLICATE OF NAME (e.g. "tmpfs")
// https://github.com/mackerelio/mackerel-agent/blob/master/spec/filesystem.go
fun getFileSystemsSpec(): Map<String, FileSystemSpec> {
    return getFileSystemStats().map {
        it.name to FileSystemSpec(it.kbSize, it.kbUsed, it.kbAvailable, "${it.percentUsed.toInt()}%", it.mount)
    }.toMap()
}

data class FileSystemSpec(
        @SerializedName("kb_size")
        val kbSize: Long,
        @SerializedName("kb_used")
        val kbUsed: Long,
        @SerializedName("kb_available")
        val kbAvailable: Long,
        @SerializedName("percent_used")
        val percentUsed: String,
        @SerializedName("mount")
        val mount: String
)