package com.example.vetrovnik_projekt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.io.OutputStream;

import static android.widget.Toast.makeText;
import static com.example.vetrovnik_projekt.R.color.colorAccent;

public class serijski_terminal extends MainActivity {
    Button sendButton, goBackButton;
    EditText data;
    private TextView recieve_text;
    BluetoothSocket blsocket;
    BluetoothDevice pairedBluetoothDevice;
    private String newline = "\n";

    Switch izpisuj;


    private static final int REQUEST_ENABLE_BT = 1;
    //    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serijski_terminal);
        izpisuj = findViewById(R.id.izpisuj);
        sendButton = findViewById(R.id.sendButton);
        recieve_text = findViewById(R.id.recieved_text);                          // TextView performance decreases with number of spans
        recieve_text.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme())); // set as default color to reduce number of spans
        recieve_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        goBackButton = findViewById(R.id.goBackButton);

        data = findViewById(R.id.editText);

        izpisuj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {

                    if (blsocket.isConnected()) {
                        startThreadConnected(blsocket);
                    }
                } else {
                    myThreadConnected = new ThreadConnected(blsocket);
                }

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String string = String.valueOf(data.getText());
                try {
                    posljipodatke(string);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });//
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    blsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(serijski_terminal.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        setup();
    }

    private void setup() {
//        String bundle;
//        BluetoothDevice device;
//        device = getIntent().getBundleExtra("ListviewClickValue");

        BluetoothDevice bt;
        bt = getIntent().getExtras().getParcelable("ListviewClickValue");
        vzpostaviBT(bt);

        recieve_text.setText("start ThreadConnectBTdevice");
//        myThreadConnectBTdevice = new ThreadConnectBTdevice(blsocket);
//        myThreadConnectBTdevice.start();
//       startThreadConnected(blsocket);
    }

    private void vzpostaviBT(BluetoothDevice bluetoothDevice) {

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            blsocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            blsocket.connect();
            pairedBluetoothDevice = bluetoothDevice;
            makeText(getApplicationContext(), "Device paired successfully!", Toast.LENGTH_LONG).show();


        } catch (IOException ioe) {
            //Log("taha>", "cannot connect to device :( " +ioe);
            makeText(getApplicationContext(), "Could not connect", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(serijski_terminal.this, MainActivity.class);
            startActivity(intent);
            pairedBluetoothDevice = null;
        }
    }


    void posljipodatke(String podatki) {
        //make sure there is a paired device
        if (pairedBluetoothDevice != null && blsocket != null) {
            return;
        }
        try {
            byte[] data = (podatki + newline + "\r").getBytes();
            blsocket.getOutputStream().write(data);
        } catch (Exception c) {
            ///
        }
        ;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (myThreadConnectBTdevice != null) {
////            myThreadConnectBTdevice.cancel();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else {
                makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void startThreadConnected(BluetoothSocket socket) {

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }


    private class ThreadConnected extends Thread {
        private final InputStream inputStream;

        private ThreadConnected(BluetoothSocket socket) {

            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = in;
        }

        // FUNKCIJA ZA SPREJEMANJE !
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;


            while (true) {
                if (izpisuj.isChecked()) {
                    try {
                        bytes = inputStream.read(buffer);
                        String strReceived = new String(buffer, 0, bytes);
                        final String msgReceived = strReceived;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recieve_text.setText(msgReceived);
                            }
                        });

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        final String msgConnectionLost = "Connection lost:\n"
                                + e.getMessage();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

//                            makeText(this,"Lost connection",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(serijski_terminal.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });

                    }
                } else {
                    return;
                }
            }

        }

    }
}

