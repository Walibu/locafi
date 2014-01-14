package ch.buerkigiger.locationfinder;

import java.util.List;
import java.util.Locale;
import java.util.Random;

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

public class MainActivity extends Activity {

    private EditText txtLatitude;
    private EditText txtLongitude;
    private TextView txtAddress;
    private TextView txtStatus;
    private double latitude;
    private double longitude;
    private String address;
 

    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btnRandom = (Button) findViewById(R.id.buttonRandom);
		btnRandom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				setRandom();
			}
		});

		Button btnGetAddress = (Button) findViewById(R.id.buttonAddress);
		btnGetAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				setAddress();
			}
		});

		txtLatitude = (EditText) findViewById(R.id.editTextLatitude);
		txtLongitude = (EditText) findViewById(R.id.editTextLongitude);
		txtAddress = (TextView) findViewById(R.id.textAddress);
		txtStatus = (TextView) findViewById(R.id.textStatus);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case R.id.action_about:
			navigateToAboutActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setRandom()
	{

		txtAddress.setText("");
		txtStatus.setText("");

		Random random = new Random();
		txtLatitude.setText(Double.toString((random.nextDouble() * 180) - 90));
		txtLongitude.setText(Double.toString((random.nextDouble() * 360) - 180));
	}

	private void setAddress()
	{

		txtAddress.setText("");
		txtStatus.setText("");

		latitude = getDoubleValue(txtLatitude);
		longitude = getDoubleValue(txtLongitude);

		if (!isInRange(latitude, -90.0, 90.0) || !isInRange(longitude, -180.0, 180.0))
		{
			displayMyAlert(R.string.dialog_message_position);
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

	private String getAddress(double latitude, double longitude)
	{
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try
		{
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			if (!addresses.isEmpty())
			{
				Address address = addresses.get(0);
				StringBuffer sb = new StringBuffer();
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

	private void navigateToAboutActivity()
	{
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}

	private static double getDoubleValue(EditText textField)
	{
		String strValue = textField.getText().toString();
		if (strValue.isEmpty() || strValue.isEmpty())
		{
			return Double.MAX_VALUE;
		}
		return Double.parseDouble(strValue);
	}

	
	private static boolean isInRange(double value, double minValue, double maxValue)
	{
		return value >= minValue && value <= maxValue;
	}
	
	class Worker extends AsyncTask<Void, Integer, Void>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			txtStatus.setText(R.string.searching_start);
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			if (address != null)
			{
				txtAddress.setText(address);
				txtStatus.setText("");
			} else {
				txtStatus.setText(R.string.searching_no_result);
			}
		}

		@Override
		protected Void doInBackground(Void... arg0)
		{
			address = getAddress(latitude, longitude);
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			super.onProgressUpdate(values);
		}
	}
}
