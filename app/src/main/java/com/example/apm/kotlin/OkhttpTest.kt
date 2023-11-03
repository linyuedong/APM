package com.example.apm.kotlin

import android.util.Log
import com.example.apm.utils.HttpHelper
import okhttp3.*
import java.io.IOException

class OkhttpTest {

    companion object instance {
        private const val TAG = "OkhttpTest"

        var nameStatic: String = "BB"

        fun speakStatic() {
            Log.d(TAG, "speakStatic: ")
        }


        fun get() {
            Thread {
                Log.d(TAG, "get: ")
                val okHttpClient = OkHttpClient().newBuilder().build()
                val request = Request.Builder()
                    .url(HttpHelper.GET_URL)
                    .build()
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val string = response.body()!!.string()
                        Log.i(HttpHelper.TAG, "asyncGetRequest onResponse111: ")
                    }
                })
            }.start()
        }

    }
}