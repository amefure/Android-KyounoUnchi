package com.amefure.unchilog.View.Calendar

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Space
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.SCCalender.SCDate
import com.amefure.unchilog.R
import com.amefure.unchilog.Repository.SCCalender.SCCalenderRepository
import com.amefure.unchilog.Repository.SCCalender.fullname
import com.amefure.unchilog.Repository.SCCalender.toDayOfWeek
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.View.InputPoopFragment
import com.amefure.unchilog.View.Calendar.RecycleViewSetting.PoopCalendarAdapter
import com.amefure.unchilog.View.Calendar.RecycleViewSetting.TheDayTouchListener
import com.amefure.unchilog.View.Calendar.RecycleViewSetting.WeekAdapter
import com.amefure.unchilog.View.Charts.PoopChartsFragment
import com.amefure.unchilog.View.Setting.SettingFragment
import com.amefure.unchilog.View.TheDayDetail.TheDayDetailFragment
import com.amefure.unchilog.ViewModel.PoopCalendarViewModel
import com.amefure.unchilog.ViewModel.PoopViewModel
import com.amefure.unchilog.ViewModel.SCCalendarViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class PoopCalendarFragment : Fragment(){

    private var mInterstitialAd: InterstitialAd? = null
    private var mInterstitialCount: Int = 0

    private var selectMode: Int = 0

    private val poopViewModel: PoopViewModel by viewModels()
    private val viewModel: PoopCalendarViewModel by viewModels()
    private val calendarViewModel: SCCalendarViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var weekRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_poop_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadInterstitial()
        observeInterstitialCount()

        recyclerView = view.findViewById(R.id.day_recycle_layout)
        weekRecyclerView = view.findViewById(R.id.week_recycle_layout)

        poopViewModel.fetchAllPoops()

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.observeInitWeek().collect { week ->
                val dayOfWeek = week.toString().toDayOfWeek()
                calendarViewModel.updateInitWeek(dayOfWeek)
                updateRecycleView()
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.observeEntryMode().collect { mode ->
                selectMode = mode ?: 0
            }
        }

        setUpHeaderAction(view)
        setUpFooterAction(view)
        setUpPoopMessage(view)
        setUpRecycleView()
    }

    /**
     * Mr Poopのメッセージ格納
     */
    public fun setUpPoopMessage(view: View) {
        val messageLayout: ConstraintLayout = view.findViewById(R.id.include_message)
        val messageText: TextView = messageLayout.findViewById(R.id.message_text)

        messageLayout.setOnClickListener {
            val randomValue = (1..12).random()
            val msg =  when(randomValue) {
                1 -> getString(R.string.poop_message_1)
                2 -> getString(R.string.poop_message_2)
                3 -> getString(R.string.poop_message_3)
                4 -> getString(R.string.poop_message_4)
                5 -> getString(R.string.poop_message_5)
                6 -> getString(R.string.poop_message_1) // getString(R.string.poop_message_6)
                7 -> getString(R.string.poop_message_7)
                8 -> getString(R.string.poop_message_8)
                9 -> getString(R.string.poop_message_9)
                10 -> getString(R.string.poop_message_10)
                11 -> getString(R.string.poop_message_11)
                12 -> getString(R.string.poop_message_12)
                else -> getString(R.string.poop_message_1)
            }
            messageText.setText(msg)
        }
        // 初回のみクリックしたことにする
        messageLayout.callOnClick()
    }

    /**
     * グリッドレイアウトリサイクルビューセットアップ
     * 1.月の日付
     * 2.曜日
     */
    private fun setUpRecycleView() {
        recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 7, RecyclerView.VERTICAL, false)
        val itemTouchListener = TheDayTouchListener()
        itemTouchListener.setOnTappedListener(
            object : TheDayTouchListener.onTappedListener {
                override fun onTapped(scdate: SCDate) {
                    scdate.date?.let { date ->
                        if (mInterstitialCount >= 5) {
                            mInterstitialCount = 0
                            viewModel.saveInterstitialCount(0)
                            if (mInterstitialAd != null) {
                                mInterstitialAd?.show(this@PoopCalendarFragment.requireActivity())
                            }
                        } else {
                            mInterstitialCount = mInterstitialCount + 1
                            viewModel.saveInterstitialCount(mInterstitialCount)
                        }
                        parentFragmentManager.beginTransaction().apply {
                            add(
                                R.id.main_frame,
                                TheDayDetailFragment.newInstance(date.time)
                            )
                            commit()
                        }
                    }
                }
            }
        )
        recyclerView.addOnItemTouchListener(itemTouchListener)

        weekRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 7, RecyclerView.VERTICAL, false)


        poopViewModel.poops.observe(viewLifecycleOwner) { poops ->
            // 月の日付更新
            lifecycleScope.launch(Dispatchers.Main) {
                calendarViewModel.currentDates.collect { scdate ->
                    recyclerView.adapter = PoopCalendarAdapter(scdate,  poops,this@PoopCalendarFragment.requireContext())
                }
            }

            // 曜日グリッドレイアウト更新
            lifecycleScope.launch(Dispatchers.Main) {
                calendarViewModel.dayOfWeekList.collect { week ->
                    weekRecyclerView.adapter = WeekAdapter(week, this@PoopCalendarFragment.requireContext())
                }
            }
        }
    }

    /**
     * グリッドレイアウトリサイクルビュー更新
     * 1.月の日付
     * 2.曜日
     */
    private fun updateRecycleView() {
        poopViewModel.poops.observe(viewLifecycleOwner) { poops ->
            // 月の日付更新
            lifecycleScope.launch(Dispatchers.Main) {
                calendarViewModel.currentDates.collect { scdate ->
                    recyclerView.adapter = PoopCalendarAdapter(scdate, poops, this@PoopCalendarFragment.requireContext())
                }
            }
            // 曜日グリッドレイアウト更新
            lifecycleScope.launch(Dispatchers.Main) {
                calendarViewModel.dayOfWeekList.collect { week ->
                    weekRecyclerView.adapter = WeekAdapter(week, this@PoopCalendarFragment.requireContext())
                }
            }
        }
    }

    /**
     * フッターボタンセットアップ
     */
    private fun setUpFooterAction(view: View) {
        val footer: ConstraintLayout = view.findViewById(R.id.include_footer)
        val entryPoopButton: ImageButton = footer.findViewById(R.id.entry_poop_button)

        entryPoopButton.setOnClickListener {
            // モードによって切り替え
            if (selectMode == 0) {
                poopViewModel.insertPoop(createdAt = Date())
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_success_entry_poop),
                    showPositive = true,
                    showNegative = false
                )
                dialog.show(parentFragmentManager, "SuccessEntryPoopDialog")
            } else {
                parentFragmentManager.beginTransaction().apply {
                    add(R.id.main_frame, InputPoopFragment())
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }


    /**
     * ヘッダーボタンセットアップ
     * [LeftButton]：backButton
     * [RightButton]：登録処理ボタン
     */
    private fun setUpHeaderAction(view: View) {
        val header: ConstraintLayout = view.findViewById(R.id.include_header)
        val forwardMonthButton: ImageButton = header.findViewById(R.id.forward_month_button)
        val backMonthButton: ImageButton = header.findViewById(R.id.back_month_button)
        val headerSpace: Space = header.findViewById(R.id.header_space)
        val todayButton: ImageButton = header.findViewById(R.id.today_button)

        headerSpace.visibility = View.VISIBLE
        forwardMonthButton.visibility = View.VISIBLE
        backMonthButton.visibility = View.VISIBLE
        todayButton.visibility = View.VISIBLE

        forwardMonthButton.setOnClickListener {
            var result = calendarViewModel.forwardMonth()
            if (!result) {
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_failed_calendar_out_range),
                    showPositive = true,
                    showNegative = false
                )
                dialog.show(parentFragmentManager, "CalendarOutRangeNameDialog")
            }
            updateHeaderYearAndMonth(view)
        }

        backMonthButton.setOnClickListener {
            var result = calendarViewModel.backMonth()
            if (!result) {
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_failed_calendar_out_range),
                    showPositive = true,
                    showNegative = false
                )
                dialog.show(parentFragmentManager, "CalendarOutRangeNameDialog")
            }
            updateHeaderYearAndMonth(view)
        }

        todayButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH) + 1
            calendarViewModel.moveYearAndMonthCalendar(year, month)
        }

        val leftButton: ImageButton = header.findViewById(R.id.left_button)
        val graphIcon: Drawable? = ResourcesCompat.getDrawable(getResources(), R.drawable.button_graph, null)
        leftButton.setImageDrawable(graphIcon)
        leftButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, PoopChartsFragment())
                addToBackStack(null)
                commit()
            }
        }

        val rightButton: ImageButton = header.findViewById(R.id.right_button)
        rightButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, SettingFragment())
                addToBackStack(null)
                commit()
            }
        }
        updateHeaderYearAndMonth(view)
    }

    /**
     * ヘッダーの年月日更新
     */
    private fun updateHeaderYearAndMonth(view: View) {
        val header: ConstraintLayout = view.findViewById(R.id.include_header)
        val headerTitleButton: Button = header.findViewById(R.id.header_title_button)
        // ヘッダーの[2024年4月]テキスト更新
        lifecycleScope.launch(Dispatchers.Main) {
            calendarViewModel.currentYearAndMonth.collect {
                it?.let {
                    headerTitleButton.text = it.fullname
                }
            }
        }
    }

    /**
     * インタースティシャルカウント観測
     */
    private fun observeInterstitialCount() {
        lifecycleScope.launch {
            viewModel.observeInterstitialCount().collect {
                mInterstitialCount = it ?: 0
            }
        }
    }

    /**
     * AdMobインタースティシャル読み込み
     */
    private fun loadInterstitial() {

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this.requireContext(),getString(R.string.admob_Interstitial_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }
}