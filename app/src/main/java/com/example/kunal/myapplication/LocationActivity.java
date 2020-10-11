package com.example.kunal.myapplication;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

/**
 * Created by Kunal on 15-03-2018.
 */

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "LocationActivity";
    private GoogleMap mMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationactivity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

    }
    public String lat = "0", lng = "0";
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                //Map <String, Double> map = dataSnapshot.getValue();
                int i = 0;
                for(DataSnapshot snap : dataSnapshot.child("location").getChildren()) {
                    if(i == 0)
                        lat =  snap.getValue().toString();
                    else
                        lng =  snap.getValue().toString();
                    i++;
                }

                //lng=map.get("lon");
                Log.d(TAG, "Lat: "+lat);
                Log.d(TAG, "Long: "+lng);
mMap.clear();
                double x=Double.parseDouble(lat), y=Double.parseDouble(lng) ;
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(x,y);
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);

                mMap.addMarker(new MarkerOptions().position(sydney).title("TVS WEGO").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(sydney)      // Sets the center of the map to Mountain View
                        .zoom(18)                   // Sets the zoom
                        // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
}
