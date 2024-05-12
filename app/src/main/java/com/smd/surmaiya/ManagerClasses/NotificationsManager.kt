package com.smd.surmaiya.ManagerClasses

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.smd.surmaiya.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties


object NotificationsManager {
    private const val CHANNEL_ID = "message_notification_channel"
    private const val NOTIFICATION_ID = 100
    private const val PERMISSION_REQUEST_CODE = 101

    //return get instance

    private var instance: NotificationsManager? = null

    @JvmStatic
    fun  getInstance(): NotificationsManager {
        if(instance == null) {
            instance = NotificationsManager
        }
        return NotificationsManager
    }

    @JvmStatic
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Message Notifications"
            val descriptionText = "Channel for displaying message notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    @JvmStatic
    fun showNotification(context: Context, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_home)
            .setContentTitle("Budget Exceeded")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission from the user
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        // If permission is granted, show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    fun buildNotification(context: Context, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_home)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission from the user
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        // If permission is granted, show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    fun sendCollaborationNotification(title: String,message :  String, otherUserFcm : String, playlistId:String)
    {
        UserManager.getCurrentUser()?.id?.let {
            val jsonObject = JSONObject()
            val jsonNotificationObject=JSONObject()
            val jsonDataObject=JSONObject()
            jsonNotificationObject.put("title", title)
            jsonNotificationObject.put("body", message)
            Log.d("Notification", "sendCollaborationNotification: ${jsonNotificationObject.toString()}")

            jsonDataObject.put("playlistId", playlistId)
            jsonDataObject.put("chat_type", "collaboration")

            Log.d("Notification", "sendCollaborationNotification: ${jsonDataObject.toString()}")

            jsonObject.put("notification", jsonNotificationObject)
            jsonObject.put("data", jsonDataObject)
            jsonObject.put("to", otherUserFcm)
            Log.d("Notification", "sendCollaborationNotification: ${jsonObject.toString()}")


            callApi(jsonObject)

        }
    }

    fun callApi(jsonObject: JSONObject) {
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val body: RequestBody = RequestBody.create(JSON, jsonObject.toString())
        val properties = Properties()
        try {
            properties.load(FileInputStream("local.properties"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val serverKey = properties.getProperty("SERVER_KEY")

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
//            .header("Authorization", "key=$serverKey")
            .header("Authorization", "Bearer AAAAH6rBws0:APA91bGt5JT-nDR2yRvC3lUJjiCOVpzSgB_ln-PFlKDi3zvjbynFTClJo88qdsqzm2B67woLwQw_q3j-bJvks3ojgVFqyx7KAUIvqX5TLqD2gkiPUEublDpwJYW2PRn1w2VwEsg4G1Xt")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle response
                Log.d("Notification", "onResponse: ${response.body?.string()}")
            }
        })

    }


}

