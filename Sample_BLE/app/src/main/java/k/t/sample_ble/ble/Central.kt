package k.t.sample_ble.ble

import android.bluetooth.*
import android.content.Context
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.addTo
import k.t.sample_ble.utils.DataSplitter
import k.t.sample_ble.utils.LoggerAdapter
import timber.log.Timber
import java.util.*

/**
 * 1. With the [BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE] property,
 * the client can only use the "Write without response" ATT command, which is limited by the MTU.
 *
 * > When performing a write request operation (write without response), the data sent is truncated to the MTU size.
 *
 * > MTU default size: 20 bytes
 * The core spec defines the default MTU of the ATT to be 23 bytes.
 * After removing one byte of the ATT opcode and the ATT handle2 bytes, the remaining 20 bytes are reserved for the GATT
 *
 * > onCharacteristicWrite will be called.
 *
 * 2. With the [BluetoothGattCharacteristic.PROPERTY_WRITE] property,
 * the client can use both "Write with response" ATT request as well as the sequence of multiple prepare writes followed by execute write, also known as "Long write".
 * With long writes, the client (automatically) splits up the write in different chunks with offsets.
 * Note that Long write take considerably more time than if you just increase the MTU due to the number of multiple round trips needed.
 */
object Central {
    private var logger: LoggerAdapter.EventListener? = null

    fun setEventListener(eventListener: LoggerAdapter.EventListener) {
        this.logger = eventListener
    }

    private fun log(msg: String) {
        AndroidSchedulers.mainThread().scheduleDirect {
            Timber.d(msg)
            logger?.log(msg)
        }
    }

