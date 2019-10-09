package myproject.kosanku.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import myproject.kosanku.R;

public class FulllImageActivity extends AppCompatActivity {

    Intent i;
    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulll_image);

        i = getIntent();
        downloadUrl = i.getStringExtra("url");

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);


        Glide.with(this).load(downloadUrl).into(photoView);


    }
}
