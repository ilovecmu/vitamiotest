package com.example.gangzhang.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

public class ImageViewDetail extends Activity{
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题

        setContentView(R.layout.imageview_detail);
        String position =  getIntent().getExtras().getString(PictureLookUp.KEY_IMAGE_URL);
        Log.d("myapp", "position=" + position);
        Bitmap bitmap = BitmapFactory.decodeFile(position);
        imageView = (ImageView)findViewById(R.id.image);
        imageView.setImageBitmap(bitmap);
    }
}