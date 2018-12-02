package com.slc.android.sceneliner.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.slc.android.sceneliner.control.AppController;
import com.slc.android.sceneliner_1_0.R;

import java.util.ArrayList;

public class BusinessSettingsActivity extends Activity {

    private Button viewFeedsButton;
    private Switch enableSwitch;
    private Button saveButton;

    private EditText special1EditText;
    private EditText special2EditText;
    private EditText special3EditText;
    private EditText special4EditText;

    private ArrayList<String> currentSpecials;

    private Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_settings);
        appContext = this.getApplicationContext();

        viewFeedsButton = (Button) findViewById(R.id.viewFeedsButton);
        enableSwitch = (Switch) findViewById(R.id.videoStreamActiveSwitch);
        saveButton = (Button) findViewById(R.id.saveButton);

        special1EditText = (EditText) findViewById(R.id.special1EditText);
        special2EditText = (EditText) findViewById(R.id.special2EditText);
        special3EditText = (EditText) findViewById(R.id.special3EditText);
        special4EditText = (EditText) findViewById(R.id.special4EditText);

        currentSpecials = AppController.getCurrentBusinessSpecials();


        special1EditText.setText(currentSpecials.get(0));
        special2EditText.setText(currentSpecials.get(1));
        special3EditText.setText(currentSpecials.get(2));
        special4EditText.setText(currentSpecials.get(3));

        viewFeedsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.setCurrentRegion(AppController.getCurrentUserRegion());
                AppController.setCurrentBusiness(AppController.getCurrentUserBusinessObject());
                Intent intent = new Intent(appContext, SLPlayerActivity.class);
                startActivity(intent);
            }
        });

        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppController.setBusinessEnabled(isChecked);
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String special1 = null;
                String special2 = null;
                String special3 = null;
                String special4 = null;
                if (!special1EditText.getText().toString().equals(currentSpecials.get(0)))
                    special1 = special1EditText.getText().toString();
                if (!special2EditText.getText().toString().equals(currentSpecials.get(1)))
                    special2 = special2EditText.getText().toString();
                if (!special3EditText.getText().toString().equals(currentSpecials.get(2)))
                    special3 = special3EditText.getText().toString();
                if (!special4EditText.getText().toString().equals(currentSpecials.get(3)))
                    special4 = special4EditText.getText().toString();

                AppController.updateSpecials(special1, special2, special3 ,special4);

            }
        });
    }


}
