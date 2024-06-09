package com.android.proyekakhir;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Dampak extends AppCompatActivity {

    public static final String SELECTED_IMAGE = "selected_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dampak);

        ImageButton macetButton = findViewById(R.id.mac);
        ImageButton kecelakaanButton = findViewById(R.id.kecelak);
        ImageButton meninggalButton = findViewById(R.id.mening);
        ImageButton logistikButton = findViewById(R.id.logis);
        ImageButton terlambatButton = findViewById(R.id.terlam);
        ImageButton cederaButton = findViewById(R.id.ceder);
        ImageButton lainnyaButton = findViewById(R.id.lain);

        macetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(SELECTED_IMAGE, "Macet");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        kecelakaanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(SELECTED_IMAGE, "Kecelakaan");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        meninggalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(SELECTED_IMAGE, "Meninggal");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        logistikButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(SELECTED_IMAGE, "Logistik");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        terlambatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(SELECTED_IMAGE, "Terlambat");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cederaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(SELECTED_IMAGE, "Cedera");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        lainnyaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate layout untuk dialog
                LayoutInflater inflater = LayoutInflater.from(Dampak.this);
                View dialogView = inflater.inflate(R.layout.dialog_input_nama_bencana, null);

                EditText inputEditText = dialogView.findViewById(R.id.input_nama_bencana);

                // Membuat dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Dampak.this);
                builder.setView(dialogView)
                        .setTitle("Dampak Lainnya")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String dampakLainnya = inputEditText.getText().toString();
                                if (!dampakLainnya.isEmpty()) {
                                    // Mengirim dampakLainnya ke activity tambah laporan
                                    Intent intent = new Intent();
                                    intent.putExtra(SELECTED_IMAGE, dampakLainnya);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(Dampak.this, "Dampak lainnya tidak boleh kosong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Batal", null);

                AlertDialog dialog = builder.create();

                // Mengatur warna latar belakang dan warna teks
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getWindow().setBackgroundDrawableResource(R.color.biru);
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
                    }
                });

                // Menampilkan dialog
                dialog.show();
            }
        });
    }
}
