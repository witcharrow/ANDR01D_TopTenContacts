package es.android.toptencontacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TopTenMainActivity extends Activity {

	private static final String TAG = "TopTenMainActivity-->";

	private static final String[] PROJECTION = {Contacts._ID, Contacts.DISPLAY_NAME};
	
	private TextView mContact;
	private TextView mNameList;
	private String tenContacts="";
	private TextView mTotalContacts;
	private TextView mfinalList;
	private Button mAllContacts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_ten_main);

		ContentResolver cr = getContentResolver();

		// Lanzamos el servicio
		Intent intent = new Intent(TopTenMainActivity.this,BackgroundService.class);
		startService(intent);

		readNumberOfContacts(cr);
		showTopTen(cr);

		mAllContacts = (Button) findViewById(R.id.button1);
		mAllContacts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Lanzamos el Intent
				Intent intent = new Intent(TopTenMainActivity.this, ContactsListActivity.class);
				startActivity(intent);
			}
		});

	}

	private void showTopTen(ContentResolver cr) {
		
		// ## Hacemos la consulta--> cambiado del ejemplo para que devuelva los
		// contactos por orden de veces contactados
		// en orden DESCENDENTE (de mas a menos)
		Cursor c = cr.query(Contacts.CONTENT_URI, PROJECTION, null, null,
				Contacts.TIMES_CONTACTED + " DESC");
		int count=0;
		mContact = (TextView) findViewById(R.id.textView2);
		mContact.setText("¡Estos son tus 10 Favoritos!");
		
		if (c != null && c.moveToFirst()) {
			int columnName = c.getColumnIndexOrThrow(Contacts.DISPLAY_NAME);
			
			do {
				String name = c.getString(columnName);
				int id=count+1;
				tenContacts+= id+".- "+name+".\n";
				mfinalList = (TextView) findViewById(R.id.textView0);
				mNameList = (TextView) findViewById(R.id.textView1);				
				if(count<3){
					mfinalList.setText(tenContacts);
					mfinalList.setTextColor(Color.YELLOW);
				}
				else{
					mNameList.setText(tenContacts);
					mNameList.setTextColor(Color.DKGRAY);
				}
					
				count++;
			} while (c.moveToNext() && count<10);
			c.close();
		} else
			Log.i(TAG, "No hay informacion");
		Log.i(TAG, "Has LEIDO " + count + " contactos");		
		mTotalContacts.setTextColor(Color.LTGRAY);

	}

	private void readNumberOfContacts(ContentResolver cr) {		
		Cursor c = cr.query(Contacts.CONTENT_URI, PROJECTION, null, null,
				Contacts.TIMES_CONTACTED + " DESC");
		int count=0;
		int id=0;
		if (c != null && c.moveToFirst()) {
			do {
				id=count+1;
				mContact = (TextView) findViewById(R.id.textView2);
				mContact.setText("Leyendo " + Integer.valueOf(count)
						+ " contactos");
				count++;
			} while (c.moveToNext());
			c.close();
		} else
			Log.i(TAG, "No hay informacion");		
		Log.i(TAG, "Has LEIDO " + count + " contactos");
		mTotalContacts = (TextView) findViewById(R.id.textView3);		
		mTotalContacts.setText("("+id+" contactos totales)");
	}

}
