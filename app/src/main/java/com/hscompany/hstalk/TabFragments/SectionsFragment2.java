package com.hscompany.hstalk.TabFragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.hscompany.hstalk.MainActivity;
import com.hscompany.hstalk.Network;
import com.hscompany.hstalk.R;
import com.hscompany.hstalk.chatting.ChattingActivity;
import com.hscompany.hstalk.chatting.chattingroomData;
import com.hscompany.hstalk.login.UserData;
import com.hscompany.hstalk.login.UserLocalStore;
import com.hscompany.hstalk.working;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hs695 on 2016-02-07.
 */
public class SectionsFragment2 extends Fragment implements AbsListView.OnScrollListener
{
    int totalnotreadtalk=0;
    int tmpforsetIcon;
    private Timer timerChattingroom = null;
    private int locker = 0;
    private boolean mLockListView = true;
    private boolean isLastChatting = false;

    final int GET_DATA_FIRST_TIME = -1;
    final int GET_DATA_OLD = 0;
    final int GET_DATA_NEW = 1;

    final String ServerUrl = "http://54.238.209.107";
    EditText search; //검색할 내용 입력란
    ListView list; //뿌려줄 리스트

    //메인 리스트뷰
    ChattingroomlistAdapter Adapter;
    View rootView;

    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<chattingroomData> chattingroomDataList;

    String url;
    int usernum;
    int yourid;
    String yourname;

    UserData userData;
    public SectionsFragment2() {

    }

