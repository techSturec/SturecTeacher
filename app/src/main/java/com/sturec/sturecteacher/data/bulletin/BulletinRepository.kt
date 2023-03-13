package com.sturec.sturecteacher.data.bulletin

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface BulletinRepository {

    fun getAllAnnouncements(): Flow< MutableList <Pair <LocalDate, MutableList <AnnouncementData> > > >
}