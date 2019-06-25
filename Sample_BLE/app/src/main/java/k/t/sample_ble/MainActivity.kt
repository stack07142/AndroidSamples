package k.t.sample_ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : RxAppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        bluetoothManager?.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant(Timber.DebugTree())

        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return
        val navController = navHost.navController

        bottomNavView.setupWithNavController(navController)
        setupActionBarWithNavController(
            navController,
            AppBarConfiguration(setOf(R.id.central_dest, R.id.peripheral_dest))
        )
    }

    override fun onResume() {
        super.onResume()

        bluetoothAdapter?.takeIf { !it.isEnabled }?.apply {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 100)
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, permissions, 101)
        }
    }

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private fun allPermissionsGranted(): Boolean =
        permissions.all { ContextCompat.checkSelfPermission(this, it) == PERMISSION_GRANTED }
}

