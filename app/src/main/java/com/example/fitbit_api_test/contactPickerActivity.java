package com.example.fitbit_api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class contactPickerActivity extends AppCompatActivity {

    final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);

        //function to check if emergency contact has been set and change page content accordingly
        setPageContent();

        //set on click listener for contact picker
        Button pickContactButton = findViewById(R.id.chooseContactButton);
        pickContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked on contact button", Toast.LENGTH_SHORT).show();


                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    public void setPageContent(){

        //check if emergency contacts have been set
        String contact_name = readSharedPreference("emergency_contact_name");
        String contact_number = readSharedPreference("emergency_contact_number");
        TextView messageTextView = findViewById(R.id.contactMessageTextView);
        Button pickContactButton = findViewById(R.id.chooseContactButton);
        TextView contactNameTextView = findViewById(R.id.contactNameTextView);
        TextView contactNumberTextView = findViewById(R.id.contactNumberTextView);
        if(contact_name!=null)
        {
            messageTextView.setText("Current Emergency Contact");
            pickContactButton.setText("Change Contact");
            contactNameTextView.setText(contact_name);
            contactNumberTextView.setText(contact_number);
        }
        else
        {
            messageTextView.setText("No Emergency Contact has been set");
            pickContactButton.setText("Pick Contact");
            contactNameTextView.setText("");
            contactNumberTextView.setText("");
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

                saveSharedPreference("emergency_contact_name", name);
                saveSharedPreference("emergency_contact_number", number);

                cursor.close();
                setPageContent();
            }
        }
    };

    //save to shared preferences
    public void saveSharedPreference(String key, String value)
    {
        SharedPreferences sharedPref = getApplication().getSharedPreferences("mcProject", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String readSharedPreference(String key)
    {
        SharedPreferences sharedPref = this.getSharedPreferences("mcProject", Context.MODE_PRIVATE);
        String value = sharedPref.getString(key, null);
        return value;
    }

}
