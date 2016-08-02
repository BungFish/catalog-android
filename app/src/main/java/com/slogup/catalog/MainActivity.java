package com.slogup.catalog;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.slogup.catalog.adapter.ProductRecyclerAdapter;
import com.slogup.catalog.custom_widget.ScalableImageView;
import com.slogup.catalog.custom_widget.SimpleProgressDialog;
import com.slogup.catalog.custom_widget.SurfacePreview;
import com.slogup.catalog.manager.AppManager;
import com.slogup.catalog.models.Product;
import com.slogup.catalog.models.ProductCategory;
import com.slogup.catalog.network.APIConstants;
import com.slogup.catalog.network.APIRequester;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

public class MainActivity extends AppCompatActivity implements ProductRecyclerAdapter.ClickListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int BACK_PRESS_TIME_DELAY = 2000;
    private static long sBackPressedTime;

    private Context _this = this;
    public static Bitmap shareBitmap;
    private FrameLayout shareLayout;
    private ImageView captureButton;
    private ScalableImageView mImageView;
    private Button openGalleryButton;
    private ImageView previousImageButton;
    private ImageView nextImageButton;
    private ArrayList<Drawable> drawableArray = new ArrayList<>();
    private TextView productCountTextView;
    private TextView productNameTextView;
    private TextView productPriceTextView;
    private ArrayList<Product> mProductArrayList = new ArrayList<>();
    private ArrayList<ProductCategory> mProductCategoryArrayList = new ArrayList<>();
    private TextView manufacturerTextView;
    private TextView productDescriptionTextView;
    private RecyclerView productRecyclerView;
    private ProductRecyclerAdapter productRecyclerAdapter;
    private SimpleProgressDialog mSimpleProgressDialog;
    private MediaScannerConnection mediaScannerConnection;
    private String folder;
    private String filename;
    private File myImage;
    private File storagePath;
    private Button categoryButton;
    private AlertDialog alert;
    private ImageView phoneButton;
    private ImageView locationButton;
    private Animation slideInFromBottom;
    private boolean firstLoad = true;
    private boolean isViewInit = false;
    private LinearLayout topLayout;
    private RelativeLayout countLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSimpleProgressDialog = new SimpleProgressDialog(this);
        slideInFromBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        productRecyclerView = (RecyclerView) findViewById(R.id.product_list);
        LinearLayoutManager mMyHamLinearLayoutManager = new LinearLayoutManager(this);
        mMyHamLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mMyHamLinearLayoutManager.setSmoothScrollbarEnabled(true);
        productRecyclerView.setLayoutManager(mMyHamLinearLayoutManager);

        productRecyclerAdapter = new ProductRecyclerAdapter(this);
        productRecyclerAdapter.setClickListener(this);
        productRecyclerView.setAdapter(productRecyclerAdapter);

        manufacturerTextView = (TextView) findViewById(R.id.manufacturer);
        productNameTextView = (TextView) findViewById(R.id.productName);
        productDescriptionTextView = (TextView) findViewById(R.id.productDescription);

        productCountTextView = (TextView) findViewById(R.id.productCount);

        shareLayout = (FrameLayout) findViewById(R.id.shareLayout);
        captureButton = (ImageView) findViewById(R.id.captureButton);
        openGalleryButton = (Button) findViewById(R.id.openGalleryButton);
        categoryButton = (Button) findViewById(R.id.categoryButton);
//        previousImageButton = (ImageView) findViewById(R.id.previousImageButton);
//        nextImageButton = (ImageView) findViewById(R.id.nextImageButton);
        mImageView = (ScalableImageView) findViewById(R.id.imageView);

        locationButton = (ImageView) findViewById(R.id.locationButton);
        phoneButton = (ImageView) findViewById(R.id.phoneButton);

        topLayout = (LinearLayout) findViewById(R.id.topLayout);
        countLayout = (RelativeLayout) findViewById(R.id.countLayout);

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

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String categories[] = new String[mProductCategoryArrayList.size()];
                for (int i = 0; i < mProductCategoryArrayList.size(); i++) {
                    categories[i] = mProductCategoryArrayList.get(i).getProductCategoryName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                builder.setTitle("카테고리 선택");
                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        getProductData(mProductCategoryArrayList.get(item));
                        alert.dismiss();
                    }
                });
                alert = builder.create();
                alert.show();
            }
        });
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHelper.showDialog(_this, getResources().getString(R.string.himart_mobile), "열기", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.e-himart.co.kr/app/common/offLineShopSearchTab1"));
                        startActivity(browserIntent);
                    }
                });
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHelper.showDialog(_this, getResources().getString(R.string.customer_center), "연결", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri number = Uri.parse("tel:" + String.valueOf(R.string.customer_center_num));
                        Intent dial = new Intent(Intent.ACTION_DIAL, number);
                        startActivity(dial);
                    }
                });
            }
        });

