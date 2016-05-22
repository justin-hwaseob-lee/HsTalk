package com.hscompany.hstalk.chatting;

/**
 * Created by hs695 on 2016-02-09.
 */
public class chattingData
{
    final int CHAT_TYPE_TALK_VISITOR = 0;
    final int CHAT_TYPE_TALK_NOT_VISITOR = 1;
    final int CHAT_TYPE_PICTURE_VISITOR = 2;
    final int CHAT_TYPE_PICTURE_NOT_VISITOR = 3;

    private int chattingid;
    private int chattingroomlistid;
    private int writerid;
    private String content;
    private String posttime;
    private boolean haspicture;
    private String writername;
    private int talkType;
    private int picchange;
    private boolean writerhaspicture;

    public chattingData(int chattingid, int chattingroomlistid, int writerid, String content, String posttime, boolean haspicture, String writername, int ownerid, int picchange, boolean writerhaspicture)
    {
        this.chattingid = chattingid;
        this.chattingroomlistid = chattingroomlistid;
        this.writerid = writerid;
        this.writername=writername;
        this.content = content;
        this.posttime = posttime;
        this.haspicture = haspicture;
        this.picchange=picchange;
        this.writerhaspicture = writerhaspicture;


        if(ownerid==writerid) //내가쓴글이라면
        {
            if(haspicture)//사진이있다면
                talkType=CHAT_TYPE_PICTURE_NOT_VISITOR;
            else
                talkType=CHAT_TYPE_TALK_NOT_VISITOR;
        }
        else
        {
            if(haspicture)
                talkType=CHAT_TYPE_PICTURE_VISITOR;
            else
                talkType=CHAT_TYPE_TALK_VISITOR;
        }
    }

    public boolean isWriterhaspicture() {
        return writerhaspicture;
    }

    public void setWriterhaspicture(boolean writerhaspicture) {
        this.writerhaspicture = writerhaspicture;
    }

    public int getPicchange() {
        return picchange;
    }

    public void setPicchange(int picchange) {
        this.picchange = picchange;
    }

    public String getWritername() {
        return writername;
    }

    public void setWritername(String writername) {
        this.writername = writername;
    }

    public int getTalkType() {
        return talkType;
    }

    public void setTalkType(int talkType) {
        this.talkType = talkType;
    }

    public int getChattingid() {
        return chattingid;
    }

    public void setChattingid(int chattingid) {
        this.chattingid = chattingid;
    }

    public int getChattingroomlistid() {
        return chattingroomlistid;
    }

    public void setChattingroomlistid(int chattingroomlistid) {
        this.chattingroomlistid = chattingroomlistid;
    }

    public int getWriterid() {
        return writerid;
    }

    public void setWriterid(int writerid) {
        this.writerid = writerid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPosttime()
    {

        String Year = this.posttime.substring(2,4);
        String Month = this.posttime.substring(5, 7);
        String Day = this.posttime.substring(8, 10);
        return Year +"년" + Month + "월" + Day + "일";
    }

    public String getSimpleTime()
    {
        String ampm;
        String Year = this.posttime.substring(2,4);
        String Month = this.posttime.substring(5, 7);
        String Day = this.posttime.substring(8, 10);
        int Hour = Integer.parseInt(this.posttime.substring(11, 13));
        String Min = this.posttime.substring(14,16);

        if(Hour > 12)
        {
            Hour -= 12;
            ampm = "pm";
        }
        else
            ampm = "am";

        return Month + "/" + Day + " " + Hour + ":" + Min + ampm;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public boolean isHaspicture() {
        return haspicture;
    }

    public void setHaspicture(boolean haspicture) {
        this.haspicture = haspicture;
    }
}
