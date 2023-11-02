@file:JvmName("ActivityTheming")
@file:Suppress("unused")

package com.sixtyninefourtwenty.theming

import android.app.Activity
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.StyleRes
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import com.sixtyninefourtwenty.theming.preferences.ThemingPreferenceFragment
import com.sixtyninefourtwenty.theming.preferences.ThemingPreferences
import com.sixtyninefourtwenty.theming.preferences.ThemingPreferencesSupplier
import com.sixtyninefourtwenty.theming.preferences.addThemingPreferences

/**
 * @param preferencesSupplier Preference storage to use if available. If
 * you're using [PreferenceGroup.addThemingPreferences] on a [PreferenceFragmentCompat],
 * make sure that the supplier is linked to whatever [SharedPreferences] or [PreferenceDataStore]
 * the fragment is using.
 *
 * If null, the default storage used by [ThemingPreferenceFragment] will be used.
 */
@JvmOverloads
fun Activity.applyTheming(
    @StyleRes material2ThemeStyleRes: Int,
    @StyleRes material3CustomColorsThemeStyleRes: Int,
    @StyleRes material3DynamicColorsThemeStyleRes: Int,
    preferencesSupplier: ThemingPreferencesSupplier? = null
) {
    val preferences = preferencesSupplier ?: ThemingPreferences(this)
    val themeStyleRes: Int = if (!preferences.md3) {
        material2ThemeStyleRes
    } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
        material3CustomColorsThemeStyleRes
    } else {
        material3DynamicColorsThemeStyleRes
    }
    setTheme(themeStyleRes)
    preferences.lightDarkMode.apply()
    preferences.themeColor.applyTo(theme)
}