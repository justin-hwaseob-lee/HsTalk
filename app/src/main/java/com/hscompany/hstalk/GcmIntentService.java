package com.hscompany.hstalk;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hscompany.hstalk.chatting.ChattingActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hs695 on 2016-02-13.
 */
public class GcmIntentService extends IntentService {
    Context context;
    public static final int NOTIFICATION_ID = 1;
    //public int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final String TAG = "GCM Demo";
    String placeNumber;


    public GcmIntentService() {
        super("GcmIntentService");
        Log.d("fuck", "man2");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        Log.d("fuck", "man");
        Bundle extras = intent.getExtras();
        String msg = intent.getStringExtra("message");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        /*
        if(isServiceRunningCheck() )
        Log.d("fuckman", "fuck1");
        else
            Log.d("fuckman", "fuck2");
            */
/*
        if(checkscreenon==null && !isServiceRunningCheck()) //화면이 켜져있고 내앱이 실행중이 아니라면
        {
            Intent myintent2 = new Intent(getApplicationContext(), ShowWhiteMsgActivity.class);
            myintent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            myintent2.putExtra("message", msg);
            getApplicationContext().startActivity(myintent2);
        }
*/


        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
	               /*
	                */
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                //sendNotification("Received: " + extras.tofString());

                sendNotification(msg);

                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }



    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound);

        String tname="";
        String tmessage="새로운 글이 등록되었습니다!";
        int tmpyourid=0;
        int tmpchattingroomlistid=0;
        //추가부분
        try {
            JSONObject json = new JSONObject(msg);
            tname = json.getString("name"); //작성한사람이름
            tmessage = json.getString("deal");//보낸내용
            tmpyourid = Integer.parseInt(json.getString("valid"));//상대방id
            tmpchattingroomlistid = Integer.parseInt(json.getString("address"));//chattingroomlistid

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Intent myintent = new Intent(this, ChattingActivity.class);
        myintent.putExtra("yourid", tmpyourid);
        myintent.putExtra("chattingroomlistid", tmpchattingroomlistid);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);




        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(tname)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(tmessage))
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setVibrate(new long[]{1000, 1000, 1000})
                        .setContentText(tmessage);



        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


}