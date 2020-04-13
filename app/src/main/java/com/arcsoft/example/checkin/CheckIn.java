package com.arcsoft.example.checkin;

import android.os.Bundle;
/*import android.support.v7.app.AppCompatActivity;*/

/*import androidx.appcompat.app.AppCompatActivity;*/

import androidx.appcompat.app.AppCompatActivity;

import com.arcsoft.arcfacedemo.R;

@SuppressWarnings({"ALL", "MagicConstant"})
/*打卡页面*/
public class CheckIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
    }
}
