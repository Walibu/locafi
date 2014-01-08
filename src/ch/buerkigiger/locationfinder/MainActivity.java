package ch.buerkigiger.locationfinder;

import java.util.Random;

import ch.buerkigiger.locationfinder.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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
				
				if (!strLatitude.isEmpty() && !strLongitude.isEmpty())
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
}
