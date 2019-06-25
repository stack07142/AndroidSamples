package k.t.sample_ble.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.lang.ref.WeakReference

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
                BLEUtils.cleanup(scanner)
            })
        }
    }
}