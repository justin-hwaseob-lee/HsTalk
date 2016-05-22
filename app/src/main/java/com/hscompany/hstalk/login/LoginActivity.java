package com.hscompany.hstalk.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hscompany.hstalk.MainActivity;
import com.hscompany.hstalk.Network;
import com.hscompany.hstalk.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends Activity implements View.OnClickListener
{

    public static final String ServerUrl = "http://54.238.209.107";
    Button bLogin, tvRegisterLink;
    EditText etNickName, etPassword;
    UserLocalStore userLocalStore;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("sleep", "splash와서 onCreat왔나?2 start요청");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d("sleep", "splash와서 onCreat왔나?2 start요청");
        etNickName = (EditText) findViewById(R.id.etNickName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin); //로그인버튼
        tvRegisterLink = (Button) findViewById(R.id.tvRegisterLink); //회원가입페이지 이동버튼



        bLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);

        userLocalStore=new UserLocalStore(this);
        Log.d("sleep", "splash와서 onCreat왔나?3 start요청");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(userLocalStore.getUserLoggedIn())
        {
            Toast.makeText(getLayoutInflater().getContext(), "" + userLocalStore.getLoggedInUser().getNickname() + "님 환영합니다!", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.bLogin:

                String nickname=etNickName.getText().toString();
                String password=etPassword.getText().toString();
                UserData userData=new UserData(nickname, password); //로그인 정보(이메일과 패스워드를 입력한다)

                authenticate(userData);

                break;

            case R.id.tvRegisterLink:
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(myIntent);
                break;
        }
    }

    private void authenticate(UserData userData) //로그인정보 확인하는 메소드
    {

        String[][] postData=new String[][]{{"nickname", userData.getNickname()}, {"password", userData.getPassword()}};

        String result = new Network().sendDatePost(ServerUrl+"/FetchUserData.php", postData);

        try
        {

            JSONObject jsonObj = new JSONObject(result);
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

            Log.d("fuck", nickName+" / "+password +" / "+sex+" / "+hasPicture+" / "+userJoinDate);
            UserData returnedUser = new UserData(userNum, nickName, password, sex, hasPicture, userJoinDate, picChange);

            logUserIn(returnedUser);

        }
        catch(Exception e) //로그인정보가 맞지 않는경우
        {
            showErrorMessage();
        }

    }

    private void showErrorMessage()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("로그인정보가 잘못되었습니다.");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }


    private void logUserIn(UserData returnedUser)
    {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);

        Toast.makeText(getLayoutInflater().getContext(), "" + returnedUser.getNickname() + "님 환영합니다!", Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}

