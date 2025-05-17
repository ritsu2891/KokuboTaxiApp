package jp.rpakafarm.kokubotaxi.screen

import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import jp.rpakafarm.kokubotaxi.page.ReservationListPage
import jp.rpakafarm.kokubotaxi.data.loadPinnedReservation
import jp.rpakafarm.kokubotaxi.data.savePinnedReservation
import jp.rpakafarm.kokubotaxi.page.HomePage
import jp.rpakafarm.kokubotaxi.data.loadReservations
import jp.rpakafarm.kokubotaxi.data.saveReservations
import jp.rpakafarm.kokubotaxi.page.SettingPage

/**
 * メイン画面
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val tabs = listOf("ホーム", "予約一覧", "設定")
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var showReservationText by rememberSaveable { mutableStateOf(false) }
    var pinnedReservation by remember { mutableStateOf(loadPinnedReservation(context)) }
    var reservations by remember { mutableStateOf(loadReservations(context)) }
    var filterSelectedIndex by remember { mutableIntStateOf(0) }

    // 状態が変化するたびに保存
    LaunchedEffect(pinnedReservation) {
        savePinnedReservation(context, pinnedReservation)
    }
    LaunchedEffect(reservations) {
        saveReservations(context, reservations)
    }

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(66.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Filled.Home
                                    1 -> Icons.AutoMirrored.Filled.List
                                    2 -> Icons.Filled.Settings
                                    else -> Icons.Filled.Home
                                },
                                contentDescription = null
                            )
                        },
                        label = { Text(title) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> HomePage(
                    reservations = reservations,
                    onReservationsChange = { reservations = it },
                    pinnedReservation = pinnedReservation,
                    onReservationPinned = { pinnedReservation = it },
                    pinnedReservation,
                    showReservationText,
                    onShowReservationTextChange = { showReservationText = it }
                )
                1 -> ReservationListPage(
                    reservations = reservations,
                    onReservationsChange = { reservations = it },
                    pinnedReservation = pinnedReservation,
                    onReservationPinned = { pinnedReservation = it },
                    filterSelectedIndex = filterSelectedIndex,
                    onfilterSelectedIndexChange = { filterSelectedIndex = it }
                )
                2 -> SettingPage(
                    onReservationsChange = { reservations = it },
                )
            }
        }
    }
}
