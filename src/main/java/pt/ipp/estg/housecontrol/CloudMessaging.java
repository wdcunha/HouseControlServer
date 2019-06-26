package pt.ipp.estg.housecontrol;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

class CloudMessaging {

    static FirebaseDatabase database;
    static FirebaseApp app;


    private static String[] SCOPES = { "https://www.googleapis.com/auth/firebase.messaging"};

    private static void sendFCMNewData() throws IOException, JSONException {
        URL url = new URL("https://fcm.googleapis.com/v1/projects/housecontrolmobile/messages:send");

        String token = getAccessToken();
        String tokenApp = "";
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
