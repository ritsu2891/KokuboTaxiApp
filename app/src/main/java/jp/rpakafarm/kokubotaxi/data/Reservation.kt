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
import android.net.Uri
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

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

/**
 * LocalDateTimeのGson用TypeAdapter
 * @since 1.0.0
 * @author ChatGPT 4o
 */
class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun write(out: JsonWriter, value: LocalDateTime?) {
        out.value(value?.format(formatter))
    }

    override fun read(input: JsonReader): LocalDateTime? {
        return input.nextString()?.let { LocalDateTime.parse(it, formatter) }
    }
}

/**
 * Gsonのインスタンスを取得する
 * @since 1.0.0
 * @author ChatGPT 4o
 */
private fun getGson(): Gson {
    return Gson().newBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()
}

/**
 * 予約情報のリストをローカルストレージから読み込む
 * @since 1.0.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
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
 * @author ChatGPT 4o, Ritsuki KOKUBO
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
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
fun loadPinnedReservation(context: Context): Reservation? {
    val prefs = context.getSharedPreferences("reservation_prefs", Context.MODE_PRIVATE)
    val json = prefs.getString("selected_reservation", null)
    return if (json != null) getGson().fromJson(json, Reservation::class.java) else null
}

/**
 * ピン留め中の予約情報をローカルストレージに保存する
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
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

/**
 * CSVでの予約日時形式
 */
private val csvDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d H:mm")

/**
 * CSVファイルに予約情報をエクスポートする
 * @param context コンテキスト
 * @param uri 書き込み先のURI
 * @since 1.0.0
 * @author ChatGPT 4.1, Ritsuki KOKUBO
 */
fun exportReservationsToCsv(context: Context, uri: Uri) {
    val reservations = loadReservations(context)
    if (reservations.isEmpty()) {
        Toast.makeText(context, "予約情報がありません", Toast.LENGTH_SHORT).show()
        return
    }

    context.contentResolver.openOutputStream(uri)?.writer(Charset.forName("SJIS"))?.use { writer ->
        writer.appendLine("予約日時,予約者名,お迎え先住所,電話番号,目的地")
        reservations.forEach { reservation ->
            writer.appendLine(
                "${reservation.datetime.format(csvDateTimeFormatter)}," +
                        "${reservation.customerName}," +
                        "${reservation.pickupAddress}," +
                        "'${reservation.phoneNumber}," +
                        "${reservation.destination}"
            )
        }
    }

    Toast.makeText(context, "CSVを出力しました", Toast.LENGTH_LONG).show()
}

/**
 * CSVファイルから予約情報をインポートする
 * @param context コンテキスト
 * @param uri 読み込み元のURI
 * @param replace 既存の予約情報を置き換えるかどうか
 * @return インポートした予約情報のリスト
 * @since 1.0.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
fun importReservationsFromCsv(context: Context, uri: Uri, replace: Boolean): List<Reservation> {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return loadReservations(context)
    val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("SJIS")))
    val reservations = mutableListOf<Reservation>()

    reader.useLines { lines ->
        lines.drop(1).forEach { line -> // ヘッダーをスキップ
            Log.d("kta", "Importing line: $line")

            val columns = line.split(",")
            if (columns.size < 5) return@forEach
            try {
                val datetime = LocalDateTime.parse(columns[0], csvDateTimeFormatter)
                val customerName = columns[1]
                if (customerName.isBlank()) return@forEach
                reservations.add(
                    Reservation(
                        datetime = datetime,
                        customerName = customerName,
                        pickupAddress = columns[2],
                        phoneNumber = columns[3],
                        destination = columns[4]
                    )
                )
            } catch (e: Exception) {
                // 無効な行は無視
            }
        }
    }

    if (replace) {
        saveReservations(context, reservations)
    } else {
        val existingReservations = loadReservations(context)
        saveReservations(context, existingReservations + reservations)
    }

    Toast.makeText(context, "CSVを取り込みました", Toast.LENGTH_SHORT).show()

    return loadReservations(context)
}