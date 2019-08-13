package com.a420.methlab.badtrip;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * @brief
 * Die MainActivity wird bei öffnen der App aufgerufen und erstellt das GameFragment, ClientFragment, ServerFragment, MenuFragment, und das
 * GameOverFragment.
 * Bei starten der App wird zuerst die Methode switchtoMenu aufgerufen, welche das MenuFragment lädt.
 */
public class MainActivity extends AppCompatActivity {

    BluetoothConnectionService btService;
    Screen gameScreen;

    ArrayList<Integer> scores = new ArrayList<Integer>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btService = new BluetoothConnectionService(getApplicationContext(), BluetoothAdapter.getDefaultAdapter());
        switchToMenu();
    }

    public void switchToMenu() {
        MenuFragment menuFragment = new MenuFragment();
        menuFragment.setBtService(btService);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, menuFragment).commit();
    }

    public void switchToPlay() {
        GameFragment gameFragment = new GameFragment();
        gameFragment.setBtService(btService);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, gameFragment).commit();
    }

    public void switchToServer() {
        ServerFragment serverFragment = new ServerFragment();
        serverFragment.setBtService(btService);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, serverFragment).commit();
    }

    public void switchToClient() {
        ClientFragment clientFragment = new ClientFragment();
        clientFragment.setBtService(btService);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, clientFragment).commit();
    }

    public void switchToGameOver(int score) {
        GameOverFragment killFragment = new GameOverFragment();
        killFragment.setScoreValue(score);
        killFragment.setScoreBoardList(scores);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, killFragment).commit();
    }
}
