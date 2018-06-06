package de.everybag.express.base

import android.content.Context
import android.support.v4.app.ActivityCompat
import android.view.inputmethod.InputMethodManager
import dagger.android.support.DaggerAppCompatActivity

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
abstract class BaseActivity : DaggerAppCompatActivity() {
    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            ActivityCompat.finishAfterTransition(this)
        }
    }
}