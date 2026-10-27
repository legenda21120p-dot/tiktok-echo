package com.echo.tiktok

import android.telephony.TelephonyManager
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.Locale

class MainHook : IXposedHookLoadPackage {
    
    companion object {
        private const val MODULE_PACKAGE = "com.echo.tiktok"
        private val TARGET_PACKAGES = listOf(
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill"
        )
    }
    
    private lateinit var prefs: XSharedPreferences
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!TARGET_PACKAGES.contains(lpparam.packageName)) {
            return
        }
        
        try {
            prefs = XSharedPreferences(MODULE_PACKAGE, "tiktok_echo_prefs")
            prefs.makeWorldReadable()
            
            if (!isSpoofEnabled()) {
                return
            }
            
            val targetRegion = getTargetRegion()
            XposedBridge.log("TikTok Echo: Spoofing region to $targetRegion for ${lpparam.packageName}")
            
            // Hook TelephonyManager methods
            hookTelephonyManager(lpparam.classLoader, targetRegion)
            
            // Hook Locale
            hookLocale(targetRegion)
            
        } catch (e: Throwable) {
            XposedBridge.log("TikTok Echo: Error in handleLoadPackage - ${e.message}")
        }
    }
    
    private fun isSpoofEnabled(): Boolean {
        prefs.reload()
        return prefs.getBoolean("spoof_enabled", true)
    }
    
    private fun getTargetRegion(): String {
        prefs.reload()
        return prefs.getString("spoof_region", "US") ?: "US"
    }
    
    private fun getOperatorCode(region: String): String {
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
    
    private fun hookTelephonyManager(classLoader: ClassLoader, targetRegion: String) {
        val telephonyClass = TelephonyManager::class.java
        
        // getSimCountryIso
        XposedHelpers.findAndHookMethod(
            telephonyClass,
            "getSimCountryIso",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (isSpoofEnabled()) {
                        param.result = targetRegion.lowercase(Locale.ROOT)
                    }
                }
            }
        )
        
        // getNetworkCountryIso
        XposedHelpers.findAndHookMethod(
            telephonyClass,
            "getNetworkCountryIso",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (isSpoofEnabled()) {
                        param.result = targetRegion.lowercase(Locale.ROOT)
                    }
                }
            }
        )
        
        // getSimOperator
        XposedHelpers.findAndHookMethod(
            telephonyClass,
            "getSimOperator",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (isSpoofEnabled()) {
                        param.result = getOperatorCode(targetRegion)
                    }
                }
            }
        )
        
        // getNetworkOperator
        XposedHelpers.findAndHookMethod(
            telephonyClass,
            "getNetworkOperator",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (isSpoofEnabled()) {
                        param.result = getOperatorCode(targetRegion)
                    }
                }
            }
        )
    }
    
    private fun hookLocale(targetRegion: String) {
        try {
            XposedHelpers.findAndHookMethod(
                Locale::class.java,
                "getDefault",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!isSpoofEnabled()) {
                            return
                        }
                        val originalLocale = param.result as? Locale ?: return
                        val spoofedLocale = Locale(originalLocale.language, targetRegion)
                        param.result = spoofedLocale
                    }
                }
            )
            
            // Also hook getCountry() specifically
            XposedHelpers.findAndHookMethod(
                Locale::class.java,
                "getCountry",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (isSpoofEnabled()) {
                            param.result = targetRegion
                        }
                    }
                }
            )
        } catch (e: Throwable) {
            XposedBridge.log("TikTok Echo: Error hooking Locale - ${e.message}")
        }
    }
}
