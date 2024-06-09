package com.android.proyekakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.InputType;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText emaill, passwordd;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private ImageView togglePasswordVisibility;
    private boolean isPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emaill = findViewById(R.id.email);
        passwordd = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Silahkan Tunggu");
        progressDialog.setCancelable(false);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);

        Button masuk = findViewById(R.id.masuk);
        masuk.setOnClickListener(v -> {
            String email = emaill.getText().toString();
            String password = passwordd.getText().toString();

            if (email.length() > 0 && password.length() > 0) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    progressDialog.show();
                    login(email, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Email tidak valid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Silahkan isi semua data", Toast.LENGTH_SHORT).show();
            }
        });

        TextView lupaPass = findViewById(R.id.lupapass);
        lupaPass.setOnClickListener(v -> startActivity(new Intent(Login.this, LupaSandi.class)));

        TextView belumPunya = findViewById(R.id.belumpunya);
        belumPunya.setOnClickListener(v -> startActivity(new Intent(Login.this, Daftar.class)));
    }


    public void togglePasswordVisibility(View view) {
        if (isPasswordVisible) {
            passwordd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.baseline_visibility_off_24);
        } else {
            passwordd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.baseline_visibility_24);
        }
        passwordd.setSelection(passwordd.length());
        isPasswordVisible = !isPasswordVisible;
    }
    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful() && task.getResult() != null) {
                FirebaseUser user = task.getResult().getUser();
                if (user != null) {
                    reload();
                } else {
                    Toast.makeText(getApplicationContext(), "Akun Belum Terdaftar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Akun Belum Terdaftar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reload() {
        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();  // Close the current activity
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }
}
