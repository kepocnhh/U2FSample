package test.android.u2f

import android.app.Application
import test.android.u2f.provider.FinalLoggers
import test.android.u2f.provider.Injection
import test.android.u2f.provider.Loggers
import test.android.u2f.provider.U2FRemotes
import test.android.u2f.provider.YubicoU2FRemotes

internal class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val loggers: Loggers = FinalLoggers
        val u2FRemotes: U2FRemotes = YubicoU2FRemotes()
        _injection = Injection(
            loggers = loggers,
            u2FRemotes = u2FRemotes,
        )
    }

    companion object {
        private var _injection: Injection? = null
        val injection: Injection get() = checkNotNull(_injection) { "No injection!" }
    }
}
