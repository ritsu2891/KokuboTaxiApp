package jp.rpakafarm.kokubotaxi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.rpakafarm.kokubotaxi.ui.theme.KokuboTaxiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLDecoder
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.sin

data class Reservation(
    val datetime: String,
    val customerName: String,
    val pickupAddress: String,
    val phoneNumber: String,
    val destination: String
)

class MainActivity : ComponentActivity() {
    private var sharedText by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初回起動時のインテントから共有されたデータを取得
        sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)

        setContent {
            KokuboTaxiTheme {
                MainScreen(sharedText)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        intent.let { super.onNewIntent(it) }
        // 新しいインテントから共有されたデータを取得
        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        // UIを更新
        setContent {
            KokuboTaxiTheme {
                MainScreen(sharedText)
            }
        }
    }
}

@Composable
fun MainScreen(sharedText: String?) {
    val context = LocalContext.current
    val tabs = listOf("ホーム", "予約一覧")
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    // 初期値として保存された予約を読み込み
    var selectedReservation by remember { mutableStateOf<Reservation?>(loadSelectedReservation(context)) }

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
                                    1 -> Icons.Filled.List
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
                0 -> HomeScreen(sharedText, selectedReservation)
                1 -> ReservationListScreen(
                    selectedReservation = selectedReservation,
                    onReservationSelected = { selectedReservation = it }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(sharedText: String?, selectedReservation: Reservation?) {
    val coroutineScope = rememberCoroutineScope()
    var displayText by remember { mutableStateOf("読み込み中...") }
    var showReservationText by remember { mutableStateOf(true) }

    LaunchedEffect(sharedText) {
        if (sharedText != null) {
            coroutineScope.launch {
                val resolvedUrl = resolveShortUrl(sharedText)
                println(sharedText)
                println(resolvedUrl)
                val (address, latLng) = extractLocationInfo(resolvedUrl)
                displayText = "住所: $address"
            }
        } else {
            displayText = "共有データがありません"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnalogClock()
        }

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

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedReservation?.let {
                Column(
                    modifier = Modifier.widthIn(0.dp, 500.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4B5))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = it.datetime)
                            Text(text = it.customerName)
                            Text(text = it.pickupAddress)
                            Text(text = it.phoneNumber)
                            Text(text = it.destination)
                        }
                    }
                }
            } ?: Text(text = "")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { showReservationText = false }) {
                    Text("切", fontSize = 30.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { showReservationText = true }) {
                    Text("入", fontSize = 30.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            /*
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge
            )
            */
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    KokuboTaxiTheme {
        MainScreen(null)
    }
}


suspend fun resolveShortUrl(shortUrl: String): String? {
    val client = OkHttpClient.Builder()
        .followRedirects(false)
        .build()

    val request = Request.Builder()
        .url(shortUrl)
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            response.header("Location")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun extractLocationInfo(resolvedUrl: String?): Pair<String, Pair<Double, Double>?> {
    if (resolvedUrl.isNullOrEmpty()) return "取得失敗" to null

    val placePattern = """/place/([^/@]+)""".toRegex()
    val locationPattern = """@([-.\d]+),([-.\d]+)""".toRegex()

    val placeMatch = placePattern.find(resolvedUrl)
    val locationMatch = locationPattern.find(resolvedUrl)

    val address = placeMatch?.groupValues?.get(1)?.let { URLDecoder.decode(it, "UTF-8") } ?: "住所取得失敗"
    val latLng = null

    return Pair(address, latLng)
}
// Material3準拠のTimePickerDialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("キャンセル")
            }
        },
        title = { Text(text = "時刻を選択") },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@Composable
fun AnalogClock() {
    val currentTime = remember { mutableStateOf(LocalTime.now()) }

    // 毎秒更新
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = LocalTime.now()
            delay(1000L)
        }
    }

    Canvas(modifier = Modifier.size(200.dp)) {
        val clockRadius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // 時計の外枠
        drawCircle(
            color = Color.Black,
            radius = clockRadius,
            style = Stroke(width = 4.dp.toPx())
        )

        // 時間の目盛りと数字
        for (i in 1..12) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val lineStart = Offset(
                x = center.x + (clockRadius - 10.dp.toPx()) * cos(angle).toFloat(),
                y = center.y + (clockRadius - 10.dp.toPx()) * sin(angle).toFloat()
            )
            val lineEnd = Offset(
                x = center.x + clockRadius * cos(angle).toFloat(),
                y = center.y + clockRadius * sin(angle).toFloat()
            )
            drawLine(
                color = Color.Black,
                start = lineStart,
                end = lineEnd,
                strokeWidth = 2.dp.toPx()
            )

            val textOffset = Offset(
                x = center.x + (clockRadius - 21.dp.toPx()) * cos(angle).toFloat(),
                y = center.y + (clockRadius - 21.dp.toPx()) * sin(angle).toFloat()
            )
            drawContext.canvas.nativeCanvas.drawText(
                i.toString(),
                textOffset.x,
                textOffset.y + 10.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 24.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        // 時針
        val hourAngle = Math.toRadians((currentTime.value.hour % 12 * 30 - 90).toDouble())
        drawLine(
            color = Color.Black,
            start = center,
            end = Offset(
                x = center.x + (clockRadius - 50.dp.toPx()) * cos(hourAngle).toFloat(),
                y = center.y + (clockRadius - 50.dp.toPx()) * sin(hourAngle).toFloat()
            ),
            strokeWidth = 8.dp.toPx()
        )

        // 分針
        val minuteAngle = Math.toRadians((currentTime.value.minute * 6 - 90).toDouble())
        drawLine(
            color = Color.Gray,
            start = center,
            end = Offset(
                x = center.x + (clockRadius - 30.dp.toPx()) * cos(minuteAngle).toFloat(),
                y = center.y + (clockRadius - 30.dp.toPx()) * sin(minuteAngle).toFloat()
            ),
            strokeWidth = 6.dp.toPx()
        )

        // 秒針
        val secondAngle = Math.toRadians((currentTime.value.second * 6 - 90).toDouble())
        drawLine(
            color = Color.Red,
            start = center,
            end = Offset(
                x = center.x + (clockRadius - 20.dp.toPx()) * cos(secondAngle).toFloat(),
                y = center.y + (clockRadius - 20.dp.toPx()) * sin(secondAngle).toFloat()
            ),
            strokeWidth = 4.dp.toPx()
        )
    }
}


// 以下のヘルパー関数を追加
fun loadSelectedReservation(context: Context): Reservation? {
    val prefs = context.getSharedPreferences("reservation_prefs", Context.MODE_PRIVATE)
    val json = prefs.getString("selected_reservation", null)
    return if (json != null) Gson().fromJson(json, Reservation::class.java) else null
}

fun saveSelectedReservation(context: Context, reservation: Reservation?) {
    val prefs = context.getSharedPreferences("reservation_prefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    if (reservation != null) {
        val json = Gson().toJson(reservation)
        editor.putString("selected_reservation", json)
    } else {
        editor.remove("selected_reservation")
    }
    editor.apply()
}
