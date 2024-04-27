package com.amefure.unchilog.View

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.amefure.unchilog.Model.Key.AppArgKey
import com.amefure.unchilog.Model.Room.Poop
import com.amefure.unchilog.Model.Room.PoopColor
import com.amefure.unchilog.Model.Room.PoopShape
import com.amefure.unchilog.Model.Room.PoopVolume
import com.amefure.unchilog.R
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.View.TheDayDetail.TheDayDetailFragment
import com.amefure.unchilog.ViewModel.PoopViewModel
import java.util.Calendar
import java.util.Date


class InputPoopFragment : Fragment() {

    private var selectColor: PoopColor = PoopColor.BROWN
    private var selectShape: PoopShape = PoopShape.NORMAL

    private val viewModel: PoopViewModel by viewModels()

    // 色
    private lateinit var selectYellowishBrownButton: View
    private lateinit var selectYellowButton: View
    private lateinit var selectBrownButton: View
    private lateinit var selectDarkBrownButton: View
    private lateinit var selectBlackButton: View
    private lateinit var selectGreenButton: View
    private lateinit var selectRedButton: View
    private lateinit var selectGrayishWhiteButton: View

    // 形
    private lateinit var poopShapeKorokoro: ImageButton
    private lateinit var poopShapeSemiKorokoro: ImageButton
    private lateinit var poopShapeNormal: ImageButton
    private lateinit var poopShapeLiquid: ImageButton
    private lateinit var poopShapeSemiLiquid: ImageButton

    // 量
    private lateinit var volumeBar: SeekBar
    private lateinit var volumeLabel: TextView

    // 時間とMEMO
    private lateinit var timePicker: TimePicker
    private lateinit var memoText: EditText

    private var dateStr: Long = 0
    private var poopId: Int = 0
    private var poop: Poop? = null
    private var date: Date = Date()

