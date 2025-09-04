package com.example.smartalert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Timer extends AppCompatActivity {

    TextView textView;

    private Button btnAbort;

    private SoundPool soundPool;
    private int sound1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_ALARM,0);
        }

        sound1 = soundPool.load(this,R.raw.sound1,1);
        setContentView(R.layout.activity_timer);

        textView = findViewById(R.id.text_view);

        btnAbort = (Button) findViewById(R.id.btn_abort);

        btnAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.release();

                saveFalseAlert("FALL-false");
                Intent intent = new Intent(Timer.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        long duration = TimeUnit.SECONDS.toMillis(30);

        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                soundPool.play(sound1,1,1,0,-1,1);
                String sDuration = String.format(Locale.ENGLISH,"%02d"+" sec",
                        TimeUnit.MILLISECONDS.toSeconds(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toSeconds(l)) );

                textView.setText(sDuration);

            }

            @Override
            public void onFinish() {
                textView.setVisibility(View.GONE);
                soundPool.release();

                sendMessageSOS();

            }
        }.start();

    }

    private void sendMessageSOS(){


        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);

        String  phoneNo1  =  "tel:"+sharedpreferences.getString(MainActivity.Phone1, "");
        String phoneNo2 =  "tel:"+sharedpreferences.getString(MainActivity.Phone2, "");

       String message = "SOS";
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> msgArray = smsManager.divideMessage(message);

        saveFalseAlert("FALL");



        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MainActivity.MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        else{
            Log.d("Sending SMS", "YES");
            if(!phoneNo1.trim().equals("tel:")  && phoneNo1!=null && !phoneNo1.isEmpty() && phoneNo1!="tel:") {
                smsManager.sendMultipartTextMessage(phoneNo1, null, msgArray, null, null);
                Toast.makeText(getApplicationContext(), "SMS Fire alert has sent to " + phoneNo1,
                        Toast.LENGTH_LONG).show();
            }
            Log.d("Sending SMS", phoneNo2 +":vdfv");
            if(!phoneNo2.trim().equals("tel:") && phoneNo2!=null && !phoneNo2.isEmpty() && phoneNo2!="tel:") {
                smsManager.sendMultipartTextMessage(phoneNo2, null, msgArray, null, null);
                Toast.makeText(getApplicationContext(), "SMS Fire alert has sent to " + phoneNo2,
                        Toast.LENGTH_LONG).show();
            }

        }


    }

    public static void saveFalseAlert(String setValue){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentFirebaseUser = MainActivity.currentFirebaseUser;
        String userid=currentFirebaseUser.getUid();
        final DatabaseReference myRef = database.getReference(userid);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String ts = simpleDateFormat.format(new Date());
        myRef.child(ts).push();
        String key = myRef.child(ts).getKey();
        myRef.child(key).child("alarm").setValue(setValue);
    }


}
