package pt.ipp.estg.housecontrol.FirebaseClasses;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

public class DataBaseFRDManaging {


    private static FirebaseDatabase database;
    private static DatabaseReference usersRef;

    public DataBaseFRDManaging() throws IOException, FileNotFoundException {

        FileInputStream serviceAccount = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/Archive/Exemplo-Firebase 2/src/main/java/security/housecontrolmobile-firebase-adminsdk-qv0hl-30dfe92663.json");

        // Initialize the app with a service account, granting admin privileges
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://housecontrolmobile.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        database = FirebaseDatabase.getInstance();

        usersRef  = database.getReference("users");

    }

    public static void getAllUsers() throws IOException {

        System.out.println("userRef: "+usersRef);

        usersRef.addValueEventListener(new ValueEventListener() {
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

    public static void getUserFRD() throws IOException {

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object obj = dataSnapshot.getValue();
                System.out.println("object: "+obj);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    public static void writeUserFRD() throws IOException {

        Utilizador user = new Utilizador("Wellington", "qq@qq.com");

        String userId2 = "2";

        usersRef.child(userId2).setValueAsync(user);

        String userId = "3";

//        usersRef.child(userId).setValue("another text", new DatabaseReference.CompletionListener() {
        usersRef.push().setValue("another text", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });

    }


    public static void main(String[] args) throws IOException, JSONException {

        getAllUsers();
    }

}
