package com.dduunk.ecg.models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dduunk.ecg.ble.peripheral.GattServer;
import com.dduunk.ecg.ble.peripheral.PeripheralService;
import com.dduunk.ecg.utils.SingleLiveEvent;

import java.util.List;

public class PeripheralModeViewModel extends AndroidViewModel implements GattServer.Listener {

    // Constants
    @SuppressWarnings("unused")
    private final static String TAG = PeripheralModeViewModel.class.getSimpleName();

    private final SingleLiveEvent<Integer> mStartAdvertisingErrorCode = new SingleLiveEvent<>();

    public PeripheralModeViewModel(@NonNull Application application) {
        super(application);
    }

    public String getLocalName() {
        return PeripheralModeManager.getInstance().getLocalName();
    }

    public void setLocalName(@NonNull Context context, @Nullable String name) {
        PeripheralModeManager.getInstance().setLocalName(context, name, true);
    }

    public List<PeripheralService> getPeripheralServices() {
        return PeripheralModeManager.getInstance().getPeripheralServices();
    }

    public boolean start(@NonNull Context context) {
        return PeripheralModeManager.getInstance().start(context, this);
    }

    public void stop(@NonNull Context context) {
        PeripheralModeManager.getInstance().stop(context);
    }

    public boolean isIncludingDeviceName(@NonNull Context context) {
        return PeripheralModeManager.getInstance().isIncludingDeviceName(context);
    }

    public void setIncludeDeviceName(@NonNull Context context, boolean enabled) {
        PeripheralModeManager.getInstance().setIncludeDeviceName(context, enabled);
    }

    public static boolean isPeripheralModeSupported() {
        return GattServer.isPeripheralModeSupported();
    }

    public boolean isPeripheralModeAvailable() {
        return PeripheralModeManager.getInstance().isPeripheralModeAvailable();
    }

    public LiveData<Integer> getStartAdvertisingErrorCode() {
        return mStartAdvertisingErrorCode;
    }

    @Override
    public void onCentralConnectionStatusChanged(int status) {
        // TODO: surface errors
    }

    @Override
    public void onWillStartAdvertising() {

    }

    @Override
    public void onDidStartAdvertising() {

    }

    @Override
    public void onDidStopAdvertising() {

    }

    @Override
    public void onAdvertisingError(int errorCode) {
        mStartAdvertisingErrorCode.setValue(errorCode);
    }
}
