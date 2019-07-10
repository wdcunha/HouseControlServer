package pt.ipp.estg.housecontrol.FirebaseClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

class CloudMessaging {

    private static String[] SCOPES = { "https://www.googleapis.com/auth/firebase.messaging"};

    private static String title;
    private static String message;


    public CloudMessaging(String title, String message) {
        this.title = title;
        this.message = message;

        try {
            sendFCMNewData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendFCMNewData() throws IOException, JSONException {
        
        URL url = new URL("https://fcm.googleapis.com/v1/projects/housecontrolmobile/messages:send");

        FileInputStream appToken = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/appToken");

        String tokenApp_t = appToken.toString();
        String nomeArquivo = "/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/appToken";

        title = "sixth message from HCServer";
        message = "Notificação FCM enviada pelo HCServer!";

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
//        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + token);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");

        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(body.getBytes());

        outputStream.flush();

        StringBuilder sb = new StringBuilder();

        int httpResult = httpURLConnection.getResponseCode();

        Boolean cond = false;

        if(httpResult == HttpURLConnection.HTTP_OK){
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(),"utf-8")
            );

            String line = null;

            while ((line = bufferedReader.readLine()) != null){
                sb.append(line + "\n");
            }

//            System.out.println(getCurrentDate()+"Message sent succesfully!!!!");
            bufferedReader.close();
            httpURLConnection.disconnect();
            //return httpURLConnection.toString();
            cond = true;

            return cond;
        } else {
            httpURLConnection.disconnect();
            //return httpURLConnection.getResponseMessage();
            cond = false;

            return cond;
        }

    }

    private static String getAccessToken() throws IOException {
        GoogleCredential myGoogleCredential = GoogleCredential
                .fromStream(new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/housecontrolmobile-firebase-adminsdk-qv0hl-b0b6bf3ff5.json"))
                .createScoped(Arrays.asList(SCOPES));
        myGoogleCredential.refreshToken();
        return myGoogleCredential.getAccessToken();
    }


    public static void main(String[] args) throws IOException, JSONException {

        sendFCMNewData();
    }
}
