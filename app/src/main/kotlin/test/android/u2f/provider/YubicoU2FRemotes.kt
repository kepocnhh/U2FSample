package test.android.u2f.provider

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import test.android.u2f.entity.AttestationConveyancePreference
import test.android.u2f.entity.PublicKeyCredentialCreationOptions
import test.android.u2f.entity.PublicKeyCredentialRpEntity
import test.android.u2f.entity.PublicKeyCredentialUserEntity
import java.net.URI
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.milliseconds

internal class YubicoU2FRemotes : U2FRemotes {
    private val client = OkHttpClient.Builder().build()
    private val uri = URI("https://demo.yubico.com/api/v1/simple/webauthn")

    override fun startRegistration(): PublicKeyCredentialCreationOptions {
        val contentType = "application/json".toMediaType()
        val request = Request.Builder()
            .url("$uri/register-begin")
            .post("{}".toRequestBody(contentType = contentType))
            .build()
        return client.newCall(request).execute().use { response ->
            when (val code = response.code) {
                200 -> {
                    val obj = JSONObject(response.body!!.string()).getJSONObject("data")
                    val pk = obj.getJSONObject("publicKey")
                    val rp = pk.getJSONObject("rp")
                    val user = pk.getJSONObject("user")
                    PublicKeyCredentialCreationOptions(
                        rp = PublicKeyCredentialRpEntity(
                            id = rp.getString("id"),
                            name = rp.getString("name"),
                        ),
                        user = PublicKeyCredentialUserEntity(
                            id = user
                                .getJSONObject("id")
                                .getString("\$base64")
                                .let(Base64::decode),
                            name = user.getString("name"),
                            displayName = user.getString("displayName"),
                        ),
                        challenge = pk
                            .getJSONObject("challenge")
                            .getString("\$base64")
                            .let(Base64::decode),
                        timeout = pk.getLong("timeout").milliseconds,
                        attestation = when (pk.getString("attestation")) {
                            "direct" -> AttestationConveyancePreference.Direct
                            else -> TODO()
                        }
                    )
                }
                else -> error("Unknown code: $code")
            }
        }
    }
}
