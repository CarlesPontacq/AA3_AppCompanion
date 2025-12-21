package com.example.appcompanion

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics

class App : Application() {
    override fun onCreate(){
        super.onCreate()

        AnalyticsManager.init(this)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        FirebaseCrashlytics.getInstance().setCustomKey("app_layer", "pokemon_api_app")
    }

}