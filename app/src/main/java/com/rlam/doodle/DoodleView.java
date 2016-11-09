package com.rlam.doodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by raymondlam on 11/3/16.
 */

public class DoodleView extends View {

    private Paint mPaintDoodle = new Paint();
    private Paint mCanvasPaint = new Paint();
    private Path mPath = new Path();

    //I didn't want to use a bitmap, so I'm going to use an ArrayList implementation lol.
    private ArrayList<Paint> colors = new ArrayList<Paint>(); //Colors
    private ArrayList<Path> paths = new ArrayList<Path>(); // Path

    private Canvas mCanvas;
    private Bitmap mBitmap;

//    public DoodleView(Context context){
//        super(context);
//        initPaint();
//    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

//    public DoodleView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        initPaint();
//    }

    public void initPaint() {
        mPaintDoodle = new Paint();
        mPath = new Path();

        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        mPaintDoodle.setColor(Color.BLACK);
        mPaintDoodle.setAntiAlias(true);
        mPaintDoodle.setStyle(Paint.Style.STROKE);

        mPaintDoodle.setStrokeWidth(14);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    public void onDraw(Canvas canvas) {

//        canvas.drawPath(mPath, mPaintDoodle);
        canvas.drawBitmap(mBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mPath, mPaintDoodle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(touchX, touchY);
                mCanvas.drawPath(mPath, mPaintDoodle);
                mPath.reset();
                break;
        }

        invalidate();
        return true;
    }

    public void setColor(int color) {
        mPaintDoodle.setColor(color);
    }

    public void setBrushWidth(float width) {
        mPaintDoodle.setStrokeWidth(width);
    }

    public void setPaintOpacity(int opacity) {
        mPaintDoodle.setAlpha(opacity);
    }

    public void startNewPainting(){
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

}
