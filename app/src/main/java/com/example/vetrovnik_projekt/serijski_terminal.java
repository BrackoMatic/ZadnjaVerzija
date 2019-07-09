package com.example.vetrovnik_projekt;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import static android.widget.Toast.makeText;

public class serijski_terminal extends MainActivity {
    Button sendButton, goBackButton;
    EditText data;
    private TextView recieve_text;
    BluetoothSocket blsocket;
    BluetoothDevice pairedBluetoothDevice;
    Switch fragmentSwitch;
    CountDownTimer countDownTimer;
    Boolean BreakRUNflag;
    Fragment SetRegulatorParameters, GraphFragment;

    private static final int REQUEST_ENABLE_BT = 1;

    ThreadConnected myThreadConnected;
    Fragment graphFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serijski_terminal);


        fragmentSwitch = findViewById(R.id.izpisuj);
//        sendButton = findViewById(R.id.sendButton);
        recieve_text = findViewById(R.id.recieved_text);                          // TextView performance decreases with number of spans
        recieve_text.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme())); // set as default color to reduce number of spans
        recieve_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        goBackButton = findViewById(R.id.goBackButton);

//        data = findViewById(R.id.editText);


        fragmentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    if (blsocket.isConnected()) {
                        try {
                            Fragment f = (Fragment) (GraphFragment.class).newInstance();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                        fragmentSwitch.setText("Regulator");
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.changeFragment, new GraphFragment()).commit();
                        fm.executePendingTransactions();
                        BreakRUNflag = false;
                        return;
                    }
                }

                fragmentSwitch.setText("Graph");
                GraphFragment fragment = new GraphFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.changeFragment, new SetRegulatorParameters()).commit();
                fm.executePendingTransactions();
                return;
            }
        });
//
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BreakRUNflag = true;
//
//            }
//        });//
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentSwitch.setChecked(false);
                try {
                    blsocket.close();
                    myThreadConnected = null;
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


        BluetoothDevice bt;
        try {
            bt = Objects.requireNonNull(getIntent().getExtras()).getParcelable("ListviewClickValue");
            assert bt != null;
            vzpostaviBT(bt);
            recieve_text.setText("start ThreadConnectBTdevice");
            fragmentSwitch.setChecked(false);
        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    private void vzpostaviBT(BluetoothDevice bluetoothDevice) {

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            blsocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            blsocket.connect();
            pairedBluetoothDevice = bluetoothDevice;
            makeText(getApplicationContext(), "Device paired successfully!", Toast.LENGTH_LONG).show();
            BreakRUNflag = false;
            startThreadConnected(blsocket);

        } catch (IOException ioe) {
            //Log("taha>", "cannot connect to device :( " +ioe);
            makeText(getApplicationContext(), "Could not connect", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(serijski_terminal.this, MainActivity.class);
            startActivity(intent);
            pairedBluetoothDevice = null;
        }
    }

    public void posljipodatke(String podatki) {
        //make sure there is a paired device
        if (blsocket == null) {
            return;
        }
        try {
            byte[] data = (podatki + "\r\n").getBytes();
            blsocket.getOutputStream().write(data);
        } catch (Exception c) {
            ///
        }
        BreakRUNflag = false;
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

    public void posredujNaActivity(String string) {
        posljipodatke(string);
    }

    private void startThreadConnected(BluetoothSocket socket) {

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }


    private class ThreadConnected extends Thread {
        private final InputStream inputStream;

        private ThreadConnected(BluetoothSocket socket) {

            InputStream in = null;
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
            final Bundle bundle = new Bundle();


            while (true) {
                if (!BreakRUNflag) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                        bytes = inputStream.read(buffer);
                        final String msgReceived = new String(buffer, 0, bytes);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GraphFragment graphFragment = new GraphFragment();
                                if (fragmentSwitch.isChecked()) {
                                    if (!msgReceived.isEmpty()) {
                                        try {
                                            Scanner in = new Scanner(msgReceived).useDelimiter("[^0-9]+");
                                            int integer = in.nextInt();
                                            graphFragment.addEntry(graphFragment, integer);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }
                                recieve_text.setText(msgReceived);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(serijski_terminal.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });

                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String string = String.valueOf(data.getText());
                            try {
                                posljipodatke(string);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    });

                }
            }
        }

    }

}

