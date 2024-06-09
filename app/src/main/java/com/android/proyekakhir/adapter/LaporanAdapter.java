package com.android.proyekakhir.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.proyekakhir.Model.Laporan;
import com.android.proyekakhir.R;

import java.util.List;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {
    private List<Laporan> laporanList;

    public LaporanAdapter(List<Laporan> laporanList) {
        this.laporanList = laporanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hasil_laporan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Laporan laporan = laporanList.get(position);
        holder.kategori.setText(laporan.getKategori());
        holder.waktu.setText("Just now"); // Contoh, Anda bisa memformat waktu dengan lebih baik
        holder.latitude.setText(String.valueOf(laporan.getLatitude()));
        holder.longitude.setText(String.valueOf(laporan.getLongitude()));
        holder.status.setText(laporan.getStatus());
        // Anda bisa menambahkan lebih banyak data seperti alamat, dampak, dll.
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView kategori, waktu, latitude, longitude, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            kategori = itemView.findViewById(R.id.kategori);
            waktu = itemView.findViewById(R.id.waktu);
            latitude = itemView.findViewById(R.id.latitude);
            longitude = itemView.findViewById(R.id.longtitude);
            status = itemView.findViewById(R.id.status);
        }
    }
}
