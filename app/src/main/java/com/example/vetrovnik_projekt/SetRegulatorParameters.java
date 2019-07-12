package com.example.vetrovnik_projekt;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;


public class SetRegulatorParameters extends Fragment

{
    EditText insertKd,insertKi,insertKp;
    Button btnSendKd,btnSendKi,btnSendKp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view;
        view =  inflater.inflate(R.layout.fragment_set_regulator_parameters,container,false);

        btnSendKd=view.findViewById(R.id.btnSendKd);
        btnSendKi=view.findViewById(R.id.btnSendKi);
        btnSendKp=view.findViewById(R.id.btnSendKp);

        insertKd=view.findViewById(R.id.insertKd);
        insertKi=view.findViewById(R.id.insertKi);
        insertKp=view.findViewById(R.id.insertKp);
        Switch turnOnOffswitch = view.findViewById(R.id.turnOnOffswitch);


        turnOnOffswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    ((serijski_terminal)getActivity()).posredujNaActivity("V1");
                    turnOnOffswitch.setText("Turn Off");
                    turnOnOffswitch.setBackgroundColor(Color.GREEN);
                }else
                {
                    ((serijski_terminal)getActivity()).posredujNaActivity("V0");
                    turnOnOffswitch.setText("Turn On");
                    turnOnOffswitch.setBackgroundColor(Color.RED);
                }
            }
        });
        btnSendKd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String podatki= insertKd.getText().toString();
                ((serijski_terminal)getActivity()).posredujNaActivity("K"+podatki);
            }
        });
        btnSendKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String podatki= insertKi.getText().toString();
                ((serijski_terminal)getActivity()).posredujNaActivity("I"+podatki);
            }
        });
        btnSendKp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String podatki= insertKp.getText().toString();
                ((serijski_terminal)getActivity()).posredujNaActivity("P"+podatki);
            }
        });

        return view;

    }

///https://stackoverflow.com/questions/12659747/call-an-activity-method-from-a-fragment
}
