package test.android.u2f.provider

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import test.android.u2f.entity.AttestationConveyancePreference
import test.android.u2f.entity.PublicKeyCredential
import test.android.u2f.entity.PublicKeyCredentialCreationOptions
import test.android.u2f.entity.PublicKeyCredentialRpEntity
import test.android.u2f.entity.PublicKeyCredentialUserEntity
import test.android.u2f.entity.StartRegistrationResponse
import java.net.URI
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.milliseconds

internal class YubicoU2FRemotes : U2FRemotes {
    private val client = OkHttpClient.Builder().build()
    private val uri = URI("https://demo.yubico.com/api/v1/simple/webauthn")

    override fun startRegistration(): StartRegistrationResponse {
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
                    val options = PublicKeyCredentialCreationOptions(
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
                    StartRegistrationResponse(
                        options = options,
                        requestId = UUID.fromString(obj.getString("requestId")),
                    )
                }
                else -> error("Unknown code: $code")
            }
        }
    }

    override fun finishRegistration(
        credential: PublicKeyCredential,
        user: PublicKeyCredentialUserEntity,
        requestId: UUID,
    ) {
        val contentType = "application/json".toMediaType()
        val attestation = JSONObject()
            .put("attestationObject", JSONObject().put("\$base64", Base64.encode(credential.response.attestationObject)))
            .put("clientDataJSON", JSONObject().put("\$base64", Base64.encode(credential.response.clientDataJSON)))
        val request = Request.Builder()
            .url("$uri/register-finish")
            .post(
                body = JSONObject()
                    .put("attestation", attestation)
                    .put("displayName", user.displayName)
                    .put("requestId", requestId.toString())
                    .put("username", user.name)
                    .toString()
                    .toRequestBody(contentType = contentType),
            )
            .build()
        return client.newCall(request).execute().use { response ->
            when (val code = response.code) {
                else -> {
                    val body = runCatching {
                        JSONObject(response.body!!.string()).toString(4)
                    }.getOrNull()
                    error(body ?: "code: $code")
                }
            }
        }
    }
}
