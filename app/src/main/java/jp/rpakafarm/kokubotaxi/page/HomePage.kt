package jp.rpakafarm.kokubotaxi.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.rpakafarm.kokubotaxi.data.Reservation
import jp.rpakafarm.kokubotaxi.ui.AnalogClock
import jp.rpakafarm.kokubotaxi.ui.ReservationCard

/**
 * ホームページ
 * @param selectedReservation 選択された予約
 * @param showReservationText 予約配車のテキストを表示するかどうか
 * @param onShowReservationTextChange 予約配車のテキストの表示状態を変更するコールバック
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
@Composable
fun HomePage(
    selectedReservation: Reservation?,
    showReservationText: Boolean,
    onShowReservationTextChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 時計
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnalogClock()
        }

        // 予約配車表示
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(0.dp, 500.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showReservationText) {
                    Surface(
                        color = Color(255, 100, 0),
                        tonalElevation = 4.dp,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = "予約配車",
                            modifier = Modifier.padding(10.dp),
                            fontSize = 80.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // 選択中の乗務員個人予約
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedReservation?.let {
                Column(
                    modifier = Modifier.widthIn(0.dp, 500.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ReservationCard(
                        reservation = it,
                        isSelected = true,
                        onClick = {}
                    )
                }
            } ?: Text(text = "")

            // 予約配車の表示・非表示切り替えボタン
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { onShowReservationTextChange(false) }) {
                    Text("切", fontSize = 30.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onShowReservationTextChange(true) }) {
                    Text("入", fontSize = 30.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}