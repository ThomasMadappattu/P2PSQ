package com.d2dsq.ui;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.d2dsq.radio.ClientThread;
import com.d2dsq.radio.ProgressData;
import com.d2dsq.radio.ServerThread;
import com.d2dsq.routing.Packet;

public class MainApplication extends Application
{
	
	protected static BluetoothAdapter adapter;
    protected static Set<BluetoothDevice> pairedDevices;
    protected static Handler clientHandler;
    protected static Handler serverHandler;
    protected static Handler wifiMsgHandler; 
    protected static ClientThread clientThread;
    protected static ServerThread serverThread;
    protected static ProgressData progressData = new ProgressData();
    protected static  ConcurrentLinkedQueue<Packet> packetQueue = new ConcurrentLinkedQueue<Packet> () ;
	
    
    // Application related constants 
    public  static final int MSG_WIFI_SERVER = 1; 
    public  static final int MSG_WIFI_CLIENT = 2; 

}
