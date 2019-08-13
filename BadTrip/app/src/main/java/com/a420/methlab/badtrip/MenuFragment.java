package com.a420.methlab.badtrip;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ParcelUuid;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.UUID;


/**
 * Main Menu Class, opens MainActivity.class when Button new Game is pressed:
 */

/**
 * @brief Main Menu Class
 * öffnet das GameFragment wenn der newGame button geclickt wird.
 * Zwei weitere Buttons erzeugen jeweils die BleutoothConnection mittels Server-Client Verbindung.
 * Ein CountdownTimer wartet mit dem Fragment wechsel zu ClientFragment und ServerFragment, bis der ConnectedThread läuft.
 */
public class MenuFragment extends Fragment {
    private final String TAG = "MenuFragment";

    //Main Menu Class calls MainActivity in New Game Button
    private BluetoothConnectionService btService = null;
    BluetoothDevice preferredDevice;

    boolean commitOnce = true;
    FragmentTransaction commitingTransaction = null;
    CountDownTimer commitTimer;
    FragmentTransaction startTransaction;
    FragmentTransaction serverTransaction;
    FragmentTransaction clientTransaction;

    ListView deviceListView;

    public void enableDiscoverable() {
        Log.d(TAG, "make Discorable for 200sec");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);
        startActivity(discoverableIntent);
    }

    public void setBtService(BluetoothConnectionService btService) {
        this.btService = btService;
    }

    public void setPlayTransaction(FragmentTransaction t) {
        startTransaction = t;
    }

    public void setServerTransaction(FragmentTransaction t) {
        serverTransaction = t;
    }

    public void setClientTransaction(FragmentTransaction t) {
        clientTransaction = t;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.menu, container, false);

        deviceListView = (ListView) contentView.findViewById(R.id.lvNewDevices);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                preferredDevice = (BluetoothDevice) adapterView.getItemAtPosition(i);
                ParcelUuid[] list = preferredDevice.getUuids();
                if(list != null) {
                    for(ParcelUuid id: list) {
                        Log.d("deviceList.onItemClick", id.toString());
                    }
                } else
                    Log.d(TAG, "Keine UUIDs");
                Log.d(TAG, "Starte Client zu " + preferredDevice);
                btService.startClient(preferredDevice, UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));

            }
        });
        deviceListView.setAdapter(new DeviceListAdapter(getContext(), R.layout.device_adapter_view));

        /*
        initialize Soundmanager to import Game sounds
        play background theme:
         */
        SoundManager.initializeSoundmanager(getContext());
        SoundManager.getInstance().playMenuMusic();



        /*
        Menu Buttons ids:
         */
        ImageButton newGame = (ImageButton) contentView.findViewById(R.id.startactivitymain);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundManager.getInstance().playButton1Sound();
                SoundManager.getInstance().stopMenuMusic();


                //Switch Fragment!!!
                ((MainActivity)getActivity()).switchToPlay();
            }
        });

        ImageButton bluetoothServer = (ImageButton) contentView.findViewById(R.id.bluetoothserver);
        bluetoothServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("", "bluetoothserver;");

                btService.enableBluetooth();
                enableDiscoverable();
                btService.startServer();

                new CountDownTimer(Long.MAX_VALUE, 20) {

                    @Override
                    public void onTick(long l) {
                        if(btService.isConnected() && commitOnce) {
                            ((MainActivity)getActivity()).switchToServer();
                            commitOnce = false;
                            cancel();
                        }
                    }

                    @Override
                    public void onFinish() {
                    }
                }.start();
            }
        });

        ImageButton bluetoothReciever = (ImageButton) contentView.findViewById(R.id.bluetoothreciever);
        bluetoothReciever.setOnClickListener(new View.OnClickListener() {
            boolean discovered;

            @Override
            public void onClick(View view) {
                Log.d("", "bluetoothreciever;");
                ((DeviceListAdapter)deviceListView.getAdapter()).clear();

                btService.enableBluetooth();
                BroadcastReceiver discoveryReciever = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent)
                    {
                        String action = intent.getAction();
                        DeviceListAdapter temp = (DeviceListAdapter) deviceListView.getAdapter();

                        if(action.equals(BluetoothDevice.ACTION_FOUND))
                        {
                            BluetoothDevice newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                            //if(newDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                                Log.d("discoveryReceiver", "Found device: " + newDevice.getAddress());
                                temp.add(newDevice);
                            //}
                        } else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
                        {
                            //Discovery finished...
                        }
                    }
                };
                getContext().registerReceiver(discoveryReciever, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                getContext().registerReceiver(discoveryReciever, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
                btService.discover();

                new CountDownTimer(Long.MAX_VALUE, 20) {

                    @Override
                    public void onTick(long l) {
                        if(btService.isConnected() && commitOnce) {
                            ((MainActivity)getActivity()).switchToClient();
                            commitOnce = false;
                            cancel();
                        }
                    }

                    @Override
                    public void onFinish() {
                    }
                }.start();
            }
        });

        return contentView;
    }


}
