package com.w10group.hertzdictionary

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.support.v4.UI

/**
 * 主界面Fragment
 */
class MainFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            UI {
                verticalLayout {
                    cardView {
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
                    }
                }
            }.view


    override fun onStart() {
        super.onStart()
        mActivity.fragmentStatus = mActivity.MAIN
        mActivity.invalidateOptionsMenu()
    }
}