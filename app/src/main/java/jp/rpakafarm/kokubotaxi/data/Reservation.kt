package jp.rpakafarm.kokubotaxi.data

import com.google.gson.Gson
import android.content.Context
import androidx.core.content.edit

/**
 * 乗務員個人予約
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
data class Reservation (
    /**
     * 予約日時
     * @since 0.1.0
     */
    val datetime: String,
    /**
     * 予約者名
     * @since 0.1.0
     */
    val customerName: String,
    /**
     * お迎え先住所
     * @since 0.1.0
     */
    val pickupAddress: String,
    /**
     * 電話番号
     * @since 0.1.0
     */
    val phoneNumber: String,
    /**
     * 目的地
     * @since 0.1.0
     */
    val destination: String
)

/**
 * 予約情報をローカルストレージから読み込む
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
fun loadSelectedReservation(context: Context): Reservation? {
    val prefs = context.getSharedPreferences("reservation_prefs", Context.MODE_PRIVATE)
    val json = prefs.getString("selected_reservation", null)
    return if (json != null) Gson().fromJson(json, Reservation::class.java) else null
}

/**
 * 予約情報をローカルストレージに保存する
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
fun saveSelectedReservation(context: Context, reservation: Reservation?) {
    val prefs = context.getSharedPreferences("reservation_prefs", Context.MODE_PRIVATE)
    prefs.edit {
        if (reservation != null) {
            val json = Gson().toJson(reservation)
            putString("selected_reservation", json)
        } else {
            remove("selected_reservation")
        }
    }
}