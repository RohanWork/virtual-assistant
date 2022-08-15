package com.tech.jarvis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SimActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simchange);

        Button button = findViewById(R.id.button);
        final EditText editText = findViewById(R.id.edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database database = new Database(SimActivity.this);
                boolean result = database.updateSimNumber(editText.getText().toString());
                if (result == true) {
                    Toast.makeText(SimActivity.this, "Number updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SimActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}
