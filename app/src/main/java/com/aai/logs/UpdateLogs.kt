package com.aai.logs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_update_logs.*

class UpdateLogs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_logs)

        val id=intent.extras!!.getString("id")
        id_updatelog.text=id
    }
}
