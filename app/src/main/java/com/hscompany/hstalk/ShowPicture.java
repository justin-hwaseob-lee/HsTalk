package com.hscompany.hstalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by hs695 on 2016-02-10.
 */
public class ShowPicture extends AppCompatActivity
{
    private Button buttonchatting;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chatpicture);

        buttonchatting = (Button)findViewById(R.id.buttonchatting);
        ImageView chatpicture = (ImageView) findViewById(R.id.chatpicture); //연사진
        TextView toolBar_title = (TextView) findViewById(R.id.toolBar_title); //title

        Glide.with(getApplicationContext())
                .load(getIntent().getExtras().getInt("myyurl"))
                .thumbnail(0.3f)
                .into(chatpicture);

        toolBar_title.setText("HS TALK - 사진보기");

        buttonchatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
