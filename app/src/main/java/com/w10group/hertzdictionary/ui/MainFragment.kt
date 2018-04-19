package com.w10group.hertzdictionary.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.w10group.hertzdictionary.model.Word
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI

/**
 * 主界面Fragment
 */
class MainFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mData: List<Word>
    private lateinit var mLastVisibleItem: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mData = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            UI {
                verticalLayout {
                    /*cardView {
                        elevation = dip(4).toFloat()
                        translationZ = dip(4).toFloat()
                        isClickable = true
                        isFocusable = true
                        radius = dip(4).toFloat()
                        backgroundTintList = ContextCompat.getColorStateList(mActivity, android.R.color.white)!!
                        //foreground = ContextCompat.getDrawable(mActivity, actionBarSize)
                        linearLayout {

                        }
                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(4)
                        marginEnd = dip(4)
                    }*/
                    mRecyclerView = recyclerView {
                        val linearLayoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
                        layoutManager = linearLayoutManager
                        itemAnimator = DefaultItemAnimator()
                        addOnScrollListener(object : RecyclerView.OnScrollListener {
                            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                            }

                            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                                super.onScrollStateChanged(recyclerView, newState)
                                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == )
                            }
                        })
                    }.lparams(matchParent, wrapContent) {
                        gravity = Gravity.CENTER
                    }
                }
            }.view





    override fun onStart() {
        super.onStart()
        mActivity.fragmentStatus = mActivity.MAIN
        mActivity.invalidateOptionsMenu()
    }

}