package com.w10group.hertzdictionary.app.biz.ui.main

import android.animation.LayoutTransition
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import com.google.android.material.appbar.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
import com.w10group.hertzdictionary.app.R
import com.w10group.hertzdictionary.app.biz.ui.about.AboutDeveloperActivity
import com.w10group.hertzdictionary.data.InquireResult
import com.w10group.hertzdictionary.app.biz.ui.features.FeaturesActivity
import com.w10group.hertzdictionary.app.biz.ui.licence.LicenceActivity
import com.w10group.hertzdictionary.app.biz.manager.ImageManagerService
import com.w10group.hertzdictionary.app.biz.ui.statistics.StatisticsActivity
import com.w10group.hertzdictionary.app.core.view.*
import com.w10group.hertzdictionary.data.Dict
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * MainActivity 的 Anko UI
 * @author Qiao
 */

class MainActivityUIComponent(private val mMainActivity: MainActivity) : AnkoComponent<MainActivity>, LifecycleObserver {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mCollapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var mToolBar: Toolbar
    private lateinit var mNestedScrollView: NestedScrollView

    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mBackgroundImageView: ImageView
    private lateinit var mOtherMeanCard: CardView
    private lateinit var mOtherMeanLayout: LinearLayout
    private lateinit var mETInput: EditText

    private lateinit var mTVSrcPronunciation: TextView
    private lateinit var mTVResult: TextView
    private lateinit var mTVPronunciation: TextView
    private lateinit var mTVOtherTranslation: TextView
    private lateinit var mTVRelatedWords: TextView

    private lateinit var mCurveView: CurveView

    private val gray600 by lazy { ContextCompat.getColor(mMainActivity, R.color.gray600) }
    private val deepWhite by lazy { ContextCompat.getColor(mMainActivity, R.color.deepWhite) }
    private val blue1 by lazy { ContextCompat.getColor(mMainActivity, R.color.blue1) }
    private val blue2 by lazy { ContextCompat.getColor(mMainActivity, R.color.blue2) }
    private val mTitleText by lazy { mMainActivity.getString(R.string.app_name) }

    override fun createView(ui: AnkoContext<MainActivity>): View = ui.apply {
        mDrawerLayout = drawerLayout {
            layoutTransition = LayoutTransition()

            coordinatorLayout {
                backgroundColor = deepWhite
                fitsSystemWindows = true

                mAppBarLayout = appBarLayout {
                    fitsSystemWindows = true
                    backgroundColor = Color.WHITE
                    isFocusableInTouchMode = true
                    translationZ = dip(8).toFloat()

                    mCollapsingToolbarLayout = collapsingToolbarLayout {
                        fitsSystemWindows = true
                        title = mTitleText
                        setExpandedTitleColor(Color.TRANSPARENT)

                        view {
                            fitsSystemWindows = true
                            backgroundColor = blue2
                        }.lparams(matchParent, getStatusBarSize(context)) {
                            collapseMode = COLLAPSE_MODE_PIN
                        }

                        verticalLayout {
                            mETInput = editText {
                                setHint(R.string.click_input_word)
                                hintTextColor = gray600
                                textColor = Color.BLACK
                                background = null
                                textSize = 22f
                                singleLine = true
                                inputType = InputType.TYPE_TEXT_VARIATION_URI
                                imeOptions = EditorInfo.IME_ACTION_SEARCH
                                setOnEditorActionListener { _, actionId, _ ->
                                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                        val text = text.toString().trim()
                                        mMainActivity.inquire(text)
                                    }
                                    false
                                }
                                addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                                    override fun afterTextChanged(s: Editable?) {
                                        if (mMainActivity.status == MainActivity.STATUS_INQUIRED) restore()
                                    }
                                })
                            }.lparams(matchParent, wrapContent)

                            mTVSrcPronunciation = textView {
                                visibility = View.GONE
                                textColor = Color.BLACK
                                textSize = 14f
                            }.lparams(wrapContent, wrapContent)

                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(100)
                            marginStart = dip(16)
                            marginEnd = dip(16)
                            collapseMode = COLLAPSE_MODE_PARALLAX
                        }

                        mToolBar = toolbar {
                            backgroundColor = blue1
                        }.lparams(matchParent, getActionBarSize(context)) {
                            collapseMode = COLLAPSE_MODE_PIN
                        }

                    }.lparams(matchParent, wrapContent) {
                        scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    }

                }.lparams(matchParent, wrapContent)

