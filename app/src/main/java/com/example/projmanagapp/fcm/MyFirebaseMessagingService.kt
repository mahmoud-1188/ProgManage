package com.example.projmanagapp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.projmanagapp.Activites.MainActivity
import com.example.projmanagapp.Activites.SigninActivity
import com.example.projmanagapp.R
import com.example.projmanagapp.utils.Constant
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.projmanagapp.Firebase.FirestoreClass

// this class to handel notification messages....

class MyFirebaseMessagingService :FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG,"FROM: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {

            Log.d(TAG,"Message data payload ${remoteMessage.data}")

            val title = remoteMessage.data[Constant.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constant.FCM_KEY_MESSAGE]!!

            sendNotification(title,message)

        }

        remoteMessage.notification?.let {

            Log.d(TAG,"Message Notification ${it.body}")

        }


    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG,"Refresh token: ${token}")

        sendRegistrationToServer(token)
    }


    private fun sendRegistrationToServer (token:String){

        //implement
    }


    private fun sendNotification (title:String, MessageBody:String){

        val intent = if (FirestoreClass().getcurrentuserid().isNotEmpty()){
            Intent(this,MainActivity::class.java)
        }else{
            Intent(this,SigninActivity::class.java)
        }

           intent.addFlags(
           Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                   or  Intent.FLAG_ACTIVITY_CLEAR_TOP )


        val pendingintent = PendingIntent.getActivity(this,
                0,intent,PendingIntent.FLAG_ONE_SHOT)

        val chanelid = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this,chanelid
        ).setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(MessageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingintent)

        val notificationmanager = getSystemService(
              Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val chaneelid = NotificationChannel(chanelid,
                    "Chaneel projmanag title",
                    NotificationManager.IMPORTANCE_DEFAULT)

            notificationmanager.createNotificationChannel(chaneelid)
        }

        notificationmanager.notify(0,notificationBuilder.build())

    }



    companion object{

        const val TAG = "myFirebaseMsgService"
    }



}