package com.echo.tiktok

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val PREFS_NAME = "tiktok_echo_prefs"
    private const val KEY_REGION = "spoof_region"
    private const val KEY_ENABLED = "spoof_enabled"
    
    const val DEFAULT_REGION = "US"
    
    val AVAILABLE_REGIONS = arrayOf("US", "KZ", "BY", "TR", "DE", "RU")
    
    fun getPrefs(context: Context): SharedPreferences {
        // Use world-readable mode for LSPosed/Xposed access
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_WORLD_READABLE)
    }
    
    fun getRegion(context: Context): String {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_REGION, DEFAULT_REGION) ?: DEFAULT_REGION
    }
    
    fun setRegion(context: Context, region: String) {
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_REGION, region).apply()
    }
    
    fun isEnabled(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(KEY_ENABLED, true)
    }
    
    fun setEnabled(context: Context, enabled: Boolean) {
        val prefs = getPrefs(context)
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }
    
    fun getOperatorCode(region: String): String {
        return when (region) {
            "US" -> "310260"
            "KZ" -> "40102"
            "BY" -> "25701"
            "TR" -> "28601"
            "DE" -> "26201"
            "RU" -> "25001"
            else -> "310260"
        }
    }
}
