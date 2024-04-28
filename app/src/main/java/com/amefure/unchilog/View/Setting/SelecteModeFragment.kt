package com.amefure.unchilog.View.Setting

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.amefure.unchilog.R
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.ViewModel.SettingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelecteModeFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()

    private var selecteMode: Int = 0

    private lateinit var simpleButton: Button
    private lateinit var detailButton: Button
    private lateinit var modeDescText: TextView
    private lateinit var modeDemoImage: ImageView

    private var tintSelectList: ColorStateList? = null
    private var textSelectColor: Int = 0
    private var tintUnSelectList: ColorStateList? = null
    private var textUnSelectColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selecte_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHeaderAction(view)

        simpleButton = view.findViewById(R.id.simple_button)
        detailButton = view.findViewById(R.id.detail_button)
        // toggleGroup.check(simpleButton.id) 選択状態にする
        modeDescText = view.findViewById(R.id.select_mode_desc)
        modeDemoImage = view.findViewById(R.id.select_mode_image)

        // 選択状態の背景色と文字色
        tintSelectList = ContextCompat.getColorStateList(this.requireContext(), R.color.ex_text)
        textSelectColor = ContextCompat.getColor(this.requireContext(), R.color.white)
        // 非選択状態の背景色と文字色
        tintUnSelectList = ContextCompat.getColorStateList(this.requireContext(), R.color.ex_gray)
        textUnSelectColor = ContextCompat.getColor(this.requireContext(), R.color.ex_text)


        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.observeEntryMode().collect { mode ->
                selecteMode = mode ?: 0
                if (mode == 0) {
                    selectSimpleMode()
                } else {
                    selectDetailMode()
                }
            }
        }

        simpleButton.setOnClickListener {
            selecteMode = 0
            selectSimpleMode()
        }

        detailButton.setOnClickListener {
            selecteMode = 1
            selectDetailMode()
        }

        val registerButton: Button = view.findViewById(R.id.register_button)

        registerButton.setOnClickListener {
            viewModel.saveEntryMode(selecteMode)

            var modeName = if (selecteMode == 0) "シンプル" else "詳細"
            val dialog = CustomNotifyDialogFragment.newInstance(
                title = getString(R.string.dialog_title_notice),
                msg = getString(R.string.dialog_msg_update_entry_mode, modeName),
                showPositive = true,
                showNegative = false
            )
            dialog.setOnTappedListner(
                object : CustomNotifyDialogFragment.onTappedListner {
                    override fun onNegativeButtonTapped() { }

                    override fun onPositiveButtonTapped() {
                        parentFragmentManager.beginTransaction().remove(this@SelecteModeFragment).commit()
                    }
                }
            )
            dialog.show(parentFragmentManager, "AppLockSuccess")
        }
    }

    /**
     * シンプルモードボタン押下時UI調整
     */
    private fun selectSimpleMode() {
        simpleButton.backgroundTintList = tintSelectList
        simpleButton.setTextColor(textSelectColor)

        detailButton.backgroundTintList = tintUnSelectList
        detailButton.setTextColor(textUnSelectColor)

        modeDescText.setText(getString(R.string.simple_mode_desc))
        val demoImage: Drawable? = ContextCompat.getDrawable(this.requireContext(), R.drawable.ss_simple_mode)
        modeDemoImage.setImageDrawable(demoImage)
    }

    /**
     * 詳細モードボタン押下時UI調整
     */
    private fun selectDetailMode() {
        detailButton.backgroundTintList = tintSelectList
        detailButton.setTextColor(textSelectColor)

        simpleButton.backgroundTintList = tintUnSelectList
        simpleButton.setTextColor(textUnSelectColor)

        modeDescText.setText(getString(R.string.detail_mode_desc))
        val demoImage: Drawable? = ContextCompat.getDrawable(this.requireContext(), R.drawable.ss_detail_mode)
        modeDemoImage.setImageDrawable(demoImage)
    }

    /**
     * ヘッダーボタンセットアップ
     * [LeftButton]：backButton
     * [RightButton]：非表示(GONE)
     */
    private fun setUpHeaderAction(view: View) {
        var header: ConstraintLayout = view.findViewById(R.id.include_header)
        val headerTitleButton: Button = header.findViewById(R.id.header_title_button)
        headerTitleButton.text = "登録モード"

        val leftButton: ImageButton = header.findViewById(R.id.left_button)
        leftButton.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        val rightButton: ImageButton = header.findViewById(R.id.right_button)
        rightButton.setImageDrawable(null)
        rightButton.isEnabled = false
    }
}