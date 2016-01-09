package com.example.gangzhang.myapplication;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class VideoPlayerSettingFragment extends Fragment{

    private Button mButton;
    private EditText mUsername;
    private EditText mPasswd;
    private EditText mUrl;
    private Activity mActivity;
    public final static String KEY_URL="url";
    public final static String KEY_URERNAME="username";
    public final static String KEY_PASSWORD="password";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_main_setting,container,false);

        mUsername = (EditText)view.findViewById(R.id.user_id);
        mPasswd = (EditText)view.findViewById(R.id.user_passwd);
        mUrl = (EditText)view.findViewById(R.id.url_text);
        mButton = (Button)view.findViewById(R.id.button_setting);


        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("my_videoplayer_data", Context.MODE_PRIVATE); //私有数据
        String url = sharedPreferences.getString(KEY_URL, "");
        String username =sharedPreferences.getString(KEY_URERNAME, "");
        String password =sharedPreferences.getString(KEY_PASSWORD,"");
        Log.d("myapp","url="+url);
        Log.d("myapp","username="+username);
        Log.d("myapp","password="+password);


        if(url!=""){
            mUrl.setText(url);
        }
        if(username!=""){
            mUsername.setText(username);
        }
        if(password!=""){
            mPasswd.setText(password);
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String passwd = mPasswd.getText().toString();
                String url = mUrl.getText().toString();

                Log.d("myapp", "url=" + url);
                Log.d("myapp", "passwd=" + passwd);
                Log.d("myapp", "username=" + username);

                SharedPreferences sharedPreferences = mActivity.getSharedPreferences("my_videoplayer_data", Context.MODE_PRIVATE); //私有数据
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putString(KEY_URL, url);
                editor.putString(KEY_URERNAME, username);
                editor.putString(KEY_PASSWORD, passwd);

                editor.commit();//提交修改
                Intent it = new Intent(mActivity, VideoPlayerActivity.class);
                it.putExtra(KEY_URL,url);
                it.putExtra(KEY_URERNAME,username);
                it.putExtra(KEY_PASSWORD,passwd);
                startActivity(it);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("myapp","onAttach");
        mActivity = activity;
        ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}