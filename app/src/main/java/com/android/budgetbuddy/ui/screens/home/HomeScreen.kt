package com.android.budgetbuddy.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.TransactionsState
import com.android.budgetbuddy.ui.composables.TransactionItem

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModelState: TransactionsState
) {
    val context = LocalContext.current
    val pointsData: MutableList<Point> = mutableListOf(Point(-1f, 0f))
    val xAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .steps(pointsData.size)
        .labelData { i -> i.toString() }
        .build()

    val yAxisData = AxisData.Builder()
        .backgroundColor(Color.Red)
        .build()


    val lineChartData = LineChartData(
        isZoomAllowed = false,
        paddingTop = 0.dp,
        bottomPadding = 0.dp,
        paddingRight= 0.dp,
        containerPaddingEnd = 0.dp,
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        lineType = LineType.SmoothCurve(isDotted = false),
                        color = MaterialTheme.colorScheme.primary
                    ),
                    intersectionPoint = null,
                    SelectionHighlightPoint(),
                    ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.secondary,
                                Color.Transparent
                            )
                        ), alpha = 1f
                    ),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    var totalBalance: Double = 0.0
    var i = 0f
    for (transaction in viewModelState.transactions) {
        totalBalance += transaction.amount
        pointsData.add(Point(i++, totalBalance.toFloat()))

    }

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(45.dp),
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 15.dp, 30.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.total_balance),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "$totalBalance €",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(color = MaterialTheme.colorScheme.surface)
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(color = MaterialTheme.colorScheme.surface)
                ) {
                    /*LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(color = MaterialTheme.colorScheme.secondary),
                        lineChartData = lineChartData
                    )*/


                }
            }
        }


        if (viewModelState.transactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(16.dp, 10.dp, 16.dp, 5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ClickableText(
                    text = AnnotatedString(stringResource(R.string.see_all)),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    navController.navigate("allTransactions")
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            LazyColumn(
                modifier = Modifier.padding(10.dp, 0.dp)
            ) {
                items(viewModelState.transactions) {
                    TransactionItem(it, navController)
                }
            }
        }

        //TransactionList(state = viewModelState, actions = viewModelActions)
    }
}
