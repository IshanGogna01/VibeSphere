package com.example.isangeet;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lstview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstview = findViewById(R.id.lstview);

        requestPermissionProperly();
    }

    private void requestPermissionProperly() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // ANDROID 13+

            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_MEDIA_AUDIO)
                    .withListener(permissionListener)
                    .check();

        } else {
            // ANDROID 12 AND BELOW (parents' phone)

            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(permissionListener)
                    .check();
        }
    }

    private final PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {

            ArrayList<File> mysongs =
                    fetchSongs(Environment.getExternalStorageDirectory());

            String[] items = new String[mysongs.size()];

            for (int i = 0; i < mysongs.size(); i++) {
                items[i] = mysongs.get(i).getName().replace(".mp3", "");
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            items);

            lstview.setAdapter(adapter);

            lstview.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(MainActivity.this,PlaySong.class);
                String currentSong = lstview.getItemAtPosition(position).toString();
                intent.putExtra("songlist",mysongs);
                intent.putExtra("currentSong",currentSong);
                intent.putExtra("position",position);
                startActivity(intent);
            });
        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse response) {
            Toast.makeText(MainActivity.this,
                    "Permission required to show songs",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPermissionRationaleShouldBeShown(
                PermissionRequest permission,
                PermissionToken token) {
            token.continuePermissionRequest();
        }
    };

    public ArrayList<File> fetchSongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] songs = file.listFiles();

        if (songs != null) {
            for (File myFile : songs) {
                if (myFile.isDirectory() && !myFile.isHidden()) {
                    arrayList.addAll(fetchSongs(myFile));
                } else if (myFile.getName().endsWith(".mp3")) {
                    arrayList.add(myFile);
                }
            }
        }
        return arrayList;
    }
}
