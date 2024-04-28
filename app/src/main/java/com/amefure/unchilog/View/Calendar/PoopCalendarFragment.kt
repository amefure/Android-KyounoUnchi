package com.amefure.unchilog.View.Calendar

import android.os.Build
import android.os.Bundle
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
import com.amefure.unchilog.View.Setting.SettingFragment
import com.amefure.unchilog.View.TheDayDetail.TheDayDetailFragment
import com.amefure.unchilog.ViewModel.PoopCalendarViewModel
import com.amefure.unchilog.ViewModel.PoopViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class PoopCalendarFragment : Fragment(){

    // カレンダーロジックリポジトリ
    private lateinit var sccalenderRepository: SCCalenderRepository

    private var selectMode: Int = 0

    private val poopViewModel: PoopViewModel by viewModels()
    private val viewModel: PoopCalendarViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_poop_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sccalenderRepository = SCCalenderRepository()
        poopViewModel.fetchAllPoops()

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.observeInitWeek().collect { week ->
                val dayOfWeek = week.toString().toDayOfWeek()
                sccalenderRepository = SCCalenderRepository(dayOfWeek)
                setUpRecycleView(view)
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
    private fun setUpRecycleView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.day_recycle_layout)
        val weekRecyclerView: RecyclerView = view.findViewById(R.id.week_recycle_layout)
        poopViewModel.poops.observe(viewLifecycleOwner) { poops ->
            // 月の日付更新
            lifecycleScope.launch(Dispatchers.Main) {
                sccalenderRepository.currentDates.collect { scdate ->
                    recyclerView.layoutManager =
                        GridLayoutManager(requireContext(), 7, RecyclerView.VERTICAL, false)
                    val itemTouchListener = TheDayTouchListener()
                    itemTouchListener.setOnTappedListener(
                        object : TheDayTouchListener.onTappedListener {
                            override fun onTapped(scdate: SCDate) {
                                scdate.date?.let { date ->
                                    parentFragmentManager.beginTransaction().apply {
                                        add(
                                            R.id.main_frame,
                                            TheDayDetailFragment.newInstance(date.time)
                                        )
                                        addToBackStack(null)
                                        commit()
                                    }
                                }
                            }
                        }
                    )
                    recyclerView.addOnItemTouchListener(itemTouchListener)
                    recyclerView.adapter = PoopCalendarAdapter(scdate, poops)
                }
            }

            // 曜日グリッドレイアウト更新
            lifecycleScope.launch(Dispatchers.Main) {
                sccalenderRepository.dayOfWeekList.collect { week ->
                    weekRecyclerView.layoutManager =
                        GridLayoutManager(requireContext(), 7, RecyclerView.VERTICAL, false)
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
        val headerTitleButton: Button = header.findViewById(R.id.header_title_button)
        val todayButton: ImageButton = header.findViewById(R.id.today_button)

        headerSpace.visibility = View.VISIBLE
        forwardMonthButton.visibility = View.VISIBLE
        backMonthButton.visibility = View.VISIBLE
        todayButton.visibility = View.VISIBLE

        forwardMonthButton.setOnClickListener {
            var result = sccalenderRepository.forwardMonth()
            if (!result) {
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_failed_calendar_out_range),
                    showPositive = true,
                    showNegative = false
                )
                dialog.show(parentFragmentManager, "CalendarOutRangeNameDialog")
            }
        }

        backMonthButton.setOnClickListener {
            var result = sccalenderRepository.backMonth()
            if (!result) {
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_failed_calendar_out_range),
                    showPositive = true,
                    showNegative = false
                )
                dialog.show(parentFragmentManager, "CalendarOutRangeNameDialog")
            }
        }

        todayButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH) + 1
            sccalenderRepository.moveYearAndMonthCalendar(year, month)
        }

        val leftButton: ImageButton = header.findViewById(R.id.left_button)
        leftButton.setImageDrawable(null)
        leftButton.isEnabled = false

        val rightButton: ImageButton = header.findViewById(R.id.right_button)
        rightButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, SettingFragment())
                addToBackStack(null)
                commit()
            }
        }

        // ヘッダーの[2024年4月]テキスト更新
        lifecycleScope.launch(Dispatchers.Main) {
            sccalenderRepository.currentYearAndMonth.collect {
                it?.let {
                    headerTitleButton.text = it.fullname
                }
            }
        }
    }
}