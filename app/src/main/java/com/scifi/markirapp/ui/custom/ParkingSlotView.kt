package com.scifi.markirapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.scifi.markirapp.R
import com.scifi.markirapp.ui.viewmodel.SlotsViewModel

class ParkingSlotView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val slotsViewModel: SlotsViewModel by lazy {
        ViewModelProvider(context as ViewModelStoreOwner)[SlotsViewModel::class.java]
    }

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.primary_blue)
        strokeWidth = 4f
    }

    private val carDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.car)

    private var slotAspectRatio = 1.5f

    init {
        setPadding(4, 4, 4, 4)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        slotsViewModel.parkingSlots.observe(context as LifecycleOwner) { slots ->
            parkingSlots = slots ?: emptyList()
            requestLayout()
            invalidate()
        }
    }

    var parkingSlots = slotsViewModel.parkingSlots.value ?: emptyList()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val columns = parkingSlots.mapNotNull { it?.column }.maxOrNull() ?: 1
        val rows = parkingSlots.mapNotNull { it?.row }.maxOrNull() ?: 1
        val slotHeight = desiredWidth / columns * slotAspectRatio
        val desiredHeight =
            (rows * slotHeight * (slotAspectRatio + 0.25f)).toInt() + paddingTop + paddingBottom
        setMeasuredDimension(
            resolveSize(desiredWidth + paddingLeft + paddingRight, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val columns = parkingSlots.mapNotNull { it?.column }.maxOrNull() ?: 1
        val slotWidth = (width - paddingLeft - paddingRight) / columns.toFloat()
        val slotHeight = slotWidth * slotAspectRatio

        parkingSlots.forEach { slot ->
            val row = (slot?.row ?: 1) - 1
            val column = (slot?.column ?: 1) - 1
            val top = paddingTop + row * slotHeight * slotAspectRatio
            val left = paddingLeft + column * slotWidth
            val right = left + slotWidth
            val bottom = top + slotHeight

            canvas.drawLine(left, top, left, bottom, paint)
            canvas.drawLine(right, top, right, bottom, paint)
            canvas.drawLine(left, top, right, top, paint)
            canvas.drawLine(left, bottom, right, bottom, paint)

            val horizontalInset = slotWidth * 0.2f
            val verticalInset = slotHeight * 0.1f
            val drawableLeft = (left + horizontalInset).toInt()
            val drawableTop = (top + verticalInset).toInt()
            val drawableRight = (right - horizontalInset).toInt()
            val drawableBottom = (bottom - verticalInset).toInt()

            if (slot?.occupied == 1) {
                carDrawable?.apply {
                    setBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
                    draw(canvas)

                    if ((row + 1) % 2 == 0) {
                        val canvasSave = canvas.save()
                        val centerX = (drawableLeft + drawableRight) / 2f
                        val centerY = (drawableTop + drawableBottom) / 2f
                        canvas.scale(1f, -1f, centerX, centerY)
                        draw(canvas)
                        canvas.restoreToCount(canvasSave)
                    }
                }
            } else {
                val cornerRadius = 14f
                val fillColor = ContextCompat.getColor(context, R.color.very_light_blue)
                val strokeColor = ContextCompat.getColor(context, R.color.primary_blue)
                val textColor = ContextCompat.getColor(context, R.color.primary_blue)
                val strokeWidthValue = 4f
                val textSizeValue = slotWidth * 0.35f

                paint.apply {
                    style = Paint.Style.FILL
                    color = fillColor
                }
                canvas.drawRoundRect(
                    drawableLeft.toFloat(),
                    drawableTop.toFloat(),
                    drawableRight.toFloat(),
                    drawableBottom.toFloat(),
                    cornerRadius,
                    cornerRadius,
                    paint
                )

                paint.apply {
                    style = Paint.Style.STROKE
                    color = strokeColor
                    strokeWidth = strokeWidthValue
                }
                canvas.drawRoundRect(
                    drawableLeft.toFloat(),
                    drawableTop.toFloat(),
                    drawableRight.toFloat(),
                    drawableBottom.toFloat(),
                    cornerRadius,
                    cornerRadius,
                    paint
                )

                paint.apply {
                    style = Paint.Style.FILL
                    color = textColor
                    textAlign = Paint.Align.CENTER
                    textSize = textSizeValue
                }
                canvas.save()
                canvas.rotate(
                    -90f,
                    (drawableLeft + drawableRight) / 2f,
                    (drawableTop + drawableBottom) / 2f
                )
                slot?.num?.let {
                    canvas.drawText(
                        it,
                        (drawableLeft + drawableRight) / 2f,
                        (drawableTop + drawableBottom) / 2f - (paint.descent() + paint.ascent()) / 2,
                        paint
                    )
                }
                canvas.restore()
            }
        }
    }
}
