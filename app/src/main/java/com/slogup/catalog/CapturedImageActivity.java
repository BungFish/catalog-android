package com.slogup.catalog;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class CapturedImageActivity extends AppCompatActivity {

    public static int REQUEST_CODE = 1;
    private ImageView capturedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_image);

        Intent intent = getIntent();

        capturedImageView = (ImageView) findViewById(R.id.capturedImageView);
        Picasso.with(this).load(Uri.parse("file://" + intent.getStringExtra("path"))).into(capturedImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 1000);
    }
}
