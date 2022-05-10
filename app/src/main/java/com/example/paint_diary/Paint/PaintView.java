package com.example.paint_diary.Paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class PaintView extends View {
    public int BRUSH_SIZE = 5;
    public static final int COLOR_PEN = Color.BLACK;
    public static final int COLOR_ERASER = Color.WHITE;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private MaskFilter lastMaskFilter;

    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    public int currentColor;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private ArrayList<FingerPath> undoList=new ArrayList<>();
    private ArrayList<PathXY> pathXYList=new ArrayList<>();

    public Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPath = new Path();
        mPaint.setAntiAlias(true);
        //mPaint.setDither(true);
        mPaint.setColor(COLOR_PEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setMaskFilter(null);
        //mPaint.setXfermode(null);
       // mPaint.setAlpha(0xff);
    }

    public void init(DisplayMetrics metrics){
        //canvas크기 1:1
        int height = metrics.widthPixels;
        int width = metrics.widthPixels;
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        currentColor = COLOR_PEN;
    }

    public void pen(){
        currentColor = COLOR_PEN;
    }

    public void eraser(){
        currentColor = COLOR_ERASER;
    }

    public void clear(){
        paths.clear();
        undoList.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(DEFAULT_BG_COLOR);
        for (FingerPath fp : paths){
            mPaint.setColor(fp.getColor());
            mPaint.setStrokeWidth(fp.getStrokeWidth());
            mPaint.setMaskFilter(fp.getFilter());

            mCanvas.drawPath(fp.getPath(), mPaint);
        }
        listToJson();
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        //canvas.drawBitmap (GetImageFromURL(stringToBitmap), 0,  0,null);
        canvas.restore();
    }

    private void touchStart(float x, float y){

        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor, BRUSH_SIZE, mPath,lastMaskFilter);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        Log.e("moveTo:x", String.valueOf(x));
        Log.e("moveTo:y", String.valueOf(y));
    }

    private void touchMove(float x, float y){
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            Log.e("quadTo:x", String.valueOf(x));
            Log.e("quadTo:y", String.valueOf(y));
            PathXY pathxy = new PathXY(x,y);
            pathXYList.add(pathxy);
        }
    }

    private void touchUp(){
        mPath.lineTo(mX, mY);
        Log.e("lineTo:x", String.valueOf(mX));
        Log.e("lineTo:y", String.valueOf(mY));
        for (int i = 0; i < pathXYList.size(); i++){
            Log.e("pathx:", String.valueOf(pathXYList.get(i).getX()));
            Log.e("pathy:", String.valueOf(pathXYList.get(i).getY()));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
        }


        return true;
    }
    public void onClickUndo () {
        if (paths.size()>0) {
            undoList.add(paths.remove(paths.size()-1));
            invalidate();
        }else{

        }

    }

    public void onClickRedo (){
        if (undoList.size()>0) {
            paths.add(undoList.remove(undoList.size()-1));
            invalidate();
        }else{

        }
    }

    public void setBrushType(int id, float radius){
        lastMaskFilter=idToMaskFilter(id,radius);
        mPaint.setMaskFilter(lastMaskFilter);
    }
    private MaskFilter idToMaskFilter(int id, float radius){
        switch (id){
            case BrushType.BRUSH_DEFAULT:
            return Brush.setSolidBrush(radius);
            case BrushType.BRUSH_NEON:
                return Brush.setNeonBrush(radius);
            case BrushType.BRUSH_BLUR:
                return Brush.setBlurBrush(radius);
            case BrushType.BRUSH_INNER:
                return Brush.setInnerBrush(radius);
            default:
                return Brush.setSolidBrush(radius);

        }
    }

    private void listToJson(){
        try {
            JSONArray jArray = new JSONArray();//배열
            for (int i = 0; i < paths.size(); i++) {
                JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                sObject.put("color", paths.get(i).getColor());
                sObject.put("filter", paths.get(i).getFilter());
                sObject.put("width", paths.get(i).getStrokeWidth());
                sObject.put("path", paths.get(i).getPath());
                jArray.put(sObject);
            }

            Log.d("JSON Test", jArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
