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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import jp.rpakafarm.kokubotaxi.page.ReservationListPage
import jp.rpakafarm.kokubotaxi.data.loadSelectedReservation
import jp.rpakafarm.kokubotaxi.data.saveSelectedReservation
import jp.rpakafarm.kokubotaxi.page.HomePage

/**
 * メイン画面
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val tabs = listOf("ホーム", "予約一覧")
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var showReservationText by rememberSaveable { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf(loadSelectedReservation(context)) }

    // 状態が変化するたびに保存
    LaunchedEffect(selectedReservation) {
        saveSelectedReservation(context, selectedReservation)
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
                    selectedReservation,
                    showReservationText,
                    onShowReservationTextChange = { showReservationText = it }
                )
                1 -> ReservationListPage(
                    selectedReservation = selectedReservation,
                    onReservationSelected = { selectedReservation = it }
                )
            }
        }
    }
}