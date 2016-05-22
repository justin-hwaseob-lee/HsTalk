package com.hscompany.hstalk.chatting;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.hscompany.hstalk.MyPage;
import com.hscompany.hstalk.R;
import com.hscompany.hstalk.login.UserLocalStore;

import java.util.ArrayList;

/**
 * Created by hs695 on 2016-02-09.
 */
public class ChattingAdapter extends BaseAdapter
{
    final String ServerUrl = "http://54.238.209.107";
    String surl;

    final int CHAT_TYPE_TALK_VISITOR = 0;
    final int CHAT_TYPE_TALK_NOT_VISITOR = 1;
    final int CHAT_TYPE_PICTURE_VISITOR = 2;
    final int CHAT_TYPE_PICTURE_NOT_VISITOR = 3;

    private Context con;
    private LayoutInflater inflater;
    private ArrayList<chattingData> chatData;//채팅 정보를 받아들일 리스트
    private int layout;

    AlertDialog alertDialog;
    public ChattingAdapter(ChattingActivity context,  ArrayList<chattingData> items)
    {
        this.con = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //this.layout = layout;
        chatData = items;
    }

    @Override
    public int getCount() {
        return chatData.size();
    }

    @Override
    public Object getItem(int position) {
        return chatData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return chatData.get(position).getTalkType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        /*
        if(convertView == null)
        {
            convertView = inflater.inflate(layout, parent, false);
        }
        */

        Log.d("hihwa", "here6.5????????????????????????????????????????????????");
        chattingData data = chatData.get(position);


        int type = data.getTalkType();

        switch (type) {
            case CHAT_TYPE_TALK_VISITOR:
                convertView = inflater.inflate(R.layout.listitem_chat_talk_visitor, null);
                break;
            case CHAT_TYPE_TALK_NOT_VISITOR:
                convertView = inflater.inflate(R.layout.listitem_chat_talk_notvisitor, null);
                break;
            case CHAT_TYPE_PICTURE_VISITOR:
                convertView = inflater.inflate(R.layout.listitem_chat_picture_visitor, null);
                break;
            case CHAT_TYPE_PICTURE_NOT_VISITOR:
                convertView = inflater.inflate(R.layout.listitem_chat_picture_notvisitor, null);
                break;
        }

        ImageView userPic = (ImageView) convertView.findViewById(R.id.userPic);
        TextView talkTime = (TextView) convertView.findViewById(R.id.talkTime);
        TextView userName = (TextView) convertView.findViewById(R.id.userName);
        TextView content = (TextView) convertView.findViewById(R.id.talkContent);
        final ImageView talkPic = (ImageView) convertView.findViewById(R.id.talkPic);

        if (data.getContent().length() == 0)
            content.setVisibility(View.GONE);
        else
            content.setText(data.getContent());


        //상대바일때만 사진을 가져옴
        if(type==CHAT_TYPE_PICTURE_VISITOR || type==CHAT_TYPE_TALK_VISITOR) //상대방인경우에
        {
            final boolean tmpyohaspicture=chatData.get(position).isWriterhaspicture();
            final boolean tmpyosex=new UserLocalStore(con).getLoggedInUser().isSex();
            final int tmpyonum=chatData.get(position).getWriterid();
            final int tmpyopicchange=chatData.get(position).getPicchange();

            if(tmpyohaspicture==true)
            {
                surl = ServerUrl+"/picture/user/" + tmpyonum + ".jpg";
                Glide.with(con)
                        .load(surl).thumbnail(0.3f).placeholder(R.drawable.smallloading)
                        .override(60,60)
                        .signature(new StringSignature(""+tmpyopicchange))
                        .transform(new MyPage.CircleTransform(con.getApplicationContext()))
                        .into(userPic);
            }
            else
            {
                if(tmpyosex==false) //내가 여자면 남자사진을 가져옴
                {
                    surl=ServerUrl+"/picture/user/default_man.jpg";
                } else
                {
                    surl=ServerUrl+"/picture/user/default_woman.jpg";
                }
                Glide.with(con)
                        .load(surl).thumbnail(0.3f).placeholder(R.drawable.smallloading)
                        .override(60,60)
                        .transform(new MyPage.CircleTransform(con.getApplicationContext()))
                        .into(userPic);
            }
            userName.setText(data.getWritername());

            userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(con);
                    //여기서 부터 시작
                    LayoutInflater inflater = (LayoutInflater) con.getSystemService(con.LAYOUT_INFLATER_SERVICE);
                    final View inflate = inflater.inflate(R.layout.dialog_chatpicture, null);

                    ImageView chatpicture = (ImageView) inflate.findViewById(R.id.chatpicture); //연사진
                    TextView toolBar_title = (TextView) inflate.findViewById(R.id.toolBar_title); //title
                    Button okbutton = (Button) inflate.findViewById(R.id.okbutton); //확인 버튼

                    String myyurl=ServerUrl+"/picture/user/default_woman.jpg";
                    if(tmpyohaspicture)
                    {
                        myyurl = ServerUrl + "/picture/user/" + tmpyonum + ".jpg";

                        Glide.with(con)
                                .load(myyurl)
                                .thumbnail(0.3f)
                                .signature(new StringSignature("" + tmpyopicchange))
                                .into(chatpicture);
                    }
                    else
                    {
                        if(tmpyosex==false)
                            myyurl=ServerUrl+"/picture/user/default_man.jpg";

                        Glide.with(con)
                                .load(myyurl)
                                .thumbnail(0.3f)
                                .into(chatpicture);
                    }
                    toolBar_title.setText("HS TALK - 사진보기");
                    builder.setView(inflate);

                    alertDialog = builder.create();
                    alertDialog.show();
                    WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes(); //대화연결창 크기조절
                    params.width=1455;
                    params.height=2480;
                    params.x=-500;
                    alertDialog.getWindow().setAttributes(params);


                    okbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss(); //창 닫아주고
                        }
                    });





                }
            });
        }
        //userPic.setImageBitmap(data.getUserPic());


        talkTime.setText(data.getSimpleTime());



        if (type == CHAT_TYPE_PICTURE_VISITOR || type == CHAT_TYPE_PICTURE_NOT_VISITOR)
        {
            final int tmpinfo=chatData.get(position).getChattingid();
            surl = ServerUrl+"/picture/chatting/" + tmpinfo + ".jpg";
            Glide.with(con)
                    .load(surl)
                    .override(180,240)
                    .thumbnail(0.3f)
                    .into(talkPic);

             talkPic.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     AlertDialog.Builder builder = new AlertDialog.Builder(con);
                     //여기서 부터 시작
                     LayoutInflater inflater = (LayoutInflater) con.getSystemService(con.LAYOUT_INFLATER_SERVICE);
                     final View inflate = inflater.inflate(R.layout.dialog_chatpicture, null);

                     ImageView chatpicture = (ImageView) inflate.findViewById(R.id.chatpicture); //연사진
                     TextView toolBar_title = (TextView) inflate.findViewById(R.id.toolBar_title); //title
                     Button okbutton = (Button) inflate.findViewById(R.id.okbutton); //확인 버튼


                     String myyurl = ServerUrl+"/picture/chatting/" + tmpinfo + ".jpg";
                     Glide.with(con)
                             .load(myyurl)
                             .thumbnail(0.3f)
                             .into(chatpicture);

                     toolBar_title.setText("HS TALK - 사진보기");
                     builder.setView(inflate);

                     alertDialog = builder.create();
                     alertDialog.show();
//                     alertDialog.getWindow().setLayout(1500,2480);

                     WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes(); //대화연결창 크기조절
                     params.width=1455;
                     params.height=2480;
                     params.x=-500;
                     alertDialog.getWindow().setAttributes(params);


                     okbutton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             alertDialog.dismiss(); //창 닫아주고
                         }
                     });
                 }
             });
        }



        return convertView;
    }


    public int getLastIndex() {
        if (getCount() == 0)
            return -1;
        chattingData cur = (chattingData) this.getItem(getCount() - 1);
        return cur.getChattingid();
    }
}