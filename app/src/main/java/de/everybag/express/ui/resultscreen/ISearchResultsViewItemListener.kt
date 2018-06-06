package de.everybag.express.ui.resultscreen

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
interface ISearchResultsViewItemListener {
    fun onItemClick(image: ByteArray)

    fun onOpenUrl(link: String)
}