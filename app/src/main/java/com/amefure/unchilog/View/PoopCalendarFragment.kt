package com.amefure.unchilog.View

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.R
import com.amefure.unchilog.Repository.SCCalender.SCCalenderRepository
import com.amefure.unchilog.Repository.SCCalender.fullname
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.View.RecycleViewSetting.PoopCalendarAdapter
import com.amefure.unchilog.View.RecycleViewSetting.WeekAdapter
import com.amefure.unchilog.ViewModel.PoopViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class PoopCalendarFragment : Fragment() {

    // カレンダーロジックリポジトリ
    private var sccalenderRepository = SCCalenderRepository()

    private val viewModel: PoopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_poop_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var header: ConstraintLayout = view.findViewById(R.id.include_header)
        var forwardMonthButton: ImageButton = header.findViewById(R.id.forward_month_button)
        var backMonthButton: ImageButton = header.findViewById(R.id.back_month_button)
        var yearAndMonthButton: Button = header.findViewById(R.id.year_and_month_button)

        var footer: ConstraintLayout = view.findViewById(R.id.include_footer)
        var entryPoopButton: ImageButton = footer.findViewById(R.id.entry_poop_button)

        entryPoopButton.setOnClickListener {
            viewModel.insertPoop(createdAt = Date())
            val dialog = CustomNotifyDialogFragment.newInstance(
                title = getString(R.string.dialog_title_notice),
                msg = getString(R.string.dialog_msg_success_entry_poop),
                showPositive = true,
                showNegative = false
            )
            dialog.show(parentFragmentManager, "SuccessEntryPoopDialog")
        }

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

        // 月の日付更新
        lifecycleScope.launch(Dispatchers.Main) {
            sccalenderRepository.currentDates.collect {
                val recyclerView: RecyclerView = view.findViewById(R.id.day_recycle_layout)
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 7, RecyclerView.VERTICAL, false)
                recyclerView.addItemDecoration(
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                )
                recyclerView.adapter = PoopCalendarAdapter(it)
            }
        }

        // 曜日グリッドレイアウト更新
        lifecycleScope.launch(Dispatchers.Main) {
            sccalenderRepository.dayOfWeekList.collect {
                val recyclerView: RecyclerView = view.findViewById(R.id.week_recycle_layout)
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 7, RecyclerView.VERTICAL, false)
                recyclerView.addItemDecoration(
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                )
                recyclerView.adapter = WeekAdapter(it)
            }
        }

        // ヘッダーの[2024年4月]テキスト更新
        lifecycleScope.launch(Dispatchers.Main) {
            sccalenderRepository.currentYearAndMonth.collect {
                it?.let {
                    yearAndMonthButton.text = it.fullname
                }
            }
        }

        viewModel.poops.observe(viewLifecycleOwner) {
            Log.e("poops", it.toString())
        }
    }

}