package test.android.u2f

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView

internal class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context: Context = this
        val view = ComposeView(context = context)
        setContentView(view)
        view.setContent {
            MainScreen()
        }
    }
}
