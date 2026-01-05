package test.android.u2f.provider

internal class Injection(
    val loggers: Loggers,
    val u2FRemotes: U2FRemotes,
    val u2FProvider: U2FProvider,
)
