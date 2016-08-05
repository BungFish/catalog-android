package com.himart.showroom;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.himart.showroom.manager.AppManager;
import com.himart.showroom.models.Metadata;
import com.himart.showroom.models.Product;
import com.himart.showroom.models.ProductCategory;
import com.himart.showroom.network.APIConstants;
import com.himart.showroom.network.APIRequester;

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
        setContentView(com.himart.showroom.R.layout.activity_splash);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void appInit() {
        APIRequester apiRequester = new APIRequester(this);
        apiRequester.requestGET(APIConstants.API_ETC_META_STD, null, new APIRequester.APICallbackListener() {
            @Override
            public void onBefore() {
            }

            @Override
            public void onSuccess(JSONObject object) {
                AppManager.meta = new Metadata();
                AppManager.meta.setStd(object);

                try {
                    PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);

                    if (AppManager.appVersion.equals(i.versionName)) {
                        getProductData();
                    } else {
                        CommonHelper.showDialogWithNegative(_this, "새로운 앱 버전을 업데이트 받아야합니다.", getResources().getString(com.himart.showroom.R.string.update), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(it);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    Log.i(LOG_TAG, e.getMessage());
                }

            }

            @Override
            public void onError(Error error) {
                Log.i(LOG_TAG, error.toString());
                CommonHelper.showDialogWithNegative(_this, error.getMessage(), getResources().getString(com.himart.showroom.R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appInit();
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

                        if (!productObject.isNull("description")) {
                            product.setDescription(productObject.getString("description"));
                        }

                        product.setManufacturer(productObject.getString("manufacturer"));

                        if (!productObject.isNull("price")) {
                            product.setPrice(productObject.getInt("price"));
                        }

                        if (!productObject.isNull("productCategory")) {
                            JSONObject jsonObject = productObject.getJSONObject("productCategory");
                            if (!jsonObject.isNull("id") && !jsonObject.isNull("categoryName"))
                                product.setProductCategory(new ProductCategory(jsonObject.getInt("id"), jsonObject.getString("categoryName")));
                        }

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
                CommonHelper.showDialogWithNegative(_this, error.getMessage(), getResources().getString(com.himart.showroom.R.string.retry), new View.OnClickListener() {
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

                    appInit();

                } else {

                    finish();
                }

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, CAMERA_PERMISSION_REQUEST);
    }
}
