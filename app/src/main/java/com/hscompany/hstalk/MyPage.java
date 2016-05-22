package com.hscompany.hstalk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.signature.StringSignature;
import com.devsmart.android.ui.HorizontalListView;
import com.hscompany.hstalk.Navigator.DrawerItem;
import com.hscompany.hstalk.Navigator.DrawerListAdapter;
import com.hscompany.hstalk.login.LoginActivity;
import com.hscompany.hstalk.login.UserData;
import com.hscompany.hstalk.login.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MyPage extends AppCompatActivity {
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
/////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String ServerUrl = "http://54.238.209.107";


    //사진관련
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    String picPath = null; //내 폰내에서 불러올 사진 주소
    Uri mImageCaptureUri;
    int fileNum;

    // 로그인 정보
    private UserLocalStore userLocalStore =null;
    private UserData userData =null;
    private boolean loggedIn=false;


    // MypagE resource
    ImageButton etProfilePic;
    ImageButton mypage_ProfileEditBtn;
    ImageButton bEditUserInfo;
    EditText etNickName, etPassword;
    Bitmap pic;
    RadioGroup etSex;
    RadioButton etMan, etWoman;
    int tmpsex;

    String tmpurl;
    ProgressBar progressCircle;
    String modifyresult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        progressCircle = (ProgressBar) findViewById(R.id.progresscircle);
        progressCircle.setVisibility(View.VISIBLE);



        settingLoginInfo(); //로그인정보 세팅
        settingnavigation(); //네비게이션 드로어바 세팅

        if (authenticate() == true) //만약 로그인되어있다면 로그인된정보를 보여주고
        {
            displayUserDetails();
        }
        else
        {
            Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        afternavigation();
    }

    public void settingLoginInfo()
    {
        //matching 시작
        etNickName = (EditText) findViewById(R.id.etNickName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        //etSex = (EditText) findViewById(R.id.etSex);

        bEditUserInfo = (ImageButton) findViewById(R.id.bEditUserInfo); //회원정보 수정버튼
        mypage_ProfileEditBtn = (ImageButton)findViewById(R.id.mypage_ProfileEditBtn); //회원사진 수정
        etProfilePic = (ImageButton) findViewById(R.id.etProfilePic); //회원사진 수정
        etSex = (RadioGroup) findViewById(R.id.etSex);
        etMan = (RadioButton) findViewById(R.id.etMan);
        etWoman = (RadioButton) findViewById(R.id.etWoman);
        //matching 끝

        //리스너 시작

        bEditUserInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String password = etPassword.getText().toString();
                String nickName = etNickName.getText().toString();
                //String sex = etSex.getText().toString();

                UserData userData = new UserData(nickName, password, true); //나중에 true 이거 섹스로 바꿔주기

                modifyUser(userData);
            }
        });

        mypage_ProfileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onClickFindPicture(v);
            }
        }); //회원사진 수정

        etSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (etSex.getCheckedRadioButtonId() == R.id.etMan) {
                    tmpsex = 1;
                } else {
                    tmpsex = 0;
                }
            }
        });
        //리스너 끝



        // 로그인 정보
        userLocalStore = new UserLocalStore(this);
        loggedIn=userLocalStore.getUserLoggedIn();
        if(loggedIn) //로그인인 상태면 회원정보를 userData에 저장
        {
            userData=userLocalStore.getLoggedInUser();
            if(userData.isSex()) //남자이면
                etSex.check(etMan.getId());
            else
                etSex.check(etWoman.getId());
        }
    }

    public void afternavigation()
    {

        userLocalStore = new UserLocalStore(this);
        loggedIn=userLocalStore.getUserLoggedIn();
        if(loggedIn)
        {
            userData=userLocalStore.getLoggedInUser();
        }

        if(loggedIn)
        {
            dText1.setText("\""+userData.getNickname()+"\""+" 님");//이름
            dText2.setText("총 포인트 : 341점");//포인트
            dloginText.setText("Log out");

            int userNum = userData.getUsernumber();

            if(userData.isHaspicture()==true)
            {

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
        }
    }


    public void settingnavigation()
    {
        //matching 시작
        //툴바 관련
        toolbar = (Toolbar) findViewById(R.id.toolbar); // 툴바
        toolbar_title = (TextView) findViewById(R.id.toolBar_title); //툴바 타이틀
        toolbar_menuBtn = (ImageButton) findViewById(R.id.toolBar_menuBtn); //네비게이션 메뉴
        toolbar_searchBtn = (ImageButton) findViewById(R.id.toolBar_searchBtn); //검색 메뉴

        //네비게이션 드로어 관련
        drawer = (RelativeLayout) findViewById(R.id.drawer);

        // 사진
        dImage = (ImageView) findViewById(R.id.drawerImage); //회원사진
        dText1 = (TextView) findViewById(R.id.drawerText); //환영합니다
        dText2 = (TextView) findViewById(R.id.drawerText2); //로그인하세요
        dEXITBtn = (ImageButton) findViewById(R.id.drawer_closeBtn);//드로어 닫기
        dloginText = (TextView) findViewById(R.id.drawer_loginText); //로그인하기 버튼

        // 전체 레이아웃
        dlDrawer = (DrawerLayout) findViewById(R.id.main_layer);
        //matching 끝


        //툴바 상단표시내용
        toolbar_title.setText("MyPage"); //상단 바에 표시될 내용

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
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
                //추가0924
                finish();
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

            int userNum=userData.getUsernumber();
            if(userData.isHaspicture()==true)
            {

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
            /*
            if(userData.isHaspicture()==true)
            {
                tmpurl = ServerUrl+"/picture/user/" + userNum + ".jpg";
                Glide.with(this)
                        .load(tmpurl).thumbnail(0.3f)
                        .override(60,60)
                        .signature(new StringSignature(""+userData.getPicchange()))
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
                        .override(60,60)
                        .transform(new MyPage.CircleTransform(this.getApplicationContext()))
                        .into(dImage);
            }
            */
            // 1) 환영합니다 xx님 2) 당신의 포인트는 몇포인트입니다.
        }

        //네비게이션 작동시키기
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, toolbar, R.string.app_name, R.string.app_name);
        dtToggle.setDrawerIndicatorEnabled(false);
        dlDrawer.setDrawerListener(dtToggle);
        dtToggle.syncState();
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
                    break;
                case 1:
                    dlDrawer.closeDrawer(drawer);
                    Intent myIntent2 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(myIntent2);

                    finish();
                    break;
                case 2:
                    dlDrawer.closeDrawer(drawer);
                    Intent myIntent3 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(myIntent3);

                    finish();
                    break;
            }
        }
    }

    class KeypadEnterListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            switch (v.getId()) {
                case R.id.mypage_UserName_Text:
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                        if (etNickName.getText().toString().equals(""))
                            etNickName.setText("서버->USERNAME");
                        InputMethodManager inputMethodManager2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager2.hideSoftInputFromWindow(etNickName.getWindowToken(), 0);
                    }
                    break;
                case R.id.mypage_Password_Text:
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                        if (etPassword.getText().toString().equals(""))
                            etPassword.setText("서버->PASSWORD");
                        InputMethodManager inputMethodManager3 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager3.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
                    }
                    break;
            }
            return false;
        }
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }


    private void displayUserDetails() //로그인시 유저정보를 뿌려준다
    {
        UserData userData = userLocalStore.getLoggedInUser();

        int userNum = userData.getUsernumber();
        if(userData.isHaspicture() == true)
        {
            tmpurl = ServerUrl+"/picture/user/" + userNum + ".jpg";
            Glide.with(this)
                    .load(tmpurl).thumbnail(0.3f)
                    .signature(new StringSignature("" + userData.getPicchange()))
                    .transform(new MyPage.CircleTransform(this.getApplicationContext()))
                    .into(etProfilePic);
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
                    .transform(new MyPage.CircleTransform(this.getApplicationContext()))
                    .into(etProfilePic);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressCircle.setVisibility(View.INVISIBLE);
            }
        }, 200);

        // Glide.with(this).load(tmpurl).signature(new StringSignature(UUID.randomUUID().toString())).thumbnail(0.1f).transform(new CircleTransform(getApplicationContext())).into(dImage);
        // 1) 환영합니다 xx님 2) 당신의 포인트는 몇포인트입니다.


        fileNum = userData.getUsernumber();
        etNickName.setText(userData.getNickname());
        etPassword.setText(userData.getPassword());
        //etSex.setText(userData.)
    }

    private void modifyUser(UserData userData) //회원정보 수정요청
    {
        int tmppicchange = userLocalStore.getLoggedInUser().getPicchange();


        if (mImageCaptureUri != null) //사진을 건드린경우에만
        {
            AsyncTask async = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {

                    new Network().sendPhoto(ServerUrl + "/userimage.php", picPath, "" + fileNum);

                    // 파일 전송후에는 임시 파일 삭제
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists()) {
                        f.delete();
                    }
                    return null;
                }
            }.execute();
            String[][] postData = new String[][]{{"usernumber", "" + userLocalStore.getLoggedInUser().getUsernumber()}, {"password", userLocalStore.getLoggedInUser().getPassword()}, {"sex", "" + tmpsex}, {"picchange", "" + (1 + userLocalStore.getLoggedInUser().getPicchange())}};
            modifyresult = new Network().sendDatePost(ServerUrl + "/ModifyUser.php", postData);
            /*
            String[][] postData = new String[][]{{"usernumber", "" + userLocalStore.getLoggedInUser().getUsernumber()}, {"password", userData.getPassword()}, {"sex", "" + tmpsex}, {"picchange", "" + (1 + tmppicchange)}};
            result = new Network().sendDatePost(ServerUrl + "/ModifyUser.php", postData);

             */
        } else {
            String[][] postData = new String[][]{{"usernumber", "" + userLocalStore.getLoggedInUser().getUsernumber()}, {"password", userData.getPassword()}, {"sex", "" + tmpsex}, {"picchange", "" + (tmppicchange)}};
            modifyresult = new Network().sendDatePost(ServerUrl + "/ModifyUser.php", postData);

            if (modifyresult.equals("Fail")) {
                showErrorMessage("update실패");
            }
        }
        try {
            JSONObject jsonObject = new JSONObject(modifyresult);
            JSONArray jsonArr = jsonObject.getJSONArray("user"); //php파일에서 user로 저장한 로그인정보를 가져옴

            jsonObject = jsonArr.getJSONObject(0);

            int userNum = jsonObject.getInt("usernumber");
            String nickName = jsonObject.getString("nickname");
            String password = jsonObject.getString("password");
            boolean sex;
            int tmpSex = jsonObject.getInt("sex");
            boolean hasPicture;
            int tmpHasPicture = jsonObject.getInt("haspicture");
            String userJoinDate = jsonObject.getString("joindate");
            int picChange = jsonObject.getInt("picchange");

            if (tmpSex == 1) {
                sex = true;
            } else
                sex = false;

            if (tmpHasPicture == 1) {
                hasPicture = true;
            } else
                hasPicture = false;


            UserData returnedUser = new UserData(userNum, nickName, password, sex, hasPicture, userJoinDate, picChange);

            userLocalStore.storeUserData(returnedUser); //수정된 새 로그인정보를 저장시킨다
        } catch (Exception e) //수정에 실패한경우
        {
            showErrorMessage("불러오기실패");
        }
        Toast.makeText(getLayoutInflater().getContext(), "회원정보 수정 성공", Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onResume();
            }
        }, 1000);
        //여기수정
    }



    private void showErrorMessage(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
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

            finish();

        }
    }


    //사진 시작
    public Bitmap getCircleBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
        p.setStrokeWidth(3);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
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

                picPath = mImageCaptureUri.getPath();
                pic = BitmapFactory.decodeFile(picPath);
                pic=getCircleBitmap(pic);
                etProfilePic.setImageBitmap(pic);

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



    public static class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override public String getId() {
            return getClass().getName();
        }
    }
}