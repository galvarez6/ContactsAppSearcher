package edu.utep.cs.cs4330.contactsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACTS_PERMISSION = 100;
    private ContactsAdapter contactsAdapter;
    private EditText nameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestContactsPermission();

        contactsAdapter = new ContactsAdapter(this, R.layout.name_phone_item, new ArrayList<>());
        nameEdit = findViewById(R.id.nameEdit);

        Button button = findViewById(R.id.findButton);
        button.setOnClickListener(this::findClicked);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(contactsAdapter);
    }

    //** To be called when the Find button is clicked. */
    private void findClicked(View view) {
        contactsAdapter.clear();
        contactsAdapter.notifyDataSetChanged();

        // do the following to display a found name-and-phone pair:
        //  contactsAdapter.add(new NameAndPhone(name, phone));
        //  contactsAdapter.notifyDataChanged();
        //

        String search = nameEdit.getText().toString().toLowerCase();
        Uri allContacts = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //String[] selectionArgs = {search};
        String searchArgs = "%" + search + "%";

        //LIKE compares character by character for display name from the input the user provided
        Cursor c = getContentResolver().query(allContacts,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?",new String[]{searchArgs},null);
        Log.d("mes", "QUERY STRING: " + String.valueOf(c));
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactsAdapter.add(new NameAndPhone(name, phone));
        }
        c.close();


        //--
    }

    //-- TODO: WRITE YOUR CODE HERE, e.g., helper methods if any.
    //
    //--

    private void requestContactsPermission() {
        String smsPermission = Manifest.permission.READ_CONTACTS;
        int grant = ContextCompat.checkSelfPermission(this, smsPermission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[] { smsPermission };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CONTACTS_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            showToast(grantResults[0] == PackageManager.PERMISSION_GRANTED ?
                    "Permission granted!" : "Permission not granted!");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private static class NameAndPhone {
        public final String name;
        public final String phone;
        public NameAndPhone(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }
    }

    private static class ContactsAdapter extends ArrayAdapter<NameAndPhone> {
        public ContactsAdapter(Context context, int resourceId, List<NameAndPhone> items) {
            super(context, resourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.name_phone_item, parent, false);
            }

            NameAndPhone item = getItem(position);
            TextView textView = convertView.findViewById(R.id.nameView);
            textView.setText(item.name);
            textView = convertView.findViewById(R.id.phoneView);
            textView.setText(item.phone);
            return convertView;
        }
    }
}