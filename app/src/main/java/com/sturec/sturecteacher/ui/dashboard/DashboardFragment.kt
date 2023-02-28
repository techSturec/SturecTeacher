package com.sturec.sturecteacher.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.sturec.sturecteacher.databinding.FragmentDashboardBinding
import com.sturec.sturecteacher.util.UiEvents
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        lifecycleScope.launch{
            dashboardViewModel.uiEvent.collect{
                when(it)
                {
                    is UiEvents.ShowSnackbar->{
                        Snackbar.make(requireView(), it.message, Snackbar.LENGTH_SHORT)
                            .show()
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
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        Button(onClick = {
            dashboardViewModel.onEvent(DashboardEvent.ButtonClicked("classroom"))
        }
        ) {
            Text("classroom")
        }

        Button(onClick = {
            dashboardViewModel.onEvent(DashboardEvent.ButtonClicked("subjects"))
        }
        ) {
            Text("subjects")
        }

        Button(onClick = {
            dashboardViewModel.onEvent(DashboardEvent.ButtonClicked("attendance"))
        }
        ) {
            Text("attendance")
        }

        Button(onClick = {
            dashboardViewModel.onEvent(DashboardEvent.ButtonClicked("announcements"))
        }
        ) {
            Text("announcements")
        }
    }
}