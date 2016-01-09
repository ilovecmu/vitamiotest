/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.gangzhang.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 网格视图显示Activity
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class PictureLookUp extends Fragment{

    private ArrayList<String> imageUrls;					// 图片Url

    private  DisplayImageOptions options;		// 显示图片的设置
    private GridView listView;
    private Activity mActivity;
    private ImageLoader imageLoader;
    public final static String KEY_IMAGE_URL="url";
    private TextView textViewIndicator;
    private int screenWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_gridview,container,false);
        DisplayMetrics metric = new DisplayMetrics();

        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;     // 屏幕宽度（像素）

        FileScan fs  = new FileScan();
        File appDir = new File(Environment.getExternalStorageDirectory(), "Player");
        imageUrls = new ArrayList<String>();

        if(appDir.exists()) {

            HashMap<String, String> hashMap = fs.getMusicListOnSys(appDir);
            Iterator iter = hashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                Log.d("myapp", "key=" + key);
                Log.d("myapp", "val=" + val);
                imageUrls.add(val);
            }
        }
        Log.d("myapp","imageUrls="+imageUrls.size());

        textViewIndicator= (TextView)view.findViewById(R.id.empty_indicator);

        if(imageUrls.size()==0){
            textViewIndicator.setVisibility(View.VISIBLE);
            return  view;
        }else {
            textViewIndicator.setVisibility(View.INVISIBLE);

        }

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.fx_setting_about_normal)
                .showImageForEmptyUri(R.drawable.fx_setting_about_normal)
                .showImageOnFail(R.drawable.fx_setting_about_normal)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();

        listView = (GridView) view.findViewById(R.id.gridview);
        ((GridView) listView).setAdapter(new ImageAdapter());			// 填充数据
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startImagePagerActivity(position);
            }
        });

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mActivity));


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        ((MainActivity) activity).onSectionAttached(2);
    }

    private void startImagePagerActivity(int position) {
        Log.d("myapp", "position=" + position);
//        Intent intent = new Intent(mActivity, ImageViewDetail.class);
//        intent.putExtra(KEY_IMAGE_URL, imageUrls.get(position));
//        startActivity(intent);

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        try {
            File file = new File(imageUrls.get(position));
            intent.setDataAndType(Uri.fromFile(file), "image/*");
            startActivity(intent);
        }catch (Exception e){

        }

    }

    public class ImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
//            if (convertView == null) {
//                imageView = (ImageView) LayoutInflater.from(mActivity).inflate(R.layout.item_grid_image, parent, false);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//            imageView.setImageResource(R.drawable.fx_setting_about_normal);

            imageView = (ImageView) LayoutInflater.from(mActivity).inflate(R.layout.item_grid_image, parent, false);


            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
            imageLoader.displayImage("file://"+imageUrls.get(position), imageView, options);

            return imageView;
        }

    }
}