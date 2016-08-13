package com.gaba.alex.trafficincidents.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class IncidentsSyncService extends Service {

    private static IncidentsSyncAdapter sSyncAdapter = null;

    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new IncidentsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}