package optimun.softstarter;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btiniciar, btparar, btemergencia;
    int progresso;
    String stempo = "00";
    SeekBar seekBar;
    TextView tempo;
    ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    boolean isBtConnected = false;
    String address = null;
    //SPP UUID. Look for it
    final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btiniciar = (Button) findViewById(R.id.btiniciar);
        btparar = (Button) findViewById(R.id.btparar);
        btemergencia = (Button) findViewById(R.id.btemergencia);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tempo = (TextView) findViewById(R.id.txt);

        Intent newint = getIntent();
        address = "98:D3:31:30:42:9F";
        new ConnectBT().execute();

        btiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subida();      //method to turn on
            }
        });

        btparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descida();      //method to turn on
            }
        });
        btemergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergencia();      //method to turn on
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                progresso = progress;
                if(progress<10){
                    stempo = "0"+String.valueOf(progress);
                }
                else{
                    stempo = String.valueOf(progress);
                }
                tempo.setText(String.valueOf(5+progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void descida() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(("d"+stempo).toString().getBytes());
            } catch (IOException e) {
                msg("Erro");
            }
        }
    }

    private void subida() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(("s"+stempo).toString().getBytes());
            } catch (IOException e) {
                msg("Erro");
            }
        }
    }

    private void emergencia() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("e00".toString().getBytes());
            } catch (IOException e) {
                msg("Erro");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Conectando...", "Aguarde");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Erro. Ligue o bluetooth do aparelho e do modulo");
                finish();
            } else {
                msg("Conectado.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}

