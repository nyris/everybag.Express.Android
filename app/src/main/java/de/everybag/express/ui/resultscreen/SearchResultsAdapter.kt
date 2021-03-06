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

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.everybag.express.R
import de.everybag.express.model.OfferParcelable
import kotlinx.android.synthetic.main.layout_search_item_offer_item.view.*
import java.io.ByteArrayOutputStream

/**
 * SearchResultsAdapter.kt
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */

internal class SearchResultsAdapter(private val listener: ISearchResultsViewItemListener) : RecyclerView.Adapter<SearchResultsAdapter.OfferViewHolder>() {
    private val mOffers = ArrayList<OfferParcelable>()
    private var mBottomListener : IOnBottomReachedListener? = null

    fun setOnBottomListener(listener: IOnBottomReachedListener){
        mBottomListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_search_item_offer_item, parent, false)
        return OfferViewHolder(itemView)
    }

    fun setOffers(offers: ArrayList<OfferParcelable>) {
        mOffers.clear()
        mOffers.addAll(offers)
        notifyDataSetChanged()
    }


    private val onClickUrl = View.OnClickListener {
        val offer = it.tag as OfferParcelable
        if (offer.links?.main != null) {
            val link = offer.links.main!!
            listener.onOpenUrl(link)
        }
    }

    private val onOfferClick = View.OnClickListener {
        val imOffer = it as ImageView
        val bitmap = (imOffer.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInByte = baos.toByteArray()
        mOffers.clear()
        notifyDataSetChanged()
        listener.onItemClick(imageInByte)
    }

    override fun onBindViewHolder(viewHolder: OfferViewHolder, position: Int) {
        if(position == mOffers.size -1) mBottomListener?.onBottomReached()

        val offer = mOffers[position]
        viewHolder.itemView.tag = offer

        viewHolder.tvTitle.text = offer.title
        if (offer.priceStr == null)
            viewHolder.tvOfferPrice.visibility = View.INVISIBLE
        else {
            viewHolder.tvOfferPrice.visibility = View.VISIBLE
            viewHolder.tvOfferPrice.text = offer.priceStr
        }

        if (offer.images != null && offer.images.isNotEmpty()) {
            val url = "${offer.images[0]}?r=512x512"
            Picasso
                    .get()
                    .load(url)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(viewHolder.imOffer)
        }

        if (offer.links?.main != null) {
            viewHolder.imgCart.visibility = View.VISIBLE
            viewHolder.imgCart.tag = offer
            viewHolder.btnShopping.tag = offer
            viewHolder.imgCart.setOnClickListener(onClickUrl)
            viewHolder.btnShopping.setOnClickListener(onClickUrl)
        } else {
            viewHolder.imgCart.visibility = View.GONE
            viewHolder.btnShopping.setOnClickListener(null)
            viewHolder.imgCart.setOnClickListener(null)
        }

        if (offer.links == null && offer.priceStr == null) {
            viewHolder.btnShopping.visibility = View.GONE
        } else {
            viewHolder.btnShopping.visibility = View.VISIBLE
        }

        viewHolder.imOffer.tag = offer
        viewHolder.imOffer.setOnClickListener(onOfferClick)
    }

    override fun getItemCount(): Int {
        return mOffers.size
    }

    internal inner class OfferViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var imOffer: ImageView = view.imOffer
        var tvOfferPrice: TextView = view.tvOfferPrice
        var tvTitle: TextView = view.tvTitle
        var btnShopping: View = view.btnShopping
        var imgCart: View = view.imgCart
    }
}
