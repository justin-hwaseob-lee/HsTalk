package com.hscompany.hstalk.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hscompany.hstalk.Network;
import com.hscompany.hstalk.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String ServerUrl = "http://54.238.209.107";


    //gcm관련 시작
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // please enter your sender id
    String SENDER_ID = "57189245716";
    GoogleCloudMessaging gcm;
    Context context;
    String regid;
    //gcm 관련 끝

    Button bRegister;
    EditText etNickName, etPassword;
    RadioGroup etSex;
    RadioButton etMan, etWoman;
    UserLocalStore userLocalStore;

    int tmpsex=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //gcm 추가
        context = getApplicationContext();
        if(checkPlayServices()){
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            if(regid.isEmpty()){
                new RegisterBackground().execute();
            }

        }
        //gcm 추가 끝

        userLocalStore=new UserLocalStore(this);
        etNickName = (EditText) findViewById(R.id.etNickName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);
        etSex = (RadioGroup) findViewById(R.id.etSex);
        etMan = (RadioButton) findViewById(R.id.etMan);
        etWoman = (RadioButton) findViewById(R.id.etWoman);


        bRegister.setOnClickListener(this);
        etSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (etSex.getCheckedRadioButtonId() == R.id.etMan) {
                    tmpsex = 1;
                } else {
                    tmpsex = 0;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.bRegister:
                String nickName=etNickName.getText().toString();
                String password=etPassword.getText().toString();



                UserData userData = new UserData(nickName, password, false);

                registerUser(userData);
                break;
        }
    }

    private void registerUser(UserData userData)
    {

        String[][] postData=new String[][]{{"nickname", userData.getNickname()}, {"password", userData.getPassword()}, {"sex", ""+tmpsex}, {"regid", regid}};
        String result = new Network().sendDatePost(ServerUrl+"/Register.php", postData);

        if(result.equals("Fail"))
        {
            showErrorMessage();
        }
        else
        {
            String[][] postDataa=new String[][]{{"nickname", userData.getNickname()}, {"password", userData.getPassword()}};
            String myresult = new Network().sendDatePost(ServerUrl+"/FetchUserData.php", postDataa);
            try
            {

                JSONObject jsonObj = new JSONObject(myresult);
                JSONArray jsonArr = jsonObj.getJSONArray("user"); //php파일에서 user로 저장한 로그인정보를 가져옴
                jsonObj = jsonArr.getJSONObject(0);

                int userNum = jsonObj.getInt("usernumber");
                String nickName = jsonObj.getString("nickname");
                String password = jsonObj.getString("password");
                boolean sex=false;
                int tmpSex = jsonObj.getInt("sex");
                boolean hasPicture=false;
                int tmpHasPicture = jsonObj.getInt("haspicture");
                String userJoinDate = jsonObj.getString("joindate");
                int picChange= jsonObj.getInt("picchange");

                if(tmpSex==1)  sex = true;
                if(tmpHasPicture==1) hasPicture = true;

                Log.d("fuckyoman", nickName + " / " + password + " / " + sex + " / " + hasPicture + " / " + userJoinDate);
                UserData returnedUser = new UserData(userNum, nickName, password, sex, hasPicture, userJoinDate, picChange);

                logUserIn(returnedUser);

            }
            catch(Exception e) //로그인정보가 맞지 않는경우
            {
                showErrorMessage();
            }

            //Toast.makeText(getLayoutInflater().getContext(), "회원가입 성공!\n로그인 하세요.", Toast.LENGTH_SHORT).show();
            Toast.makeText(getLayoutInflater().getContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void logUserIn(UserData returnedUser)
    {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
    }

    private void showErrorMessage()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
        dialogBuilder.setMessage("회원가입 실패하였습니다.\n 다른 아이디로 다시 시도해주세요.");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.d("what", "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d("what", "App version changed.");
            return "";
        }
        return registrationId;
    }
    private SharedPreferences getGCMPreferences(Context context) {

        return getSharedPreferences(RegisterActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    class RegisterBackground extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Dvice registered, registration ID=" + regid;

                Log.d("mylog:", msg);
                //sendRegistrationIdToBackend();

                // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {

            //여기서 regid를 서버에 등록!!!

            Log.d("Mylogerㄱㄱㄱ", msg);

        }
        private void sendRegistrationIdToBackend() {
            // Your implementation here.
            Log.d("Myloger", "드가기전1");
            String[][] sendData=new String[][]{{"reg_id", regid}};

            String str = null;
            String strurl = ServerUrl+"/gcmRegister.php";
            URL url=null;
            try
            {
                url = new URL(strurl);
                Log.d("Mylog", url.toString());
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            String data = new String();
            try
            {
                for (int i = 0; i < sendData.length; i++)
                {
                    if (i != 0)
                    {
                        data += "&";
                    }
                    data += URLEncoder.encode(sendData[i][0], "UTF-8") + "=" + URLEncoder.encode(sendData[i][1], "UTF-8");
                }
                Log.d("Mylog", data);
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            final String send = data;
            BufferedReader br = null;
            String result="";
            HttpURLConnection conn = null;
            try
            {
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                try
                {
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(send);
                    wr.flush();
                    wr.close();

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        result = new String();

                        for (; ; )
                        {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            result += line;
                        }

                        br.close();
                        conn.disconnect();
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            Log.d("Myloger", "드가기전2");
            if(result.equals("Fail"))
            {
                //실패
                Log.d("Myloger", "실패");
            }
            else
            {
                Log.d("Myloger", "성공");
                //  Toast.makeText(getLayoutInflater().getContext(), "성공", Toast.LENGTH_SHORT).show();
            }

        }

        private void storeRegistrationId(Context context, String regId) {
            final SharedPreferences prefs = getGCMPreferences(context);
            int appVersion = getAppVersion(context);
            Log.d("what", "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, regId);
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.commit();
        }
    }
}