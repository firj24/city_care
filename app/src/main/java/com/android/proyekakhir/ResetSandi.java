package com.android.proyekakhir;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetSandi extends AppCompatActivity {

    private EditText passwordBaruEditText, ulangiPasswordBaruEditText;
    private Button resetSandiButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_sandi);

        mAuth = FirebaseAuth.getInstance();

        passwordBaruEditText = findViewById(R.id.passwordbaru);
        ulangiPasswordBaruEditText = findViewById(R.id.passwordbaru2);
        resetSandiButton = findViewById(R.id.resetsandi);

        resetSandiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String passwordBaru = passwordBaruEditText.getText().toString().trim();
        String ulangiPasswordBaru = ulangiPasswordBaruEditText.getText().toString().trim();

        if (TextUtils.isEmpty(passwordBaru)) {
            passwordBaruEditText.setError("Masukkan kata sandi baru");
            return;
        }

        if (TextUtils.isEmpty(ulangiPasswordBaru)) {
            ulangiPasswordBaruEditText.setError("Ulangi kata sandi baru");
            return;
        }

        if (!passwordBaru.equals(ulangiPasswordBaru)) {
            Toast.makeText(this, "Kata sandi baru tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.getCurrentUser().updatePassword(passwordBaru)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetSandi.this, "Kata sandi berhasil direset", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ResetSandi.this, "Gagal mereset kata sandi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
