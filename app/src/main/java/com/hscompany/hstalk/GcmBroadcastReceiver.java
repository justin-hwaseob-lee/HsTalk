package com.hscompany.hstalk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by hs695 on 2016-02-13.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;
    boolean black=false;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("fuck", "inside?");
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.

        Log.d("justin", "beforeinside?");
        getMessage(context, (intent.setComponent(comp)));
        Log.d("justin", "afterinside?");

        if(black==false && working.bAppRunned==true) //실행중이면
        { //실해x
        }
        else {

                        Log.d("justin", "white?");
                        String msg = intent.setComponent(comp).getStringExtra("message");


                        Log.d("justin msg", msg);
                        Intent tintent = new Intent(context, ShowWhiteMsgActivity.class);
                        tintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        tintent.putExtra("message", msg);
                        context.startActivity(tintent);



            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }

    }

    private void getMessage(Context context, Intent upintent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        if(!isScreenOn)//검은화면
        {

            black=true;
            Log.d("justin", "black?");
            String msg = upintent.getStringExtra("message");


            Log.d("justin msg", msg);
            Intent intent = new Intent(context, ShowMsgActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("message", msg);
            context.startActivity(intent);

        }



    }




}