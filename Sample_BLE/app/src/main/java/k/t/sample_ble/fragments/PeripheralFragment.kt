package k.t.sample_ble.fragments

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import k.t.sample_ble.R
import k.t.sample_ble.ble.BLEAdvertiser
import kotlinx.android.synthetic.main.peripheral_fragment.*

class PeripheralFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.peripheral_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAdvertise.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                BLEAdvertiser.startAdvertise()
                tvDeviceName.text = BluetoothAdapter.getDefaultAdapter().name
            } else {
                BLEAdvertiser.stopAdvertise()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BLEAdvertiser.stopAdvertise()
    }
}