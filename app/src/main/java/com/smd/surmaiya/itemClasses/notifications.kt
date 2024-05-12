package com.smd.surmaiya.itemClasses

data class notifications(
    val title:String,
    val message: String,
){
    constructor(): this("","")
}
