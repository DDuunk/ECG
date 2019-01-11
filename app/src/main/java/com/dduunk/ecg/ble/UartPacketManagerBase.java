package com.dduunk.ecg.ble;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dduunk.ecg.ble.central.BlePeripheralUart;
import com.dduunk.ecg.mqtt.MqttManager;
import com.dduunk.ecg.mqtt.MqttSettings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class UartPacketManagerBase implements BlePeripheralUart.UartRxHandler {

    // Log
    private final static String TAG = UartPacketManagerBase.class.getSimpleName();

    // Listener
    public interface Listener {
        void onUartPacket(UartPacket packet);
    }

    // Data
    protected final Handler mMainHandler = new Handler(Looper.getMainLooper());
    protected WeakReference<Listener> mWeakListener;
    protected List<UartPacket> mPackets = new ArrayList<>();
    protected Semaphore mPacketsSemaphore = new Semaphore(1, true);
    private boolean mIsPacketCacheEnabled;
    protected Context mContext;
    protected MqttManager mMqttManager;

    protected long mReceivedBytes = 0;
    protected long mSentBytes = 0;

    public UartPacketManagerBase(@NonNull Context context, @Nullable Listener listener, boolean isPacketCacheEnabled, @Nullable MqttManager mqttManager) {
        mContext = context.getApplicationContext();
        mIsPacketCacheEnabled = isPacketCacheEnabled;
        mMqttManager = mqttManager;
        mWeakListener = new WeakReference<>(listener);
    }

    @Override
    public void onRxDataReceived(@NonNull byte[] data, @Nullable String identifier, int status) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.w(TAG, "onRxDataReceived error:" + status);
            return;
        }
        UartPacket uartPacket = new UartPacket(identifier, UartPacket.TRANSFERMODE_RX, data);

        // Mqtt publish to RX
        if (mMqttManager != null) {
            if (MqttSettings.isPublishEnabled(mContext)) {
                final String topic = MqttSettings.getPublishTopic(mContext, MqttSettings.kPublishFeed_RX);
                if (topic != null) {
                    final int qos = MqttSettings.getPublishQos(mContext, MqttSettings.kPublishFeed_RX);
                    mMqttManager.publish(topic, uartPacket.getData(), qos);
                }
            }
        }

        try {
            mPacketsSemaphore.acquire();
        } catch (InterruptedException e) {
            Log.w(TAG, "InterruptedException: " + e.toString());
        }
        mReceivedBytes += data.length;
        if (mIsPacketCacheEnabled) {
            mPackets.add(uartPacket);
        }

        // Send data to delegate
        Listener listener = mWeakListener.get();
        if (listener != null) {
            mMainHandler.post(() -> listener.onUartPacket(uartPacket));
        }
        mPacketsSemaphore.release();
    }

    public void clearPacketsCache() {
        mPackets.clear();
    }

    public List<UartPacket> getPacketsCache() {
        return mPackets;
    }

    public void resetCounters() {
        mReceivedBytes = 0;
        mSentBytes = 0;
    }

    public long getReceivedBytes() {
        return mReceivedBytes;
    }

    public long getSentBytes() {
        return mSentBytes;
    }
}
