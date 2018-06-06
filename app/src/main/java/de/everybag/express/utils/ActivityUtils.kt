package de.everybag.express.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager


/**
 * ActivityUtils.kt - Helper class that contain activity operations
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class ActivityUtils {
    companion object {
        fun addFragmentToActivity(fragmentManager: FragmentManager,
                                  fragment: Fragment, frameId: Int) {
            val transaction = fragmentManager.beginTransaction()
            transaction.add(frameId, fragment)
            transaction.addToBackStack(fragment.javaClass.name)
            transaction.commit()
        }
    }
}
