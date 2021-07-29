package com.example.paint_diary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class PaintView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var drawPath: Path? = null
    private var erase = false
    var drawPaint: Paint? = null
    var canvasPaint: Paint? = null
    private var drawCanvas: Canvas? = null
    var paintColor : Int? = Color.BLACK
    private var canvasBitmap: Bitmap? = null
    private var brushSize = 0f
    private var lastBrushSize = 0f
    var params: ViewGroup.LayoutParams? = null
    fun setPaintColor(paintColor: Int) {
        this.paintColor = paintColor
        Log.e("setPaintColor", "String.valueOf(paintColor)")
    }

    fun startNew() {
        drawCanvas!!.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun setErase(isErase: Boolean) {
        erase = isErase
        if (erase) drawPaint!!.xfermode =
            PorterDuffXfermode(PorterDuff.Mode.CLEAR) else drawPaint!!.xfermode = null
    }

    fun setBrushSize(newSize: Float) {
        val pixelAmount = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,
            resources.displayMetrics
        )
        brushSize = pixelAmount
        drawPaint!!.strokeWidth = brushSize
    }

    fun setLastBrushSize(lastSize: Float) {
        lastBrushSize = lastSize
    }

    fun getBrushSize(): Float {
        return lastBrushSize
    }

    fun setColor(newColor: Int) {
        invalidate()
        paintColor = newColor
        drawPaint!!.color = paintColor as Int
        Log.e("setColor", paintColor.toString())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath!!, drawPaint!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> drawPath!!.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> drawPath!!.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                drawCanvas!!.drawPath(drawPath!!, drawPaint!!)
                drawPath!!.reset()
            }
            else -> {
            }
        }
        invalidate()
        return true
    }

    fun setupDrawing() {
        drawPath = Path()
        drawPaint = Paint()
        drawPaint!!.color = paintColor!!
        drawPaint!!.isAntiAlias = true
        drawPaint!!.strokeWidth = 5f
        drawPaint!!.style = Paint.Style.STROKE
        drawPaint!!.strokeJoin = Paint.Join.ROUND
        drawPaint!!.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
        brushSize = 10f
        lastBrushSize = brushSize
        drawPaint!!.strokeWidth = brushSize
        params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    init {
        setupDrawing()
    }
}