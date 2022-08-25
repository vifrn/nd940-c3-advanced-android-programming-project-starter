package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var progress = 0f

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (new == ButtonState.Loading) {
            valueAnimator.start()
            invalidate()
        } else {
            valueAnimator.cancel()
            progress = 0f
            invalidate()
        }
    }

    private var buttonBackgroundColor = 0
    private var buttonForegroundColor = 0
    private var buttonCircleColor = 0
    private var buttonTextColor = 0

    val paint = Paint()

    private val buttonTextReady : String = resources.getString(R.string.button_name)
    private val buttonTextClicked : String = resources.getString(R.string.button_loading)
    private val buttonTextSize : Float = resources.getDimension(R.dimen.button_text_size)
    private val circleRadius : Float = resources.getDimension(R.dimen.circle_radius)
    private val circleMargin : Float = resources.getDimension(R.dimen.circle_margin)

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, resources.getColor(R.color.colorPrimary))
            buttonForegroundColor = getColor(R.styleable.LoadingButton_buttonForegroundColor, resources.getColor(R.color.colorPrimaryDark))
            buttonCircleColor = getColor(R.styleable.LoadingButton_buttonCircleColor, resources.getColor(R.color.colorAccent))
            buttonTextColor = getColor(R.styleable.LoadingButton_buttonTextColor, Color.WHITE)
        }

        valueAnimator.duration = 500
        valueAnimator.addUpdateListener { animation ->
            progress = animation.animatedValue as Float
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipRect(0, 0, widthSize, heightSize)
        canvas.drawColor(buttonBackgroundColor)

        canvas.save()
        canvas.clipRect(0f, 0f,
            widthSize * progress,
            heightSize.toFloat()
        )
        canvas.drawColor(buttonForegroundColor)
        canvas.restore()

        paint.color = buttonTextColor
        paint.textSize = buttonTextSize
        paint.textAlign = Paint.Align.CENTER

        val text = when(buttonState) {
            ButtonState.Loading -> buttonTextClicked
            else -> buttonTextReady
        }
        canvas.drawText(text, widthSize / 2f, (heightSize / 2f) + (buttonTextSize / 2f), paint)

        paint.color = buttonCircleColor
        canvas.save()
        val heightExtra = heightSize - circleRadius * 2
        canvas.drawArc(
            widthSize - circleRadius * 2,
            circleMargin + (heightExtra / 2f),
            widthSize - circleMargin * 4,
            heightSize - circleMargin - (heightExtra / 2f),
            0f,
            360 * progress,
            true,
            paint)
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}