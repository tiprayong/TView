package com.comtip.tview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by TipRayong on 16/9/2559.
 * Config Channel  Add,Edit,Delete
 */
public class ConfigChannel extends AppCompatActivity {
    Button addBT,saveBT;
    ListView channelList;

    ArrayList<String> channel = new ArrayList<>();
    ArrayList<String> channelUrl = new ArrayList<>();
    int indexChannel = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channellayout);

        // รับข้อมูลจาก MainActivity
        Bundle bundle = getIntent().getExtras();
        channel = bundle.getStringArrayList("channel");
        channelUrl = bundle.getStringArrayList("channelUrl");
        indexChannel = bundle.getInt("indexChannel");

        setupWidgets();
    }

    public void setupWidgets () {

         //ปุ่ม Add
        addBT = (Button) findViewById(R.id.addBT);
        addBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChannel();
            }
        });

        //ปุ่ม Save
        saveBT = (Button) findViewById(R.id.saveBT);
        saveBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putStringArrayListExtra("channel",channel);
                intent.putStringArrayListExtra("channelUrl",channelUrl);
                intent.putExtra("indexChannel",indexChannel);
                setResult(77,intent);
                finish();
            }
        });

         // List แสดงรายชื่อช่องทีวี
        channelList = (ListView) findViewById(R.id.channelList);
        setupChannelLIst();
    }

    public  void setupChannelLIst () {
        CustomList adapter = new CustomList(ConfigChannel.this,channel);
        channelList.setAdapter(adapter);

        // เรียกโหมด Edit
        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    editChannel(channel.get(position), channelUrl.get(position), position);
                }  else {
                    Toast.makeText(ConfigChannel.this, "  Created By TipRayong  ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // กดแช่จะเปลี่ยนเป็นโหมด Delete
        channelList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                 deleteChannel(channel.get(position),position);
                return false;
            }
        });

    }

    //  Edit Channel
    public void  editChannel (final String ch , final String url , final int index) {

           final Dialog dialogEdit = new Dialog(ConfigChannel.this);
           dialogEdit.setContentView(R.layout.editlayout);
           dialogEdit.setTitle("Edit Channel");
           dialogEdit.setCancelable(false);

           final EditText channelEdit = (EditText) dialogEdit.findViewById(R.id.channelEdit);
           channelEdit.setText(ch);

           final EditText urlEdit  = (EditText) dialogEdit.findViewById(R.id.urlEdit);
           urlEdit.setText(url);

           Button  cancelBT = (Button) dialogEdit.findViewById(R.id.cancelBT);
           cancelBT.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   dialogEdit.dismiss();
               }
           });

           Button  changeBT = (Button) dialogEdit.findViewById(R.id.changeBT);
           changeBT.setText("Change");
           changeBT.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   //ข้อมูลห้ามว่างเปล่า
                   if(!channelEdit.getText().toString().isEmpty()  && !urlEdit.getText().toString().isEmpty()){
                       channel.set(index,channelEdit.getText().toString());
                       channelUrl.set(index,urlEdit.getText().toString());
                       dialogEdit.dismiss();
                       setupChannelLIst();
                   }  else {
                       Toast.makeText(ConfigChannel.this, "Please fill Data", Toast.LENGTH_SHORT).show();
                   }
               }
           });

          dialogEdit.show();
    }

    //  Add New Channel

    public void  addChannel () {

        final Dialog dialogEdit = new Dialog(ConfigChannel.this);
        dialogEdit.setContentView(R.layout.editlayout);
        dialogEdit.setTitle("Add New Channel");
        dialogEdit.setCancelable(false);

        final EditText channelEdit = (EditText) dialogEdit.findViewById(R.id.channelEdit);
        channelEdit.setHint("Name Channel");

        final EditText urlEdit  = (EditText) dialogEdit.findViewById(R.id.urlEdit);
        urlEdit.setHint("URL Channel");

        Button  cancelBT = (Button) dialogEdit.findViewById(R.id.cancelBT);
        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEdit.dismiss();
            }
        });

        Button  changeBT = (Button) dialogEdit.findViewById(R.id.changeBT);
        changeBT.setText("Add");
        changeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!channelEdit.getText().toString().isEmpty()  && !urlEdit.getText().toString().isEmpty()){
                    channel.add(indexChannel,channelEdit.getText().toString());
                    channelUrl.add(indexChannel,urlEdit.getText().toString());
                    indexChannel = indexChannel + 1;
                    dialogEdit.dismiss();
                    setupChannelLIst();
                }  else {
                    Toast.makeText(ConfigChannel.this, "Please fill Data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogEdit.show();
    }

    //Delete Channel

    public  void deleteChannel (String ch, final int position) {
        AlertDialog.Builder alertDelete = new AlertDialog.Builder(ConfigChannel.this);
        alertDelete.setTitle("Delete "+ch+" ?");
        alertDelete.setPositiveButton("✔ Yes ✔", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                channel.remove(position);
                channelUrl.remove(position);
                indexChannel = indexChannel - 1;
                setupChannelLIst();
            }
        });

        alertDelete.setNegativeButton("✘ No ✘", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //no Action
            }
        });

        AlertDialog alertD = alertDelete.create();
        alertD.show();

    }

}
