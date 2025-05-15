package jp.rpakafarm.kokubotaxi.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import jp.rpakafarm.kokubotaxi.R
import android.app.AlertDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import jp.rpakafarm.kokubotaxi.data.exportReservationsToCsv
import jp.rpakafarm.kokubotaxi.data.importReservationsFromCsv
import jp.rpakafarm.kokubotaxi.data.Reservation

/**
 * 設定ページ
 * @since 1.0.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
@Composable
fun SettingPage(
    onReservationsChange: (List<Reservation>) -> Unit
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo?.versionName ?: "x.y.z"
    val versionCode = packageInfo?.longVersionCode ?: "n"

    val reservationCsvImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            AlertDialog.Builder(context).apply {
                setTitle("取込方法を選択")
                setItems(arrayOf("洗い替え", "追加")) { _, which ->
                    val replace = (which == 0)
                    onReservationsChange(importReservationsFromCsv(context, it, replace))
                }
                setNegativeButton("キャンセル", null)
            }.show()
        }
    }

    val reservationCsvOutputLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri ->
            if (uri != null) {
                exportReservationsToCsv(context, uri)
            }
        }
    )

    Column (
        modifier = Modifier.fillMaxHeight()
    ) {
        // アプリ情報
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(128.dp)
                    .padding(16.dp)
            )
            Text("KokuboTaxiApp", fontSize = 24.sp)
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top=10.dp)
            ) {
                // PackageInfoからバージョン情報を取得
                Text("ver $versionName build $versionCode", fontSize = 16.sp)
                Text("Copyright © 2025 rpaka-farm", fontSize = 16.sp)
            }
        }

        // 設定項目
        Column (
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            HorizontalDivider(thickness = 2.dp)
            /*
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = "Pinned",
                    )
                },
                headlineContent = { Text("お得意様マスタ") }
            )
            */
            Row (
                modifier = Modifier.padding(all = 10.dp)
            ) {
                OutlinedButton(onClick = {
                    AlertDialog.Builder(context).apply {
                        setTitle("予約CSV")
                        setItems(arrayOf("予約出力", "予約取込")) { _, which ->
                            when (which) {
                                0 -> reservationCsvOutputLauncher.launch("KokuboTaxi_予約一覧.csv")
                                1 -> reservationCsvImportLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "text/plain"))
                            }
                        }
                        setNegativeButton("キャンセル", null)
                    }.show()
                }) {
                    Text(text = "予約CSV")
                }
                /*
                Spacer(Modifier.width(10.dp))
                OutlinedButton (onClick = {}) { Text(text = "お得意様CSV") }
                */
            }
        }
    }
}

@Preview
@Composable
fun SettingPagePreview() {
    Column (
        Modifier
            .height(900.dp)
            .background(Color.White)
    ) {
        SettingPage({})
    }
}