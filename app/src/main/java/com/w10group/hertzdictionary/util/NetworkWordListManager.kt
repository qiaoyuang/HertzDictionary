package com.w10group.hertzdictionary.util

import android.content.Context
import android.view.View
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import com.w10group.hertzdictionary.model.Word
import com.w10group.hertzdictionary.model.appUser
import com.w10group.hertzdictionary.network.GetWordListService
import com.w10group.hertzdictionary.network.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.design.snackbar

class NetworkWordListManager(context: Context,
                             view: View,
                             adapter: Adapter<out ViewHolder>,
                             data: MutableList<Word>,
                             index: Int) : WordListManager(context, view, adapter, data, index) {

    override fun getWordList(model: Int) {
        if (!NetworkUtil.checkNetwork(mContext)) {
            snackbar(mView, "当前无网络连接")
            return
        }
        NetworkUtil.create<GetWordListService>().get(appUser!!.token, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            mData.addAll(it)
                            when (model) {
                                FIRST -> {
                                    mAdapter.notifyDataSetChanged()
                                }
                                LOAD_MORE -> {
                                    mAdapter.notifyItemRangeInserted(mData.size - 1, mData.size - index * 30)
                                }
                            }
                        },
                        onError = {
                            when (model) {
                                FIRST -> {
                                    snackbar(mView, "加载单词失败")
                                }
                                LOAD_MORE -> {
                                    index--
                                    snackbar(mView, "已无更多单词")
                                }
                            }
                            it.printStackTrace()
                        }
                )

    }

}