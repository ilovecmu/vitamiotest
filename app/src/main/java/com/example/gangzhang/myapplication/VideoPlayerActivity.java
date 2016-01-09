/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.gangzhang.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.gangzhang.myapplication.R;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameRecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.provider.MediaStore;

public class VideoPlayerActivity extends Activity implements
        OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
        OnVideoSizeChangedListener, SurfaceHolder.Callback ,View.OnClickListener,MediaPlayer.OnErrorListener{

    private static final String TAG = "videoapp";
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private String path;
    private Bundle extras;
    private static final String MEDIA = "media";
    private static final int LOCAL_AUDIO = 1;
    private static final int STREAM_AUDIO = 2;
    private static final int RESOURCES_AUDIO = 3;
    private static final int LOCAL_VIDEO = 4;
    private static final int STREAM_VIDEO = 5;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private Animation operatingAnim;
    private ImageView mProgressBar;
    private ImageView mPlayImageView;
    private boolean mIsPlaying = false;
    private Button mImageCapture;
    private Button mImageRecord;
    private boolean mIsRecoding=false;
    private int mPlayerError1 =0;
    private int mPlayerError2 =0;
    private PowerManager powerManager;
    private  PowerManager.WakeLock wakeLock;
    private  SharedPreferences sp;
    private Boolean hardEnabled;

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getTitle());
        actionBar.setIcon(null);
        actionBar.setDisplayUseLogoEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("myapp", "activity onCreateOptionsMenu");

