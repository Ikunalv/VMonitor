package com.example.kunal.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Kunal on 15-03-2018.
 */

public class ControlsActivity extends AppCompatActivity {
    private static final String TAG = "ControlsActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.controlsactivity);
    }
    public void isHorn(View view)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("horn");

        for(int i=0;i<2000;i++)
        {
            ref.setValue("ON");
        }
        ref.setValue("OFF");

    }

    boolean lck=true;
    String l;
    public void lock(View view)
    {
        ImageView iv=findViewById(R.id.lck1);
        if(lck) {
            iv.setImageResource(R.drawable.ic_lock_black_24dp);
            lck=false;
        }
        else{
            iv.setImageResource(R.drawable.ic_lock_open_black_24dp);
            lck=true;
        }
        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("light");
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("lock");
        boolean isLocked=((ToggleButton)view).isChecked();

        if(isLocked)
        {

            ref.setValue("LOCKED");
            ref1.setValue("OFF");
        }
        else
        {
            ref.setValue("UNLOCKED");


        }
    }
    boolean lgt=true;
    String lock;
    public void light(View view)
    {
        ImageView iv=findViewById(R.id.lgt1);
        if(lck) {
            iv.setImageResource(R.drawable.ic_flash_off_black_24dp);
            lck=false;
        }
        else{
            iv.setImageResource(R.drawable.ic_flash_on_black_24dp);
            lck=true;
        }
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("light");
        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("lock");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lock=(String)dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        boolean isOn=((ToggleButton)view).isChecked();
        if(isOn)
        {
            ref.setValue("OFF");

        }
        else
        {
            Log.d(TAG, "light: "+lock);
            if(lock.equals("LOCKED")) {
                Toast.makeText(this, "First unlock the vehicle", Toast.LENGTH_SHORT).show();

            }
            else
            {
                ref.setValue("ON");
            }
        }

    }
}
