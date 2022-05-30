package com.qlh.crop.cropview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class DragZoomImageView extends View {


    private Bitmap image;

    private Paint paint;

    private GestureDetector gestures;

    private ScaleGestureDetector scaleGesture;

    private Matrix drawMatrix;

    private float lastFocusX;

    private float lastFocusY;



    public DragZoomImageView(Context context) {
        super(context);
        init(context);
    }

    public DragZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    private void init(Context context){

        drawMatrix = new Matrix();
        drawMatrix.postTranslate(100, 100);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        scaleGesture = new ScaleGestureDetector(getContext(),
                new ScaleListener());
        gestures = new GestureDetector(getContext(), new GestureListener());

        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();

        canvas.drawBitmap(image, drawMatrix, paint);

        canvas.restore();
    }


    public class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            Matrix transformationMatrix = new Matrix();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            transformationMatrix.postTranslate(-focusX, -focusY);

            transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());

            float focusShiftX = focusX - lastFocusX;
            float focusShiftY = focusY - lastFocusY;
            transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);
            drawMatrix.postConcat(transformationMatrix);
            lastFocusX = focusX;
            lastFocusY = focusY;
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }

    }

    public class GestureListener implements GestureDetector.OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {

            drawMatrix.postTranslate(-distanceX, -distanceY);
            invalidate();
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGesture.onTouchEvent(event);
        gestures.onTouchEvent(event);
        return true;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
