package yabomonkey.example.contentproviderexercise

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import yabomonkey.example.contentproviderexercise.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

    private var readGranted = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val hasReadContactPermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS)
        Log.d(TAG, ".onCreate: checkSelfPermission returned $hasReadContactPermission")

        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Permission Granted")
            readGranted = true      // TODO DON'T DO THIS
        } else {
            Log.d(TAG, "REQUESTING PERMISSION")
            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
        }

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Log.d(TAG, "fab onClick: Starts")
            val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

            val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

            val contacts = arrayListOf<String>()        //create a list to hold our contacts
            cursor?.use {                   // loop through the cursor
                while (it.moveToNext()) {
                    contacts.add(it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                }
            }

            val adapter = ArrayAdapter<String>(this, R.layout.contact_detail, R.id.name, contacts)
            binding.root.findViewById<ListView>(R.id.contact_names).adapter = adapter

            Log.d(TAG, "fab onClick: ends")

        }
        Log.d(TAG, "onCreate: ends")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: starts")
        when (requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {
                readGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, YAY! Do the
                    // contacts-related task we need to do.
                    Log.d(TAG, "onRequestPermissionsResult: permission granted")
                    true
                } else {
                    //
                    //
                    Log.d(TAG, "onRequestPermissionsResult: permission refused")
                    false
                }
            }
        }
    }

    //test update 2

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

}