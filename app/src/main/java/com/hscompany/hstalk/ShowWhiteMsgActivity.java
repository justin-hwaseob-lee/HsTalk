package com.hscompany.hstalk;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class ShowWhiteMsgActivity extends Activity
{

    TextView name, content;
    private Timer originalTimer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_white_msg);


        name = (TextView)findViewById(R.id.name);
        content=(TextView)findViewById(R.id.content);

        String msg = getIntent().getExtras().getString("message");

        try {
            JSONObject json = new JSONObject(msg);
            String stime = json.getString("name"); //작성한사람이름
            String slecturename = json.getString("deal");//보낸내용
            name.setText(stime);
            content.setText(slecturename);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




        originalTimer.schedule(closeWindow, 1 * 1000);    //Closes activity after 10 seconds of inactivity

    }

    private TimerTask closeWindow = new TimerTask() {

        @Override
        public void run() {
            finish();
        }
    };
}
