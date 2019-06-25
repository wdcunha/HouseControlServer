package pt.ipp.estg.housecontrol;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;

class DataBaseFRDManaging {

    FileInputStream serviceAccount = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/java/resources/housecontrolmobile-firebase-adminsdk-qv0hl-b0b6bf3ff5.json");

    // Initialize the app with a service account, granting admin privileges
    FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://housecontrolmobile.firebaseio.com/")
            .build();
        FirebaseApp.initializeApp(options);

    database = FirebaseDatabase.getInstance();

    DatabaseReference usersRef  = database.getReference("users");

        System.out.println("userRef: "+usersRef);

    Utilizador userDataFRD = new Utilizador();

        System.out.println("got here outside onDataChange");

        usersRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            System.out.println("got here inside onDataChange");

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

    Utilizador user = new Utilizador("Wellington", "qq@qq.com");

    String userId2 = "2";

        usersRef.child(userId2).setValueAsync(user);

    String userId = "3";


        usersRef.child(userId).setValue("another text", new DatabaseReference.CompletionListener() {
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
}
