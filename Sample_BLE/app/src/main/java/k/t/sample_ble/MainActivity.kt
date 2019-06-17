package k.t.sample_ble

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return
        val navController = navHost.navController

        NavigationUI.setupWithNavController(bottomNavView, navController)
    }

    override fun onResume() {
        super.onResume()

        if (BluetoothAdapter.getDefaultAdapter()?.isEnabled == false) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0)
        }
    }
}

