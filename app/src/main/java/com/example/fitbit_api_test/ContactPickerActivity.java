package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fitbit_api_test.utils.PrefsHelper;

public class ContactPickerActivity extends AppCompatActivity {

    final int REQUEST_CODE = 1;
    PrefsHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);
        prefsHelper = new PrefsHelper(this);

        //function to check if emergency contact has been set and change page content accordingly
        setPageContent();

        //set on click listener for contact picker
        Button pickContactButton = findViewById(R.id.chooseContactButton);
        pickContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Clicked on contact button", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //call button click logic
        Button callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                String phoneNumber = prefsHelper.getEmergencyContactNumber();
                phoneIntent.setData(Uri.parse("tel:"+phoneNumber));
                if (ActivityCompat.checkSelfPermission(ContactPickerActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("phone call button", "no permission");
                    final int REQUEST_PHONE_CALL = 1;
                    ActivityCompat.requestPermissions(ContactPickerActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL
                            );
                    return;
                }
                Log.d("phone call button", "calling");
                startActivity(phoneIntent);
            }
        });

        //beep button logic
        Button beepButton = findViewById(R.id.beepButton);
        beepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBeep();
                vibrateForTime(1000);
            }
        });

//        //coffee suggestion button logic
//        Button gotoSuggestionsButton = findViewById(R.id.gotoSuggestionsButton);
//        gotoSuggestionsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent coffeeActviityIntent = new Intent(ContactPickerActivity.this, CoffeeSuggestionsActivity.class);
//                startActivity(coffeeActviityIntent);
//            }
//        });


//        //tracking button logic
//        Button gotoTrackingButton = findViewById(R.id.startTrackingButton);
//        gotoTrackingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent trackingIntent = new Intent(ContactPickerActivity.this, trackingActivity.class);
//                startActivity(trackingIntent);
//            }
//        });
    }

    //function to vibrate for specific time in milliseconds
    public void vibrateForTime(int duration)
    {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(duration);
        }
    }

    //function to play beep sound
    public void playBeep()
    {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play( );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPageContent(){

        //check if emergency contacts have been set
        String contact_name = prefsHelper.getEmergencyContactName();
        String contact_number = prefsHelper.getEmergencyContactNumber();
        TextView messageTextView = findViewById(R.id.contactMessageTextView);
        Button pickContactButton = findViewById(R.id.chooseContactButton);
        Button callButton = findViewById(R.id.callButton);
        TextView contactNameTextView = findViewById(R.id.contactNameTextView);
        TextView contactNumberTextView = findViewById(R.id.contactNumberTextView);
        if(contact_name!=null)
        {
            messageTextView.setText("Current Emergency Contact");
            pickContactButton.setText("Change Contact");
            contactNameTextView.setText(contact_name);
            contactNumberTextView.setText(contact_number);
            callButton.setText("Call "+contact_name);
            callButton.setVisibility(Button.VISIBLE);
        }
        else
        {
            messageTextView.setText("No Emergency Contact has been set");
            pickContactButton.setText("Pick Contact");
            contactNameTextView.setText("");
            contactNumberTextView.setText("");
            callButton.setVisibility(Button.INVISIBLE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //check if contact picking worked
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);

                Log.d("phone number", "Z number : " + number + " , name : " + name);

                prefsHelper.setEmergencyContactName(name);
                prefsHelper.setEmergencyContactNumber(number);

                cursor.close();
                setPageContent();
            }
        }
    }
}
