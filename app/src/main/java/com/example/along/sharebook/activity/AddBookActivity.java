package com.example.along.sharebook.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.along.sharebook.GlideApp;
import com.example.along.sharebook.R;
import com.example.along.sharebook.fragment.RentFragment;
import com.example.along.sharebook.fragment.SellFragment;
import com.example.along.sharebook.model.Book;
import com.example.along.sharebook.myInterface.RentBook;
import com.example.along.sharebook.myInterface.SellBook;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AddBookActivity extends AppCompatActivity implements SellBook, RentBook{
    private StorageReference mStorageRef;
    private FirebaseUser currentUser;
    private RadioGroup radioGroup;
    private RadioButton radioButtonSell, radioButtonRent;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    private EditText mName, mAuthor, mDescription;
    private Bitmap mBitmap;
    private int mStatus;
    private int mPrice;
    private String mCurrentPhotoPath;
    private String mCurrentPhotoName;
    private ImageView mImageView, mLoading;
    private RelativeLayout mRelativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mRelativeLayout = findViewById(R.id.rlAddBook);
        mImageView = findViewById(R.id.add_book_ivCover);
        mLoading = findViewById(R.id.ivUploading);
        mName = findViewById(R.id.etAddBookName);
        mAuthor = findViewById(R.id.etAddBookAuthor);
        mDescription = findViewById(R.id.etAddBookDescription);
        radioGroup = findViewById(R.id.rgSellOrRent);
        radioButtonSell = findViewById(R.id.rbAddBookSell);
        radioButtonRent = findViewById(R.id.rbAddBookRent);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        Button btUpload = findViewById(R.id.btAddBookUpload);
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlideApp.with(AddBookActivity.this).load(R.drawable.loading3).into(mLoading);
                mRelativeLayout.setVisibility(View.INVISIBLE);
                mLoading.setVisibility(View.VISIBLE);
                upload();
            }
        });

        mPrice = 0;
        mStatus = 0;

        radioButtonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SellFragment sellFragment = new SellFragment();
                sellFragment.show(getFragmentManager(),"SellDialog");
            }
        });
        radioButtonRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RentFragment rentFragment = new RentFragment();
                rentFragment.show(getFragmentManager(),"RentDialog");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        mCurrentPhotoName = image.getName();
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
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    photoURI = FileProvider.getUriForFile(AddBookActivity.this,
                            "com.example.along.sharebook",
                            photoFile);
                } else{
                    photoURI = Uri.fromFile(photoFile);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        mBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(mBitmap);
    }

    public void upload(){
        final Uri file = Uri.fromFile(new File(mCurrentPhotoPath));
        StorageReference imagesRef = mStorageRef.child("images/"+mCurrentPhotoName);
        imagesRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        currentUser = mAuth.getCurrentUser();

                        Book book = new Book(mName.getText().toString(), mAuthor.getText().toString(),
                                mStatus, mAuth.getUid(),mCurrentPhotoName,mDescription.getText().toString(),mPrice);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference myRef = database.getReference("books").push();
                        myRef.setValue(book);
                        final DatabaseReference newRef = database.getReference("users").child(currentUser.getUid()).child("curSell");
                        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<String> temp = new ArrayList<>();
                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                    String elementString = childSnapshot.getValue(String.class);
                                    temp.add(elementString);
                                }
                                temp.add(myRef.getKey());

                                newRef.setValue(temp);
                                Intent intent = new Intent(AddBookActivity.this,HomeActivity.class);
                                finish();
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        mRelativeLayout.setVisibility(View.VISIBLE);
                        mLoading.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public void priceOfBook(int bookPrice) {
        mPrice = bookPrice;
        mStatus = 1;
        radioButtonSell.setChecked(true);
    }

    @Override
    public void priceOfRent(int price) {
        mPrice = price;
        mStatus = 2;
        radioButtonRent.setChecked(true);
    }
}
