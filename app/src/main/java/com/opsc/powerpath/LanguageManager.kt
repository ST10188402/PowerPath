package com.opsc.powerpath

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import java.util.Locale

class LanguageManager(private val context: Context) {

    fun updateResource(code: String) {
        val locale = Locale(code)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}