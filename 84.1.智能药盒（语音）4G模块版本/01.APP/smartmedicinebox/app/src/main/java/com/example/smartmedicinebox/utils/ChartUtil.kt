package com.example.smartmedicinebox.utils

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


/**
 * 图表工具类
 * @author CP
 */
object ChartUtil {
    private val xValues= mutableListOf<String>()
    /***
     * @brief 初始化折线图
     * @param chart 折线图控件
     */
    fun initChart(chart: LineChart) {
        // 开启文本描述
        chart.description.isEnabled = true
        chart.setNoDataText("暂时没有数据")
        // 开启触摸手势
        chart.setTouchEnabled(true)
        // 允许缩放和拖动
        chart.isDragEnabled = true //拖动
        chart.setScaleEnabled(true) //缩放
        chart.setDrawGridBackground(false)
        // 如果禁用，可以分别在x轴和y轴上进行缩放
        chart.setPinchZoom(true)
        val data = LineData()
        //设置数据颜色
        data.setValueTextColor(Color.WHITE)
        // 添加空数据
        chart.data = data
        val xl = chart.xAxis
        xl.position = XAxis.XAxisPosition.BOTTOM // 设置 x 轴显示在底部
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(false)
        xl.axisLineColor = Color.rgb(126, 198, 153)
        xl.textColor = Color.rgb(126, 198, 153)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true
        xl.granularity = 1f //每隔2个数据才显示
//        xl.valueFormatter = MyXAxisValueFormatter() // 设置X轴值格式化器

        val leftAxis = chart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.axisMaximum = 150f //最大条目
        leftAxis.axisMinimum = 0f //最小条目
        leftAxis.labelCount = 10 //设置最大分为几格
        leftAxis.setDrawGridLines(true)
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.axisLineColor = Color.rgb(126, 198, 153)
        leftAxis.textColor = Color.rgb(126, 198, 153)
        leftAxis.isEnabled = true
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
    }

    /***
     * @brief 向指定的折线控件添加数据
     * @param chart 控件
     * @param data 数据，这里为接收数据的
     */
    fun addEntry(chart: LineChart, values: Array<Float>) {
        var lineData = chart.data
        xValues.add(
            TimeCycle.getDateTime().split(" ")[1]
        )
        if (lineData != null && lineData.dataSetCount > 0) {
            values.forEachIndexed { index, value ->
                lineData.addEntry(
                    Entry(
                        lineData.getDataSetByIndex(index).entryCount.toFloat(),
                        value
                    ), index
                )
            }
            lineData.notifyDataChanged()
        } else {
            val listLineData: MutableList<ILineDataSet> = mutableListOf()
            for (i in values.indices) {
                listLineData.add(createLineData(i))
            }
            lineData = LineData(listLineData)
            chart.data = lineData
            addEntry(chart, values)
            return
        }
        // 更新 x 轴上的时间
        setXAxisValues(chart)
        // 限制x可见数目
        chart.setVisibleXRange(3f, 3f)
        // 提交数据数据改变，更新图表
        chart.notifyDataSetChanged()
        // 移动到最新条目
        if (chart.data.getDataSetByIndex(0).entryCount > 0) chart.moveViewToX(
            chart.data.getDataSetByIndex(
                0
            ).entryCount.toFloat()
        )
    }

    /***
     * @brief 创建折线数据
     * @param dataType 数据类型 1为环境湿度 2为土壤湿度 3为预测温度 4为预测湿度 其它为环境温度
     * @return LineDataSet
     */
    private fun createLineData(dataType: Int): LineDataSet {
        val lineData = when (dataType) {
            //
            1 -> {
                val set = LineDataSet(null, "心率")
                set.color = Color.rgb(50, 205, 50)
                set.setCircleColor(Color.rgb(50, 205, 50))
                set
            }
            //
//            2 -> {
//                val set = LineDataSet(null, "心率")
//                set.color = Color.rgb(205, 173, 0)
//                set.setCircleColor(Color.rgb(205, 173, 0))
//                set
//            }
            //
            else -> {
                val set = LineDataSet(null, "血氧")
                set.color = Color.rgb(205, 92, 92)
                set.setCircleColor(Color.rgb(205, 92, 92))
                set
            }
        }
        lineData.axisDependency = AxisDependency.LEFT
        lineData.lineWidth = 2f
        lineData.circleRadius = 4f
        lineData.setDrawValues(true) //是否显示数值
        lineData.setDrawCircles(true) //是否在每个节点上画上一个圆圈
        lineData.setDrawFilled(true) //是否在折线下方填满颜色
        lineData.fillAlpha = 65 //填充透明度
        lineData.mode = LineDataSet.Mode.LINEAR
        return lineData
    }

    /**
     * 设置x轴的值
     */
    private fun setXAxisValues(chart: LineChart) {
        if (xValues.size >= 5000) {
            xValues.removeAt(0)
        }
        //        xValues.add(getCurrentTime());
        chart.xAxis.valueFormatter =
            IndexAxisValueFormatter(xValues)
    }
    // 自定义X轴数值格式化器
    private class MyXAxisValueFormatter : ValueFormatter() {

        private var mFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            // 转换为中国时区
            TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-8"))
            mFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            // 将数值格式化为时间字符串
            return mFormat.format(Date(value.toLong() * 1000))
        }
    }
}