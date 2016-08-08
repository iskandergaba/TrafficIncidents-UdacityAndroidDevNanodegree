package com.gaba.alex.trafficincidents.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class IncidentsAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private IncidentsAuthenticatorStub mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new IncidentsAuthenticatorStub(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}