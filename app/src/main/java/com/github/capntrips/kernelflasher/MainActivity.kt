package com.github.capntrips.kernelflasher

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.capntrips.kernelflasher.ui.screens.RefreshableScreen
import com.github.capntrips.kernelflasher.ui.screens.backups.BackupsContent
import com.github.capntrips.kernelflasher.ui.screens.backups.SlotBackupsContent
import com.github.capntrips.kernelflasher.ui.screens.error.ErrorScreen
import com.github.capntrips.kernelflasher.ui.screens.main.MainContent
import com.github.capntrips.kernelflasher.ui.screens.main.MainViewModel
import com.github.capntrips.kernelflasher.ui.screens.reboot.RebootContent
import com.github.capntrips.kernelflasher.ui.screens.slot.SlotContent
import com.github.capntrips.kernelflasher.ui.screens.slot.SlotFlashContent
import com.github.capntrips.kernelflasher.ui.screens.updates.UpdatesAddContent
import com.github.capntrips.kernelflasher.ui.screens.updates.UpdatesChangelogContent
import com.github.capntrips.kernelflasher.ui.screens.updates.UpdatesContent
import com.github.capntrips.kernelflasher.ui.screens.updates.UpdatesViewContent
import com.github.capntrips.kernelflasher.ui.theme.KernelFlasherTheme
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalSerializationApi
@ExperimentalUnitApi
class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "MainActivity"
        init {
            Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER))
        }
    }

    private var viewModel: MainViewModel? = null
    private lateinit var mainListener: MainListener
    var isAwaitingResult = false

    private fun copyAsset(filename: String) {
        val dest = File(filesDir, filename)
        assets.open(filename).use { inputStream ->
            dest.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Shell.cmd("chmod +x $dest").exec()
    }

    private fun copyNativeBinary(filename: String) {
        val binary = File(applicationInfo.nativeLibraryDir, "lib$filename.so")
        println("binary: $binary")
        val dest = File(filesDir, filename)
        println("dest: $dest")
        binary.inputStream().use { inputStream ->
            dest.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Shell.cmd("chmod +x $dest").exec()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val scale = ObjectAnimator.ofPropertyValuesHolder(
                splashScreenView.view,
                PropertyValuesHolder.ofFloat(
                    View.SCALE_X,
                    1f,
                    0f
                ),
                PropertyValuesHolder.ofFloat(
                    View.SCALE_Y,
                    1f,
                    0f
                )
            )
            scale.interpolator = AccelerateInterpolator()
            scale.duration = 250L
            scale.doOnEnd { splashScreenView.remove() }
            scale.start()
        }

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel?.isRefreshing == false) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

        setContent {
            val navController = rememberNavController()
            viewModel = viewModel {
                val application = checkNotNull(get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY))
                MainViewModel(application, FileSystemManager.getLocal(), navController)
            }
            val mainViewModel = viewModel!!
            KernelFlasherTheme {
                if (!mainViewModel.hasError) {
                    mainListener = MainListener {
                        mainViewModel.refresh(this)
                    }
                    val slotViewModelA = mainViewModel.slotA
                    val slotViewModelB = mainViewModel.slotB
                    val backupsViewModel = mainViewModel.backups
                    val updatesViewModel = mainViewModel.updates
                    val rebootViewModel = mainViewModel.reboot
                    BackHandler(enabled = mainViewModel.isRefreshing, onBack = {})
                    val slotContent: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit = { backStackEntry ->
                        val slotSuffix = backStackEntry.arguments?.getString("slotSuffix") ?: ""
                        val slotViewModel = if (slotSuffix == "_b") slotViewModelB else slotViewModelA
                        if (slotViewModel!!.wasFlashSuccess != null && listOf("slot{slotSuffix}", "slot").any { navController.currentDestination!!.route.equals(it) }) {
                            slotViewModel.clearFlash(this@MainActivity)
                        }
                        RefreshableScreen(mainViewModel, navController, swipeEnabled = true) {
                            SlotContent(slotViewModel, slotSuffix, navController)
                        }
                    }
                    val slotFlashContent: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit = { backStackEntry ->
                        val slotSuffix = backStackEntry.arguments?.getString("slotSuffix") ?: ""
                        val slotViewModel = if (slotSuffix == "_b") slotViewModelB else slotViewModelA
                        RefreshableScreen(mainViewModel, navController) {
                            SlotFlashContent(slotViewModel!!, slotSuffix, navController)
                        }
                    }
                    val slotBackupsContent: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit = { backStackEntry ->
                        val slotSuffix = backStackEntry.arguments?.getString("slotSuffix") ?: ""
                        val slotViewModel = if (slotSuffix == "_b") slotViewModelB else slotViewModelA
                        if (backStackEntry.arguments?.getString("backupId") != null) {
                            backupsViewModel.currentBackup = backStackEntry.arguments?.getString("backupId")
                        } else {
                            backupsViewModel.clearCurrent()
                        }
                        RefreshableScreen(mainViewModel, navController) {
                            SlotBackupsContent(slotViewModel!!, backupsViewModel, slotSuffix, navController)
                        }
                    }
                    val slotBackupFlashContent: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit = { backStackEntry ->
                        val slotSuffix = backStackEntry.arguments?.getString("slotSuffix") ?: ""
                        val slotViewModel = if (slotSuffix == "_b") slotViewModelB else slotViewModelA
                        backupsViewModel.currentBackup = backStackEntry.arguments?.getString("backupId")
                        if (backupsViewModel.backups.containsKey(backupsViewModel.currentBackup)) {
                            RefreshableScreen(mainViewModel, navController) {
                                SlotFlashContent(slotViewModel!!, slotSuffix, navController)
                            }
                        }
                    }
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            RefreshableScreen(mainViewModel, navController, swipeEnabled = true) {
                                MainContent(mainViewModel, navController)
                            }
                        }
                        if (mainViewModel.isAb) {
                            composable("slot{slotSuffix}", content = slotContent)
                            composable("slot{slotSuffix}/flash", content = slotFlashContent)
                            composable("slot{slotSuffix}/flash/ak3", content = slotFlashContent)
                            composable("slot{slotSuffix}/flash/image", content = slotFlashContent)
                            composable("slot{slotSuffix}/flash/image/flash", content = slotFlashContent)
                            composable("slot{slotSuffix}/backup", content = slotFlashContent)
                            composable("slot{slotSuffix}/backup/backup", content = slotFlashContent)
                            composable("slot{slotSuffix}/backups", content = slotBackupsContent)
                            composable("slot{slotSuffix}/backups/{backupId}", content = slotBackupsContent)
                            composable("slot{slotSuffix}/backups/{backupId}/restore", content = slotBackupsContent)
                            composable("slot{slotSuffix}/backups/{backupId}/restore/restore", content = slotBackupsContent)
                            composable("slot{slotSuffix}/backups/{backupId}/flash/ak3", content = slotBackupFlashContent)
                        } else {
                            composable("slot", content = slotContent)
                            composable("slot/flash", content = slotFlashContent)
                            composable("slot/flash/ak3", content = slotFlashContent)
                            composable("slot/flash/image", content = slotFlashContent)
                            composable("slot/flash/image/flash", content = slotFlashContent)
                            composable("slot/backup", content = slotFlashContent)
                            composable("slot/backup/backup", content = slotFlashContent)
                            composable("slot/backups", content = slotBackupsContent)
                            composable("slot/backups/{backupId}", content = slotBackupsContent)
                            composable("slot/backups/{backupId}/restore", content = slotBackupsContent)
                            composable("slot/backups/{backupId}/restore/restore", content = slotBackupsContent)
                            composable("slot/backups/{backupId}/flash/ak3", content = slotBackupFlashContent)
                        }
                        composable("backups") {
                            backupsViewModel.clearCurrent()
                            RefreshableScreen(mainViewModel, navController) {
                                BackupsContent(backupsViewModel, navController)
                            }
                        }
                        composable("backups/{backupId}") { backStackEntry ->
                            backupsViewModel.currentBackup = backStackEntry.arguments?.getString("backupId")
                            if (backupsViewModel.backups.containsKey(backupsViewModel.currentBackup)) {
                                RefreshableScreen(mainViewModel, navController) {
                                    BackupsContent(backupsViewModel, navController)
                                }
                            }
                        }
                        composable("updates") {
                            updatesViewModel.clearCurrent()
                            RefreshableScreen(mainViewModel, navController) {
                                UpdatesContent(updatesViewModel, navController)
                            }
                        }
                        composable("updates/add") {
                            RefreshableScreen(mainViewModel, navController) {
                                UpdatesAddContent(updatesViewModel, navController)
                            }
                        }
                        composable("updates/view/{updateId}") { backStackEntry ->
                            val updateId = backStackEntry.arguments?.getString("updateId")!!.toInt()
                            val currentUpdate = updatesViewModel.updates.firstOrNull { it.id == updateId }
                            updatesViewModel.currentUpdate = currentUpdate
                            if (updatesViewModel.currentUpdate != null) {
                                RefreshableScreen(mainViewModel, navController) {
                                    UpdatesViewContent(updatesViewModel, navController)
                                }
                            }
                        }
                        composable("updates/view/{updateId}/changelog") { backStackEntry ->
                            val updateId = backStackEntry.arguments?.getString("updateId")!!.toInt()
                            val currentUpdate = updatesViewModel.updates.firstOrNull { it.id == updateId }
                            updatesViewModel.currentUpdate = currentUpdate
                            if (updatesViewModel.currentUpdate != null) {
                                RefreshableScreen(mainViewModel, navController) {
                                    UpdatesChangelogContent(updatesViewModel, navController)
                                }
                            }
                        }
                        composable("reboot") {
                            RefreshableScreen(mainViewModel, navController) {
                                RebootContent(rebootViewModel, navController)
                            }
                        }
                        composable("error/{error}") { backStackEntry ->
                            val error = backStackEntry.arguments?.getString("error")
                            ErrorScreen(error!!)
                        }
                    }
                } else {
                    ErrorScreen(mainViewModel.error)
                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (this::mainListener.isInitialized) {
            if (!isAwaitingResult) {
                mainListener.resume()
            }
        }
    }
}