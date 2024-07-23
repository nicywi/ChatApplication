package com.nkcdev.chatapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText editTextEmailSignup, editTextPasswordSignup, editTextUsernameSignup;
    private Button buttonRegister;
    private CircleImageView imageViewCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextEmailSignup = findViewById(R.id.editTextEmailSignup);
        editTextPasswordSignup = findViewById(R.id.editTextEmailSignup);
        editTextUsernameSignup = findViewById(R.id.editTextUsernameSignup);
        buttonRegister = findViewById(R.id.buttonRegister);
    }
}