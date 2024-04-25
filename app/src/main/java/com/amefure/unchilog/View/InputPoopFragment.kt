package com.amefure.unchilog.View

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import com.amefure.unchilog.Model.Room.PoopColor
import com.amefure.unchilog.Model.Room.PoopShape
import com.amefure.unchilog.R
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.ViewModel.PoopViewModel
import java.util.Date


class InputPoopFragment : Fragment() {


    private var selectTime: PoopColor = PoopColor.UNDEFINED
    private var selectColor: PoopColor = PoopColor.BROWN
    private var selectShape: PoopShape = PoopShape.NORMAL

    private val viewModel: PoopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_input_poop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpHeaderAction(view)
        setShapeView(view)
        setColorView(view)

        val timePicker: TimePicker = view.findViewById(R.id.timePicker)
        // TimePickerの値が変化したときに呼び出されるリスナーを設定
        timePicker.setOnTimeChangedListener { timePicker, hour, minutes ->

        }
    }

    /**
     * ヘッダーボタンセットアップ
     * [LeftButton]：backButton
     * [RightButton]：登録処理ボタン
     */
    private fun setUpHeaderAction(view: View) {
        var header: ConstraintLayout = view.findViewById(R.id.include_header)
        val yearAndMonthButton: Button = header.findViewById(R.id.header_title_button)
        yearAndMonthButton.text = "うんちの記録"

        val leftButton: ImageButton = header.findViewById(R.id.left_button)
        leftButton.setOnClickListener {
            closedKeyBoard()
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        val rightButton: ImageButton = header.findViewById(R.id.right_button)
        rightButton.setOnClickListener {
            closedKeyBoard()
            viewModel.insertPoop(
                color = selectColor.id,
                shape = selectShape.id,
                volume = 0,
                memo = "",
                createdAt = Date()
            )

            val dialog = CustomNotifyDialogFragment.newInstance(
                title = getString(R.string.dialog_title_notice),
                msg = getString(R.string.dialog_msg_success_entry_poop),
                showPositive = true,
                showNegative = false,
            )
            dialog.setOnTappedListner(
                object : CustomNotifyDialogFragment.onTappedListner {
                    override fun onNegativeButtonTapped() { }

                    override fun onPositiveButtonTapped() {
                        parentFragmentManager.beginTransaction().remove(this@InputPoopFragment).commit()
                    }
                }
            )
            dialog.show(parentFragmentManager, "SuccessEntryPoopDialog")
        }
    }


    public fun setShapeView(view: View) {
        val poopShapeKorokoro: ImageButton = view.findViewById(R.id.poop_shape_korokoro)
        val poopShapeSemiKorokoro: ImageButton = view.findViewById(R.id.poop_shape_semi_korokoro)
        val poopShapeNormal: ImageButton = view.findViewById(R.id.poop_shape_normal)
        val poopShapeLiquid: ImageButton = view.findViewById(R.id.poop_shape_semi_liquid)
        val poopShapeSemiLiquid: ImageButton = view.findViewById(R.id.poop_shape_liquid)

        poopShapeKorokoro.setOnClickListener {
            selectShape = PoopShape.KOROKORO
        }
        poopShapeSemiKorokoro.setOnClickListener {
            selectShape = PoopShape.SEMIKOROKORO
        }
        poopShapeNormal.setOnClickListener {
            selectShape = PoopShape.NORMAL
        }
        poopShapeLiquid.setOnClickListener {
            selectShape = PoopShape.SEMILIQUID
        }
        poopShapeKorokoro.setOnClickListener {
            selectShape = PoopShape.LIQUID
        }
    }


    public fun setColorView(view: View) {
        val selectYellowishBrownButton: View = view.findViewById(R.id.select_yellowish_brown_button)
        val selectYellowButton: View = view.findViewById(R.id.select_yellow_button)
        val selectBrownButton: View = view.findViewById(R.id.select_brown_button)
        val selectDarkBrownButton: View = view.findViewById(R.id.select_dark_brown_button)
        val selectBlackButton: View = view.findViewById(R.id.select_black_button)
        val selectGreenButton: View = view.findViewById(R.id.select_green_button)
        val selectRedButton: View = view.findViewById(R.id.select_red_button)
        val selectGrayishWhiteButton: View = view.findViewById(R.id.select_grayish_white_button)



        selectYellowishBrownButton.setOnClickListener {
            selectColor = PoopColor.YELLOWISHBROWN
        }
        selectYellowButton.setOnClickListener {
            selectColor = PoopColor.YELLOW
        }
        selectBrownButton.setOnClickListener {
            selectColor = PoopColor.BROWN
        }
        selectDarkBrownButton.setOnClickListener {
            selectColor = PoopColor.DARKBROWN
        }
        selectBlackButton.setOnClickListener {
            selectColor = PoopColor.BLACK
        }
        selectGreenButton.setOnClickListener {
            selectColor = PoopColor.GREEN
        }
        selectRedButton.setOnClickListener {
            selectColor = PoopColor.RED
        }
        selectGrayishWhiteButton.setOnClickListener {
            selectColor = PoopColor.GRAYISHWHITE
        }
    }

    /**
     * キーボードを閉じるメソッド
     */
    private fun closedKeyBoard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


}