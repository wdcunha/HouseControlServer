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
import com.sun.scenario.effect.impl.prism.PrImage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import pt.ipp.estg.housecontrol.Sensors.Sensor;
import pt.ipp.estg.housecontrol.Server.ServerClass;
import static pt.ipp.estg.housecontrol.Sensors.ServerHome.parseData;

public class SensorsFRDManaging {


    private FirebaseDatabase database;
    private static DatabaseReference sensorsRef;
    private ServerClass serverClass;
    private static String sensorValue;

    public SensorsFRDManaging(ServerClass serverClass) throws IOException, FileNotFoundException {

        FileInputStream serviceAccount = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/housecontrolmobile-firebase-adminsdk-qv0hl-0ab5cb2e4d.json");

        // Initialize the app with a service account, granting admin privileges
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://housecontrolmobile.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        database = FirebaseDatabase.getInstance();

//        database.setPersistenceEnabled(true);

        sensorsRef  = database.getReference("Sensor");

        this.serverClass = serverClass;

    }

    public void checkConnection() throws IOException {
        DatabaseReference myReference =
                FirebaseDatabase.getInstance().getReference(".info/connected");
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot myData) {
                boolean bConnected = myData.getValue(Boolean.class);
                // TODO - ver se coloca no aj, mas tem que descomentar a chamada pra este método
                System.out.println("bConnected: "+bConnected);
            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    public void getAllSensors() throws IOException {

        //TODO ver se será necessário este listener, senão, excluir, por ora ñ tem uso, só o child (abaixo)
        sensorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    whatSensor(dataSnapshot.getKey(), dataSnapshot.getValue().toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error trying to get data from FRD: " + error);
            }
        });
    }

    public void getSensorChildFRD() throws IOException {

        sensorsRef.child("mobile").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("SensorsFRDManaging class onChildAdded: " + dataSnapshot+" "+ s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("SensorsFRDManaging class onChildChanged: " + dataSnapshot+" "+ s);

                if (dataSnapshot.exists()) {
                    System.out.println("dataSnapshot.exists");
                }
                serverClass.sendToHomeBus(dataSnapshot.getValue().toString());

                // chama método para verificar que sensor é, como há mais de uma situação, evita repetição de código
                whatSensor(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("SensorsFRDManaging class onChildRemoved: " + dataSnapshot);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println("SensorsFRDManaging class onChildMoved: " + dataSnapshot+" "+ s);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error onCancelled when trying to get data from FRD via ChildEventListener in SensorsFRDManaging class: " + databaseError);

            }
        });    }


    public void writeSensorFRD(String sensor, String value) throws IOException {

        sensorsRef.child("server").child(sensor).setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    // TODO --> fazer prints no Aj
                    System.out.println("--> Data saved successfully: "+sensor+" "+value + " - databaseReference: "+ databaseReference);
                }
            }
        });
    }

    /**
     * Lê apenas um evento do sensor informado, que será carregado como node da pesquisa
     */
    public static Sensor getSensorSingleEvent(String sensorNode) {


        sensorsRef.child("server").child(sensorNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                sensorValue = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println("++++sensorValue: "+sensorValue);

        return parseData(sensorValue);
    }

    public void whatSensor(String key, String value) {
        switch (key){
            // TODO --> fazer prints no Aj
            case "blinder":
                System.out.println("blinder getKey: "+key);
                System.out.println("blinder getValue: "+value);
                break;
            case "door":
                System.out.println("door getKey: "+key);
                System.out.println("door getValue: "+value);
                break;
            case "hvac":
                System.out.println("hvac getKey: "+key);
                System.out.println("hvac getValue: "+value);
                break;
            case "light":
                System.out.println("light getKey: "+key);
                System.out.println("light getValue: "+value);
                break;
            case "temperature":
                System.out.println("temperature getKey: "+key);
                System.out.println("temperature getValue: "+value);
                break;
        }

    }

}
