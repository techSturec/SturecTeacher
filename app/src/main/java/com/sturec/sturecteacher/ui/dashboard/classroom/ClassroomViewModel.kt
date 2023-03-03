package com.sturec.sturecteacher.ui.dashboard.classroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sturec.sturecteacher.data.classroom_operations.ClassroomData
import com.sturec.sturecteacher.data.classroom_operations.ClassroomOperationsRepositoryImpl
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.Flow
import javax.inject.Inject

@HiltViewModel
class ClassroomViewModel @Inject constructor(
    val classroomOperationsRepository: ClassroomOperationsRepositoryImpl
): ViewModel() {


    fun onEvent(event:ClassroomEvents){
        when(event)
        {
            is ClassroomEvents.GetAssignedClassrooms->{
                viewModelScope.launch {

//                        _dataMap.value = classroomOperationsRepository.getAllClassroomsData(data)
//                        Log.e("dataMap", dataMap.value.toString())

                }
            }
        }
    }
}