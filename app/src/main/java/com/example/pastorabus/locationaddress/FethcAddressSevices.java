package com.example.pastorabus.locationaddress;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FethcAddressSevices extends IntentService {
protected ResultReceiver receiver;

    public FethcAddressSevices() {
        super("fetchAddressService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) return;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(Constant.LOCATION_DATA_EXTRA);
        receiver = intent.getParcelableExtra(Constant.RECEIVER);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

        }catch (IOException e){
            Log.e("teste", "Serviço indisponivel");
        }catch (IllegalArgumentException e){
            Log.e("Teste", "Latitude o longetude errada", e);
        }

        if(addresses == null || addresses.isEmpty()){
            Log.e("teste", "Nenhum endereço encontrado");
            deliverResultToReceiver(Constant.FAILURE_RESULT, "nenhum endereço encontrado");
        }else{
            Address address = addresses.get(0);
            List<String> addressF = new ArrayList<>();

            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                addressF.add(address.getAddressLine(i));
            }
            deliverResultToReceiver(Constant.SUCCESS_RESULT,
                    TextUtils.join("|", addressF));
        }

    }

    private void deliverResultToReceiver(int resultCode, String message){
        Bundle bundle = new Bundle();
        bundle.putString(Constant.RESULT_DATA_KEY, message);
        receiver.send(resultCode, bundle);
    }
}
