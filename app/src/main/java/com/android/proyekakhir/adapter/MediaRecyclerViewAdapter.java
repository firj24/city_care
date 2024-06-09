package com.android.proyekakhir.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.VideoView;
import com.android.proyekakhir.R;
import com.android.proyekakhir.Model.MediaItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class MediaRecyclerViewAdapter extends RecyclerView.Adapter<MediaRecyclerViewAdapter.MediaViewHolder> {

    private List<MediaItem> mediaItemList;

    public MediaRecyclerViewAdapter(List<MediaItem> mediaItemList) {
        this.mediaItemList = mediaItemList;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaItem mediaItem = mediaItemList.get(position);
        Uri mediaUri = Uri.parse(mediaItem.getMediaPath());

        // Extract metadata from the media path if it contains metadata
        String query = mediaUri.getQuery();
        if (query != null) {
            String[] queries = query.split("&");
            for (String q : queries) {
                if (q.startsWith("lat=")) {
                    String latitude = q.substring(4);
                    Log.d("Metadata", "Latitude: " + latitude);
                } else if (q.startsWith("lon=")) {
                    String longitude = q.substring(4);
                    Log.d("Metadata", "Longitude: " + longitude);
                }
            }
        }

        // Load media file into ImageView using Glide library
        if (mediaItem.getMediaType().equals("video")) {
            holder.videoView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVideoURI(mediaUri);
            holder.videoView.start();
        } else {
            holder.videoView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(mediaItem.getMediaPath())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageView);
        }

        holder.deleteIcon.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            mediaItemList.remove(currentPosition);
            notifyItemRemoved(currentPosition);
        });
    }

    @Override
    public int getItemCount() {
        return mediaItemList.size();
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;
        ImageView deleteIcon;
        MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoViewMedia);
            imageView = itemView.findViewById(R.id.imageViewMedia);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}