package com.example.kimhun.thewalker

import android.graphics.drawable.Drawable

/**
 * Created by Hp on 2017-09-13.
 */
class ItemsListItem() {
    private var itemImage : Drawable? = null
    private var itemName : String = ""
    private var itemCost : Int? = null
    private var itemContext : String = ""

    fun setItemImage( image : Drawable? ) { this.itemImage = image }
    fun setItemName( name : String ) { this.itemName = name }
    fun setItemCost( point : Int? ) { this.itemCost = point }
    fun setItemContext( context : String ) {this.itemContext = context}

    fun getItemImage() : Drawable? = this.itemImage
    fun getItemName() : String = this.itemName
    fun getItemCost() : Int?  = this.itemCost
    fun getItemContext() : String = this.itemContext
}