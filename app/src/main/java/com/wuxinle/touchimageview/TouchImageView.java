package com.wuxinle.touchimageview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.wuxinle.touchimageview.utils.L;


/**
 * Created by wuxin on 2015/12/25.
 */
public class TouchImageView extends ImageView {

    float x_down = 0;
    float y_down = 0;

    float scale;

    PointF mid = new PointF();
    float oldDist = 1f;
    float oldRotation = 0;
    Matrix mMatrix = new Matrix();
    Matrix matrix1 = new Matrix();
    Matrix savedMatrix = new Matrix();

    Matrix oldMatrix = new Matrix();
    Matrix yuanMatrix = new Matrix();


    Paint paint;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    int mode = NONE;

    private int matrixCheck; //检查

    boolean isBug = false;

    int widthScreen;
    int heightScreen;

    float rotation;
    float finalRotation;

    Bitmap mBitmap;//用户图片

    float[] dst = new float[2];

    private int designWidth;
    private int designHeight;

    private float maxScale = 5f;

    private int isTrue = 1111;
    private int isYueJie = 2222;
    private int isScaleYue = 3333;


    public TouchImageView(Activity activity) {
        super(activity);
        init();
    }

    private void init() {
        mMatrix = new Matrix();
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setBitmap(int resId) {
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();
        canvas.drawBitmap(mBitmap, mMatrix, paint);
        canvas.restore();
    }

    /**
     * 获取缩放比例
     *
     * @return
     */
    public float getNowWidth() {

        getFinalMatrix();

        float nowWidth = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return nowWidth;
    }

    /**
     * 获取距离图片Y轴距离比例
     */
    public float getYSize() {

        getFinalMatrix();


    /**
     * 获取倾斜角度
     */
    return y1;
}
    public float getAngle() {

        getFinalMatrix();

        if (Math.abs(y1 - y2) < 0.5 && x2 > x1) {
            finalRotation = 0;

            return finalRotation;
        }

        if (Math.abs(y1 - y2) < 0.5 && x1 > x2) {
            finalRotation = 180;

            return finalRotation;
        }

        finalRotation = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));

