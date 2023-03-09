package com.sturec.sturecteacher.ui.dashboard.attendance

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
//import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sturec.sturecteacher.R
import com.sturec.sturecteacher.data.attendance.AttendanceData
import com.sturec.sturecteacher.data.classroom_operations.ClassroomListStudentData
import com.sturec.sturecteacher.data.classroom_operations.StudentData
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.data.subject_operations.SubjectData
import com.sturec.sturecteacher.databinding.FragmentAttendanceBinding
import com.sturec.sturecteacher.databinding.FragmentBulletinBinding
import com.sturec.sturecteacher.ui.dashboard.bulletin.BulletinFragmentUi
import com.sturec.sturecteacher.ui.theme.AppTheme
import com.sturec.sturecteacher.util.UiEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.reflect.KProperty


@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[AttendanceViewModel::class.java]
        val root = binding.root

        lifecycleScope.launch{
            viewModel.uiEvents.collect{
                when(it){
                    is UiEvents.Navigate->{
                        findNavController().navigate(it.actionId)
                    }
                    is UiEvents.ShowSnackbar->{
                        Snackbar.make(
                            root,
                            it.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else->{

                    }
                }
            }
        }

        binding.fragmentAttendanceComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme(
                    useDarkTheme = isSystemInDarkTheme()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ){
                        AttendanceFragmentUI(viewModel)
                    }
                }
            }
        }

        return root
    }

}

@Composable
fun AttendanceFragmentUI(
    viewModel: AttendanceViewModel
) {
    val context = LocalContext.current
    val initialList = listOf(TeacherAssignedClassroomData())

    var classroomList: State<List<TeacherAssignedClassroomData>> = viewModel.attendanceRepositoryImpl.getAssignedClassroomList().collectAsState(
        initial = listOf(TeacherAssignedClassroomData())
    )

    var selectedButton by remember {
        mutableStateOf(initialList[0])
    }

    var flag by remember {
        mutableStateOf(true)
    }
    val mCalendar = Calendar.getInstance()
    val mYearNow = mCalendar.get(Calendar.YEAR)
    val mMonthNow = mCalendar.get(Calendar.MONTH)
    val mDayNow = mCalendar.get(Calendar.DAY_OF_MONTH)

    val scope = rememberCoroutineScope()


//    val mDate = remember {
//        mutableStateOf("$mDayNow/${mMonthNow+1}/$mYearNow")
//    }
    val mDate = remember {
        mutableStateOf("$mDayNow-${mMonthNow+1}-$mYearNow")
    }
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth-${mMonth+1}-$mYear"
        },
        mYearNow, mMonthNow, mDayNow
    )

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (bgCard, classRow, loadingIcon, datePicker,loadingIcon2, list, save) = createRefs()
        val bgCardGuideline = createGuidelineFromTop(0.12625f)


        //heading card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp),
            modifier = Modifier.constrainAs(bgCard) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(bgCardGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Attendance Management",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }


        //Classroom List Row
        Row(
            modifier = Modifier
                .constrainAs(classRow) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(bgCard.bottom)
                    height = Dimension.value(50.dp)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 10.dp, top = 10.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedButton == initialList[0]) {
                selectedButton = classroomList.value[0]
            }
            for(i in classroomList.value){
                Button(
                    onClick = {
                        selectedButton = i
                        flag = !flag
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedButton == i) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .defaultMinSize(minWidth = 71.dp)
                        .padding(horizontal = 5.dp)
                ) {
                    Text(
                        text = i.className,
                        color = if (selectedButton == i) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
        }

        //loading
        if (classroomList.value == initialList) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.constrainAs(loadingIcon) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
            )
        }
        //rest of UI
        else{
            val initialClassroomList = mutableListOf(ClassroomListStudentData())
            val listOfStudents = viewModel
                .attendanceRepositoryImpl
                .getStudentsList(selectedButton)
                .collectAsState(
                    initial = initialClassroomList
                ).value

            var attendanceData = if(flag){
                viewModel
                    .attendanceRepositoryImpl
                    .getSpecificDateAttendance(mDate.value, selectedButton)
                    .collectAsState(initial = null)
            }else {
                viewModel
                    .attendanceRepositoryImpl
                    .getSpecificDateAttendance(mDate.value, selectedButton)
                    .collectAsState(initial = null)
            }


            if(listOfStudents!=initialClassroomList && attendanceData.value!=null)
            {
                Button(
                    onClick = {
                        datePickerDialog.show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.constrainAs(datePicker){
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(classRow.bottom)
                    }
                ) {
                    Text(
                        mDate.value,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .constrainAs(list) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(datePicker.bottom)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                            width = Dimension.fillToConstraints
                        }
                        .padding(start = 11.dp, end = 21.dp)
                ) {
                    items(listOfStudents.size){
                        var status:String

                            if(attendanceData.value!!.absentList.contains(listOfStudents[it].mail)){
                                status = "absent"
                            }else if(attendanceData.value!!.leaveList.contains(listOfStudents[it].mail)){
                                status = "leave"
                            }else {
                                status = "present"
                            }


                        TableItem(
                            studentName = listOfStudents[it].studentName,
                            status,
                            onAbsent = {
                                attendanceData.value!!.absentList.add(listOfStudents[it].mail)
                            },
                            onLeave = {
                                attendanceData.value!!.leaveList.add(listOfStudents[it].mail)
                            },
                            onPresent = {
                                attendanceData.value!!.absentList.remove(listOfStudents[it].mail)
                                attendanceData.value!!.leaveList.remove(listOfStudents[it].mail)
                            }
                        )
                    }
                }
                
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            viewModel.attendanceRepositoryImpl
                                .uploadAttendanceOfSpecificDate(
                                    selectedButton,
                                    attendanceData.value!!,
                                    mDate.value
                                )
                            viewModel.onEvent(AttendanceEvents.Snackbar("Saved Attendance Successfully"))
                            viewModel.onEvent(AttendanceEvents.Navigate)
                        }
                    },
                    modifier = Modifier.constrainAs(save){
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Save Button"
                    )
                }


            }else{
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.constrainAs(loadingIcon2) {
                        centerHorizontallyTo(parent)
                        centerVerticallyTo(parent)
                    }
                )
            }
        }
    }

}

