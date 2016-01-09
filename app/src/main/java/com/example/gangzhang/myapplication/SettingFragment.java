package com.example.gangzhang.myapplication;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

public class SettingFragment extends Fragment implements Button.OnClickListener{

    private Button mButton;
    private EditText mUsername;
    private EditText mPasswd;
    private EditText mUrl;
    private RadioGroup mRadioGroup;
    private Activity mActivity;
    private ImageButton mSleepButton;
    private boolean mWakeUpEnabled  =false;
    private boolean mHardDecoderEnable = false;
    public final static String KEY_SETTING="my_videoplayer_data";
    public final static String KEY_WAKEUP_ENABLE="wake_up";
    public final static String KEY_HARD_DECODER="hard_decoder";

    private SharedPreferences sp;
    private  SharedPreferences.Editor editor;

    private ImageButton mHardDecoderButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);
        mSleepButton = (ImageButton)view.findViewById(R.id.toggle);
        mHardDecoderButton = (ImageButton)view.findViewById(R.id.hard_decode);
        mSleepButton.setOnClickListener(this);
        mHardDecoderButton.setOnClickListener(this);


        sp = mActivity.getSharedPreferences("my_videoplayer_data", Context.MODE_PRIVATE); //私有数据
        editor = sp.edit();

        mWakeUpEnabled= sp.getBoolean(KEY_WAKEUP_ENABLE, true);
        mHardDecoderEnable = sp.getBoolean(KEY_HARD_DECODER,false);

        if(mWakeUpEnabled){
            mSleepButton.setBackgroundResource(R.drawable.ate);
        }else{
            mSleepButton.setBackgroundResource(R.drawable.atd);
        }

        if(mHardDecoderEnable){
            mHardDecoderButton.setBackgroundResource(R.drawable.ate);
        }else{
            mHardDecoderButton.setBackgroundResource(R.drawable.atd);
        }
        return view;
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.hard_decode:

                if(mHardDecoderEnable){
                    mHardDecoderButton.setBackgroundResource(R.drawable.atd);
                }else{
                    mHardDecoderButton.setBackgroundResource(R.drawable.ate);
                }
                mHardDecoderEnable =!mHardDecoderEnable;

                try {
                    editor.putBoolean(KEY_HARD_DECODER, mHardDecoderEnable);
                    editor.commit();
                    editor.apply();
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case R.id.toggle:
                if(mWakeUpEnabled){
                    mSleepButton.setBackgroundResource(R.drawable.atd);
                }else{
                    mSleepButton.setBackgroundResource(R.drawable.ate);
                }
                mWakeUpEnabled =!mWakeUpEnabled;

                try {
                    editor.putBoolean(KEY_WAKEUP_ENABLE, mWakeUpEnabled);
                    editor.commit();
                    editor.apply();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("myapp","onAttach");
        mActivity = activity;
        ((MainActivity) activity).onSectionAttached(3);
    }
}