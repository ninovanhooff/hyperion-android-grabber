package com.abrenoch.hyperiongrabber;

import android.graphics.Color;

import com.example.android.screencapture.HyperionProto;
import java.io.IOException;

class HyperionThread extends Thread {
    private String HOST;
    private int PORT;
    private int PRIORITY;
    private final int FRAME_DURATION = -1;
    private Hyperion mHyperion;

    HyperionScreenService.HyperionThreadBroadcaster mSender;
    HyperionThreadListener mReceiver = new HyperionThreadListener() {
        @Override
        public void sendFrame(byte[] data, int width, int height) {
            HyperionProto.HyperionRequest req =
                    Hyperion.setImageRequest(data, width, height, PRIORITY, FRAME_DURATION);
            if (mHyperion != null && mHyperion.isConnected()) {
                try {
                    mHyperion.sendRequest(req);
                } catch (IOException e) {
                    mSender.onConnectionError(e.hashCode(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void clear() {
            if (mHyperion != null && mHyperion.isConnected()) {
                try {
                    mHyperion.clear(PRIORITY);
                } catch (IOException e) {
                    mSender.onConnectionError(e.hashCode(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void disconnect() {
            if (mHyperion != null) {
                try {
                    mHyperion.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    HyperionThread(HyperionScreenService.HyperionThreadBroadcaster listener, final String host,
                   final int port, final int priority){
        HOST = host;
        PORT = port;
        PRIORITY = priority;
        mSender = listener;
    }

    public HyperionThreadListener getReceiver() {return mReceiver;}

    @Override
    public void run(){
        try {
            mHyperion = new Hyperion(HOST, PORT);
        } catch (IOException e) {
            mSender.onConnectionError(e.hashCode(), e.getMessage());
            e.printStackTrace();
        } finally {
            if (mHyperion != null && mSender != null && mHyperion.isConnected()) {
                mSender.onConnected();
            }
        }
    }

    public interface HyperionThreadListener {
        void sendFrame(byte[] data, int width, int height);
        void clear();
        void disconnect();
    }
}