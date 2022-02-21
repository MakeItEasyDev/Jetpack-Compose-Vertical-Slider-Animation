package com.jetpack.verticalslideranimation

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInteropFilter

private const val MAX_VALUE = 100
private const val MIN_VALUE = 0

class ComposeVerticalSliderState {
    var isEnabled = mutableStateOf(true)
        internal set
    var adjustTop = mutableStateOf(0f)
        internal set
    var progressValue = mutableStateOf(0)
        internal set
    private fun updateAdjustTopValue(value: Float) {
        this.adjustTop.value = value
    }
    private fun updateProgressValue(value: Int) {
        this.progressValue.value = value
    }

    internal fun updateOnTouch(
        motionEvent: MotionEvent,
        canvasHeight: Int
    ) {
        if (!isEnabled.value) return

        val y = motionEvent.y.roundToInt()

        updateAdjustTopValue(y.toFloat())

        val progress = calculateProgress(y.toFloat(), canvasHeight).coerceIn(MIN_VALUE, MAX_VALUE)

        updateProgressValue(progress)
    }

    private fun calculateProgress(adjustTop: Float, canvasHeight: Int): Int {
        return MAX_VALUE - (adjustTop / canvasHeight).times(100).roundToInt()
    }

    fun calculateAdjustTopFromProgressValue(progressValue: Int, canvasHeight: Int): Float {
        return (MAX_VALUE - progressValue).times(canvasHeight).div(100).toFloat()
    }
}

@Composable
fun rememberComposeVerticalSlider(): ComposeVerticalSliderState {
    return remember { ComposeVerticalSliderState() }
}

@ExperimentalComposeUiApi
@Composable
fun VerticalSlider(
    state: ComposeVerticalSliderState,
    progressValue: Int? = null,
    enabled: Boolean = true,
    width: Dp = 140.dp,
    height: Dp = 300.dp,
    radius: CornerRadius = CornerRadius(80f, 80f),
    trackColor: Color = Color.LightGray,
    progressTrackColor: Color = Color.Green,
    onProgressChanged: (Int) -> Unit,
    onStopTrackingTouch: (Int) -> Unit
) {
    val left = 0f
    val top = 0f
    var right by remember { mutableStateOf(0f) }
    var bottom by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0) }
    val radiusX = radius.x
    val radiusY = radius.y
    val enabledState by rememberSaveable { state.isEnabled }
    var adjustTop by rememberSaveable { state.adjustTop }
    var progressValueData by rememberSaveable {
        if (progressValue != null) {
            state.progressValue.value = progressValue
            onProgressChanged(state.progressValue.value)
            onStopTrackingTouch(state.progressValue.value)
        }
        state.progressValue
    }
    val rect = Rect(left, top, right, bottom)
    val trackPaint = Paint().apply {
        color = trackColor
        isAntiAlias = true
        strokeWidth = 10f
    }
    val progressPaint = Paint().apply {
        color = if (enabledState) progressTrackColor else Color.Gray
        isAntiAlias = true
        strokeWidth = 10f
    }
    val path = Path()

    LaunchedEffect(enabledState) {
        state.isEnabled.value = enabled
    }

    Canvas(
        modifier = Modifier
            .pointerInteropFilter { motionEvent ->
                when(motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        enabledState
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (enabledState) {
                            state.updateOnTouch(motionEvent, canvasHeight)
                            adjustTop = state.adjustTop.value
                            progressValueData = state.progressValue.value
                            onProgressChanged(progressValueData)
                        }
                        enabledState
                    }
                    MotionEvent.ACTION_UP -> {
                        if (enabledState) {
                            state.updateOnTouch(motionEvent = motionEvent, canvasHeight)
                            adjustTop = state.adjustTop.value
                            progressValueData = state.progressValue.value
                            onStopTrackingTouch(progressValueData)
                        }
                        enabledState
                    }
                    else -> false
                }
            }
            .width(width = width)
            .height(height)
    ) {
        canvasHeight = size.height.roundToInt()
        right = size.width
        bottom = size.height
        val aCanvas = drawContext.canvas
        path.addRoundRect(roundRect = RoundRect(left, top, right, bottom, CornerRadius(x = radiusX, y = radiusY)))
        aCanvas.clipPath(path = path, ClipOp.Intersect)
        aCanvas.drawRect(rect, trackPaint)

        if (rect.width > MIN_VALUE && rect.height > MIN_VALUE) {
            adjustTop = state.calculateAdjustTopFromProgressValue(progressValueData, canvasHeight)
            aCanvas.drawRect(left, adjustTop, right, bottom, progressPaint)
        }
    }
}











