package com.aai.logs

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_engineer_scanner.*
import kotlinx.android.synthetic.main.fragment_requests.view.*
import org.json.JSONObject


class EngineerScanner : AppCompatActivity() {

    private val manager=Manager(this)
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engineer_scanner)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 10)
        }

        val list=ArrayList<Requests>()
        val adapter=RequestAdapter(list)
        recycler_engineer_scanner.layoutManager=LinearLayoutManager(this)
        recycler_engineer_scanner.adapter=adapter

        manager.fetchRequests(object :Manager.OnCompleteListener {
            override fun onComplete(task: Boolean, info: String, data: JSONObject?) {
                if (task) {
                    val json=data!!.getJSONArray("createdresp")
                    for (i in 0 until json.length()) {
                        for (j in 0 until json.getJSONObject(i).getJSONArray("currentissues").length()) {
                            var js=json.getJSONObject(i).getJSONArray("currentissues")
                            list.add(Requests(js.getJSONObject(j).getString("_id"),js.getJSONObject(j).getString("reporteddate"), js.getJSONObject(j).getInt("urgent"), js.getJSONObject(j).getString("problemfaced"), json.getJSONObject(i).getString("name")))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }else {
                    Snackbar.make(capture_engineer_scanner, info, Snackbar.LENGTH_LONG).show()
                }
            }
        })

        capture_engineer_scanner.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
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


    private inner class Requests(val id:String, val reportdate:String, val urgent:Int, val problemfaced:String, val name:String)

    private inner class RequestAdapter(val list: ArrayList<Requests>): RecyclerView.Adapter<RequestAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RequestAdapter.ViewHolder {
            return ViewHolder(LayoutInflater.from(this@EngineerScanner).inflate(R.layout.fragment_requests, parent, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: RequestAdapter.ViewHolder, position: Int) {
            holder.id.text=list[position].id
            holder.dt.text=list[position].reportdate
            if (list[position].urgent==1) {
                holder.status.text="Urgent"
            }else {
                holder.status.text="Regular"
            }
            holder.name.text=list[position].name
            holder.card.setOnClickListener {
                val integrator = IntentIntegrator(this@EngineerScanner)
                integrator.setOrientationLocked(false)
                integrator.initiateScan()
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val id=itemView.id_frag_requests
            val name=itemView.name_frag_requests
            val dt=itemView.dt_frag_requests
            val status=itemView.engineer_frag_requests
            val card=itemView.card_frag_request
        }
    }
}
