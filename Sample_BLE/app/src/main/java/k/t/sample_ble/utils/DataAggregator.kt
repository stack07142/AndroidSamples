package k.t.sample_ble.utils

import k.t.sample_ble.ble.BLEProfile
import java.nio.charset.StandardCharsets
import java.util.*

class DataAggregator {

    private val list = LinkedList<ByteArray>()

    fun clear() {
        list.clear()
    }

    var isLast = false
        private set(value) {
            field = value
        }

    fun add(value: ByteArray) {
        when (value.lastOrNull()) {
            BLEProfile.EOT -> {
                isLast = true
                list.add(value.dropLast(1).toByteArray())
            }
            else -> {
                isLast = false
                list.add(value)
            }
        }
    }

    fun newStart(value: ByteArray) {
        clear()
        add(value)
    }

    fun getResult(): String {
        return list.reduce { merged, bytes -> merged + bytes }.toString(StandardCharsets.UTF_8)
    }
}