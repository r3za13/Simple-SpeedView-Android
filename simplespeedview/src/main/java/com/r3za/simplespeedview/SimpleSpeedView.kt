package com.r3za.simplespeedview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.OvershootInterpolator

/**
 * @author R3ZA13
 * @since 7/5/19
 */
class SimpleSpeedView : View {

    var totalProgress = 100f
    var progress = 10f

    var partCount = 5
    var totalAngle = 180
    var paints: ArrayList<Paint> = arrayListOf()
    var indicatorSweepAngle = 12f
    var indicatorPaint = Paint()
    var transparentCirclePaint = Paint()
    var whiteCircleRadius = dpToPx(12)
        set(value) {
            field = value
            transparentCircleRadius = value / 3
        }

    var animationDuration = 500.toLong()
    var transparentCircleRadius = whiteCircleRadius / 3
    var circleIndicatorMargin = dpToPx(3)
    var strokeWidth = dpToPx(30)
    var isInHalfMode = totalAngle <= 180
    var myRotation = progress - 90
    var hasStartAnimation: Boolean = false

    var partColors: ArrayList<Int> = arrayListOf(
            R.color.indicator_color_1,
            R.color.indicator_color_2,
            R.color.indicator_color_3,
            R.color.indicator_color_4,
            R.color.indicator_color_5
    )
    set(value) {
        field = value
        generateColors()
        invalidate()
    }

    private var tempCanvas: Canvas? = null
    private var tempCanvas2: Canvas? = null

