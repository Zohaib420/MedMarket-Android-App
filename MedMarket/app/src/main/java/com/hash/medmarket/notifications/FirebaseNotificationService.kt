package com.hash.medmarket.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hash.medmarket.R
import com.hash.medmarket.ui.admin.activities.AdminActivity
import com.hash.medmarket.ui.client.activities.MainClientActivity
import com.hash.medmarket.ui.pharmacists.activities.MainPharmacistActivity
import com.hash.medmarket.utils.UserAuthManager
import java.util.Random

class FirebaseNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            val map = remoteMessage.data
            val title = map["title"]
            val message = map["message"]
            val hisID = map["hisID"]

            Log.d("TAG", "onMessageReceived: HisID$hisID")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createOreoNotification(
                title,
                message,
                hisID
            )
            else createNormalNotification(title, message, hisID)
        } else Log.d("TAG", "onMessageReceived: no data ")

        super.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(s: String) {
        updateToken(s)
        super.onNewToken(s)
    }

    private fun updateToken(token: String) {

        val firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            val tokenMap: MutableMap<String, Any> = HashMap()
            tokenMap["token_id"] = token
            // update token
            FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .update("token", token)
        }
    }

    private fun createNormalNotification(title: String?, message: String?, hisID: String?) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, AllConstants.CHANNEL_ID)
        builder.setContentTitle(title).setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setColor(
                ResourcesCompat.getColor(
                    resources, R.color.white, null
                )
            ).setSmallIcon(R.mipmap.ic_launcher_round).setAutoCancel(true).setSound(uri)
        if (UserAuthManager.getUserData(this)!!.userType == "client") {
            val intent = Intent(this, MainClientActivity::class.java)
            intent.putExtra("hisID", hisID)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            builder.setContentIntent(pendingIntent)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(Random().nextInt(85 - 65), builder.build())
        }
        if (UserAuthManager.getUserData(this)!!.userType == "admin") {
            val intent = Intent(this, AdminActivity::class.java)
            intent.putExtra("hisID", hisID);
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(Random().nextInt(85 - 65), builder.build());
        }
        if (UserAuthManager.getUserData(this)!!.userType == "pharmacist") {
            val intent = Intent(this, MainPharmacistActivity::class.java)
            intent.putExtra("hisID", hisID);
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(Random().nextInt(85 - 65), builder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createOreoNotification(title: String?, message: String?, hisID: String?) {
        val channel = NotificationChannel(
            AllConstants.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        if (UserAuthManager.getUserData(this)!!.userType == "client") {

            val intent = Intent(this, MainClientActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("hisID", hisID)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            val notification: Notification =
                Notification.Builder(this, AllConstants.CHANNEL_ID).setContentTitle(title)
                    .setContentText(message).setColor(
                        ResourcesCompat.getColor(
                            resources, R.color.white, null
                        )
                    ).setSmallIcon(R.mipmap.ic_launcher_round).setContentIntent(pendingIntent)
                    .setAutoCancel(true).build()
            manager.notify(100, notification)

        }
        if (UserAuthManager.getUserData(this)!!.userType == "admin") {
            val intent =  Intent(this, AdminActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("hisID", hisID);
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            val notification =  Notification.Builder(this, AllConstants.CHANNEL_ID).setContentTitle(title).setContentText(message).setColor(ResourcesCompat.getColor(
                resources, R.color.white, null)).setSmallIcon(R.mipmap.ic_launcher_round).setContentIntent(pendingIntent).setAutoCancel(true).build();
            manager.notify(100, notification)
        }
        if (UserAuthManager.getUserData(this)!!.userType == "pharmacist") {
            val intent = Intent(this, MainPharmacistActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("hisID", hisID)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            val notification =  Notification.Builder(this, AllConstants.CHANNEL_ID).setContentTitle(title).setContentText(message).setColor(ResourcesCompat.getColor(
                resources, R.color.white, null)).setSmallIcon(R.mipmap.ic_launcher_round).setContentIntent(pendingIntent).setAutoCancel(true).build();
            manager.notify(100, notification)
        }
    }
}