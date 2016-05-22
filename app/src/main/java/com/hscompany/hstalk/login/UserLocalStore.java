package com.hscompany.hstalk.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by hs695 on 2016-02-05.
 */
public class UserLocalStore
{

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase; //여기서 sharedPreferences는 지금 우리 앱(곧감) 프로세스 내에 FIle형태로 Data를 저장할 수있음. 따라서 로그인했을시 여기에다 저장해놓고 로그아웃시 삭제하고 하는 형태로~

    public UserLocalStore(Context context)
    {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }


    public void storeUserData(UserData userData) //넘겨받은 유저의 정보를 로컬 database에 저장
    {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putInt("userNum", userData.getUsernumber());
        spEditor.putString("nickName", userData.getNickname());
        spEditor.putString("password", userData.getPassword());
        spEditor.putBoolean("sex", userData.isSex());
        spEditor.putBoolean("hasPicture", userData.isHaspicture());
        spEditor.putString("userJoinDate", userData.getUserjoindate());
        spEditor.putInt("picChange", userData.getPicchange());
        spEditor.commit();
    }

    public UserData getLoggedInUser() //로그인된 유저를 리턴하는 함수
    {
        int userNum=userLocalDatabase.getInt("userNum", -1);
        String nickName=userLocalDatabase.getString("nickName", "");
        String password=userLocalDatabase.getString("password", "");
        boolean sex=userLocalDatabase.getBoolean("sex", false);
        boolean hasPicture=userLocalDatabase.getBoolean("hasPicture", false);
        String userJoinDate=userLocalDatabase.getString("userJoinDate","");
        int picChange=userLocalDatabase.getInt("picChange", -1);
Log.d("fuck hasPicture??", "" + hasPicture);
        UserData storedUser = new UserData(userNum, nickName, password, sex, hasPicture, userJoinDate, picChange);
        return storedUser;
    }

    public void setUserLoggedIn(boolean loggedIn)
    {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean getUserLoggedIn()
    {
        if(userLocalDatabase.getBoolean("loggedIn", false)==true)
        {
            return true;
        }
        else
            return false;
    }

    public void clearUserData() //로그아웃시 디바이스 디비에 저장된 회원정보를 삭제해준다
    {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
