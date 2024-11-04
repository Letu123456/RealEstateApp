package com.example.realestateapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.realestateapp.R;
import com.example.realestateapp.databinding.ActivityMainBinding;
import com.example.realestateapp.fragments.ChatListFragment;
import com.example.realestateapp.fragments.FavoriteListFragment;
import com.example.realestateapp.fragments.HomeFragment;
import com.example.realestateapp.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startLoginOptionsActivity();
        }
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.item_home) {
                    showHomeFragment();
                } else if (itemId == R.id.item_chats) {
                    showChatListFragment();
                } else if (itemId == R.id.item_favorits) {
                    showFavoritsFragment();
                } else if (itemId == R.id.item_profile) {

                    if (firebaseAuth.getCurrentUser() == null) {
                        MyUtils.toast(MainActivity.this, "Login Required...!");
                        return false;
                    } else {
                        showProfileFragment();
                        return true;
                    }
                }
                    return true;

            }
        });
    }

    private void startLoginOptionsActivity() {
        startActivity(new Intent(this, LoginOptionActivity.class));
    }

    private void showHomeFragment() {
        binding.toolbarTilteTv.setText("Home");

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), homeFragment, "HomeFragment");
        fragmentTransaction.commit();
    }

    private void showChatListFragment() {
        binding.toolbarTilteTv.setText("Chats");

        ChatListFragment chatListFragment = new ChatListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), chatListFragment, "ChatListFragment");
        fragmentTransaction.commit();
    }

    private void showFavoritsFragment() {
        binding.toolbarTilteTv.setText("Favorites");

        FavoriteListFragment favoriteListFragment = new FavoriteListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), favoriteListFragment, "FavoriteListFragment");
        fragmentTransaction.commit();
    }

    private void showProfileFragment() {
        binding.toolbarTilteTv.setText("Profiles");

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), profileFragment, "ProfileFragment");
        fragmentTransaction.commit();
    }

}
