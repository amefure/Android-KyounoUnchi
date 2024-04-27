package com.amefure.unchilog.View.AppLock

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.amefure.unchilog.Model.Key.AppArgKey
import com.amefure.unchilog.R
import com.amefure.unchilog.View.Calendar.PoopCalendarFragment
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.ViewModel.AppLockViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class AppLockFragment : Fragment() {

    private val viewModel: AppLockViewModel by viewModels()

    private var inputFlag: Boolean = false

    private lateinit var number1: Button
    private lateinit var number2: Button
    private lateinit var number3: Button
    private lateinit var number4: Button
    private lateinit var number5: Button
    private lateinit var number6: Button
    private lateinit var number7: Button
    private lateinit var number8: Button
    private lateinit var number9: Button
    private lateinit var number0: Button
    private lateinit var numberBack: Button

    private lateinit var registerButton: Button
    private lateinit var loadingIcon: ProgressBar

    private lateinit var password1: TextView
    private lateinit var password2: TextView
    private lateinit var password3: TextView
    private lateinit var password4: TextView

    private var password: MutableList<Int> = mutableListOf()
    private var localPass: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            inputFlag = it.getBoolean(AppArgKey.APP_LOCK_FLAG_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_lock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!inputFlag) {
            lifecycleScope.launch {
                // ここはnullのはずがない
                localPass = viewModel.observeAppLockPassword().first() ?: 0
            }
        }

        setUpHeaderAction(view)

        number1 = view.findViewById(R.id.number_button1)
        number2 = view.findViewById(R.id.number_button2)
        number3 = view.findViewById(R.id.number_button3)
        number4 = view.findViewById(R.id.number_button4)
        number5 = view.findViewById(R.id.number_button5)
        number6 = view.findViewById(R.id.number_button6)
        number7 = view.findViewById(R.id.number_button7)
        number8 = view.findViewById(R.id.number_button8)
        number9 = view.findViewById(R.id.number_button9)
        number0 = view.findViewById(R.id.number_button0)
        numberBack = view.findViewById(R.id.number_button_back)

        password1 = view.findViewById(R.id.password_label1)
        password2 = view.findViewById(R.id.password_label2)
        password3 = view.findViewById(R.id.password_label3)
        password4 = view.findViewById(R.id.password_label4)

        registerButton = view.findViewById(R.id.register_button)
        loadingIcon = view.findViewById(R.id.loading_icon)

        // Inputの時のみ表示する
        registerButton.visibility = if (inputFlag) View.VISIBLE else View.GONE
        registerButton.isEnabled = false

        // Input：パスワード登録ボタン
        registerButton.setOnClickListener {
            if (password.size != 4) return@setOnClickListener
            var pass = password.joinToString("").toInt()
            viewModel.saveAppLockPassword(pass)
            val dialog = CustomNotifyDialogFragment.newInstance(
                title = getString(R.string.dialog_title_notice),
                msg = getString(R.string.dialog_save_password),
                showPositive = true,
                showNegative = false
            )
            dialog.setOnTappedListner(
                object : CustomNotifyDialogFragment.onTappedListner {
                    override fun onNegativeButtonTapped() { }

                    override fun onPositiveButtonTapped() {
                        parentFragmentManager.popBackStack()
                    }
                }
            )
            dialog.show(parentFragmentManager, "AppLockSuccess")
        }

        number1.setOnClickListener {
            clickNumber(1)
        }
        number2.setOnClickListener {
            clickNumber(2)
        }
        number3.setOnClickListener {
            clickNumber(3)
        }
        number4.setOnClickListener {
            clickNumber(4)
        }
        number5.setOnClickListener {
            clickNumber(5)
        }
        number6.setOnClickListener {
            clickNumber(6)
        }
        number7.setOnClickListener {
            clickNumber(7)
        }
        number8.setOnClickListener {
            clickNumber(8)
        }
        number9.setOnClickListener {
            clickNumber(9)
        }
        number0.setOnClickListener {
            clickNumber(0)
        }
        numberBack.setOnClickListener {
            if (password.size != 0) {
                password.removeAt(password.size - 1)
                when(password.size) {
                    0 -> password1.text = getString(R.string.app_lock_password_placeholder)
                    1 -> password2.text = getString(R.string.app_lock_password_placeholder)
                    2 -> password3.text = getString(R.string.app_lock_password_placeholder)
                    3 -> password4.text = getString(R.string.app_lock_password_placeholder)
                }
            }
        }
    }

    /**
     * パスワードをチェックして正しければ画面遷移
     * 間違っていればダイアログ表示
     */
    private fun checkPassword() {
        loadingIcon.visibility = View.VISIBLE
        val handler = Handler(Looper.getMainLooper())
        // ローディング処理を表示するため2秒間遅延
        handler.postDelayed({
            loadingIcon.visibility = View.GONE
            var pass = password.joinToString("").toInt()
            if (localPass == pass) {
                parentFragmentManager.beginTransaction().apply {
                    add(R.id.main_frame, PoopCalendarFragment())
                    addToBackStack(null)
                    commit()
                }
            } else {
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_different_password),
                    showPositive = true,
                    showNegative = false
                )
                dialog.setOnTappedListner(
                    object : CustomNotifyDialogFragment.onTappedListner {
                        override fun onNegativeButtonTapped() { }

                        override fun onPositiveButtonTapped() {
                            password = mutableListOf()
                            var placeholder = getString(R.string.app_lock_password_placeholder)
                            password1.text = placeholder
                            password2.text = placeholder
                            password3.text = placeholder
                            password4.text = placeholder
                        }
                    }
                )
                dialog.show(parentFragmentManager, "AppLockSuccess")
            }
        }, 2000)
    }


    /**
     * カスタムキーボードタップ処理
     */
    private fun clickNumber(num: Int) {
        if (password.size != 4) {
            password.add(num)
            when(password.size) {
                1 -> password1.text = getString(R.string.app_lock_password_fill)
                2 -> password2.text = getString(R.string.app_lock_password_fill)
                3 -> password3.text = getString(R.string.app_lock_password_fill)
                4 -> {
                    password4.text = getString(R.string.app_lock_password_fill)
                    if (inputFlag) {
                        registerButton.isEnabled = true
                    } else {
                        checkPassword()
                    }
                }
            }
        }
    }

    /**
     * ヘッダーボタンセットアップ
     * [LeftButton]：非表示(GONE)
     * [RightButton]：非表示(GONE)
     */
    private fun setUpHeaderAction(view: View) {
        val headerView: ConstraintLayout = view.findViewById(R.id.include_header)
        val leftButton: ImageButton = headerView.findViewById(R.id.left_button)
        val headerTitleButton: Button = headerView.findViewById(R.id.header_title_button)
        headerTitleButton.text = "アプリロック"

        if (inputFlag) {
            leftButton.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        } else {
            leftButton.setImageDrawable(null)
            leftButton.isEnabled = false
        }

        val rightButton: ImageButton = headerView.findViewById(R.id.right_button)
        rightButton.setImageDrawable(null)
        rightButton.isEnabled = false
    }

    /**
     * Input か 初期ロックか
     */
    companion object {
        @JvmStatic
        fun newInstance(flag: Boolean) =
            AppLockFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(AppArgKey.APP_LOCK_FLAG_KEY, flag)
                }
            }
    }
}