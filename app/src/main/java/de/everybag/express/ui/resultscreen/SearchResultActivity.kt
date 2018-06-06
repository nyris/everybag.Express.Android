package de.everybag.express.ui.resultscreen

import android.os.Bundle
import dagger.Lazy
import de.everybag.express.R
import de.everybag.express.base.BaseActivity
import de.everybag.express.utils.ActivityUtils
import javax.inject.Inject

class SearchResultActivity : BaseActivity() {
    @Inject
    lateinit var searchFragmentProvider: Lazy<SearchFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val fragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
        val searchFragment: SearchFragment
        if (fragment == null) {
            // Get the fragment from dagger
            searchFragment = searchFragmentProvider.get()
            searchFragment.arguments = intent.extras
            ActivityUtils.addFragmentToActivity(
                    supportFragmentManager, searchFragment, R.id.contentFrame)
        }
    }
}
