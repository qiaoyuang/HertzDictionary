package com.w10group.hertzdictionary.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.w10group.hertzdictionary.model.Word
import com.w10group.hertzdictionary.network.InquireWordService
import com.w10group.hertzdictionary.network.NetworkUtil
import com.w10group.hertzdictionary.ui.MainActivity.FragmentID
import com.w10group.hertzdictionary.util.FIRST
import com.w10group.hertzdictionary.util.LOAD_MORE
import com.w10group.hertzdictionary.util.NetworkWordListManager
import com.w10group.hertzdictionary.util.WordListManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI

/**
 * 主界面Fragment
 */
class MainFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCardView: CardView

    private lateinit var mData: MutableList<Word>
    private var mLastVisibleItem: Int = 0
    private lateinit var mAdapter: WordListAdapter

    private lateinit var mWordListManager: WordListManager

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mData = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = UI {
            verticalLayout {
                mCardView = cardView {
                    relativeLayout {

                    }
                }.lparams(matchParent, wrapContent) {
                    margin = dip(4)
                    elevation = dip(4).toFloat()
                    translationZ = dip(4).toFloat()
                    gravity = Gravity.CENTER
                }
                mRecyclerView = recyclerView {
                    val linearLayoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
                    mAdapter = WordListAdapter(mActivity, mData)
                    mRecyclerView.adapter = mAdapter
                    mWordListManager = NetworkWordListManager(mActivity, mRecyclerView, mAdapter, mData, index)
                    layoutManager = linearLayoutManager
                    itemAnimator = DefaultItemAnimator()
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                        }

                        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.itemCount) {
                                if (mData.size % 30 == 0) {
                                    index++
                                    mWordListManager.getWordList(LOAD_MORE)
                                }
                            }
                        }
                    })
                }.lparams(matchParent, wrapContent) {
                    gravity = Gravity.CENTER
                }
            }
        }.view
        mWordListManager.getWordList(FIRST)
        return view
    }

    //查询单词的网络请求
    private fun inquireWord() {
        if (!NetworkUtil.checkNetwork(mActivity)) {
            snackbar(mRecyclerView, "当前无网络连接")
            return
        }
        NetworkUtil.create<InquireWordService>().inquire("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {

                        },
                        onError = {
                            snackbar(mRecyclerView, "单词查询失败")
                            it.printStackTrace()
                        }
                )
    }

    override fun onStart() {
        super.onStart()
        mActivity.fragmentStatus = FragmentID.MAIN
        mActivity.invalidateOptionsMenu()
    }

}