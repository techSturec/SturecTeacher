package com.sturec.sturecteacher.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sturec.sturecteacher.databinding.FragmentDashboardBinding
import com.sturec.sturecteacher.util.UiEvents
import kotlinx.coroutines.launch
import com.sturec.sturecteacher.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.testUserData.setOnClickListener{
            lifecycleScope.launch{
                Log.e("testing get user",dashboardViewModel.userDataRepositoryImpl.getUserData(1).toString())
            }
        }

        lifecycleScope.launch{
            dashboardViewModel.uiEvent.collect{event->
                when(event)
                {
                    is UiEvents.ShowSnackbar->{
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is UiEvents.Navigate->{
                        findNavController().navigate(event.actionId)
                    }
                }
            }
        }

        binding.composeDashboard.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ManageClassrooms(dashboardViewModel)
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun ManageClassrooms(
    dashboardViewModel: DashboardViewModel
) {
//    val localDensity = LocalDensity.current
//    var constraintLayoutHeight by remember {
//        mutableStateOf(0f)
//    }
    val bottomGuidelineFraction = 0.296f
//    val heightForBox = with(localDensity) {(constraintLayoutHeight*(1-bottomGuidelineFraction)).toDp()}

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
//                constraintLayoutHeight = coordinates.size.height.toFloat()
            }
    ) {
        val (text, buttonRow) = createRefs()
        val startGuideline = createGuidelineFromStart(0.044f)
        val bottomGuideline = createGuidelineFromTop(bottomGuidelineFraction)

        Text(
            "Classroom management",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(text){
                start.linkTo(startGuideline)
                bottom.linkTo(bottomGuideline)
            }
        )

//        Log.e("height", heightForBox.toString())

        Box(
            modifier = Modifier
                .constrainAs(buttonRow) {
                    top.linkTo(text.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
                .fillMaxWidth(),
//                .height(heightForBox - paddingBetweenTextAndBox),
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconWithTitle(
                    title = "Classroom",
                    imageResource = R.drawable.classroom_icon
                ) {
                    dashboardViewModel.onEvent(DashboardEvent.OnClassroomButtonClicked)
                }

                IconWithTitle(
                    title = "Subjects",
                    imageResource = R.drawable.bookshelf
                ) {
                    dashboardViewModel.onEvent(DashboardEvent.OnSubjectsButtonClicked)
                }

                IconWithTitle(
                    title = "Attendance",
                    imageResource = R.drawable.attendance
                ){
                    dashboardViewModel.onEvent(DashboardEvent.OnAttendanceButtonClicked)
                }

                IconWithTitle(
                    title = "Bulletin",
                    imageResource = R.drawable.bulletin
                ){
                    dashboardViewModel.onEvent(DashboardEvent.OnBulletinButtonClicked)
                }
            }
        }
    }
}

@Composable
fun IconWithTitle(
    title:String,
    imageResource:Int,
    onClick: ()->Unit
) {
    Box(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 10.dp, top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                )
                {
                    Icon(
                        painter = painterResource(id = imageResource),
                        contentDescription = "$title icon",
                        modifier = Modifier
                            .fillMaxHeight()
                    )
                }
            }

            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}