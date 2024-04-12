package com.android.chewbiteSensors;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.chewbiteSensors.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private  String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Prueba Log
        android.util.Log.d(tag, "onCreate");
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        //BottomNavigationView customBottomNavigationView = findViewById(R.id.customBottomNavigationView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_settings, R.id.navigation_predictions)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }

    @Override
    protected void onStart() {
        android.util.Log.d(tag, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        android.util.Log.d(tag, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        android.util.Log.d(tag, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        android.util.Log.d(tag, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        android.util.Log.d(tag, "onDestroy");
        super.onDestroy();
    }
}