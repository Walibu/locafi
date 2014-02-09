package ch.buerkigiger.locationfinder;

import java.util.Random;

import ch.buerkigiger.locationfinder.AboutActivity;
import ch.buerkigiger.locationfinder.R;
import ch.buerkigiger.locationfinder.Utility;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LocationActivity extends Activity implements
        LocationHelper.LocationReceiver {

    private EditText txtLatitude;
    private EditText txtLongitude;
    private TextView txtAddress;
    private TextView txtStatus;
    private double latitude;
    private double longitude;
    private String address;
	private LocationHelper locationHelper;
 

    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

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
	protected void onStop() {
        if (locationHelper != null)
        {
          // stop locationHelper in case it is searching
          locationHelper.stopLocationUpdates();
        }
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_about:
				startActivity(new Intent(this, AboutActivity.class));
				return true;
			case R.id.action_address:
				startActivity(new Intent(this, AddressActivity.class));
				return true;
			case R.id.current_position:
				startLocationUpdater();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
    @Override
    public void updateLocation(Location location)
    {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        
        // update position into text fields
        txtLatitude.setText(latitude.toString());
        txtLongitude.setText(longitude.toString());
    }
    
    private void startLocationUpdater()
    {
        if (locationHelper == null)
        {
          locationHelper = new LocationHelper();    
        }
        locationHelper.startLocationUpdates(this, this);
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

		latitude = Utility.getDoubleValue(txtLatitude);
		longitude = Utility.getDoubleValue(txtLongitude);

		if (!Utility.isInRange(latitude, -90.0, 90.0) || !Utility.isInRange(longitude, -180.0, 180.0))
		{
			Utility.displayMessage(R.string.dialog_message_position, this);
		}
		else if (!Utility.isNetworkConnected(getBaseContext()))
		{
			Utility.displayMessage(R.string.dialog_message_connection, this);
		}
		else
		{
			new Worker().execute();
		}
	}

	class Worker extends AsyncTask<Void, Integer, Void>
	{
		private ProgressDialog progress;
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
            progress = ProgressDialog.show(LocationActivity.this, getText(R.string.searching_progress_title), getText(R.string.searching_progress_message), true);
        }

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			progress.dismiss();
			if (address != null)
			{
				txtAddress.setText(address);
			}
			else
			{
				txtStatus.setText(R.string.searching_no_result_address);
			}
		}

		@Override
		protected Void doInBackground(Void... arg0)
		{
			address = Utility.getAddress(latitude, longitude, LocationActivity.this);
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			super.onProgressUpdate(values);
		}
	}
}
