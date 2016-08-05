package com.himart.showroom.custom_widget;

import android.app.Activity;
import android.content.Context;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.himart.showroom.R;

import java.util.List;

/**
 * Created by CharmSae on 7/20/16.
 */
public class SurfacePreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    SurfaceHolder holder;
    public static Camera mCamera = null;
    public static byte[] surfaceData;
    Activity activity;
    private Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;

    public SurfacePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SurfacePreview(Context context) {
        super(context);
        init(context);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//        setMeasuredDimension(width, height);
//
//        if (mSupportedPreviewSizes != null) {
//            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
//        }
//    }

    public void init(Context context) {
        holder = getHolder();
        holder.addCallback(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        activity = (Activity) context;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        try {

//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//            mCamera.setParameters(parameters);

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point sizepoint = new Point();
            display.getSize(sizepoint);
            int w = sizepoint.x;
            int h = sizepoint.y;
            Log.i("=====", w + " x " + h);
            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

            Camera.Size optimalSize;

            optimalSize = getOptimalPreviewSize(sizes, h, w);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
//            parameters.setPreviewSize(1920, 1080);
            Log.i("=====", optimalSize.width + " x " + optimalSize.height);
            mCamera.setParameters(parameters);

            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewCallback(this);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();


        } catch (Exception e) {
            Log.e(getContext().getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) width / height;
        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        int i = 0;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            Log.i("camera size " + i++, "" + size.width + " x " + size.height);
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            Log.i("optimal size", "not found");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.i("optimal size", "" + optimalSize.width + " x " + optimalSize.height);
        return optimalSize;
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//        Camera.Parameters params = mCamera.getParameters();
//        int w = params.getPreviewSize().width;
//        int h = params.getPreviewSize().height;
//        int format = params.getPreviewFormat();
//        YuvImage image = new YuvImage(data, format, w, h, null);
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        Rect area = new Rect(0, 0, w, h);
//        image.compressToJpeg(area, 100, out);
//        Bitmap bm = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
//
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
//        MainActivity.shareBitmap = rotatedBitmap;
        surfaceData = data;
        mCamera = camera;

    }
}