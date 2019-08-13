package com.a420.methlab.badtrip;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/** @brief
 * Klasse BluettohConnectionService erstellt drei Threads.
 * InsecureAcceptThread läuft bis eine verbindung erstellt wird.
 * ConnectThread läuft um eine Verbindung aufzubauen.
 * ConnectedThread läuft während eine Verbindung besteht.
 */

public class BluetoothConnectionService
{

    private final String TAG = "BTConnectionService";
    private final String APPNAME = "Asteroids";
    private final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter bluetoothAdapter;
    Context context;

    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private BluetoothDevice mmDevice;
    private UUID deviceUUID;

    private Queue<String> msgQueue;

    /**@brief
     * BluetoothConnectionService Object wird von Fragment zu Fragment überreicht in der MainActivity
     *
     * @param context current context
     * @param bluetoothAdapter der bluetoothAdapter des current Devices
     */
    public BluetoothConnectionService(Context context, BluetoothAdapter bluetoothAdapter)
    {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;

        msgQueue = new ConcurrentLinkedQueue<String>();
    }





    /** @brief
     *
    #################################################################
    Thread class die auf einkommende verbindungen wartet. Läuft bis eine Verbindung akzeptiert wurde.
    Ist ein Server side client
     */

    private class AcceptThread extends Thread
    {

        private final BluetoothServerSocket serverSocket;

        public AcceptThread()
        {
            BluetoothServerSocket tmp = null;

            try
            {
                //create a listening serversocet RFCOM
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APPNAME, MY_UUID_INSECURE);
                Log.d(TAG, "Accept Thread setting up server socket: " + MY_UUID_INSECURE);
            }catch (IOException e)
            {

            }

            serverSocket = tmp;

        }

        /**
         * @brief
         * run Methode des Accept Threads wartet auf eine Verbindung
         */
        public void run()
        {
            Log.d(TAG, "Accept Thread running...");

            BluetoothSocket socket = null;

            //only return on succesful connection or an exception
            Log.d(TAG, "RFCOM Server socket start...");
            try {
                socket = serverSocket.accept(); //runs until connection start or exc
                Log.d(TAG, "RFCOM accepted connection");
            }
            catch (IOException e)
            {
                Log.d(TAG, "Accept Thread IOException "+ e.getMessage());
            }

            if(socket != null)
            {
                connected(socket, mmDevice);
            }

            Log.d(TAG, "Accept Thread END.");
        }

