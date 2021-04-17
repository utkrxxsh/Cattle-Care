package com.cattle_care.cattlecare;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class userForm extends AppCompatActivity{

    Spinner dropdown;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView username,userphone;
    EditText locationDetails,description;
    Intent data;
    Button submit;
    String numOfCattles;
    String imagePathInFireStore="";
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    ImageView selectedImage,tick;
    ImageButton cameraBtn,galleryBtn;
    String currentPhotoPath;
    StorageReference storageReference;
    ProgressBar imageUploading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        username=findViewById(R.id.username);
        userphone=findViewById(R.id.userphone);
        locationDetails=findViewById(R.id.locationDetails);
        description=findViewById(R.id.description);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        submit=findViewById(R.id.submit);
        data=getIntent();
        final String[] name = new String[1];
        final String[] mobileNumber = new String[1];
        selectedImage=findViewById(R.id.imageView);
        cameraBtn=findViewById(R.id.camera);
        galleryBtn=findViewById(R.id.gallery);
        storageReference= FirebaseStorage.getInstance().getReference();
        imageUploading=findViewById(R.id.progressBar);
        tick=findViewById(R.id.tick);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

        dropdown=findViewById(R.id.spinner);
        String[] items = new String[]{"1","2","3","4","5","more than five"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                numOfCattles=item.toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        DocumentReference docRef=fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    name[0] = documentSnapshot.getString("firstName")+" "+documentSnapshot.getString("lastName");
                    username.setText(name[0]);
                    mobileNumber[0] =fAuth.getCurrentUser().getPhoneNumber();
                    userphone.setText(mobileNumber[0]);
                }
            }
        });

        final String latitude=data.getStringExtra("latitude");
        final String longitude=data.getStringExtra("longitude");

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    if (!name[0].isEmpty() && !mobileNumber[0].isEmpty() && !locationDetails.getText().toString().isEmpty()
                            && !numOfCattles.isEmpty() && !description.getText().toString().isEmpty()
                            && !latitude.isEmpty() && !longitude.isEmpty() && !imagePathInFireStore.isEmpty()) {

                        submit.setClickable(false);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        final String dateTime=dtf.format(now).toString();
                        submit.setText("Submitting Report....");
                        Log.d("TAG", "Username : " + name[0]);
                        Log.d("TAG", "Userphone : " + mobileNumber[0]);
                        Log.d("TAG", "Location Details : " + locationDetails.getText().toString());
                        Log.d("TAG", "Number of Cattles : " + numOfCattles);
                        Log.d("TAG", "Description : " + description.getText().toString());
                        Log.d("TAG", "Latitude : " + latitude);
                        Log.d("TAG", "Longitude : " + longitude);
                        Log.d("TAG", "ImageURL : " + imagePathInFireStore);
                        Log.d("TAG","submitDateTime : "+dateTime);


                        //code to save in firestore in a folder named report>> (random unique ids will be generated)

                        DocumentReference docref = fStore.collection("Cattle reports").document();

                        //code to save in firestore if the user personal database

                        final DocumentReference userDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                                .collection("User Reports").document();

                        final Map<String, Object> report = new HashMap<>();
                        report.put("username", name[0]);
                        report.put("userphone", mobileNumber[0]);
                        report.put("number", numOfCattles);
                        report.put("location", locationDetails.getText().toString());
                        report.put("latitude", latitude);
                        report.put("longitude", longitude);
                        report.put("description", description.getText().toString());
                        report.put("submitDateTime", dateTime);
                        report.put("imageURL", imagePathInFireStore);
                        docref.set(report).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userDocRef.set(report).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(userForm.this, "Report submitted", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getApplicationContext(),ThankYouPage.class);
                                        intent.putExtra("Username",name[0]);
                                        intent.putExtra("Phone Number", mobileNumber[0]);
                                        intent.putExtra("Number of Cattles", numOfCattles);
                                        intent.putExtra("Location Details", locationDetails.getText().toString());
                                        intent.putExtra("Description", description.getText().toString());
                                        intent.putExtra("Image URL", imagePathInFireStore);
                                        intent.putExtra("Date and Time",dateTime);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(userForm.this, "Error , try again later!", Toast.LENGTH_SHORT).show();
                                        submit.setClickable(true);
                                        submit.setText("Submit Request");
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(userForm.this, "Error , try again later!", Toast.LENGTH_SHORT).show();
                                submit.setClickable(true);
                                submit.setText("Submit Request");
                            }
                        });
                    } else {
                        Toast.makeText(userForm.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(userForm.this, "Retrieving Name and Username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void askCameraPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }else{
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_REQUEST_CODE){
            if(resultCode== Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.i("Image PAth","Absolute URL of the image is --> " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                uploadImageToFirebase(f.getName(),contentUri);
            }
        }


        if(requestCode==GALLERY_REQUEST_CODE){
            if(resultCode== Activity.RESULT_OK){

                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." +getFileExt(contentUri);
                Log.i("TAG","onActivityResult : Gallery Image Uri : " +imageFileName);
                selectedImage.setImageURI(contentUri);

                uploadImageToFirebase(imageFileName,contentUri);
            }
        }

    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        imageUploading.setVisibility(View.VISIBLE);
        cameraBtn.setClickable(false);
        galleryBtn.setClickable(false);
        final StorageReference image=storageReference.child("images/"+name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imagePathInFireStore=uri.toString();
                        Log.i("TaG","SUCCESS : Uploaded Image URL is : "+imagePathInFireStore);
                    }
                });
                Toast.makeText(userForm.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                imageUploading.setVisibility(View.INVISIBLE);
                tick.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(userForm.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                imageUploading.setVisibility(View.INVISIBLE);
            }
        });

    }

    private String getFileExt(Uri contentUri) {

        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));

    }


    private File createImageFile() throws IOException {

        // Create an image file name using timeStamp

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix  or extension */
                storageDir      /* directory */
        );


        // Save a file: path for use with ACTION_VIEW intents

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File.
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.cattle_care.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

}
