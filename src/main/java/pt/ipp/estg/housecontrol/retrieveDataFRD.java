package pt.ipp.estg.housecontrol;

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

public class retrieveDataFRD {


    private static DatabaseReference database;
    static Object obj;

    public static Object recuperarUsuarioDoBanco(){

        database.child("Sensor").child("blinder");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Got onDataChange");
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    obj = dataSnapshot.getValue();
                    System.out.println("obj: "+obj);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                System.out.println("Error trying to read user: "+error);
            }
        };

        database.addValueEventListener(userListener);

        return obj;
    }

    public static void main(String[] args) throws IOException {

        initFRD();

        database = FirebaseDatabase.getInstance().getReference();

        String userId = "1";
        System.out.println("finally, I hope: "+recuperarUsuarioDoBanco());
    }

    public static void initFRD() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/housecontrolmobile-firebase-adminsdk-qv0hl-b0b6bf3ff5.json");

        // Initialize the app with a service account, granting admin privileges
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://housecontrolmobile.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

    }

}
