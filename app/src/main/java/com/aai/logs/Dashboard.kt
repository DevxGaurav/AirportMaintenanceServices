package com.aai.logs

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_dashboard.*

class Dashboard : AppCompatActivity() {

    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val home=Home()
        val add=Add()

        val fragmentManager= supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.container_dashboard, add).hide(add).commit()
        fragmentManager.beginTransaction().add(R.id.container_dashboard, home).commit()

        var active: Fragment = home
        nav_view_dashboard.setOnNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.navigation_home-> {
                    if (active!=home) {
                        fragmentManager.beginTransaction().hide(active).show(home).commit()
                        active=home
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_scan-> {
                    if (!allPermissionsGranted()) {
                        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 10)
                    }else {
                        val integrator = IntentIntegrator(this)
                        integrator.setOrientationLocked(false)
                        integrator.initiateScan()
                    }
                    return@setOnNavigationItemSelectedListener false
                }
                R.id.navigation_add-> {
                    if (active!=add) {
                        fragmentManager.beginTransaction().hide(active).show(add).commit()
                        active=add
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 10) {
            if (allPermissionsGranted()) {
                val integrator = IntentIntegrator(this)
                integrator.setOrientationLocked(false)
                integrator.initiateScan()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                parseURL(result.contents.trim())
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun parseURL(url:String) {
        var params=url.split("?")[1].split("&")
        val map=HashMap<String, String>()
        for (param in params) {
            val key=param.split("=")[0]
            val value=param.split("=")[1]
            map[key] = value
        }
        val bundle=Bundle()
        bundle.putString("id", map["id"])
        startActivity(Intent(this, LogHistory::class.java).putExtras(bundle))
    }
}
