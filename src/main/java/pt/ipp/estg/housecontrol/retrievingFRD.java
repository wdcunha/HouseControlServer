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
import java.io.IOException;

public class retrievingFRD {


    private static DatabaseReference database;

    public static void recuperarUsuarioDoBanco(){

        System.out.println("Got in recuperarUsuarioDoBanco");

        database.child("users").child("2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Got onDataChange");
                Object document = dataSnapshot.getValue();
                System.out.println(document);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Got an error: "+error);
            }
        });
    }

    public static void addValueListener() {

        System.out.println("inside addValueListener");

        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("inside onDataChange");

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


    public static void main(String[] args) throws IOException {

        FileInputStream serviceAccount = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/housecontrolmobile-firebase-adminsdk-qv0hl-b0b6bf3ff5.json");

        // Initialize the app with a service account, granting admin privileges
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://housecontrolmobile.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        database = FirebaseDatabase.getInstance().getReference();

//        recuperarUsuarioDoBanco();

        addValueListener();
    }

}
