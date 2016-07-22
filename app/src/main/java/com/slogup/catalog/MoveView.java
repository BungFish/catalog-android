package com.slogup.catalog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by CharmSae on 7/20/16.
 */
public class MoveView extends ImageView implements View.OnTouchListener {
    private Matrix matrix = new Matrix();
    private Matrix moveMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private static final int WIDTH = 0;
    private static final int HEIGHT = 1;

    private float[] value = new float[9];
    private Drawable drawable;
    private int width;
    private int height;
    private int imageWidth;
    private int imageHeight;
    private int scaledImageWidth;
    private int scaledImageHeight;

    public MoveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MoveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoveView(Context context) {
        this(context, null);
        setOnTouchListener(this);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    protected void init() {

        this.matrix.getValues(value); // 매트릭스 값
        width = this.getWidth(); // 뷰크기
        height = this.getHeight();
        drawable = this.getDrawable();
        if (drawable == null) return;
        imageWidth = drawable.getIntrinsicWidth(); //실제 이미지 너비
        imageHeight = drawable.getIntrinsicHeight(); //실제 이미지 높이

        if (imageWidth > width || imageHeight > height) {
            setImageFitOnView();
        }

        setCenter();
        matrix.setValues(value);
        setImageMatrix(matrix);
    }

    public void initImageReal() {
        value[0] = 1;
        value[4] = 1;
        value[2] = 0;
        value[5] = 0;
        matrix.setValues(value);
        setImageMatrix(matrix);
    }

    public void initImageFit() {

        if (imageWidth > width || imageHeight > height) {
            setImageFitOnView();
        }
        setCenter();
        value[2] = 0;
        value[5] = 0;
        matrix.setValues(value);
        setImageMatrix(matrix);

    }

    // 뷰에 맞게 이미지 사이즈 설정
    private void setImageFitOnView() {
        // 이미지의 가로와 세로의 길이 비교하여 target 설정
        int target = WIDTH;
//        if (imageWidth < imageHeight)
//        	target = HEIGHT;

        // 너비와 높이 중 큰값을 뷰에 맞도록 value 값 설정
        if (target == WIDTH) {
            value[4] = (float) width / imageWidth;
            value[0] = value[4];
        }
//        else if (target == HEIGHT)
//    	{
//        	value[4] = (float)height / imageHeight;
//        	value[0] = value[4];
//    	}

        scaledImageWidth = (int) (imageWidth * value[0]);
        scaledImageHeight = (int) (imageHeight * value[4]);

        // 너비(높이)가 뷰와 같지만 높이(너비)는 뷰보다 클 경우 이미지 크기를 높이(너비)에 맞게 조정
//        if (scaledImageWidth > width)
//    	{
//        	value[4] = (float) width / imageWidth;
//        	value[0] = value[4];
//    	}
//        if (scaledImageHeight > height)
//        {
//        	value[4] = (float)height / imageHeight;
//        	value[0] = value[4];
//        }
    }

    private void setCenter() {
        scaledImageWidth = (int) (imageWidth * value[0]);
        scaledImageHeight = (int) (imageHeight * value[4]);

        if (scaledImageWidth < width) {
            value[2] = (float) (width / 2) - (float) (scaledImageWidth / 2);
        }
        if (scaledImageHeight < height) {
            value[5] = (float) (height / 2) - (float) (scaledImageHeight / 2);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                moveMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 20f) // 20f를 변경하여 줌을 인식하는 두 손가락의 거리값을 변경함
                {
                    moveMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) // 이동
                {
                    matrix.set(moveMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (mode == ZOOM) // 줌
                {
                    float newDist = spacing(event);
                    if (newDist > 5f) {
                        matrix.set(moveMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        changeMatrixValue(matrix, view);
//        view.setImageMatrix(matrix);
        return true;
    }

    private void changeMatrixValue(Matrix matrix, ImageView view) {
        matrix.getValues(value);
        if (drawable == null) return;

        // 이미지가 바깥으로 나가지 않도록.
        if (value[2] < width - scaledImageWidth) value[2] = width - scaledImageWidth;
        if (value[5] < height - scaledImageHeight) value[5] = height - scaledImageHeight;
        if (value[2] > 0) value[2] = 0;
        if (value[5] > 0) value[5] = 0;

        // 실제크기 이상 확대 되지 않도록
        if (value[0] > 1 || value[4] > 1) {
            value[0] = 1;
            value[4] = 1;
        }

        // 화면보다 작게 축소 하지 않도록
        if (imageWidth > width || imageHeight > height) {
//            if (scaledImageWidth < width && scaledImageHeight < height)
//            {
//                setImageFitOnView();
//            }
            if (scaledImageWidth < width) {
                setImageFitOnView();
            }
        } else // 원래부터 작은 이미지는 본래 크기보다 작게 되지 않도록
        {
            if (value[0] < 1) value[0] = 1;
            if (value[4] < 1) value[4] = 1;
        }
        setCenter();
        matrix.setValues(value);
        setImageMatrix(matrix);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}