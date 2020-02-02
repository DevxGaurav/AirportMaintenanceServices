package com.aai.logs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_log_history.*
import kotlinx.android.synthetic.main.fragment_history.view.*
import org.json.JSONObject

class LogHistory : AppCompatActivity() {

    private val manager=Manager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_history)

        val list=ArrayList<Logs>()
        val adapter=LogsAdapter(list)
        recycler_log.adapter=adapter
        recycler_log.layoutManager=LinearLayoutManager(this)
        val id=intent.extras!!.getString("id")
        manager.logHistory(id!!, object :Manager.OnCompleteListener{
            override fun onComplete(task: Boolean, info: String, data: JSONObject?) {
                if (task) {
                    id_log_history.text="#"+data!!.getString("_id")
                    name_log_history.text=data!!.getString("name")
                    doi_log_history.text=data!!.getString("doi")
                    lro_log_history.text=data.getString("lastrepaired")
                    nu_log_history.text=data.getString("nextperiodicrepair")

                    for (i in 0 until  data.getJSONArray("logs").length()) {
                        Log.d("count: ", i.toString())
                        list.add(Logs(data.getJSONArray("logs").getJSONObject(i).getString("_id"), data.getJSONArray("logs").getJSONObject(i).getString("engineer"), data.getString("name"), data.getJSONArray("logs").getJSONObject(i).getString("reporteddate")))
                    }
                    adapter.notifyDataSetChanged()
                }else {
                    Snackbar.make(id_log_history, info, Snackbar.LENGTH_LONG).show()
                }
            }
        })

        updatelogs_log_history.setOnClickListener {
            var bundle=Bundle()
            bundle.putString("id", id)
            startActivity(Intent(this, UpdateLogs::class.java).putExtras(bundle))
        }
    }

    private inner class Logs (val id:String, val engineer:String, val name:String, val reportdate:String)

    private inner class LogsAdapter(val list: ArrayList<Logs>): RecyclerView.Adapter<LogsAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val id=itemView.id_frag_history
            val engineer=itemView.engineer_frag_history
            val name=itemView.name_frag_history
            val report=itemView.dt_frag_history
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(this@LogHistory).inflate(R.layout.fragment_history, parent, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.id.text= list[position].id
            holder.name.text= list[position].name
            holder.engineer.text= list[position].engineer
            holder.report.text=list[position].reportdate
        }
    }
}
