package com.kingpark.familymapclient.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kingpark.familymapclient.R;
import com.kingpark.familymapclient.model.DataCache;
import com.kingpark.familymapclient.model.Settings;
import com.kingpark.familymapclient.ui.fragments.LoginFragment;
import com.kingpark.familymapclient.ui.fragments.MapFragment;

public class MainActivity extends FragmentActivity {
    static {
        TAG = "MainActivity";
    }
    
    public static final String ARG_IS_LOGGED_IN = "com.kingpark.familymapclient.is_logged_in";
    public static final String ARG_AUTH_TOKEN = "com.kingpark.familymapclient.main_auth_token";
    public static final String ARG_HOST_NAME = "com.kingpark.familymapclient.main_host_name";
    public static final String ARG_PORT_NUMBER = "com.kingpark.familymapclient.main_port_number";
    public static final String ARG_USERNAME = "com.kingpark.familymapclient.main_username";
    public static final String ARG_SETTINGS = "com.kingpark.familymapclient.main_settings";
    private static final int REQUEST_CODE_LOGOUT = 1;
    
    private DataCache mData;
    private boolean mIsLoggedIn;
    private String mAuthToken;
    
    @Override
    protected Fragment createFragment(Bundle args) {
        mData = DataCache.getInstance();
        
        // TODO Probably get rid of AuthToken arg
        if (args != null) {
            if (args.containsKey(ARG_IS_LOGGED_IN) && args.containsKey(ARG_AUTH_TOKEN)) {
                mData.setLoggedIn(args.getBoolean(ARG_IS_LOGGED_IN,false));
                mAuthToken = args.getString(ARG_AUTH_TOKEN);
            } else {
                mData.setLoggedIn(false);
                mAuthToken = null;
            }
    
            mData.setHostName(args.getString(ARG_HOST_NAME,"10.0.2.2"));
            mData.setPortNumber(args.getInt(ARG_PORT_NUMBER,8080));
            mData.setUsername(args.getString(ARG_USERNAME,""));
    
            if (args.containsKey(ARG_SETTINGS)) mData.setSettings(args.getBooleanArray(ARG_SETTINGS));
            else mData.setSettings(new Settings().toArray());
        }
        
        if (mIsLoggedIn) {
            // TODO add load screen if already logged in; add authToken, hostName, portNumber and username to instance
            return MapFragment.newInstance();
        } else {
            return LoginFragment.newInstance();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        new MenuInflater(this).inflate(R.menu.activity_main,menu);
        
        if (mData.isLoggedIn()) {
            showToolbar(menu,true);
        } else {
            showToolbar(menu,false);
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(SearchActivity.newIntent(this));
                return true;
            case R.id.action_settings:
                startActivityForResult(SettingsActivity.newIntent(this),REQUEST_CODE_LOGOUT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode != Activity.RESULT_OK) return;
        
        switch (requestCode) {
            case REQUEST_CODE_LOGOUT:
                if (data == null) return;
                logout(SettingsActivity.isLogout(data));
                break;
            default:
                return;
        }
    }
    
    private void logout(boolean isLogout) {
        if (isLogout) {
            FragmentManager manager = getSupportFragmentManager();
            Fragment loginFragment = LoginFragment.newInstance();
    
            assert manager != null;
            mData.logout();
            invalidateOptionsMenu();
            manager.beginTransaction().replace(R.id.fragment_container,loginFragment).commit();
        }
    }
    
    private void showToolbar(Menu menu,boolean isShown) {
        menu.findItem(R.id.action_search).setVisible(isShown);
        menu.findItem(R.id.action_settings).setVisible(isShown);
    }
}