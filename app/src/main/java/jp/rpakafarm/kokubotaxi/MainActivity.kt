package jp.rpakafarm.kokubotaxi

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import jp.rpakafarm.kokubotaxi.ui.theme.KokuboTaxiTheme
import jp.rpakafarm.kokubotaxi.screen.MainScreen

/**
 * メインアクティビティ
 * アプリのエントリポイント
 * @since 0.1.0
 * @author ChatGPT 4o, Ritsuki KOKUBO
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KokuboTaxiTheme {
                MainScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        intent.let { super.onNewIntent(it) }
        setContent {
            KokuboTaxiTheme {
                MainScreen()
            }
        }
    }
}