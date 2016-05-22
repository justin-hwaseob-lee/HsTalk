package com.hscompany.hstalk;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.devsmart.android.ui.HorizontalListView;
import com.hscompany.hstalk.Navigator.DrawerItem;
import com.hscompany.hstalk.Navigator.DrawerListAdapter;
import com.hscompany.hstalk.TabFragments.SectionsFragment1;
import com.hscompany.hstalk.TabFragments.SectionsFragment2;
import com.hscompany.hstalk.TabFragments.SectionsFragment3;
import com.hscompany.hstalk.login.LoginActivity;
import com.hscompany.hstalk.login.UserData;
import com.hscompany.hstalk.login.UserLocalStore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    public static final String ServerUrl = "http://54.238.209.107";

    // 네이게이션 드로어
    private DrawerLayout dlDrawer = null;


    // 드로어 구성
    private RelativeLayout drawer = null;
    private ImageView dImage = null;
    private TextView dText1 = null;
    private TextView dText2 = null;
    private HorizontalListView lvNavList;
    private TextView dloginText = null;

    // 툴바 구성
    private ActionBarDrawerToggle dtToggle = null;
    private Toolbar toolbar = null;
    ImageButton toolbar_menuBtn, toolbar_searchBtn;
    TextView toolbar_title;
    ImageButton dEXITBtn;

    // 로그인 정보
    private UserLocalStore userLocalStore =null;
    private UserData userData =null;
    private boolean loggedIn=false;
    private boolean savesex;


    //tab 관련
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int[] tabIcons = {
            R.drawable.myfriend,
            R.drawable.myonetoone,
            R.drawable.myinterest
    }; //tab 사진
    TabLayout tabLayout;

    //이미지
    TextView newchatimage;

    String tmpurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //image처리
        newchatimage = (TextView)findViewById(R.id.newchatimage);

        //탭 관련 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //탭 관련 설정 끝

        //로그인정보 확인
        userLocalStore = new UserLocalStore(this);
        loggedIn=userLocalStore.getUserLoggedIn();
        if(loggedIn)
        {
            userData=userLocalStore.getLoggedInUser();
            savesex=userData.isSex();
        }

        settingnavigation(); //네비게이션 드로어바 세팅
        setupTabIcons(); //tab 아이콘 추가
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        userLocalStore = new UserLocalStore(this);
        loggedIn=userLocalStore.getUserLoggedIn();

        if(loggedIn)
        {
            userData=userLocalStore.getLoggedInUser();
            Log.d("what", "call this?");
            //refresh 해주기
            afternavigation();
            if(savesex != userData.isSex())
            {
                savesex=userData.isSex();

                //afternavigation();
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                tabLayout.setupWithViewPager(mViewPager);
                setupTabIcons(); //tab 아이콘 추가
            }

        } else
        {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    public void afternavigation()
    {
        // 로그인 유무에 따라 정보변경
        // 로그인 되어있을 때
        if(loggedIn)
        {
            dText1.setText("\""+userData.getNickname()+"\""+" 님");//이름
            dText2.setText("총 포인트 : 341점");//포인트
            dloginText.setText("Log out");
            int userNum = userData.getUsernumber();

            if(userData.isHaspicture()==true)
            {
                tmpurl = ServerUrl+"/picture/user/" + userNum + ".jpg";
                Glide.with(this)
                        .load(tmpurl).thumbnail(0.3f)
                        .override(60, 60)
                        .signature(new StringSignature("" + userData.getPicchange()))
                        .transform(new MyPage.CircleTransform(this.getApplicationContext()))
                        .into(dImage);
            }
            else
            {
                if(userData.isSex()==true)
                {
                    tmpurl=ServerUrl+"/picture/user/default_man.jpg";
                } else
                {
                    tmpurl=ServerUrl+"/picture/user/default_woman.jpg";
                }
                Glide.with(this)
                        .load(tmpurl).thumbnail(0.3f)
                        .override(60, 60)
                        .transform(new MyPage.CircleTransform(this.getApplicationContext()))
                        .into(dImage);
            }
            // 1) 환영합니다 xx님 2) 당신의 포인트는 몇포인트입니다.
        }
    }

    public void changetochatting(){tabLayout.getTabAt(1).select();}

    public void setupNewTabIcon(String message)
    {
        newchatimage.setText(message);
        newchatimage.setVisibility(View.VISIBLE);
    }
    public void setupOldTabIcon()
    {
        newchatimage.setVisibility(View.GONE);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    public void settingnavigation()
    {
        //matching 시작
        //툴바 관련
        toolbar = (Toolbar) findViewById(R.id.toolbar); // 툴바
        TextView toolbar_title = (TextView) findViewById(R.id.toolBar_title); //툴바 타이틀
        ImageButton toolbar_menuBtn = (ImageButton) findViewById(R.id.toolBar_menuBtn); //네비게이션 메뉴
        ImageButton toolbar_searchBtn = (ImageButton) findViewById(R.id.toolBar_searchBtn); //검색 메뉴

        //네비게이션 드로어 관련
        drawer = (RelativeLayout) findViewById(R.id.drawer);

        // 사진
        dImage = (ImageView) findViewById(R.id.drawerImage); //회원사진
        dText1 = (TextView) findViewById(R.id.drawerText); //환영합니다
        dText2 = (TextView) findViewById(R.id.drawerText2); //로그인하세요
        ImageButton dEXITBtn = (ImageButton) findViewById(R.id.drawer_closeBtn);//드로어 닫기
        dloginText = (TextView) findViewById(R.id.drawer_loginText); //로그인하기 버튼

        // 전체 레이아웃
        dlDrawer = (DrawerLayout) findViewById(R.id.main_layer);
        //matching 끝


        //툴바 상단표시내용
        toolbar_title.setText("Hs Talk"); //상단 바에 표시될 내용

        //네비게이션 메뉴 클릭리스너
        toolbar_menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlDrawer.openDrawer(Gravity.LEFT);
            }
        });
        setSupportActionBar(toolbar);

        //검색 메뉴 클릭리스너
        toolbar_searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(myIntent);
                //추가0924
                //finish();
            }
        });

        //네비게이션 메뉴 닫기 리스너
        dEXITBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlDrawer.closeDrawer(Gravity.LEFT);
            }
        });

        // 내 정보, 내 포인트, 내 쿠폰 리스트 뷰
        ArrayList<DrawerItem> navItems = new ArrayList<DrawerItem>();
        DrawerItem drawerItem;

        drawerItem = new DrawerItem("내 정보", R.drawable.setting_icon);
        navItems.add(drawerItem);
        drawerItem = new DrawerItem("내 포인트", R.drawable.point_icon);
        navItems.add(drawerItem);
        drawerItem = new DrawerItem("내 쿠폰", R.drawable.coupon_icon);
        navItems.add(drawerItem);

        lvNavList = (HorizontalListView) findViewById(R.id.drawerList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, R.layout.draweritem, navItems);
        lvNavList.setAdapter(adapter);

        lvNavList.setOnItemClickListener(new DrawerItemClickListener());

        //로그인정보 유무에 따른 네비게이션 정보 설정
        if(loggedIn)
        {
            dText1.setText("\""+userData.getNickname()+"\""+" 님");//이름
            dText2.setText("총 포인트 : 341점");//포인트
            dloginText.setText("Log out");

            int userNum = userData.getUsernumber();

            if(userData.isHaspicture()==true)
            {
                tmpurl = ServerUrl+"/picture/user/" + userNum + ".jpg";
                Glide.with(this)
                        .load(tmpurl).thumbnail(0.3f)
                        .override(60, 60)
                        .signature(new StringSignature("" + userData.getPicchange()))
                        .transform(new MyPage.CircleTransform(this.getApplicationContext()))
                        .into(dImage);
            }
            else
            {
                if(userData.isSex()==true)
                {
                    tmpurl=ServerUrl+"/picture/user/default_man.jpg";
                } else
                {
                    tmpurl=ServerUrl+"/picture/user/default_woman.jpg";
                }
                Glide.with(this)
                        .load(tmpurl).thumbnail(0.3f)
                        .override(60, 60)
                        .transform(new MyPage.CircleTransform(this.getApplicationContext()))
                        .into(dImage);
            }
            // 1) 환영합니다 xx님 2) 당신의 포인트는 몇포인트입니다.
        }

        //네비게이션 작동시키기
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, toolbar, R.string.app_name, R.string.app_name);
        dtToggle.setDrawerIndicatorEnabled(false);
        dlDrawer.setDrawerListener(dtToggle);
        dtToggle.syncState();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    return SectionsFragment1.newInstance(position + 1);
                case 1:
                    return SectionsFragment2.newInstance(position + 1);
                case 2:
                    return SectionsFragment3.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "이성 친구";
                case 1:
                    return "1:1 채팅";
                case 2:
                    return "관심사 채팅";
            }
            return null;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return super.onCreateThumbnail(outBitmap, canvas);
    }


    class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position,
                                long id) {
            switch (position) {
                case 0:
                    dlDrawer.closeDrawer(drawer);
                    Intent myIntent = new Intent(getApplicationContext(), MyPage.class);
                    startActivity(myIntent);

                    onPause();
                    break;
                case 1:
                    dlDrawer.closeDrawer(drawer);
                    Intent myIntent2 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(myIntent2);

                    onPause();
                    break;
                case 2:
                    dlDrawer.closeDrawer(drawer);
                    Intent myIntent3 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(myIntent3);

                    onPause();
                    break;
            }
        }
    }

    public void loginListener (View v) {
        // 로그인
        //  if (dloginText.getText().toString().equals("Log in")) {
        if(!loggedIn) //로그인되있는경우
        {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        }
        // 로그아웃
        else
        {
            userLocalStore.clearUserData();
            userData=null;
            loggedIn=false;
            dlDrawer.closeDrawer(drawer);


            dText1.setText("환영합니다!");//이름
            dText2.setText("로그인 하세요.");//포인트
            dloginText.setText("Log in");
            dImage.setImageResource(R.drawable.profile);
            Toast.makeText(getLayoutInflater().getContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();



            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);
            finish();

        }
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("HsTalk".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}