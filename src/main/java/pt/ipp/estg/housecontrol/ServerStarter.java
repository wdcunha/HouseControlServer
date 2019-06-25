package pt.ipp.estg.housecontrol;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class ServerStarter {

    private static String[] SCOPES = { "https://www.googleapis.com/auth/firebase.messaging"};
    static FirebaseDatabase database;
    static FirebaseApp app;


    protected static void databaseGetInstance() throws IOException {


        DatabaseReference sensorRef  = database.getReference("users");

    }

    public static void main(String[] args) throws IOException, JSONException {

//        sendFCMNewData();
//        databaseGetInstance();

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

    private static void sendFCMNewData() throws IOException, JSONException {
        URL url = new URL("https://fcm.googleapis.com/v1/projects/housecontrolmobile/messages:send");

        String token = getAccessToken();
        String tokenApp = "cuh2hmdJDuw:APA91bHWSn_8izzRva2GFtRQCSzdFOMMDDh8c8KrNml3_3IQ7jWaaqq8nZ8EpSy0o4TKYMJnt8aw2P2myqCQeScjW9_fHeWzRqNqDcvwVQ9ciRQyDIdohoqtlJlPH-tO6DvgErGSsc8A";
        String title = "sixth message from HCServer";
        String message = "Notificação FCM enviada pelo HCServer!";
        String body = "{ \"message\": {\"token\": \""+tokenApp+"\",\"notification\" : {\"title\" : \""+title+"\",\"body\" : \""+message+"\"}}}";
        System.out.println("token: "+token);

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
//        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + token);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");

        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(body.getBytes());

        outputStream.flush();

        StringBuilder sb = new StringBuilder();

        int httpResult = httpURLConnection.getResponseCode();

        if(httpResult == HttpURLConnection.HTTP_OK){
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(),"utf-8")
            );
            String line = null;

            while ((line = bufferedReader.readLine()) != null){
                sb.append(line + "\n");
            }
            System.out.println("Message sent succesfully!!!!");
            bufferedReader.close();
            httpURLConnection.disconnect();
            //return httpURLConnection.toString();

        } else {
            httpURLConnection.disconnect();
            //return httpURLConnection.getResponseMessage();
        }
    }

    private static String getAccessToken() throws IOException {
        GoogleCredential myGoogleCredential = GoogleCredential
                .fromStream(new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/java/resources/housecontrolmobile-firebase-adminsdk-qv0hl-b0b6bf3ff5.json"))
                .createScoped(Arrays.asList(SCOPES));
        myGoogleCredential.refreshToken();
        return myGoogleCredential.getAccessToken();
    }

}
