package test.android.u2f

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun MainScreen() {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val injection = remember { App.injection }
    val logger = remember { injection.loggers.create("[Main]") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val loading = remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        ) {
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        lifecycle.coroutineScope.launch {
                            loading.value = true
                            withContext(Dispatchers.Default) {
                                runCatching {
                                    injection.u2FRemotes.startRegistration()
                                }.fold(
                                    onSuccess = { response ->
                                        TODO("start registration: $response")
                                    },
                                    onFailure = { error ->
                                        logger.warning("start registration error: $error")
                                    }
                                )
                            }
                            loading.value = false
                        }
                    }
                    .wrapContentSize(),
                text = "registration",
            )
        }
        if (loading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.8f))
                    .clickable(
                        interactionSource = null,
                        indication = null,
                    ) { /*noop*/ },
            ) {
                BasicText(
                    modifier = Modifier.align(Alignment.Center),
                    text = "loading...",
                )
            }
        }
    }
}
