package k.t.sample_ble.fragments

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import k.t.sample_ble.R
import k.t.sample_ble.ble.BLEAdvertiser
import k.t.sample_ble.ble.Peripheral
import k.t.sample_ble.utils.LoggerAdapter
import kotlinx.android.synthetic.main.peripheral_fragment.*
import timber.log.Timber

class PeripheralFragment : Fragment() {
    private val loggerAdapter = LoggerAdapter()
    private val gattServerLogger = object : LoggerAdapter.EventListener {
        override fun log(msg: String) {
            loggerAdapter.log(msg)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.peripheral_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvLogger.apply {
            adapter = loggerAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        btnAdvertise.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                loggerAdapter.clear()

                BLEAdvertiser.startAdvertise()
                tvDeviceName.text = BluetoothAdapter.getDefaultAdapter().name
            } else {
                BLEAdvertiser.stopAdvertise()
            }

            Peripheral.openGattServer(gattServerLogger)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BLEAdvertiser.stopAdvertise()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnClear -> loggerAdapter.clear()
        }
        return true
    }
}