package com.slogup.catalog.custom_widget;

import android.app.Activity;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.slogup.catalog.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by CharmSae on 7/20/16.
 */
public class SurfacePreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final int IMAGE_WIDTH = 1080; // 찍을 높이
    private static final int IMAGE_HEIGHT = 1920; // 찍을 넓이
    SurfaceHolder holder;
    public static Camera mCamera = null;
    public static byte[] surfaceData;
    Activity activity;

    public SurfacePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SurfacePreview(Context context) {
        super(context);
        init(context);
    }

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

//            Display display = activity.getWindowManager().getDefaultDisplay();
//            Point sizepoint = new Point();
//            display.getSize(sizepoint);
//            int width = sizepoint.x;
//            int height = sizepoint.y;
//
//            Camera.Parameters parameters = mCamera.getParameters();
//
//            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
//
//            Camera.Size optimalSize;
//
//            optimalSize = getOptimalPreviewSize(sizes, width, height);
//            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
//            mCamera.setParameters(parameters);

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

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
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