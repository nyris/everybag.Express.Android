/*
 * Copyright 2018 nyris GmbH. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        fun getParam(context: Context, key: String, default: String = ""): String {
            val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            return mPrefs.getString(key, default)
        }

        /**
         * Get Saved value from Shared Preferences
         * @param key A variable of type String
         * @return String value of key
         */
        fun getParamBoolean(context: Context, key: String, default: Boolean = false): Boolean {
            val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            return mPrefs.getBoolean(key, false)
        }

    }
}