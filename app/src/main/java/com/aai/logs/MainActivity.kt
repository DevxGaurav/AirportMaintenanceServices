package com.aai.logs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private val manager=Manager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signin_opt = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("1079660556134-j051mg254uuaa0m0dtr3evvs3k5abifi.apps.googleusercontent.com").requestEmail().requestProfile().build()
        val client= GoogleSignIn.getClient(this, signin_opt)
        val account= GoogleSignIn.getLastSignedInAccount(this)
        if (account!=null) {
            updateUI(account)
        }

        /*signin_main.setOnClickListener {
            val signin = client.signInIntent
            startActivityForResult(signin, 101)
        }*/

        manager_main.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }

        engineer_main.setOnClickListener {
            startActivity(Intent(this, EngineerScanner::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==101) {
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            val account=task.getResult(ApiException::class.java)
            updateUI(account!!)
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        manager.setEmail(account.email!!)
        manager.setName(account.displayName!!)
        manager.setProfilePicURL(account.photoUrl.toString())
        manager.login(object :Manager.OnCompleteListener {
            override fun onComplete(task: Boolean, info: String, data: JSONObject?) {
                if (task) {
                    if (data!!.getInt("managerflag")==1) {
                        startActivity(Intent(this@MainActivity, Dashboard::class.java))
                        finish()
                    }else {
                        startActivity(Intent(this@MainActivity, EngineerScanner::class.java))
                        finish()
                    }
                }else {
                    Snackbar.make(manager_main, info, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }
}
