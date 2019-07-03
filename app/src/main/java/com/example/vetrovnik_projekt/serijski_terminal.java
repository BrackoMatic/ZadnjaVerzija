package com.example.vetrovnik_projekt;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.io.OutputStream;

import static com.example.vetrovnik_projekt.R.color.colorAccent;

public class serijski_terminal extends MainActivity {
    Button button2, button;
    EditText data;
    String str, podatki;
    private TextView recieve_text;
    InputStream inputStream;
    OutputStream outputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    private String newline = "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serijski_terminal);

//            byte[] buffer = new byte[1024];
//            int bytes;
//            try {
//                inputStream=blsocket.getInputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                outputStream = blsocket.getOutputStream();
//            } catch(IOException e)
//            {
//                e.printStackTrace();
//            }
        button2 = findViewById(R.id.button2);
        recieve_text = findViewById(R.id.recieve_text);                          // TextView performance decreases with number of spans
        recieve_text.setTextColor(getResources().getColor(R.color.colorPrimary)); // set as default color to reduce number of spans
        recieve_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        button = findViewById(R.id.button);

        data = findViewById(R.id.editText);

        beginListenForData();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String test = data.getText().toString();

                String string = String.valueOf(data.getText());
                try {
                    posljipodatke(string);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // serijski_terminal.this.posljipodatke(data.getText().toString());
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(serijski_terminal.this, MainActivity.class));
            }
        });
    }

    void posljipodatke(String podatki) {
        //make sure there is a paired device
        if (pairedBluetoothDevice != null && blsocket != null) {
            return;
        }
        try {
            //SpannableStringBuilder spn = new SpannableStringBuilder(podatki+"\r\n");
            // Span to set text color to some RGB value
            //final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(158, 158, 158));
            // spn.setSpan(fcs, 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            recieve_text.setText("");
            recieve_text.append(podatki + "\r\n");

            byte[] data = (podatki + newline + "\r").getBytes();
            blsocket.getOutputStream().write(data);
        } catch (Exception c) {
            ///
        }
        ;
    }
    public void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                try {
                    inputStream=blsocket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = inputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            recieve_text.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }


}

