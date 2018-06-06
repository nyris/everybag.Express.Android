package de.everybag.express.utils

import android.content.Context
import android.preference.PreferenceManager

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class ParamsUtils {
    companion object {
        /**
         * Save value to Shared Preferences
         * @param key A variable of type String
         * @param value A variable of type String
         */
        fun saveParam(context: Context, key: String, value: String) {
            val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = mPrefs.edit()
            editor.putString(key, value)
            editor.apply()
        }

        /**
         * Get Saved value from Shared Preferences
         * @param key A variable of type String
         * @return String value of key
         */
        fun getParam(context: Context, key: String): String {
            val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            return mPrefs.getString(key, "")
        }
    }
}