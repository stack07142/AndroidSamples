/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.example.android.codelabs.navigation

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.navigation_activity.*

/**
 * A simple activity demonstrating use of a NavHostFragment with a navigation drawer.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        setSupportActionBar(toolbar)

        /**
         * [NavHostFragment]
         */
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return
        /**
         * [NavController]
         */
        val navController = host.navController

        /**
         * Create an AppBarConfiguration with the correct top-level destinations
         *
         * The purpose of [AppBarConfiguration] is to specify the "configuration options" you want for your toolbars,
         * collapsing toolbars, and action bars.
         *
         * Configuration options include whether the bar must handle a drawer layout and which destinations are considered
         * "top-level destinations".
         *
         * Top-level destinations are the root-level destinations of your app.
         * These destinations do not display an "up" button in the app bar,
         * and they display the drawer icon if the destination uses a drawer layout.
         */
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.home_dest, R.id.deeplink_dest), drawer_layout
        )

        /**
         * 1. Show a title in the ActionBar based off of the destinations label
         * 2. Display the Up button whenever you're not on a top-level destination
         * 3. Display a drawer icon when you're on a top-level destination
         */
        setupActionBarWithNavController(navController, appBarConfiguration)

        /**
         * Set up a [NavigationView]
         */
        nav_view?.setupWithNavController(navController)

        /**
         * Set up a [BottomNavigationView] for use with a [NavController].
         * This will call [android.view.MenuItem.onNavDestinationSelected] when a menu item is selected.
         *
         * Notice that their ids match the destinations of navigation graph
         */
        bottom_nav_view?.setupWithNavController(navController)

        /**
         * Adds an [NavController.OnDestinationChangedListener] to this [NavController]
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }

            Toast.makeText(
                this@MainActivity, "Navigated to $dest",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("NavigationActivity", "Navigated to $dest")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val retValue = super.onCreateOptionsMenu(menu)

        /**
         * [NavigationView] is an easy way to display a navigation menu from a menu resource.
         * [NavigationView] is typically placed inside a [androidx.drawerlayout.widget.DrawerLayout]
         */
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        /**
         * The NavigationView already has these same navigation items, so we only add
         * navigation items to the menu here if there isn't a NavigationView
         *
         * Material Design Guide
         * : Avoid using a navigation drawer with other primary navigation components, such as a bottom navigation bar.
         */
        if (navigationView == null) {
            menuInflater.inflate(R.menu.overflow_menu, menu)
            return true
        }
        return retValue
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Have the NavigationUI look for an action or destination matching the menu
        // item id and navigate there if found.
        // Otherwise, bubble up to the parent.
        return item.onNavDestinationSelected(findNavController(R.id.my_nav_host_fragment))
                || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Allows NavigationUI to support proper up navigation or the drawer layout
        // drawer menu, depending on the situation
        return findNavController(R.id.my_nav_host_fragment).navigateUp(appBarConfiguration)
    }
}
