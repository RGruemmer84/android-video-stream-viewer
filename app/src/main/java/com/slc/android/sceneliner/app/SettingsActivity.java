package com.slc.android.sceneliner.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.slc.android.sceneliner.control.AppController;
import com.slc.android.sceneliner_1_0.R;

import org.w3c.dom.Text;

public class SettingsActivity extends ActionBarActivity {
    
    private ViewFlipper settingsFlipper;
    private TextView curUser;
    private ImageView resetPasswordArrow;
    private ImageView editBulletinArrow;
    private ImageView viewProfileArrow;
    private Switch videoStreamActiveSwitch;
    private Toolbar toolbar;

    private EditText special1Field;
    private EditText special2Field;
    private EditText special3Field;
    private EditText special4Field;
    private Button saveSpecialsButton;

    private EditText emailForPasswordResetField;
    private Button resetPasswordSendEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        curUser = (TextView) findViewById(R.id.currentLoggedInUserLabel);
        curUser.setText(AppController.getCurrentUsername());
        settingsFlipper = (ViewFlipper) findViewById(R.id.settingsFlipper);
        resetPasswordArrow = (ImageView) findViewById(R.id.resetPasswordButton);
        editBulletinArrow = (ImageView) findViewById(R.id.editBulletinButton);
        viewProfileArrow = (ImageView) findViewById(R.id.viewProfileArrow);
        videoStreamActiveSwitch = (Switch) findViewById(R.id.videoStreamActiveSwitch);

        toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(0xFFFFFFFF);

        resetPasswordArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToResetPasswordScreen();
            }
        });

        editBulletinArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEditBulletinScreen();
            }
        });

        viewProfileArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewBusinessProfile();
            }
        });

        videoStreamActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppController.setBusinessEnabled(isChecked);
            }
        });



        emailForPasswordResetField = (EditText) findViewById(R.id.resetPasswordEmailField);
        emailForPasswordResetField.setText(AppController.getCurrentUserEmail());
        resetPasswordSendEmailButton = (Button) findViewById(R.id.resetPasswordSendEmailButton);

        resetPasswordSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO add parse API
            }
        });

        special1Field = (EditText) findViewById(R.id.special1EditText);
        special2Field = (EditText) findViewById(R.id.special2EditText);
        special3Field = (EditText) findViewById(R.id.special3EditText);
        special4Field = (EditText) findViewById(R.id.special4EditText);
        special1Field.setText(AppController.getCurrentBusinessSpecials().get(0));
        special2Field.setText(AppController.getCurrentBusinessSpecials().get(1));
        special3Field.setText(AppController.getCurrentBusinessSpecials().get(2));
        special4Field.setText(AppController.getCurrentBusinessSpecials().get(3));

        saveSpecialsButton = (Button) findViewById(R.id.saveSpecialsButton);
        saveSpecialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAndSaveSpecials();
            }
        });





    }

    private void previewBusinessProfile() {
        AppController.setCurrentRegion(AppController.getCurrentUserRegion());
        AppController.setCurrentBusiness(AppController.getCurrentUserBusinessObject());
        Intent intent = new Intent(this.getApplicationContext(), SLPlayerActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        toolbar.setTitle("Settings");
    }



    private void updateAndSaveSpecials() {
        AppController.updateSpecials(
                special1Field.getText().toString(),
                special2Field.getText().toString(),
                special3Field.getText().toString(),
                special4Field.getText().toString());
    }
    
    private void switchToResetPasswordScreen() {
        settingsFlipper.setOutAnimation(this, R.anim.abc_slide_out_bottom);
        settingsFlipper.setInAnimation(this, R.anim.abc_slide_in_top);
        settingsFlipper.showNext();
        toolbar.setTitle("Reset Password");
    }
    
    
    private void switchToEditBulletinScreen() {
        settingsFlipper.setOutAnimation(this, R.anim.abc_slide_out_bottom);
        settingsFlipper.setInAnimation(this, R.anim.abc_slide_in_top);
        settingsFlipper.showNext();
        settingsFlipper.setOutAnimation(this, R.anim.abc_slide_out_bottom);
        settingsFlipper.setInAnimation(this, R.anim.abc_slide_in_top);
        settingsFlipper.showNext();
        toolbar.setTitle("Bulletin Settings");
    }
    
    private void switchToMainSettingsScreen() {
        while (settingsFlipper.getDisplayedChild() != 0) {
            settingsFlipper.setInAnimation(this, R.anim.abc_slide_in_bottom);
            settingsFlipper.setOutAnimation(this, R.anim.abc_slide_out_top);
            settingsFlipper.showPrevious();
        }
        toolbar.setTitle("Settings");
    }

    @Override
    public void onBackPressed() {
        if (settingsFlipper.getDisplayedChild() == 0)
            this.finish();
        else
            switchToMainSettingsScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AppController.userLogout();
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    


}
