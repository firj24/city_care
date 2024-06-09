package com.android.proyekakhir.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.proyekakhir.Login;
import com.android.proyekakhir.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends Fragment {

    private TextView namakun, emakun, genderakun;
    private Button keluarButton, hapusAkunButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        namakun = view.findViewById(R.id.namakun);
        emakun = view.findViewById(R.id.emakun);
        genderakun = view.findViewById(R.id.genderakun);
        keluarButton = view.findViewById(R.id.keluar);
        hapusAkunButton = view.findViewById(R.id.hapusakun);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            emakun.setText(currentUser.getEmail());
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

            // Retrieve user data from the database
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nama = snapshot.child("Nama").getValue(String.class);
                        String gender = snapshot.child("JenisKelamin").getValue(String.class);
                        namakun.setText(nama);
                        genderakun.setText(gender);
                    } else {
                        Toast.makeText(getActivity(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            });

            keluarButton.setOnClickListener(v -> showLogoutConfirmationDialog());

            hapusAkunButton.setOnClickListener(v -> showDeleteAccountConfirmationDialog());
        }

        return view;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.DialogButtonText)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Iya", (dialog1, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("Tidak", null)
                .create();
        dialog.show();

        // Set the color of the buttons
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teks_dialog)); // Warna terang untuk teks "Iya"
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teks_dialog)); // Warna terang untuk teks "Tidak"
    }

    private void showDeleteAccountConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.DialogButtonText)
                .setTitle("Konfirmasi Hapus Akun")
                .setMessage("Apakah Anda yakin ingin menghapus akun?")
                .setPositiveButton("Iya", (dialog1, which) -> {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Akun berhasil dihapus", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), Login.class);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                Toast.makeText(getActivity(), "Gagal menghapus akun", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Tidak", null)
                .create();
        dialog.show();

        // Set the color of the buttons
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teks_dialog)); // Warna terang untuk teks "Iya"
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teks_dialog)); // Warna terang untuk teks "Tidak"
    }
}