//        restoreActionBar();

        return super.onCreateOptionsMenu(menu);
    }
    /**
     *
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d(TAG, "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题

        Vitamio.isInitialized(this.getApplicationContext());
        setContentView(R.layout.videoview);
        mPreview = (SurfaceView)findViewById(R.id.surface);
        mPreview.setClickable(false);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);

        mProgressBar = (ImageView)findViewById(R.id.loading);
        mPlayImageView = (ImageView)findViewById(R.id.play);
        mPlayImageView.setOnClickListener(this);


        mImageCapture= (Button)findViewById(R.id.capture);
        mImageCapture.setOnClickListener(this);
        mImageCapture.setEnabled(false);


        mImageRecord= (Button)findViewById(R.id.record);
        mImageRecord.setOnClickListener(this);

        extras = getIntent().getExtras();

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        sp = getSharedPreferences(SettingFragment.KEY_SETTING, PreferenceActivity.MODE_PRIVATE);
        hardEnabled = sp.getBoolean(SettingFragment.KEY_HARD_DECODER, false);

//        byte[] bt= new byte[]{};
//        load();
//        initH264(bt);

    }


    private void playVideo(Integer Media) {
        doCleanUp();

        String url = extras.get(VideoPlayerSettingFragment.KEY_URL).toString();
        String username = extras.get(VideoPlayerSettingFragment.KEY_URERNAME).toString();
        String password = extras.get(VideoPlayerSettingFragment.KEY_PASSWORD).toString();

        Log.d(TAG, "url=" + url);
        Log.d(TAG, "username=" + username);
        Log.d(TAG, "pd=" + password);

        path = "rtsp://"+username+":"+password+"@"+url;


        try {
            if(url.compareTo("")==0)
            path = "/sdcard/download/hisi.asf";
//            path = "/storage/sdcard/hisi.asf";

            HashMap<String, String> options = new HashMap<String, String>();
            options.put("rtsp_transport", "tcp"); // udp
            //	options.put("user-agent", "userAgent");
            //	options.put("cookies", "cookies");
//            options.put("analyzeduration", "1000000");

            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer(this,hardEnabled);
            mMediaPlayer.setDataSource(this,Uri.parse(path),options);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setBufferSize(1920 * 1080 * 5);

            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.startAnimation(operatingAnim);


        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }



    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.capture){
            Bitmap bitmap =  mMediaPlayer.getCurrentFrame();
            File dir = Environment.getExternalStorageDirectory();
            Log.d("myapp","dir="+dir);

            File appDir = new File(Environment.getExternalStorageDirectory(), "Player");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);

            try {
                file.createNewFile();

                FileOutputStream fos = new FileOutputStream(file);
                BitmapFactory.Options options = new BitmapFactory.Options();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
                bitmap.recycle();
            } catch (Exception e){
                e.printStackTrace();
            }


                // 其次把文件插入到系统图库
            try {
                android.provider.MediaStore.Images.Media.insertImage(
                        getApplicationContext().getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

//            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        }else if(view.getId()==R.id.record) {
            if(mIsRecoding){

            }else{
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bitmap bitmap = mMediaPlayer.getCurrentFrame();
//                        if(bitmap==null){
//                            Log.d("myapp","bitmap null");
//                        }
//                        byte[] yuv = getNV21(1920,1080,bitmap);
//                        Log.d("myapp","yuv.length="+yuv.length);
//
//                        startH264(yuv);
//                        bitmap.recycle();
//                    }
//                }).start();

            }
            mIsRecoding = !mIsRecoding;
        }else {
            if(mIsPlaying) {
                mPlayImageView.setImageResource(R.drawable.selector_play);
                stopVideoPlayback();
            }else{
                mPlayImageView.setImageResource(R.drawable.selector_pause);
                startVideoPlayback();
            }
        }

    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
//         Log.d(TAG, "onBufferingUpdate percent:" + percent);

    }

    public void onCompletion(MediaPlayer arg0) {

        Log.d(TAG, "onCompletion called");
        if(!mIsVideoSizeKnown || !mIsVideoReadyToBePlayed){
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.clearAnimation();
            String log ="播放错误";
            if(mPlayerError1 == 1) {
                mPlayerError1 = 0;
                if(mPlayerError2==-13){
                    log= "找不到文件";
                }else if(mPlayerError2==-110){
                    log= "网络不可达";
                }else if(mPlayerError2==-5){
                log= "IO错误";
            }

            }
            mPlayerError1 = 0;
            mPlayerError1 = 0;
            Toast.makeText(this.getApplicationContext(), log, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("myapp","Error wht="+what);
        Log.d("myapp","Error extra="+extra);
        mPlayerError1 = what;
        mPlayerError2 = extra;
        return false;
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.d(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called="+mIsVideoSizeKnown);
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.clearAnimation();
            if(hardEnabled ==false)
                mImageCapture.setEnabled(true);
            startVideoPlayback();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged called");

    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed called");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");
         playVideo(null);
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        boolean enabled = sp.getBoolean(SettingFragment.KEY_WAKEUP_ENABLE,false);

        if(enabled) {
            powerManager = ((PowerManager) getSystemService(POWER_SERVICE));
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, TAG);
            wakeLock.acquire();
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
        SharedPreferences sp = this.getSharedPreferences(SettingFragment.KEY_SETTING, Context.MODE_PRIVATE);
        boolean enabled = sp.getBoolean(SettingFragment.KEY_WAKEUP_ENABLE, false);
        if(enabled) {
            if (wakeLock != null) {
                wakeLock.release();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        if(!mIsPlaying) {
            Log.d(TAG, "startVideoPlayback mVideoWidth="+mVideoWidth);
            Log.d(TAG, "startVideoPlayback mVideoHeight="+mVideoHeight);

//            holder.setFixedSize(mVideoWidth, mVideoHeight);
            mIsPlaying = true;
            mMediaPlayer.start();
        }
    }

    private void stopVideoPlayback() {
        Log.d(TAG, "stopVideoPlayback");
        if(mIsPlaying) {
            mIsPlaying = false;
            mMediaPlayer.stop();
        }
    }

    private void load(){
        String LIB_ROOT = Vitamio.getLibraryPath();

        System.load(LIB_ROOT+"libcom_example_gangzhang_myapplication_VideoPlayerActivity.so");

    }


    public static String getDataDir(Context ctx) {
        ApplicationInfo ai = ctx.getApplicationInfo();

        if (ai.dataDir != null)
            return fixLastSlash(ai.dataDir);
        else
            return "/data/data/" + ai.packageName + "/";
    }

    public static String fixLastSlash(String str) {
        String res = str == null ? "/" : str.trim() + "/";
        if (res.length() > 2 && res.charAt(res.length() - 2) == '/')
            res = res.substring(0, res.length() - 1);
        return res;
    }


    // untested function
    byte [] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {

        int [] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte [] yuv = new byte[inputWidth*inputHeight*3/2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        scaled.recycle();

        return yuv;
    }

    void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                }

                index ++;
            }
        }
    }
    private native int startH264(byte[] bytes);
    private native int initH264(byte[] name);

}
