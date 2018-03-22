package com.galdino.autocompletplaces;

import android.content.Intent;
import android.databinding.DataBindingUtil;
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
            // TODO: Solucionar o erro.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Solucionar o erro.
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
                // TODO: Solucionar o erro.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private void putPlace(Place place)
    {
        LatLng latLng = place.getLatLng();
        mBinding.tvAddress.setText(place.getAddress());
        mBinding.tvLat.setText(String.valueOf(latLng.latitude));
        mBinding.tvLong.setText(String.valueOf(latLng.longitude));
    }
}
