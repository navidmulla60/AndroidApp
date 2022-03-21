package com.example.opencam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class MainActivity extends AppCompatActivity {
    ImageView cover;
    Button camerabtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cover=findViewById(R.id.imageView);
        camerabtn=findViewById(R.id.button);

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        /*.crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)*/
                        .saveDir(getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                        .start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri =data.getData();
        cover.setImageURI(uri);

    }
}