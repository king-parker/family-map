package com.kingpark.familymapclient.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kingpark.familymapclient.model.DataCache;
import com.kingpark.familymapclient.model.Person;
import com.kingpark.familymapclient.network.ServerProxy;
import com.kingpark.familymapclient.network.request.LoginRequest;
import com.kingpark.familymapclient.network.request.RegisterRequest;
import com.kingpark.familymapclient.R;
import com.kingpark.familymapclient.ui.tasks.GetDataTask;
import com.kingpark.familymapclient.ui.tasks.LoginTask;
import com.kingpark.familymapclient.ui.tasks.RegisterTask;

public class LoginFragment extends Fragment implements LoginTask.Listener, RegisterTask.Listener,
        GetDataTask.Listener {
    private final String TAG = "LoginFragment";
    
    private EditText mHostEditText;
    private EditText mPortEditText;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;
    private RadioGroup mGenderRadioGroup;
    
    private Button mLoginButton;
    private Button mRegisterButton;
    
    private TextView mLoadingIcon;
    
    private DataCache mData;
    
    public LoginFragment() {
        // Required empty public constructor
    }
    
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = DataCache.getInstance();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login,container,false);
        
        mHostEditText = v.findViewById(R.id.server_host_edit);
        mHostEditText.setText(mData.getHostName());
        mHostEditText.addTextChangedListener(new LoginTextWatcher());
        
        mPortEditText = v.findViewById(R.id.server_port_edit);
        mPortEditText.setText(Integer.toString(mData.getPortNumber()));
        mPortEditText.addTextChangedListener(new LoginTextWatcher());
        
        mUsernameEditText = v.findViewById(R.id.user_name_edit);
        mUsernameEditText.setText(mData.getUsername());
        mUsernameEditText.addTextChangedListener(new LoginTextWatcher());
        
        mPasswordEditText = v.findViewById(R.id.password_edit);
        mPasswordEditText.addTextChangedListener(new LoginTextWatcher());
        
        mFirstNameEditText = v.findViewById(R.id.first_name_edit);
        mFirstNameEditText.addTextChangedListener(new LoginTextWatcher());
        
        mLastNameEditText = v.findViewById(R.id.last_name_edit);
        mLastNameEditText.addTextChangedListener(new LoginTextWatcher());
        
        mEmailEditText = v.findViewById(R.id.email_edit);
        mEmailEditText.addTextChangedListener(new LoginTextWatcher());
        
        mGenderRadioGroup = v.findViewById(R.id.radio_gender);
        mGenderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup,int i) {
                checkButtons();
            }
        });
        
        mLoginButton = v.findViewById(R.id.button_sign_in);
        mLoginButton.setEnabled(false);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setServerInfo();
    
                showLoadIcon(true);
                
                LoginTask loginTask = new LoginTask(LoginFragment.this);
                loginTask.execute(createLoginRequest());
            }
        });
        
        mRegisterButton = v.findViewById(R.id.button_register);
        mRegisterButton.setEnabled(false);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setServerInfo();
    
                showLoadIcon(true);
    
                RegisterTask registerTask = new RegisterTask(LoginFragment.this);
                registerTask.execute(createRegisterRequest());
            }
        });
        
        mLoadingIcon = v.findViewById(R.id.loading_icon);
        
        return v;
    }
    
    @Override
    public void onLoginSuccess(String authToken,String username,String personID) {
        authenticationSuccess(authToken,username,personID);
    }
    
    @Override
    public void onLoginFail(String message) {
        showLoadIcon(false);
        
        requestFailed(message);
    }
    
    @Override
    public void onRegisterSuccess(String authToken,String username,String personID) {
        authenticationSuccess(authToken,username,personID);
    }
    
    @Override
    public void onRegisterFail(String message) {
        showLoadIcon(false);
    
        requestFailed(message);
    }
    
    @Override
    public void onGetDataSuccess() {
        dataRetrievalSuccess();
    }
    
    @Override
    public void onGetDataFail(String message) {
        showLoadIcon(false);
    
        requestFailed(message);
    }
    
    private void checkButtons() {
        if (serverFieldsReady() && loginFieldsReady()) {
            mLoginButton.setEnabled(true);
        } else {
            mLoginButton.setEnabled(false);
        }
        
        if (serverFieldsReady() && registerFieldsReady()) {
            mRegisterButton.setEnabled(true);
        } else {
            mRegisterButton.setEnabled(false);
        }
    }
    
    private boolean serverFieldsReady() {
        String hostName = mHostEditText.getText().toString();
        String portNumber = mPortEditText.getText().toString();
        return !hostName.isEmpty() && !portNumber.isEmpty();
    }
    
    private boolean loginFieldsReady() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        return !username.isEmpty() && !password.isEmpty();
    }
    
    private boolean registerFieldsReady() {
        if (!loginFieldsReady()) return false;
    
        int radioId = mGenderRadioGroup.getCheckedRadioButtonId();
        if (radioId != R.id.radio_female && radioId != R.id.radio_male) return false;
        
        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        return !firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty();
    }
    
    private void setServerInfo() {
        String hostName = mHostEditText.getText().toString();
        
        String portText = mPortEditText.getText().toString();
        for (char c : portText.toCharArray()) if (!Character.isDigit(c)) return;
        int portNum = Integer.parseInt(portText);
    
        ServerProxy.setProxyServer(hostName,portNum);
    }
    
    private void showLoadIcon(boolean isShown) {
        String spinnerIcon = "{fa-spinner spin}";
        if (isShown) mLoadingIcon.setText(spinnerIcon);
        else mLoadingIcon.setText("");
    }
    
    private LoginRequest createLoginRequest() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        return new LoginRequest(username,password);
    }
    
    private RegisterRequest createRegisterRequest() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
    
        char gender;
        if (mGenderRadioGroup.getCheckedRadioButtonId() == R.id.radio_male) gender = 'm';
        else gender = 'f';
        
        return new RegisterRequest(username,password,email,firstName,lastName,gender);
    }
    
    private void authenticationSuccess(String authToken,String username,String personID) {
        GetDataTask dataTask = new GetDataTask(LoginFragment.this);
        dataTask.execute(authToken,personID);
        
        String logMessage = "Success! Token: " + authToken + " Username: " + username +
                " ID: " + personID;
        Log.i(TAG,logMessage);
    }
    
    private void dataRetrievalSuccess() {
        Person userPerson = mData.getUserPerson();
        assert userPerson != null;
        String userPersonName = userPerson.getFirstName() + " " + userPerson.getLastName();
        Log.i(TAG,"Data retrieved into the data cache for " + userPersonName);
        
        toMapFragment();
    }
    
    private void requestFailed(String message) {
        Log.i(TAG,"Request Error: " + message);
        Toast.makeText(LoginFragment.this.getContext(),message,Toast.LENGTH_SHORT).show();
    }
    
    private void toMapFragment() {
        getActivity().invalidateOptionsMenu();
        
        Log.i(TAG,"Switching to map fragment");
        // TODO Switch to Map Fragment
        FragmentManager manager =  this.getFragmentManager();
        Fragment mapFragment = new MapFragment();
    
        assert manager != null;
        manager.beginTransaction().replace(R.id.fragment_container,mapFragment).commit();
    }
    
    private class LoginTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence,int i,int i1,int i2) {}
        
        @Override
        public void onTextChanged(CharSequence charSequence,int i,int i1,int i2) {
            checkButtons();
        }
        
        @Override
        public void afterTextChanged(Editable editable) {}
    }
}