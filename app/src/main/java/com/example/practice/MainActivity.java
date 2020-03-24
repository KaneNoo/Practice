package com.example.practice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button button;
    ImageView showPicture;
    final int PICK_PICTURE_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phoneGeoHttp();
            }
        });


    }

    public String Recognition(String command) {

        Pattern phoneNumber = Pattern.compile("(\\d{1,3}\\-?)", Pattern.CASE_INSENSITIVE);
        Pattern address = Pattern.compile("^([A-Za-z0-9]{1,63})\\.([a-z]{1,6})$");
        //Простой адрес (google.com, mail.ru) без подадресов
        Pattern geoCoordinates = Pattern.compile("^(\\d{1,3}\\.\\d{1,7})(\\, )((\\d{1,3}\\.\\d{1,7}))$", Pattern.CASE_INSENSITIVE);

        Matcher phoneNumberMatch = phoneNumber.matcher(command);
        Matcher addressMatch = address.matcher(command);
        Matcher geoCoordinatesMatch = geoCoordinates.matcher(command);


        if (addressMatch.find()) {
            return "address";
        }

        if (geoCoordinatesMatch.find()) {
            return "geo";
        }

        if (phoneNumberMatch.find()) {
            return "phone";
        }

        return "undefined";
    }

    public void phoneGeoHttp() {

        EditText commandForm = (EditText) findViewById(R.id.command);
        String command = commandForm.getText().toString();

        String recognized = Recognition(command);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        switch (recognized) {

            case "geo":
                intent.setData(Uri.parse("geo:" + command));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                break;

            case "phone":
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + command));
                startActivity(intent);
                break;

            case "address":
                intent.setData(Uri.parse("https://" + command));
                startActivity(intent);
                break;

            case "undefined":
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Введены непонятные данные, прошу исправить.", Toast.LENGTH_LONG);
                toast.show();
                break;

        }
    }

    public void takePicture(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, PICK_PICTURE_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == PICK_PICTURE_CODE){

            Uri uri = data.getData();

            try {

                showPicture = findViewById(R.id.show_picture);
                showPicture.setImageBitmap(getBitmapFromUri(uri));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException{

        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().
                        openFileDescriptor(uri, "r");

        FileDescriptor fileDescriptor =
                parcelFileDescriptor
                .getFileDescriptor();

        Bitmap image =
                BitmapFactory.decodeFileDescriptor(fileDescriptor);


        parcelFileDescriptor.close();
        return image;
    }

}





