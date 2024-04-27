package com.amefure.unchilog.View.TheDayDetail

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.Key.AppArgKey
import com.amefure.unchilog.Model.Room.Poop
import com.amefure.unchilog.Model.SCCalender.SCDate
import com.amefure.unchilog.R
import com.amefure.unchilog.Utility.DateFormatUtility
import com.amefure.unchilog.View.Calendar.RecycleViewSetting.PoopCalendarAdapter
import com.amefure.unchilog.View.Calendar.RecycleViewSetting.TheDayTouchListener
import com.amefure.unchilog.View.Calendar.RecycleViewSetting.WeekAdapter
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.View.InputPoopFragment
import com.amefure.unchilog.View.TheDayDetail.RecycleViewSetting.PoopRowAdapter
import com.amefure.unchilog.View.TheDayDetail.RecycleViewSetting.PoopRowTouchListener
import com.amefure.unchilog.ViewModel.PoopViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


class TheDayDetailFragment : Fragment() , PopupMenu.OnMenuItemClickListener {

    private var dateStr: Long = 0
    private var date: Date = Date()

    private var selectPoop: Poop? = null

    private val viewModel: PoopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dateStr = it.getLong(AppArgKey.ARG_THE_DAY_KEY)
            date = Date(dateStr)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_the_day_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchAllPoops()
        setUpHeaderAction(view)
        setUpFooterAction(view)
        setUpRecycleView(view)
    }

    /**
     * ポップアップメニューを表示する
     * [view]には表示したいViewを渡す
     */
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.poop_row_menu)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.show()
    }


    /**
     * グリッドレイアウトリサイクルビューセットアップ
     * 1.月の日付
     * 2.曜日
     */
    private fun setUpRecycleView(view: View) {
        viewModel.poops.observe(viewLifecycleOwner) { poops ->
            val filteringList = poops.filter  { DateFormatUtility.isSameDate(it.createdAt, date) }
            val recyclerView: RecyclerView = view.findViewById(R.id.poop_recycle_layout)
            if (filteringList.size != 0) {
                recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
                recyclerView.addItemDecoration(
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                )
                val itemTouchListener = PoopRowTouchListener()
                itemTouchListener.setOnTappedListener(
                    object : PoopRowTouchListener.onTappedListener {
                        override fun onTapped(poop: Poop, rowView: View) {
                            selectPoop = poop
                            showPopupMenu(rowView)
                        }
                    }
                )
                recyclerView.addOnItemTouchListener(itemTouchListener)
                recyclerView.adapter = PoopRowAdapter(filteringList, this.requireContext())
            } else {
                recyclerView.visibility = View.GONE
                val messageLayout: ConstraintLayout = view.findViewById(R.id.include_message)
                messageLayout.visibility = View.VISIBLE
                val messagText: TextView = messageLayout.findViewById(R.id.message_text)
                messagText.setText(getString(R.string.poop_message_nothing))
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
            if (false) {
                viewModel.insertPoop(createdAt = date)
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_success_entry_poop),
                    showPositive = true,
                    showNegative = false
                )
                dialog.show(parentFragmentManager, "SuccessEntryPoopDialog")
            } else {
                parentFragmentManager.beginTransaction().apply {
                    add(R.id.main_frame, InputPoopFragment.newInstance(dateStr))
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }


    /**
     * ヘッダーボタンセットアップ
     * [LeftButton]：backButton
     * [RightButton]：非表示(GONE)
     */
    private fun setUpHeaderAction(view: View) {
        val header: ConstraintLayout = view.findViewById(R.id.include_header)
        val headerTitleButton: Button = header.findViewById(R.id.header_title_button)
        val df = DateFormatUtility()
        headerTitleButton.text = df.getString(date)

        val leftButton: ImageButton = header.findViewById(R.id.left_button)
        leftButton.isEnabled = true
        leftButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val rightButton: ImageButton = header.findViewById(R.id.right_button)
        rightButton.setImageDrawable(null)
        rightButton.isEnabled = false
    }

    /**
     *  メニューアイテムが選択された時の処理
     */
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_edit -> {
                val poop = selectPoop ?:return true
                parentFragmentManager.beginTransaction().apply {
                    add(R.id.main_frame, InputPoopFragment.newEditInstance(dateStr, poop.id))
                    addToBackStack(null)
                    commit()
                }
                return true
            }
            R.id.menu_delete -> {
                val poop = selectPoop ?:return true
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_delete_poop),
                    showPositive = true,
                    showNegative = false
                )
                dialog.setOnTappedListner(
                    object : CustomNotifyDialogFragment.onTappedListner {
                        override fun onNegativeButtonTapped() { }

                        override fun onPositiveButtonTapped() {
                            viewModel.deletePoop(poop)
                        }
                    }
                )
                dialog.show(parentFragmentManager, "DeletePoopDialog")
                return true
            }
            else -> false
        }
    }

    /**
     * 引数を渡すため
     * シングルトンインスタンス生成
     */
    companion object {
        @JvmStatic
        fun newInstance(date: Long) =
            TheDayDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(AppArgKey.ARG_THE_DAY_KEY, date)
                }
            }
    }
}