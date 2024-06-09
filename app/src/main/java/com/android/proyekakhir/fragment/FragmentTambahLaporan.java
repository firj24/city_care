package com.android.proyekakhir.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.proyekakhir.Dampak;
import com.android.proyekakhir.KategoriKer;
import com.android.proyekakhir.LocationPickerActivity;
import com.android.proyekakhir.Model.Laporan;
import com.android.proyekakhir.R;
import com.android.proyekakhir.adapter.MediaRecyclerViewAdapter;
import com.android.proyekakhir.Model.MediaItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class FragmentTambahLaporan extends Fragment {

    private static final int REQUEST_SELECT_IMAGE = 2;
    private static final int REQUEST_SELECT_CATEGORY = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_VIDEO_CAPTURE = 4;
    private static final int REQUEST_AUDIO_CAPTURE = 5;
    private static final int REQUEST_GALLERY_PICK = 6;
    private static final int REQUEST_PICK_LOCATION = 7;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private FusedLocationProviderClient fusedLocationClient;
    Laporan model;

    private Uri mediaUri;
    private double latitude;
    private double longitude;
    private String address;
    private MapView mapView;
    private GoogleMap googleMap;
    RadioGroup radioGroup;

    private String mediaType;

    TextView kateg, pilihDampakButton, pilihLokasiButton, tvLatitude, tvLongitude, tvAlamat;
    Button tambahMediaButton;
    private Button kirimButton;

    LinearLayout as;
    RecyclerView mediaRecyclerView;
    MediaRecyclerViewAdapter mediaRecyclerViewAdapter;
    List<MediaItem> mediaItemList;
    private static final String TAG = "TambahLaporan";
    private EditText catatann;

    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    public interface OnLaporanSubmittedListener {
        void onLaporanSubmitted(Laporan laporan);
    }

    private OnLaporanSubmittedListener mListener;

    public void setOnLaporanSubmittedListener(OnLaporanSubmittedListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tambah_laporan, container, false);
        radioGroup = root.findViewById(R.id.radioker);
        pilihDampakButton = root.findViewById(R.id.pilihdampak);
        pilihLokasiButton = root.findViewById(R.id.btnTambahLokasi);
        kateg = root.findViewById(R.id.kateg);
        tambahMediaButton = root.findViewById(R.id.tambahmedia);
        kirimButton = root.findViewById(R.id.Kirimlap);  // Tombol kirim laporan
        mediaRecyclerView = root.findViewById(R.id.recymedia);
        mapView = root.findViewById(R.id.mapView);
        catatann = root.findViewById(R.id.catat);
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        as = root.findViewById(R.id.linearbungkus);
        Button ubahButton = root.findViewById(R.id.ubahLok); // Mendapatkan referensi ke tombol "Ubah"
        mapView.onCreate(savedInstanceState);
        mapView.setVisibility(View.GONE); // Mulai dengan menyembunyikan MapView
        ubahButton.setVisibility(View.GONE);
        as.setVisibility(View.GONE);
        tvLatitude = root.findViewById(R.id.tvLatitude);
        tvLongitude = root.findViewById(R.id.tvLongitude);
        tvAlamat = root.findViewById(R.id.tvAlamat);

        mediaItemList = new ArrayList<>();
        mediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mediaItemList);
        mediaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mediaRecyclerView.setAdapter(mediaRecyclerViewAdapter);

        // Initialize map
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                updateMap();
            }
        });

        kateg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), KategoriKer.class);
            startActivityForResult(intent, REQUEST_SELECT_CATEGORY);
        });

        // Mengatur RadioButton "Tidak" menjadi terpilih secara default
        radioGroup.check(R.id.tidker);

        // Menyembunyikan tombol "Pilih Dampak" secara default
        pilihDampakButton.setVisibility(View.GONE);

        // Listener untuk RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.tidker) {
                // Jika RadioButton "Tidak" dipilih, sembunyikan tombol "Pilih Dampak"
                pilihDampakButton.setVisibility(View.GONE);
                // Reset nilai pilihan dampak
                pilihDampakButton.setText("Pilih Dampak");
            } else if (checkedId == R.id.iyaker) {
                // Jika RadioButton "Iya" dipilih, tampilkan tombol "Pilih Dampak"
                pilihDampakButton.setVisibility(View.VISIBLE);
            }
        });

        pilihDampakButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Dampak.class);
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        });

        tambahMediaButton.setOnClickListener(v -> showBottomSheetDialog());

        // Listener untuk Pilih Lokasi
        pilihLokasiButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LocationPickerActivity.class);
            startActivityForResult(intent, REQUEST_PICK_LOCATION);
            mapView.setVisibility(View.VISIBLE); // Tampilkan MapView ketika tombol ditekan
            ubahButton.setVisibility(View.VISIBLE);
            as.setVisibility(View.VISIBLE);
            pilihLokasiButton.setVisibility(View.GONE);
        });
        ubahButton.setOnClickListener(v -> {
            // Memanggil kembali LocationPickerActivity untuk memilih lokasi baru
            Intent intent = new Intent(getActivity(), LocationPickerActivity.class);
            startActivityForResult(intent, REQUEST_PICK_LOCATION);

        });
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();

        // Sembunyikan informasi lokasi saat aplikasi pertama kali dijalankan
        hideLocationInfo();

        // Set onClickListener untuk tombol kirim
        kirimButton.setOnClickListener(v -> {
            kirimLaporan();
        });

        return root;
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Get last known location
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                address = getAddressFromCoordinates(latitude, longitude);
                                tvLatitude.setText(String.valueOf(latitude));
                                tvLongitude.setText(String.valueOf(longitude));
                                tvAlamat.setText(address);
                                updateMap();
                            }
                        }
                    });
        }
    }

    private void updateMap() {
        if (googleMap != null && latitude != 0 && longitude != 0) {
            LatLng location = new LatLng(latitude, longitude);
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(location).title("Lokasi Anda"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }

    private void hideLocationInfo() {
        tvLatitude.setVisibility(View.GONE);
        tvLongitude.setVisibility(View.GONE);
        tvAlamat.setVisibility(View.GONE);
    }

    private void showLocationInfo() {
        tvLatitude.setVisibility(View.VISIBLE);
        tvLongitude.setVisibility(View.VISIBLE);
        tvAlamat.setVisibility(View.VISIBLE);
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                if (data.getExtras() != null && data.getExtras().get("data") != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    Uri imageUri = saveMediaToExternalStorage(imageBitmap, "image");
                    addMediaToList(imageUri, "image");
                }
            } else if (requestCode == REQUEST_VIDEO_CAPTURE && data != null) {
                Uri videoUri = data.getData();
                addMediaToList(videoUri, "video");
            } else if (requestCode == REQUEST_AUDIO_CAPTURE && data != null) {
                Uri audioUri = data.getData();
                addMediaToList(audioUri, "audio");
            } else if (requestCode == REQUEST_GALLERY_PICK && data != null) {
                Uri selectedMediaUri = data.getData();
                if (selectedMediaUri != null) {
                    String mediaType = getMediaType(selectedMediaUri);
                    if (mediaType != null) {
                        addMediaToList(selectedMediaUri, mediaType);
                    } else {
                        Toast.makeText(getActivity(), "Unsupported media type", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_PICK_LOCATION && data != null) {
                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);
                address = getAddressFromCoordinates(latitude, longitude);
                tvLatitude.setText(String.valueOf(latitude));
                tvLongitude.setText(String.valueOf(longitude));
                tvAlamat.setText(address);
                updateMap();
                showLocationInfo();
            }
            if(requestCode == REQUEST_SELECT_CATEGORY) {
                String selectedCategory = data.getStringExtra("kategori");
                if (selectedCategory != null) {
                    kateg.setText(selectedCategory);
                    Log.d("TambahLaporan", "Selected Category: " +
                            selectedCategory);
                } else {
                    Log.d("TambahLaporan", "Selected Category is null");
                }
            }else if (requestCode == REQUEST_SELECT_IMAGE){
                if (data != null) {
                    String selectedImpact =
                            data.getStringExtra("selected_image");
                    pilihDampakButton.setText(selectedImpact);
                }
            }
            // Other request codes...
            else {
                Log.d("TambahLaporan", "Result not OK or data is null");
            }
        }
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottommedia, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        LinearLayout photoOption = bottomSheetView.findViewById(R.id.option_camera);
        LinearLayout videoOption = bottomSheetView.findViewById(R.id.option_video);
        LinearLayout audioOption = bottomSheetView.findViewById(R.id.option_audio);
        LinearLayout galleryOption = bottomSheetView.findViewById(R.id.option_gallery);

        photoOption.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            takePhoto();
        });

        videoOption.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            requestCameraPermission(); // Panggil metode ini untuk meminta izin kamera sebelum menangkap video
        });

        audioOption.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            recordAudio();
        });

        galleryOption.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            pickFromGallery();
        });

        bottomSheetDialog.show();
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void captureVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void recordAudio() {
        Intent recordAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (recordAudioIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(recordAudioIntent, REQUEST_AUDIO_CAPTURE);
        }
    }

    private void pickFromGallery() {
        Intent pickMediaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickMediaIntent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*", "audio/*"};
        pickMediaIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(pickMediaIntent, REQUEST_GALLERY_PICK);
    }

    private void addMediaToList(Uri mediaUri, String mediaType) {
        MediaItem mediaItem = new MediaItem(mediaUri, mediaType);
        mediaItemList.add(mediaItem);
        mediaRecyclerViewAdapter.notifyDataSetChanged();
    }

    private String getMediaType(Uri uri) {
        String type = getActivity().getContentResolver().getType(uri);
        if (type.startsWith("image/")) {
            return "image";
        } else if (type.startsWith("video/")) {
            return "video";
        } else if (type.startsWith("audio/")) {
            return "audio";
        }
        return "unknown";
    }

    private Uri saveMediaToExternalStorage(Bitmap bitmap, String mediaType) {
        Uri uri = null;
        File mediaFile;
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        if (mediaType.equals("image")) {
            mediaFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        } else {
            mediaFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName);
        }
        try (OutputStream out = new FileOutputStream(mediaFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            uri = Uri.fromFile(mediaFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    private void kirimLaporan() {
        String kategori = kateg.getText().toString();
        String catatan = catatann.getText().toString();
        String dampak = pilihDampakButton.getText().toString();
        double latitude = Double.parseDouble(tvLatitude.getText().toString());
        double longitude = Double.parseDouble(tvLongitude.getText().toString());
        String alamat = tvAlamat.getText().toString();

        if (kategori.isEmpty() || catatan.isEmpty() || alamat.isEmpty()) {
            Toast.makeText(getActivity(), "Harap isi semua bidang", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> laporan = new HashMap<>();
        laporan.put("kategori", kategori);
        laporan.put("catatan", catatan);
        laporan.put("dampak", dampak);
        laporan.put("latitude", latitude);
        laporan.put("longitude", longitude);
        laporan.put("alamat", alamat);
        laporan.put("timestamp", FieldValue.serverTimestamp());
        laporan.put("status", "Dalam Proses");

        firestore.collection("laporan")
                .add(laporan)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Laporan berhasil dikirim", Toast.LENGTH_SHORT).show();

                    Laporan newLaporan = new Laporan(kategori, catatan, dampak, latitude, longitude, alamat, "Dalam Proses");

                    if (mListener != null) {
                        mListener.onLaporanSubmitted(newLaporan);
                    }

                    resetForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Gagal mengirim laporan", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error adding document", e);
                });
    }

    private void resetForm() {
        kateg.setText("");
        catatann.setText("");
        pilihDampakButton.setText("Pilih Dampak");
        tvLatitude.setText("");
        tvLongitude.setText("");
        tvAlamat.setText("");
        mediaItemList.clear();
        mediaRecyclerViewAdapter.notifyDataSetChanged();
        mapView.setVisibility(View.VISIBLE);
        as.setVisibility(View.GONE);
        kirimButton.setVisibility(View.VISIBLE);
        hideLocationInfo();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            captureVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureVideo();
            } else {
                Toast.makeText(getActivity(), "Camera permission is required to capture video", Toast.LENGTH_SHORT).show();
            }
        }
    }
}