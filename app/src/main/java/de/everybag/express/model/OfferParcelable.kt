package de.everybag.express.model

import android.os.Parcelable
import io.nyris.sdk.CustomIds
import io.nyris.sdk.Links
import kotlinx.android.parcel.Parcelize

/**
 * OfferParcelable.kt - Presentation model mapped using Domain model
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
@Parcelize
data class OfferParcelable(val id: String,
                           val title: String?,
                           val description: String?,
                           val descriptionLong: String?,
                           val language: String?,
                           val brand: String?,
                           val catalogNumbers: Array<String>?,
                           val customIds: CustomIds?,
                           val keywords: Array<String>?,
                           val categories: Array<String>?,
                           val availability: String?,
                           val groupId: String? = null,
                           val priceStr: String? = null,
                           val salePrice: String? = null,
                           val links: Links? = null,
                           val images: Array<String>?,
                           val metadata: String? = null,
                           val sku: String? = null,
                           val score: Float) : Parcelable