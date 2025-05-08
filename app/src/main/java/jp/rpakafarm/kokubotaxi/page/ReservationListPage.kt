package jp.rpakafarm.kokubotaxi.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import jp.rpakafarm.kokubotaxi.data.Reservation
import jp.rpakafarm.kokubotaxi.ui.ReservationCard
import jp.rpakafarm.kokubotaxi.ui.dialog.TimePickerDialog
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * 予約一覧ページ
 * @param selectedReservation 選択された予約
 * @param onReservationSelected 予約が選択されたときのコールバック
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationListPage(
    selectedReservation: Reservation?,
    onReservationSelected: (Reservation?) -> Unit
) {
    var reservations by remember {
        mutableStateOf(
            listOf(
                Reservation(
                    datetime = "2023-10-01 10:00",
                    customerName = "山田 太郎",
                    pickupAddress = "東京都新宿区1-1-1",
                    phoneNumber = "090-1234-5678",
                    destination = "東京駅"
                ),
                Reservation(
                    datetime = "2023-10-02 14:30",
                    customerName = "佐藤 花子",
                    pickupAddress = "東京都渋谷区2-2-2",
                    phoneNumber = "080-9876-5432",
                    destination = "羽田空港"
                ),
                Reservation(
                    datetime = "2023-10-03 09:00",
                    customerName = "鈴木 一郎",
                    pickupAddress = "東京都港区3-3-3",
                    phoneNumber = "070-1111-2222",
                    destination = "六本木ヒルズ"
                ),
                Reservation(
                    datetime = "2023-10-04 16:45",
                    customerName = "高橋 美咲",
                    pickupAddress = "東京都品川区4-4-4",
                    phoneNumber = "090-3333-4444",
                    destination = "品川駅"
                ),
                Reservation(
                    datetime = "2023-10-05 12:15",
                    customerName = "田中 健",
                    pickupAddress = "東京都世田谷区5-5-5",
                    phoneNumber = "080-5555-6666",
                    destination = "渋谷駅"
                )
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }

    var datetime by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }

    val openDatePicker = remember { mutableStateOf(false) }
    val openTimePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val focusRequester1 = FocusRequester()
    val focusRequester2 = FocusRequester()
    val focusRequester3 = FocusRequester()
    val focusRequester4 = FocusRequester()
    val focusRequester5 = FocusRequester()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "予約追加")
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 350.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(reservations) { reservation ->
                ReservationCard(
                    reservation = reservation,
                    isSelected = selectedReservation == reservation,
                    onClick = {
                        val newSelection = if (selectedReservation == reservation) null else reservation
                        onReservationSelected(newSelection)
                    }
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "新規予約") },
                text = {
                    Column(
                        modifier = Modifier
                            .height(200.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = datetime,
                            onValueChange = { /* 直接編集は禁止 */ },
                            label = { Text("予約日時") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester1),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { openDatePicker.value = true }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select date"
                                    )
                                }
                            },
                            maxLines = 1
                        )
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("お客様氏名") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester2),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusRequester3.requestFocus() })
                        )
                        OutlinedTextField(
                            value = pickupAddress,
                            onValueChange = { pickupAddress = it },
                            label = { Text("お迎え先住所") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester3),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusRequester4.requestFocus() })
                        )
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("登録電話番号") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester4),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusRequester5.requestFocus() })
                        )
                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            label = { Text("行先") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester5),
                            maxLines = 1
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            reservations = reservations + Reservation(
                                datetime = datetime,
                                customerName = customerName,
                                pickupAddress = pickupAddress,
                                phoneNumber = phoneNumber,
                                destination = destination
                            )
                            // リセット
                            datetime = ""
                            customerName = ""
                            pickupAddress = ""
                            phoneNumber = ""
                            destination = ""
                            showDialog = false
                        }
                    ) {
                        Text("登録")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("キャンセル")
                    }
                }
            )
        }

        if (openDatePicker.value) {
            DatePickerDialog(
                onDismissRequest = { openDatePicker.value = false },
                confirmButton = {
                    Button(onClick = {
                        openDatePicker.value = false
                        openTimePicker.value = true
                    }) {
                        Text("次へ")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { openDatePicker.value = false }) {
                        Text("キャンセル")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (openTimePicker.value) {
            TimePickerDialog(
                onDismissRequest = { openTimePicker.value = false },
                onConfirm = { hour, minute ->
                    openTimePicker.value = false
                    val pickedDateTime = LocalDateTime.ofEpochSecond(
                        (datePickerState.selectedDateMillis ?: System.currentTimeMillis()) / 1000,
                        0,
                        ZoneOffset.UTC
                    ).withHour(hour).withMinute(minute)
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    datetime = pickedDateTime.format(formatter)
                }
            )
        }
    }
}
