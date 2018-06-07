package de.everybag.express.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.SystemClock
import android.provider.MediaStore
import de.everybag.express.utils.ActionsConst
import de.everybag.express.utils.KeysConst
import de.everybag.express.utils.ParamsUtils


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

    private fun sendImplicitBroadcast(i: Intent) {
        val pm = packageManager
        val matches = pm.queryBroadcastReceivers(i, 0)

        for (resolveInfo in matches) {
            val explicit = Intent(i)
            val cn = ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
                    resolveInfo.activityInfo.name)

            explicit.component = cn
            sendOrderedBroadcast(explicit, null)
        }
    }

    override fun onScreenshotTaken(uri: Uri) {
        val isRunning = ParamsUtils.getParam(this, "isRunning").toBoolean()
        if(isRunning)
            return
        val broadcast = Intent(ActionsConst.HANDLE_SCREENSHOT)
        broadcast.putExtra(KeysConst.SCREENSHOT_PATH, uri.encodedPath)
        sendImplicitBroadcast(broadcast)
    }
}