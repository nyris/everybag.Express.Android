package de.everybag.express.base

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.inputmethod.InputMethodManager
import dagger.android.support.DaggerAppCompatActivity
import de.everybag.express.utils.ParamsUtils

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
abstract class BaseActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        ParamsUtils.saveParam(this, "isRunning",true.toString())
    }

    override fun onPause() {
        super.onPause()
        ParamsUtils.saveParam(this, "isRunning",false.toString())
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            ActivityCompat.finishAfterTransition(this)
        }
    }
}