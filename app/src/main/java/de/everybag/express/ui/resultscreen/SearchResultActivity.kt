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

package de.everybag.express.ui.resultscreen

import android.os.Bundle
import dagger.Lazy
import de.everybag.express.R
import de.everybag.express.base.BaseActivity
import de.everybag.express.utils.ActivityUtils
import javax.inject.Inject

/**
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class SearchResultActivity : BaseActivity() {
    @Inject
    lateinit var searchFragmentProvider: Lazy<SearchFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val fragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
        val searchFragment: SearchFragment
        if (fragment == null) {
            // Get the fragment from dagger
            searchFragment = searchFragmentProvider.get()
            searchFragment.arguments = intent.extras
            ActivityUtils.addFragmentToActivity(
                    supportFragmentManager, searchFragment, R.id.contentFrame)
        }
    }
}
