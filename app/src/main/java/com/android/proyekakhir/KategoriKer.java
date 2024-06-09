package com.android.proyekakhir;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class KategoriKer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori_ker);

        ImageButton jalanButton = findViewById(R.id.jal);
        ImageButton jembatanButton = findViewById(R.id.jem);
        ImageButton bencanaButton = findViewById(R.id.benc);
        ImageButton lainnyaButton = findViewById(R.id.lain);

        jalanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultAndFinish("Jalan");
            }
        });

        jembatanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultAndFinish("Jembatan");
            }
        });

        bencanaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultAndFinish("Bencana");
            }
        });

        lainnyaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate layout untuk dialog
                LayoutInflater inflater = LayoutInflater.from(KategoriKer.this);
                View dialogView = inflater.inflate(R.layout.dialog_input_nama_bencana, null);

                EditText inputEditText = dialogView.findViewById(R.id.input_nama_bencana);

                // Membuat dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(KategoriKer.this);
                builder.setView(dialogView)
                        .setTitle("Nama Bencana")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String namaBencana = inputEditText.getText().toString();
                                if (!namaBencana.isEmpty()) {
                                    setResultAndFinish(namaBencana);
                                } else {
                                    Toast.makeText(KategoriKer.this, "Nama bencana tidak boleh kosong", Toast.LENGTH_SHORT).show();
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

    private void setResultAndFinish(String kategori) {
        Intent intent = new Intent();
        intent.putExtra("kategori", kategori);
        setResult(RESULT_OK, intent);
        Log.d("KategoriKer", "Setting result with category: " + kategori); // Log the category being set
        finish();
    }

}