        /**
         * @brief
         * schließt den Accept Thread
         */
        public void cancel()
        {
            Log.d(TAG, "cancel Accept Thread...");

            try
            {
                serverSocket.close();
            }catch(IOException e)
            {
                Log.d(TAG, "cancel close Accept Thread failed: "+ e.getMessage());
            }
        }
    }

    /* @Brief
    ##################################################
    Läuft bei Verbindungs Aufbau
     */
    private class ConnectThread extends Thread
    {
        private BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device, UUID id)
        {
            mmDevice = device;
            deviceUUID = id;
        }

        public void run()
        {
            BluetoothSocket tmp = null;
            Log.d(TAG, "ConnectThread running...");

            /*
            get aBluetooth Socket for connection with given Bluetooth Device:
             */
            try
            {
                Log.d(TAG, "ConnectThread create fromSocket: " + deviceUUID);
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            }catch (IOException e)
            {
                Log.d(TAG, "ConnectThread create fromSocket failed" + e.getMessage());
            }

            socket = tmp;

            //cancel discovery to avoid slowing down connection:
            bluetoothAdapter.cancelDiscovery();

            //make a connection
            try
            {
                socket.connect();
                Log.d(TAG, "ConnectThread connected...");
            }
            catch (IOException e)
            {
                Log.d(TAG, "ConnectThread connect() failed" + e.getMessage());
            }

            connected(socket, mmDevice);
        }

        public void cancel()
        {
            try
            {
               Log.d(TAG, "cancel(), closing Client Socket" );
                socket.close();
            }catch (IOException e)
            {
                Log.d(TAG, "cancel() failed" + e.getMessage());
            }
        }
    }

    /**
     * @brief
     * aktiviert Bluetooth auf dem Device
     */
    public void enableBluetooth() {

        if (bluetoothAdapter == null) {
            Log.d(TAG, "not supported Bluetooth");
        }
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {

            Log.d(TAG, "enable:");

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBTIntent);
        }
    }

    /**
     * @brief
     * startet die discovery von anderen devices
     */
    public void discover() {
        Log.d(TAG, "looking for unpaired devices:");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "cancelDiscovery:");

            //check BT permissions in manifest
            //checkBTPermissions();

            bluetoothAdapter.startDiscovery();
        }
        if (!bluetoothAdapter.isDiscovering()) {

            //check BT permissions in manifest
            //checkBTPermissions();

            bluetoothAdapter.startDiscovery();
        }
    }

    /**
     * @brief
     * startet den Accept thread um im server mode verbindungen anzunehmen
     *
     */
    public synchronized void start()
    {
    Log.d(TAG, "start");

        //cancel any thread making a connection:
        if(connectThread != null)
        {
            connectThread.cancel();
            connectThread = null;
        }

        if(insecureAcceptThread != null)
        {
            insecureAcceptThread = new AcceptThread();
            insecureAcceptThread.start();
        }
    }

    /**
     * @brief
     * startet den Server Accept Thread
     */
    public void startServer() {
        Log.d(TAG, "startServer...");

        insecureAcceptThread = new AcceptThread();
        insecureAcceptThread.start();
    }

    /**
     * @brief ConnectThread baut eine verbindung mit dem anderen Device auf
     */
    public void startClient(BluetoothDevice device, UUID uuid)
    {
        Log.d(TAG, "startClient...");

        connectThread = new ConnectThread(device, uuid);
        connectThread.start();
    }

    /**
     * @brief
     * ConnectedThread läuft bei erfolgreichem Pairing
     * besitzt input und output stream mit dem in der run() Methode Daten empfangen werden
     */
    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket msocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            Log.d(TAG, "ConnectedThread starting...");
            msocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                tmpIn = msocket.getInputStream();
                tmpOut = msocket.getOutputStream();
            }catch (IOException e)
            {
                Log.d(TAG, "getInput /getOutput failed:" + e.getMessage());
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        /**
         * @brief
         * läuft bei erfolgreicher verbindung und empfängt gesendete Daten von write()
         */
        public void run()
        {
            byte[] buffer = new byte[1024]; //buffer store for stream
            int bytes; //bytes returned from read()

            //keep listening until exception:
            while(true)
            {
                try
                {
                    //read from Input Stream:
                    bytes = inputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                    msgQueue.add(incomingMessage);
                }catch (IOException e)
                {
                   Log.d(TAG, "IOException: " + e.getMessage());
                    break;
                }
            }
        }

        /**
         * @brief
         * sende Daten and den output Stream. Wird im connected Thread des anderen Devices empfangen
         * @param bytes
         */
        public void write(byte[] bytes)
        {
            String data = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write to outputStream: " + data);

            try
            {
                outputStream.write(bytes);
                outputStream.flush();
            }catch (IOException e)
            {
                Log.d(TAG, "write() error writing to outputStream: "+ e.getMessage());
            }
        }
        //call to shutdown the connection:
        public void cancel()
        {

            try
            {
              msocket.close();
            }
            catch (IOException e)
            {

            }
        }

    }

    /**
     * @brief
     * startet den connected Thread mit dem angegebenen Server
     * @param socket
     * @param device
     */
    private void connected(BluetoothSocket socket, BluetoothDevice device)
    {
        Log.d(TAG, "connected Starting...");

        if(insecureAcceptThread != null) {
            //insecureAcceptThread.cancel();
            insecureAcceptThread = null;
        }
        if(connectThread != null) {
            //connectThread.cancel();
            connectThread = null;
        }
        //start Thread to manage connection and transmit data
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    /**
     * @brief
     * return ob eine aktive Verbindung besteht
     * @return
     */
    public boolean isConnected() {
        return connectedThread != null;
    }

    /*
     write to connected Thread from main:
     */

    /**
     * @brief
     * Schreibe Daten an den Output Stream. Wird im ConnectedThread run() des paired Devices empfangen.
     * @param out
     */
    public void write(byte[] out)
    {
        //creat temp. obj.
        ConnectedThread c;

        //sychronize a copy of ConnectedThread, perform write
        connectedThread.write(out);
    }

    /**
     * @brief hasMessages
     * checkt, ob die Message Queue msgQueue nachrichten gespeichert die in der run() Methode des Connected Threads empfangen und gespeichert wurden.
     * @return
     */
    public boolean hasMessages() {
        return !msgQueue.isEmpty();
    }

    /**
     * @brief pollMessages
     * nimmt eine Nachricht aus der Warteschlange
     * @return
     */
    public String pollMessages() {
        return msgQueue.poll();
    }
}
