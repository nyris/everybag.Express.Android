package de.everybag.express.ui.resultscreen

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.everybag.express.R
import de.everybag.express.base.BaseFragment
import de.everybag.express.di.ActivityScoped
import de.everybag.express.model.OfferParcelable
import de.everybag.express.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
@ActivityScoped
class SearchFragment @Inject constructor() : BaseFragment<SearchResultContract.Presenter>(), SearchResultContract.View {
    private var mOffersAdapter: SearchResultsAdapter
    private lateinit var mDialogUtils: DialogUtils
    private var isShowed: Boolean = false

    @Inject
    lateinit var mPresenter: SearchResultContract.Presenter

    private val snackListener = object : DialogUtils.ISnackListener {
        override fun onClick() {
            showClassificationActivity()
        }
    }

    init {
        val listener = object : ISearchResultsViewItemListener {
            override fun onItemClick(image: ByteArray) {
                mPresenter.searchSimilarOffers(image)
            }

            override fun onOpenUrl(link: String) {
                mPresenter.openOfferLink(link)
            }
        }
        mOffersAdapter = SearchResultsAdapter(listener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDialogUtils = DialogUtils(context as Activity)

        val image = arguments!!.getByteArray("image")
        val imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
        imCropped.setImageBitmap(imageBitmap)
        imCropped.setOnClickListener({
            activity?.onBackPressed()
        })

        val listOffers = arguments!!.getParcelableArrayList<OfferParcelable>("listOffers")
        if (listOffers != null)
            mOffersAdapter.setOffers(listOffers)

        rcOffers.layoutManager = GridLayoutManager(context, 2)
        rcOffers.adapter = mOffersAdapter

        if (listOffers == null) {
            mPresenter.searchOffers(image)
        } else {
            if (listOffers.size == 0) {
                showError("No offers found for the selected object")
                return
            }
            showOffers(listOffers)
            hideProgress()
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onAttach(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDetach()
    }

    override fun showProgress() {
        vProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        vProgress.visibility = View.GONE
    }

    override fun showError(message: String) {
        mDialogUtils.messageBoxDialog("Error", message, DialogUtils.KindMessageBox.Back)
    }

    override fun showOffers(offers: ArrayList<OfferParcelable>) {
        mOffersAdapter.setOffers(offers)
        showSnackViewOnce()
    }

    override fun showOfferWebSite(link: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context!!, Uri.parse(link))
    }

    override fun showSnackViewOnce() {
        if (isShowed)
            return
        mDialogUtils.showSnackBar(view!!,
                "Did you find what you were looking for?",
                "Yes",
                "No",
                null,
                snackListener)
        isShowed = true
    }

    override fun showClassificationActivity() {
        TODO("To classification activity not implemented")
    }
}