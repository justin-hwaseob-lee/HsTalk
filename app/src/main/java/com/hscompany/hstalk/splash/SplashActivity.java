package com.hscompany.hstalk.splash;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hscompany.hstalk.MainActivity;
import com.hscompany.hstalk.R;
import com.hscompany.hstalk.login.LoginActivity;
import com.hscompany.hstalk.login.UserLocalStore;
import com.hscompany.hstalk.working;

public class SplashActivity extends AppCompatActivity
{
    // 로그인 정보
    private UserLocalStore userLocalStore = null;
    private boolean loggedIn = false;
    Intent myIntent;
    Context mycontext;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
           mycontext=getApplicationContext();
           working.bAppRunned=true;

           //인터넷 연결정보 확인
           if(!isOnline())
           {
               Toast.makeText(getLayoutInflater().getContext(), "인터넷 연결을 확인하세요.", Toast.LENGTH_SHORT).show();

               Handler hd = new Handler();
               hd.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       finish();       // 3 ���� �̹����� �ݾƹ���
                   }
               }, 1000);
               return;
           }
           // 로그인 정보
           userLocalStore = new UserLocalStore(this);
           loggedIn = userLocalStore.getUserLoggedIn();

           if (loggedIn)//login되어 있으면 main페이지로
           {
               myIntent = new Intent(SplashActivity.this, MainActivity.class);
           } else  //login되어 있지않으면 login페이지로
           {
               myIntent = new Intent(SplashActivity.this, LoginActivity.class);
           }

            Handler hd = new Handler();
            hd.postDelayed(new Runnable() {
                 @Override
                public void run() {

                    startActivity(myIntent);
                    finish();       // 3 ���� �̹����� �ݾƹ���
                }
            }, 1000);
       }

    private boolean isOnline() { // network 연결 상태 확인
        try {
            ConnectivityManager conMan = (ConnectivityManager) mycontext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState(); // mobile ConnectivityManager.TYPE_MOBILE
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                return true;
            }

        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }

}
