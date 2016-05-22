package com.hscompany.hstalk.TabFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.hscompany.hstalk.MainActivity;
import com.hscompany.hstalk.Network;
import com.hscompany.hstalk.R;
import com.hscompany.hstalk.chatting.ChattingActivity;
import com.hscompany.hstalk.login.UserData;
import com.hscompany.hstalk.login.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hs695 on 2016-02-07.
 */
public class SectionsFragment1 extends Fragment
{

    final String ServerUrl = "http://54.238.209.107";

    EditText search; //검색할 내용 입력란
    ListView list; //뿌려줄 리스트

    //메인 리스트뷰
    FriendlistAdapter Adapter;

    //대화연결창
    AlertDialog alertDialog;

    View rootView;

    ProgressBar progressCircle;
    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<UserData> userDataList;

    ImageView friendimage;
    String url;















    public SectionsFragment1() {
    }

    // PlaceholderFragment.newInstance() 와 똑같이 추가
    public static SectionsFragment1 newInstance(int SectionNumber)
    {
        SectionsFragment1 fragment = new SectionsFragment1();
        Bundle args = new Bundle();
        args.putInt("section_number", SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_page1, container, false);

        //Async 없이 네트워크 사용가능하게
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        //Define visual things...
        search = (EditText)rootView.findViewById(R.id.edittext_search);
        list = (ListView)rootView.findViewById(R.id.listView);
        userDataList = new ArrayList<UserData>(); //list view에 보여줄 목록(친구)
       // progressCircle = (ProgressBar) rootView.findViewById(R.id.progresscircle);



        settingNewInfo();//친구목록 userDataList에 담기


        //메인 리스트뷰
        Adapter = new FriendlistAdapter(getActivity(), R.layout.friendlistitem, userDataList);
        list.setTextFilterEnabled(true); //list UI에 필터적용시키기(검색하는필터 적용시켜야됨)
        list.setAdapter(Adapter); //list UI에 Adapter 추가시켜주기

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(true);//refresh 시키고
                settingNewInfo();
                Adapter = new FriendlistAdapter(getActivity(), R.layout.friendlistitem, userDataList);
                list.setTextFilterEnabled(true); //list UI에 필터적용시키기(검색하는필터 적용시켜야됨)
                list.setAdapter(Adapter); //list UI에 Adapter 추가시켜주기
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //이제 리스트보고 회원정보 클릭시 프로필화면 보여주도록 띄어주는부분 구현
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                Log.d("fuck", "onItem click");
                //final AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);


                final Context mContext = getActivity();

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


                //여기서 부터 시작
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final View inflate = inflater.inflate(R.layout.dialog_friend, null);

                //ui 연결
                friendimage = (ImageView) inflate.findViewById(R.id.friendimage); //개인사진
                Button buttonchatting = (Button) inflate.findViewById(R.id.buttonchatting); //1:1채팅 버튼
                TextView toolBar_title = (TextView) inflate.findViewById(R.id.toolBar_title); //title



                int userNum = userDataList.get(position).getUsernumber();

                if(userDataList.get(position).isHaspicture()==true)
                {
                    url = ServerUrl+"/picture/user/" + userNum + ".jpg";
                    Glide.with(mContext)
                            .load(url).thumbnail(0.3f)
                            .signature(new StringSignature("" + userDataList.get(position).getPicchange()))
                            .into(friendimage);
                }
                else
                {
                    if(userDataList.get(position).isSex()==true)
                    {
                        url=ServerUrl+"/picture/user/default_man.jpg";
                    } else
                    {
                        url=ServerUrl+"/picture/user/default_woman.jpg";
                    }
                    Glide.with(mContext)
                            .load(url).thumbnail(0.3f)
                            .into(friendimage);
                }
                //friendimage.setImageBitmap(userDataList.get(position).getUserPic()); //사진 세팅
                toolBar_title.setText("HS TALK - " + userDataList.get(position).getNickname() + "님");

                Log.d("fuck", "onItem click");
                builder.setView(inflate);
                // builder.setPositiveButton("작성", null);



                alertDialog = builder.create();
                alertDialog.show();

                WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes(); //대화연결창 크기조절
                params.height=2200;
                alertDialog.getWindow().setAttributes(params);

                //1:1채팅 신청시
                buttonchatting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //여기다가 내 아이디랑 상대 아이디 가지고 대화 DB만들어서 넣어주고

                        Intent myIntent = new Intent(getActivity(), ChattingActivity.class);
                        int myid=new UserLocalStore(getActivity()).getLoggedInUser().getUsernumber();
                        int yourid=userDataList.get(position).getUsernumber();
                        int chattingroomlistid=0;
                        myIntent.putExtra("myid", myid);
                        myIntent.putExtra("yourid", yourid);

                        //여기서 우리가 이미 채팅중이던 방이 있는지 확인해봐용

                        try {
                            String mresult = new Network().getData(ServerUrl + "/justchattingroomlist.php?myid=" + myid + "&yourid=" + yourid);
                            JSONObject jsonObj = new JSONObject(mresult);
                            JSONArray jsonArr = jsonObj.getJSONArray("result");
                            jsonObj = jsonArr.getJSONObject(0);

                            chattingroomlistid=Integer.parseInt(jsonObj.getString("chattingroomlistid"));
                            if(chattingroomlistid!=0)
                                myIntent.putExtra("chattingroomlistid", chattingroomlistid);
                        }
                        catch (Exception e){;}

                        startActivity(myIntent);
                        alertDialog.dismiss(); //창 닫아주고

                        Handler hd = new Handler();
                        hd.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                ((MainActivity)getActivity()).changetochatting();  //대화엑티비티로 넘어가게해줌
                            }
                        }, 1000);

                        Toast.makeText(getActivity().getApplicationContext(), userDataList.get(position).getNickname() + "님과 대화 시작", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Adapter.getFilter().filter(search.getText());
            }
        });


        return rootView;
    }

    public void settingNewInfo()
    {
        Log.d("hsfuck", "here1");
        //서버에 이성 정보 요청
        UserLocalStore userLocalStore=new UserLocalStore(getActivity());
        String result;
        boolean tmpsex=userLocalStore.getLoggedInUser().isSex();
        if(tmpsex) //남자면 여자리스트
        {
            result = new Network().getData(ServerUrl + "/friendlist2.php?req=getWomanList");
        }
        else//여자면 남자리스트
        {
            result = new Network().getData(ServerUrl + "/friendlist2.php?req=getManList");
        }
        //나중에 내 성별에 따라 이거 고쳐주기/////////////////////////////////////

        userDataList.clear();
        try {
            Log.d("hsfuck", "here2");

            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArr = jsonObj.getJSONArray("list");

            for(int i = 0 ; i < jsonArr.length(); i++)
            {
                jsonObj = jsonArr.getJSONObject(i);

                boolean hasPicture=false;

                int tmpHasPicture = jsonObj.getInt("haspicture");
                int picChange = jsonObj.getInt("picchange");
                if(tmpHasPicture==1)hasPicture=true;
                Log.d("hsfuck", "here3");
                //여기도 고쳐주기 이성여부에 따라 세팅 지금은 내가 남자라는 가정하에 여성리스트 뽑았으니 여성인 false값을 집어넣음
                if(tmpsex) //남자라면
                {

                    userDataList.add(new UserData(
                            jsonObj.getInt("usernumber"),
                            jsonObj.getString("nickname"),
                            "",
                            false,
                            hasPicture,
                            "",
                            picChange));
                    Log.d("hsfuck", "hereman");
                }
                else //여자라면
                {
                    userDataList.add(new UserData(
                            jsonObj.getInt("usernumber"),
                            jsonObj.getString("nickname"),
                            "",
                            true,
                            hasPicture,
                            "",
                            picChange));
                    Log.d("hsfuck", "herewoman");
                }
            }
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
}