package com.sujin.nearbymessages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;


public class MainActivity extends Activity {

    Button button1,button2,button3;
    ConnectionLifecycleCallback mConnectionLifecycleCallback;
    EndpointDiscoveryCallback endpointDiscoveryCallback;
    PayloadCallback payloadCallback;
    String newEndpoint;
    ArrayList<String> connectedDevices,availableDevices;
    ListView listView;
    String myRole="",status = "selection";
    ArrayAdapter connectedArrayAdapter,availableArrayAdapter;
    TextView heading;
    int noOfDevices = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectedDevices = new ArrayList<>();
        availableDevices = new ArrayList<>();

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        heading = findViewById(R.id.heading);
        listView = findViewById(R.id.listView);

        connectedArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,connectedDevices);
        availableArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,availableDevices);


        /*connectedDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String toEndpointId = connectedDevices.get(i);
                String message = "This is the message!";
                byte[] bArray = message.getBytes();
                Payload bytesPayload = Payload.fromBytes(bArray);
                Nearby.getConnectionsClient(getApplicationContext()).sendPayload(toEndpointId, bytesPayload);
            }
        });*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(myRole.equals("discoverer")&& status.equals("connection"))
                {
                    Nearby.getConnectionsClient(getApplicationContext())
                            .requestConnection("user1", availableDevices.get(i), mConnectionLifecycleCallback)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    //Toast.makeText(MainActivity.this, "Connected bruh!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                }
            }
        });


        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status.equals("selection"))
                {
                    myRole = "advertiser";
                    heading.setText("Advertising...");
                    startAdvertising();
                    listView.setAdapter(connectedArrayAdapter);
                    button1.setVisibility(View.INVISIBLE);
                    button3.setVisibility(View.INVISIBLE);
                    status="connection";

                }

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status.equals("selection"))
                {
                    myRole = "discoverer";
                    heading.setText("Discovering...");
                    startDiscovery();
                    listView.setAdapter(availableArrayAdapter);
                    button1.setVisibility(View.INVISIBLE);
                    button3.setVisibility(View.INVISIBLE);
                    status="connection";
                }
            }
        });

        payloadCallback = new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {

                byte[] receivedBytes = payload.asBytes();
                String message = new String(receivedBytes);
                Toast.makeText(MainActivity.this, "Message from "+s+" "+message, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

            }
        };

        endpointDiscoveryCallback =
                new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                        // An endpoint was found. We request a connection to it.
                        availableDevices.add(endpointId);
                        availableArrayAdapter.notifyDataSetChanged();
                        /*Nearby.getConnectionsClient(getApplicationContext())
                                .requestConnection("user1", endpointId, mConnectionLifecycleCallback)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(MainActivity.this, "Connected bruh!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });*/
                    }

                    @Override
                    public void onEndpointLost(String endpointId) {
                        // A previously discovered endpoint has gone away.
                        Toast.makeText(MainActivity.this, "Gone!", Toast.LENGTH_SHORT).show();
                        availableDevices.remove(endpointId);
                        availableArrayAdapter.notifyDataSetChanged();
                    }
                };

         mConnectionLifecycleCallback =
                new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(final String endpointId, ConnectionInfo info) {

                        newEndpoint = endpointId;
                        AlertDialog.Builder builder
                                = new AlertDialog
                                .Builder(MainActivity.this);
                        // Set the message show for the Alert time
                        builder.setMessage("Confirm the code matches on both devices: " + info.getAuthenticationToken());
                        // Set Alert Title
                        builder.setTitle("Accept connection to " + info.getEndpointName());
                        // Set Cancelable false
                        // for when the user clicks on the outside
                        // the Dialog Box then it will remain show
                        builder.setCancelable(false);
                        // Set the positive button with yes name
                        // OnClickListener method is use of
                        // DialogInterface interface.
                        builder
                                .setPositiveButton(
                                        "Yes",
                                        new DialogInterface
                                                .OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which)
                                            {

                                                Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(newEndpoint, payloadCallback);

                                            }
                                        });
                        // Set the Negative button with No name
                        // OnClickListener method is use
                        // of DialogInterface interface.
                        builder
                                .setNegativeButton(
                                        "No",
                                        new DialogInterface
                                                .OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which)
                                            {

                                                // If user click no
                                                // then dialog box is canceled.
                                                Nearby.getConnectionsClient(getApplicationContext()).rejectConnection(newEndpoint);
                                            }
                                        })
                                .setIcon(android.R.drawable.ic_dialog_alert);

                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();
                        // Show the Alert Dialog box
                        alertDialog.show();

                    }

                    @Override
                    public void onConnectionResult(String endpointId, ConnectionResolution result) {
                        switch (result.getStatus().getStatusCode()) {
                            case ConnectionsStatusCodes.STATUS_OK:
                                // We're connected! Can now start sending and receiving data.
                                //Toast.makeText(MainActivity.this, "Connected Yeah!", Toast.LENGTH_SHORT).show();
                                if(myRole.equals("discoverer") && status.equals("connection"))
                                {
                                    Toast.makeText(MainActivity.this, "Connected Successfully !", Toast.LENGTH_SHORT).show();
                                    button2.setVisibility(View.VISIBLE);
                                    button2.setText("Play Now!");
                                    status="connected";
                                    availableDevices.clear();
                                    availableArrayAdapter.notifyDataSetChanged();
                                    stopDiscovery();
                                    heading.setText("Connected!");
                                } else if (myRole.equals("advertiser") && status.equals("connection"))
                                {
                                    connectedDevices.add(endpointId);
                                    connectedArrayAdapter.notifyDataSetChanged();
                                    noOfDevices++;
                                    if(noOfDevices==2)
                                    {
                                        button2.setVisibility(View.VISIBLE);
                                        button2.setText("Play Now !");
                                        status = "connected";
                                        heading.setText("Connected!");
                                        startAdvertising();
                                    }

                                }

                                break;
                            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                                // The connection was rejected by one or both sides.
                                break;
                            case ConnectionsStatusCodes.STATUS_ERROR:
                                // The connection broke before it was able to be accepted.
                                break;
                            default:
                                // Unknown status code
                        }
                    }

                    @Override
                    public void onDisconnected(String endpointId) {
                        // We've been disconnected from this endpoint. No more data can be
                        // sent or received.
                        Toast.makeText(MainActivity.this, "Gone "+endpointId , Toast.LENGTH_SHORT).show();
                        connectedDevices.remove(endpointId);
                        connectedArrayAdapter.notifyDataSetChanged();
                    }
                };



    }


    private void startAdvertising() {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(getApplicationContext())
                .startAdvertising(
                        "user2", "com.sujin.nearbymessages", mConnectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(MainActivity.this, "Advertising Started!", Toast.LENGTH_SHORT).show();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                );
    }

    private void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(getApplicationContext())
                .startDiscovery("com.sujin.nearbymessages", endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Discovery Started!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void stopDiscovery()
    {
        Nearby.getConnectionsClient(getApplicationContext()).stopDiscovery();
    }

    private void stopAdvetising()
    {
        Nearby.getConnectionsClient(getApplicationContext()).stopAdvertising();
    }
}
