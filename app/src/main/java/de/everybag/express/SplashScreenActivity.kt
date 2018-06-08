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

package de.everybag.express

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import de.everybag.express.ui.mainscreen.MainActivity
import de.everybag.express.utils.DialogUtils
import de.everybag.express.utils.NetworkUtils
/**
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val dialogUtils = DialogUtils(this)
        if (!NetworkUtils.isNetworkConnected(this)) {
            dialogUtils.messageBoxDialog(getString(R.string.error_no_network_label),
                    getString(R.string.error_no_network_msg), DialogUtils.KindMessageBox.Finish)
            return
        }

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 500)
    }
}
