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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
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
    reservations: List<Reservation>,
    onReservationsChange: (List<Reservation>) -> Unit,
    pinnedReservation: Reservation?,
    onReservationPinned: (Reservation?) -> Unit,
    filterSelectedIndex: Int,
    onfilterSelectedIndexChange: (Int) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    val selectedReservations = remember { mutableStateListOf<Reservation>() }

    val filterOptions = listOf("今日", "全て")

    var datetime by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }

    val openDatePicker = remember { mutableStateOf(false) }
    val openTimePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }

    val focusRequester1 = FocusRequester()
    val focusRequester2 = FocusRequester()
    val focusRequester3 = FocusRequester()
    val focusRequester4 = FocusRequester()
    val focusRequester5 = FocusRequester()

    Scaffold(
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = {
                        isEditMode = !isEditMode;
                        selectedReservations.clear();
                    }) {
                        Icon(
                            if (isEditMode) Icons.Filled.Done else Icons.Filled.Edit,
                            contentDescription = if (isEditMode) "編集モード終了" else "編集モード開始"
                        )
                    }
                    if (isEditMode) {
                        IconButton(
                            onClick = {
                                // 削除指定された予約の中にピン留め中の予約があれば解除する
                                if (pinnedReservation in selectedReservations) {
                                    onReservationPinned(null)
                                }
                                // 選択された予約を削除
                                onReservationsChange(reservations - selectedReservations)
                                selectedReservations.clear()
                                isEditMode = false
                            },
                            enabled = selectedReservations.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "削除実行")
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    SingleChoiceSegmentedButtonRow (
                        modifier = Modifier.width(180.dp),
                    ) {
                        filterOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = filterOptions.size
                                ),
                                onClick = { onfilterSelectedIndexChange(index) },
                                selected = index == filterSelectedIndex,
                                label = { Text(label) }
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))
                },
                floatingActionButton = {
                    if (!isEditMode) {
                        FloatingActionButton(
                            onClick = { showDialog = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "予約追加")
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 350.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(
                reservations
                    .filter { reservation ->
                        when (filterSelectedIndex) {
                            0 -> reservation.datetime.toLocalDate() == LocalDateTime.now().toLocalDate()
                            1 -> true
                            else -> false
                        }
                    }
                    .sortedBy { it.datetime }
            ) { reservation ->
                ReservationCard(
                    reservation = reservation,
                    isSelected = !isEditMode && reservation == pinnedReservation,
                    onClick = {
                        if (isEditMode) {
                            if (reservation in selectedReservations) {
                                selectedReservations.remove(reservation)
                            } else {
                                selectedReservations.add(reservation)
                            }
                        } else {
                            onReservationPinned(
                                if (pinnedReservation == reservation) null else reservation
                            )
                        }
                    },
                    showCheckbox = reservation in selectedReservations,
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
                            val newReservation = Reservation(
                                datetime = selectedDateTime ?: LocalDateTime.now(),
                                customerName = customerName,
                                pickupAddress = pickupAddress,
                                phoneNumber = phoneNumber,
                                destination = destination
                            )
                            onReservationsChange(reservations + newReservation)
                            // リセット
                            datetime = ""
                            customerName = ""
                            pickupAddress = ""
                            phoneNumber = ""
                            destination = ""
                            selectedDateTime = null
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
                    selectedDateTime = pickedDateTime
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    datetime = pickedDateTime.format(formatter)
                }
            )
        }
    }
}

@Preview
@Composable
fun ReservationListPagePreview() {
    val reservations = listOf(
        Reservation(
            datetime = LocalDateTime.now(),
            customerName = "山田太郎",
            pickupAddress = "東京都千代田区1-1-1",
            phoneNumber = "090-1234-5678",
            destination = "東京都新宿区2-2-2"
        ),
        Reservation(
            datetime = LocalDateTime.now().plusDays(1),
            customerName = "鈴木花子",
            pickupAddress = "東京都港区3-3-3",
            phoneNumber = "080-9876-5432",
            destination = "東京都渋谷区4-4-4"
        )
    )
    ReservationListPage(
        reservations = reservations,
        onReservationsChange = {},
        pinnedReservation = null,
        onReservationPinned = {},
        filterSelectedIndex = 1,
        onfilterSelectedIndexChange = {}
    )
}