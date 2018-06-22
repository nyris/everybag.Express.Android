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

package de.everybag.express.ui.mainscreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import dagger.Lazy
import de.everybag.express.R
import de.everybag.express.base.BaseActivity
import de.everybag.express.utils.ActionsConst
import de.everybag.express.utils.ActivityUtils
import de.everybag.express.utils.KeysConst
import de.everybag.express.utils.ParamsUtils
import java.io.File
import javax.inject.Inject

/**
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class MainActivity : BaseActivity() {
    @Inject
    lateinit var mainFragmentProvider: Lazy<MainFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
        val mainFragment: MainFragment
        if (fragment == null) {
            // Get the fragment from dagger
            mainFragment = mainFragmentProvider.get()
            mainFragment.arguments = createBundleFromIntent(intent)
            ActivityUtils.addFragmentToActivity(
                    supportFragmentManager, mainFragment, R.id.contentFrame)
        }

        val isBuddySearch = ParamsUtils.getParam(this, KeysConst.BUDDY_SEARCH)
        if(isBuddySearch.isEmpty())
            ParamsUtils.saveParam(this, KeysConst.BUDDY_SEARCH, false.toString())
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val mainFragment = mainFragmentProvider.get()
        mainFragment.arguments = createBundleFromIntent(intent)
    }

    private fun createBundleFromIntent(intent: Intent): Bundle {
        val bundle = Bundle()
        if (intent.extras == null) return bundle

        var uri: Uri? = null
        val action: String? = if (intent.action != null) {
            intent.action
        } else {
            intent.extras.getString(KeysConst.ACTION)
        }

        when (action) {
            Intent.ACTION_SEND -> {
                uri = intent.getParcelableExtra(Intent.EXTRA_STREAM)
            }
            ActionsConst.HANDLE_SCREENSHOT -> {
                val path = intent.extras.getString(KeysConst.SCREENSHOT_PATH)!!
                val file = File(path)
                uri = Uri.fromFile(file)
            }
        }

        bundle.putParcelable(KeysConst.SCREENSHOT_PATH, uri)
        bundle.putString(KeysConst.ACTION, action)
        return bundle
    }

    override fun onBackPressed() {
        val mainFragment = mainFragmentProvider.get()
        if (mainFragment.isCanBack()) {
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack()
            } else {
                this.finish()
            }
        } else {
            mainFragment.clearView()
            mainFragment.startCamera()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val fragment = mainFragmentProvider.get()
        return fragment.onKeyDown(keyCode, event)
    }
}