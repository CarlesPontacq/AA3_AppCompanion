package com.example.appcompanion

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics

//Script that is loaded at the beginning of the app execution,
// it is used to load the crashlytics and look out for all the activities
class App : Application() {
    override fun onCreate(){
        super.onCreate()

        AnalyticsManager.init(this)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        FirebaseCrashlytics.getInstance().setCustomKey("app_layer", "pokemon_api_app")
    }

}