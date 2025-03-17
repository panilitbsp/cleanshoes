package com.cleanshoes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private final LatLngBounds SURAKARTA_BOUNDS = new LatLngBounds(
            new LatLng(-7.7100, 110.6900),
            new LatLng(-7.4400, 110.9600)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            loadMap();
        }
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        LatLng initialLocation = new LatLng(-7.559575, 110.825089);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15));
        mMap.setLatLngBoundsForCameraTarget(SURAKARTA_BOUNDS);

        EditText searchLocation = findViewById(R.id.search_location);
        searchLocation.setSingleLine(true);
        searchLocation.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchLocation.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String location = searchLocation.getText().toString().trim();
                if (!location.isEmpty()) {
                    LatLng searchedLocation = getLocationFromAddress(location);
                    if (searchedLocation != null && SURAKARTA_BOUNDS.contains(searchedLocation)) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchedLocation, 15));
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(searchedLocation).title(location));
                    } else {
                        Toast.makeText(this, "Lokasi tidak ditemukan atau di luar area Surakarta", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
            return false;
        });

        Button btnConfirmLocation = findViewById(R.id.btn_confirm_location);
        btnConfirmLocation.setOnClickListener(v -> {
            LatLng currentCameraPosition = mMap.getCameraPosition().target;

            // Ambil alamat dari search bar, kalau kosong ambil dari koordinat
            String enteredAddress = searchLocation.getText().toString().trim();
            String finalAddress = enteredAddress.isEmpty() ? getAddressFromLatLng(currentCameraPosition) : enteredAddress;

            // Kirim hasil ke ShippingActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("location", currentCameraPosition.latitude + ", " + currentCameraPosition.longitude);
            resultIntent.putExtra("address", finalAddress);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(strAddress, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Alamat tidak ditemukan";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMap();
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}