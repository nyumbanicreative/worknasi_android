package worknasi.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import worknasi.preference.FcmDeviceKey;

/**
 * Created by user on 1/24/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    FcmDeviceKey fcmDeviceKey;

    //this method will be called
    //when the token is generated
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        //now we will have the token
        String token = FirebaseInstanceId.getInstance().getToken();
        fcmDeviceKey = new FcmDeviceKey(getApplicationContext());
        fcmDeviceKey.setKeyFcmdevicekey(token);

        //for now we are displaying the token in the log
        //copy it as this method is called only when the new token is generated
        //and usually new token is only generated when the app is reinstalled or the data is cleared
        Log.d("MyRefreshedToken", token);
    }
}