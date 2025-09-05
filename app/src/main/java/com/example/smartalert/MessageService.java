package com.example.smartalert;


import static com.example.smartalert.MainActivity.MY_PERMISSIONS_REQUEST_SEND_SMS;
import static com.example.smartalert.MainActivity.MY_PREFERENCES;
import static com.example.smartalert.MainActivity.PHONE_1_KEY;
import static com.example.smartalert.MainActivity.PHONE_2_KEY;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MessageService  {
    SmsManager smsManager;
    ArrayList<String> msgArray;
    SharedPreferences sharedpreferences;


    public void sendSMS(String message, Activity mainActivity) {

        sharedpreferences = mainActivity.getSharedPreferences(MY_PREFERENCES,
                Context.MODE_PRIVATE);

       String phoneNo1  =  "tel:"+sharedpreferences.getString(PHONE_1_KEY, "");
       String phoneNo2 =  "tel:"+sharedpreferences.getString(PHONE_2_KEY, "");


        smsManager = SmsManager.getDefault();
        msgArray = smsManager.divideMessage(message);
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                    Manifest.permission.SEND_SMS)) {

            } else {

                ActivityCompat.requestPermissions(mainActivity,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            Log.d("Sending SMS", "YES");
            if (!phoneNo1.trim().equals("tel:") && phoneNo1 != null && !phoneNo1.isEmpty() && phoneNo1 != "tel:") {
                smsManager.sendMultipartTextMessage(phoneNo1, null, msgArray, null, null);
                Toast.makeText(mainActivity.getApplicationContext(), "SMS Fire alert has sent to " + phoneNo1,
                        Toast.LENGTH_LONG).show();
            }
            Log.d("Sending SMS", phoneNo2 + ":vdfv");
            if (!phoneNo2.trim().equals("tel:") && phoneNo2 != null && !phoneNo2.isEmpty() && phoneNo2 != "tel:") {
                smsManager.sendMultipartTextMessage(phoneNo2, null, msgArray, null, null);
                Toast.makeText(mainActivity.getApplicationContext(), "SMS Fire alert has sent to " + phoneNo2,
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}
