package com.hscompany.hstalk.chatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hscompany.hstalk.Network;
import com.hscompany.hstalk.R;
import com.hscompany.hstalk.login.UserData;
import com.hscompany.hstalk.login.UserLocalStore;
import com.hscompany.hstalk.working;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChattingActivity extends AppCompatActivity implements AbsListView.OnScrollListener
{
    public static final String ServerUrl = "http://54.238.209.107";
    Intent myIntent;

    final int GET_DATA_FIRST_TIME = -1;
    final int GET_DATA_OLD = 0;
    final int GET_DATA_NEW = 1;

    //사진관련
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    String picPathChatting = null; //내 폰내에서 불러올 사진 주소
    Uri mImageCaptureUri;
    Bitmap pic;


    // 로그인 정보
    private UserLocalStore userLocalStore =null;
    private UserData userData =null;
    private boolean loggedIn=false;

    //UI 매핑
    private ListView chattingLv; //채팅내용을 보여줄 리스트 뷰
    private ImageButton toolBar_backBtn; //닫기 버튼
    private ImageButton picAddButton; //사진 추가버튼
    private EditText editChatInput; //채팅글 적는내용
    private ImageButton sendChattingButton; //채팅전송 버튼
    private ImageView picture_check; //사진 추가시 체ㅡ표시
    private ImageView talkPic; //사지같이보내는경우

    //채팅방정보
    int myid; //내아이디
    int yourid; //상대아이디
    int chattingroomlistid; //채팅방아이디



    private ChattingAdapter chatadapter = null;
    private ArrayList<chattingData> chatData;//채팅 정보를 받아들일 리스트
    private int locker = 0;
    private boolean mLockListView = true;
    private boolean isLastChatting = false;
    private Timer timerChatting = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        //setting
        chattingLv = (ListView)findViewById(R.id.listview_chatting);
        toolBar_backBtn = (ImageButton)findViewById(R.id.toolBar_backBtn);//닫기버튼
        picAddButton = (ImageButton)findViewById(R.id.buttonAddPicture); //사진 추가버튼
        editChatInput = (EditText)findViewById(R.id.editText_chat); //채팅글 적는내용
        sendChattingButton = (ImageButton)findViewById(R.id.buttonSend); //채팅전송 버튼
        picture_check = (ImageView)findViewById(R.id.imageView_check); //사진 추가시 체크표시
        talkPic = (ImageView)findViewById(R.id.talkPic); //채팅사진
        myIntent = getIntent();



        // 로그인 정보
        userLocalStore = new UserLocalStore(this);
        loggedIn = userLocalStore.getUserLoggedIn();
        if (loggedIn) {
            userData = userLocalStore.getLoggedInUser();
        }


        myid=userData.getUsernumber(); //내 아이디
        yourid=myIntent.getExtras().getInt("yourid"); //상대 아이디
        chattingroomlistid=myIntent.getExtras().getInt("chattingroomlistid"); // 채팅방 아이디

        //listener
        //닫기버튼
        toolBar_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

        Log.d("fuckyou", "myid = " + myid + " / yourid = " + yourid + " / chattingroomlistid = " + chattingroomlistid);

        AsyncTask async = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d("fuckyou", "doinbackground");
            chatData = getChatData(GET_DATA_FIRST_TIME);
            //chatadapter = new ChattingAdapter(getApplicationContext(), chatData);
            chatadapter = new ChattingAdapter(ChattingActivity.this, chatData);
            return chatadapter;
        }
        @Override
        protected void onPostExecute(Object result) {
            ChattingAdapter adapter = (ChattingAdapter) result;
            chattingLv.setAdapter(adapter);
            chattingLv.setOnScrollListener(ChattingActivity.this); //동적 스크롤 하기위해
            chattingLv.setDivider(null); //구분선 없애기
            chattingLv.setSelection(chatadapter.getCount() - 1); //채팅은 맨밑부터 보여주어야 하니깐;
            Log.d("fuckyou", "postexcetue");



            startChattingRefreshTask();
        }
    }.execute();


        /*
        chattingLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (chatData.get(position).isHaspicture())//채팅내용에 사진이 있는경우만
                {
                    Log.d("super", "super21");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    //여기서 부터 시작
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
                    final View inflate = inflater.inflate(R.layout.dialog_chatpicture, null);

                    ImageView chatpicture = (ImageView) inflate.findViewById(R.id.chatpicture); //연사진
                    TextView toolBar_title = (TextView) inflate.findViewById(R.id.toolBar_title); //title

                    String url = ServerUrl + "/picture/chatting/" + chatData.get(position).getChattingid() + ".jpg";
                    Glide.with(getApplicationContext())
                            .load(url).thumbnail(0.3f)
                            .into(chatpicture);
                    toolBar_title.setText("HS TALK - 사진보기");
                    builder.setView(inflate);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes(); //대화연결창 크기조절
                    params.height=2200;
                    alertDialog.getWindow().setAttributes(params);

    }
    else
    {
        Log.d("super", "super2");
    }
}
});
         */
    }

    @Override
    public void onPause() {
        super.onPause();
        working.bAppRunned=false;
        stopChattingRefreshTask();
    }


    @Override
    public void onResume() {
        super.onResume();
        working.bAppRunned=true;

        if (chatadapter != null)
            startChattingRefreshTask();
    }


    private void startChattingRefreshTask() {
        TimerTask refresh = new TimerTask() {
            @Override
            public void run() {
                if (chatData != null) {

                    ArrayList<chattingData> latest=getChatData(GET_DATA_NEW, chatadapter.getLastIndex());

                    if (latest.size() != 0) {
                        if (chatadapter.getLastIndex() < latest.get(0).getChattingid())
                        {
                            chatData.addAll(latest);

                            Message msg = Message.obtain();
                            Bundle data = new Bundle();
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }

                    Log.d("fuckyou", "inside StartChattingRefreshtask");
                    /*
                    if (latest.size() != 0) {
                        if (chatadapter.getLastIndex() < latest.get(0).getChattingid())
                        {
                            chatData.addAll(latest);

                            Message msg = Message.obtain();
                            Bundle data = new Bundle();
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }
                    */
                }
            }

        };

        if (timerChatting == null) {
            timerChatting = new Timer();
            timerChatting.schedule(refresh, 1000, 1000);
        }
    }


    private void stopChattingRefreshTask() {
        if (timerChatting != null) {
            timerChatting.cancel();
            timerChatting = null;
        }
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();
            Log.d("fuckyou", "inside handler");
                chatadapter.notifyDataSetChanged();
                chattingLv.setSelection(chatadapter.getCount() - 1);

        }
    };



    public void onClickuserpic(View view) //chatting사진을 클릭한경우
    {

        Log.d("hiow", "hiow1");
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        //여기서 부터 시작
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        Log.d("hiow", "hiow2");
        final View inflate = inflater.inflate(R.layout.dialog_chatpicture, null);

        Log.d("hiow", "hiow3");
        ImageView chatpicture = (ImageView) inflate.findViewById(R.id.chatpicture); //연사진
        TextView toolBar_title = (TextView) inflate.findViewById(R.id.toolBar_title); //title


        Log.d("hiow", "hiow4");
        Bitmap tempImg = view.getDrawingCache();
        chatpicture.setImageBitmap(tempImg);
        Log.d("hiow", "hiow5");
        //chatpicture.setImageDrawable(tempImg);

        toolBar_title.setText("HS TALK - 사진보기");
        builder.setView(inflate);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes(); //대화연결창 크기조절
        params.height=2200;
        alertDialog.getWindow().setAttributes(params);

    }

    public void callme()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("로그인정보가 잘못되었습니다.");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }


    public void onClickchatpic(View view) //chatting사진을 클릭한경우
    {

        Log.d("hiow", "hiow1");
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        //여기서 부터 시작
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        Log.d("hiow", "hiow2");
        final View inflate = inflater.inflate(R.layout.dialog_chatpicture, null);

        Log.d("hiow", "hiow3");
        ImageView chatpicture = (ImageView) inflate.findViewById(R.id.chatpicture); //연사진
        TextView toolBar_title = (TextView) inflate.findViewById(R.id.toolBar_title); //title


Log.d("hiow", "hiow4");
        Bitmap tempImg = view.getDrawingCache();
        chatpicture.setImageBitmap(tempImg);
        Log.d("hiow", "hiow5");
        //chatpicture.setImageDrawable(tempImg);

        toolBar_title.setText("HS TALK - 사진보기");
        builder.setView(inflate);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes(); //대화연결창 크기조절
        params.height=2200;
        alertDialog.getWindow().setAttributes(params);

    }

    //채팅보내기 버튼 클릭했을시 호출
    public void onClickSend(View view)
    {
        int picAdded = picture_check.getVisibility();

        if (chattingroomlistid==0) //처음 대화를 하는경우
        {
            Log.d("fuckyou", "nowe didnt    ");
            //첫 대화내용을 전송하는 경우
            //새로 db instance들 생성하게 하고
            if(editChatInput.getText().length()==0 && picAdded == View.GONE) //아무것도 입력하지 않은경우
            {
                Toast.makeText(getLayoutInflater().getContext(),"채팅을 입력 하세요.", Toast.LENGTH_SHORT).show();
            }
            else //뭐라도 입력한 경우
            {
                //chattingRoomList db 생성하고 그 id이용해서
                //chatting db 생성하고
                String[][] roomlistmakeData=new String[][]{{"user1id", ""+myid}, {"user2id", ""+yourid}};
                String roomlistmakeresult = new Network().sendDatePost(ServerUrl+"/makechattingroomlist.php", roomlistmakeData);
                try
                {
                    JSONObject jsonObj = new JSONObject(roomlistmakeresult);
                    JSONArray jsonArr = jsonObj.getJSONArray("mychattingroom"); //php파일에서 mychattingroom로 저장한 방정보를 가져옴
                    jsonObj = jsonArr.getJSONObject(0);

                    chattingroomlistid = jsonObj.getInt("chattingroomlistid"); //만들어진 방의 id를 집어넣음
                }
                catch(Exception e) //실패 한경우
                {
                    showErrorMessage("다수의 접속자가 몰려 잠시후 다시 시도해 주세요.");
                    return;
                }
                //chattingroomlistid에해당하는
                //chattin db에 글 게시하고
                //chattingroomlist db에 new chat랑 lastcontent, lastwriter 수정하고
                //gcm보내고

                if (editChatInput.getText().length() != 0 || !(picAdded == View.GONE))
                {
                    //chattingroomlistid에해당하는
                    //chattin db에 글 게시하고
                    //chattingroomlist db에 new chat랑 lastcontent, lastwriter 수정하고
                    //db뒤져서 마지막 작성자가 내가 아닌경우 new 0으로 바꿔주고 데이터 읽어오고
                    String result = null;
                    String[][] talk = new String[][]{{"type", "write"}, {"chattingroomlistid", "" + chattingroomlistid}, {"writerid", ""+myid}, {"content", editChatInput.getText().toString()}};

                    //text
                    if (picAdded == View.GONE)
                    {
                        result = new Network().sendDataAndPhoto(ServerUrl + "/chattingTalk.php", null, talk);
                    }
                    //picture
                    else if (picAdded == View.VISIBLE)
                    {
                        Log.d("iwantpic", "fuck1");
                        result = new Network().sendDataAndPhoto(ServerUrl + "/chattingTalk.php", picPathChatting, talk);
                        picPathChatting = null;
                    }

                    if (result.equals("Failed"))
                    {
                        Toast.makeText(getApplicationContext(), "전송실패", Toast.LENGTH_SHORT).show();
                        picture_check.setVisibility(View.GONE);
                        picPathChatting = null;
                        return;
                    }
                    else
                    {
                        //gcm 보내는 부분

                        Log.d("fuck", "fuck1");
                        String name=userLocalStore.getLoggedInUser().getNickname(); //내이름
                        String deal=editChatInput.getText().toString(); //내가 보내는 대화내용
                        if (picAdded == View.VISIBLE) //사진이라면
                            deal="사진";
                        else
                            deal=editChatInput.getText().toString(); //내가 보내는 대화내용

                        String valid=""+yourid; //상대방 id
                        String address=""+chattingroomlistid; //chattingroomlistid
                        String[][] postaData=new String[][]{{"name", name}, {"deal", deal}, {"valid", valid}, {"address", address}};

                        String hiuyresult = new Network().sendDatePost(ServerUrl+"/sendmsg.php", postaData);
                        Log.d("gcm", hiuyresult);

                    }
                    if (picture_check.getVisibility() == View.VISIBLE)
                    {
                        picture_check.setVisibility(View.GONE);
                    }

                    editChatInput.setText("");

                    ArrayList<chattingData> latest = getChatData(GET_DATA_NEW, chatadapter.getLastIndex());

                    if (latest.size() != 0) {
                        if (chatadapter.getLastIndex() < latest.get(0).getChattingid()) {
                            chatData.addAll(latest);
                            chatadapter.notifyDataSetChanged();
                            chattingLv.setSelection(chattingLv.getCount() - 1);
                        }
                    }
                }
                else//아무것도 입력하지 않은경우
                {
                    Toast.makeText(getLayoutInflater().getContext(),"입력 하세요.", Toast.LENGTH_SHORT).show();
                }

            }
        }
        else //이미 대화한 상대화 대화하는 경우
        {
            Log.d("fuckyou", "yes we did");
            if (editChatInput.getText().length() != 0 || !(picAdded == View.GONE))
            {
                //chattingroomlistid에해당하는
                //chattin db에 글 게시하고
                //chattingroomlist db에 new chat랑 lastcontent, lastwriter 수정하고
                //db뒤져서 마지막 작성자가 내가 아닌경우 new 0으로 바꿔주고 데이터 읽어오고
                String result = null;
                String[][] talk = new String[][]{{"type", "write"}, {"chattingroomlistid", "" + chattingroomlistid}, {"writerid", ""+myid}, {"content", editChatInput.getText().toString()}};

                //text
                if (picAdded == View.GONE)
                {
                    result = new Network().sendDataAndPhoto(ServerUrl + "/chattingTalk.php", null, talk);
                }
                //picture
                else if (picAdded == View.VISIBLE)
                {
                    result = new Network().sendDataAndPhoto(ServerUrl + "/chattingTalk.php", picPathChatting, talk);
                    picPathChatting = null;
                }

                if (result.equals("Failed"))
                {
                    Toast.makeText(getApplicationContext(), "전송실패", Toast.LENGTH_SHORT).show();
                    picture_check.setVisibility(View.GONE);
                    picPathChatting = null;
                    return;
                }
                else
                {
                    //gcm 보내는 부분

                    Log.d("fuck", "fuck1");
                    String name=userLocalStore.getLoggedInUser().getNickname(); //내이름
                    String deal=editChatInput.getText().toString(); //내가 보내는 대화내용
                    if (picAdded == View.VISIBLE) //사진이라면
                        deal="사진";
                    else
                        deal=editChatInput.getText().toString(); //내가 보내는 대화내용

                    String valid=""+yourid; //상대방 id
                    String address=""+chattingroomlistid; //chattingroomlistid
                    String[][] postaData=new String[][]{{"name", name}, {"deal", deal}, {"valid", valid}, {"address", address}};

                    String hiuyresult = new Network().sendDatePost(ServerUrl+"/sendmsg.php", postaData);
                    Log.d("gcm", hiuyresult);
                }
                if (picture_check.getVisibility() == View.VISIBLE)
                {
                    picture_check.setVisibility(View.GONE);
                }

                editChatInput.setText("");

                ArrayList<chattingData> latest = getChatData(GET_DATA_NEW, chatadapter.getLastIndex());

                if (latest.size() != 0) {
                    if (chatadapter.getLastIndex() < latest.get(0).getChattingid()) {
                        chatData.addAll(latest);
                        chatadapter.notifyDataSetChanged();
                        chattingLv.setSelection(chattingLv.getCount() - 1);
                    }
                }
            }
            else//아무것도 입력하지 않은경우
            {
                Toast.makeText(getLayoutInflater().getContext(),"입력 하세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ArrayList<chattingData> getChatData(int mode)
    {
        return getChatData(mode, 0);
    }

    //처음 채팅 데이터 가져올때는 mode GET_CHATTING_AT_FIRST로 설정 index는 좋아하는 숫자
    //이전 데이터는 GET_DATA_OLD, index는 가져올 숫자 +1 ex)1~100번은 101번
    //이후 데이터는 GET_DATA_NEW, index는 가져올 숫자 -1 ex)100~는 99번
    public ArrayList<chattingData> getChatData(int mode, int index)
    {

        String result = null;
        ArrayList<chattingData> chatdata = new ArrayList<chattingData>();

        if (mode == GET_DATA_FIRST_TIME)
            result = new Network().getData(ServerUrl + "/chattingTalk.php?req=getTalk&chattingroomlistid=" + chattingroomlistid);
        else if (mode == GET_DATA_OLD)
            result = new Network().getData(ServerUrl + "/chattingTalk.php?req=getTalk&chattingroomlistid=" + chattingroomlistid + "&mode=old" + "&index=" + index);
        else if (mode == GET_DATA_NEW)
            result = new Network().getData(ServerUrl + "/chattingTalk.php?req=getTalk&chattingroomlistid=" + chattingroomlistid + "&mode=new" + "&index=" + index);

        try
        {
            Log.d("fuckyouyou", "mode="+mode+" reusult="+result);

            JSONObject jsonObj=new JSONObject(result);

            //JSONObject jsonObj = new JSONObject(result);
            Log.d("fuckyou", "inside getchatdata2");
            JSONArray jsonArr = jsonObj.getJSONArray("talk");
            Log.d("fuckyou", "inside getchatdata3");
            for (int i = jsonArr.length() - 1; i >= 0; i--) {
                jsonObj = jsonArr.getJSONObject(i);
                int chattingid = Integer.parseInt(jsonObj.getString("chattingid"));
                int chattingroomlistid = Integer.parseInt(jsonObj.getString("chattingroomlistid"));
                int writerid = Integer.parseInt(jsonObj.getString("writerid"));
                String content = jsonObj.getString("content");
                String posttime = jsonObj.getString("posttime");
                int tmphaspicture = Integer.parseInt(jsonObj.getString("haspicture"));
                String writername = jsonObj.getString("writername");
                int picchange = Integer.parseInt(jsonObj.getString("picchange"));
                int tmpwriterhaspicture=Integer.parseInt(jsonObj.getString("writerhaspicture"));
                boolean writerhaspicture=false;
                if(tmpwriterhaspicture==1)writerhaspicture=true;

                boolean haspicture=false;
                Log.d("fuckyou", "yes we can");
                if(tmphaspicture==1) haspicture=true;
                chatdata.add(new chattingData(chattingid, chattingroomlistid, writerid, content, posttime, haspicture, writername, myid, picchange, writerhaspicture));
            }
            //마지막 작성자가 내가 아니면 newchat 0으로
            if(chatdata.get(chatdata.size()-1).getWriterid() != myid)
            {
                new Network().getData(ServerUrl + "/deleteroominfo.php?chattingroomlistid=" + chatdata.get(chatdata.size()-1).getChattingroomlistid());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chatdata;
    }
    private void showErrorMessage(String msg)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ChattingActivity.this);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }






    //스크롤 관련
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d("hihwa", "ohohohohohohohohohohohohohohohohfuckfuckout");
        if (locker++ == 0) {
            Log.d("hihwa", "ohohohohohohohohohohohohohohohohfuckfuck");
            mLockListView = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, final int firstVisibleItem, final int visibleItemCount, int totalItemCount) {

        if (chatadapter != null)
        {
            if (chatadapter.getCount() > visibleItemCount && locker != 0)
            {
                if (firstVisibleItem == 0 && !isLastChatting && !mLockListView)
                {
                    mLockListView = true;

                    chattingLv.setSelection(3);

                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                   // final View loadingView = inflater.inflate(R.layout.listitem_loading, null);
                  //  chattingLv.addHeaderView(loadingView);
                    /*
                    AsyncTask asyn = new AsyncTask()
                    {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            chattingData curData = (chattingData) chatadapter.getItem(0);
                            ArrayList<chattingData> receive = getChatData(GET_DATA_OLD, curData.getChattingid());
                            int receiveSize = receive.size();

                            if (receiveSize == 0)
                                isLastChatting = true;

                            chatadapter.notifyDataSetChanged();
                            int tmpyo=receiveSize-1;
                            for (int i=tmpyo; i >= 0; i--) {
                                chatData.add(0, receive.get(i));
                            }
                            return tmpyo;
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                            chattingLv.removeHeaderView(loadingView);
                            int index = (int) result;
                            chattingLv.setSelection(index);
                            mLockListView = false;
                        }
                    }.execute();

*/
                    chattingData curData = (chattingData) chatadapter.getItem(0);
                    Log.d("hihwa", "here1");
                    ArrayList<chattingData> receive = getChatData(GET_DATA_OLD, curData.getChattingid());
                    Log.d("hihwa", "here2");
                    int receiveSize = receive.size();

                    if (receiveSize == 0)
                        isLastChatting = true;

                    Log.d("hihwa", "here3");
                    int tmpyo=receiveSize-1;
                    for (int i=tmpyo; i >= 0; i--) {
                        Log.d("hihwa", "fuck");
                        chatData.add(0, receive.get(i));
                    }
                    Log.d("hihwa", "here4");
                    Log.d("hihwa", "here4.5");

                    Log.d("hihwa", "here5");
                    Log.d("hihwa", "here6");
                    chatadapter.notifyDataSetChanged();
                    Log.d("hihwa", "here7");
                    chattingLv.setSelection(tmpyo);
                    Log.d("hihwa", "here8");
                    mLockListView = false;
                    Log.d("hihwa", "here9");
/*
                    if (isLastChatting == true)
                        chattingLv.removeHeaderView(loadingView);
                        */
                }
            }
        }
    }




























    //사진관련
    public void onClickFindPicture(View view) {
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakePhotoAction();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();

    }

    /**
     * 카메라 호출 하기
     */
    private void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Crop된 이미지를 저장할 파일의 경로를 생성
        mImageCaptureUri = createSaveCropFile();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);


        //push by lhj 20150925
        onPause();
    }

    /**
     * 앨범 호출 하기
     */
    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);

        //push by lhj 20150925
        onPause();
    }

    /**
     * Result Code
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM: {

                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
                File original_file = getImageFile(mImageCaptureUri);

                mImageCaptureUri = createSaveCropFile();
                File cpoy_file = new File(mImageCaptureUri.getPath());

                // SD카드에 저장된 파일을 이미지 Crop을 위해 복사한다.
                copyFile(original_file, cpoy_file);
            }

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                //이미지 정사각형 최소 최대설정(프로필사진으로 쓸거라서)
                intent.putExtra("outputX", 360);
                intent.putExtra("outputY", 480);
                intent.putExtra("aspectX", 3);
                intent.putExtra("aspectY", 4);
                intent.putExtra("scale", true);


                // Crop한 이미지를 저장할 Path
                intent.putExtra("output", mImageCaptureUri);

                // Return Data를 사용하면 번들 용량 제한으로 크기가 큰 이미지는
                // 넘겨 줄 수 없다.
//			intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }

            case CROP_FROM_CAMERA: {
                // Crop 된 이미지를 넘겨 받습니다.

                picPathChatting = mImageCaptureUri.getPath();
                picture_check.setVisibility(View.VISIBLE);
                /*
                pic = BitmapFactory.decodeFile(picPathChatting);
                talkPic.setImageBitmap(pic);
                */

                break;
            }
        }
    }
    /**
     * Crop된 이미지가 저장될 파일을 만든다.
     *
     * @return Uri
     */
    private Uri createSaveCropFile() {
        Uri uri;
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        return uri;
    }


    /**
     * 선택된 uri의 사진 Path를 가져온다.
     * uri 가 null 경우 마지막에 저장된 사진을 가져온다.
     *
     * @param uri
     * @return
     */
    private File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    /**
     * 파일 복사
     *
     * @param srcFile  : 복사할 File
     * @param destFile : 복사될 File
     * @return
     */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    //사진 끝
}
