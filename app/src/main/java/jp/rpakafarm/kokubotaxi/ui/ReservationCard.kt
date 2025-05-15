package jp.rpakafarm.kokubotaxi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.rpakafarm.kokubotaxi.data.Reservation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.unit.sp

/**
 * 乗務員個人予約カード
 * @param reservation 予約情報
 * @param isSelected 選択状態
 * @param onClick クリック時のコールバック
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
@Composable
fun ReservationCard(
    reservation: Reservation,
    isSelected: Boolean = false,
    onClick: (Reservation) -> Unit,
    showCheckbox: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(reservation) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                val dayFormatter = DateTimeFormatter.ofPattern("M/d(E)")
                val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.US)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = "${reservation.datetime.format(dayFormatter)}"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = "${reservation.datetime.format(timeFormatter)}",
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = reservation.customerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = reservation.phoneNumber,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(text = reservation.pickupAddress)
                Text(text = reservation.destination)
            }
            if (showCheckbox) {
                Checkbox(
                    checked = true,
                    onCheckedChange = null, // チェックボックス自体はクリック不可
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = "Pinned",
                    tint = Color(0xFFFFA500),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(30.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewReservationCard() {
    ReservationCard(
        reservation = Reservation(
            datetime = LocalDateTime.of(2025, 5, 1, 15, 30),
            customerName = "山田 太郎",
            pickupAddress = "東京都新宿区西新宿1-1-1",
            phoneNumber = "090-1234-5678",
            destination = "東京都渋谷区渋谷1-1-1"
        ),
        isSelected = true, // プレビューでピン留めアイコンを表示
        onClick = {},
        showCheckbox = true // プレビューでチェックボックスを表示
    )
}

