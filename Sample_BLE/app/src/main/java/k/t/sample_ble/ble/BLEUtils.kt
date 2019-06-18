package k.t.sample_ble.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.ParcelUuid
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import org.apache.commons.lang3.reflect.MethodUtils
import timber.log.Timber
import java.lang.ref.WeakReference

object BLEUtils {
    fun startScan(): Observable<List<ScanResult>> {
        return Observable.create { e ->
            val filters = ScanFilter.Builder().apply {
                setServiceUuid(ParcelUuid(BLEProfile.SERVICE_UUID))
            }.build()

            val settings = ScanSettings.Builder().apply {
                setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                setReportDelay(2000L)
            }.build()

            val callback = object : ScanCallback() {
                private val refEmitter = WeakReference(e)

                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    Timber.tag("BLE SCANNER").d("onScanResult : %d %s", callbackType, result)
                }

                override fun onBatchScanResults(results: List<ScanResult>) {
                    Timber.tag("BLE SCANNER").d("onBatchScanResults(count) : %d", results.size)
                    onNext(results)
                }

                override fun onScanFailed(errorCode: Int) {
                    Timber.tag("BLE SCANNER").e("onScanFailed %d", errorCode)
                    tryOnError(IllegalStateException("onScanFailed $errorCode"))
                }

                private fun onNext(results: List<ScanResult>) {
                    val emitter = refEmitter.get()
                    emitter?.onNext(results)
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