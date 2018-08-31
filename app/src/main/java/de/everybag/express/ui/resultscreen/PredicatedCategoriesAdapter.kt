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
import kotlinx.android.synthetic.main.layout_search_item_category_item.view.*
import java.io.ByteArrayOutputStream

/**
 * SearchResultsAdapter.kt
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */

internal class PredicatedCategoriesAdapter : RecyclerView.Adapter<PredicatedCategoriesAdapter.PredicatedCategoryViewHolder>() {
    private val mPredicatedCategories = mutableMapOf<String, Float>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredicatedCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_search_item_category_item, parent, false)
        return PredicatedCategoryViewHolder(itemView)
    }

    fun setPredictedCategories(predicatedCategories : Map<String, Float>) {
        mPredicatedCategories.clear()
        mPredicatedCategories.putAll(predicatedCategories)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: PredicatedCategoryViewHolder, position: Int) {
        val key :String = mPredicatedCategories.keys.toList()[position]
        var transformed = if(mPredicatedCategories.keys.toList()[position].isEmpty()) "Empty"
        else key

        val splitted = transformed.split(">")
        transformed = splitted[splitted.size-1]

        val value = Math.round(mPredicatedCategories[key]!!*100)

        viewHolder.tvCategory.text = "$transformed : $value%"
    }

    override fun getItemCount(): Int {
        return mPredicatedCategories.size
    }

    internal inner class PredicatedCategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var tvCategory: TextView = view.tvCategory
    }
}
