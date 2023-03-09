package com.sturec.sturecteacher.data.bulletin


data class AnnouncementData(
    val title:String = "",
    val message:String = "",
    var date:String = "",
    val sender:String = "",
    val toRole:String = ""
)