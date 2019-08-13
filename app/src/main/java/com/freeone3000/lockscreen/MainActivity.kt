package com.freeone3000.lockscreen

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

const val ADMIN_INTENT = 2333

class MainActivity : AppCompatActivity() {
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
        val compName = ComponentName(this, ScreenLocker::class.java)

        if(mdp.isAdminActive(compName)) {
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
        val mdp = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mdp.lockNow()
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
