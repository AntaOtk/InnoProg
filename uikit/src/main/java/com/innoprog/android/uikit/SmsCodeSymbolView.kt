package com.innoprog.android.uikit

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.annotation.ColorInt


@SuppressLint("ViewConstructor")
internal class SmsCodeSymbolView(context: Context, private val symbolStyle: SymbolStyle) :
    View(context) {
    data class State(
        val symbol: Char? = null,
        val isActive: Boolean = false
    )

    var state: State = State()
        set(value) {
            if (field == value) return
            field = value
            updateState(state)
        }

    private val itemWidth: Int = resources.getDimensionPixelSize(R.dimen.sms_item_size)
    private val itemHeight: Int = resources.getDimensionPixelSize(R.dimen.sms_item_size)
    private val cornerRadius: Float = 8.0F


    private val backgroundPaint: Paint = Paint().apply {
        color = symbolStyle.backgroundColor
        style = Paint.Style.FILL
    }
    private val borderPaint: Paint = Paint().apply {
        isAntiAlias = true
        color = symbolStyle.borderColor
        style = Paint.Style.STROKE
        strokeWidth = 2.0F
    }


    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.text_size_16)
        color = symbolStyle.textColor
        textAlign = Paint.Align.CENTER
    }

    private val backgroundRect = RectF()
    private val borderRect = RectF()

    private var textAnimator: Animator? = null

    private fun updateState(state: State) = with(state) {
        textAnimator?.cancel()
        if (symbol == null && isActive && symbolStyle.showCursor) {
            textAnimator = ObjectAnimator.ofInt(textPaint, "alpha", 255, 255, 0, 0)
                .apply {
                    duration = cursorAlphaAnimDuration
                    startDelay = cursorAlphaAnimStartDelay
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    addUpdateListener { invalidate() }
                }
        } else {
            val startAlpha = if (symbol == null) 255 else 127
            val endAlpha = if (symbol == null) 0 else 255
            textAnimator = ObjectAnimator.ofInt(textPaint, "alpha", startAlpha, endAlpha)
                .apply {
                    duration = textPaintAlphaAnimDuration
                    addUpdateListener { invalidate() }
                }
        }

        textAnimator?.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = resolveSizeAndState(itemWidth, widthMeasureSpec, 0)
        val h = resolveSizeAndState(itemHeight, heightMeasureSpec, 0)
        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        backgroundRect.left = 0f
        backgroundRect.top = 0F
        backgroundRect.right = measuredWidth.toFloat() + borderPaint.strokeWidth / 2
        backgroundRect.bottom = measuredHeight.toFloat() + borderPaint.strokeWidth / 2
        borderRect.left = 0f
        borderRect.top = 0F
        borderRect.right = measuredWidth.toFloat()
        borderRect.bottom = measuredHeight.toFloat()
    }

    override fun onDraw(canvas: Canvas) = with(canvas) {
        drawCodeBoxPaint()
        drawCodeBoxBorderPaint()
        drawInputText()
    }

    private fun Canvas.drawCodeBoxBorderPaint() {
        drawRoundRect(
            borderRect,
            cornerRadius,
            cornerRadius,
            borderPaint
        )
    }

    private fun Canvas.drawCodeBoxPaint() {
        drawRoundRect(
            backgroundRect,
            cornerRadius,
            cornerRadius,
            backgroundPaint
        )
    }

    private val y = itemWidth / 2 + textPaint.textSize / 2 - textPaint.descent()
    private fun Canvas.drawInputText() {
        drawText(
            if (state.isActive && symbolStyle.showCursor) cursorSymbol else state.symbol?.toString()
                ?: "",
            backgroundRect.width() / 2 + borderPaint.strokeWidth / 2,
            y,
            textPaint
        )
    }

    companion object {
        const val textPaintAlphaAnimDuration = 25L
        const val cursorAlphaAnimDuration = 500L
        const val cursorAlphaAnimStartDelay = 200L
        const val cursorSymbol = "|"
    }
}

data class SymbolStyle(
    val showCursor: Boolean,
    @ColorInt val backgroundColor: Int,
    @ColorInt val borderColor: Int,
    @ColorInt val textColor: Int,
)