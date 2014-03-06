package es.android.toptencontacts;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.widget.SimpleCursorAdapter;

/**
 * Este ejemplo muestra como usar un SimpleCursorAdapter para mostrar la informacion de los
 * contactos en una ListActivity
 *
 */
public class ContactsListActivity extends ListActivity {

	private static final String[] PROJECTION = {Contacts._ID, Contacts.DISPLAY_NAME};
	
	private SimpleCursorAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ContentResolver cr = getContentResolver();
		
		//## Hacemos la consulta--> cambiado del ejemplo para que devuelva los contactos por orden de veces contactados
		// en orden DESCENDENTE (de mas a menos)
		Cursor c = cr.query(Contacts.CONTENT_URI, PROJECTION, null, null, Contacts.TIMES_CONTACTED+" DESC");
		
		//## Escogemos las columnas y recursor que deben vincularse:
		//## DISPLAY_NAME --> text1
		String[] from = new String[] {Contacts.DISPLAY_NAME};
		int[] to = new int[] {android.R.id.text1};
		
		//## Creamos el adapter con el cursor y la informacion del mapeo
		mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, from, to, 0);
		setListAdapter(mAdapter);
	}

}
