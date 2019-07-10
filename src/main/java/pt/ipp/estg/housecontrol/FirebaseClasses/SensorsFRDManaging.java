package pt.ipp.estg.housecontrol.FirebaseClasses;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
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

        FileInputStream serviceAccount = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/Archive/Exemplo-Firebase 2/src/main/java/security/housecontrolmobile-firebase-adminsdk-qv0hl-30dfe92663.json");

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

        System.out.println("userRef: "+sensorsRef);

        sensorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Utilizador userDataFRD = ds.getValue(Utilizador.class);
                    System.out.println("Nome: " + userDataFRD.getNome());
                    System.out.println("Email: " + userDataFRD.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error trying to get data from FRD: " + error);
            }
        });
    }

    public static void getSensorFRD() throws IOException {

//        sensorsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Object obj = dataSnapshot.getValue();
//                System.out.println("object: "+obj);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });
    }


    public static void writeSensorFRD(String value, String sensor) throws IOException {

//        sensorsRef.child(sensor).setValueAsync(value);

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
