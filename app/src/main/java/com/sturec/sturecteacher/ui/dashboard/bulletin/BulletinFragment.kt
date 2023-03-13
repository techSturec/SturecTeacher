package com.sturec.sturecteacher.ui.dashboard.bulletin

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
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModelProvider
import com.sturec.sturecteacher.R
import com.sturec.sturecteacher.data.bulletin.AnnouncementData
import com.sturec.sturecteacher.data.subject_operations.SubjectData
import com.sturec.sturecteacher.databinding.FragmentBulletinBinding
import com.sturec.sturecteacher.databinding.FragmentClassroomBinding
import com.sturec.sturecteacher.ui.dashboard.classroom.ClassroomViewModel
import com.sturec.sturecteacher.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@AndroidEntryPoint
class BulletinFragment : Fragment() {
    private var _binding: FragmentBulletinBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this)[BulletinViewModel::class.java]
        _binding = FragmentBulletinBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.fragmentBulletinComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme(
                    useDarkTheme = isSystemInDarkTheme()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        BulletinFragmentUi(viewModel)
                    }
                }
            }
        }

        return root
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulletinFragmentUi(
    viewModel: BulletinViewModel
) {

    val listOfAnnouncements = viewModel.bulletinRepositoryImpl
        .getAllAnnouncements()
        .collectAsState(initial = emptyList())

    var searchBt by remember {
        mutableStateOf(false)
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    val todayDate = LocalDate.now()



    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (bgCard, classRow, list) = createRefs()
        val bgCardGuideline = createGuidelineFromTop(0.12625f)

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
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 21.dp, start = 13.dp, end = 11.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    if (!searchBt) {
                        Text(
                            "Bulletins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(3f)

                        )
                    } else {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            maxLines = 1,
                            modifier = Modifier.weight(3f)
                        )
                    }
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = {
                                searchBt = !searchBt
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Button",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = R.drawable.sort
                                ),
                                contentDescription = "Filter Button",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                }
            }
        }

        if (listOfAnnouncements.value.isEmpty()) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .constrainAs(list) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(bgCard.bottom)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .padding(horizontal = 10.dp)

            ) {
                items(listOfAnnouncements.value.size) {
                    val temp = listOfAnnouncements.value[it]
                    val date = temp.first
                    Text(
                        text = if (date == todayDate) {
                            "Today"
                        } else {
                            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                        },
                        modifier = Modifier.padding(top = 30.dp)
                    )
                    for(i in temp.second){
                        TableItem(data = i)
                    }
//                Text(
//                    text = listOfAnnouncements.value[it].message
//                )
                    //TableItem(data = listOfAnnouncements.value[it])
                }
            }
        }


    }
}

@Composable
fun TableItem(
    data: AnnouncementData
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.width(10.dp))
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
                        Spacer(modifier = Modifier.width(0.dp))
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                data.title,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .padding(top = 10.dp, start = 5.dp)
                                    .horizontalScroll(rememberScrollState())
                            )
                            Text(
                                data.message,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(top = 3.dp, start = 5.dp)
                                    .horizontalScroll(rememberScrollState())
                            )

                            Text(
                                "Sent By: - ${data.sender}",
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

//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.CenterStart
//        ) {
//            Card(
//                modifier = Modifier
//                    .width(60.dp)
//                    .height(60.dp),
//                shape = RoundedCornerShape(14.dp),
//                colors = CardDefaults.cardColors(
//                    MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
//                )
////                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
//            ) {
//                Image(
//                    painter = painterResource(
//                        id = R.drawable.goal
//                    ),
//                    contentDescription = "Icon",
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
    }
}