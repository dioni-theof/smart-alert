package com.example.smartalert;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalert.dto.LocationDto;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Timer extends AppCompatActivity {

    TextView textView;

    private Button btnAbort;

    private SoundPool soundPool;
    private int sound1;

    private  MessageService messageService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        messageService = new MessageService();
        messageService = new MessageService();
        final SendAlertService sendAlertService = new SendAlertService();
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
                LocationDto locationDto;
                locationDto = sendAlertService.sendAlert(Timer.this);
                String message = "Βρίσκομαι στην τοποθεσία με γεωγραφικό μήκος : " + String.valueOf(locationDto.getLatitude()) + " και γεωγραφικό πλάτος :" + String.valueOf(locationDto.getLatitude()) + " και κινδυνευω";
                messageService.sendSMS(message,Timer.this);


            }
        }.start();

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
