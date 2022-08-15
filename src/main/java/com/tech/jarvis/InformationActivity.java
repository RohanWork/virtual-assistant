package com.tech.jarvis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tech.jarvis.Database;
import com.tech.jarvis.R;

public class InformationActivity extends AppCompatActivity {

    EditText first, last, email, contact;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);

        Button button = findViewById(R.id.submit);
        first = findViewById(R.id.edit1);
        last = findViewById(R.id.edit2);
        email = findViewById(R.id.edit3);
        contact = findViewById(R.id.edit4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database database = new Database(InformationActivity.this);
                boolean result = database.updateUser(first.getText().toString(), last.getText().toString(), email.getText().toString(), contact.getText().toString());
                if (result == true) {
                    Toast.makeText(InformationActivity.this, "User Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InformationActivity.this, "Updated Failed", Toast.LENGTH_SHORT).show();
                }
                database.changePageState("true");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
