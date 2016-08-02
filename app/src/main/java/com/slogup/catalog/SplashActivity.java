package com.slogup.catalog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.slogup.catalog.manager.AppManager;
import com.slogup.catalog.models.Product;
import com.slogup.catalog.models.ProductCategory;
import com.slogup.catalog.network.APIConstants;
import com.slogup.catalog.network.APIRequester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    private static final String LOG_TAG = SplashActivity.class.getSimpleName();

    private int CAMERA_PERMISSION_REQUEST = 1;
    private Context _this = this;
    private ImageView splashImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        splashImageView = (ImageView) findViewById(R.id.splashImageView);
//        Picasso.with(_this).load(R.drawable.icon).into(splashImageView);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, CAMERA_PERMISSION_REQUEST);

    }

//    public void appInit() {
//        APIRequester apiRequester = new APIRequester(this);
//        apiRequester.requestGET(APIConstants.API_ETC_META, null, new APIRequester.APICallbackListener() {
//            @Override
//            public void onBefore() {
//            }
//
//            @Override
//            public void onSuccess(JSONObject object) {
//                AppManager.meta = new Metadata(object);
//
//                getProductData();
//
//            }
//
//            @Override
//            public void onError(Error error) {
//                Log.i(LOG_TAG, error.toString());
//                CommonHelper.showDialog(_this, error.getMessage(), getResources().getString(R.string.retry), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        appInit();
//                    }
//                });
//            }
//        });
//    }

    public void getProductData() {

        APIRequester apiRequester = new APIRequester(this);
        apiRequester.requestGET(APIConstants.API_CATALOG_PRODUCT, null, new APIRequester.APICallbackListener() {
            @Override
            public void onBefore() {
            }

            @Override
            public void onSuccess(JSONObject object) {
                try {

                    JSONArray list = object.getJSONArray(APIConstants.COMMON_RESP_LIST);

                    AppManager.productArrayList.clear();

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

                        AppManager.productArrayList.add(product);

                    }

                    startMainActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Error error) {
                Log.i(LOG_TAG, error.toString());
                CommonHelper.showDialogWithNegative(_this, error.getMessage(), getResources().getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getProductData();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });
    }

    public void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    getProductData();

                } else {

                    finish();
                }

            }
        }
    }
}
