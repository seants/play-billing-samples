/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sample.android.trivialdrivesample;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;
import com.sample.android.trivialdrivesample.databinding.ActivityMainBinding;

/**
 * MainActivity here exists as a container for the
 */
public class MainActivity extends AppCompatActivity{
    private MainActivityViewModel mainActivityViewModel;
    private ActivityMainBinding activityMainBinding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MainActivity", "onCreate");
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Setup toolbar with nav controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);

        // Create our Activity ViewModel, which exists to handle global Snackbar messages
        mainActivityViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(MainActivityViewModel.class);
        mainActivityViewModel.getMessages().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout,s,Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
        // Allows billing to refresh purchases during onResume
        getLifecycle().addObserver(mainActivityViewModel.getBillingLifecycleObserver());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // we could nicely disable this when we don't have a premium purchase by observing
            // the LiveData for the SKU_GAS, but it's just there for testing
            case R.id.menu_consume_premium:
                mainActivityViewModel.debugConsumePremium();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
