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

package de.everybag.express.base

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.inputmethod.InputMethodManager
import dagger.android.support.DaggerAppCompatActivity
import de.everybag.express.utils.KeysConst
import de.everybag.express.utils.ParamsUtils

/**
 * BaseActivity.kt - Base abstracted class that extend DaggerAppCompatActivity and contain common
 * features between activities
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
        ParamsUtils.saveParam(this, KeysConst.IS_RUNNING, true.toString())
    }

    override fun onPause() {
        super.onPause()
        ParamsUtils.saveParam(this, KeysConst.IS_RUNNING, false.toString())
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            ActivityCompat.finishAfterTransition(this)
        }
    }
}