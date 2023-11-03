@file:JvmName("ThemingPreferenceUI")
@file:Suppress("unused")

package com.sixtyninefourtwenty.theming.preferences

import android.app.Activity
import android.os.Build
import androidx.preference.ListPreference
import androidx.preference.PreferenceGroup
import androidx.preference.SwitchPreferenceCompat
import com.sixtyninefourtwenty.custompreferences.PredefinedColorPickerPreference
import com.sixtyninefourtwenty.theming.LightDarkMode
import com.sixtyninefourtwenty.theming.R
import com.sixtyninefourtwenty.theming.applyTheming

/**
 * Calls [addM3Preference], [addLightDarkModePreference], [addUseMD3CustomColorsThemeOnAndroid12Preference]
 * and [addThemeColorPreference] in that exact order.
 */
@JvmOverloads
fun PreferenceGroup.addThemingPreferences(
    activity: Activity,
    lightDarkMode: LightDarkMode,
    md3: Boolean,
    themeColorPreferenceAlwaysEnabledOnAndroid12: Boolean,
    m3PrefKey: String = ThemingPreferences.MD3_KEY,
    lightDarkModePrefKey: String = ThemingPreferences.LIGHT_DARK_MODE_KEY,
    useMD3CustomColorsThemeOnAndroid12PrefKey: String = ThemingPreferences.USE_MD3_CUSTOM_COLORS_ON_ANDROID_12,
    themeColorPrefKey: String = ThemingPreferences.PRIMARY_COLOR_KEY
) {
    addM3Preference(activity, m3PrefKey)
    addLightDarkModePreference(activity, lightDarkMode, lightDarkModePrefKey)
    addUseMD3CustomColorsThemeOnAndroid12Preference(activity, md3, useMD3CustomColorsThemeOnAndroid12PrefKey)
    addThemeColorPreference(activity, md3, themeColorPreferenceAlwaysEnabledOnAndroid12, themeColorPrefKey)
}

/**
 * Adds a [SwitchPreferenceCompat].
 * @param prefKey Key to use for the preference. Default is an internal key the library
 * implementations of [ThemingPreferencesSupplier] use - if you call [Activity.applyTheming]
 * with one such implementation, you don't need this parameter.
 */
@JvmOverloads
fun PreferenceGroup.addM3Preference(
    activity: Activity,
    prefKey: String = ThemingPreferences.MD3_KEY
) {
    addPreference(SwitchPreferenceCompat(activity).apply {
        key = prefKey
        title = activity.getString(R.string.md3)
        summary = activity.getString(R.string.md3_pref_summary)
        setDefaultValue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        setOnPreferenceChangeListener { _, _ ->
            activity.recreate()
            true
        }
    })
}

/**
 * Adds a [ListPreference].
 * @param prefKey Key to use for the preference. Default is an internal key the library
 * implementations of [ThemingPreferencesSupplier] use - if you call [Activity.applyTheming]
 * with one such implementation, you don't need this parameter.
 * @param lightDarkMode obtained from [ThemingPreferencesSupplier], will be used to set the preference's icon.
 */
@JvmOverloads
fun PreferenceGroup.addLightDarkModePreference(
    activity: Activity,
    lightDarkMode: LightDarkMode,
    prefKey: String = ThemingPreferences.LIGHT_DARK_MODE_KEY
) {
    addPreference(ListPreference(activity).apply {
        key = prefKey
        title = activity.getString(R.string.theme)
        dialogTitle = activity.getString(R.string.theme)
        entries = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.resources.getStringArray(R.array.themes_preference_entries)
        } else {
            activity.resources.getStringArray(R.array.themes_preference_entries_p)
        }
        entryValues = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.resources.getStringArray(R.array.themes_preference_entry_values)
        } else {
            activity.resources.getStringArray(R.array.themes_preference_entry_values_p)
        }
        setIcon(when (lightDarkMode) {
            LightDarkMode.LIGHT -> R.drawable.light_mode
            LightDarkMode.DARK -> R.drawable.dark_mode
            LightDarkMode.BATTERY -> R.drawable.battery_saver
            LightDarkMode.SYSTEM -> R.drawable.android
        })
        setDefaultValue(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) "system" else "light")
        setOnPreferenceChangeListener { _, _ ->
            activity.recreate()
            true
        }
        summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
    })
}

/**
 * Adds a [SwitchPreferenceCompat].
 * @param prefKey Key to use for the preference. Default is an internal key the library
 * implementations of [ThemingPreferencesSupplier] use - if you call [Activity.applyTheming]
 * with one such implementation, you don't need this parameter.
 * @param md3 obtained from [ThemingPreferencesSupplier], will be used to disable the preference if
 * it's `false` (Material 3 theme isn't active).
 */
@JvmOverloads
fun PreferenceGroup.addUseMD3CustomColorsThemeOnAndroid12Preference(
    activity: Activity,
    md3: Boolean,
    prefKey: String = ThemingPreferences.USE_MD3_CUSTOM_COLORS_ON_ANDROID_12
) {
    addPreference(SwitchPreferenceCompat(activity).apply {
        key = prefKey
        title = activity.getString(R.string.custom_colors_m3)
        setDefaultValue(false)
        setOnPreferenceChangeListener { _, _ ->
            activity.recreate()
            true
        }

        if (!md3) {
            isEnabled = false
            summary = activity.getString(R.string.md3_not_applied)
        }
    })
}

/**
 * Adds a [PredefinedColorPickerPreference].
 * @param prefKey Key to use for the preference. Default is an internal key the library
 * implementations of [ThemingPreferencesSupplier] use - if you call [Activity.applyTheming]
 * with one such implementation, you don't need this parameter.
 * @param md3 obtained from [ThemingPreferencesSupplier], will be used to disable the preference if
 * Material 3 dynamic colors is active.
 * @param alwaysEnabledOnAndroid12 obtained from [ThemingPreferencesSupplier.useM3CustomColorThemeOnAndroid12],
 * determines whether this preference will always be enabled on Android 12 or later. Nullifies [md3]
 * if set to `true`.
 */
@JvmOverloads
fun PreferenceGroup.addThemeColorPreference(
    activity: Activity,
    md3: Boolean,
    alwaysEnabledOnAndroid12: Boolean,
    prefKey: String = ThemingPreferences.PRIMARY_COLOR_KEY
) {
    addPreference(PredefinedColorPickerPreference(activity).apply {
        key = prefKey
        title = activity.getString(R.string.primary_color)
        setIcon(R.drawable.palette)
        setDefaultValue("#3385ff")
        setAvailableColorsArrayRes(R.array.theme_color_preference_available_colors)
        setOnPreferenceChangeListener { _, _ ->
            activity.recreate()
            true
        }

        if (!alwaysEnabledOnAndroid12) {
            if (md3 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                isEnabled = false
                summary = activity.getString(R.string.using_android_12_dynamic_colors)
            } else {
                summaryProvider = PredefinedColorPickerPreference.getSimpleSummaryProvider()
            }
        } else {
            summaryProvider = PredefinedColorPickerPreference.getSimpleSummaryProvider()
        }
    })
}