                mNestedScrollView = nestedScrollView {
                    visibility = View.GONE
                    verticalLayout {
                        bottomPadding = dip(16)
                        cardView {
                            elevation = dip(4).toFloat()
                            isClickable = true
                            backgroundColor = blue1
                            foreground = createTouchFeedbackBorderless(context)
                            relativeLayout {
                                val sourceLanguageId = 1
                                val resultId = 2
                                val pronunciationId = 3
                                val otherTranslationId = 4
                                imageView {
                                    setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close_white_24dp))
                                    setOnClickListener { if (mMainActivity.status == MainActivity.STATUS_INQUIRED) restore() }
                                }.lparams(wrapContent, wrapContent) {
                                    alignParentTop()
                                    alignParentEnd()
                                }
                                textView {
                                    id = sourceLanguageId
                                    setText(R.string.sample_chinese)
                                    textColor = Color.WHITE
                                    textSize = 16f
                                }.lparams(wrapContent, wrapContent) {
                                    alignParentTop()
                                    alignParentStart()
                                }
                                mTVResult = textView {
                                    id = resultId
                                    textColor = Color.WHITE
                                    textSize = 22f
                                }.lparams(wrapContent, wrapContent) {
                                    alignStart(sourceLanguageId)
                                    bottomOf(sourceLanguageId)
                                    topMargin = dip(16)
                                    marginStart = dip(8)
                                }
                                mTVPronunciation = textView {
                                    id = pronunciationId
                                    textColor = Color.WHITE
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    alignStart(resultId)
                                    bottomOf(resultId)
                                    topMargin = dip(4)
                                }
                                mTVOtherTranslation = textView {
                                    id = otherTranslationId
                                    textColor = Color.WHITE
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    alignStart(pronunciationId)
                                    bottomOf(pronunciationId)
                                    topMargin = dip(16)
                                }
                                mTVRelatedWords = textView {
                                    textColor = Color.WHITE
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    alignStart(otherTranslationId)
                                    bottomOf(otherTranslationId)
                                    topMargin = dip(16)
                                }
                            }.lparams(matchParent, wrapContent) {
                                margin = dip(16)
                            }
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(8)
                            marginStart = dip(8)
                            marginEnd = dip(8)
                        }

                        mOtherMeanCard = cardView {
                            elevation = dip(4).toFloat()
                            isClickable = true
                            backgroundColor = Color.WHITE
                            foreground = createTouchFeedbackBorderless(context)
                            verticalLayout {
                                textView {
                                    setText(R.string.word_extension)
                                    textColor = Color.BLACK
                                    textSize = 16f
                                }.lparams(wrapContent, wrapContent) {
                                    topMargin = dip(8)
                                    bottomMargin = dip(16)
                                    marginStart = dip(8)
                                }
                                mOtherMeanLayout = verticalLayout().lparams(matchParent, wrapContent)
                            }.lparams(matchParent, wrapContent)
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(8)
                            marginStart = dip(8)
                            marginEnd = dip(8)
                        }

                        cardView {
                            elevation = dip(4).toFloat()
                            isClickable = true
                            backgroundColor = Color.WHITE
                            foreground = createTouchFeedbackBorderless(context)
                            verticalLayout {

                                appCompatSpinner {
                                    adapter = DateSpinnerAdapter(context)
                                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                            mMainActivity.curveStatus = position
                                        }
                                    }
                                }.lparams(wrapContent, wrapContent) {
                                    topMargin = dip(16)
                                }

