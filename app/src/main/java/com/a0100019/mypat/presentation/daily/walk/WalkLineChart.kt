//package com.a0100019.mypat.presentation.daily.walk
//
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.width
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import co.yml.charts.axis.AxisData
//import co.yml.charts.common.model.Point
//import co.yml.charts.ui.linechart.LineChart
//import co.yml.charts.ui.linechart.model.GridLines
//import co.yml.charts.ui.linechart.model.IntersectionPoint
//import co.yml.charts.ui.linechart.model.Line
//import co.yml.charts.ui.linechart.model.LineChartData
//import co.yml.charts.ui.linechart.model.LinePlotData
//import co.yml.charts.ui.linechart.model.LineStyle
//import co.yml.charts.ui.linechart.model.LineType
//import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
//import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
//import co.yml.charts.ui.linechart.model.ShadowUnderLine
//import com.a0100019.mypat.data.room.walk.Walk
//
//
//@Composable
//fun WalkLineChart(
//    walkDataList: List<Walk>,
//    todayWalk: Int,
//    modifier: Modifier = Modifier,
//    mode: String
//) {
//
//    //y라벨 개수
//    val steps = 4
//
//    val pointsData = listOf(Point(0f, todayWalk.toFloat())) + // 첫 번째 데이터 추가
//            walkDataList.mapIndexed { index, walk ->
//                Point((index + 1).toFloat(), walk.count.toFloat()) // index+1부터 시작
//            }
//
//    val xAxisData = AxisData.Builder()
//        .axisStepSize(50.dp)
//        .labelAndAxisLinePadding(20.dp)
//        .backgroundColor(Color.Transparent)
//        .steps(pointsData.size - 1)
//        .labelData { i ->
//            if (i == 0) {
//                "today"
//            } else {
//                if(mode == "일"){
//                    walkDataList.getOrNull(i - 1)?.date?.let { date ->
//                        val parts = date.split("-") // "yyyy-MM-dd"를 "-" 기준으로 나누기
//                        "${parts[1].toInt()}/${parts[2].toInt()}" // "MM/dd" 형식으로 변환
//                    } ?: "" // 리스트 범위를 벗어나면 빈 문자열 반환}}
//            } else {
//                    walkDataList.getOrNull(i - 1)?.date ?: "" // 리스트 범위를 벗어나면 빈 문자열 반환}}
//                }
//            }
//        }
//        .labelAndAxisLinePadding(15.dp)
//        .axisLineColor(MaterialTheme.colorScheme.tertiary)
//        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
//        .build()
//
//    val yAxisData = AxisData.Builder()
//        .steps(steps)
//        .labelAndAxisLinePadding(15.dp)
//        .backgroundColor(Color.Transparent)
//        .labelAndAxisLinePadding(20.dp)
//        .labelData { i ->
//            when (i) {
//                0 -> "0"
//                1 -> "25%"
//                2 -> "50%"
//                3 -> "75%"
//                4 -> "최고"
//                else -> ""
//            }
//        }
//        .axisLineColor(MaterialTheme.colorScheme.tertiary)
//        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
//        .build()
//
//    val lineChartData = LineChartData(
//        linePlotData = LinePlotData(
//            lines = listOf(
//                Line(
//                    dataPoints = pointsData,
//                    LineStyle(
//                        color = MaterialTheme.colorScheme.tertiary,
//                        lineType = LineType.SmoothCurve(isDotted = false)
//                    ),
//                    IntersectionPoint(
//                        color = MaterialTheme.colorScheme.tertiary
//                    ),
//                    SelectionHighlightPoint(color = MaterialTheme.colorScheme.primary),
//                    ShadowUnderLine(
//                        alpha = 0.5f,
//                        brush = Brush.verticalGradient(
//                            colors = listOf(
//                                MaterialTheme.colorScheme.inversePrimary,
//                                Color.Transparent
//                            )
//                        )
//                    ),
//                    SelectionHighlightPopUp(
//                        popUpLabel = { x, y ->
//                            if (x == 0f) {
//                                "오늘: ${y.toInt()} 걸음"
//                            } else {
//                                if(mode == "일") {
//                                    walkDataList.getOrNull(x.toInt() - 1)?.let { walk ->
//                                        val parts = walk.date.split("-")
//                                        val dateLabel = "${parts[1].toInt()}/${parts[2].toInt()}"
//                                        "$dateLabel: ${y.toInt()} 걸음"
//                                    } ?: "${y.toInt()} 걸음"
//                                } else {
//                                    "평균 : ${y.toInt()} 걸음"
//                                }
//                            }
//                        }
//                    )
//
//                )
//            )
//        ),
//        backgroundColor = MaterialTheme.colorScheme.surface,
//        xAxisData = xAxisData,
//        yAxisData = yAxisData,
//        gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant)
//    )
//
//    LineChart(
//        modifier = modifier,
//        lineChartData = lineChartData,
//    )
//}
//
//
//@Composable
//@Preview(showBackground = true)
//fun WalkLineChartPreview() {
//    WalkLineChart(
//        listOf(
//            Walk(date = "2924-11-11", count = 1000)
//        ),
//        todayWalk = 500,
//        mode = "일"
//    )
//}
//
