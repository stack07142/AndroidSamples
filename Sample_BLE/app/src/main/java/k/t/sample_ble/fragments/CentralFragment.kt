package k.t.sample_ble.fragments

import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.trello.rxlifecycle3.components.support.RxFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import k.t.sample_ble.R
import k.t.sample_ble.ble.BLEScanner
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.central_fragment.*
import kotlinx.android.synthetic.main.listitem_device.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val SCAN_PERIOD: Long = 60L

class CentralFragment : RxFragment() {
    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.central_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scanResultAdapter = ScanResultAdapter()
        rvScanList.apply {
            setHasFixedSize(true)
            adapter = scanResultAdapter
        }

        btnScan.text = getString(R.string.btn_scan, SCAN_PERIOD)
        btnScan.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scanResultAdapter.clear()
                scanResultAdapter.updateResults().addTo(compositeDisposable)

                BLEScanner.startScan()
                    .takeUntil(Observable.timer(SCAN_PERIOD, TimeUnit.SECONDS))
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onNext = { scanResult ->
                            Timber.d("${scanResult.device.name} / ${scanResult.device.address}")
                            scanResultAdapter.submit(scanResult)
                        },
                        onComplete = {
                            Timber.d("BLE startScan-onComplete")
                            btnScan.isChecked = false
                        },
                        onError = {
                            Timber.e(it)
                        }
                    ).addTo(compositeDisposable)
            } else {
                compositeDisposable.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }
}

class ScanResultAdapter : ListAdapter<ScanResult, ScanResultAdapter.ItemViewHolder>(ItemCallback()) {
    private val foundDevices = LinkedHashMap<String, ScanResult>()

    fun clear() {
        foundDevices.clear()
        submitList(null)
    }

    fun updateResults(): Disposable {
        var elapsedTime = 0L
        return Observable.interval(2L, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .doOnNext { elapsedTime = SystemClock.elapsedRealtimeNanos() }
            .flatMap {
                Observable.fromIterable(foundDevices.entries)
            }
            .subscribeBy(
                onNext = {
                    val address = it.key
                    val scanResult = it.value
                    val timeSince =
                        TimeUnit.SECONDS.convert(elapsedTime - scanResult.timestampNanos, TimeUnit.NANOSECONDS)
                    if (timeSince > 6) {
                        Timber.d("remove: ${scanResult.device.name}")
                        foundDevices.remove(address)
                        submitList(foundDevices.values.toMutableList())
                    }
                }
            )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_device, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        getItem(position)?.let {
            with(holder) {
                tvDeviceName.text = it.device.name ?: "null"
                tvDeviceAddress.text = it.device.address
            }
        }
    }

    fun submit(result: ScanResult) {
        val address = result.device.address
        foundDevices[address] = result
        submitList(foundDevices.values.toMutableList())
    }

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    class ItemCallback : DiffUtil.ItemCallback<ScanResult>() {
        override fun areItemsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean =
            areContentsTheSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean =
            oldItem.device.address == newItem.device.address
    }
}

