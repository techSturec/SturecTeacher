package com.sturec.sturecteacher.ui.dashboard.classroom

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.sturec.sturecteacher.R
import com.sturec.sturecteacher.data.classroom_operations.*
import com.sturec.sturecteacher.databinding.FragmentClassroomBinding
import com.sturec.sturecteacher.ui.theme.AppTheme
import com.sturec.sturecteacher.util.UiEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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

        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvents.ShowSnackbar -> {
                        Snackbar.make(
                            binding.root,
                            event.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is UiEvents.Navigate -> {

                    }
                }
            }
        }

        binding.classroomComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme(useDarkTheme = isSystemInDarkTheme()) {
                    ClassroomFragmentUi(viewModel)
                }
            }
        }

        return root
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomFragmentUi(
    viewModel: ClassroomViewModel,
) {
    var selectedButton by remember {
        mutableStateOf(0)
    }

    val classroomList by viewModel.classroomList.collectAsState(
        initial = emptyList()
    )

    var flag by remember {
        mutableStateOf(false)
    }

//    val temp by viewModel.classroomOperationsRepository.getAllClassroomsData(classroomList)
//        .collectAsState(
//            initial = emptyMap()
//        )

    val initialDataMap = mutableListOf(ClassroomListStudentData())

    var studentNameField by remember {
        mutableStateOf("")
    }

    var mailField by remember {
        mutableStateOf("")
    }

    var rollNoField by remember {
        mutableStateOf("")
    }

    var fabClicked by remember {
        mutableStateOf(false)
    }

    ConstraintLayout(
        modifier = Modifier
    ) {
        val (heading, classRow, totalStudentRow, loadingIcon, loadingIcon2, addStudentsButton,
            addStudentPopUp, table, refreshFab) = createRefs()
        val addStudentTopGuideline = createGuidelineFromTop(0.2f)
        val addStudentBottomGuideline = createGuidelineFromBottom(0.2f)
        val addStudentStartGuideline = createGuidelineFromStart(0.1f)
        val addStudentEndGuideline = createGuidelineFromEnd(0.1f)
//        val headingBottomGuideLine = createGuidelineFromTop(0.118f)
        if (classroomList.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(loadingIcon) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo((parent.end))
                },
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            var dataMap: State<MutableList<ClassroomListStudentData>> = if (flag) {
                viewModel.classroomOperationsRepository.getAllClassroomsData(classroomList[selectedButton])
                    .collectAsState(
                    initial = initialDataMap
                )
            } else {
                viewModel.classroomOperationsRepository.getAllClassroomsData(classroomList[selectedButton])
                    .collectAsState(
                        initial = initialDataMap
                    )
            }

            if (dataMap.value != initialDataMap) {
                Text(
                    text = "Classroom Management",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                    LazyRow {
                        items(classroomList.size) {

                            Button(
                                onClick = {
                                    selectedButton = it
                                    flag = !flag
//                            Log.e("checking ass", assignedClassroomList[selectedButton].toString())
//                            Log.e("checking dataMap", dataMap[assignedClassroomList[selectedButton].className].toString())
                                },
                                modifier = Modifier
                                    .defaultMinSize(75.dp)
                                    .height(37.dp)
                                    .padding(end = 10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedButton == it) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.primaryContainer
                                    }
                                )

                            ) {
                                Box(
                                    modifier = Modifier.fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = classroomList[it].className,
                                        color = if(selectedButton==it){
                                            MaterialTheme.colorScheme.onPrimary
                                        }else{
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = dataMap.value.size.toString() + " Student(s)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .constrainAs(totalStudentRow) {
                            top.linkTo(classRow.bottom)
                            start.linkTo(parent.start)
                        }
                        .padding(start = 16.dp, top = 13.dp),
                )


                LazyColumn(
                    modifier = Modifier
                        .constrainAs(table) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(totalStudentRow.bottom)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                        .padding(horizontal = 10.dp)
                ) {

//                    item {
//                        TableListItem()
//                    }

                    for (i in dataMap.value) {
                        item {
                            TableListItem(
                                i.rollNo,
                                i.studentName,
                                i.mail
                            )
                        }
                    }

                }

                if (fabClicked) {
                    Box(
                        modifier = Modifier
                            .constrainAs(addStudentPopUp) {
                                top.linkTo(addStudentTopGuideline)
                                bottom.linkTo(addStudentBottomGuideline)
                                start.linkTo(addStudentStartGuideline)
                                end.linkTo(addStudentEndGuideline)
                                height = Dimension.fillToConstraints
                                width = Dimension.fillToConstraints
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFD9D9D9)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Add Student",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
                                )

                                TextField(
                                    value = studentNameField,
                                    onValueChange = {
                                        studentNameField = it
                                    },
                                    placeholder = {
                                        Text("Add Student Name")
                                    },
                                    label = {
                                        Text("Student Name")
                                    },
                                    singleLine = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "clear student name",
                                            modifier = Modifier.clickable {
                                                studentNameField = ""
                                            }
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                                TextField(
                                    value = mailField,
                                    onValueChange = {
                                        mailField = it.replace(" ", "")
                                    },
                                    placeholder = {
                                        Text("Add Mail")
                                    },
                                    label = {
                                        Text("Mail")
                                    },
                                    singleLine = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "clear admission no",
                                            modifier = Modifier.clickable {
                                                mailField = ""
                                            }
                                        )
                                    },
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                                TextField(
                                    value = rollNoField,
                                    onValueChange = {
                                        rollNoField = it
                                    },
                                    placeholder = {
                                        Text("Add Roll No.")
                                    },
                                    label = {
                                        Text("Roll No.")
                                    },
                                    singleLine = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "clear roll no",
                                            modifier = Modifier.clickable {
                                                rollNoField = ""
                                            }
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.onEvent(
                                        ClassroomEvents.OnSaveStudentButtonClicked(
                                            StudentData(
                                                mail = mailField,
                                                rollNo = rollNoField,
                                                studentName = studentNameField,
                                                standard = classroomList[selectedButton].standard,
                                                section = classroomList[selectedButton].section,
                                                className = classroomList[selectedButton].className
                                            )
                                        )
                                    )

                                    studentNameField = ""
                                    mailField = ""
                                    rollNoField = ""
                                    fabClicked = false

                                },
                                enabled = mailField.isNotEmpty() && rollNoField.isNotEmpty() && studentNameField.isNotEmpty(),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Save Student"
                                )
                                Text("Save Student")
                            }
                        }
                    }
                }


                FloatingActionButton(
                    onClick = {
                        fabClicked = !fabClicked
                    },
                    modifier = Modifier.constrainAs(addStudentsButton)
                    {
                        centerHorizontallyTo(parent, 0.9333f)
                        centerVerticallyTo(parent, 0.9475f)
                    },
                    containerColor = Color(0xFFFFD8D8),
                    contentColor = Color.Red
                ) {
                    Icon(
                        imageVector = if (fabClicked) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add Students",
                    )
                }

                FloatingActionButton(
                    onClick = {
                        flag = !flag
                    },
                    modifier = Modifier
                        .constrainAs(refreshFab) {
                            end.linkTo(addStudentsButton.start)
                            centerVerticallyTo(parent, 0.9475f)
                        }
                        .padding(end = 8.dp),
                    contentColor = Color.Red,
                    containerColor = Color(0xFFFFD8D8),
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh List"
                    )
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.constrainAs(loadingIcon) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo((parent.end))
                    },
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }


        }
    }
}


@Composable
fun TableListItem(
    rollNo: String,
    studentName: String,
    mail: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 120.dp)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.width(30.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.width(40.dp))
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.Start
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
                            Text(
                                "Mail: - $mail",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(top = 3.dp, start = 5.dp)
                                    .horizontalScroll(rememberScrollState())
                            )

                            Text(
                                "Roll No: - $rollNo",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(top = 3.dp, start = 5.dp, bottom = 5.dp)
                                    .horizontalScroll(rememberScrollState())
                            )
                        }
                    }
                }
            }

        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ){
            Card(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                )
//                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
            ){
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


@Composable
fun TableListItemAlt(
    rollNo: String = "Roll No.",
    studentName: String = "Student Name",
    mail: String = "Mail"
) {
    Row {
        Column(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
//                                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(rollNo)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
//                                .background(Color.Red),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(mail)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
//                                .background(Color.Green),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(studentName)
        }
    }
}