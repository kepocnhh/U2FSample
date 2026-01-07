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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.coroutineScope
import co.nstant.`in`.cbor.CborDecoder
import co.nstant.`in`.cbor.model.Map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import sp.kx.bytes.hex
import kotlin.io.encoding.Base64

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
                                    val options = injection.u2FRemotes.startRegistration()
                                    logger.debug("options:challenge: ${Base64.encode(options.challenge)}")
                                    val credential = injection.u2FProvider.create(options = options)
                                    logger.debug("credential:id: ${credential.rawId.hex()}")
                                    logger.debug("credential:client: ${JSONObject(String(credential.response.clientDataJSON))}")
                                    CborDecoder.decode(credential.response.attestationObject).single().let {
                                        check(it is Map)
                                        it.keys.map { key -> key to it.get(key) }
                                    }.joinToString(separator = "\n") { (k, v) ->
                                        "$k: $v"
                                    }.also { message ->
                                        logger.debug("credential:attestation: $message")
                                    }
                                    injection.u2FRemotes.finishRegistration(credential = credential)
                                }.fold(
                                    onSuccess = {
                                        TODO("registration...")
                                    },
                                    onFailure = { error ->
                                        logger.warning("start registration error:")
                                        logger.info(error.message!!)
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