                                mCurveView = curveView().lparams(matchParent, dip(256))
                            }.lparams(matchParent, wrapContent) {
                                marginStart = dip(16)
                                marginEnd = dip(16)
                            }
                        }.lparams(matchParent, wrapContent) {
                            marginStart = dip(8)
                            marginEnd = dip(8)
                            topMargin = dip(8)
                        }

                    }.lparams(matchParent, wrapContent)
                }.lparams(matchParent, matchParent) {
                    behavior = AppBarLayout.ScrollingViewBehavior()
                }

                mRecyclerView = recyclerView {
                    val linearLayoutManager = LinearLayoutManager(context)
                    layoutManager = linearLayoutManager
                    itemAnimator = DefaultItemAnimator()
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        private var firstPosition = 0
                        private var lastPosition = 0

                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            with(linearLayoutManager) {
                                firstPosition = findFirstCompletelyVisibleItemPosition()
                                lastPosition = findLastCompletelyVisibleItemPosition()
                            }
                        }

                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                if (firstPosition == 0 && mScrollFlag) {
                                    recyclerView.snackbar(R.string.slide_to_top)
                                    mScrollFlag = false
                                }
                                if (lastPosition + 1 == adapter?.itemCount && mScrollFlag) {
                                    recyclerView.snackbar(R.string.slide_to_bottom)
                                    mScrollFlag = false
                                }
                            }
                        }
                    })
                }.lparams(matchParent, matchParent) {
                    behavior = AppBarLayout.ScrollingViewBehavior()
                }

            }.lparams(matchParent, matchParent)

            navigationView {
                inflateMenu(R.menu.menu_navigation)
                fitsSystemWindows = true
                isClickable = true
                backgroundColor = deepWhite
                itemTextColor = ContextCompat.getColorStateList(context, R.color.gray600)
                addHeaderView(createHeaderView())
                setNavigationItemSelectedListener {
                    it.isCheckable = false
                    when (it.itemId) {
                        R.id.main_menu_statistics -> startActivity<StatisticsActivity>()
                        // R.id.main_menu_more_features -> startActivity<FeaturesActivity>()
                        // R.id.main_menu_mine -> startActivity<AboutDeveloperActivity>()
                        R.id.main_menu_licence -> startActivity<LicenceActivity>()
                    }
                    true
                }
            }.lparams(matchParent, matchParent) {
                gravity = Gravity.START
            }
        }
    }.view

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun init() {
        mMainActivity.setSupportActionBar(mToolBar)
        val toggle = ActionBarDrawerToggle(mMainActivity, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    suspend fun loadBackgroundImageView() = ImageManagerService.loadBackground(mBackgroundImageView, mMainActivity.lifecycle)

    fun scrollToTop() {
        if (mMainActivity.status == MainActivity.STATUS_INQUIRED_NOT) {
            mScrollFlag = true
            mRecyclerView.smoothScrollToPosition(0)
        }
    }

    fun scrollToBottom() {
        if (mMainActivity.status == MainActivity.STATUS_INQUIRED_NOT)
            mRecyclerView.adapter?.itemCount?.let {
                mScrollFlag = true
                mRecyclerView.smoothScrollToPosition(it - 1)
            }
    }

    private fun createHeaderView() = mMainActivity.UI {
        mBackgroundImageView = imageView {
            layoutParams = ViewGroup.LayoutParams(matchParent, dip(176))
            scaleType = ImageView.ScaleType.CENTER_CROP
            backgroundColor = blue1
            isClickable = true
            foreground = createTouchFeedbackBorderless(context)
        }
    }.view

    // 标记位，当值为 true 时，RecyclerView 滑动到顶部或底部才会有弹窗。
    private var mScrollFlag = false

    private fun TextView.setWords(tips: String, words: String) {
        visibility = if (words.isBlank()) View.GONE
        else {
            val wordText = "$tips\n$words"
            text = wordText
            View.VISIBLE
        }
    }

    fun isShouldHideInput(view: View?, event: MotionEvent): Boolean {
        if (view != null && view == mETInput) {
            val l = intArrayOf(0, 0)
            view.getLocationInWindow(l)
            val (left, top) = l
            val right = left + view.width
            val bottom = top + view.height
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    //将界面恢复到未查询的状态
    fun restore() {
        mMainActivity.status = MainActivity.STATUS_INQUIRED_NOT
        mRecyclerView.visibility = View.VISIBLE
        mNestedScrollView.visibility = View.GONE
        mTVSrcPronunciation.visibility = View.GONE
        mCollapsingToolbarLayout.title = mTitleText
        mETInput.setText("")
        mMainActivity.refreshRecyclerView()
    }

    fun setWordText(text: String) = mETInput.setText(text)

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mRecyclerView.adapter = adapter
    }

    fun displayInquireResult(inquireResult: InquireResult, word: String) {
        //改变控件状态
        if (mMainActivity.status == MainActivity.STATUS_INQUIRED_NOT) {
            mRecyclerView.visibility = View.GONE
            mNestedScrollView.visibility = View.VISIBLE
            mTVSrcPronunciation.visibility = View.VISIBLE
            mAppBarLayout.setExpanded(true, true)
            mCollapsingToolbarLayout.title = word
            mMainActivity.status = MainActivity.STATUS_INQUIRED
        }

        //展示读音信息
        inquireResult.word?.let {
            mTVResult.text = it[0].ch
            if (it[1].srcPronunciation.isBlank()) {
                mTVSrcPronunciation.visibility = View.GONE
            } else {
                mTVSrcPronunciation.visibility = View.VISIBLE
                val srcPronunciationText = mMainActivity.getString(R.string.pronunciation, it[1].srcPronunciation)
                mTVSrcPronunciation.text = srcPronunciationText
            }

            mTVPronunciation.visibility = if (it[1].pronunciation.isBlank())
                 View.GONE
            else {
                mTVPronunciation.text = it[1].pronunciation
                View.VISIBLE
            }
        }

        //显示扩展词意
        mOtherMeanLayout.removeAllViews()
        mOtherMeanCard.visibility = if (inquireResult.dict == null) View.GONE
        else {
            (inquireResult.dict as List<Dict>).forEach { dict ->
                with<_LinearLayout, Unit>(mOtherMeanLayout as _LinearLayout) {
                    textView {
                        textSize = 16f
                        textColor = gray600
                        text = dict.posType
                    }.lparams(wrapContent, wrapContent) {
                        marginStart = dip(16)
                    }
                    recyclerView {
                        layoutManager = LinearLayoutManager(context)
                        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                        dict.dictInfo?.let {
                            adapter = OtherMeanAdapter(context, it)
                        }
                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(32)
                        marginEnd = dip(8)
                        topMargin = dip(16)
                        bottomMargin = dip(24)
                    }
                }
            }
            View.VISIBLE
        }
    }

    infix fun displayOtherTranslation(words: String) =
            mTVOtherTranslation.setWords(mMainActivity.getString(R.string.other_translation), words)

    infix fun displayRelatedWords(words: String) =
            mTVRelatedWords.setWords(mMainActivity.getString(R.string.related_words), words)

    fun updateCurveView(timeList: List<Long>, valueList: List<Int>) = mCurveView.setData(timeList, valueList)

    fun snackBar(@StringRes msg: Int) = mRecyclerView.snackbar(msg)

}