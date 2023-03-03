package com.sturec.sturecteacher.ui.dashboard.classroom

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sturec.sturecteacher.R
import com.sturec.sturecteacher.data.classroom_operations.AssignedClassroom
import com.sturec.sturecteacher.data.classroom_operations.ClassroomData
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.databinding.FragmentClassroomBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClassroomFragment : Fragment() {

    private var _binding: FragmentClassroomBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this)[ClassroomViewModel::class.java]

        _binding = FragmentClassroomBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.classroomComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                
                LaunchedEffect(key1 = true) {
                    viewModel.onEvent(ClassroomEvents.GetAssignedClassrooms)
                }
                ClassroomFragmentUi(viewModel)
            }
        }

        return root
    }

}

@Composable
fun ClassroomFragmentUi(
    viewModel: ClassroomViewModel,
) {
    var selectedButton by remember {
        mutableStateOf(0)
    }

    val classroomList by viewModel.classroomOperationsRepository.getAssignedClassroomsList().collectAsState(
        initial = emptyList()
    )
    val dataMap by viewModel.classroomOperationsRepository.getAllClassroomsData(classroomList).collectAsState(
        initial = emptyMap()
    )

    ConstraintLayout(
        modifier = Modifier
    ) {
        val (heading, classRow, totalStudentRow, loadingIcon) = createRefs()
//        val headingBottomGuideLine = createGuidelineFromTop(0.118f)
        if((classroomList.isEmpty()&& dataMap.isEmpty()) || dataMap[classroomList[selectedButton].className]==null)
        {
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(loadingIcon){
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo((parent.end))
                }
            )
        }else
        {
            Text(
                text = "Classroom Management",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.constrainAs(heading) {
//                bottom.linkTo(headingBottomGuideLine)
                    centerVerticallyTo(parent, 0.046f)
                    centerHorizontallyTo(parent, 0.5f)
                }
            )

            Row(
                modifier = Modifier
                    .padding(10.dp, 10.dp)
                    .constrainAs(classRow) {
                        top.linkTo(heading.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
            ) {
                LazyRow{
                    items(classroomList.size) {

                        Button(
                            onClick = {
                                selectedButton = it
//                            Log.e("checking ass", assignedClassroomList[selectedButton].toString())
//                            Log.e("checking dataMap", dataMap[assignedClassroomList[selectedButton].className].toString())
                            },
                            modifier = Modifier
                                .defaultMinSize(75.dp)
                                .height(37.dp)
                                .padding(end = 10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedButton == it) {
                                    Color(0xFFFF6666)
                                } else {
                                    Color(0xFFFFD8D8)
                                }
                            )

                        ) {
                            Box(
                                modifier = Modifier.fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = classroomList[it].className,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text =  dataMap[classroomList[selectedButton].className]?.let {
                    it.listOfStudents.size.toString() + " Students"
                } ?:"Total Students",
                color = Color.Red,
                modifier = Modifier
                    .constrainAs(totalStudentRow) {
                        top.linkTo(classRow.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(start = 16.dp, top = 13.dp),
            )
        }







    }
}