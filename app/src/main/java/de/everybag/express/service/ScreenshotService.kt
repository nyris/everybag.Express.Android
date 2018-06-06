package de.everybag.express.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import de.everybag.express.ui.MainActivity
import de.everybag.express.R
import de.everybag.express.utils.ActionsConst
import de.everybag.express.utils.KeysConst


/**
 * ScreenshotService - A service run in the background to listen when user take screenshot
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class ScreenshotService : Service(), IOnScreenshotTakenListener {
    private var screenshotObserver: ScreenshotObserver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartService = Intent(applicationContext,
                this.javaClass)
        restartService.`package` = packageName
        val restartServicePI = PendingIntent.getService(
                applicationContext, 1, restartService,
                PendingIntent.FLAG_ONE_SHOT)

        //Restart the service once it has been killed android
        val alarmService = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI)

    }

    override fun onCreate() {
        super.onCreate()
        val handlerThread = HandlerThread("content_observer")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        screenshotObserver = ScreenshotObserver(this, this, handler)
        contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                screenshotObserver
        )
    }

    override fun onScreenshotTaken(uri: Uri) {
        val idChannel = "everybag_channel"

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(KeysConst.ACTION, ActionsConst.HANDLE_SCREENSHOT)
        intent.putExtra(KeysConst.SCREENSHOT_PATH, uri.encodedPath)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(this, idChannel)
                .setContentTitle(getText(R.string.notification_screenshot_title))
                .setContentText(getText(R.string.notification_screenshot_message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(idChannel, getString(R.string.app_name), importance)
            channel.description = idChannel
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(ContextCompat.getColor(this, android.R.color.transparent))
                    .setAutoCancel(true)
        }

        builder.setChannelId(idChannel)
        notificationManager.notify(1, builder.build())
    }
}