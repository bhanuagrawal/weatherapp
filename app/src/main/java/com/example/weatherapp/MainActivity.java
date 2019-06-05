package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.weatherapp.ui.MainFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TG_MAIN_FRAGMENT = "dffasjdfdsf";
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment = (MainFragment)getSupportFragmentManager().findFragmentByTag(TG_MAIN_FRAGMENT);
        if(mainFragment == null){
            mainFragment = MainFragment.newInstance();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, mainFragment, TG_MAIN_FRAGMENT)
                .commit();
    }
}
