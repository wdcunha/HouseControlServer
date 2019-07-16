package pt.ipp.estg.housecontrol.FirebaseClasses;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


public class CloudMessagingClass {

    private static String[] SCOPES = { "https://www.googleapis.com/auth/firebase.messaging"};

    public CloudMessagingClass() {

    }

    public static boolean sendFCMNewData(String title, String message) throws IOException, JSONException {
        
        URL url = new URL("https://fcm.googleapis.com/v1/projects/housecontrolmobile/messages:send");

        FileInputStream appToken = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/appToken");

        String tokenApp_t = appToken.toString();
        String nomeArquivo = "/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/appToken";

        String tokenApp = "";
        Path path = Paths.get(nomeArquivo);

        List<String> allLines = Files.readAllLines(path);

        for (String line : allLines) {
            tokenApp = line.replace("token: ","");

        }

        String token = getAccessToken();

        String body = "{ \"message\": {\"token\": \""+tokenApp+"\",\"notification\" : {\"title\" : \""+title+"\",\"body\" : \""+message+"\"}}}";

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + token);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");

        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(body.getBytes());

        outputStream.flush();

        StringBuilder sb = new StringBuilder();

        int httpResult = httpURLConnection.getResponseCode();

        boolean cond = false;

        if(httpResult == HttpURLConnection.HTTP_OK){
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(),"utf-8")
            );

            String line = null;

            while ((line = bufferedReader.readLine()) != null){
                sb.append(line + "\n");
            }

            bufferedReader.close();
            httpURLConnection.disconnect();
            cond = true;

            return cond;
        } else {
            httpURLConnection.disconnect();
            cond = false;

            return cond;
        }

    }

    /**
     * Token necessário para o envio de notificação pelo FCM, que dá o privilégio necessário
     */

    private static String getAccessToken() throws IOException {
        GoogleCredential myGoogleCredential = GoogleCredential
                .fromStream(new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/housecontrolmobile-firebase-adminsdk-qv0hl-0ab5cb2e4d.json"))
                .createScoped(Arrays.asList(SCOPES));
        myGoogleCredential.refreshToken();

        return myGoogleCredential.getAccessToken();
    }

}
