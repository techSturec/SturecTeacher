package com.sturec.sturecteacher.ui.dashboard.subjects

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.sturec.sturecteacher.data.classroom_operations.StudentData
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.data.subject_operations.SubjectData
import com.sturec.sturecteacher.databinding.FragmentSubjectsBinding
import com.sturec.sturecteacher.ui.dashboard.classroom.ClassroomEvents
import com.sturec.sturecteacher.ui.dashboard.classroom.TableListItem
import com.sturec.sturecteacher.ui.theme.AppTheme
import com.sturec.sturecteacher.ui.theme.md_theme_light_primary50
import com.sturec.sturecteacher.ui.theme.primary90
import com.sturec.sturecteacher.util.StringHashing
import com.sturec.sturecteacher.util.UiEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.security.auth.Subject

@AndroidEntryPoint
class SubjectsFragment : Fragment() {

    private var _binding: FragmentSubjectsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this)[SubjectsViewModel::class.java]
        _binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        val root = binding.root

        lifecycleScope.launch {
            viewModel.uiEvents.collect {
                when (it) {
                    is UiEvents.ShowSnackbar -> {
                        Snackbar.make(root, it.message, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> {

                    }
                }
            }
        }

        binding.fragmentSubjectsComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme(isSystemInDarkTheme()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SubjectsFragmentUi(viewModel)
                    }
                }
            }
        }

        return root
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsFragmentUi(
    viewModel: SubjectsViewModel
) {

    var fabClicked by remember {
        mutableStateOf(false)
    }

    var subjectNameField by remember {
        mutableStateOf("")
    }

    var teacherNameField by remember {
        mutableStateOf("")
    }
    var teacherEmailField by remember {
        mutableStateOf("")
    }

    val initialList = mutableListOf(
        TeacherAssignedClassroomData(
            1,
            "A",
            "",
            "Loading"
        )
    )

    val classroomList =
        viewModel.classroomOperationsRepositoryImpl.getAssignedClassroomsList().collectAsState(
            initial = initialList
        )

    var selectedButton by remember {
        mutableStateOf(classroomList.value[0])
    }
    var flag by remember {
        mutableStateOf(true)
    }


//    Log.e("get classroom test", classroomList.value.toString())

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {

        val (bgCard, classRow, tableCard, totalStudents, addSubject, refreshList,
            addStudentPopUp, loadingIcon, loadingIcon2) = createRefs()
        val bgCardGuideline = createGuidelineFromTop(0.12625f)
        val tableCardGuideline = createGuidelineFromBottom(0.0869f)
        val addStudentTopGuideline = createGuidelineFromTop(0.2f)
        val addStudentBottomGuideline = createGuidelineFromBottom(0.2f)
        val addStudentStartGuideline = createGuidelineFromStart(0.1f)
        val addStudentEndGuideline = createGuidelineFromEnd(0.1f)


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
                    "Subject Management",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

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
            for (i in classroomList.value) {
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



        if (classroomList.value == initialList) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.constrainAs(loadingIcon) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
            )
        } else {
            var subjectsData: State<List<SubjectData>>
            if (flag) {
                subjectsData = viewModel.subjectOperationsRepositoryImpl
                    .getAllSubjectsData(selectedButton).collectAsState(initial = emptyList())
            } else {
                subjectsData = viewModel.subjectOperationsRepositoryImpl
                    .getAllSubjectsData(selectedButton).collectAsState(initial = emptyList())
            }
            val emptySubjectData = listOf(SubjectData())
//            Log.e("loading issue", subjectsData.value.toString())


            if (subjectsData.value.isEmpty()) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.constrainAs(loadingIcon2) {
                        centerHorizontallyTo(parent)
                        centerVerticallyTo(parent)
                    }
                )
            } else {
                Text(
                    text = if (subjectsData.value != emptySubjectData) {
                        "Total Subjects: - ${subjectsData.value.size}"
                    } else {
                        "No Subject Created Yet!"
                    },
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.constrainAs(totalStudents) {
                        top.linkTo(classRow.bottom, margin = 10.dp)
                        centerHorizontallyTo(parent, 0.0743f)
                    }
                )

                Card(
                    modifier = Modifier
                        .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                        .constrainAs(tableCard) {
                            top.linkTo(totalStudents.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, 20.dp)
                            height = Dimension.fillToConstraints
                            width = Dimension.fillToConstraints
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = primary90
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier
                    ) {
                        items(subjectsData.value.size) {
                            if (subjectsData.value != emptySubjectData) {
                                TableItem(
                                    SubjectData(
                                        subjectName = subjectsData.value[it].subjectName,
                                        teacherName = subjectsData.value[it].teacherName,
                                        teacherMail = subjectsData.value[it].teacherMail
                                    )
                                )
                            }
                        }
                    }
                }

//        Icon(
//            imageVector = Icons.Default.Add,
//            contentDescription = "Add Subject",
//            tint = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.constrainAs(addSubject){
//                bottom.linkTo(tableCard.top)
//                top.linkTo(classRow.top)
//                end.linkTo(refreshList.start)
//                height = Dimension.fillToConstraints
//                width = height
//            }
//        )


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
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Add Subject",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
                                )

                                TextField(
                                    value = subjectNameField,
                                    onValueChange = {
                                        subjectNameField = it
                                    },
                                    placeholder = {
                                        Text(
                                            "Add Subject Name",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Subject Name",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    singleLine = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "clear subject name",
                                            modifier = Modifier.clickable {
                                                subjectNameField = ""
                                            },
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                                TextField(
                                    value = teacherNameField,
                                    onValueChange = {
                                        teacherNameField = it
                                    },
                                    placeholder = {
                                        Text(
                                            "Add Teacher Name",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Teacher Name",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    singleLine = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "clear admission no",
                                            modifier = Modifier.clickable {
                                                teacherNameField = ""
                                            },
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                                TextField(
                                    value = teacherEmailField,
                                    onValueChange = {
                                        teacherEmailField = it.replace(" ", "")
                                    },
                                    placeholder = {
                                        Text(
                                            "Add Teacher Email",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Teacher Email",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    singleLine = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear Teacher Name",
                                            modifier = Modifier.clickable {
                                                teacherEmailField = ""
                                            },
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.onEvent(
                                        SubjectsEvent.OnSaveSubjectButtonClicked(
                                            data = SubjectData(
                                                subjectName = subjectNameField,
                                                teacherMail = teacherEmailField,
                                                teacherName = teacherNameField
                                            ),
                                            classroomData = selectedButton
                                        )
                                    )
//                                    Log.e("testing hash", GuavaTest().testHashingSha256(teacherEmailField))

                                    flag = !flag

                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                enabled = (subjectNameField.isNotEmpty()
                                        && teacherNameField.isNotEmpty()
                                        && teacherEmailField.isNotEmpty()),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Save Subject",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "Save Subject",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = {
                        flag = !flag
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.constrainAs(refreshList) {
                        end.linkTo(parent.end, 15.dp)
                        bottom.linkTo(parent.bottom, 15.dp)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh List",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                FloatingActionButton(
                    onClick = {
                        fabClicked = !fabClicked
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.constrainAs(addSubject) {
                        end.linkTo(refreshList.start, margin = 10.dp)
                        bottom.linkTo(parent.bottom, 15.dp)
                    }
                ) {
                    Icon(
                        imageVector = if (fabClicked) {
                            Icons.Default.Close
                        } else {
                            Icons.Default.Add
                        },
                        contentDescription = "Add Subject",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

    }
}

@Composable
fun TableItem(
    subjectData: SubjectData
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
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.width(40.dp))
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                subjectData.subjectName,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .padding(top = 10.dp, start = 5.dp)
                                    .horizontalScroll(rememberScrollState())
                            )
                            Text(
                                "Teacher Name: - ${subjectData.teacherName}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(top = 3.dp, start = 5.dp)
                                    .horizontalScroll(rememberScrollState())
                            )

                            Text(
                                "Teacher Mail: - ${subjectData.teacherMail}",
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


@Composable
fun TableItemAlt(
    subjectData: SubjectData
) {
    val cardColor = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )


    Card(
        colors = cardColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = subjectData.subjectName,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            Text(
                text = subjectData.teacherName,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp, top = 5.dp)
            )
            Text(
                text = subjectData.teacherMail,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp, top = 5.dp, bottom = 5.dp)
            )
        }
    }

//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(10.dp)
//    ) {
//        Card(
//            modifier = Modifier
//                .height(40.dp)
//                .weight(0.7f)
//                .padding(horizontal = 4.dp),
//            colors = cardColor
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .horizontalScroll(rememberScrollState()),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    subject,
//                    color = MaterialTheme.colorScheme.onPrimaryContainer,
//                    modifier = Modifier.padding(horizontal = 4.dp)
//                )
//            }
//        }
//
//        Card(
//            modifier = Modifier
//                .height(40.dp)
//                .weight(1f)
//                .padding(horizontal = 4.dp),
//            colors = cardColor
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .horizontalScroll(rememberScrollState()),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    teacherName,
//                    color = MaterialTheme.colorScheme.onPrimaryContainer,
//                    modifier = Modifier.padding(horizontal = 4.dp)
//                )
//            }
//        }
//
//        Card(
//            modifier = Modifier
//                .height(40.dp)
//                .weight(0.8f)
//                .padding(horizontal = 4.dp),
//            colors = cardColor
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .horizontalScroll(rememberScrollState()),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    emailID,
//                    color = MaterialTheme.colorScheme.onPrimaryContainer,
//                    modifier = Modifier.padding(horizontal = 4.dp)
//                )
//            }
//        }
//    }
}