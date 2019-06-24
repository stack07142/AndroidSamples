package k.t.sample_ble.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.ParcelUuid
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import org.apache.commons.lang3.reflect.MethodUtils
import timber.log.Timber
import java.lang.ref.WeakReference

object BLEAdvertiser {
    private var advertiser: BluetoothLeAdvertiser? = null
    private val callback = object : AdvertiseCallback() {
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
        advertiser?.startAdvertising(settings, data, callback)
    }

    fun stopAdvertise() {
        advertiser?.stopAdvertising(callback)
        advertiser = null
    }
}

object BLEScanner {
    fun startScan(): Observable<ScanResult> {
        return Observable.create { e ->
            val filters = ScanFilter.Builder().apply {
                setServiceUuid(ParcelUuid(BLEProfile.SERVICE_UUID))
            }.build()

            val settings = ScanSettings.Builder().apply {
                //setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            }.build()

            val callback = object : ScanCallback() {
                private val refEmitter = WeakReference(e)

                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    Timber.tag("BLE SCANNER").d("onScanResult : %d %s", callbackType, result)
                    onNext(result)
                }

                override fun onBatchScanResults(results: List<ScanResult>) {
                    Timber.tag("BLE SCANNER").d("onBatchScanResults(count) : %d", results.size)
                }

                override fun onScanFailed(errorCode: Int) {
                    Timber.tag("BLE SCANNER").e("onScanFailed %d", errorCode)
                    tryOnError(IllegalStateException("onScanFailed $errorCode"))
                }

                private fun onNext(result: ScanResult) {
                    val emitter = refEmitter.get()
                    emitter?.onNext(result)
                }

                private fun tryOnError(error: Throwable) {
                    val emitter = refEmitter.get()
                    emitter?.tryOnError(error)
                }
            }

            val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
            scanner.startScan(listOf(filters), settings, callback)

            e.setDisposable(Disposables.fromAction {
                Timber.d("stopScan")
                scanner.stopScan(callback)
                cleanup(scanner)
            })
        }
    }

    private fun cleanup(scanner: BluetoothLeScanner) {
        try {
            Timber.d("cleanup")
            MethodUtils.invokeMethod(scanner, true, "cleanup")
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}