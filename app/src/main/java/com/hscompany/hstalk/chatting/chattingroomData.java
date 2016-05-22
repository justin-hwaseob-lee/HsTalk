package com.hscompany.hstalk.chatting;

/**
 * Created by hs695 on 2016-02-09.
 */
public class chattingroomData
{
    private int chattingroomlistid;
    private int user1id;
    private int user2id;
    private int newchat;
    private String lastcontent;
    private int lastwriter;
    private String lastdate;
    private String user1name;
    private String user2name;
    private boolean user1sex;
    private boolean user2sex;
    private boolean user1haspicture;
    private boolean user2haspicture;
    private int user1picchange;
    private int user2picchange;

    public chattingroomData(int chattingroomlistid, int user1id, int user2id, int newchat, String lastcontent, int lastwriter, String lastdate, String user1name, String user2name, boolean user1sex, boolean user2sex, boolean user1haspicture, boolean user2haspicture, int user1picchange, int user2picchange) {
        this.chattingroomlistid = chattingroomlistid;
        this.user1id = user1id;
        this.user2id = user2id;
        this.newchat = newchat;
        this.lastcontent = lastcontent;
        this.lastwriter = lastwriter;
        this.lastdate=lastdate;
        this.user1name=user1name;
        this.user2name=user2name;
        this.user1sex=user1sex;
        this.user2sex=user2sex;
        this.user1haspicture=user1haspicture;
        this.user2haspicture=user2haspicture;
        this.user1picchange=user1picchange;
        this.user2picchange=user2picchange;
    }

    public int getUser1picchange() {
        return user1picchange;
    }

    public void setUser1picchange(int user1picchange) {
        this.user1picchange = user1picchange;
    }

    public int getUser2picchange() {
        return user2picchange;
    }

    public void setUser2picchange(int user2picchange) {
        this.user2picchange = user2picchange;
    }

    public String getSimpleTime()
    {
        String ampm;
        String Year = this.lastdate.substring(0,4);
        String Month = this.lastdate.substring(5, 7);
        String Day = this.lastdate.substring(8, 10);
        int Hour = Integer.parseInt(this.lastdate.substring(11, 13));
        String Min = this.lastdate.substring(14,16);

        if(Hour > 12)
        {
            Hour -= 12;
            ampm = "pm";
        }
        else
            ampm = "am";

        return Year+"/"+Month + "/" + Day + " " + Hour + ":" + Min + ampm;
    }

    public boolean isUser1sex() {
        return user1sex;
    }

    public void setUser1sex(boolean user1sex) {
        this.user1sex = user1sex;
    }

    public boolean isUser2sex() {
        return user2sex;
    }

    public void setUser2sex(boolean user2sex) {
        this.user2sex = user2sex;
    }

    public boolean isUser1haspicture() {
        return user1haspicture;
    }

    public void setUser1haspicture(boolean user1haspicture) {
        this.user1haspicture = user1haspicture;
    }

    public boolean isUser2haspicture() {
        return user2haspicture;
    }

    public void setUser2haspicture(boolean user2haspicture) {
        this.user2haspicture = user2haspicture;
    }

    public String getUser1name() {
        return user1name;
    }

    public void setUser1name(String user1name) {
        this.user1name = user1name;
    }

    public String getUser2name() {
        return user2name;
    }

    public void setUser2name(String user2name) {
        this.user2name = user2name;
    }

    public String getLastdate() {
        return lastdate;
    }

    public void setLastdate(String lastdate) {
        this.lastdate = lastdate;
    }

    public int getChattingroomlistid() {
        return chattingroomlistid;
    }

    public void setChattingroomlistid(int chattingroomlistid) {
        this.chattingroomlistid = chattingroomlistid;
    }

    public int getUser1id() {
        return user1id;
    }

    public void setUser1id(int user1id) {
        this.user1id = user1id;
    }

    public int getUser2id() {
        return user2id;
    }

    public void setUser2id(int user2id) {
        this.user2id = user2id;
    }

    public int getNewchat() {
        return newchat;
    }

    public void setNewchat(int newchat) {
        this.newchat = newchat;
    }

    public String getLastcontent() {
        return lastcontent;
    }

    public void setLastcontent(String lastcontent) {
        this.lastcontent = lastcontent;
    }

    public int getLastwriter() {
        return lastwriter;
    }

    public void setLastwriter(int lastwriter) {
        this.lastwriter = lastwriter;
    }
}
