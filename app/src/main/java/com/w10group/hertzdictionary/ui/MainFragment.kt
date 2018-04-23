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
    private var mLastVisibleItem: Int = 0
    private lateinit var mAdapter: WordListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mData = ArrayList()
        mAdapter = WordListAdapter(mActivity, mData)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            UI {
                verticalLayout {
                    mRecyclerView = recyclerView {
                        val linearLayoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
                        layoutManager = linearLayoutManager
                        itemAnimator = DefaultItemAnimator()
                        adapter = mAdapter
                        addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                            }

                            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                                super.onScrollStateChanged(recyclerView, newState)
                                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.itemCount) {

                                }
                            }
                        })
                    }.lparams(matchParent, wrapContent) {
                        gravity = Gravity.CENTER
                    }
                }
            }.view

    private fun getWord() {

    }





    override fun onStart() {
        super.onStart()
        mActivity.fragmentStatus = mActivity.MAIN
        mActivity.invalidateOptionsMenu()
    }

}