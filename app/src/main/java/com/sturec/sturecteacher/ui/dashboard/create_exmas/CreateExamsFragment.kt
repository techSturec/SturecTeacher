package com.sturec.sturecteacher.ui.dashboard.create_exmas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.sturec.sturecteacher.R
import com.sturec.sturecteacher.data.create_exams.ExamData
import com.sturec.sturecteacher.databinding.FragmentCreateExamsBinding
import com.sturec.sturecteacher.ui.theme.AppTheme
import com.sturec.sturecteacher.util.UiEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreateExamsFragment : Fragment() {

    private var _binding: FragmentCreateExamsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this)[CreateExamsViewModel::class.java]
        _binding = FragmentCreateExamsBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.createExamsComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme(
                    isSystemInDarkTheme()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        FragmentCreateExamsUI(
                            viewModel
                        )
                    }
                }
            }
        }

        return root
    }

}

@Composable
fun FragmentCreateExamsUI(
    viewModel: CreateExamsViewModel
) {

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {

    }
}