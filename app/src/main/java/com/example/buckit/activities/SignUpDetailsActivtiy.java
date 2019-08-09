package com.example.buckit.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.utils.MultiSelectSpinner;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.buckit.fragments.EventsExploreFragment.KEY_SELECTED_CATEGORIES;
import static com.example.buckit.models.User.KEY_EVENT_RADIUS;

public class SignUpDetailsActivtiy extends AppCompatActivity {

    @BindView(R.id.spinCategoriesSignUp)
    MultiSelectSpinner spinCategoriesSignUp;
    @BindView(R.id.etEventRadius)
    EditText etEventRadius;
    @BindView(R.id.spinDistanceType)
    Spinner spinDistanceType;
    @BindView(R.id.btnFinishRegistration)
    Button btnFinishRegistration;
    @BindView(R.id.btnTakeProfilePicture) Button btnTakeProfilePicture;
    @BindView(R.id.ivShowTakenProfilePic)
    ImageView ivShowTakenProfilePic;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final String TAG = "CameraAction";
    public String photoFileName = "photo.jpg";
    private File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_details_activtiy);
        ButterKnife.bind(this);
        spinCategoriesSignUp.setItems(getResources().getStringArray(R.array.category_names));
        btnTakeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchCamera();
            }
        });
        btnFinishRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                List<String> selected = spinCategoriesSignUp.getSelectedStrings();
                String eventRadius = (etEventRadius.getText().toString())+(spinDistanceType.getSelectedItem().toString());
                if (eventRadius == null || selected == null){
                    Toast.makeText(getApplicationContext(), "Please fill in all required inputs", Toast.LENGTH_SHORT);
                }
                else {
                    completeRegistration(user, selected, eventRadius);
                }
            }
        });

    }

    public void LaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }
    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivShowTakenProfilePic.setImageBitmap(takenImage);
                ivShowTakenProfilePic.setVisibility(View.VISIBLE);
                btnTakeProfilePicture.setVisibility(View.GONE);
        }
    }

    private void completeRegistration(ParseUser user, List<String> categories, String radius){
        user.put(KEY_EVENT_RADIUS, radius);
        ParseACL acl = new ParseACL(user);
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        user.setACL(acl);
        ArrayList<String> catList = new ArrayList<>();
        catList.addAll(categories);
        user.put("deviceId", FirebaseInstanceId.getInstance().getToken());
        if (photoFile != null) user.put("profilePic", new ParseFile(photoFile));
        user.put(KEY_SELECTED_CATEGORIES, catList);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                startActivity(new Intent(SignUpDetailsActivtiy.this, HomeActivity.class));
                Toast.makeText(getApplicationContext(), "Account details saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
