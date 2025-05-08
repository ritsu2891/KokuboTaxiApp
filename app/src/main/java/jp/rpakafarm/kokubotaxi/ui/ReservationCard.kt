package jp.rpakafarm.kokubotaxi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jp.rpakafarm.kokubotaxi.data.Reservation

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
    onClick: (Reservation) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(reservation) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFE4B5) else Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = reservation.datetime)
            Text(text = reservation.customerName)
            Text(text = reservation.pickupAddress)
            Text(text = reservation.phoneNumber)
            Text(text = reservation.destination)
        }
    }
}