    // Shape選択時のグレー背景カラーリソース格納用
    private var shapeSelectColorValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dateStr = it.getLong(AppArgKey.ARG_INPUT_THE_DAY_KEY)
            poopId = it.getInt(AppArgKey.ARG_INPUT_POOP_ID_KEY)
            date = Date(dateStr)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_input_poop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timePicker = view.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)
        memoText = view.findViewById(R.id.memo_edit)

        // Shape選択時のグレー背景カラーリソース格納
        shapeSelectColorValue = ContextCompat.getColor(this.requireContext(), R.color.ex_gray)

        setUpHeaderAction(view)
        setColorView(view)
        setShapeView(view)
        setVolumeView(view)

        if (poopId != 0) {
            this.viewModel.fetchSinglePoop(poopId) {
                setUpUpdatePoopUI(it)
                poop = it
            }
        } else {
            setUpInitPoopUI()
        }
    }

    /**
     * ヘッダーボタンセットアップ
     * [LeftButton]：backButton
     * [RightButton]：登録処理ボタン
     */
    private fun setUpHeaderAction(view: View) {
        var header: ConstraintLayout = view.findViewById(R.id.include_header)
        val headerTitleButton: Button = header.findViewById(R.id.header_title_button)
        headerTitleButton.text = "うんちの記録"

        val leftButton: ImageButton = header.findViewById(R.id.left_button)
        leftButton.setOnClickListener {
            closedKeyBoard()
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        val rightButton: ImageButton = header.findViewById(R.id.right_button)
        val checkIcon: Drawable? = ResourcesCompat.getDrawable(getResources(), R.drawable.button_check, null)
        rightButton.setImageDrawable(checkIcon)
        rightButton.setOnClickListener {
            registerAction()
        }
    }

    /**
     * Poop登録処理
     */
    private fun registerAction() {
        closedKeyBoard()

        // 現在の日付の時間をTimePickerで選択したものに変更
        var now = date
        val calendar = Calendar.getInstance()
        calendar.time = now
        val hour = timePicker.hour
        val minute = timePicker.minute
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val updatedDate = calendar.time

        if (poop != null) {
            viewModel.updatePoop(
                id = poop!!.id,
                color = selectColor.id,
                shape = selectShape.id,
                volume = volumeBar.progress,
                memo = memoText.text.toString(),
                createdAt = updatedDate
            )
        } else {
            viewModel.insertPoop(
                color = selectColor.id,
                shape = selectShape.id,
                volume = volumeBar.progress,
                memo = memoText.text.toString(),
                createdAt = updatedDate
            )
        }

        val dialog = CustomNotifyDialogFragment.newInstance(
            title = getString(R.string.dialog_title_notice),
            msg =  poop?.let { getString(R.string.dialog_msg_success_entry_poop) } ?: getString(R.string.dialog_msg_update_entry_poop),
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

    /**
     * うんちの量選択処理
     */
    public fun setVolumeView(view: View) {
        volumeBar = view.findViewById(R.id.volume_bar)
        volumeLabel = view.findViewById(R.id.volume_label)
        volumeBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                // プログレス値が変化しているとき
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    volumeLabel.text = PoopVolume.getName(p1)
                }
                // スライド開始
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                // スライド終了
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            }
        )
    }


    /**
     * うんちの形選択処理
     */
    public fun setShapeView(view: View) {
        poopShapeKorokoro = view.findViewById(R.id.poop_shape_korokoro)
        poopShapeSemiKorokoro = view.findViewById(R.id.poop_shape_semi_korokoro)
        poopShapeNormal = view.findViewById(R.id.poop_shape_normal)
        poopShapeLiquid = view.findViewById(R.id.poop_shape_semi_liquid)
        poopShapeSemiLiquid = view.findViewById(R.id.poop_shape_liquid)

        poopShapeKorokoro.setOnClickListener {
            selectShape = PoopShape.KOROKORO
            resetSelectShapeButton()
            poopShapeKorokoro.setBackgroundColor(shapeSelectColorValue)
        }
        poopShapeSemiKorokoro.setOnClickListener {
            selectShape = PoopShape.SEMIKOROKORO
            resetSelectShapeButton()
            poopShapeSemiKorokoro.setBackgroundColor(shapeSelectColorValue)
        }
        poopShapeNormal.setOnClickListener {
            selectShape = PoopShape.NORMAL
            resetSelectShapeButton()
            poopShapeNormal.setBackgroundColor(shapeSelectColorValue)
        }
        poopShapeLiquid.setOnClickListener {
            selectShape = PoopShape.SEMILIQUID
            resetSelectShapeButton()
            poopShapeLiquid.setBackgroundColor(shapeSelectColorValue)
        }
        poopShapeSemiLiquid.setOnClickListener {
            selectShape = PoopShape.LIQUID
            resetSelectShapeButton()
            poopShapeSemiLiquid.setBackgroundColor(shapeSelectColorValue)
        }
    }

    /**
     * 形：選択状態リセット
     */
    private fun resetSelectShapeButton() {
        poopShapeKorokoro.setBackgroundColor(Color.WHITE)
        poopShapeSemiKorokoro.setBackgroundColor(Color.WHITE)
        poopShapeNormal.setBackgroundColor(Color.WHITE)
        poopShapeLiquid.setBackgroundColor(Color.WHITE)
        poopShapeSemiLiquid.setBackgroundColor(Color.WHITE)
    }


    /**
     * うんち色選択処理
     */
    public fun setColorView(view: View) {
        selectYellowishBrownButton = view.findViewById(R.id.select_yellowish_brown_button)
        selectYellowButton = view.findViewById(R.id.select_yellow_button)
        selectBrownButton = view.findViewById(R.id.select_brown_button)
        selectDarkBrownButton = view.findViewById(R.id.select_dark_brown_button)
        selectBlackButton = view.findViewById(R.id.select_black_button)
        selectGreenButton = view.findViewById(R.id.select_green_button)
        selectRedButton = view.findViewById(R.id.select_red_button)
        selectGrayishWhiteButton = view.findViewById(R.id.select_grayish_white_button)

        selectYellowishBrownButton.setOnClickListener {
            selectColor = PoopColor.YELLOWISHBROWN
            resetSelectColorButton()
            selectYellowishBrownButton.alpha = 0.5f
        }
        selectYellowButton.setOnClickListener {
            selectColor = PoopColor.YELLOW
            resetSelectColorButton()
            selectYellowButton.alpha = 0.5f
        }
        selectBrownButton.setOnClickListener {
            selectColor = PoopColor.BROWN
            resetSelectColorButton()
            selectBrownButton.alpha = 0.5f
        }
        selectDarkBrownButton.setOnClickListener {
            selectColor = PoopColor.DARKBROWN
            resetSelectColorButton()
            selectDarkBrownButton.alpha = 0.5f
        }
        selectBlackButton.setOnClickListener {
            selectColor = PoopColor.BLACK
            resetSelectColorButton()
            selectBlackButton.alpha = 0.5f
        }
        selectGreenButton.setOnClickListener {
            selectColor = PoopColor.GREEN
            resetSelectColorButton()
            selectGreenButton.alpha = 0.5f
        }
        selectRedButton.setOnClickListener {
            selectColor = PoopColor.RED
            resetSelectColorButton()
            selectRedButton.alpha = 0.5f
        }
        selectGrayishWhiteButton.setOnClickListener {
            selectColor = PoopColor.GRAYISHWHITE
            resetSelectColorButton()
            selectGrayishWhiteButton.alpha = 0.5f
        }
    }

    /**
     * 色：選択状態リセット
     */
    private fun resetSelectColorButton() {
        selectYellowishBrownButton.alpha = 1f
        selectYellowButton.alpha = 1f
        selectBrownButton.alpha = 1f
        selectDarkBrownButton.alpha = 1f
        selectBlackButton.alpha = 1f
        selectGreenButton.alpha = 1f
        selectRedButton.alpha = 1f
        selectGrayishWhiteButton.alpha = 1f
    }

    /**
     * 新規用PoopUIセット
     */
    private fun setUpInitPoopUI() {
        // 色
        selectBrownButton.alpha = 0.5f

        // 形
        poopShapeNormal.setBackgroundColor(shapeSelectColorValue)
    }

    /**
     * 更新用PoopUIセット
     */
    private fun setUpUpdatePoopUI(poop: Poop) {

        // 時間
        val calendar = Calendar.getInstance()
        calendar.time = poop.createdAt
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        timePicker.hour = hour
        timePicker.minute = minute

        // 色
        var poopColor = PoopColor.getPoopColor(poop.color)
        selectColor = poopColor
        resetSelectColorButton()
        when(poopColor) {
            PoopColor.UNDEFINED -> selectYellowishBrownButton.alpha = 0.5f
            PoopColor.YELLOWISHBROWN -> selectYellowishBrownButton.alpha = 0.5f
            PoopColor.YELLOW -> selectYellowButton.alpha = 0.5f
            PoopColor.BROWN -> selectBrownButton.alpha = 0.5f
            PoopColor.DARKBROWN -> selectDarkBrownButton.alpha = 0.5f
            PoopColor.BLACK -> selectBlackButton.alpha = 0.5f
            PoopColor.GREEN -> selectGreenButton.alpha = 0.5f
            PoopColor.RED -> selectRedButton.alpha = 0.5f
            PoopColor.GRAYISHWHITE -> selectGrayishWhiteButton.alpha = 0.5f
        }

        // 形
        var poopShape = PoopShape.getPoopShape(poop.shape)
        selectShape = poopShape
        when(poopShape) {
            PoopShape.UNDEFINED -> poopShapeKorokoro.setBackgroundColor(shapeSelectColorValue)
            PoopShape.KOROKORO -> poopShapeKorokoro.setBackgroundColor(shapeSelectColorValue)
            PoopShape.SEMIKOROKORO -> poopShapeSemiKorokoro.setBackgroundColor(shapeSelectColorValue)
            PoopShape.NORMAL -> poopShapeNormal.setBackgroundColor(shapeSelectColorValue)
            PoopShape.SEMILIQUID -> poopShapeSemiLiquid.setBackgroundColor(shapeSelectColorValue)
            PoopShape.LIQUID -> poopShapeLiquid.setBackgroundColor(shapeSelectColorValue)
        }

        // 量
        volumeLabel.text = PoopVolume.getName(poop.volume)
        volumeBar.progress = poop.volume

        // Memo
        memoText.setText(poop.memo)
    }

    /**
     * キーボードを閉じるメソッド
     */
    private fun closedKeyBoard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    /**
     * 引数を渡すため
     * シングルトンインスタンス生成
     */
    companion object {
        @JvmStatic
        fun newInstance(date: Long) =
            InputPoopFragment().apply {
                arguments = Bundle().apply {
                    putLong(AppArgKey.ARG_INPUT_THE_DAY_KEY, date)
                }
            }

        @JvmStatic
        fun newEditInstance(date: Long, poopId: Int) =
            InputPoopFragment().apply {
                arguments = Bundle().apply {
                    putLong(AppArgKey.ARG_INPUT_THE_DAY_KEY, date)
                    putInt(AppArgKey.ARG_INPUT_POOP_ID_KEY, poopId)
                }
            }
    }
}