package com.example.vetrovnik_projekt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ArrayList<BluetoothDevice> ListDevices;
    Button tipka_napolni, tipka_visinvis, tipka_onoff;
    ListView seznam_naprav;
    BluetoothAdapter myBT_A;
    Set<BluetoothDevice> povezane_naprave;
    // com.example.vetrovnik_projekt.BluetoothSocket blsocket = null;
    public BluetoothSocket blsocket;
    BluetoothDevice pairedBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tipka_napolni = findViewById(R.id.tipka_napolni);
        tipka_visinvis = findViewById(R.id.tipka_visinvis);
        tipka_onoff = findViewById(R.id.tipka_onoff);
        seznam_naprav = findViewById(R.id.seznam_naprav);

        ListDevices = new ArrayList<>();
        myBT_A = BluetoothAdapter.getDefaultAdapter();

        if (myBT_A == null) {
            Toast.makeText(this, "Bt aint compatible", Toast.LENGTH_SHORT).show();
        }
        if (myBT_A.isEnabled()) {
            Toast.makeText(this, "Bt je omogocen", Toast.LENGTH_SHORT).show();

        }

        tipka_onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (myBT_A.isEnabled()) {
                    myBT_A.disable();
                    //     Toast.makeText(this,"BT disabled", Toast.LENGTH_SHORT).show();

                    if (blsocket != null && blsocket.isConnected()) {
                        try {
                            blsocket.close();
                            Toast.makeText(getApplicationContext(), "disconnected", Toast.LENGTH_LONG).show();

                        } catch (IOException ioe) {
                            Log.e("app>", "Cannot close socket");
                            pairedBluetoothDevice = null;
                            Toast.makeText(getApplicationContext(), "Could not disconnect", Toast.LENGTH_LONG).show();

                        }

                    }
                } else {
                    Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intentOn, 0);
                    //   Toast.makeText(this,"BT omogocen",Toast.LENGTH_SHORT).show();


                }
            }
        });

        tipka_visinvis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
        });
        tipka_napolni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seznam();
            }
        });
        seznam_naprav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice posljibt = ListDevices.get(i);

                Intent intent = new Intent(MainActivity.this, serijski_terminal.class);
                intent.putExtra("ListviewClickValue", posljibt);
                startActivityForResult(intent, 0);

            }
        });
    }

    public void seznam() {
        povezane_naprave = myBT_A.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();


        for (BluetoothDevice bt : povezane_naprave) {
            list.add(bt.getName());

            Toast.makeText(this, "Prikazane so povezane naprave", Toast.LENGTH_SHORT).show();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            seznam_naprav.setAdapter(adapter);
            ListDevices.add(bt);
        }


    }
}
