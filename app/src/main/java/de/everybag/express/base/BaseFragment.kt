package de.everybag.express.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import dagger.android.support.DaggerFragment
import de.everybag.express.utils.NetworkUtils


/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */

@Suppress("DEPRECATION")
abstract class BaseFragment<T> : DaggerFragment(), MvpView<T> {
    private var mActivity: BaseActivity? = null
    private var mProgressDialog: ProgressDialog? = null

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