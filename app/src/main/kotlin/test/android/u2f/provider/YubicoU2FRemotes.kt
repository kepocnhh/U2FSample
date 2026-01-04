package test.android.u2f.provider

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import test.android.u2f.entity.StartRegistrationResponse
import java.net.URI
import kotlin.io.encoding.Base64

internal class YubicoU2FRemotes : U2FRemotes {
    private val client = OkHttpClient.Builder().build()
    private val uri = URI("https://demo.yubico.com/api/v1/simple/webauthn")

    override fun startRegistration(): StartRegistrationResponse {
        val request = Request.Builder()
            .url("$uri/register-begin")
            .post("{}".toRequestBody(contentType = "application/json".toMediaType()))
            .build()
        return client.newCall(request).execute().use { response ->
            when (val code = response.code) {
                200 -> {
                    val obj = JSONObject(response.body!!.string()).getJSONObject("data")
                    val challenge = obj.getJSONObject("publicKey")
                        .getJSONObject("challenge")
                        .getString("\$base64")
                        .let(Base64::decode)
                    StartRegistrationResponse(
                        challenge = challenge,
                    )
                }
                else -> error("Unknown code: $code")
            }
        }
    }
}
