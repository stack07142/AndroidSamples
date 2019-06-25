package k.t.sample_ble.ble

import android.bluetooth.*
import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import k.t.sample_ble.BaseApplication
import k.t.sample_ble.utils.LoggerAdapter
import timber.log.Timber

object Peripheral {
    private val appContext: Context
        get() = BaseApplication.appContext

    private var logger: LoggerAdapter.EventListener? = null

    fun openGattServer(logger: LoggerAdapter.EventListener): BluetoothGattServer? {
        this.logger = logger
        return gattServer
    }

    private val bluetoothGattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            log(
                "onConnectionStateChange" +
                        "\nname: ${device.name}" +
                        "\nstatus: ${BLEUtils.getReadableStatus(status)}" +
                        "\nnewState: ${BLEUtils.getReadableState(newState)}"
            )
        }

        override fun onNotificationSent(device: BluetoothDevice, status: Int) {
            super.onNotificationSent(device, status)
            log("onNotificationSent\nname: ${device.name}\nstatus: ${BLEUtils.getReadableStatus(status)}")
        }

        override fun onExecuteWrite(device: BluetoothDevice, requestId: Int, execute: Boolean) {
            super.onExecuteWrite(device, requestId, execute)
            log("onExecuteWrite\nname: ${device.name}\nrequestId: $requestId\nexecute: $execute")

            val writtenValue = gattServer?.getService(BLEProfile.SERVICE_UUID)?.getCharacteristic(BLEProfile.CHARACTERISTIC_WRITE_UUID)?.value
            log("writtenValue: ${String(writtenValue ?: "null".toByteArray())}")

            gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray
        ) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
            log("onCharacteristicWriteRequest" +
                    "\nname: ${device.name}" +
                    "\nrequestId: $requestId" +
                    "\nuuid: ${BLEUtils.getReadableUUID(characteristic.uuid)}" +
                    "\npreparedWrite: $preparedWrite" +
                    "\nvalue: ${String(value)}" +
                    "\nresponseNeeded: $responseNeeded" +
                    "\noffset: $offset")

            if (responseNeeded) {
                // 데이터 검사 후 정상이면 GATT_SUCCESS
                log("response: ${String(value)}")
                gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value) // todo: offset 과 value 용도를 모르겠다
            }
        }

        override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            log("onCharacteristicReadRequest\nname: ${device.name}\nuuid: ${BLEUtils.getReadableUUID(characteristic.uuid)}\nrequestId: $requestId\noffset: $offset")
        }
    }

    private fun log(msg: String) {
        AndroidSchedulers.mainThread().scheduleDirect {
            Timber.d(msg)
            logger?.log(msg)
        }
    }

    private var gattServer: BluetoothGattServer? = null
        get() {
            if (field == null) {
                field =
                    (appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.let { bluetoothManager ->
                        bluetoothManager.openGattServer(appContext, bluetoothGattServerCallback)?.also { server ->
                            server.addService(
                                BluetoothGattService(
                                    BLEProfile.SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY
                                ).apply {
                                    addCharacteristic(
                                        // Write
                                        BluetoothGattCharacteristic(
                                            BLEProfile.CHARACTERISTIC_WRITE_UUID,
                                            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
                                            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
                                        )
                                    )

                                    addCharacteristic(
                                        // Write with no response
                                        BluetoothGattCharacteristic(
                                            BLEProfile.CHARACTERISTIC_WRITE_NO_RESPONSE_UUID,
                                            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                                            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
                                        )
                                    )
                                }
                            )
                        }
                    }
            }
            return field
        }
}