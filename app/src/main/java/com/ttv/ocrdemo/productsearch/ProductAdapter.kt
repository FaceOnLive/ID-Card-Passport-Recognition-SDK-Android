/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ttv.ocrdemo.productsearch

import android.graphics.BitmapFactory
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ttv.ocrdemo.R
import com.ttv.ocrdemo.productsearch.ProductAdapter.ProductViewHolder
import org.json.JSONObject

/** Presents the list of product items from cloud product search.  */
class ProductAdapter(public val productList: List<Product>) : Adapter<ProductViewHolder>() {

    class ProductViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {

//        private val imageView: ImageView = view.findViewById(R.id.product_image)
        private val titleView: TextView = view.findViewById(R.id.product_title)
        private val subtitleView: TextView = view.findViewById(R.id.product_subtitle)
        private val imageSize: Int = view.resources.getDimensionPixelOffset(R.dimen.product_item_image_size)

        fun bindProduct(product: Product) {
//            imageView.setImageDrawable(null)
//            if (!TextUtils.isEmpty(product.imageUrl)) {
//                imageView.setImageBitmap(BitmapFactory.decodeFile(product.imageUrl))
//            } else {
//                imageView.setImageResource(R.drawable.favicon)
//            }
            titleView.text = product.title

            val s = SpannableStringBuilder()
            var jsonResult = JSONObject(product.subtitle)
            for(i in 0..jsonResult.names().length() - 1) {
                val keyName:String = jsonResult.names().getString(i)
                //s.append(keyName + ": ").bold { append(jsonResult.getString(keyName)) }.append("\n")
                s.bold { append(keyName) }.append(": " + jsonResult.getString(keyName)).append("\n")
            }
            subtitleView.text = s
        }

        companion object {
            fun create(parent: ViewGroup) =
                ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder.create(parent)

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bindProduct(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}
