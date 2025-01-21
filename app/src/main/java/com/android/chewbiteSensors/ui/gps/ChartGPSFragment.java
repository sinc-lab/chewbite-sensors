package com.android.chewbiteSensors.ui.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.databinding.FragmentChartGpsBinding;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class ChartGPSFragment extends Fragment {

    private FragmentChartGpsBinding binding;
    private MapView mapView;
    private LocationManager locationManager;
    private TextView messageTextView;

    // Lanzador para gestionar permisos
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineLocationGranted != null && fineLocationGranted || coarseLocationGranted != null && coarseLocationGranted) {
                    checkLocationSettings();
                } else {
                    showMessage("Se requieren permisos de ubicación para mostrar el mapa.");
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChartGpsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar el MapView
        mapView = binding.map;
        messageTextView = root.findViewById(R.id.messageTextView); // Referencia al TextView

        Context context = getContext();
        if (context != null) {
            Configuration.getInstance().load(context, context.getSharedPreferences("prefs", Context.MODE_PRIVATE));
        }
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);  // Controles multitáctiles

        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            checkLocationSettings();
        }

        return root;
    }

    private void checkLocationSettings() {
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showMessage("Por favor, encienda el GPS para continuar.");
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            showMessage("Buscando ubicación, por favor aguarde un momento...");

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    showLocationOnMap(location);
                    locationManager.removeUpdates(this);  // Detener actualizaciones después de obtener la ubicación
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // Puedes manejar el cambio de estado aquí si es necesario
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    // El proveedor ha sido habilitado
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    showMessage("El GPS ha sido desactivado.");
                }
            });
        }
    }

    private void showLocationOnMap(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        GeoPoint currentLocation = new GeoPoint(latitude, longitude);
        mapView.getController().setCenter(currentLocation);  // Centrar el mapa en la ubicación actual
        mapView.getController().setZoom(8.0);  // Usar método de zoom con valor double

        // Agregar un marcador en la ubicación actual
        Marker marker = new Marker(mapView);
        marker.setPosition(currentLocation);
        marker.setTitle("Ubicación Actual");
        mapView.getOverlays().add(marker);
        marker.showInfoWindow();

        // Limpiar el mensaje
        messageTextView.setVisibility(View.GONE); // Ocultar el mensaje
        mapView.setVisibility(View.VISIBLE);  // Mostrar el mapa
    }

    private void showMessage(String message) {
        messageTextView.setText(message);
        messageTextView.setVisibility(View.VISIBLE); // Mostrar el mensaje
        mapView.setVisibility(View.GONE);  // Ocultar el mapa
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mapView.onDetach();
    }
}
