package com.example.motoprimemusic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView list;
    String[] item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        list=findViewById(R.id.list);


        runtimePermission();


    }


    public void runtimePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                     displaysong();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findsong(File file) {
        ArrayList arrayList = new ArrayList();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singlefile : files) {
                if (singlefile.isDirectory() && !singlefile.isHidden()) {
                    arrayList.addAll(findsong(singlefile));
                } else {
                    if (singlefile.getName().endsWith(".mp4")) {
                        arrayList.add(singlefile);
                    } else if (singlefile.getName().endsWith(".mp3")) {
                        arrayList.add(singlefile);
                    }
                }
            }
        }
        return arrayList;
    }

    void displaysong(){
        final ArrayList<File> mysongs= findsong(Environment.getExternalStorageDirectory());
        item= new String[mysongs.size()];

        for(int i=0; i< mysongs.size();i++)
        {
            item[i]=mysongs.get(i).getName().toString().replace(".mp3","").replace(".mp4","");

        }

        /*ArrayAdapter<String> myAdapter= new ArrayAdapter<String >(this,android.R.layout.simple_list_item_1,item);
        list.setAdapter(myAdapter);*/
        customAdapter customAdapter=new customAdapter();
        list.setAdapter(customAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                String songName=(String) list.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                .putExtra("songs",mysongs)
                .putExtra("songname",songName)
                .putExtra("pos",i));
            }
        });



    }


    class customAdapter extends BaseAdapter
    {


        @Override
        public int getCount() {
            return  item.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
          View myview=getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textsong=myview.findViewById(R.id.songName);
            textsong.setSelected(true);
            textsong.setText(item[i]);

            return myview;
        }
    }

}

