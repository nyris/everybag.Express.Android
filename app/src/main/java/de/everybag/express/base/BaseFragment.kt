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

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import dagger.android.support.DaggerFragment
import de.everybag.express.utils.NetworkUtils


/**
 * BaseFragment.kt - Base generic abstracted class that extend DaggerFragment and MvpView and contain
 * common features between fragments
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
abstract class BaseFragment<T> : DaggerFragment(), MvpView<T> {
    private var mActivity: BaseActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mActivity = null
    }

    override fun onError(message: String) {
        showMessage(message)
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun isNetworkConnected(): Boolean {
        return NetworkUtils.isNetworkConnected(context!!)
    }

    override fun hideKeyboard() {
        mActivity?.hideKeyboard()
    }
}