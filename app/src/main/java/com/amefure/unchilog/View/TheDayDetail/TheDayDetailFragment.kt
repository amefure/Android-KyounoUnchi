package com.amefure.unchilog.View.TheDayDetail

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.amefure.unchilog.ViewModel.TheDayDetailViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class TheDayDetailFragment : Fragment() , PopupMenu.OnMenuItemClickListener {

    private var dateStr: Long = 0
    private var date: Date = Date()

    private var selectPoop: Poop? = null
    private var selectMode: Int = 0

    private val poopViewModel: PoopViewModel by viewModels()
    private val viewModel: TheDayDetailViewModel by viewModels()

    private var popupMenu: PopupMenu? = null

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

        // 広告の読み込み
        var adView: AdView = view.findViewById(R.id.adView)
        adView.loadAd(AdRequest.Builder().build())

        poopViewModel.fetchAllPoops()
        setUpHeaderAction(view)
        setUpFooterAction(view)
        setUpRecycleView(view)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.observeEntryMode().collect { mode ->
                selectMode = mode ?: 0
            }
        }
    }

    /**
     * ポップアップメニューを表示する
     * [view]には表示したいViewを渡す
     */
    private fun showPopupMenu(view: View) {
        popupMenu = PopupMenu(context, view)
        popupMenu?.let {
            it.inflate(R.menu.poop_row_menu)
            it.setOnMenuItemClickListener(this)
            it.show()
        }
    }


    /**
     * グリッドレイアウトリサイクルビューセットアップ
     * 1.月の日付
     * 2.曜日
     */
    private fun setUpRecycleView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.poop_recycle_layout)
        val messageLayout: ConstraintLayout = view.findViewById(R.id.include_message)
        poopViewModel.poops.observe(viewLifecycleOwner) { poops ->
            val filteringList = poops.filter  { DateFormatUtility.isSameDate(it.createdAt, date) }
            if (filteringList.size != 0) {
                recyclerView.visibility = View.VISIBLE
                messageLayout.visibility = View.GONE
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
            if (selectMode == 0) {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val createdAt = DateFormatUtility.getSetTimeDate(date, hour, minute)
                poopViewModel.insertPoop(createdAt = createdAt)
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
                popupMenu?.let { it.dismiss() }
                val poop = selectPoop ?:return true
                parentFragmentManager.beginTransaction().apply {
                    add(R.id.main_frame, InputPoopFragment.newEditInstance(dateStr, poop.id))
                    addToBackStack(null)
                    commit()
                }
                return true
            }
            R.id.menu_delete -> {
                popupMenu?.let { it.dismiss() }
                val poop = selectPoop ?:return true
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_delete_poop),
                    showPositive = true,
                    showNegative = true
                )
                dialog.setOnTappedListner(
                    object : CustomNotifyDialogFragment.onTappedListner {
                        override fun onNegativeButtonTapped() {
                            dialog.dismiss()
                        }

                        override fun onPositiveButtonTapped() {
                            poopViewModel.deletePoop(poop)
                        }
                    }
                )
                dialog.show(parentFragmentManager, "DeletePoopDialog")
                return true
            }
            R.id.menu_show_memo -> {
                val poop = selectPoop ?:return true
                val msg = if (poop.memo != "") poop.memo else "MEMOがありません。"
                AlertDialog.Builder(this.requireContext())
                    .setMessage(msg)
                    .setPositiveButton("OK", { dialog, id ->
                    })
                    .show()
                return true
            }
            R.id.menu_close -> {
                popupMenu?.let { it.dismiss() }
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