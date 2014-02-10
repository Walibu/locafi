package ch.buerkigiger.locationfinder;

import ch.buerkigiger.locationfinder.AboutActivity;
import ch.buerkigiger.locationfinder.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddressActivity extends Activity
{

    private EditText txtAddress;
    private TextView txtLocation;
    private TextView txtStatus;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnClear = (Button) findViewById(R.id.buttonClear);
        btnClear.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                clearFields();
            }
        });

        Button btnGetLocation = (Button) findViewById(R.id.buttonLocation);
        btnGetLocation.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
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
        switch (item.getItemId())
        {
        case R.id.action_location:
            startActivity(new Intent(this, LocationActivity.class));
            return true;
        case R.id.action_about:
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        case android.R.id.home:
            // Respond to the action bar's Up/Home button
            NavUtils.navigateUpFromSameTask(this);
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
            Utility.displayMessage(R.string.dialog_message_address, this);
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
            progress = ProgressDialog.show(AddressActivity.this,
                    getText(R.string.searching_progress_title),
                    getText(R.string.searching_progress_message), true);
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
                txtStatus.setText(R.string.searching_no_result_location);
            }
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            address = Utility.getLocation(address, AddressActivity.this);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
        }
    }
}