        return finalRotation;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //判断是否是bitmap区域
//        if (!isTouchOnBitmap(event)) {
//            return false;
//        }
//        super.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                savedMatrix.set(mMatrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                savedMatrix.set(mMatrix);
                midPoint(mid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    matrix1.set(savedMatrix);
                    rotation = rotation(event) - oldRotation;
                    float newDist = spacing(event);
                    if (isBug) {
                        scale = 1;
                    } else {
                        scale = newDist / oldDist;
                    }
                    matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
                    matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉
                    matrixCheck = matrixCheck();
                    if (matrixCheck == isTrue) {
                        mMatrix.set(matrix1);
                        invalidate();
                    } else if (matrixCheck == isYueJie) {
                        mMatrix.set(yuanMatrix);
                        invalidate();
                    }
                } else if (mode == DRAG) {
                    matrix1.set(savedMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
                    matrixCheck = matrixCheck();
                    if (matrixCheck == isTrue) {
                        mMatrix.set(matrix1);
                        invalidate();
                    } else if (matrixCheck == isYueJie) {
                        mMatrix.set(yuanMatrix);
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:

                mode = NONE;
                break;

        }
        return true;
    }

    //判断落点是否在操作后的图片上
    private boolean isTouchOnBitmap(MotionEvent event) {

        mMatrix.invert(oldMatrix);

        oldMatrix.mapPoints(dst, new float[]{event.getX(), event.getY()});

//        L.e("dst[0]===" + dst[0]);
//        L.e("dst[1]===" + dst[1]);
//
//        L.e("x===" + event.getX());
//        L.e("y===" + event.getY());

        if (dst[0] > 0 && dst[0] < mBitmap.getWidth() && dst[1] > 0 && dst[1] < mBitmap.getHeight()) {
            return true;
        }
        return false;
    }

    private int matrixCheck() {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * mBitmap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * mBitmap.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * mBitmap.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * mBitmap.getHeight() + f[5];
        float x4 = f[0] * mBitmap.getWidth() + f[1] * mBitmap.getHeight() + f[2];
        float y4 = f[3] * mBitmap.getWidth() + f[4] * mBitmap.getHeight() + f[5];


        // 图片现宽度
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

        double finalScale = width / designWidth;

        // 缩放比率判断
        if (finalScale > maxScale) {
            return isScaleYue;
        }

        // 出界判断
        if ((x1 > designWidth - 20 && x2 > designWidth - 20 && x3 > designWidth - 20 && x4 > designWidth - 20)
                || (x1 < 20 && x2 < 20 && x3 < 20 && x4 < 20)
                || (y1 > designHeight - 20 && y2 > designHeight - 20 && y3 > designHeight - 20 && y4 > designHeight - 20)
                || (y1 < 20 && y2 < 20 && y3 < 20 && y4 < 20)
                ) {
            return isYueJie;
        } else {
            return isTrue;
        }
    }

    float x1;
    float y1;
    float x2;
    float y2;
    float x3;
    float y3;
    float x4;
    float y4;

    private void getFinalMatrix() {
        float[] f = new float[9];
        mMatrix.getValues(f);
        // 图片4个顶点的坐标
        x1 = f[0] * 0 + f[1] * 0 + f[2];
        y1 = f[3] * 0 + f[4] * 0 + f[5];
        x2 = f[0] * mBitmap.getWidth() + f[1] * 0 + f[2];
        y2 = f[3] * mBitmap.getWidth() + f[4] * 0 + f[5];
        x3 = f[0] * 0 + f[1] * mBitmap.getHeight() + f[2];
        y3 = f[3] * 0 + f[4] * mBitmap.getHeight() + f[5];
        x4 = f[0] * mBitmap.getWidth() + f[1] * mBitmap.getHeight() + f[2];
        y4 = f[3] * mBitmap.getWidth() + f[4] * mBitmap.getHeight() + f[5];

        L.e("左上角X坐标====" + (f[0] * 0 + f[1] * 0 + f[2]));
        L.e("左上角Y坐标====" + (f[3] * 0 + f[4] * 0 + f[5]));
        L.e("右下角X坐标====" + (f[0] * mBitmap.getWidth() + f[1] * mBitmap.getHeight() + f[2]));
        L.e("右下角Y坐标====" + (f[3] * mBitmap.getWidth() + f[4] * mBitmap.getHeight() + f[5]));

        RectF r = new RectF((f[0] * 0 + f[1] * 0 + f[2]), (f[3] * 0 + f[4] * 0 + f[5]), (f[0] * mBitmap.getWidth() + f[1] * mBitmap.getHeight() + f[2]), (f[3] * mBitmap.getWidth() + f[4] * mBitmap.getHeight() + f[5]));
        L.e("中心点X ====" + r.centerX());
        L.e("中心点Y ====" + r.centerY());
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {

        float x = 0;
        float y = 0;
        try {
            isBug = false;
            x = event.getX(0) - event.getX(1);
            y = event.getY(0) - event.getY(1);
        } catch (IllegalArgumentException e) {
            isBug = true;
            e.printStackTrace();
        }
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x;
        double delta_y;
        double radians = 0;
        try {
            delta_x = (event.getX(0) - event.getX(1));
            delta_y = (event.getY(0) - event.getY(1));
            radians = Math.atan2(delta_y, delta_x);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return (float) Math.toDegrees(radians);
    }

    // 将移动，缩放以及旋转后的图层保存为新图片
    // 本例中沒有用到該方法，需要保存圖片的可以參考
    public Bitmap CreatNewPhoto() {
        Bitmap bitmap = Bitmap.createBitmap(widthScreen, heightScreen,
                Bitmap.Config.ARGB_8888); // 背景图片
        Canvas canvas = new Canvas(bitmap); // 新建画布
        canvas.drawBitmap(mBitmap, mMatrix, null); // 画图片
        canvas.save(Canvas.ALL_SAVE_FLAG); // 保存画布
        canvas.restore();
        return bitmap;
    }

    public int getDesignWidth() {
        return designWidth;
    }

    public void setDesignWidth(int designWidth) {
        this.designWidth = designWidth;
    }

    public int getDesignHeight() {
        return designHeight;
    }

    public void setDesignHeight(int designHeight) {
        this.designHeight = designHeight;
    }
}