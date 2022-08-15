package com.tech.jarvis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    }

    public void user(View view) {
        startActivity(new Intent(getApplicationContext(), InformationActivity.class));
    }

    public void light(View view) {
        startActivity(new Intent(getApplicationContext(), LightActivity.class));
    }

    public  void about(View view) {
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
    }

    public  void help(View view) {
        startActivity(new Intent(getApplicationContext(), HelpActivity.class));
    }

}
