package com.aai.logs

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

class Manager(val context:Context) {

    private val baseurl:String="http://192.168.58.77:8080"
    private var task:Boolean=false
    private var info:String=""
    private var data:JSONObject?=null

    interface OnCompleteListener {
        fun onComplete(task:Boolean, info:String, data:JSONObject?)
    }


    fun setEmail(email: String): Manager{
        context.getSharedPreferences("User", Context.MODE_PRIVATE).edit().putString("email", email).commit()
        return this
    }

    fun setName(name: String): Manager{
        context.getSharedPreferences("User", Context.MODE_PRIVATE).edit().putString("name", name).commit()
        return this
    }

    fun setProfilePicURL(url: String): Manager{
        context.getSharedPreferences("User", Context.MODE_PRIVATE).edit().putString("profilepicurl", url).commit()
        return this
    }

    fun getEmail(): String?{
        return context.getSharedPreferences("User", Context.MODE_PRIVATE).getString("email", null)
    }

    fun getName(): String?{
        return context.getSharedPreferences("User", Context.MODE_PRIVATE).getString("name", null)
    }

    fun getProfilePicURL(): String?{
        return context.getSharedPreferences("User", Context.MODE_PRIVATE).getString("profilepicurl", null)
    }

    fun login(listener:OnCompleteListener?) {
        Login(listener).execute()
    }

    fun logHistory(id:String, listener: OnCompleteListener?) {
        LogHistory(id, listener).execute()
    }

    fun fetchRequests(listener: OnCompleteListener?) {
        Requests(listener).execute()
    }

    private inner class Login(val listener:OnCompleteListener?): AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                val url= URL("$baseurl/login")
                val connection= url.openConnection() as HttpURLConnection
                connection.requestMethod= "POST"
                connection.doOutput= true
                connection.doInput= true
                val outputStream= connection.outputStream
                val writer= BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
                val strpath= URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(getEmail(), "UTF-8")+"&"+
                        URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(getName(), "UTF-8")
                writer.write(strpath)
                writer.flush()
                writer.close()
                outputStream.close()
                val inputstream= connection.inputStream
                val reader= BufferedReader(InputStreamReader(inputstream, "ISO-8859-1"))
                var line:String?= ""
                var result= ""
                while (line!=null) {
                    result+= line
                    line= reader.readLine()
                }
                inputstream.close()
                reader.close()
                connection.disconnect()
                return result
            }catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            if (result==null) {
                task=false
                info="Connection Error"
                data=null
            }else {
                val resp=JSONObject(result)
                if (resp.getInt("code")==1) {
                    task=true
                    info="Login success"
                    data=resp.getJSONObject("data")
                }else {
                    task=false
                    info=resp.getString("info")
                    data=null
                }
            }
            if (listener!==null) {
                listener.onComplete(task, info, data)
            }
        }
    }

    private inner class LogHistory(val id:String, val listener:OnCompleteListener?): AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                val url= URL("$baseurl/getitem")
                val connection= url.openConnection() as HttpURLConnection
                connection.requestMethod= "POST"
                connection.doOutput= true
                connection.doInput= true
                val outputStream= connection.outputStream
                val writer= BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
                val strpath= URLEncoder.encode("emailid", "UTF-8")+"="+URLEncoder.encode("emply", "UTF-8")+"&"+
                        URLEncoder.encode("itemid", "UTF-8")+"="+URLEncoder.encode(id, "UTF-8")
                writer.write(strpath)
                writer.flush()
                writer.close()
                outputStream.close()
                val inputstream= connection.inputStream
                val reader= BufferedReader(InputStreamReader(inputstream, "ISO-8859-1"))
                var line:String?= ""
                var result= ""
                while (line!=null) {
                    result+= line
                    line= reader.readLine()
                }
                inputstream.close()
                reader.close()
                connection.disconnect()
                Log.d("Result: 1 ", result)
                return result
            }catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.d("Result: m ", "NULL")
            return null
        }

        override fun onPostExecute(result: String?) {
            if (result==null) {
                task=false
                info="Connection Error"
                data=null
            }else {
                val resp=JSONObject(result)
                if (resp.getInt("code")==1) {
                    task=true
                    info="Login success"
                    data=resp.getJSONObject("data")
                }else {
                    task=true
                    info=resp.getString("information")
                    data=null
                }
            }
            if (listener!==null) {
                listener.onComplete(task, info, data)
            }
        }
    }


    private inner class Requests(val listener:OnCompleteListener?): AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                val url= URL("$baseurl/getallcurrentissues")
                val connection= url.openConnection() as HttpURLConnection
                connection.requestMethod= "POST"
                connection.doOutput= true
                connection.doInput= true
                val outputStream= connection.outputStream
                val writer= BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
                val strpath= URLEncoder.encode("emailid", "UTF-8")+"="+URLEncoder.encode("emply", "UTF-8")
                writer.write(strpath)
                writer.flush()
                writer.close()
                outputStream.close()
                val inputstream= connection.inputStream
                val reader= BufferedReader(InputStreamReader(inputstream, "ISO-8859-1"))
                var line:String?= ""
                var result= ""
                while (line!=null) {
                    result+= line
                    line= reader.readLine()
                }
                inputstream.close()
                reader.close()
                connection.disconnect()
                Log.d("Result: 1 ", result)
                return result
            }catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.d("Result: m ", "NULL")
            return null
        }

        override fun onPostExecute(result: String?) {
            if (result==null) {
                task=false
                info="Connection Error"
                data=null
            }else {
                val resp=JSONObject(result)
                if (resp.getInt("code")==1) {
                    task=true
                    info="Login success"
                    data=resp.getJSONObject("data")
                }else {
                    task=true
                    info=resp.getString("information")
                    data=null
                }
            }
            if (listener!==null) {
                listener.onComplete(task, info, data)
            }
        }
    }
}