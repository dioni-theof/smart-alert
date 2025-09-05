package com.example.smartalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalert.dto.LocationDto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private Button signOut, btn_save, btn_abort, btnData, btnSelect, btnUpload;
    ;
    static FirebaseUser currentFirebaseUser;
    //= FirebaseAuth.getInstance().getCurrentUser();

    //public String userid=currentFirebaseUser.getUid();

    Spinner spinner;
    Locale myLocale;
    String currentLanguage = "en", currentLang;
    private GpsTracker gpsTracker;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String PHONE_1_KEY = "Phone1key";
    public static final String PHONE_2_KEY = "Phone2key";
    EditText ed_number1, ed_number2;
    SharedPreferences sharedpreferences;
    ArrayList<String> msgArray;
    String phoneNo1, phoneNo2;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    private MessageService messageService;
    private SendAlertService sendAlertService;
    private Accelerometer accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        messageService = new MessageService();
        sendAlertService = new SendAlertService();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        signOut = (Button) findViewById(R.id.btn_singout);
        auth.signOut();

// this listener will be called when there is change in firebase user session
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
        btnSelect = (Button) findViewById(R.id.btnChoose);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Choosephoto.class);
                startActivity(intent);


                finish();

            }
        });
        currentLanguage = getIntent().getStringExtra(currentLang);

        spinner = (Spinner) findViewById(R.id.spinner);

        List<String> list = new ArrayList<String>();

        list.add("Select language");
        list.add("English");
        list.add("Greek");
        list.add("Spanish");
        // list.add("French");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        setLocale("en");
                        break;
                    case 2:
                        setLocale("el");
                        break;
                    case 3:
                        setLocale("es");
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        ed_number1 = (EditText) findViewById(R.id.ed_number1);
        ed_number2 = (EditText) findViewById(R.id.ed_number2);


        btn_save = (Button) findViewById(R.id.btb_save);
        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(PHONE_1_KEY)) {
            ed_number1.setText(sharedpreferences.getString(PHONE_1_KEY, ""));
        }
        if (sharedpreferences.contains(PHONE_2_KEY)) {
            ed_number2.setText(sharedpreferences.getString(PHONE_2_KEY, ""));

        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = ed_number1.getText().toString();
                String ph = ed_number2.getText().toString();


                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(PHONE_1_KEY, n);
                editor.putString(PHONE_2_KEY, ph);
                editor.commit();
                Toast.makeText(MainActivity.this, "Thanks they are saved in sharedPreferences", Toast.LENGTH_LONG).show();
            }
        });
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(
                Color.parseColor("#0F9D58"));
        actionBar.setBackgroundDrawable(colorDrawable);

        accelerometer = new Accelerometer(this);
        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(double tx, double ty, double tz) {

                double loAccelerationReader = Math.sqrt(Math.pow(tx, 2)
                        + Math.pow(ty, 2)
                        + Math.pow(tz, 2));

                if (loAccelerationReader > 0.3d && loAccelerationReader < 0.5d) {

                    startActivity(new Intent(MainActivity.this, Timer.class));

                    finish();
                }

            }
        });


        btn_abort = (Button) findViewById(R.id.btn_abort);

        btn_abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer.saveFalseAlert("FIRE-False");
                messageService.sendSMS("Άκυρος ο συναγερμος. Όλα καλά", MainActivity.this);

            }
        });

        btnData = (Button) findViewById(R.id.btnData);

        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AlarmList.class));
                finish();

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.register();

    }

    @Override
    protected void onPause() {
        super.onPause();

        accelerometer.unregister();
    }

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(MainActivity.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendFireAlert(View view) {
        LocationDto locationDto = sendAlertService.sendAlert(MainActivity.this);
        String message = "Βρίσκομαι στην τοποθεσία με γεωγραφικό μήκος : " + String.valueOf(locationDto.getLatitude()) + " και γεωγραφικό πλάτος :" + String.valueOf(locationDto.getLatitude()) + " και παρατηρώ μια φωτια";
        messageService.sendSMS(message, MainActivity.this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("GPS onrew", "GPS Enabled");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    if (!phoneNo1.trim().equals("tel:") && phoneNo1 != null && !phoneNo1.isEmpty() && phoneNo1 != "tel:") {
                        smsManager.sendMultipartTextMessage(phoneNo1, null, msgArray, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Fire alert has sent to " + phoneNo1,
                                Toast.LENGTH_LONG).show();
                    }
                    Log.d("Sending SMS", phoneNo2 + ":vdfv");
                    if (!phoneNo2.trim().equals("tel:") && phoneNo2 != null && !phoneNo2.isEmpty() && phoneNo2 != "tel:") {
                        smsManager.sendMultipartTextMessage(phoneNo2, null, msgArray, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Fire alert has sent to " + phoneNo2,
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();

                }
            }
        }

    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(/* TODO: provide the application ID. For example: */ getPackageName(), "com.example.smartalert.LoginActivity");
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }
}




