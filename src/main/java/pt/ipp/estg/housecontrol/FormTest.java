package pt.ipp.estg.housecontrol;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

class FormTest {


    private JPanel testePanel;
    private JButton UserBtn;
    private JButton AllUsersBtn;
    private JButton UserValueBtn;
    private JButton WriteUserBtn;
    private static DatabaseReference database;

    public FormTest() throws IOException {


        UserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                database.child("users").child("1").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println("onDataChange");
                        Object document = dataSnapshot.getValue();
                        System.out.println(document);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println("Got an error: "+error);
                    }
                });
            }
        });

        AllUsersBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                System.out.println("button2 addValueListener");

                database.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        System.out.println("button2 onDataChange");

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
        });


        UserValueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

//                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
//                    DatabaseReference usersRef = database.getReference("/users");

                    database.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            System.out.println(dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                } catch (Exception error) {
                    System.out.println(error);
                }        // TODO add your handling code here:
            }
        });


        WriteUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                Utilizador user = new Utilizador("Wellington", "qq@qq.com");

                String userId2 = "2";

                database.child("users").child(userId2).setValueAsync(user);

                String userId = "3";


                database.child("users").child(userId).setValue("another text", new DatabaseReference.CompletionListener() {
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
        });
    }

    public static void initFRD() throws IOException {

        FileInputStream serviceAccount = new FileInputStream("/Users/wdcunha/ESTG/2osemestre/Des Web/TrabalhoFinal/HouseControlServer/src/main/resources/housecontrolmobile-firebase-adminsdk-qv0hl-b0b6bf3ff5.json");

        // Initialize the app with a service account, granting admin privileges
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://housecontrolmobile.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        database = FirebaseDatabase.getInstance().getReference();

    }


    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("FormTest");
        frame.setContentPane(new FormTest().testePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


        try {
            initFRD();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
