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

import pt.ipp.estg.housecontrol.Server.ServerClass;

public class SensorsFRDManaging {


    private FirebaseDatabase database;
    private DatabaseReference sensorsRef;
    private ServerClass serverClass;

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
                System.out.println("bConnected: "+bConnected);
            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    public void getAllSensors() throws IOException {

        System.out.println("sensorsRef: "+sensorsRef);

        //TODO ver se será necessário este listener, senão, excluir, por ora ñ tem uso, só o child (abaixo)
        sensorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){

//                    Object obj = dataSnapshot.getValue();
//                    System.out.println("object: "+obj);
//
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
                serverClass.sendMessage(dataSnapshot.getValue().toString());

                switch (dataSnapshot.getKey()){
                    case "blinder":
                        System.out.println("blinder getKey: "+dataSnapshot.getKey());
                        System.out.println("blinder getValue: "+dataSnapshot.getValue().toString());
                        break;
                    case "door":
                        System.out.println("door getKey: "+dataSnapshot.getKey());
                        System.out.println("door getValue: "+dataSnapshot.getValue().toString());
                        break;
                    case "hvac":
                        System.out.println("hvac getKey: "+dataSnapshot.getKey());
                        System.out.println("hvac getValue: "+dataSnapshot.getValue().toString());
                        break;
                    case "light":
                        System.out.println("light getKey: "+dataSnapshot.getKey());
                        System.out.println("light getValue: "+dataSnapshot.getValue().toString());
                        break;
                    case "temperature":
                        System.out.println("temperature getKey: "+dataSnapshot.getKey());
                        System.out.println("temperature getValue: "+dataSnapshot.getValue().toString());
                        break;
                }
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

//        sensorsRef.child(sensor).setValue(value, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                if (databaseError != null) {
//                    System.out.println("Data could not be saved " + databaseError.getMessage());
//                } else {
//                    // TODO --> fazer prints no Aj
//                    System.out.println("--> Data saved successfully: "+sensor+" "+value + " - databaseReference: "+ databaseReference);
//                }
//            }
//        });

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

//        String sensorIdent = sensor + "_id";
//
//        sensorsRef.child(sensorIdent).setValue(identifier, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                if (databaseError != null) {
//                    System.out.println("Data could not be saved " + databaseError.getMessage());
//                } else {
//                    // TODO --> fazer prints no Aj
//                    System.out.println("--> Data saved successfully: identifier "+identifier);
//                }
//            }
//        });
//
//        String sensorFrom = sensor + "_from";
//
//        sensorsRef.child(sensorFrom).setValue("server", new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                if (databaseError != null) {
//                    System.out.println("Data could not be saved " + databaseError.getMessage());
//                } else {
//                    // TODO --> fazer prints no Aj
//                    System.out.println("--> Data saved successfully: from server");
//                }
//            }
//        });
    }

}
