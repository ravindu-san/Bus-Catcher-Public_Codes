package com.example.buscatcher;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class PassengerMapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;//Google map

    String id = null;//store id
    String routeNo = null;//store route number

    HashMap<String, Marker> hashMap = new HashMap<>();//store map markers

    private Marker busLocationMarker;


    //float bearing = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_maps);

        //get route no from user
        routeNo = getIntent().getStringExtra("route_number");
        // Obtain MapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //route markers
        DatabaseReference cityLocationRef = FirebaseDatabase.getInstance().getReference("Users").child("Buses").child(routeNo).child("route_Details");
        cityLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double city_1_lat;//latitude of city 1
                double city_1_long;//longitude of city 1
                double city_2_lat;//latitude of city 2
                double city_2_long;//longitude of city 2

                if(dataSnapshot.exists()){

                    city_1_lat = dataSnapshot.child("city_1_lat").getValue(Double.class);
                    city_1_long = dataSnapshot.child("city_1_long").getValue(Double.class);
                    city_2_lat = dataSnapshot.child("city_2_lat").getValue(Double.class);
                    city_2_long = dataSnapshot.child("city_2_long").getValue(Double.class);

                    LatLng city_1 = new LatLng(city_1_lat,city_1_long);
                    LatLng city_2 =new LatLng(city_2_lat,city_2_long);

                    //store markers
                    Marker startMarker = mMap.addMarker(new MarkerOptions().position(city_1));
                    Marker endMarker = mMap.addMarker(new MarkerOptions().position(city_2));

                    //calculate the map boundaries
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(startMarker.getPosition());
                    builder.include(endMarker.getPosition());

                    LatLngBounds bounds = builder.build();

                    int padding = 0;
                    final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);
                    mMap.animateCamera(cu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Update bus marker
        DatabaseReference busLocationRef = FirebaseDatabase.getInstance().getReference("Users").child("Buses").child(routeNo);
        busLocationRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            //update database change or update
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()) {

                    List<Object> map = (List<Object>) dataSnapshot.child("l").getValue();//put snapshot values in a list

                    double locationLat = 0;
                    double locationLong = 0;

                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());//get latitude
                    }

                    if (map.get(1) != null) {
                        locationLong = Double.parseDouble(map.get(1).toString());//get longitude
                    }

                    id = dataSnapshot.getKey();//get key

                    //if already existing marker
                    if(hashMap.containsKey(id)){
                        Marker marker = hashMap.get(id);

                        LatLng startPosition = marker.getPosition();

                        LatLng newPos = new LatLng(locationLat, locationLong);

                        marker.setPosition(newPos);
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setRotation(getBearing(startPosition,newPos));

                    }
                    //if marker is new
                    else{

                        busLocationMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(locationLat, locationLong))
                                .title("route 01").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus))
                                .anchor(0.5f, 0.5f)

                        );

                        hashMap.put(id, busLocationMarker);

                    }

                }

            }

            //remove marker when bus logged out
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                id = dataSnapshot.getKey();

                Marker marker = hashMap.get(id);
                if (marker!= null) {
                    marker.remove();
                    hashMap.remove(id);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //smooth bus marker move
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PassengerMapsActivity.this, NavDrawerActivity.class);
        startActivity(intent);
        finish();

    }
}



