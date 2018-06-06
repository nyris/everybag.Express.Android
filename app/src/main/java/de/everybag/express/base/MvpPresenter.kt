package de.everybag.express.base

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
interface MvpPresenter<V> {
    fun onAttach(view: V)

    fun onDetach()
}