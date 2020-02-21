package com.aluminati.inventory.userprofile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aluminati.inventory.R;
import com.aluminati.inventory.firestore.UserFetch;
import com.aluminati.inventory.users.User;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UserPhoto extends Fragment implements View.OnClickListener {


    private static final String TAG = UserPhoto.class.getName();
    private static final int GET_PHOTO = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private TextView userImageChange;
    private ImageView userPhoto;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.user_photo, container, false);

        userPhoto = view.findViewById(R.id.userImage);
        userImageChange = view.findViewById(R.id.user_image_change);
        userImageChange.setOnClickListener(this);
        userPhoto.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Log.i(TAG, "Picture not found");
                return;
            }
            try {
                Log.i(TAG, "Picture found");

                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                if(inputStream != null){
                    Log.i(TAG, "Stream not null" + inputStream.toString());
                    Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(inputStream));
                    userPhoto.setImageBitmap(bitmap);
                    if(hasImage(userPhoto)) {
                        userImageChange.setVisibility(View.INVISIBLE);
                        UserFetch.update(firebaseAuth.getCurrentUser().getEmail(), "user_photo", encodeTobase64(bitmap));
                        Snackbar.make(userImageChange, getResources().getString(R.string.photo_changed), BaseTransientBottomBar.LENGTH_LONG);
                    }
                }else Log.i(TAG, "Stream  null");
            }catch (FileNotFoundException e){
                Log.w(TAG, "File not found", e);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!hasImage(userPhoto)) {
            userImageChange.setVisibility(View.VISIBLE);
            userImageChange.setText(getResources().getString(R.string.upload_user_image));
        }


        if(firebaseAuth.getCurrentUser() != null){
            try {
                new RemoteImage(userPhoto).execute(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
            } catch (NullPointerException e) {
                Log.w(TAG, "User photo null", e);
                UserFetch.getUser(firebaseAuth.getCurrentUser().getEmail()).addOnSuccessListener(resultImage -> {
                    Log.i(TAG, "Got user successfully");
                    User user = new User(resultImage);
                    if(user.getPhoto() != null){
                        userPhoto.setImageBitmap(decodeBase64(user.getPhoto()));
                        userImageChange.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(resultImage -> {
                    Log.w(TAG, "Failed to get user successfully", resultImage);
                });
            }

        }

    }

    private boolean hasImage(@NonNull ImageView view) {

        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;

    }

    private void countDown() {
        new CountDownTimer(5 * 1000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                userImageChange.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GET_PHOTO);
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        } else {
            Snackbar.make(userImageChange, " Permission already granted", BaseTransientBottomBar.LENGTH_LONG).show();
            pickImage();
        }
    }


    public String encodeTobase64(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }

    public Bitmap decodeBase64(String input)
    {
        try{
            byte [] encodeByte = Base64.decode(input,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(userImageChange, "Storage Permission Granted", BaseTransientBottomBar.LENGTH_LONG).show();
                pickImage();
            } else {
                Snackbar.make(userImageChange, "Storage Permission Denied", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.userImage: {
                userImageChange.setVisibility(View.VISIBLE);
                countDown();
                break;
            }
            case R.id.user_image_change:{
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                break;
            }
        }
    }

    private static class RemoteImage extends AsyncTask<String, Void, Bitmap> {

        @SuppressLint("StaticFieldLeak")
        ImageView profileImageViwe;

        RemoteImage(ImageView profileImageViwe){
            this.profileImageViwe = profileImageViwe;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap userIcon = null;
            try{
                InputStream inputStream = new java.net.URL(urlDisplay).openStream();
                userIcon = BitmapFactory.decodeStream(inputStream);
            }catch (Exception e){
                Log.w("Error Converting", e);
            }
            return userIcon;
        }

        protected void onPostExecute(Bitmap result){
            profileImageViwe.setImageBitmap(result);
        }


    }
}