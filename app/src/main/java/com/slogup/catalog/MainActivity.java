package com.slogup.catalog;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.slogup.catalog.manager.AppManager;
import com.slogup.catalog.models.Metadata;
import com.slogup.catalog.models.Product;
import com.slogup.catalog.models.ProductCategory;
import com.slogup.catalog.network.APIConstants;
import com.slogup.catalog.network.APIRequester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static Bitmap shareBitmap;
    private FrameLayout shareLayout;
    private ImageView captureButton;
    private ImageView mImageView;
    private Button openGalleryButton;
    private ImageView previousImageButton;
    private ImageView nextImageButton;
    private ArrayList<Drawable> drawableArray = new ArrayList<>();
    private int category = 0;
    private int productPosition = 0;
    private int tempPosition = 0;
    private TextView productCountTextView;
    private TextView productTitleTextView;
    private TextView productPriceTextView;
    private ArrayList<Product> mProductArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appInit();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                0);

        setContentView(R.layout.activity_main);

        productTitleTextView = (TextView) findViewById(R.id.productTitle);
        productPriceTextView = (TextView) findViewById(R.id.productPrice);
        productCountTextView = (TextView) findViewById(R.id.productCount);

        shareLayout = (FrameLayout) findViewById(R.id.shareLayout);
        captureButton = (ImageView) findViewById(R.id.captureButton);
//        upSizeImageButton = (Button) findViewById(R.id.upSizeImageButton);
//        downSizeImageButton = (Button) findViewById(R.id.downSizeImageButton);
        openGalleryButton = (Button) findViewById(R.id.openGalleryButton);
        previousImageButton = (ImageView) findViewById(R.id.previousImageButton);
        nextImageButton = (ImageView) findViewById(R.id.nextImageButton);
        mImageView = (ImageView) findViewById(R.id.imageView);

        drawableArray.add(getResources().getDrawable(R.drawable.sample));
        drawableArray.add(getResources().getDrawable(R.drawable.card_normal));
        drawableArray.add(getResources().getDrawable(R.drawable.card_plus));

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        previousImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productPosition > 0) {
                    productPosition--;

                    Glide.with(getApplicationContext()).load(
                            APIConstants.ROOT_URL_DEVELOPMENT + AppManager.meta.getRootUrl() + mProductArrayList.get(productPosition).getProductImageArrayList().get(tempPosition).getImageUrl())
                            .into(mImageView);
                    productCountTextView.setText((productPosition + 1) + "/" + mProductArrayList.size());

                } else {
                    Toast.makeText(getApplicationContext(), "처음 제품 입니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productPosition < mProductArrayList.size() - 1) {
                    productPosition++;
                    Glide.with(getApplicationContext()).load(
                            APIConstants.ROOT_URL_DEVELOPMENT + AppManager.meta.getRootUrl() + mProductArrayList.get(productPosition).getProductImageArrayList().get(tempPosition).getImageUrl())
                            .into(mImageView);
                    productCountTextView.setText(String.valueOf((productPosition + 1) + "/" + mProductArrayList.size()));
                } else {
                    Toast.makeText(getApplicationContext(), "마지막 제품 입니다", Toast.LENGTH_SHORT).show();
                }

            }
        });

        productCountTextView.setText((productPosition + 1) + "/" + drawableArray.size());

    }

    public void capture() {

        Camera.Parameters params = SurfacePreview.mCamera.getParameters();

        int w = params.getPreviewSize().width;
        int h = params.getPreviewSize().height;
        int format = params.getPreviewFormat();
        YuvImage image = new YuvImage(SurfacePreview.surfaceData, format, w, h, null);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        Rect area = new Rect(0, 0, w, h);
        image.compressToJpeg(area, 100, byteOut);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteOut.toByteArray(), 0, byteOut.size());

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap overlay = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

