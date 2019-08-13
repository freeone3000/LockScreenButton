package com.freeone3000.lockscreen

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.widget.Toast
import java.util.*

const val ADMIN_INTENT = 2333

class MainActivity : AppCompatActivity() {
    var compName: ComponentName? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerDeviceAdministrator()
    }

    /**
     * Start path to register as a a device administrator
     */
    private fun registerDeviceAdministrator() {
        val description = "Enable this if you're James Moore or you're dumb."

        val mdp = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, ScreenLocker::class.java)

        if(mdp.isAdminActive(compName!!)) {
            activateDeviceAdminAndLockScreen()
        } else {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, description)
            startActivityForResult(intent, ADMIN_INTENT)
        }
    }

    /**
     * Actually become the device administrator, after the callback
     */
    private fun activateDeviceAdminAndLockScreen() {
        val delay = 200L //in *ms*

        val mdp = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val oldDelay = mdp.getMaximumTimeToLock(compName)
        mdp.setMaximumTimeToLock(compName!!, delay)

        Thread.sleep((delay * 3) / 2)
        mdp.setMaximumTimeToLock(compName!!, oldDelay)

        // if screen still on, use the backup
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        dm.displays.forEach {
            if(it.state == Display.STATE_ON) {
                mdp.lockNow()
                return@forEach
            }
        }

        finish()
    }

    /**
     * Handle ALL permissions requests (blame Android for this one)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == ADMIN_INTENT) {
            if(resultCode == RESULT_OK) {
                activateDeviceAdminAndLockScreen()
            } else {
                Toast.makeText(this, "Cannot lock screen without becoming admin", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
