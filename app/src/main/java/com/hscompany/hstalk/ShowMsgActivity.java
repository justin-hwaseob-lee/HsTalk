package com.hscompany.hstalk;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class ShowMsgActivity extends Activity {

    TextView name, content;

    String message;
    int tmpyourid, tmpchattingroomlistid;
    int delayTime = 0; //여기서 delaytime이 실제 내폰에 설정된 꺼지는시간
    private Timer originalTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_msg);

        //originalTimer.schedule(closeWindow, 5 * 1000);    //Closes activity after 10 seconds of inactivity

        delayTime = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 15*1000); //15초
        Log.d("time", "" + delayTime); //여기서 delaytime이 실제 내폰에 설정된 꺼지는시간
        originalTimer.schedule(closeWindow, delayTime - 1); //두번쨰 전달인자가 그시간후 앱이 꺼지는건데 내폰에 설정된시간 보다 약간빨리 꺼지게하면 ~!된당


        message = getIntent().getExtras().getString("message"); //이 string message에 이미 메시지 가지고 있다.

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                // 키잠금 해제하기
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                // 화면 켜기
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        name = (TextView)findViewById(R.id.name);
        content=(TextView)findViewById(R.id.content);
        //tv.setText(getIntent().getStringExtra("답변이 도착했어요!"));
        // message = getIntent().getExtras().getString("message"); //이 string message에 이미 메시지 가지고 있다.


        //추가부분
        try {
            JSONObject json = new JSONObject(message);
            String stime = json.getString("name"); //작성한사람이름

            String slecturename = json.getString("deal");//보낸내용

            tmpyourid = Integer.parseInt(json.getString("valid"));//상대방id
            tmpchattingroomlistid = Integer.parseInt(json.getString("address"));//chattingroomlistid

            name.setText(stime);
            content.setText(slecturename);
            /*
            if(sroom.equals("talk"))//talk인경우
            {
                name.setText(stime+"님의 대화 도착"); //작성자 이름 넣기
                content.setText(slecturename);
            }
            else //review인경우
            {
                name.setText(stime+"님의 리뷰 도착"); //작성자 이름 넣기
                content.setText(slecturename);
            }
            */
//            tv.setText(stime);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        Button ib = (Button)findViewById(R.id.confirm);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 확인버튼을 누르면 앱의 런처액티비티를 호출한다.

                Intent intent = new Intent(ShowMsgActivity.this, MainActivity.class);
                intent.putExtra("yourid", tmpyourid);
                intent.putExtra("chattingroomlistid", tmpchattingroomlistid);

                //placenumber 빼주는데


                Log.d("mylogmessage here", "fuck");


                NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(new GcmIntentService().NOTIFICATION_ID); //클릭하면 알림 제거되는거

                //               startActivityForResult(intent, 0);//여기가 수정한데
                startActivity(intent);
                Log.d("mylogmessage here", "fuck");
                finish();
            }
        });
        Button close = (Button)findViewById(R.id.cancel);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 확인버튼을 누르면 앱의 런처액티비티를 호출한다.

                finish();
            }
        });

    }

    private TimerTask closeWindow = new TimerTask() {

        @Override
        public void run() {
            finish();
            //여기다가 꺼지는거 처리햐야되는뎅.ㅠㅠㅠ
        }
    };


}