//        shareBitmap = rotatedBitmap;

        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(overlay, 0, 0, null);

        Log.i("overlay size", overlay.getWidth() + " x " + overlay.getHeight());

        shareLayout.destroyDrawingCache();
        shareLayout.buildDrawingCache();
        Bitmap bm = shareLayout.getDrawingCache();

        Bitmap temp = Bitmap.createScaledBitmap(bm, overlay.getWidth(), overlay.getHeight(), false);
        canvas.drawBitmap(temp, 0, 0, null);

        Log.i("shareLayout size", temp.getWidth() + " x " + temp.getHeight());

//        FileOutputStream out;
//
//        String filename = System.currentTimeMillis() + ".jpg";
//        String temp = "/catalog/" + filename;
//
//        final File dir = new File(Environment.getExternalStorageDirectory().toString() + "/catalog");
//        Log.i("===", dir.mkdirs() + "");
//
//        try {
//            out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + temp);
//            Log.i("===", overlay.compress(Bitmap.CompressFormat.JPEG, 100, out) + "");
//            Toast.makeText(getApplicationContext(), temp + "에 저장되었습니다", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Log.d("screenshot", String.valueOf(e));
//            e.printStackTrace();
//        }

        String folder = "/catalog/";
        String filename = System.currentTimeMillis() + ".jpg";

        File storagePath = new File(Environment.
                getExternalStorageDirectory() + folder);
        storagePath.mkdirs();

        File myImage = new File(storagePath, filename);

        try {
            FileOutputStream out = new FileOutputStream(myImage);
            overlay.compress(Bitmap.CompressFormat.JPEG, 80, out);

            out.flush();
            out.close();

            Toast.makeText(getApplicationContext(), folder + filename + "에 저장되었습니다", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d("In Saving File", e + "");
        } catch (IOException e) {
            Log.d("In Saving File", e + "");
        }

        overlay.recycle();
        overlay = null;

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.parse("file://" + myImage.getAbsolutePath()), "image/*");
        startActivity(intent);
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/catalog/");
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public static final Uri insertImage(ContentResolver cr,
                                        Bitmap source,
                                        String title,
                                        String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 75, imageOut);
                } finally {
                    imageOut.close();
                }

            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        return url;
    }

    public void getProductData() {
        APIRequester apiRequester = new APIRequester(this);
        apiRequester.requestGET(APIConstants.API_CATALOG_PRODUCT, null, new APIRequester.APICallbackListener() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onSuccess(JSONObject object) {
                mProductArrayList = new ArrayList<>();

                try {

                    JSONArray list = object.getJSONArray(APIConstants.COMMON_RESP_LIST);

                    for (int i = 0; i < list.length(); i++) {

                        Product product = new Product();

                        JSONObject productObject = list.getJSONObject(i);

                        product.setProductName(productObject.getString("productName"));
                        product.setDescription(productObject.getString("description"));
                        product.setManufacturer(productObject.getString("manufacturer"));
                        product.setPrice(productObject.getInt("price"));

                        JSONObject jsonObject = productObject.getJSONObject("productCategory");
                        product.setProductCategory(new ProductCategory(jsonObject.getInt("id"), jsonObject.getString("categoryName")));

                        product.setProductImageArrayList(productObject.getJSONArray("productImages"));

                        mProductArrayList.add(product);
                    }

                    Glide.with(getApplicationContext()).load(
                            APIConstants.ROOT_URL_DEVELOPMENT + AppManager.meta.getRootUrl() + mProductArrayList.get(productPosition).getProductImageArrayList().get(tempPosition).getImageUrl())
                            .into(mImageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Error error) {
                Log.i(LOG_TAG, error.toString());
            }
        });
    }

    public void appInit() {
        APIRequester apiRequester = new APIRequester(this);
        apiRequester.requestGET(APIConstants.API_ETC_META, null, new APIRequester.APICallbackListener() {
            @Override
            public void onBefore() {
            }

            @Override
            public void onSuccess(JSONObject object) {
                AppManager.meta = new Metadata(object);
                Log.i(LOG_TAG, AppManager.meta.getRootUrl());
                getProductData();
            }

            @Override
            public void onError(Error error) {
                Log.i(LOG_TAG, error.toString());
            }
        });
    }

}