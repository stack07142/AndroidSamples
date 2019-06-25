package k.t.sample_ble.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.ParcelUuid
import timber.log.Timber

object BLEAdvertiser {
    private var advertiser: BluetoothLeAdvertiser? = null
    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Timber.d("Advertising successfully started")
        }

        override fun onStartFailure(errorCode: Int) {
            Timber.d("Advertising failed")
        }
    }

    fun startAdvertise() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val settings = AdvertiseSettings.Builder().apply {
            setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            setConnectable(true)
            setTimeout(0)
            setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
        }.build()

        val data = AdvertiseData.Builder().apply {
            setIncludeDeviceName(true)
            addServiceUuid(ParcelUuid(BLEProfile.SERVICE_UUID))
        }.build()

        advertiser = bluetoothAdapter.bluetoothLeAdvertiser
        advertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    fun stopAdvertise() {
        advertiser?.stopAdvertising(advertiseCallback)
        advertiser = null
    }
}