package com.gmf.stockopname.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gmf.stockopname.data.AppViewModel
import com.gmf.stockopname.data.OpnameResult
import com.gmf.stockopname.ui.screens.*

// Fixed, argument-free routes. The *actual* selection (store id, location
// name — some of which contain "/", "#", spaces) lives in AppViewModel, not
// in the nav path, so we never have to URL-encode arbitrary DB strings.
private object Routes {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val PILIH_STORE = "pilihStore"
    const val PILIH_LOCATION = "pilihLocation"
    const val LOCATION_DETAIL = "locationDetail"
    const val SCAN = "scan"
    const val DETAIL = "detail"
    const val COMPLETE = "complete"
    const val REPORT = "report"
    const val RIWAYAT = "riwayat"
    const val MASTER_DATA = "masterData"
    const val MASTER_DATA_DETAIL = "masterDataDetail"
    const val ADD_TOOL = "addTool"
}

@Composable
fun AppNavGraph(vm: AppViewModel) {
    val nav: NavHostController = rememberNavController()
    val startDestination = if (vm.loggedIn) Routes.MENU else Routes.LOGIN

    NavHost(navController = nav, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(onLoggedIn = { name ->
                vm.login(name)
                nav.navigate(Routes.MENU) { popUpTo(Routes.LOGIN) { inclusive = true } }
            })
        }

        composable(Routes.MENU) {
            MenuScreen(
                personnelName = vm.personnelName,
                onStockOp6 = { nav.navigate(Routes.PILIH_STORE) },
                onMasterData = { nav.navigate(Routes.MASTER_DATA) },
                onLogout = {
                    vm.logout()
                    nav.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.PILIH_STORE) {
            PilihStoreScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onPickStore = { storeId ->
                    vm.selectedStore = storeId
                    vm.locationSearch = ""
                    nav.navigate(Routes.PILIH_LOCATION)
                },
                onRiwayat = { nav.navigate(Routes.RIWAYAT) }
            )
        }

        composable(Routes.PILIH_LOCATION) {
            val storeId = vm.selectedStore ?: return@composable
            PilihLocationScreen(
                vm = vm,
                storeId = storeId,
                onBack = { nav.popBackStack() },
                onPickLocation = { loc ->
                    vm.selectedLocation = loc
                    nav.navigate(Routes.LOCATION_DETAIL)
                }
            )
        }

        composable(Routes.LOCATION_DETAIL) {
            val storeId = vm.selectedStore ?: return@composable
            val location = vm.selectedLocation ?: return@composable
            LocationDetailScreen(
                vm = vm,
                storeId = storeId,
                location = location,
                onBack = { nav.popBackStack() },
                onStartOrResume = {
                    val idx = vm.firstPendingIndex()
                    vm.scanCursor = if (idx == -1) 0 else idx
                    nav.navigate(Routes.SCAN)
                },
                onFinished = { nav.navigate(Routes.COMPLETE) }
            )
        }

        composable(Routes.SCAN) {
            val storeId = vm.selectedStore ?: return@composable
            val location = vm.selectedLocation ?: return@composable
            ScanScreen(
                vm = vm,
                storeId = storeId,
                location = location,
                onBack = { nav.popBackStack() },
                onBarcodeDetected = { nav.navigate(Routes.DETAIL) },
                onManualConfirm = { nav.navigate(Routes.DETAIL) }
            )
        }

        composable(Routes.DETAIL) {
            val tools = vm.currentTools()
            val tool = tools.getOrNull(vm.scanCursor) ?: return@composable
            DetailToolScreen(
                tool = tool,
                onBack = { nav.popBackStack() },
                onResult = { result: OpnameResult ->
                    vm.markCurrentTool(result)
                    val next = vm.firstPendingIndex()
                    if (next == -1) {
                        nav.navigate(Routes.COMPLETE) { popUpTo(Routes.LOCATION_DETAIL) }
                    } else {
                        vm.scanCursor = next
                        nav.navigate(Routes.SCAN) { popUpTo(Routes.LOCATION_DETAIL) }
                    }
                }
            )
        }

        composable(Routes.COMPLETE) {
            val storeId = vm.selectedStore ?: return@composable
            val location = vm.selectedLocation ?: return@composable
            CompleteScreen(
                vm = vm,
                storeId = storeId,
                location = location,
                onBack = { nav.popBackStack() },
                onGenerateReport = { nav.navigate(Routes.REPORT) },
                onHome = { nav.navigate(Routes.MENU) { popUpTo(Routes.MENU) { inclusive = true } } }
            )
        }

        composable(Routes.REPORT) {
            val storeId = vm.selectedStore ?: return@composable
            val location = vm.selectedLocation ?: return@composable
            ReportScreen(
                vm = vm,
                storeId = storeId,
                location = location,
                onBack = { nav.popBackStack() },
                onHome = { nav.navigate(Routes.MENU) { popUpTo(Routes.MENU) { inclusive = true } } }
            )
        }

        composable(Routes.RIWAYAT) {
            RiwayatScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onOpen = { storeId, location ->
                    vm.selectedStore = storeId
                    vm.selectedLocation = location
                    nav.navigate(Routes.LOCATION_DETAIL)
                }
            )
        }

        composable(Routes.MASTER_DATA) {
            MasterDataScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onOpenTool = { tool ->
                    vm.mdSelectedTool = tool
                    nav.navigate(Routes.MASTER_DATA_DETAIL)
                },
                onAddTool = { nav.navigate(Routes.ADD_TOOL) }
            )
        }

        composable(Routes.MASTER_DATA_DETAIL) {
            val tool = vm.mdSelectedTool ?: return@composable
            MasterDataDetailScreen(
                tool = tool,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.ADD_TOOL) {
            AddToolScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onSaved = { nav.popBackStack() }
            )
        }
    }
}
