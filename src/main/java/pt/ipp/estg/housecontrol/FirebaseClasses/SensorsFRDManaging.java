package pt.ipp.estg.housecontrol.FirebaseClasses;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SensorsFRDManaging {


    private static FirebaseDatabase database;
    private static DatabaseReference sensorsRef;

    public SensorsFRDManaging() throws IOException, FileNotFoundException {

        FileInputStream serviceAccount = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/housecontrolmobile-firebase-adminsdk-qv0hl-0ab5cb2e4d.json");

        // Initialize the app with a service account, granting admin privileges
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://housecontrolmobile.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        database = FirebaseDatabase.getInstance();

        sensorsRef  = database.getReference("Sensor");

    }

    public static void getAllSensors() throws IOException {

        System.out.println("sensorsRef: "+sensorsRef);

        sensorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    Object obj = dataSnapshot.getValue();
                    System.out.println("object: "+obj);

                    switch (ds.getKey()){
                        case "blinder":
                            System.out.println("blinder getKey: "+ds.getKey());
                            System.out.println("blinder getValue: "+ds.getValue().toString());
                            break;
                        case "door":
                            System.out.println("door getKey: "+ds.getKey());
                            System.out.println("door getValue: "+ds.getValue().toString());
                            break;
                        case "hvac":
                            System.out.println("hvac getKey: "+ds.getKey());
                            System.out.println("hvac getValue: "+ds.getValue().toString());
                            break;
                        case "light":
                            System.out.println("light getKey: "+ds.getKey());
                            System.out.println("light getValue: "+ds.getValue().toString());
                            break;
                        case "temperature":
                            System.out.println("temperature getKey: "+ds.getKey());
                            System.out.println("temperature getValue: "+ds.getValue().toString());
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error trying to get data from FRD: " + error);
            }
        });
    }

    public static void getSensorChildFRD() throws IOException {

        sensorsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildAdded: " + dataSnapshot+" "+ s);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildChanged: " + dataSnapshot+" "+ s);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("onChildRemoved: " + dataSnapshot);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildMoved: " + dataSnapshot+" "+ s);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error onCancelled when trying to get data from FRD via ChildEventListener in SensorsFRDManaging class: " + databaseError);

            }
        });    }


    public static void writeSensorFRD(String value, String sensor) throws IOException {

        sensorsRef.child(sensor).setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    // TODO --> fazer prints no Aj
                    System.out.println("--> Data saved successfully: "+sensor+" "+value);
                }
            }
        });
    }

}
