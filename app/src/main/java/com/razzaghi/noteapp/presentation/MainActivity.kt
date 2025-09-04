package com.razzaghi.noteapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.razzaghi.noteapp.presentation.navigation.MainNavigation
import com.razzaghi.noteapp.presentation.ui.add.AddNoteScreen
import com.razzaghi.noteapp.presentation.ui.add.view_model.AddNoteViewModel
import com.razzaghi.noteapp.presentation.ui.edit.EditNoteScreen
import com.razzaghi.noteapp.presentation.ui.edit.view_model.EditEvent
import com.razzaghi.noteapp.presentation.ui.edit.view_model.EditNoteViewModel
import com.razzaghi.noteapp.presentation.ui.home.view_model.HomeEvent
import com.razzaghi.noteapp.presentation.ui.home.HomeScreen
import com.razzaghi.noteapp.presentation.ui.home.view_model.HomeViewModel
import com.razzaghi.noteapp.presentation.ui.theme.NoteAppTheme
import com.razzaghi.noteapp.presentation.util.getEventById
import com.razzaghi.noteapp.presentation.util.toCalendar
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    private val REFRESH = "REFRESH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteAppTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                var hasPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_CALENDAR
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val readGranted = permissions[Manifest.permission.READ_CALENDAR] ?: false
                    val writeGranted = permissions[Manifest.permission.WRITE_CALENDAR] ?: false
                    hasPermission = readGranted && writeGranted
                }
                LaunchedEffect(Unit) {
                    val readPermission = Manifest.permission.READ_CALENDAR
                    val writePermission = Manifest.permission.WRITE_CALENDAR
                    if (!hasPermission) launcher.launch(arrayOf(readPermission, writePermission))

                }


                NavHost(
                    navController = navController,
                    startDestination = MainNavigation.Home
                ) {
                    composable<MainNavigation.Home> {
                        val viewModel: HomeViewModel = koinInject()
                        val refresh = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow(REFRESH, false)?.collectAsState()
                        LaunchedEffect(refresh) {
                            if (refresh?.value == true) {
                                viewModel.onTriggerEvent(HomeEvent.OnRetryNetwork)
                                navController.previousBackStackEntry?.savedStateHandle?.set(REFRESH, false)
                            }
                        }
                        HomeScreen(
                            errors = viewModel.errors,
                            state = viewModel.state.value,
                            events = viewModel::onTriggerEvent,
                            navigateToAdd = {
                                navController.navigate(MainNavigation.AddNote)
                            },
                            navigateToEdit = {
                                navController.navigate(MainNavigation.EditNote(noteId = it.id))
                            }
                        )
                    }
                    composable<MainNavigation.AddNote> {
                        val viewModel: AddNoteViewModel = koinInject()
                        AddNoteScreen(
                            errors = viewModel.errors,
                            state = viewModel.state.value,
                            action = viewModel.action,
                            events = viewModel::onTriggerEvent,
                            popUp = {
                                navController.previousBackStackEntry?.savedStateHandle?.set(REFRESH, true)
                                navController.popBackStack()
                            }
                        )
                    }
                    composable<MainNavigation.EditNote> {
                        val argument = it.toRoute<MainNavigation.EditNote>()
                        val id = argument.noteId

                        val viewModel: EditNoteViewModel = koinInject()
                        LaunchedEffect(id) {
                            viewModel.onTriggerEvent(EditEvent.GetNote(id))
                        }
                        EditNoteScreen(
                            errors = viewModel.errors,
                            state = viewModel.state.value,
                            action = viewModel.action,
                            events = viewModel::onTriggerEvent,
                            popUp = {
                                navController.previousBackStackEntry?.savedStateHandle?.set(REFRESH, true)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
