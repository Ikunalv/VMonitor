package com.example.kunal.myapplication;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    DatabaseReference mref= FirebaseDatabase.getInstance().getReference("speed");
    DatabaseReference notif=FirebaseDatabase.getInstance().getReference("Notifications");
    public String lat = "0", lng = "0";
    private FirebaseAuth auth;
    double speed;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final ImageView lightstate=findViewById(R.id.lightstate);
        final ImageView lockstate=findViewById(R.id.lockstate);
        final TextView lightstatetext=findViewById(R.id.lightstatetext);
        final TextView lockstatetext=findViewById(R.id.lockstatetext);
        final DatabaseReference lockState= FirebaseDatabase.getInstance().getReference("lock");

        lockState.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String dblock=(String)dataSnapshot.getValue();
                if(dblock.equals("LOCKED"))
                {
                    lockstate.setImageResource(R.drawable.ic_lock_black_24dp);
                    lockstatetext.setText("LOCKED");
                }
                else
                {
                    lockstate.setImageResource(R.drawable.ic_lock_open_black_24dp);
                    lockstatetext.setText("UNLOCKED");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference lights=FirebaseDatabase.getInstance().getReference("light");
        lights.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String lgt=(String)dataSnapshot.getValue();
            if(lgt.equals("ON"))
            {
                lightstate.setImageResource(R.drawable.ic_flash_on_black_24dp);
                lightstatetext.setText("LIGHTS ON");
            }
            else
            {
                lightstate.setImageResource(R.drawable.ic_flash_off_black_24dp);
                lightstatetext.setText("LIGHTS OFF");
            }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot snap:dataSnapshot.child("location").getChildren())
                {
                    if(i == 0)
                        lat =  snap.getValue().toString();
                    else
                        lng =  snap.getValue().toString();
                    i++;
                }
                Log.d(TAG, "Lat: "+lat);
                Log.d(TAG, "Long: "+lng);
                double x=Double.parseDouble(lat), y=Double.parseDouble(lng) ;
                TextView add=findViewById(R.id.addr);

                String addr;
                addr=getCompleteAddressString(x,y);
                add.setText(addr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                speed=(double)dataSnapshot.getValue();
                if(speed>60.0)
                {
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = tsLong.toString();
                    calendar= Calendar.getInstance();
                    simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    date=simpleDateFormat.format(calendar.getTime());
                    sendNotifications(date);
                    notif.push().setValue("Speed was "+speed+" at "+date);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

     DatabaseReference tref=FirebaseDatabase.getInstance().getReference("RFID_Access");
     tref.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             String value=(String)dataSnapshot.getValue();
             if(value.equals("DENIED"))
             {

                 sendTheftNotifications();
                 notif.push().setValue("Security of the vehicle was compromised on "+date);
             }

         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });

    }

public void location(View view)
{
    Intent intent=new Intent(MainActivity.this,LocationActivity.class);
    startActivity(intent);
}
public void controls(View view)
{
    Intent intent= new Intent(MainActivity.this,ControlsActivity.class);
    startActivity(intent);
}
public void documents(View view)
{
    Intent intent=new Intent(MainActivity.this,ImagesActivity.class);
    startActivity(intent);
}
public void notifications(View view)
{
    Intent intent=new Intent(MainActivity.this, NotificationActivity.class);
    startActivity(intent);
}
    public void sendNotifications(String date)
    {
        NotificationCompat.Builder mBuilder;
        mBuilder=new NotificationCompat.Builder(this).setSmallIcon(R.drawable.logo).setContentTitle("Speed Alert").setContentText("Vehicle has exceeded the speed limit on "+date);

        NotificationManager nmangr=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nmangr.notify(001,mBuilder.build());

    }
    public void sendTheftNotifications()
    {
        NotificationCompat.Builder mBuilder;
        mBuilder=new NotificationCompat.Builder(this).setSmallIcon(R.drawable.logo).setContentTitle("Theft Alert").setContentText("Please Check Your Vehicle!!!");

        NotificationManager nmangr=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nmangr.notify(001,mBuilder.build());

    }
    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }
    public void settings(View view)
    {
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }
}
