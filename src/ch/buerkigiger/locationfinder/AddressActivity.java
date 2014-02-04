package ch.buerkigiger.locationfinder;

import java.util.List;
import java.util.Locale;

import ch.buerkigiger.locationfinder.AboutActivity;
import ch.buerkigiger.locationfinder.R;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddressActivity extends Activity {

    private EditText txtAddress;
    private TextView txtLocation;
    private TextView txtStatus;
    private String address;
 

    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);

		Button btnClear = (Button) findViewById(R.id.buttonClear);
		btnClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				clearFields();
			}
		});

		Button btnGetAddress = (Button) findViewById(R.id.buttonAddress);
		btnGetAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				setLocation();
			}
		});

		txtAddress = (EditText) findViewById(R.id.editTextAddress);
		txtLocation = (TextView) findViewById(R.id.textLocation);
		txtStatus = (TextView) findViewById(R.id.textStatus);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.address, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case R.id.action_location:
			navigateToLocationActivity();
			return true;
		case R.id.action_about:
			navigateToAboutActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void clearFields()
	{
		txtAddress.setText("");
		txtLocation.setText("");
		txtStatus.setText("");

		txtAddress.requestFocus();
	}

	private void setLocation()
	{

		txtLocation.setText("");
		txtStatus.setText("");

		address = txtAddress.getText().toString();

		if (address.isEmpty())
		{
			displayMyAlert(R.string.dialog_message_address);
		}
		else if (!isNetworkConnected(getBaseContext()))
		{
			displayMyAlert(R.string.dialog_message_connection);
		}
		else
		{
			new Worker().execute();
		}
	}

	private String getAddress(String locationName)
	{
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try
		{
			List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
			if (!addresses.isEmpty())
			{
				Address address = addresses.get(0);
				StringBuffer sb = new StringBuffer();
				sb.append(getText(R.string.text_latitude) + ": ");
				sb.append(Double.toString(address.getLatitude()));
				sb.append("\r\n");
				sb.append(getText(R.string.text_longitude) + ": ");
				sb.append(Double.toString(address.getLongitude()));
				sb.append("\r\n\r\n");
				for (int i = 0;  i <= address.getMaxAddressLineIndex(); i++)
				{
					sb.append(address.getAddressLine(i));
					sb.append("\r\n");
				}
				return sb.toString();
			}
		} catch(Exception e) { }
		return null;
	}

	private boolean isNetworkConnected(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null) {
			return network.isAvailable();
		}
		return false;
	}

	private void displayMyAlert(int messageId)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(messageId);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// user cancelled the dialog
			}
		});
		// display message
		builder.create().show();
	}

	private void navigateToLocationActivity()
	{
		Intent intent = new Intent(this, LocationActivity.class);
		startActivity(intent);
	}
	
	private void navigateToAboutActivity()
	{
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}
	
	class Worker extends AsyncTask<Void, Integer, Void>
	{
		private ProgressDialog progress;
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
            progress = ProgressDialog.show(AddressActivity.this, getText(R.string.searching_progress_title), getText(R.string.searching_progress_message), true);
        }

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			progress.dismiss();
			if (address != null)
			{
				txtLocation.setText(address);
			}
			else
			{
				txtStatus.setText(R.string.searching_no_result);
			}
		}

		@Override
		protected Void doInBackground(Void... arg0)
		{
			address = getAddress(address);
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			super.onProgressUpdate(values);
		}
	}
}
