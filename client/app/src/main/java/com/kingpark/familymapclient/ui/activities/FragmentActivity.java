package com.kingpark.familymapclient.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.kingpark.familymapclient.R;

public abstract class FragmentActivity extends AppCompatActivity {
    protected static String TAG = "FragmentActivity";
    
    protected abstract Fragment createFragment(Bundle args);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_fragment);
        
        FragmentManager manager =  this.getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment(savedInstanceState);
            
            manager.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }
    }
}
