package com.android.proyekakhir;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Daftar extends AppCompatActivity {

    private EditText nama, email, pass, pass2;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private ImageView showPassword, showPassword2;
    private RadioGroup radioGroupGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        nama = findViewById(R.id.masuknama);
        email = findViewById(R.id.daftaremail);
        pass = findViewById(R.id.daftarpassword);
        pass2 = findViewById(R.id.daftarpassword2);

        progressDialog = new ProgressDialog(Daftar.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Silahkan Tunggu");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        TextView btnpunya = findViewById(R.id.punyaakun);
        Button btndaftar = findViewById(R.id.daftar);

        showPassword = findViewById(R.id.hide);
        showPassword2 = findViewById(R.id.hide2);

        radioGroupGender = findViewById(R.id.radioGroupGender);

        btnpunya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Daftar.this, Login.class));
            }
        });

        btndaftar.setOnClickListener(v -> {
            if (isInputValid()) {
                daftar(nama.getText().toString(), email.getText().toString(), pass.getText().toString());
            } else {

            }
        });

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        showPassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    private void togglePasswordVisibility() {
        boolean isPasswordVisible = pass.getTransformationMethod() instanceof HideReturnsTransformationMethod;

        if (isPasswordVisible) {
            pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            pass2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showPassword.setImageResource(R.drawable.baseline_visibility_off_24);
            showPassword2.setImageResource(R.drawable.baseline_visibility_off_24);
            Log.d("PasswordVisibility", "Password visibility: Hidden");
        } else {
            pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            pass2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            showPassword.setImageResource(R.drawable.baseline_visibility_24);
            showPassword2.setImageResource(R.drawable.baseline_visibility_24);
            Log.d("PasswordVisibility", "Password visibility: Visible");
        }
    }

    private boolean isInputValid() {
        // Mendapatkan nilai dari setiap field
        String nameInput = nama.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String passwordInput = pass.getText().toString().trim();
        String confirmPasswordInput = pass2.getText().toString().trim();

        // Validasi bahwa semua field tidak boleh kosong
        if (nameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
            Toast.makeText(this, "Silahkan isi semua data", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validasi bahwa email memiliki format yang benar
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validasi bahwa password memiliki panjang yang cukup
        if (passwordInput.length() < 6) {
            Toast.makeText(this, "Password harus memiliki setidaknya 6 karakter", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validasi bahwa kedua password cocok
        if (!passwordInput.equals(confirmPasswordInput)) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validasi bahwa password mengandung huruf kapital di awal
        if (!Character.isUpperCase(passwordInput.charAt(0))) {
            Toast.makeText(this, "Password harus dimulai dengan huruf kapital", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kembalikan true jika semua validasi berhasil
        return true;
    }


    private void daftar(String masuknama, String daftaremail, String daftarpassword) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(daftaremail, daftarpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser != null) {
                        String gender = getSelectedGender();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(masuknama)
                                .build();
                        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    saveUserDataToDatabase(firebaseUser.getUid(), masuknama, gender);
                                    Toast.makeText(getApplicationContext(), "Akun berhasil didaftarkan", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Daftar.this, Login.class));
                                    finish();  // Close the current activity
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal memperbarui profil pengguna", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Daftar Gagal!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private String getSelectedGender() {
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
        return selectedGenderRadioButton.getText().toString();
    }

    private void saveUserDataToDatabase(String userId, String nama, String gender) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> userData = new HashMap<>();
        userData.put("Nama", nama);
        userData.put("JenisKelamin", gender);
        databaseReference.child("Users").child(userId).setValue(userData);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();  // Close the current activity
        }
    }
}
