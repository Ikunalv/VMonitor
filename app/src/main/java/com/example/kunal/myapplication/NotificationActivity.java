package com.example.kunal.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Kunal on 15-03-2018.
 */

public class NotificationActivity extends AppCompatActivity {
    double speed;
    private ListView listView;
    DatabaseReference myref;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mlaLayoutManager;
    RecyclerView.Adapter mAdapter;
    private ArrayList<String> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationactivity);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        myref= FirebaseDatabase.getInstance().getReference("Notifications");

        mlaLayoutManager=new LinearLayoutManager(this);
        myref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String string =dataSnapshot.getValue(String.class);
                arrayList.add(string);

                mAdapter=new MainAdapter(arrayList);
                recyclerView.setLayoutManager(mlaLayoutManager);
                recyclerView.setAdapter(mAdapter) ;

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
