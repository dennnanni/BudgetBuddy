package com.android.budgetbuddy.ui.composables

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Layout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.budgetbuddy.R
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.rememberLegendItem
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shape.markerCornered
import com.patrykandpatrick.vico.compose.common.shape.toVicoShape
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.Insets
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.copyColor
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun rememberMarker(
    labelPosition: DefaultCartesianMarker.LabelPosition = DefaultCartesianMarker.LabelPosition.Top,
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackgroundShape = Shape.markerCornered(Corner.FullyRounded)
    val labelBackground =
        rememberShapeComponent(labelBackgroundShape, MaterialTheme.colorScheme.surface)
            .setShadow(
                radius = LABEL_BACKGROUND_SHADOW_RADIUS_DP,
                dy = LABEL_BACKGROUND_SHADOW_DY_DP,
                applyElevationOverlay = true,
            )
    val label =
        rememberTextComponent(
            color = MaterialTheme.colorScheme.onSurface,
            background = labelBackground,
            padding = Dimensions.of(8.dp, 4.dp),
            typeface = Typeface.MONOSPACE,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent = rememberShapeComponent(Shape.Pill, MaterialTheme.colorScheme.surface)
    val indicatorCenterComponent = rememberShapeComponent(Shape.Pill)
    val indicatorRearComponent = rememberShapeComponent(Shape.Pill)
    val indicator =
        rememberLayeredComponent(
            rear = indicatorRearComponent,
            front =
            rememberLayeredComponent(
                rear = indicatorCenterComponent,
                front = indicatorFrontComponent,
                padding = Dimensions.of(5.dp),
            ),
            padding = Dimensions.of(10.dp),
        )
    val guideline = rememberAxisGuidelineComponent()
    return remember(label, labelPosition, indicator, showIndicator, guideline) {
        @SuppressLint("RestrictedApi")
        object : DefaultCartesianMarker(
            label = label,
            labelPosition = labelPosition,
            indicator = if (showIndicator) indicator else null,
            indicatorSizeDp = 36f,
            setIndicatorColor =
            if (showIndicator) {
                { color ->
                    indicatorRearComponent.color = color.copyColor(alpha = .15f)
                    indicatorCenterComponent.color = color
                    indicatorCenterComponent.setShadow(radius = 12f, color = color)
                }
            } else {
                null
            },
            guideline = guideline,
        ) {
            override fun getInsets(
                context: CartesianMeasureContext,
                outInsets: Insets,
                horizontalDimensions: HorizontalDimensions,
            ) {
                with(context) {
                    outInsets.top =
                        (
                                CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP -
                                        LABEL_BACKGROUND_SHADOW_DY_DP
                                )
                            .pixels
                    if (labelPosition == LabelPosition.AroundPoint) return
                    outInsets.top += label.getHeight(context) + labelBackgroundShape.tickSizeDp.pixels
                }
            }
        }
    }
}

private const val LABEL_BACKGROUND_SHADOW_RADIUS_DP = 4f
private const val LABEL_BACKGROUND_SHADOW_DY_DP = 2f
private const val CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER = 1.4f


@Composable
fun LineChart(
    data: Map<Float, Float>,
    singleTransaction: Boolean = false,
    bottomAxisValueFormatter: CartesianValueFormatter = CartesianValueFormatter.decimal(),
    rotatedLabels: Boolean = false,
) {

    val modelProducer = remember { CartesianChartModelProducer.build() }
    if (data.isNotEmpty()) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.Default) {
                modelProducer.tryRunTransaction {
                    lineSeries { series(data.keys, data.values) }
                }
            }
        }
    }

    val marker = rememberMarker()
    val cartesianChart = rememberCartesianChart(
        rememberLineCartesianLayer(
            listOf(
                rememberLineSpec(
                    DynamicShader.color(MaterialTheme.colorScheme.primary)
                )
            )
        ),
        startAxis = rememberStartAxis(
            label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
            axis = rememberAxisLineComponent(color = MaterialTheme.colorScheme.onSurface),
            horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
            guideline = rememberAxisGuidelineComponent(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
        ),
        bottomAxis = rememberBottomAxis(
            axis = rememberAxisLineComponent(color = MaterialTheme.colorScheme.onSurface),
            guideline = null,
            valueFormatter = bottomAxisValueFormatter,
            labelRotationDegrees = if (rotatedLabels) AXIS_LABEL_ROTATION_DEGREES else 0f,
        ),
        persistentMarkers = if (singleTransaction) mapOf(0f to marker) else null,
    )
    val scrollState = rememberVicoScrollState(
        initialScroll = Scroll.Absolute.End,
        scrollEnabled = true,
    )

    CartesianChartHost(
        chart = cartesianChart,
        scrollState = scrollState,
        modifier = Modifier
            .fillMaxSize(),
        modelProducer = modelProducer,
        marker = marker
    )

}

private const val AXIS_LABEL_ROTATION_DEGREES = 45f

@Composable
fun BarChart(
    data: Map<Int, Pair<Float, Float>>,
    bottomAxisValueFormatter: CartesianValueFormatter = CartesianValueFormatter.decimal(),
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            modelProducer.tryRunTransaction {
                columnSeries {
                    series(
                        List(data.size) {
                            data[it]!!.first
                        },
                    )
                    series(
                        List(data.size) {
                            data[it]!!.second
                        },
                    )
                }
            }
        }
    }

    val shape = remember { RoundedCornerShape(30.dp).toVicoShape() }
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(color = MaterialTheme.colorScheme.secondary, thickness = 8.dp, shape = shape),
                    rememberLineComponent(color = MaterialTheme.colorScheme.primary, thickness = 8.dp, shape = shape),
                ),
            ),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
            legend = rememberLegend()
        ),
        modifier = Modifier.fillMaxSize(),
        modelProducer = modelProducer,
        marker = rememberMarker(),
        runInitialAnimation = false,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}


@Composable
private fun rememberLegend() =
    rememberVerticalLegend<CartesianMeasureContext, CartesianDrawContext>(
        items = listOf(
            rememberLegendItem(
                icon = rememberShapeComponent(Shape.Pill, MaterialTheme.colorScheme.secondary),
                label =
                rememberTextComponent(
                    color = vicoTheme.textColor,
                    textSize = 12.sp,
                    typeface = Typeface.MONOSPACE,
                ),
                labelText = stringResource(R.string.income),
            ),
            rememberLegendItem(
                icon = rememberShapeComponent(Shape.Pill, MaterialTheme.colorScheme.primary),
                label =
                rememberTextComponent(
                    color = vicoTheme.textColor,
                    textSize = 12.sp,
                    typeface = Typeface.MONOSPACE,
                ),
                labelText = stringResource(R.string.expense),
            )
        ),
        iconSize = 8.dp,
        iconPadding = 8.dp,
        spacing = 4.dp,
        padding = Dimensions.of(top = 8.dp),
    )