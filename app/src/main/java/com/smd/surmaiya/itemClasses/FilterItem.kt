package com.smd.surmaiya.itemClasses

data class FilterItem(val filter: String, var isSelected: Boolean = false){
    constructor(filter: String) : this(filter, false)
}
