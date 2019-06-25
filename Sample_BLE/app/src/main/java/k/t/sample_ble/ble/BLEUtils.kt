package k.t.sample_ble.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.os.Build
import org.apache.commons.lang3.reflect.MethodUtils
import timber.log.Timber
import java.util.*

object BLEUtils {
    fun getReadableState(state: Int): String {
        return when (state) {
            BluetoothProfile.STATE_CONNECTED -> "Connected"
            BluetoothProfile.STATE_CONNECTING -> "Connecting"
            BluetoothProfile.STATE_DISCONNECTED -> "Disconnected"
            BluetoothProfile.STATE_DISCONNECTING -> "Disconnecting"
            else -> "Unknown State $state"
        }
    }

    fun getReadableStatus(status: Int): String {
        return when (status) {
            BluetoothGatt.GATT_SUCCESS -> "SUCCESS"
            else -> "Unknown Status $status"
        }
    }

    fun getReadableUUID(uuid: UUID): String {
        return when(uuid) {
            BLEProfile.CHARACTERISTIC_WRITE_UUID -> "WRITE"
            BLEProfile.CHARACTERISTIC_WRITE_NO_RESPONSE_UUID -> "WRITE_WITHOUT_RESPONSE"
            else -> ""
        }
    }

    fun cleanup(scanner: BluetoothLeScanner) {
        try {
            Timber.d("cleanup")
            MethodUtils.invokeMethod(scanner, true, "cleanup")
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}

fun BluetoothDevice.connectGattCompat(context: Context, autoConnect: Boolean, callback: BluetoothGattCallback): BluetoothGatt? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.connectGatt(context, autoConnect, callback, BluetoothDevice.TRANSPORT_LE)
    } else {
        this.connectGatt(context, autoConnect, callback)
    }
}