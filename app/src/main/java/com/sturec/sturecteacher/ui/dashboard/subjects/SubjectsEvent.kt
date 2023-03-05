package com.sturec.sturecteacher.ui.dashboard.subjects

import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.data.subject_operations.SubjectData

sealed class SubjectsEvent{
    data class OnSaveSubjectButtonClicked(
        val data:SubjectData,
        val classroomData: TeacherAssignedClassroomData
    ):SubjectsEvent()
}
