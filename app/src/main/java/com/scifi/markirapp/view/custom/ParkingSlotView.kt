package com.scifi.markirapp.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.scifi.markirapp.R
import com.scifi.markirapp.data.ParkingSlot

class ParkingSlotView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.primary_blue)
        strokeWidth = 4f
    }

    private val carDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.car)

    var parkingSlots: List<ParkingSlot> = listOf()
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val rows = (parkingSlots.size + 7) / 8
        val slotHeight = desiredWidth / 8 * 1.25f
        val desiredHeight = (rows * slotHeight * 1.5f).toInt()
        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val slotWidth = width / 8f
        val slotHeight = slotWidth * 1.25f

        parkingSlots.forEachIndexed { index, slot ->
            val row = index / 8
            val column = index % 8
            val top = row * slotHeight * 1.0f + (row / 2) * slotHeight * 1.25f
            val left = column * slotWidth
            val right = left + slotWidth
            val bottom = top + slotHeight

            canvas.drawLine(left, top, left, bottom, paint)
            canvas.drawLine(right, top, right, bottom, paint)
            if (row % 2 == 0) {
                canvas.drawLine(left, bottom, right, bottom, paint)
            }

            val horizontalInset = 32f
            val verticalInset = 16f
            val drawableLeft = (left + horizontalInset).toInt()
            val drawableTop = (top + verticalInset).toInt()
            val drawableRight = (right - horizontalInset).toInt()
            val drawableBottom = (bottom - verticalInset).toInt()

            if (slot.isOccupied) {
                carDrawable?.apply {
                    setBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
                    draw(canvas)

                    if (row % 2 == 0) {
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
                val textSizeValue = 40f

                paint.apply {
                    style = Paint.Style.FILL
                    color = fillColor
                }
                canvas.drawRoundRect(drawableLeft.toFloat(), drawableTop.toFloat(), drawableRight.toFloat(), drawableBottom.toFloat(), cornerRadius, cornerRadius, paint)

                paint.apply {
                    style = Paint.Style.STROKE
                    color = strokeColor
                    strokeWidth = strokeWidthValue
                }
                canvas.drawRoundRect(drawableLeft.toFloat(), drawableTop.toFloat(), drawableRight.toFloat(), drawableBottom.toFloat(), cornerRadius, cornerRadius, paint)

                paint.apply {
                    style = Paint.Style.FILL
                    color = textColor
                    textAlign = Paint.Align.CENTER
                    textSize = textSizeValue
                }
                canvas.save()
                canvas.rotate(-90f, (drawableLeft + drawableRight) / 2f, (drawableTop + drawableBottom) / 2f)
                canvas.drawText(slot.id, (drawableLeft + drawableRight) / 2f, (drawableTop + drawableBottom) / 2f - (paint.descent() + paint.ascent()) / 2, paint)
                canvas.restore()
            }
        }
    }
}
