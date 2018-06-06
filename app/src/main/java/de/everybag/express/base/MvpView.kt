package de.everybag.express.base

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
interface MvpView<T> {
    fun onError(message: String)

    fun showMessage(message: String)

    fun isNetworkConnected(): Boolean

    fun hideKeyboard()
}