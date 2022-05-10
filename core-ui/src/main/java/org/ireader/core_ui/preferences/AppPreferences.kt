package org.ireader.core_ui.preferences

import org.ireader.core_api.prefs.Preference
import org.ireader.core_api.prefs.PreferenceStore

class AppPreferences(
    private val preferenceStore: PreferenceStore,
) {
    companion object PreferenceKeys {


        const val SAVED_LIBRARY_LAYOUT_KEY = "library_layout_type"
        const val SAVED_BROWSE_LAYOUT_KEY = "browser_layout_type"

        const val SORT_LIBRARY_SCREEN = "sort_library_screen"
        const val SORT_DESC_LIBRARY_SCREEN = "sort_desc_library_screen"




        /** Services **/
        const val Last_UPDATE_CHECK = "last_update_check"

        /** Setting Pref**/
        const val SAVED_DOH_KEY = "SAVED_DOH_KEY"
        const val THEME_MODE_KEY = "theme_mode_key"
        const val LIGHT_MODE_KEY = "theme_light"
        const val NIGHT_MODE_KEY = "theme_dark"
    }



    fun libraryLayoutType(): Preference<Int> {
        return preferenceStore.getInt(SAVED_LIBRARY_LAYOUT_KEY, 0)
    }

    fun exploreLayoutType(): Preference<Int> {
        return preferenceStore.getInt(SAVED_BROWSE_LAYOUT_KEY, 0)
    }

    fun dohStateKey(): Preference<Int> {
        return preferenceStore.getInt(SAVED_DOH_KEY, 0)
    }


    fun sortLibraryScreen(): Preference<Int> {
        return preferenceStore.getInt(SORT_LIBRARY_SCREEN, 0)
    }

    fun sortDescLibraryScreen(): Preference<Boolean> {
        return preferenceStore.getBoolean(SORT_DESC_LIBRARY_SCREEN, true)
    }



    fun lastUpdateCheck(): Preference<Long> {
        return preferenceStore.getLong(Last_UPDATE_CHECK, 0)
    }
}

