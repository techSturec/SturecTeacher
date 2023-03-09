package com.sturec.sturecteacher.data.bulletin

import kotlinx.coroutines.flow.Flow

interface BulletinRepository {

    fun getAllAnnouncements(): Flow<MutableList<AnnouncementData>>
}