package com.hscompany.hstalk.login;

import android.graphics.Bitmap;

/**
 * Created by hs695 on 2016-02-05.
 */
public class UserData
{
    final String ServerUrl = "http://54.238.209.107";

    private int usernumber;
    private String nickname;
    private String password;
    private boolean sex;
    private boolean haspicture = false;
    private String userjoindate;
    private int picchange;
    private Bitmap userPic;

    public UserData(String nickname, String password) {
        this(-1, nickname, password, false, false, "", -1);
    }

    public UserData(String nickname, String password, boolean sex) {
        this(-1, nickname, password, sex, false, "", -1);
    }

    public UserData(int usernumber, String nickname, String password, boolean sex, boolean haspicture, String userjoindate, int picchange)
    {
        this.usernumber = usernumber;
        this.nickname = nickname;
        this.password = password;
        this.sex = sex;
        this.haspicture = haspicture;
        this.userjoindate = userjoindate;
        this.picchange = picchange;

        /*
        if(haspicture==true)
        {
            try
            {
                URL url = new URL("http://54.238.209.107/picture/user/" + usernumber + ".jpg");
                this.userPic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                this.userPic=getCircleBitmap(this.userPic);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                URL url = new URL("http://54.238.209.107/picture/user/default.jpg");
                this.userPic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        */
    }

    public void modifyUser(String nickname, String password, boolean sex) {
        this.nickname = nickname;
        this.password = password;
        this.sex = sex;
    }


    //getter setter 함수들

    public int getPicchange(){return picchange;}

    public void setPicchange(int pic){picchange=pic;}


    public int getUsernumber() {
        return usernumber;
    }

    public void setUsernumber(int usernumber) {
        this.usernumber = usernumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public boolean isHaspicture() {
        return haspicture;
    }

    public void setHaspicture(boolean haspicture) {
        this.haspicture = haspicture;
    }

    public String getUserjoindate() {
        return userjoindate;
    }

    public void setUserjoindate(String userjoindate) {
        this.userjoindate = userjoindate;
    }

    public void setUserPic(Bitmap userPic) {
        this.userPic = userPic;
    }

    public Bitmap getUserPic()
    {
        return this.userPic;
    }
}
