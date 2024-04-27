package com.amefure.unchilog.View.Setting

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.amefure.unchilog.Model.Config.AppURL
import com.amefure.unchilog.R
import com.amefure.unchilog.View.AppLock.AppLockFragment
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.ViewModel.SettingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHeaderAction(view)

        val appLockButton: Button = view.findViewById(R.id.app_lock_button)

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.observeAppLockPassword().collect {
                if (it == null || it == 0) {
                    appLockButton.text = "OFF"
                    var bg: Drawable? = ResourcesCompat.getDrawable(getResources(), R.drawable.app_lock_button_off_background, null)
                    appLockButton.background = bg
                } else {
                    appLockButton.text = "ON"
                    var bg: Drawable? = ResourcesCompat.getDrawable(getResources(), R.drawable.app_lock_button_on_background, null)
                    appLockButton.background = bg
                }
            }
        }

        appLockButton.setOnClickListener {
            if (appLockButton.text == "ON") {
                viewModel.saveAppLockPassword(0)
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_release_password),
                    showPositive = true,
                    showNegative = false
                )
                dialog.show(parentFragmentManager, "AppLockSuccess")
            } else {
                parentFragmentManager.beginTransaction().apply {
                    add(R.id.main_frame, AppLockFragment.newInstance(true))
                    addToBackStack(null)
                    commit()
                }
            }
        }

        val inquiryRow: LinearLayout = view.findViewById(R.id.link_inquiry_row)
        val termsOfServiceRow: LinearLayout = view.findViewById(R.id.link_terms_of_service_row)


        // お問い合わせリンク
        inquiryRow.setOnClickListener {
            val uri = Uri.parse(AppURL.INQUIRY_URL)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // お問い合わせリンク
        termsOfServiceRow.setOnClickListener {
            val uri = Uri.parse(AppURL.TERMS_OF_SERVICE_URL)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    /**
     * ヘッダーボタンセットアップ
     * [LeftButton]：backButton
     * [RightButton]：非表示(GONE)
     */
    private fun setUpHeaderAction(view: View) {
        var header: ConstraintLayout = view.findViewById(R.id.include_header)
        val headerTitleButton: Button = header.findViewById(R.id.header_title_button)
        headerTitleButton.text = "設定"

        val leftButton: ImageButton = header.findViewById(R.id.left_button)
        leftButton.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        val rightButton: ImageButton = header.findViewById(R.id.right_button)
        rightButton.setImageDrawable(null)
        rightButton.isEnabled = false
    }
}