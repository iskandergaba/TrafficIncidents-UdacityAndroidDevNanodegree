package com.gaba.alex.trafficincidents.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class IncidentsAuthenticatorService extends Service {
    private IncidentsAuthenticatorStub mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new IncidentsAuthenticatorStub(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}