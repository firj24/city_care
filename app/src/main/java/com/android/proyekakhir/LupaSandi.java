package com.android.proyekakhir;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.proyekakhir.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LupaSandi extends AppCompatActivity {

    private EditText emailEditText;
    private Button selanjutnyaButton;
    private FirebaseAuth mAuth;
    private TextView masuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_sandi);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.lupaemail);
        selanjutnyaButton = findViewById(R.id.selanjutnya);
        masuk = findViewById(R.id.ingatsandi);
        masuk.setOnClickListener(v -> startActivity(new Intent(LupaSandi.this, Login.class)));
        selanjutnyaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    sendPasswordResetEmail(email);
                } else {
                    Toast.makeText(LupaSandi.this, "Masukkan alamat email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LupaSandi.this, "Email dengan instruksi reset kata sandi telah dikirim", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LupaSandi.this, Login.class));
                        } else {
                            Toast.makeText(LupaSandi.this, "Gagal mengirim email reset kata sandi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
