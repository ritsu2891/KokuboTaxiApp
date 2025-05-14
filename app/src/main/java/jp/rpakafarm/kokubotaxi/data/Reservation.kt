package jp.rpakafarm.kokubotaxi.data

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 乗務員個人予約
 * @since 0.1.0
 */
data class Reservation (
    /**
     * 予約日時
     * @since 0.1.0
     */
    val datetime: LocalDateTime,
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

// LocalDateTime用のカスタムアダプタ
class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun write(out: JsonWriter, value: LocalDateTime?) {
        out.value(value?.format(formatter))
    }

    override fun read(input: JsonReader): LocalDateTime? {
        return input.nextString()?.let { LocalDateTime.parse(it, formatter) }
    }
}

// Gsonインスタンスを取得するヘルパー
private fun getGson(): Gson {
    return Gson().newBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()
}

/**
 * 予約情報のリスト
 * @since 1.0.0
 */
fun loadReservations(context: Context): List<Reservation> {
    Log.d("kta", "Loading reservations")
    val prefs = context.getSharedPreferences("reservation_prefs", Context.MODE_PRIVATE)
    val json = prefs.getString("reservations", null)
    var reservations = if (json != null) getGson().fromJson(json, Array<Reservation>::class.java).toList() else emptyList()
    for (reservation in reservations) {
        Log.d("kta", "Reservation: ${reservation.datetime}")
    }
    return reservations
}

/**
 * 予約情報をローカルストレージに保存する
 * @since 1.0.0
 */
fun saveReservations(context: Context, reservations: List<Reservation>) {
    Log.d("kta", "Saving reservations: ${reservations.size}")
    for (reservation in reservations) {
        Log.d("kta", "Reservation: ${reservation.datetime}")
    }
    val prefs = context.getSharedPreferences("reservation_prefs", Context.MODE_PRIVATE)
    prefs.edit {
        val json = getGson().toJson(reservations)
        putString("reservations", json)
    }
}

/**
 * ピン留め中の予約情報をローカルストレージから読み込む
 * @since 0.1.0
 */
fun loadPinnedReservation(context: Context): Reservation? {
    val prefs = context.getSharedPreferences("reservation_prefs", Context.MODE_PRIVATE)
    val json = prefs.getString("selected_reservation", null)
    return if (json != null) getGson().fromJson(json, Reservation::class.java) else null
}

/**
 * ピン留め中の予約情報をローカルストレージに保存する
 * @since 0.1.0
 */
fun savePinnedReservation(context: Context, reservation: Reservation?) {
    val prefs = context.getSharedPreferences("reservation_prefs", Context.MODE_PRIVATE)
    prefs.edit {
        if (reservation != null) {
            val json = getGson().toJson(reservation)
            putString("selected_reservation", json)
        } else {
            remove("selected_reservation")
        }
    }
}
