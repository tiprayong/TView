package com.comtip.tview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Default Values
    final String  defaultCH = "Config✎";    
    final String  defaultURL = "Config✎";    

    // ตัวแปรใช้ใน SharedPreferences
    String  ch = "";
    String  chUrl  = "";

    // ตัวแปรหลัก
    ArrayList<String> channel = new ArrayList<>();
    ArrayList<String> channelUrl = new ArrayList<>();
    int indexChannel = 0;

    // Save ข้อมูล ปิดเปิดแอพ
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    //ทีวี
    VideoView tvView;
    ListView menuTV;
    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        loadingSharedPreferences();
        setupWidgets();

    }

      // Save ข้อมูลก่อนปิด App
    @Override
    protected void onPause() {
        super.onPause();

        // แปลงข้อมูล ArrayList ให้อยู่ในรูปแบบ String
        ch = "";
        chUrl ="";
        for(int i = 0; i < channel.size();i++) {
            ch +=  channel.get(i)+"✎";
            chUrl +=  channelUrl.get(i)+"✎";
        }

        editor.putString("ch",ch);
        editor.putString("chUrl",chUrl);
        editor.commit();
    }

    // load ข้อมูลที่เซฟไว้ก่อนปิดแอพ
    public void loadingSharedPreferences () {
        sp = this.getSharedPreferences("Save Mode", Context.MODE_PRIVATE);
        editor = sp.edit();
        ch =  sp.getString("ch",defaultCH);
        chUrl = sp.getString("chUrl",defaultURL);

        //  แปลง String ให้อยู่ในรูปแบบ ArrayList ก่อนนำไปใช้งาน
        if(!ch.isEmpty() && !chUrl.isEmpty()){
            String []  bufferCh =  ch.split("\\✎");
            String []  bufferUrl = chUrl.split("\\✎");

            if (bufferCh.length == bufferUrl.length) {
                indexChannel = bufferCh.length;
                for (int i = 0; i < bufferCh.length; i++) {
                    channel.add(i, bufferCh[i]);
                    channelUrl.add(i,bufferUrl[i]);
                }
            }
        }
    }

    public void setupWidgets () {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        tvView = (VideoView) findViewById(R.id.tvView);
        menuTV = (ListView) findViewById(R.id.menuTV);
        setupDrawer();
        }

    //Drawer Setup

    public void setupDrawer () {
        CustomList adapter = new CustomList(MainActivity.this,channel);
        menuTV.setAdapter(adapter);
        menuTV.setOnItemClickListener(new DrawerItemClickListener());
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            drawerLayout.closeDrawer(menuTV);

            if(position != 0) {
                playTV(channel.get(position), channelUrl.get(position));
            } else {

                // ถ้าวีดีโอกำลังเล่นให้ทำการหยุดทันที
                if(tvView.isPlaying()) {
                    tvView.stopPlayback();
                }

                // เรียกหน้า  Config ขึ้นมาทำงาน

                Intent intent = new Intent(MainActivity.this,ConfigChannel.class);
                intent.putStringArrayListExtra("channel",channel);
                intent.putStringArrayListExtra("channelUrl",channelUrl);
                intent.putExtra("indexChannel",indexChannel);
                startActivityForResult(intent,77);
            }

        }
    }

    // ส่วนเล่นวีดีโอจากทีวี
    public void playTV (String tvCh,String VideoURL){
        final ProgressDialog pDialog;
        //สร้าง  progressbar แสดงการดำเนินงานให้ user รู้
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("กำลังรับสัญญาณจาก "+tvCh);
        pDialog.setMessage("กรุณารอสักครู่...");
        pDialog.setIndeterminate(false);
        // กำหนดให้ cancel ระหว่างติดต่อได้ ดว้ยการกด back
        pDialog.setCancelable(true);
        pDialog.show();

        try {
            //  สร้างส่วน MediaController  กำหนดการเล่นวิดีโอ
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(tvView);
            // รับค่า URL จาก  VideoURL
            Uri video = Uri.parse(VideoURL);
            tvView.setMediaController(mediacontroller);
            tvView.setVideoURI(video);


        } catch (Exception e) {
            pDialog.dismiss();
            e.printStackTrace();
        }

        // เจอ Error ให้หยุด ProgressDialog
        tvView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                pDialog.dismiss();
                return false;
            }
        });

        tvView.requestFocus();

        // ส่วนเล่นวิดีโอ
        if(tvView.isFocused()) {
            tvView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    pDialog.dismiss();
                    tvView.start();
                }
            });
        }
    }

    //  รับข้อมูลกลับจากคลาส ConfigChannel
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if(requestCode == 77) {
             channel = data.getStringArrayListExtra("channel");
             channelUrl = data.getStringArrayListExtra("channelUrl");
             indexChannel = data.getIntExtra("indexChannel",0);
             // ทำการ recreate เพื่อเคลียร์สัญญาณสตรีมมิ่งต่างๆที่ค้างไว้จาก process ครั้งล่าสุด
             recreate();
         }

    }
}
