package ec.edu.tecnologicoloja.donled;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

import static android.media.session.PlaybackState.STATE_CONNECTING;

public class MainActivity extends AppCompatActivity {


    private static final int RESPONSE_MESSAGE = 10;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;

    private BluetoothSocket socket = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);
        ApagaLedBT();
        EnciendLedBT();
    }
    public void on(View v){
        if (!BA.isEnabled()) {
            //Levanta el adaptador
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }


    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }


    public void list(View v){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }


    //Envia los datos para encender el led
    private void EnciendLedBT(){
        if(socket!=null)
        {
            try{
               socket.getOutputStream().write("1".getBytes());
            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Error al encender el led",Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Envia los datos para apagar el led
    private void ApagaLedBT(){
        if (socket != null) {
            try {
                socket.getOutputStream().write("0".getBytes());
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error apagar el led", Toast.LENGTH_SHORT).show();
            }
        }

    }


    /*Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            return true;
        }

    }*/

        //Enviar datos
        class SendReceive extends Thread {
            private final BluetoothSocket bluetoothSocket;
            private final InputStream inputStream;
            private final OutputStream outputStream;

            public SendReceive(BluetoothSocket socket) {
                bluetoothSocket = socket;
                InputStream tempIn = null;
                OutputStream tempOut = null;

                try {
                    tempIn = bluetoothSocket.getInputStream();
                    tempOut = bluetoothSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputStream = tempIn;
                outputStream = tempOut;
            }
        }
    }
