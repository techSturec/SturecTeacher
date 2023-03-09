package com.sturec.sturecteacher.ui.dashboard.bulletin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.sturec.sturecteacher.R
import com.sturec.sturecteacher.databinding.FragmentBulletinBinding
import com.sturec.sturecteacher.databinding.FragmentClassroomBinding
import com.sturec.sturecteacher.ui.dashboard.classroom.ClassroomViewModel
import com.sturec.sturecteacher.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BulletinFragment : Fragment() {
    private var _binding: FragmentBulletinBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                    ){
                        BulletinFragmentUi(viewModel)
                    }
                }
            }
        }

        return root
    }

}

@Composable
fun BulletinFragmentUi(
    viewModel: BulletinViewModel
) {

    val listOfAnnouncements = viewModel.bulletinRepositoryImpl.getAllAnnouncements().collectAsState(initial = emptyList())

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ){
        val (bgCard, classRow, implementationBt, list) = createRefs()
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
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Bulletins",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Button(
            onClick = {
                viewModel.onEvent(BulletinEvent.OnButtonClicked)
            },
            modifier = Modifier.constrainAs(implementationBt){
                centerVerticallyTo(parent)
                centerHorizontallyTo(parent)
            }
        ) {
            Text(
                "Implementation",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

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
        ){
            items(listOfAnnouncements.value.size) {
                Text(
                    text = listOfAnnouncements.value[it].message
                )
            }
        }


    }
}