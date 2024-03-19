package com.innoprog.android.uikit

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class InnoProgButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.InnoProgButtonView)
    private val size =
        ButtonSize.entries[typedArray.getInt(R.styleable.InnoProgButtonView_buttonSize, 0)]
    private val type =
        ButtonType.entries[typedArray.getInt(R.styleable.InnoProgButtonView_buttonType, 0)]
    private var state =
        ButtonState.entries[typedArray.getInt(R.styleable.InnoProgButtonView_buttonState, 0)]

    private var text = typedArray.getText(R.styleable.InnoProgButtonView_buttonText)

    private var rightIconDrawable: Drawable? =
        typedArray.getDrawable(R.styleable.InnoProgButtonView_buttonRightIcon)
    private var leftIconDrawable: Drawable? =
        typedArray.getDrawable(R.styleable.InnoProgButtonView_buttonLeftIcon)

    private val textTV by lazy { findViewById<TextView>(R.id.text_button) }
    private val rightIconIV by lazy { findViewById<ImageView>(R.id.right_icon) }
    private val leftIconIV by lazy { findViewById<ImageView>(R.id.left_icon) }

    enum class ButtonType {
        PRIMARY, DEFAULT, FLAT
    }

    enum class ButtonSize {
        LARGE, MEDIUM, SMALL
    }

    enum class ButtonState {
        ENABLED, DISABLED
    }

    init {
        inflate(context, R.layout.inno_prog_button_view, this)
        setBackgroundResource(R.drawable.button_shape)
        setType()
        setSize()
        setIcons()
        textTV.text = text
        typedArray.recycle()
    }

    fun stateIsEnabled(isEnabled: Boolean) {
        state = if (isEnabled) ButtonState.ENABLED else ButtonState.DISABLED
        setType()
        invalidate()
    }

    fun setText(text: String) {
        textTV.text = text
        invalidate()
    }

    fun setRightIcon(drawable: Drawable) {
        rightIconDrawable = drawable
        setIcons()
        requestLayout()
    }

    fun setLeftIcon(drawable: Drawable) {
        leftIconDrawable = drawable
        setIcons()
        requestLayout()
    }

    private fun setType() {
        when (type) {
            ButtonType.PRIMARY -> if (state == ButtonState.ENABLED) {
                background.setTint(context.getColor(R.color.accent_default))
                textTV.setTextColor(context.getColor(R.color.text_primary))
            } else {
                background.setTint(context.getColor(R.color.background_secondary))
                textTV.setTextColor(context.getColor(R.color.text_tertiary))
            }

            ButtonType.DEFAULT -> {
                background.setTint(context.getColor(R.color.text_field_fill))
                textTV.setTextColor(context.getColor(R.color.accent_secondary))
                alpha = if (state == ButtonState.ENABLED) NOT_TRANSPARENT else HALF_TRANSPARENT
            }

            ButtonType.FLAT -> {
                setBackgroundColor(Color.TRANSPARENT)
                textTV.setTextColor(context.getColor(R.color.accent_secondary))
                alpha = if (state == ButtonState.ENABLED) NOT_TRANSPARENT else HALF_TRANSPARENT
            }
        }
    }

    private fun setSize() {
        when (size) {
            ButtonSize.LARGE -> {
                textTV.setTextAppearance(R.style.TextButtonLarge)
                layoutParams.apply {
                    setPadding(
                        resources.getDimensionPixelSize(R.dimen.button_padding_20),
                        resources.getDimensionPixelSize(R.dimen.button_padding_12),
                        resources.getDimensionPixelSize(R.dimen.button_padding_20),
                        resources.getDimensionPixelSize(R.dimen.button_padding_12),
                    )
                }
            }

            ButtonSize.MEDIUM -> {
                textTV.setTextAppearance(R.style.TextButtonMedium)
                layoutParams.apply {
                    setPadding(
                        resources.getDimensionPixelSize(R.dimen.button_padding_18),
                        resources.getDimensionPixelSize(R.dimen.button_padding_10),
                        resources.getDimensionPixelSize(R.dimen.button_padding_18),
                        resources.getDimensionPixelSize(R.dimen.button_padding_10),
                    )
                }
            }

            ButtonSize.SMALL -> {
                textTV.setTextAppearance(R.style.TextButtonSmall)
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.button_padding_12),
                    resources.getDimensionPixelSize(R.dimen.button_padding_6),
                    resources.getDimensionPixelSize(R.dimen.button_padding_12),
                    resources.getDimensionPixelSize(R.dimen.button_padding_6),
                )
            }
        }
        setIcons()
    }

    private fun setIcons() {
        val iconSize =
            if (size == ButtonSize.SMALL) resources.getDimensionPixelSize(R.dimen.button_icon_size_16)
            else resources.getDimensionPixelSize(R.dimen.button_icon_size_20)
        val iconMargin = when (size) {
            ButtonSize.LARGE -> resources.getDimensionPixelSize(R.dimen.button_padding_14)
            ButtonSize.MEDIUM -> resources.getDimensionPixelSize(R.dimen.button_padding_14)
            ButtonSize.SMALL -> resources.getDimensionPixelSize(R.dimen.button_padding_10)
        }

        leftIconDrawable?.let {
            it.setTint(textTV.currentTextColor)
            val marginParam = textTV.layoutParams as MarginLayoutParams
            marginParam.marginStart = iconMargin
            textTV.layoutParams = marginParam
            leftIconIV.layoutParams.apply {
                height = iconSize
                width = iconSize
            }
            leftIconIV.setImageDrawable(it)
        }
        rightIconDrawable?.let {
            it.setTint(textTV.currentTextColor)
            val marginParam = textTV.layoutParams as MarginLayoutParams
            marginParam.marginEnd = iconMargin
            textTV.layoutParams = marginParam
            rightIconIV.layoutParams.apply {
                height = iconSize
                width = iconSize
            }
            rightIconIV.setImageDrawable(it)
        }
    }

    companion object {
        const val NOT_TRANSPARENT = 1f
        const val HALF_TRANSPARENT = 0.5f
    }
}
