package com.amefure.unchilog.View.Setting

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.Room.Poop
import com.amefure.unchilog.R
import com.amefure.unchilog.Utility.DateFormatUtility
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.View.Setting.RecycleViewSetting.InitWeekAdapter
import com.amefure.unchilog.View.Setting.RecycleViewSetting.InitWeekTouchListener
import com.amefure.unchilog.View.TheDayDetail.RecycleViewSetting.PoopRowAdapter
import com.amefure.unchilog.View.TheDayDetail.RecycleViewSetting.PoopRowTouchListener
import com.amefure.unchilog.ViewModel.SettingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SelectInitWeekFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()

    private var selectWeek: String = "日曜日"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_init_week, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHeaderAction(view)
        setUpRecycleView(view)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.observeInitWeek().collect { week ->
                if (week != null) {
                    selectWeek = week
                    setUpRecycleView(view)
                }
            }
        }

        val registerButton: Button = view.findViewById(R.id.register_button)

        registerButton.setOnClickListener {
            viewModel.saveInitWeek(selectWeek)

            val dialog = CustomNotifyDialogFragment.newInstance(
                title = getString(R.string.dialog_title_notice),
                msg = getString(R.string.dialog_msg_update_init_week, selectWeek),
                showPositive = true,
                showNegative = false
            )
            dialog.setOnTappedListner(
                object : CustomNotifyDialogFragment.onTappedListner {
                    override fun onNegativeButtonTapped() { }

                    override fun onPositiveButtonTapped() {
                        parentFragmentManager.beginTransaction().remove(this@SelectInitWeekFragment).commit()
                    }
                }
            )
            dialog.show(parentFragmentManager, "AppLockSuccess")
        }

    }

    /**
     * 曜日リスト
     */
    private fun setUpRecycleView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.init_week_layout)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        val itemTouchListener = InitWeekTouchListener()
        itemTouchListener.setOnTappedListener(
            object : InitWeekTouchListener.onTappedListener {
                override fun onTapped(week: String) {
                    selectWeek = week
                    recyclerView.adapter = InitWeekAdapter(selectWeek)
                }
            }
        )
        recyclerView.addOnItemTouchListener(itemTouchListener)
        recyclerView.adapter = InitWeekAdapter(selectWeek)

    }

    /**
     * ヘッダーボタンセットアップ
     * [LeftButton]：backButton
     * [RightButton]：非表示(GONE)
     */
    private fun setUpHeaderAction(view: View) {
        var header: ConstraintLayout = view.findViewById(R.id.include_header)
        val headerTitleButton: Button = header.findViewById(R.id.header_title_button)
        headerTitleButton.text = "週始まり"

        val leftButton: ImageButton = header.findViewById(R.id.left_button)
        leftButton.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        val rightButton: ImageButton = header.findViewById(R.id.right_button)
        rightButton.setImageDrawable(null)
        rightButton.isEnabled = false
    }
}