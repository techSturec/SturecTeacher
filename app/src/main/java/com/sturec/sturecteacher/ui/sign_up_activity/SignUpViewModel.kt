package com.sturec.sturecteacher.ui.sign_up_activity

import androidx.lifecycle.ViewModel
import com.sturec.sturecteacher.data.user_data.UserDataRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val userDataRepositoryImpl: UserDataRepositoryImpl
):ViewModel() {
}