    var marginBetweenParts = dpToPx(2) //4f
    var intermediateBitmap: Bitmap? = null
    lateinit var intermediatePaint: Paint

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }


    private fun init(attrs: AttributeSet?) {

        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                    attrs, R.styleable.SimpleSpeedView, 0, 0
            )

            @SuppressLint("ResourceAsColor")
            indicatorPaint.color =
                    a.getColor(R.styleable.SimpleSpeedView_indicator_color, R.color.white)
            strokeWidth =
                    a.getDimension(R.styleable.SimpleSpeedView_parts_width, dpToPx(30))
            partCount = a.getInt(R.styleable.SimpleSpeedView_parts_count, 5)
            progress = a.getFloat(R.styleable.SimpleSpeedView_progress, 0f)
            totalProgress = a.getFloat(R.styleable.SimpleSpeedView_total_progress, 100f)
            partCount = a.getInt(R.styleable.SimpleSpeedView_parts_count, 5)
            marginBetweenParts =
                    a.getDimension(R.styleable.SimpleSpeedView_part_margin, dpToPx(2))
            hasStartAnimation = a.getBoolean(R.styleable.SimpleSpeedView_has_start_animation, false)
            whiteCircleRadius =
                    a.getDimension(
                            R.styleable.SimpleSpeedView_indicator_circle_radius,
                            dpToPx(12)
                    )

            a.recycle()
        } else {
            indicatorPaint.color = Color.WHITE


        }


        generateColors()

        indicatorPaint.isAntiAlias = true

        transparentCirclePaint.color = Color.TRANSPARENT
        transparentCirclePaint.isAntiAlias = true
        transparentCirclePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        intermediatePaint = Paint()
        intermediatePaint.isAntiAlias = true

        paints.forEach {
            it.isAntiAlias = true
        }

        post {
            intermediateBitmap =
                    Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            tempCanvas = Canvas(intermediateBitmap)
            tempCanvas2 = Canvas(intermediateBitmap)
            invalidate()

            if (hasStartAnimation)
                postDelayed({
                    setViewProgress(progress.toInt(), true)
                }, 300)
            else
                setViewProgress(progress.toInt(), false)
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var sizeHeight = 0
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val widthWithoutPadding = width - paddingLeft - paddingRight

        if (isInHalfMode) {
            if (height > width * 0.5)
                sizeHeight = widthWithoutPadding * 4 / 8
            else
                sizeHeight = height - paddingTop - paddingBottom
        } else
            sizeHeight = widthWithoutPadding

        setMeasuredDimension(width, sizeHeight + paddingTop + paddingBottom + 16)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Draw Parts
        val eachPartSweep = totalAngle / partCount
        for (i in 0 until partCount) {
            paints[i].strokeWidth = strokeWidth
            canvas?.drawArc(
                    canvas.clipBounds.toRectF().apply {
                        left = strokeWidth / 2
                        right -= strokeWidth / 2
                        top = strokeWidth / 2
                        if (!isInHalfMode)
                            bottom -= strokeWidth / 2
                        else
                            bottom = bottom * 2 - (strokeWidth / 2)
                    }, i * eachPartSweep.toFloat() + 180, if (i == partCount - 1) {
                eachPartSweep.toFloat()
            } else {
                eachPartSweep.toFloat() - marginBetweenParts
            }, false, paints[i]
            )
        }

        //Draw indicator and handle rotation
        tempCanvas2?.save()
        if (isInHalfMode)
            tempCanvas2?.rotate(
                    myRotation,
                    measuredWidth / 2.toFloat(),
                    measuredHeight - whiteCircleRadius
            )
        else
            tempCanvas2?.rotate(
                    myRotation,
                    measuredWidth / 2.toFloat(),
                    measuredHeight / 2.toFloat()
            )

        tempCanvas2?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        tempCanvas2?.drawArc(canvas!!.clipBounds.toRectF().apply {
            top -= bottom / 1.3.toFloat()
            if (!isInHalfMode) {
                bottom /= 2
                top /= 2
            } else
                bottom -= (whiteCircleRadius + circleIndicatorMargin)
        }, 90 - indicatorSweepAngle / 2, indicatorSweepAngle, true, indicatorPaint)
        tempCanvas2?.restore()

        //Draw transparent margin between indicator and center circle
        tempCanvas2?.drawCircle(
                measuredWidth / 2.toFloat(),
                if (!isInHalfMode) {
                    measuredHeight.toFloat() / 2
                } else {
                    measuredHeight.toFloat() - whiteCircleRadius
                },
                whiteCircleRadius + circleIndicatorMargin,
                transparentCirclePaint
        )


        //Draw white circle
        tempCanvas?.drawCircle(
                measuredWidth / 2.toFloat(),
                if (!isInHalfMode) {
                    measuredHeight.toFloat() / 2
                } else {
                    measuredHeight.toFloat() - whiteCircleRadius
                },
                whiteCircleRadius,
                indicatorPaint
        )

        //draw center of white circle
        tempCanvas?.drawCircle(
                measuredWidth / 2.toFloat(),
                if (!isInHalfMode) {
                    measuredHeight.toFloat() / 2
                } else {
                    measuredHeight.toFloat() - whiteCircleRadius
                },
                transparentCircleRadius,
                transparentCirclePaint
        )
        //Additional bitmap to make PurterDuff mode works
        intermediateBitmap?.let {
            canvas?.drawBitmap(
                    it,
                    canvas.clipBounds,
                    canvas.clipBounds?.toRectF(),
                    intermediatePaint
            )
        }

    }


    private fun generateColors() {
        paints.clear()
        for (i in 0..partCount) {
            val paint = Paint()
            paint.style = Paint.Style.STROKE
            try {
                paint.color =
                    ResourcesCompat.getColor(resources, partColors[i % partColors.size], null)
            }
            catch (e: Resources.NotFoundException)
            {
                paint.color = partColors[i % partColors.size]
            }
            paints.add(paint)
        }
    }

    private fun dpToPx(dp: Int): Float {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat()
    }

    private fun setDegree(degree: Int, hasAnimation: Boolean = true) {
        if (hasAnimation) {
            ValueAnimator().apply {
                this.setIntValues(myRotation.toInt(), degree)
                addUpdateListener {
                    myRotation = (it.animatedValue as Int).toFloat()
                    invalidate()
                }
                duration = animationDuration
                interpolator = OvershootInterpolator()
            }.start()
        } else {
            myRotation = degree.toFloat()
            invalidate()
        }
    }

    fun setViewProgress(progress: Int, hasAnimation: Boolean = true) {
        if (progress > totalProgress) {
            setDegree((totalAngle) - 90, hasAnimation)
            this.progress = totalProgress
        } else {
            setDegree(((progress / totalProgress * totalAngle) - 90).toInt(), hasAnimation)
            this.progress = progress.toFloat()
        }
    }

    inline fun Rect.toRectF(): RectF = RectF(this)
}