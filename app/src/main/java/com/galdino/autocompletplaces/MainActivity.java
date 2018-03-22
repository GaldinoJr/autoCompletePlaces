package com.galdino.autocompletplaces;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.galdino.autocompletplaces.databinding.ActivityMainBinding;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    ActivityMainBinding mBinding;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String TAG = "google api search address";
    private LatLngBounds BOUNDS_BRAZIL = new LatLngBounds(
            new LatLng(-23.533773, -46.625290),
            new LatLng(-23.533773, -46.625290));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        loadControls();
    }

    private void loadControls() {
        mBinding.btOpenSearchAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {

        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();


            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .setBoundsBias(BOUNDS_BRAZIL)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
        } catch (GooglePlayServicesNotAvailableException e) {
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                putPlace(place);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private void putPlace(Place place)
    {
        LatLng latLng = place.getLatLng();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addresses != null && addresses.get(0) != null)
            {
                Address address = addresses.get(0);
                String stateName = address.getAdminArea();
                String cityName = address.getLocality();
                String neighborhood = address.getSubLocality();
                String street = address.getThoroughfare();

                mBinding.tvState.setText(stateName);
                mBinding.tvCity.setText(cityName);
                mBinding.tvNeighborhood.setText(neighborhood);
                mBinding.tvAddress.setText(street);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        mBinding.tvLat.setText(String.valueOf(latLng.latitude));
        mBinding.tvLong.setText(String.valueOf(latLng.longitude));
    }
}
