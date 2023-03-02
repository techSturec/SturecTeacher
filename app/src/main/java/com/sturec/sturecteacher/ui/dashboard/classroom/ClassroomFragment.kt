package com.sturec.sturecteacher.ui.dashboard.classroom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.sturec.sturecteacher.R
import com.sturec.sturecteacher.databinding.FragmentClassroomBinding

class ClassroomFragment : Fragment() {

    private var _binding: FragmentClassroomBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = ViewModelProvider(this)[ClassroomViewModel::class.java]

        _binding = FragmentClassroomBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.classroomComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ClassroomFragmentUi()
            }
        }

        return root
    }

}

@Composable
fun ClassroomFragmentUi() {
    ConstraintLayout(
        modifier = Modifier
    ) {
        val (heading, classRow) = createRefs()
//        val headingBottomGuideLine = createGuidelineFromTop(0.118f)


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
                }
                .horizontalScroll(rememberScrollState())
        ) {
            val list: List<String> = listOf("6-A", "6-B", "6-C", "6-D", "7-A", "7-B")
            var selectedButton by remember {
                mutableStateOf("6-A")
            }
            for (i in list) {
                Button(
                    onClick = {
                        selectedButton = i
                    },
                    modifier = Modifier
                        .defaultMinSize(75.dp)
                        .height(37.dp)
                        .padding(end = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedButton == i) {
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
                            text = i,
                            color = Color.Black
                        )
                    }
                }
            }
        }

    }
}