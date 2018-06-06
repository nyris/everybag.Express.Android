package de.everybag.express.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BootReceiver.kt - Boot Receiver
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, ScreenshotService::class.java)
        context.startService(i)
    }
}