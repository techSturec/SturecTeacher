package com.sturec.sturecteacher.ui.dashboard.classroom

import androidx.lifecycle.ViewModel
import com.sturec.sturecteacher.data.user_data.UserDataRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClassroomViewModel @Inject constructor(
    userDataRepositoryImpl: UserDataRepositoryImpl
): ViewModel() {

}