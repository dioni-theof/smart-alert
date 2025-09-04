package com.example.smartalert;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AlarmList extends AppCompatActivity {

    private Button btn_back;
    private RecyclerView recyclerView;
    ArrayList<String> list = new ArrayList<>();
    MyRecyclerViewAdapter adapter;
    FirebaseUser currentFirebaseUser = MainActivity.currentFirebaseUser;
    public final String userid=currentFirebaseUser.getUid();
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        Intent intent = getIntent();



        recyclerView = findViewById(R.id.alarmList);

        btn_back = (Button) findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmList.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        printDatabaseList();
    }
    public void printDatabaseList() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(userid);


        final String[] value = {""};
        final Map<String, Object>[] td = new Map[0];
        final ArrayList<String> lst = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;

                // Log.d("Value is: " , String.valueOf(value));
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v("Value key is: ",""+ childDataSnapshot.getKey()); //displays the key for the node
                    // Log.v("Value is: ",""+ childDataSnapshot.getValue(String.class));   //gives the value for given keyname

                    //lst.add("Your over speed limit on date/hour: "+  childDataSnapshot.getKey() );
                    String val="On date/hour: "+  childDataSnapshot.getKey()+" " ;

                    for (DataSnapshot childDataSnapshots : childDataSnapshot.getChildren()) {

                        i++;
                        try {
                            value[0] = (String) childDataSnapshots.getValue();
                            val = val + ", " + childDataSnapshots.getKey() + ":" + value[0] + "";
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    if(!val.contains("@")) {
                        lst.add(val);
                    }

                }
                Log.v("Value new is: ",""+ lst);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setHasFixedSize(true);
                if(   value[0]==""){
                    // notfound.setText("There is not such movie in your list");
                }
                else{

                    adapter = new MyRecyclerViewAdapter(getApplicationContext(),lst);
                    //  adapter.setClickListener((MyRecyclerViewAdapter.ItemClickListener)this );
                    recyclerView.setAdapter(adapter);
                    // notfound.setText("");

                }
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}