    // PlaceholderFragment.newInstance() 와 똑같이 추가
    public static SectionsFragment2 newInstance(int SectionNumber)
    {
        SectionsFragment2 fragment = new SectionsFragment2();
        Bundle args = new Bundle();
        args.putInt("section_number", SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Async 없이 네트워크 사용가능하게
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        View rootView = inflater.inflate(R.layout.fragment_page2, container, false);

        //define visual things
        chattingroomDataList = new ArrayList<chattingroomData>(); //list view에 보여줄 목록
        list = (ListView)rootView.findViewById(R.id.listView);
        search = (EditText)rootView.findViewById(R.id.edittext_search);


        //로그인정보
        userData= new UserLocalStore(getActivity().getApplicationContext()).getLoggedInUser();
        usernum = userData.getUsernumber();
        //settingChattingroomInfo();//친구목록 userDataList에 담기
        chattingroomDataList = getChatroomData(GET_DATA_FIRST_TIME, "");

        //메인 리스트뷰
        Adapter = new ChattingroomlistAdapter(getActivity(), R.layout.chattingroomlistitem, chattingroomDataList);
        list.setTextFilterEnabled(true); //list UI에 필터적용시키기(검색하는필터 적용시켜야됨)
        list.setAdapter(Adapter); //list UI에 Adapter 추가시켜주기

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);//refresh 시키고
                //settingChattingroomInfo();
                chattingroomDataList = getChatroomData(GET_DATA_FIRST_TIME, "");
                Adapter = new ChattingroomlistAdapter(getActivity(), R.layout.chattingroomlistitem, chattingroomDataList);
                list.setTextFilterEnabled(true); //list UI에 필터적용시키기(검색하는필터 적용시켜야됨)
                list.setAdapter(Adapter); //list UI에 Adapter 추가시켜주기
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //이제 리스트보고 채팅방정보 클릭시 채팅방으로 이동하도록 띄어주는부분 구현
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //final AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);

                chattingroomData tempData=chattingroomDataList.get(position);
                int chattingroomlistid=tempData.getChattingroomlistid();

                if(usernum ==tempData.getUser1id())//내정보가 user1id와 같다면
                {
                    yourid=tempData.getUser2id();
                    yourname=tempData.getUser2name();
                }
                else
                {
                    yourid=tempData.getUser1id();
                    yourname=tempData.getUser1name();
                }


                Intent myIntent = new Intent(getActivity(), ChattingActivity.class);
                myIntent.putExtra("myid", usernum);
                myIntent.putExtra("yourid", yourid);
                myIntent.putExtra("chattingroomlistid", chattingroomlistid);
                myIntent.putExtra("yourname", yourname);

                //마지막작성자가 내가 아니면 읽음표시해주기
                if(tempData.getLastwriter() != usernum)
                {
                    new Network().getData(ServerUrl + "/deleteroominfo.php?chattingroomlistid=" + chattingroomlistid);
                    view.findViewById(R.id.howmany).setVisibility(View.GONE);

                }

                startActivity(myIntent);
            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Adapter.getFilter().filter(search.getText());
            }
        });

        startChattingRoomRefreshTask();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        working.bAppRunned=false;

        getChatroomData(GET_DATA_NEW, Adapter.getFirstIndex());
        stopChattingRoomRefreshTask();
    }


    @Override
    public void onResume() {
        super.onResume();
        working.bAppRunned=true;
        if (Adapter != null)
            startChattingRoomRefreshTask();
    }

    private void stopChattingRoomRefreshTask() {
        if (timerChattingroom != null) {
            timerChattingroom.cancel();
            timerChattingroom = null;
        }
    }

    private void startChattingRoomRefreshTask() {
        TimerTask refresh = new TimerTask() {
            @Override
            public void run() {
                if (chattingroomDataList != null) {

                    ArrayList<chattingroomData> latest=getChatroomData(GET_DATA_NEW, Adapter.getFirstIndex());

                    if (latest.size() != 0) {
                        if (getChangeTime(Adapter.getFirstIndex()) < getChangeTime(latest.get(0).getLastdate()))
                        {
                            chattingroomDataList.clear();
                            chattingroomDataList.addAll(latest);

                            Message msg = Message.obtain();
                            Bundle data = new Bundle();
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }

                    Log.d("fuckyou", "inside StartChattingroomRefreshtask");
                }
            }

        };

        if (timerChattingroom == null) {
            timerChattingroom = new Timer();
            timerChattingroom.schedule(refresh, 1000, 1000);
        }
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();
            Log.d("fuckyou", "inside handler");
            Adapter.notifyDataSetChanged();
        }
    };


    //스크롤 관련
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (locker++ == 0) {
            mLockListView = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, final int firstVisibleItem, final int visibleItemCount, int totalItemCount) {

        if (Adapter != null)
        {
            if (Adapter.getCount() > visibleItemCount && locker != 0)
            {
                if (firstVisibleItem == 0 && !isLastChatting && !mLockListView)
                {
                    mLockListView = true;

                    list.setSelection(3);

                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    chattingroomData curData = (chattingroomData) Adapter.getItem(0);
                    ArrayList<chattingroomData> receive = getChatroomData(GET_DATA_OLD, curData.getLastdate());
                    int receiveSize = receive.size();

                    if (receiveSize == 0)
                        isLastChatting = true;

                    int tmpyo=receiveSize-1;
                    for (int i=tmpyo; i >= 0; i--) {
                        chattingroomDataList.add(0, receive.get(i));
                    }
                    Adapter.notifyDataSetChanged();
                    list.setSelection(tmpyo);
                    mLockListView = false;
                }
            }
        }
    }

    public void settingChattingroomInfo()
    {
        String result;
        //내 채팅방리스트를 읽어옴
        result = new Network().getData(ServerUrl + "/chattingroomlist.php?userid=" + usernum);

        if(!chattingroomDataList.isEmpty())
            chattingroomDataList.clear();

        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArr = jsonObj.getJSONArray("chattingroomlist");

            for(int i = 0 ; i < jsonArr.length(); i++)
            {
                jsonObj = jsonArr.getJSONObject(i);
                //chattingroomlistid'=>$row[0], 'user1id'=>$row[1], 'user2id'=>$row[2], 'newchat'=>$row[3], 'lastcontent'=>$row[4], 'lastwriter'=>$row[5], 'lastdate'=>$row[6]);
                int chattingroomlistid = Integer.parseInt(jsonObj.getString("chattingroomlistid"));
                int user1id=Integer.parseInt(jsonObj.getString("user1id"));
                int user2id=Integer.parseInt(jsonObj.getString("user2id"));
                int newchat = Integer.parseInt(jsonObj.getString("newchat"));
                String lastcontent = jsonObj.getString("lastcontent");
                int lastwriter = Integer.parseInt(jsonObj.getString("lastwriter"));
                String lastdate = jsonObj.getString("lastdate");
                String user1name=jsonObj.getString("user1name");
                String user2name=jsonObj.getString("user2name");
                int tmpuser1sex=Integer.parseInt(jsonObj.getString("user1sex"));
                int tmpuser2sex=Integer.parseInt(jsonObj.getString("user2sex"));
                int tmpuser1haspicture=Integer.parseInt(jsonObj.getString("user1haspicture"));
                int tmpuser2haspicture=Integer.parseInt(jsonObj.getString("user2haspicture"));

                boolean user1sex=false;
                boolean user2sex=false;
                boolean user1haspicture=false;
                boolean user2haspicture=false;
                int user1picchange=Integer.parseInt(jsonObj.getString("user1picchange"));
                int user2picchange=Integer.parseInt(jsonObj.getString("user2picchange"));

                if(tmpuser1sex==1) user1sex=true;
                if(tmpuser2sex==1) user2sex=true;
                if(tmpuser1haspicture==1) user1haspicture=true;
                if(tmpuser2haspicture==1) user2haspicture=true;

                chattingroomDataList.add(new chattingroomData(chattingroomlistid, user1id, user2id,  newchat, lastcontent, lastwriter, lastdate, user1name, user2name, user1sex, user2sex, user1haspicture, user2haspicture, user1picchange, user2picchange));
            }
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
    public ArrayList<chattingroomData> getChatroomData(int mode, String index)
    {

        String result = null;
        ArrayList<chattingroomData> tmpchattingroomdata = new ArrayList<chattingroomData>();

        if (mode == GET_DATA_FIRST_TIME)
            result = new Network().getData(ServerUrl + "/chattingroomlist2.php?userid=" + usernum);
        else if (mode == GET_DATA_OLD)
            result = new Network().getData(ServerUrl + "/chattingroomlist2.php?userid=" + usernum + "&mode=old" + "&index=" + index);
        else if (mode == GET_DATA_NEW)
            result = new Network().getData(ServerUrl + "/chattingroomlist2.php?userid=" + usernum + "&mode=new" + "&index=" + index);

        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArr = jsonObj.getJSONArray("chattingroomlist");

//            totalnotreadtalk=0;
            int tmptotalnotreadtalk=0;
            for(int i = 0 ; i < jsonArr.length(); i++)
            {
                jsonObj = jsonArr.getJSONObject(i);
                //chattingroomlistid'=>$row[0], 'user1id'=>$row[1], 'user2id'=>$row[2], 'newchat'=>$row[3], 'lastcontent'=>$row[4], 'lastwriter'=>$row[5], 'lastdate'=>$row[6]);
                int chattingroomlistid = Integer.parseInt(jsonObj.getString("chattingroomlistid"));
                int user1id=Integer.parseInt(jsonObj.getString("user1id"));
                int user2id=Integer.parseInt(jsonObj.getString("user2id"));
                int newchat = Integer.parseInt(jsonObj.getString("newchat"));
                String lastcontent = jsonObj.getString("lastcontent");
                int lastwriter = Integer.parseInt(jsonObj.getString("lastwriter"));
                String lastdate = jsonObj.getString("lastdate");
                String user1name=jsonObj.getString("user1name");
                String user2name=jsonObj.getString("user2name");
                int tmpuser1sex=Integer.parseInt(jsonObj.getString("user1sex"));
                int tmpuser2sex=Integer.parseInt(jsonObj.getString("user2sex"));
                int tmpuser1haspicture=Integer.parseInt(jsonObj.getString("user1haspicture"));
                int tmpuser2haspicture=Integer.parseInt(jsonObj.getString("user2haspicture"));

                boolean user1sex=false;
                boolean user2sex=false;
                boolean user1haspicture=false;
                boolean user2haspicture=false;
                int user1picchange=Integer.parseInt(jsonObj.getString("user1picchange"));
                int user2picchange=Integer.parseInt(jsonObj.getString("user2picchange"));

                if(tmpuser1sex==1) user1sex=true;
                if(tmpuser2sex==1) user2sex=true;
                if(tmpuser1haspicture==1) user1haspicture=true;
                if(tmpuser2haspicture==1) user2haspicture=true;


                //만약 마지막작성자가 내가 아닌경우라면 totalnotreadtalk에 newchat값을 더해주고
                if(usernum != lastwriter)
                    tmptotalnotreadtalk+=newchat;
Log.d("awefkkkkkkk", "before"+totalnotreadtalk);
                    totalnotreadtalk=tmptotalnotreadtalk;
Log.d("awefkkkkkkk", "after"+totalnotreadtalk);
                tmpchattingroomdata.add(new chattingroomData(chattingroomlistid, user1id, user2id,  newchat, lastcontent, lastwriter, lastdate, user1name, user2name, user1sex, user2sex, user1haspicture, user2haspicture, user1picchange, user2picchange));
            }

            //깜박이는 버전
            /*
            if(totalnotreadtalk!=0 && tmpforsetIcon==0) //새로온채팅인경우
            {
                Log.d("awef", "awef1");
                tmpforsetIcon++;
                //((MainActivity)getActivity()).setupNewTabIcon(""+totalnotreadtalk);  //읽어온채팅이있는걸보여줌

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("awefkkkkkkk", "insde1"+totalnotreadtalk);
                                ((MainActivity) getActivity()).setupNewTabIcon("" + totalnotreadtalk);  //읽어온채팅이있는걸보여줌
                            }

                        });
                    }
                }).start();
                //((MainActivity)getActivity()).findViewById(R.id.newchatimage).setVisibility(View.VISIBLE);
                //((MainActivity)getActivity()).findViewById(R.id.newchatimage).setText(""+tmpforsetIcon);
            }
            else //다 읽은경우
            {
                tmpforsetIcon=0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("awefkkkkkkk", "insde2"+totalnotreadtalk);
                                ((MainActivity)getActivity()).setupOldTabIcon();
                            }
                        });
                    }
                }).start();
                Log.d("awef", "awef2");
               //((MainActivity)getActivity()).setupOldTabIcon();

            }
            */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            if(totalnotreadtalk!=0 ) //새로온채팅인경우
                            {
                                Log.d("awef", "awef1 totalnotreadtalk="+totalnotreadtalk);
                              //  tmpforsetIcon++;
                                ((MainActivity)getActivity()).setupNewTabIcon("" + totalnotreadtalk);
                            }
                            else
                            {
                                Log.d("awef", "awef2 totalnotreadtalk="+totalnotreadtalk);
                             //   tmpforsetIcon=0;
                                ((MainActivity)getActivity()).setupOldTabIcon();
                            }
                        }
                    });
                }
            }).start();
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
        return tmpchattingroomdata;
    }

    public int getChangeTime(String posttime)
    {
        //String Year = posttime.substring(2,4);
        String Month = posttime.substring(5, 7);
        String Day = posttime.substring(8, 10);
        String Hour = posttime.substring(11, 13);
        String Min = posttime.substring(14, 16);
        String sec = posttime.substring(17, 19);

        String total=Month+Day+ Hour+Min+sec;
        Log.d("qwer" , ""+total);
        int num=Integer.parseInt(total);
        Log.d("qwer" , ""+num);
        return num;
    }
}
