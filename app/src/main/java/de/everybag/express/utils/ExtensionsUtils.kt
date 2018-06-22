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

package de.everybag.express.utils

import de.everybag.express.model.OfferParcelable
import io.nyris.sdk.Offer

/**
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