@Composable
fun TableItem(
    studentName:String,
    status: String,
    onAbsent:()->Unit,
    onLeave:()->Unit,
    onPresent:()->Unit
) {
    var checkedOption by remember {
        mutableStateOf(status)
    }

    if(status.isNotEmpty())
    {
        checkedOption = status
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 120.dp)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 120.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.width(30.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                modifier = Modifier.defaultMinSize(minHeight = 120.dp)
            ) {
                Row {
                    Spacer(modifier = Modifier.width(40.dp))
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            studentName,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(top = 10.dp, start = 5.dp)
                                .horizontalScroll(rememberScrollState())
                        )
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Switch(
                                checked = checkedOption=="present",
                                onCheckedChange = {
                                    onPresent()

                                    if(checkedOption=="present") {
                                        checkedOption = "absent"
                                        onAbsent()
                                    }else{
                                        checkedOption = "present"
                                    }
                                },
                                thumbContent = {
                                    Text(
                                        text = "P",
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
//                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Switch(
                                checked = checkedOption=="absent",
                                onCheckedChange = {
                                    onPresent()
                                    if(checkedOption=="absent")
                                    {
                                        checkedOption = "present"
                                    }else {
                                        checkedOption = "absent"
                                        onAbsent()
                                    }
                                },
                                thumbContent = {
                                    Text(
                                        text = "A",
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
//                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Switch(
                                checked = checkedOption=="leave",
                                onCheckedChange = {
                                    onPresent()
                                    if(checkedOption=="leave")
                                    {
                                        checkedOption = "present"
                                    }else{
                                        checkedOption = "leave"
                                        onLeave()
                                    }
                                },
                                thumbContent = {
                                    Text(
                                        text = "L",
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
//                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }
                }
            }

        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Card(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                )
//                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.goal
                    ),
                    contentDescription = "Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
