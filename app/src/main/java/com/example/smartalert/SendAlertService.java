package com.example.smartalert;

import static com.example.smartalert.MainActivity.currentFirebaseUser;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartalert.dto.LocationDto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SendAlertService {
    private GpsTracker gpsTracker;
    double latitude, longitude;

    public LocationDto sendAlert(Activity mainAct) {
        try {
            if (ContextCompat.checkSelfPermission(mainAct.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mainAct, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        gpsTracker = new GpsTracker(mainAct.getApplicationContext());

        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            final DatabaseReference myRef = database.getReference(currentFirebaseUser.getUid());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            String ts = simpleDateFormat.format(new Date());
            myRef.child(ts).push();
            String key = myRef.child(ts).getKey();
            myRef.child(key).child("alarm").setValue("fire");
            myRef.child(key).child("longtitude").setValue(longitude);
            myRef.child(key).child("latitude").setValue(latitude);
        }
        return new LocationDto(latitude, longitude);
    }
}
