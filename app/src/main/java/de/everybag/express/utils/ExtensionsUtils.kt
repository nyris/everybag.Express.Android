package de.everybag.express.utils

import de.everybag.express.model.OfferParcelable
import io.nyris.sdk.Offer

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
fun List<Offer>.toParcelable(): ArrayList<OfferParcelable> {
    val offersParcelable = ArrayList<OfferParcelable>()

    for (offer in this) {
        val offerParcelable = OfferParcelable(offer.id!!,
                offer.title,
                offer.description,
                offer.descriptionLong,
                offer.language,
                offer.brand,
                offer.catalogNumbers,
                offer.customIds,
                offer.keywords,
                offer.categories,
                offer.availability,
                offer.groupId,
                offer.priceStr,
                offer.salePrice,
                offer.links,
                offer.images,
                offer.metadata,
                offer.sku,
                offer.score)
        offersParcelable.add(offerParcelable)
    }
    return offersParcelable
}