//        previousImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (productPosition > 0) {
//                    productPosition--;
//                    setMainView(productPosition, tempPosition);
//                } else {
//                    Toast.makeText(getApplicationContext(), "처음 제품 입니다", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        nextImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (productPosition < mProductArrayList.size() - 1) {
//                    productPosition++;
//                    setMainView(productPosition, tempPosition);
//                } else {
//                    Toast.makeText(getApplicationContext(), "마지막 제품 입니다", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
    }

    public void initView() {
        productRecyclerAdapter.setData(AppManager.productArrayList);
        setMainView();
    }

    public void capture() {

        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            captureButton.setClickable(false);
            shootSound();
            mSimpleProgressDialog.show();
            saveCapturedImage();
        } else {
            Toast.makeText(getApplicationContext(), "SD 카드를 삽입 하세요.", Toast.LENGTH_SHORT).show();
        }

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

    }

    public void saveCapturedImage() {
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


        temp = createWaterMarkBitmap(h, w);

        int cx = (canvas.getWidth() - temp.getWidth()) / 2;
        int cy = (canvas.getHeight() - temp.getHeight()) / 2;

        canvas.drawBitmap(temp, cx, cy, null);

        //=================

        folder = "/catalog/";
        filename = System.currentTimeMillis() + ".jpg";

        storagePath = new File(Environment.
                getExternalStorageDirectory() + folder);
        storagePath.mkdirs();

        myImage = new File(storagePath, filename);

        try {
            FileOutputStream out = new FileOutputStream(myImage);
            overlay.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();

            Toast.makeText(getApplicationContext(), folder + filename + "에 저장되었습니다", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, CapturedImageActivity.class);

//            intent.setDataAndType(Uri.parse("file://" + myImage.getAbsolutePath()), "image/*");

            intent.putExtra("path", myImage.getAbsolutePath());
            startActivityForResult(intent, CapturedImageActivity.REQUEST_CODE);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                Uri contentUri = Uri.fromFile(storagePath);
//                mediaScanIntent.setData(contentUri);
//                sendBroadcast(mediaScanIntent);
//            } else {
//                sendBroadcast(new Intent(
//                        Intent.ACTION_MEDIA_MOUNTED,
//                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//            }
            mediaScannerConnection = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    if (mediaScannerConnection.isConnected()) {
                        mediaScannerConnection.scanFile(myImage.getAbsolutePath(), "image/*");
                    }
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    mediaScannerConnection.disconnect();
                    mSimpleProgressDialog.dismiss();
                }
            });
            mediaScannerConnection.connect();

        } catch (FileNotFoundException e) {
            Log.d("In Saving File", e + "");
        } catch (IOException e) {
            Log.d("In Saving File", e + "");
        }

        overlay.recycle();
        overlay = null;
    }

    public void shootSound() {
        MediaActionSound sound = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            sound = new MediaActionSound();
            sound.play(MediaActionSound.SHUTTER_CLICK);
        }

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

    public void getProductData(final ProductCategory productCategory) {

        String uri = String.format("?productCategoryId=%1$s", productCategory.getProductCategoryId());

        APIRequester apiRequester = new APIRequester(this);
        apiRequester.requestGET(APIConstants.API_CATALOG_PRODUCT + uri, null, new APIRequester.APICallbackListener() {
            @Override
            public void onBefore() {
                mSimpleProgressDialog.show();
            }

            @Override
            public void onSuccess(JSONObject object) {
                try {

                    JSONArray list = object.getJSONArray(APIConstants.COMMON_RESP_LIST);

                    mProductArrayList.clear();

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

                    productRecyclerAdapter.setData(mProductArrayList);

                    if (firstLoad) {
                        productRecyclerView.startAnimation(slideInFromBottom);
                        productRecyclerView.setVisibility(View.VISIBLE);
                        countLayout.setVisibility(View.VISIBLE);
                        firstLoad = false;
                    }

                    categoryButton.setText(productCategory.getProductCategoryName());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSimpleProgressDialog.dismiss();
            }

            @Override
            public void onError(Error error) {
                mSimpleProgressDialog.dismiss();
                Log.i(LOG_TAG, error.toString());
                CommonHelper.showDialog(_this, error.getMessage(), getResources().getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSimpleProgressDialog.show();
                        getProductData(productCategory);
                    }
                });
            }
        });
    }

    public void getCategory() {
        APIRequester apiRequester = new APIRequester(this);
        apiRequester.requestGET(APIConstants.API_CATALOG_CATEGORY, null, new APIRequester.APICallbackListener() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onSuccess(JSONObject object) {
                try {

                    JSONArray list = object.getJSONArray(APIConstants.COMMON_RESP_LIST);

                    for (int i = 0; i < list.length(); i++) {

                        JSONObject productCategoryObject = list.getJSONObject(i);
                        ProductCategory productCategory = new ProductCategory(productCategoryObject.getInt("id"), productCategoryObject.getString("categoryName"));
                        mProductCategoryArrayList.add(productCategory);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSimpleProgressDialog.dismiss();
            }

            @Override
            public void onError(Error error) {
                mSimpleProgressDialog.dismiss();
                Log.i(LOG_TAG, error.toString());
                CommonHelper.showDialog(_this, error.getMessage(), getResources().getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSimpleProgressDialog.show();
                        getCategory();
                    }
                });
            }
        });
    }

    public void setMainView() {
        mSimpleProgressDialog.show();

        int currentPosition = productRecyclerAdapter.getSelectedPosition();
        Product product = productRecyclerAdapter.getItem(currentPosition);

        manufacturerTextView.setText(product.getManufacturer());
        productNameTextView.setText(product.getProductName());
        productDescriptionTextView.setText(product.getDescription());

        Picasso.with(_this).load(CommonHelper.imageUrlFormatter(product, 0)).into(mImageView, new Callback() {
            @Override
            public void onSuccess() {
                if (mImageView.getDrawable() != null) {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int screenWidth = size.x;
                    int screenHeight = size.y - 100;

                    Matrix matrix = new Matrix();

                    Drawable drawable = mImageView.getDrawable();
                    //you should call after the bitmap drawn
                    Rect bounds = drawable.getBounds();
                    int width = bounds.width();
                    int height = bounds.height();

//        Matrix m = new Matrix();
//        m.set(mImageView.getImageMatrix());
//        float[] values = new float[9];
//        m.getValues(values);
//        float bitmapWidth = values[Matrix.MSCALE_X] * drawable.getIntrinsicWidth(); //your bitmap's width
//        float bitmapHeight = values[Matrix.MSCALE_Y] * drawable.getIntrinsicHeight(); //your the bitmap's height

                    matrix.postTranslate((screenWidth / 2) - (width / 2), (screenHeight / 2) - (height / 2));
                    mImageView.setImageMatrix(matrix);

                    isViewInit = true;
                    mSimpleProgressDialog.dismiss();
                }
            }

            @Override
            public void onError() {
                mSimpleProgressDialog.dismiss();
                Toast.makeText(_this, "이미지 로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        productCountTextView.setText((currentPosition + 1) + " / " + AppManager.productArrayList.size());

    }

    @Override
    public void itemClick(View view, int position) {
        productRecyclerAdapter.setSelectedPosition(position);
        setMainView();
    }

    private Bitmap createWaterMarkBitmap(int width, int height) {
        RelativeLayout watermark = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.watermark, null);
        watermark.setLayoutParams(new RelativeLayout.LayoutParams(width, height));

        watermark.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        watermark.layout(0, 0, watermark.getMeasuredWidth(), watermark.getMeasuredHeight());

        final Bitmap clusterBitmap = Bitmap.createBitmap(watermark.getMeasuredWidth(), watermark.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(clusterBitmap);
        watermark.draw(canvas);

        return clusterBitmap;
    }

    @Override
    public void onBackPressed() {

        if (sBackPressedTime + BACK_PRESS_TIME_DELAY > System.currentTimeMillis()) {

            super.onBackPressed();

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.one_more_click_findish_app),
                    Toast.LENGTH_SHORT).show();
        }

        sBackPressedTime = System.currentTimeMillis();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && !isViewInit) {
            initView();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CapturedImageActivity.REQUEST_CODE) {
            captureButton.setClickable(true);
        }
    }
}