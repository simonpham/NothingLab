package com.simonvn.nothinglab;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private TextView profileName;
    private TextView nickname;
    private CircleImageView imgAvatar;
    private CircleImageView imgAvatar2;
    private FirebaseAuth auth;
    private RelativeLayout rlInfo;
    private RelativeLayout rlChat;
    private EditText editName;
    private Button btnToogle;
    private Button btnDeleteUser;
    private KeyListener listener;
    private Drawable bgName;

    ListView list;
    MyMessageAdapter adapter;
    public MainActivity mainActivity = this;
    public ArrayList<ChatMessage> listOfMessages = new ArrayList<ChatMessage>();


    private boolean isAllowed = true;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private String lname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if(!isUserLogin()){signOut();}

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivity = this;

        // send button
        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = (EditText)findViewById(R.id.input);
                if (!Objects.equals(input.getText().toString().trim(), "")) {

                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database

                    db.collection("messages")
                            .add(new ChatMessage(input.getText().toString(), nickname.getText().toString(), getUserUID()))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                    // add new msg

                    ChatMessage msg2 = new ChatMessage();

                    msg2.setMessageUser(nickname.getText().toString());
                    msg2.setMessageText(input.getText().toString());
                    msg2.setMessageTime(new Date().getTime());
                    msg2.setMessageUID(getUserUID());

                    listOfMessages.add(msg2);
                    adapter.notifyDataSetChanged();

                    // Clear the input
                    input.setText("");
                }
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // select info on startup
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        View navHeader = navigationView.getHeaderView(0);
        //View appbarmain = findViewById(R.id.)


        // init view
        profileName = (TextView)navHeader.findViewById(R.id.user_name);
        nickname = (TextView)navHeader.findViewById(R.id.usernickname);
        imgAvatar = (CircleImageView)navHeader.findViewById(R.id.imgAvatar);
        imgAvatar2 = (CircleImageView)findViewById(R.id.imgAvatar2);
        editName = (EditText)findViewById(R.id.editName);
        btnToogle = (Button)findViewById(R.id.btnToggle);
        btnDeleteUser = (Button)findViewById(R.id.btnDeleteUser);
        list = (ListView)findViewById(R.id.list_of_messages);


        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDelete();
            }
        });
        btnToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEditName();
            }
        });

        displayUserNickname();
        displayLoginUserProfileName();
        displayUserAvatar(getAvatarPath());
        displayNavBG();

        displayChatMessages();

        Resources res = getResources();
        adapter = new MyMessageAdapter(mainActivity, listOfMessages, res, getUserUID());
        list.setAdapter(adapter);

        // backup
        listener = editName.getKeyListener();
        bgName = editName.getBackground();
        lname = editName.getText().toString();

        isAllowed = false;
        btnToogle.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_edit, 0, 0);
        editName.setCursorVisible(false);
        editName.setLongClickable(false);
        editName.setClickable(false);
        editName.setFocusable(false);
        editName.setFocusableInTouchMode(false);
        editName.setSelected(false);
        editName.setKeyListener(null);
        editName.setBackgroundResource(android.R.color.transparent);

        imgAvatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAvatar();
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettings();
            return true;
        } else if (id == R.id.homeAsUp) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        rlInfo = (RelativeLayout)findViewById(R.id.rlayoutInfo);
        rlChat = (RelativeLayout)findViewById(R.id.rlayoutChat);
        int id = item.getItemId();

        if (id == R.id.nav_info) {
            rlInfo.setVisibility(View.VISIBLE);
            rlChat.setVisibility(View.GONE);
            setTitle(item.getTitle());
        } else if (id == R.id.nav_chat) {
            rlInfo.setVisibility(View.GONE);
            rlChat.setVisibility(View.VISIBLE);
            setTitle(item.getTitle());
        } else if (id == R.id.nav_settings) {
            showSettings();
        } else if (id == R.id.nav_about) {
            showAbout();
        } else if (id == R.id.nav_logout) {
            userLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isUserLogin(){
        if(auth.getCurrentUser() != null){
            return true;
        }
        return false;
    }
    private void signOut(){
        Intent signOutIntent = new Intent(this, SigninActivity.class);
        startActivity(signOutIntent);
        finish();
    }
    private void displayMessage(String message){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(),message,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    private void displayLoginUserProfileName(){
        FirebaseUser mUser = auth.getCurrentUser();

        if(mUser != null){
            profileName.setText(TextUtils.isEmpty(mUser.getDisplayName())? "No name found" : mUser.getDisplayName());
        }
    }

    private void updateName(String newname) {
        Map<String, Object> nickN = new HashMap<>();
        nickN.put("nickname", newname);
        db.collection("users").document(getUserUID())
                .set(nickN)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        displayUserNickname();
                        Toast snack = Toast.makeText(getWindow().getDecorView().getRootView().getContext(), "Changed nickname successfully!", Toast.LENGTH_SHORT);
                        snack.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast snack = Toast.makeText(getWindow().getDecorView().getRootView().getContext(), "Change nickname failed!", Toast.LENGTH_SHORT);
                        snack.show();
                    }
                });
    }

    private void displayUserNickname() {
        db.collection("users")
                .document(getUserUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    nickname.setText(document.getString("nickname"));
                                    editName.setText(document.getString("nickname"));
                                } else {
                                    updateName(auth.getCurrentUser().getDisplayName());
                                }
                            } else {
                                if (document.exists()) {
                                    nickname.setText(document.getString("nickname"));
                                    editName.setText(document.getString("nickname"));
                                } else {
                                    updateName(auth.getCurrentUser().getDisplayName());
                                }
                            }
                        } else {
                            nickname.setText("get failed with " + task.getException());
                            editName.setText("get failed with " + task.getException());
                        }
                    }
                });
    }

    private void displayUserAvatar(String avatarPath) {
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(avatarPath);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgAvatar.setImageBitmap(bitmap);
                imgAvatar.setBorderOverlay(true);
                imgAvatar.setBorderWidth(R.dimen.onedp);
                imgAvatar2.setImageBitmap(bitmap);
                imgAvatar2.setBorderOverlay(true);
                imgAvatar2.setBorderWidth(R.dimen.onedp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                initUserAvatar();
            }
        });
    }
    private void displayNavBG() {
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("asserts/nav_bg.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                BitmapDrawable bgg = new BitmapDrawable(getResources(), bitmap);
                LinearLayout bgNav = findViewById(R.id.bgNav);
                bgNav.setBackground(bgg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // do nothing
            }
        });
    }
    private void initUserAvatar() {
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("/users/defaultUser/defaultAvatar.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                updateUserAvatar(bytes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //
            }
        });

    }

    private void updateUserAvatar(byte[] bytes) {
        StorageReference storageRef = storage.getReference();
        StorageReference mountainImagesRef = storageRef.child(getAvatarPath());
        UploadTask uploadTask = mountainImagesRef.putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                displayUserAvatar(getAvatarPath());
            }
        });
    }

    private void changeAvatar() {
        Intent image = new Intent(Intent.ACTION_GET_CONTENT);
        image.setType("Image/*");
        startActivityForResult(image, 1);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == 1 && data != null && data.getData() != null){
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                updateUserAvatar(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getAvatarPath() {
        return "users/" + getUserUID() + "/profilePic/avatar.jpg";
    }

    private String getUserUID() {
        FirebaseUser mUser = auth.getCurrentUser();
        String uid;
        uid = "";

        if(mUser != null){
            uid = mUser.getUid();
        }
        return uid;
    }

    private void userLogout() {
        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            signOut();
                        }else {
                            displayMessage(getString(R.string.sign_out_error));
                        }
                    }
                });
    }

    private void userDelete() {
        final String tempID = getUserUID();
        final String tempAv = getAvatarPath();

        auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    StorageReference storageRef2 = storage.getReference();
                    StorageReference desertRef = storageRef2.child(tempAv);
                    StorageReference desertRef2 = storageRef2.child("users/" + tempID);
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });
                    desertRef2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });

                    db.collection("users").document(tempID).delete();
                    signOut();
                }else{
                    displayMessage(getString(R.string.user_deletion_error));
                }
            }
        });
    }

    private void showSettings() {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    private void showAbout() {
        Intent about = new Intent(this, AboutActivity.class);
        startActivity(about);
    }

    private void toggleEditName() {
        isAllowed = !isAllowed;
        if (isAllowed) {
            lname = editName.getText().toString();
            btnToogle.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_done, 0, 0);
            editName.setCursorVisible(true);
            editName.setLongClickable(true);
            editName.setClickable(true);
            editName.setFocusable(true);
            editName.setFocusableInTouchMode(true);
            editName.setSelected(true);
            editName.setKeyListener(listener);
            editName.setBackground(bgName);
        } else {
            if (!(editName.getText().toString().equals(lname))) {
                updateName(editName.getText().toString());
            }
            btnToogle.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_edit, 0, 0);
            editName.setCursorVisible(false);
            editName.setLongClickable(false);
            editName.setClickable(false);
            editName.setFocusable(false);
            editName.setFocusableInTouchMode(false);
            editName.setSelected(false);
            editName.setKeyListener(null);
            editName.setBackgroundResource(android.R.color.transparent);
        }
    }

    private void displayChatMessages() {
        db.collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                ChatMessage msg = new ChatMessage();

                                msg.setMessageUser(document.getString("messageUser"));
                                msg.setMessageText(document.getString("messageText"));
                                msg.setMessageTime(document.getLong("messageTime"));
                                msg.setMessageUID(document.getString("messageUID"));

                                listOfMessages.add(msg);

                                Collections.sort(listOfMessages, new Comparator<ChatMessage>() {
                                    public int compare(ChatMessage o1, ChatMessage o2) {

                                        //DateFormat.format("dd-MM-yyyy (HH:mm:ss)" o1.getMessageTime()
                                        //SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd"); o2.getMessageTime()
                                        //Date date = new Date(Long.parseLong(str));
                                        // DateFormat.format("dd-MM-yyyy (HH:mm:ss)",o2.getMessageTime())

                                        String datetime1 = DateFormat.format("yyyyMMddHHmmss",o1.getMessageTime()).toString() ;
                                        String datetime2 = DateFormat.format("yyyyMMddHHmmss",o2.getMessageTime()).toString() ;
                                        return datetime1.compareTo(datetime2);
                                    }
                                });

                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void onItemClick(int mPosition)
    {
        ChatMessage tempValues = ( ChatMessage ) listOfMessages.get(mPosition);

    }

    // method
}
