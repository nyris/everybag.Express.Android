/*
 * Copyright 2018 nyris GmbH. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.everybag.express.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import de.everybag.express.R
import de.everybag.express.ui.mainscreen.MainActivity
import de.everybag.express.utils.ActionsConst
import de.everybag.express.utils.KeysConst

/**
 * BootReceiver.kt - Boot Receiver
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class BootReceiver : BroadcastReceiver() {
    private val idChannel = "everybag_channel"

    override fun onReceive(context: Context, i: Intent) {
        when (i.action) {
            ActionsConst.HANDLE_SCREENSHOT -> {
                val encodedPath = i.extras.getString(KeysConst.SCREENSHOT_PATH)
                showNotification(context, encodedPath)
            }
            else -> {
                val intentScreenshotService = Intent(context, ScreenshotService::class.java)
                context.startService(intentScreenshotService)
            }
        }
    }

    private fun showNotification(context: Context, encodedPath: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(KeysConst.ACTION, ActionsConst.HANDLE_SCREENSHOT)
        intent.putExtra(KeysConst.SCREENSHOT_PATH, encodedPath)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, idChannel)
                .setContentTitle(context.getText(R.string.notification_screenshot_title))
                .setContentText(context.getText(R.string.notification_screenshot_message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(idChannel, context.getString(R.string.app_name), importance)
            channel.description = idChannel
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                    .setAutoCancel(true)
        }

        builder.setChannelId(idChannel)
        notificationManager.notify(1, builder.build())
    }
}