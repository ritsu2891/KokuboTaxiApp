package jp.rpakafarm.kokubotaxi.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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

/**
 * 設定ページ
 * @since 1.0.0
 */
@Composable
fun SettingPage() {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo?.versionName ?: "x.y.z"
    val versionCode = packageInfo?.longVersionCode ?: "n"

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
                OutlinedButton (onClick = {}) { Text(text = "予約CSV") }
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
        SettingPage()
    }
}
