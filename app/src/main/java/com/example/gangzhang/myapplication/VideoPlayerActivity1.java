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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayerActivity1 extends Activity implements View.OnClickListener
{
    private static final String TAG = "videoapp";
    private int mVideoWidth;
    private int mVideoHeight;
    private String path;
    private Bundle extras;

    private Animation operatingAnim;
    private ImageView mProgressBar;
    private ImageView mPlayImageView;
    private boolean mIsPlaying = false;
    private Button mImageCapture;
    private Button mImageRecord;
    private boolean mIsRecoding = false;
    private int mPlayerError1 = 0;
    private int mPlayerError2 = 0;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private SharedPreferences sp;
    private Boolean hardEnabled;
    private VideoView mVideoView;
    private MediaPlayer mMediaPlayer;
    private boolean mIsPrepared = false;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d(TAG, "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题

        Vitamio.isInitialized(this.getApplicationContext());
        setContentView(R.layout.videoview1);

        mProgressBar = (ImageView) findViewById(R.id.loading);
//        mPlayImageView = (ImageView) findViewById(R.id.play);
//        mPlayImageView.setOnClickListener(this);

        mImageCapture = (Button) findViewById(R.id.capture);
        mImageCapture.setEnabled(false);
        mImageCapture.setOnClickListener(this);


        mImageRecord = (Button) findViewById(R.id.record);

        extras = getIntent().getExtras();

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        sp = getSharedPreferences(SettingFragment.KEY_SETTING, PreferenceActivity.MODE_PRIVATE);
        hardEnabled = sp.getBoolean(SettingFragment.KEY_HARD_DECODER, false);

        mVideoView = (VideoView) findViewById(R.id.surface_view);
        SharedPreferences sp = this.getSharedPreferences(SettingFragment.KEY_SETTING, Context.MODE_PRIVATE);
        boolean enabled = sp.getBoolean(SettingFragment.KEY_WAKEUP_ENABLE, false);


    }

    private void doClean(){
        mIsPrepared = false;
        mMediaPlayer = null;
    }

    private void playVideo(final Integer Media) {

        String url = extras.get(VideoPlayerSettingFragment.KEY_URL).toString();
        String username = extras.get(VideoPlayerSettingFragment.KEY_URERNAME).toString();
        String password = extras.get(VideoPlayerSettingFragment.KEY_PASSWORD).toString();

        Log.d(TAG, "url=" + url);
        Log.d(TAG, "username=" + username);
        Log.d(TAG, "pd=" + password);

        path = "rtsp://" + username + ":" + password + "@" + url;


        try {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.startAnimation(operatingAnim);

            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(null);
            mVideoView.requestFocus();


            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    Log.d(TAG, "onPrepared called=" + hardEnabled);
                    mMediaPlayer = mediaPlayer;
                }
            });

            mVideoView.setHardwareDecoder(hardEnabled);
            mVideoView.setBufferSize(1920 * 1080 *3);
            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mPlayerError1 = what;
                    mPlayerError2 = extra;
                    return false;
                }
            });
            mVideoView.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "onCompletion called");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mProgressBar.clearAnimation();
                    String log = "播放错误";
                    if (mPlayerError1 == 1) {
                        mPlayerError1 = 0;
                        if (mPlayerError2 == -13) {
                            log = "找不到文件";
                        } else if (mPlayerError2 == -110) {
                            log = "网络不可达";
                        } else if (mPlayerError2 == -5) {
                            log = "IO错误";
                        }

                        mPlayerError1 = 0;
                        mPlayerError1 = 0;
                        Toast.makeText(getApplicationContext(), log, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mProgressBar.clearAnimation();
                        if (hardEnabled == false)
                            mImageCapture.setEnabled(true);
                        mVideoView.start();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }



    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        boolean enabled = sp.getBoolean(SettingFragment.KEY_WAKEUP_ENABLE, false);

        if (enabled) {
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
        playVideo(null);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences sp = this.getSharedPreferences(SettingFragment.KEY_SETTING, Context.MODE_PRIVATE);
        boolean enabled = sp.getBoolean(SettingFragment.KEY_WAKEUP_ENABLE, false);
        if (enabled) {
            if (wakeLock != null) {
                wakeLock.release();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doClean();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.capture:

                Bitmap bitmap = mMediaPlayer.getCurrentFrame();
                File dir = Environment.getExternalStorageDirectory();
                Log.d("myapp", "dir=" + dir);

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

                    android.provider.MediaStore.Images.Media.insertImage(
                            getApplicationContext().getContentResolver(),
                            file.getAbsolutePath(), fileName, null);

                    // 最后通知图库更新
                    getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));

                } catch (Exception e) {
                    Log.d("myapp", "createNewFile");
                    e.printStackTrace();
                }
                break;
            case R.id.play:
                if (mIsPlaying == true) {
                    mVideoView.pause();
                } else {
                    mVideoView.start();
                }
                mIsPlaying = !mIsPlaying;
                break;
        }
    }
}