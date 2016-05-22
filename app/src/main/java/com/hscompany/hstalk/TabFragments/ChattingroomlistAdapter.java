package com.hscompany.hstalk.TabFragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.hscompany.hstalk.MyPage;
import com.hscompany.hstalk.R;
import com.hscompany.hstalk.chatting.chattingroomData;
import com.hscompany.hstalk.login.UserData;
import com.hscompany.hstalk.login.UserLocalStore;

import java.util.ArrayList;

/**
 * Created by hs695 on 2016-02-09.
 */
public class ChattingroomlistAdapter extends BaseAdapter implements Filterable {
    final String ServerUrl = "http://54.238.209.107";

    UserData userData;
    int myid;
    int yourid;
    boolean yoursex;
    boolean yourhaspicture;
    String yourname;
    int yourpicchange;

    String surl;

    TextView howmany;

    private Context con;
    private LayoutInflater inflater;
    private ArrayList<chattingroomData> chattingroomDataList; //이성 정보를 받아들일 리스트
    private ArrayList<chattingroomData> saveList = null;
    private int layout;
    private Filter filter;

    public ChattingroomlistAdapter(Context context, int layout, final ArrayList<chattingroomData> chattingroomDataList) {
        this.con = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.chattingroomDataList = chattingroomDataList;
        this.layout = layout;
    }


    @Override
    public int getCount() {
        return chattingroomDataList.size();
    }

    @Override
    public chattingroomData getItem(int position) {
        return chattingroomDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        userData = new UserLocalStore(con).getLoggedInUser();
        myid = userData.getUsernumber();

        //사진관련 sample
        final ImageView img = (ImageView) convertView.findViewById((R.id.imageView)); //상대방 사진보여주기
        //img.setImageBitmap(getCircleBitmap(userDataList.get(position).getUserPic()));

        if (myid == chattingroomDataList.get(position).getUser1id()) //내아이디가 user1id랑 같다면
        {
            //user2가 상대방이다
            yourid = chattingroomDataList.get(position).getUser2id();
            yourname = chattingroomDataList.get(position).getUser2name();
            yoursex = chattingroomDataList.get(position).isUser2sex();
            yourhaspicture = chattingroomDataList.get(position).isUser2haspicture();
            yourpicchange = chattingroomDataList.get(position).getUser2picchange();
        } else {
            //user1이 상대방이다
            yourid = chattingroomDataList.get(position).getUser1id();
            yourname = chattingroomDataList.get(position).getUser1name();
            yoursex = chattingroomDataList.get(position).isUser1sex();
            yourhaspicture = chattingroomDataList.get(position).isUser1haspicture();
            yourpicchange = chattingroomDataList.get(position).getUser1picchange();
        }

        //채팅방 리스트에 대화상대 사진보여주기하기
        if (yourhaspicture) {
            surl = ServerUrl + "/picture/user/" + yourid + ".jpg";
            Glide.with(con)
                    .load(surl).thumbnail(0.3f).placeholder(R.drawable.smallloading)
                    .override(60, 60)
                    .signature(new StringSignature("" + yourpicchange))
                    .transform(new MyPage.CircleTransform(con.getApplicationContext()))
                    .into(img);
        } else {
            if (yoursex)//남자인경우
            {
                surl = ServerUrl + "/picture/user/default_man.jpg";
            } else {
                surl = ServerUrl + "/picture/user/default_woman.jpg";
            }
            Glide.with(con)
                    .load(surl).thumbnail(0.3f).placeholder(R.drawable.smallloading)
                    .override(60, 60)
                    .transform(new MyPage.CircleTransform(con.getApplicationContext()))
                    .into(img);
        }

        //나중에 몇개 대화 새로 왔는지 표시해주기여기다가

        //상대방 닉네임
        TextView txt1 = (TextView) convertView.findViewById(R.id.listitem_large_textView);
        txt1.setText(yourname);

        //마지막 대화내용표시
        TextView txt2 = (TextView) convertView.findViewById(R.id.listitem_small_textView1);
        txt2.setText(chattingroomDataList.get(position).getLastcontent());

        //시간표시
        TextView txt3 = (TextView) convertView.findViewById(R.id.date);
        txt3.setText(chattingroomDataList.get(position).getSimpleTime());

        //몇개 메시지 왔는지 표시
        howmany = (TextView) convertView.findViewById(R.id.howmany);

        if(chattingroomDataList.get(position).getLastwriter() != myid && chattingroomDataList.get(position).getNewchat()>0) //마지막 작성자가 내가아니면
        {
            howmany.setText(""+chattingroomDataList.get(position).getNewchat());
            howmany.setVisibility(View.VISIBLE);
        }
        else //마지막작성자가 나라면
        {
            howmany.setVisibility(View.GONE);
        }

        return convertView;

    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new mychattingroomFilter();
        }

        return filter;
    }

    public class mychattingroomFilter extends Filter //실시간으로 검색 결과 보여주느 필터(닉네임검색)
    {
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            if (saveList != null) {
                chattingroomDataList = saveList;
            }

            FilterResults Result = new FilterResults();
            // if constraint is empty return the original names
            if (constraint.length() == 0) {
                Result.values = chattingroomDataList;
                Result.count = chattingroomDataList.size();
                return Result;
            }

            ArrayList<chattingroomData> FilteredList = new ArrayList<chattingroomData>();
            String filterString = constraint.toString().toLowerCase(); //검색하는단어
            String filterableString;

            for (int i = 0; i < chattingroomDataList.size(); i++) {
                String searchname;
                if (myid == chattingroomDataList.get(i).getUser1id()) //user1이 나면
                    searchname = chattingroomDataList.get(i).getUser2name();
                else
                    searchname = chattingroomDataList.get(i).getUser1name();


                filterableString = searchname; //chattingroomDataList돌면서 검색하는단어가 포함되는 단어가 있는지 체크해서
                if (filterableString.toLowerCase().contains(filterString)) {
                    FilteredList.add(chattingroomDataList.get(i)); //FilteredList에 해당되는 내용 추가해줌
                }
            }
            Result.values = FilteredList;
            Result.count = FilteredList.size();

            saveList = chattingroomDataList;
            return Result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method

            chattingroomDataList = (ArrayList<chattingroomData>) results.values;
            notifyDataSetChanged();
        }


    }

    public String getFirstIndex() {
        if (getCount() == 0)
            return null;
        chattingroomData cur = (chattingroomData) this.getItem(0);
        return cur.getLastdate();
    }
}


