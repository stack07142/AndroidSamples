package k.t.sample_ble.utils

import k.t.sample_ble.ble.BLEProfile
import java.nio.charset.StandardCharsets
import java.util.*

class DataSplitter(value: String) : LinkedList<ByteArray>((value.toByteArray(StandardCharsets.UTF_8) + BLEProfile.EOT)
    .toList()
    .chunked(20) {
        it.toByteArray()
    }) {
    var sentValue: ByteArray? = null
    fun next(): ByteArray? {
        return this.poll().also {
            sentValue = it
        }
    }
}
