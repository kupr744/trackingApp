package com.app.trackingapp;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.app.trackingapp.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private DialogClass dc;
    ActivityResultLauncher<String[]> locationPermissionRequest;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /// TODO move code so that in mapready
        // check if app has fine location access
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // we got permission
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // got only approximate location
                        dc =  new DialogClass("Location Permission", "This message appeared because you only granted" +
                                " approximate Location access. But in order to properly use this Application you need to provice " +
                                "fine Location access.\n");
                        dc.show(getChildFragmentManager(), "no-permission");
                        terminateApp();
                    } else {
                        dc =  new DialogClass("Location Permission", "This message appeared because you only granted" +
                                " approximate Location access. But in order to properly use this Application you need to provice " +
                                "fine Location access.\n");
                        dc.show(getChildFragmentManager(), "no-permission");
                        terminateApp();
                    }
                }
        );

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void terminateApp() {
        System.exit(0);
    }

}