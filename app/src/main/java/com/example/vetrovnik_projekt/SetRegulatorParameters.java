package com.example.vetrovnik_projekt;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class SetRegulatorParameters extends Fragment {
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

        btnSendKd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertKd.getText().toString();

            }
        });

        return view;

    }

}
