package test.android.u2f.provider

import android.util.Log

internal object FinalLoggers : Loggers {
    override fun create(tag: String): Logger {
        return FinalLogger(tag = tag)
    }
}

private class FinalLogger(
    private val tag: String,
) : Logger {
    override fun debug(message: String) {
        Log.d(tag, message)
    }

    override fun warning(message: String) {
        Log.w(tag, message)
    }

    override fun info(message: String) {
        Log.i(tag, message)
    }
}
