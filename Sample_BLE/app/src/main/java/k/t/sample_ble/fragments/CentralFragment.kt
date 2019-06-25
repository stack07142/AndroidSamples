package k.t.sample_ble.fragments

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
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
import k.t.sample_ble.BaseApplication
import k.t.sample_ble.R
import k.t.sample_ble.ble.BLEScanner
import k.t.sample_ble.ble.Central
import k.t.sample_ble.utils.LoggerAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.central_fragment.*
import kotlinx.android.synthetic.main.listitem_device.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val SCAN_PERIOD: Long = 60L

class CentralFragment : RxFragment() {
    private var loggerAdapter = LoggerAdapter()

    private var scanDisposables = CompositeDisposable()
    private var selectedDevice: BluetoothDevice? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.central_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvLogger.apply {
            adapter = loggerAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        val eventListener = object : LoggerAdapter.EventListener {
            override fun log(msg: String) {
                loggerAdapter.log(msg)
            }
        }
        Central.setEventListener(eventListener)

        val scanResultAdapter = ScanResultAdapter()
        rvScanList.apply {
            setHasFixedSize(true)
            adapter = scanResultAdapter.apply {
                setItemClickListener(object : ScanResultAdapter.ItemClickListener {
                    @SuppressLint("SetTextI18n")
                    override fun onItemClick(device: BluetoothDevice) {
                        selectedDevice = device
                        tvSelected.text = "${selectedDevice?.name} / ${selectedDevice?.address}"
                    }
                })
            }
        }

        btnScan.text = getString(R.string.btn_scan, SCAN_PERIOD)
        btnScan.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scanResultAdapter.clear()
                scanResultAdapter.updateResults().addTo(scanDisposables)

                BLEScanner.startScan()
                    .takeUntil(Observable.timer(SCAN_PERIOD, TimeUnit.SECONDS))
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.newThread())
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
                            eventListener.log("Exception: ${it.message}")
                            Timber.e(it)
                        }
                    ).addTo(scanDisposables)
            } else {
                scanDisposables.clear()
            }
        }

        btnWriteWithoutResponse.setOnClickListener {
            scanDisposables.clear()
            selectedDevice?.let { device ->
                Central.writeWithoutResponse(BaseApplication.appContext, device, etWriteWithoutResponse.text.toString())
                    .timeout(30L, TimeUnit.SECONDS)
                    .retry(3)
                    .compose(bindToLifecycle<Void>())
                    .subscribeOn(Schedulers.newThread())
                    .subscribeBy(
                        onComplete = {
                            Timber.d("writeWithoutResponse: onComplete")
                        },
                        onError = {
                            Timber.d("writeWithoutResponse: onError")
                            Timber.e(it)
                        }
                    )
            }
        }

        btnWrite.setOnClickListener {
            scanDisposables.clear()
            selectedDevice?.let { device ->
                Central.write(BaseApplication.appContext, device, etWrite.text.toString())
                    .timeout(30L, TimeUnit.SECONDS)
                    .retry(3)
                    .compose(bindToLifecycle<Void>())
                    .subscribeOn(Schedulers.newThread())
                    .subscribeBy(
                        onComplete = {
                            Timber.d("write: onComplete")
                        },
                        onError = {
                            Timber.d("write: onError")
                            Timber.e(it)
                        }
                    )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scanDisposables.dispose()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.btnClear -> {
                loggerAdapter.clear()
            }
        }
        return true
    }
}

class ScanResultAdapter : ListAdapter<ScanResult, ScanResultAdapter.ItemViewHolder>(ItemCallback()) {
    private val foundDevices = LinkedHashMap<String, ScanResult>()
    private var itemClickListener: ItemClickListener? = null

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
        getItem(position)?.let { scanResult ->
            with(holder) {
                itemScanResult.setOnClickListener {
                    itemClickListener?.onItemClick(scanResult.device)
                }
                tvDeviceName.text = scanResult.device.name ?: "null"
                tvDeviceAddress.text = scanResult.device.address
            }
        }
    }

    fun submit(result: ScanResult) {
        val address = result.device.address
        foundDevices[address] = result
        submitList(foundDevices.values.toMutableList())
    }

    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    class ItemCallback : DiffUtil.ItemCallback<ScanResult>() {
        override fun areItemsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean =
            areContentsTheSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean =
            oldItem.device.address == newItem.device.address
    }

    interface ItemClickListener {
        fun onItemClick(device: BluetoothDevice)
    }
}

