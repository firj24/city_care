package com.android.proyekakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.proyekakhir.adapter.AdapterViewPager;
import com.android.proyekakhir.fragment.History;
import com.android.proyekakhir.fragment.FragmentHome;
import com.android.proyekakhir.fragment.Profile;
import com.android.proyekakhir.fragment.FragmentTambahLaporan;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    ViewPager2 pagemain;
    private FirebaseUser firebaseUser;
    private ImageView logo;
    private TextView city, name;
    private Button kateg;

    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    BottomNavigationView bottomNav;
    FragmentHome fragmentHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        logo = findViewById(R.id.logohome);
        city = findViewById(R.id.cityhome);
        name = findViewById(R.id.namaprof);
        bottomNav = findViewById(R.id.bottomnav);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            name.setText(firebaseUser.getDisplayName());
        } else {
            name.setText("Login Gagal!");
        }

        logo.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Home.class));
        });

        pagemain = findViewById(R.id.pagemain);
        fragmentHome = new FragmentHome();
        fragmentArrayList.add(fragmentHome);
        fragmentArrayList.add(new History());
        fragmentArrayList.add(new FragmentTambahLaporan());
        fragmentArrayList.add(new Profile());

        AdapterViewPager adapterViewPager = new AdapterViewPager(this, fragmentArrayList);
        pagemain.setAdapter(adapterViewPager);
        pagemain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    bottomNav.setSelectedItemId(R.id.homee);
                } else if (position == 1) {
                    bottomNav.setSelectedItemId(R.id.history);
                } else if (position == 2) {
                    bottomNav.setSelectedItemId(R.id.add);
                } else if (position == 3) {
                    bottomNav.setSelectedItemId(R.id.profile);
                }
                super.onPageSelected(position);
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.homee) {
                    pagemain.setCurrentItem(0);
                    return true;
                } else if (itemId == R.id.history) {
                    pagemain.setCurrentItem(1);
                    return true;
                } else if (itemId == R.id.add) {
                    pagemain.setCurrentItem(2);
                    return true;
                } else if (itemId == R.id.profile) {
                    pagemain.setCurrentItem(3);
                    return true;
                }
                return false;
            }
        });
    }
}
