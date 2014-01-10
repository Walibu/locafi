package ch.buerkigiger.locationfinder;

import java.util.Random;

import ch.buerkigiger.locationfinder.AboutActivity;
import ch.buerkigiger.locationfinder.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
				EditText txtLatitude = (EditText) findViewById(R.id.editTextLatitude);
				EditText txtLongitude = (EditText) findViewById(R.id.editTextLongitude);

				String strLatitude = txtLatitude.getText().toString();
				String strLongitude = txtLongitude.getText().toString();

				// clear previous address, TODO clear only if position has changed
				setAddress("");

				if (strLatitude.isEmpty() || strLongitude.isEmpty())
				{
					displayMyAlert(R.string.dialog_message_position);
				}
				else if (!isNetworkConnected(getBaseContext()))
				{
					displayMyAlert(R.string.dialog_message_connection);
				}
				else
				{
					double latitude = Double.parseDouble(strLatitude);
					double longitude = Double.parseDouble(strLongitude);
					setAddress(getAddress(latitude, longitude));
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_about:
			navigateToAboutActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setRandom() {
		EditText txtLatitude = (EditText) findViewById(R.id.editTextLatitude);
		EditText txtLongitude = (EditText) findViewById(R.id.editTextLongitude);
		Random random = new Random();
		txtLatitude.setText(Double.toString((random.nextDouble() * 180) - 90));
		txtLongitude.setText(Double.toString((random.nextDouble() * 360) - 180));
	}

	private String getAddress(double latitude, double longitude) {
		StringBuffer sb = new StringBuffer();
		sb.append("Oberseestrasse 10\r\n");
		sb.append("8650 Rapperswil\r\n");
		return sb.toString();
	}

	private void setAddress(String address) {
		TextView txtAddress = (TextView) findViewById(R.id.textAddress);
		txtAddress.setText(address);
	}

	private boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null) {
			return network.isAvailable();
		}
		return false;
	}

	private void displayMyAlert(int messageId) {
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

	private void navigateToAboutActivity() {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}
}
