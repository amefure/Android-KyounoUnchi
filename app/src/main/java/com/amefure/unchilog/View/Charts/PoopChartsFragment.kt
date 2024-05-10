package com.amefure.unchilog.View.Charts

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import android.widget.Space
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.amefure.unchilog.R
import com.amefure.unchilog.Repository.SCCalender.SCCalenderRepository
import com.amefure.unchilog.Repository.SCCalender.fullname
import com.amefure.unchilog.Utility.DateFormatUtility
import com.amefure.unchilog.View.Calendar.RecycleViewSetting.PoopCalendarAdapter
import com.amefure.unchilog.View.Calendar.RecycleViewSetting.WeekAdapter
import com.amefure.unchilog.View.Dialog.CustomNotifyDialogFragment
import com.amefure.unchilog.View.Setting.SettingFragment
import com.amefure.unchilog.ViewModel.PoopViewModel
import com.amefure.unchilog.ViewModel.SCCalendarViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class PoopChartsFragment : Fragment() {

    private val poopViewModel: PoopViewModel by viewModels()
    private val calendarViewModel: SCCalendarViewModel by viewModels()

    private val dateFormat = SimpleDateFormat("d")

    // ②元データの準備
    private val data: MutableList<Pair<Float, Float>> = mutableListOf()
    private lateinit var lineChart: LineChart
    private lateinit var messageLayout: ConstraintLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_poop_charts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        poopViewModel.fetchAllPoops()
        setUpHeaderAction(view)

        lineChart = view.findViewById(R.id.poop_line_chart)
        messageLayout = view.findViewById(R.id.include_message)

        // 広告の読み込み
        var adView: AdView = view.findViewById(R.id.adView)
        adView.loadAd(AdRequest.Builder().build())

        initSettingViewCharts()

        poopViewModel.poops.observe(viewLifecycleOwner) { poops ->
            lifecycleScope.launch(Dispatchers.Main) {
                calendarViewModel.currentDates.collect { scdates ->

                    data.removeAll { true }
                    // 表示対象の日付を取得(月を識別したいだけなのでなんでも良い)
                    val targetDate = scdates.firstOrNull { it.date != null }?.date ?: Date()
                    // 該当の月のデータだけにフィルタリング
                    val currentMonthList = poops.filter { poop ->
                        DateFormatUtility.isSameMonthDate(targetDate, poop.createdAt)
                    }

                    if (currentMonthList.size != 0) {
                        scdates.forEach { scdate ->
                            scdate.date?.let { date ->
                                val count = poops.count { DateFormatUtility.isSameDate(it.createdAt, date) }.toFloat()
                                data.add(Pair(date.time.toFloat(), count))
                            }
                        }
                        setUpPoopGraph()
                    } else {
                        resetCharts()
                    }
                }
            }
        }
    }

    /**
     * グラフデータリセット
     */
    private fun resetCharts() {
        messageLayout.visibility = View.VISIBLE
        lineChart.visibility = View.GONE
        lineChart.data?.clearValues()
        lineChart.clear()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    /**
     * グラフUI初期設定
     */
    private fun initSettingViewCharts() {
        // 右下のDescription Labelを非表示
        lineChart.description.isEnabled = false
        // データがない場合のテキスト
        lineChart.setNoDataText("データがありません")
        // 上からのオフセット
        lineChart.extraBottomOffset = 30f
        // グラフ名ラベルを非表示
        lineChart.legend.isEnabled = false

        // ダブルタップでのズームを無効
        lineChart.isDoubleTapToZoomEnabled = false
        // タップでの点の選択を無効
        lineChart.isHighlightPerTapEnabled = false
        // ピンチでのズームを無効
        lineChart.setPinchZoom(false)

        // ------ X軸------
        // x軸のラベルをbottomに表示
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        // X軸ラベル間隔：1日
        lineChart.xAxis.granularity = 86400000f // 24h × 60m × 60s × 1000ms
        // X軸ラベルフォント
        lineChart.xAxis.typeface = Typeface.DEFAULT_BOLD
        // ラベルのテキストサイズを設定
        lineChart.xAxis.textSize = 12f
        // 日付に変換
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // valueをUnixタイムスタンプとして解釈し、日付や時刻に変換する
                return dateFormat.format(Date(value.toLong())) + "日"
            }
        }

        // ------ Y軸左側 ------
        // Y軸左側最小値
        lineChart.axisLeft.axisMinimum = 0f
        // Y軸左側ラベル間隔
        lineChart.axisLeft.granularity = 1f
        // Y軸左側ラベルフォント
        lineChart.axisLeft.typeface = Typeface.DEFAULT_BOLD
        // Y軸左側ラベルテキストサイズ
        lineChart.axisLeft.textSize = 12f


        // ------ Y軸右側 ------
        // Y軸右側ラベルを非表示
        lineChart.axisRight.isEnabled = false
    }

    /**
     * グラフ描画処理
     */
    private fun setUpPoopGraph() {
        if (data.size == 0) return

        messageLayout.visibility = View.GONE
        lineChart.visibility = View.VISIBLE

        val dataEntries = mutableListOf<Entry>()
        data.forEach {value ->
            if (value.second != 0f) {
                // 画像をリサイズ
                val icon: Drawable? = ResourcesCompat.getDrawable(getResources(), R.drawable.noface_poop, null)
                val bitmap = (icon as BitmapDrawable).bitmap
                val drawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 40, 40, true))
                val dataEntry = Entry(value.first, value.second, drawable)
                dataEntries.add(dataEntry)
            } else {
                val dataEntry = Entry(value.first, value.second)
                dataEntries.add(dataEntry)
            }
        }
        val lineDataSet = LineDataSet(dataEntries, "月毎のうんちグラフ")

        // グラフの線の太さ
        lineDataSet.lineWidth = 1.0f
        // ポインタ非表示
        lineDataSet.setDrawCircles(false)
        // 値非表示
        lineDataSet.setDrawValues(false)
        // グラフの色
        lineDataSet.colors = listOf(Color.BLACK)

        // データ格納
        lineChart.data = LineData(lineDataSet)

        // x軸のラベル数をデータの数にする
        lineChart.xAxis.labelCount = dataEntries.size - 1

        // データの最大表示範囲を制限：10日
        lineChart.setVisibleXRangeMaximum(86400000f * 10)
        // データの最小表示範囲を制限：10日
        lineChart.setVisibleXRangeMinimum(86400000f * 10)

        // グラフ描画(これがないとSCDateをcollectしている場合に動作しない)
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
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
        val todayButton: ImageButton = header.findViewById(R.id.today_button)

        headerSpace.visibility = View.VISIBLE
        forwardMonthButton.visibility = View.VISIBLE
        backMonthButton.visibility = View.VISIBLE
        todayButton.visibility = View.VISIBLE

        forwardMonthButton.setOnClickListener {
            var result = calendarViewModel.forwardMonth()
            if (!result) {
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_failed_calendar_out_range),
                    showPositive = true,
                    showNegative = false
                )
                dialog.show(parentFragmentManager, "CalendarOutRangeNameDialog")
            }
            updateHeaderYearAndMonth(view)
        }

        backMonthButton.setOnClickListener {
            var result = calendarViewModel.backMonth()
            if (!result) {
                val dialog = CustomNotifyDialogFragment.newInstance(
                    title = getString(R.string.dialog_title_notice),
                    msg = getString(R.string.dialog_msg_failed_calendar_out_range),
                    showPositive = true,
                    showNegative = false
                )
                dialog.show(parentFragmentManager, "CalendarOutRangeNameDialog")
            }
            updateHeaderYearAndMonth(view)
        }

        todayButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH) + 1
            calendarViewModel.moveYearAndMonthCalendar(year, month)
        }

        val leftButton: ImageButton = header.findViewById(R.id.left_button)
        leftButton.isEnabled = true
        leftButton.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        val rightButton: ImageButton = header.findViewById(R.id.right_button)
        rightButton.setImageDrawable(null)
        rightButton.isEnabled = false
        updateHeaderYearAndMonth(view)
    }

    /**
     * ヘッダーの年月日更新
     */
    private fun updateHeaderYearAndMonth(view: View) {
        val header: ConstraintLayout = view.findViewById(R.id.include_header)
        val headerTitleButton: Button = header.findViewById(R.id.header_title_button)
        // ヘッダーの[2024年4月]テキスト更新
        lifecycleScope.launch(Dispatchers.Main) {
            calendarViewModel.currentYearAndMonth.collect {
                it?.let {
                    headerTitleButton.text = it.fullname
                }
            }
        }
    }
}