package de.everybag.express.ui.mainscreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import dagger.Lazy
import de.everybag.express.R
import de.everybag.express.base.BaseActivity
import de.everybag.express.utils.ActionsConst
import de.everybag.express.utils.ActivityUtils
import de.everybag.express.utils.KeysConst
import java.io.File
import javax.inject.Inject

/**
 *
 *
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
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val mainFragment = mainFragmentProvider.get()
        mainFragment.arguments = createBundleFromIntent(intent)
        mainFragment.onResume()
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
}