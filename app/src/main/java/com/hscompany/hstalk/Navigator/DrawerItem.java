package com.hscompany.hstalk.Navigator;

/**
 * Created by hs695 on 2016-02-05.
 */
public class DrawerItem
{
    private String text;
    private int img;

    public DrawerItem(String atext, int aimg) {
        text = atext;
        img = aimg;
    }

    public int getImg() {
        return img;
    }

    public String getText() {
        return text;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setText(String text) {
        this.text = text;
    }
}