    fun writeWithoutResponse(context: Context, device: BluetoothDevice, data: String): Completable {
        Timber.d("writeWithoutResponse")
        return Completable.create { emitter ->
            val compositeDisposable = CompositeDisposable().also { emitter.setDisposable(it) }
            var mtuSize: Int? = null
            val callback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    super.onConnectionStateChange(gatt, status, newState)
                    log("onConnectionStateChange: status= ${BLEUtils.getReadableStatus(status)}, newState= ${BLEUtils.getReadableState(newState)}")
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> gatt.discoverServices()
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            emitter.onError(RuntimeException("gatt disconnected"))
                        }
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    super.onServicesDiscovered(gatt, status)
                    log("onServicesDiscovered: status= ${BLEUtils.getReadableStatus(status)}")
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> {
                            val gattService = gatt.getService(BLEProfile.SERVICE_UUID)
                            if (gattService != null) {
                                val dataToWrite = data.toByteArray() + BLEProfile.EOT
                                gattService.getCharacteristic(BLEProfile.CHARACTERISTIC_WRITE_NO_RESPONSE_UUID).apply {
                                    writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                                    value = dataToWrite
                                }.let {
                                    gatt.writeCharacteristic(it)
                                }.also { initiated ->
                                    if (initiated) {
                                        log("write operation was initiated successfully\ndata(${data.toByteArray().size})= $data")
                                        if (dataToWrite.size > (mtuSize ?: 20)) {
                                            log("If data length is longer than mtu size(${mtuSize ?: 20}), the rest will be cut off")
                                        }
                                    } else {
                                        emitter.onError(RuntimeException("write operation was not initiated"))
                                    }
                                }
                            } else {
                                emitter.onError(RuntimeException("NA Service"))
                            }
                        }
                        else -> emitter.onError(RuntimeException("onServiceDiscovered received: ${BLEUtils.getReadableStatus(status)}"))
                    }
                }

                override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                    super.onCharacteristicWrite(gatt, characteristic, status)
                    log("onCharacteristicWrite: status= ${BLEUtils.getReadableStatus(status)}")
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> emitter.onComplete()
                        else -> emitter.onError(RuntimeException("result of the write operation: ${BLEUtils.getReadableStatus(status)}"))
                    }
                }

                override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
                    super.onMtuChanged(gatt, mtu, status)
                    log("onMtuChanged: status= ${BLEUtils.getReadableStatus(status)}, mtu size= $mtu")
                    mtuSize = mtu
                }
            }

            log("connectGatt ${device.name}@${device.address}")
            var gatt = device.connectGattCompat(context, false, callback)
            if (gatt == null) {
                emitter.onError(RuntimeException("Unable to connectGatt"))
            } else {
                Disposables.fromAction {
                    log("disconnectGatt ${device.name}@${device.address}")
                    gatt?.close()
                    gatt = null
                }.addTo(compositeDisposable)
            }
        }
    }

    fun write(context: Context, device: BluetoothDevice, data: String): Completable {
        Timber.d("write")
        val dataToWrite = DataSplitter(data)
        return Completable.create { emitter ->
            val compositeDisposable = CompositeDisposable().also { emitter.setDisposable(it) }
            val callback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    super.onConnectionStateChange(gatt, status, newState)
                    log("onConnectionStateChange: status= ${BLEUtils.getReadableStatus(status)}, newState= ${BLEUtils.getReadableState(newState)}")
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> gatt.discoverServices()
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            emitter.onError(RuntimeException("gatt disconnected"))
                        }
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    super.onServicesDiscovered(gatt, status)
                    log("onServicesDiscovered: status= ${BLEUtils.getReadableStatus(status)}")
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> {
                            val gattService = gatt.getService(BLEProfile.SERVICE_UUID)
                            if (gattService != null) {
                                gattService.getCharacteristic(BLEProfile.CHARACTERISTIC_WRITE_UUID).apply {
                                    writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                                    value = dataToWrite.next()
                                }.let {
                                    gatt.beginReliableWrite()
                                    gatt.writeCharacteristic(it)
                                }.also { initiated ->
                                    if (initiated) {
                                        log("write operation was initiated successfully\ndata(${data.toByteArray().size})= $data")
                                    } else {
                                        emitter.onError(RuntimeException("write operation was not initiated"))
                                    }
                                }
                            } else {
                                emitter.onError(RuntimeException("NA Service"))
                            }
                        }
                        else -> emitter.onError(RuntimeException("onServiceDiscovered received: ${BLEUtils.getReadableStatus(status)}"))
                    }
                }

                override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                    super.onCharacteristicWrite(gatt, characteristic, status)
                    val reportedValue = characteristic.value
                    log("onCharacteristicWrite: status= ${BLEUtils.getReadableStatus(status)}, reported value= ${String(reportedValue)}")
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> {
                            if (Arrays.equals(dataToWrite.sentValue, reportedValue)) {
                                val nextValue = dataToWrite.next()
                                if (nextValue == null) {
                                    gatt.executeReliableWrite()
                                } else {
                                    gatt.writeCharacteristic(characteristic.apply {
                                        value = nextValue
                                    })
                                }
                            } else {
                                gatt.abortReliableWrite()
                                emitter.onError(RuntimeException("values don't match"))
                            }
                        }
                        else -> emitter.onError(RuntimeException("result of the write operation: ${BLEUtils.getReadableStatus(status)}"))
                    }
                }

                override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
                    super.onReliableWriteCompleted(gatt, status)
                    log("onReliableWriteCompleted: status= ${BLEUtils.getReadableStatus(status)}")
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> emitter.onComplete()
                        else -> emitter.onError(RuntimeException("onReliableWrite Not Completed: status= ${BLEUtils.getReadableStatus(status)}"))
                    }
                }
            }

            log("connectGatt ${device.name}@${device.address}")
            var gatt = device.connectGattCompat(context, false, callback)
            if (gatt == null) {
                emitter.onError(RuntimeException("Unable to connectGatt"))
            } else {
                Disposables.fromAction {
                    log("disconnectGatt ${device.name}@${device.address}")
                    gatt?.close()
                    gatt = null

                }.addTo(compositeDisposable)
            }
        }
    }

    /*fun write(context: Context, device: BluetoothDevice, data: String): Completable {
        Timber.d("write")
        val dataToWrite = data.toByteArray() + BLEProfile.EOT
        return Completable.create { emitter ->
            val compositeDisposable = CompositeDisposable().also { emitter.setDisposable(it) }
            val callback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    super.onConnectionStateChange(gatt, status, newState)
                    log("onConnectionStateChange: status= ${BLEUtils.getReadableStatus(status)}, newState= ${BLEUtils.getReadableState(newState)}")
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> gatt.discoverServices()
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            emitter.onError(RuntimeException("gatt disconnected"))
                        }
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    super.onServicesDiscovered(gatt, status)
                    log("onServicesDiscovered: status= ${BLEUtils.getReadableStatus(status)}")
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> {
                            val gattService = gatt.getService(BLEProfile.SERVICE_UUID)
                            if (gattService != null) {
                                gattService.getCharacteristic(BLEProfile.CHARACTERISTIC_WRITE_UUID).apply {
                                    writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                                    value = dataToWrite
                                }.let {
                                    gatt.beginReliableWrite()
                                    gatt.writeCharacteristic(it)
                                }.also { initiated ->
                                    if (initiated) {
                                        log("write operation was initiated successfully\ndata(${data.toByteArray().size})= $data")
                                    } else {
                                        emitter.onError(RuntimeException("write operation was not initiated"))
                                    }
                                }
                            } else {
                                emitter.onError(RuntimeException("NA Service"))
                            }
                        }
                        else -> emitter.onError(RuntimeException("onServiceDiscovered received: ${BLEUtils.getReadableStatus(status)}"))
                    }
                }

                override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                    super.onCharacteristicWrite(gatt, characteristic, status)
                    log("onCharacteristicWrite: status= ${BLEUtils.getReadableStatus(status)}, reported value= ${String(characteristic.value)}")
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> {
                            if (Arrays.equals(dataToWrite, characteristic.value)) {
                                gatt.executeReliableWrite()
                            } else {
                                gatt.abortReliableWrite()
                            }
                        }
                        else -> emitter.onError(RuntimeException("result of the write operation: ${BLEUtils.getReadableStatus(status)}"))
                    }
                }

                override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
                    super.onReliableWriteCompleted(gatt, status)
                    log("onReliableWriteCompleted: status= ${BLEUtils.getReadableStatus(status)}")
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> emitter.onComplete()
                        else -> emitter.onError(RuntimeException("onReliableWrite Not Completed: status= ${BLEUtils.getReadableStatus(status)}"))
                    }
                }
            }

            log("connectGatt ${device.name}@${device.address}")
            var gatt = device.connectGattCompat(context, false, callback)
            if (gatt == null) {
                emitter.onError(RuntimeException("Unable to connectGatt"))
            } else {
                Disposables.fromAction {
                    log("disconnectGatt ${device.name}@${device.address}")
                    gatt?.close()
                    gatt = null

                }.addTo(compositeDisposable)
            }
        }
    }*/
}