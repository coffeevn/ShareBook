package com.example.along.sharebook.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.along.sharebook.GlideApp;
import com.example.along.sharebook.R;
import com.example.along.sharebook.model.User;
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
import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    public static final int GALLERY_REQUEST = 1212;
    private Uri selectedImage;
    private StorageReference mStorageRef;
    private ImageView ivUserAvatar;
    private FirebaseUser currentUser;
    private ArrayList<String> tempList;
    private ArrayList<String> khuVuc;
    private ArrayList<String> quan;
    private Spinner spinnerState, spinnerDistrict;
    private EditText etUserName, etUserPhone, etUserDescription;
    private int location;
    private int distr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        tempList = new ArrayList<>();

        etUserName = findViewById(R.id.etUserName);
        etUserPhone = findViewById(R.id.etUserPhone);
        etUserDescription = findViewById(R.id.etDescription);

        setInfo();

        //Avatar
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        if (currentUser.getProviders().get(0).compareTo("facebook.com")==0){
            String facebookAvatarPath = currentUser.getPhotoUrl().toString() + "?width=250";
            GlideApp.with(AccountActivity.this).load(facebookAvatarPath).into(ivUserAvatar);
        } else {
            if (currentUser.getProviders().get(0).compareTo("google.com") == 0) {
                String googleAvatar = currentUser.getPhotoUrl().toString().replace("/s96-c/", "/s250-c/");
                GlideApp.with(AccountActivity.this).load(googleAvatar).into(ivUserAvatar);
            } else {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(currentUser.getUid())){
                            StorageReference mStorageRef;
                            mStorageRef = FirebaseStorage.getInstance().getReference();
                            StorageReference avatarsRef = mStorageRef.child("avatars/"+currentUser.getUid()+".jpg");
                            GlideApp.with(AccountActivity.this).load(avatarsRef).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivUserAvatar);
                            ivUserAvatar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                                }
                            });
                        } else {
                            ivUserAvatar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        /////////// Luu len firebase
        Button btUpdate = findViewById(R.id.btUpdate);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (KiemTra()) {
                    //Upload avatar len storage
                    if (currentUser.getProviders().get(0).compareTo("facebook.com") == 0) {
                        String facebookAvatarPath = currentUser.getPhotoUrl().toString() + "?width=250";
                        GlideApp.with(AccountActivity.this).downloadOnly().load(facebookAvatarPath).apply(new RequestOptions()).into(new SimpleTarget<File>() {
                            @Override
                            public void onResourceReady(File resource, Transition<? super File> transition) {

                                Uri file = Uri.fromFile(resource);
                                StorageReference avatarRef = mStorageRef.child("avatars/" + currentUser.getUid() + ".jpg");
                                avatarRef.putFile(file)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                XacNhan();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {

                                            }
                                        });
                            }
                        });
                    } else {
                        if (currentUser.getProviders().get(0).compareTo("google.com") == 0) {
                            String googleAvatar = currentUser.getPhotoUrl().toString().replace("/s96-c/", "/s250-c/");
                            GlideApp.with(AccountActivity.this).downloadOnly().load(googleAvatar).apply(new RequestOptions()).into(new SimpleTarget<File>() {
                                @Override
                                public void onResourceReady(File resource, Transition<? super File> transition) {
                                    Uri file = Uri.fromFile(resource);
                                    StorageReference avatarRef = mStorageRef.child("avatars/" + currentUser.getUid() + ".jpg");
                                    avatarRef.putFile(file)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    XacNhan();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {

                                                }
                                            });
                                }
                            });
                        } else {
                            if (selectedImage != null) {
                                StorageReference avatarRef = mStorageRef.child("avatars/" + currentUser.getUid() + ".jpg");
                                avatarRef.putFile(selectedImage)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                XacNhan();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {

                                            }
                                        });
                            } else {
                                GlideApp.with(AccountActivity.this).downloadOnly().load(R.drawable.user).apply(new RequestOptions()).into(new SimpleTarget<File>() {
                                    @Override
                                    public void onResourceReady(File resource, Transition<? super File> transition) {
                                        Uri file = Uri.fromFile(resource);
                                        StorageReference avatarRef = mStorageRef.child("avatars/" + currentUser.getUid() + ".jpg");
                                        avatarRef.putFile(file)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        XacNhan();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {

                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    }

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST:
                    selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        ivUserAvatar.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e("Avatar", "Error" + e);
                    }
                    break;
            }
    }

    private ArrayList<String> KhuVucList(){
        ArrayList<String> state = new ArrayList<>();
        state.add("Hồ Chí Minh");
        state.add("Hà Nội");
        state.add("");
        return state;
    }
    private ArrayList<String> QuanList(int mQuan){
        switch (mQuan){
            case 0:
                ArrayList<String> HCM = new ArrayList<>();
                HCM.add("Quận 1");
                HCM.add("Quận 2");
                HCM.add("Quận 3");
                HCM.add("Quận 4");
                HCM.add("Quận 5");
                HCM.add("Quận 6");
                HCM.add("Quận 7");
                HCM.add("Quận 8");
                HCM.add("Quận 9");
                HCM.add("Quận 10");
                HCM.add("Quận 11");
                HCM.add("Quận 12");
                HCM.add("");
                return HCM;
            case 1:
                ArrayList<String> HN = new ArrayList<>();
                HN.add("Ba Đình");
                HN.add("Hoàn Kiếm");
                HN.add("Hai Bà Trưng");
                HN.add("Đống Đa");
                HN.add("Tây Hồ");
                HN.add("Cầu Giấy");
                HN.add("Thanh Xuân");
                HN.add("Hoàng Mai");
                HN.add("Long Biên");
                HN.add("Hà Đông");
                HN.add("");
                return HN;
            default:
                return new ArrayList<>();
        }
    }
    private boolean KiemTra(){
        String sdt = etUserPhone.getText().toString();
        String name = etUserName.getText().toString();
        boolean ktSDT = false;
        if ((sdt.length()==10 || sdt.length() == 11) && sdt.startsWith("0")) {
            ktSDT = true;
        }else {
            etUserPhone.setError("Số điện thoại không hợp lệ");
        }

        boolean ktName = false;
        if (name.length()>=2){
            ktName = true;
        } else {
            etUserName.setError("Tên không hợp lệ");
        }
        return (ktSDT && ktName);
    }


    private void XacNhan(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentUser.getUid())){
                    for (DataSnapshot childSnapshot: dataSnapshot.child("curSell").getChildren()) {
                        String elementString = childSnapshot.getValue(String.class);
                        tempList.add(elementString);
                    }
                    User user = new User(etUserName.getText().toString(), etUserPhone.getText().toString(), location, distr, etUserDescription.getText().toString(),currentUser.getUid()+".jpg",tempList);
                    myRef.child(currentUser.getUid()).setValue(user);

                    Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    User user = new User(etUserName.getText().toString(), etUserPhone.getText().toString(), location, distr, etUserDescription.getText().toString(),currentUser.getUid()+".jpg",tempList);
                    myRef.child(currentUser.getUid()).setValue(user);

                    Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
                    finish();
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setInfo(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentUser.getUid())){
                    User user = dataSnapshot.child(currentUser.getUid()).getValue(User.class);
                    if (user != null) {
                        etUserName.setText(user.username);
                        etUserPhone.setText(user.sdt);
                        etUserDescription.setText(user.moTa);
                        int mKhuVuc = user.khuVuc;
                        int mQuan = user.quan;
                        setKhuVuc(mKhuVuc);
                        setQuan(mQuan);
                    }
                } else {
                    etUserName.setText(currentUser.getDisplayName());
                    setKhuVuc(2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setKhuVuc(int mLocation){
        location = mLocation;
        spinnerState = findViewById(R.id.spinnerState);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        khuVuc = KhuVucList();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,khuVuc);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinnerState.setAdapter(adapter);
        spinnerState.setSelection(mLocation);
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = position;
                if(khuVuc.size()==3){
                    khuVuc.remove(2);
                }
                if(location!=2){
                    quan = QuanList(location);
                    showQuan(location);
                    ArrayAdapter<String> adapterDistrict = new ArrayAdapter<>(AccountActivity.this,android.R.layout.simple_spinner_item,quan);
                    adapterDistrict.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                    spinnerDistrict.setAdapter(adapterDistrict);

                    spinnerDistrict.setSelection(distr);
                    spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            distr = position;
                            if(quan.size()==13){
                                quan.remove(12);
                            }
                            if(quan.size()==11){
                                if (location==1) {
                                    quan.remove(10);
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showQuan(int mLocation){
        if (mLocation != 2) {
            TextView tvDistrict = findViewById(R.id.tvUserDistrict);
            tvDistrict.setVisibility(View.VISIBLE);
            spinnerDistrict.setVisibility(View.VISIBLE);
            spinnerDistrict.setSelection(quan.size()-1);
        }
    }

    private void setQuan(int mDistrict) {
        distr = mDistrict;

    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(AccountActivity.this,HomeActivity.class);
        finish();
        startActivity(intentHome);
        super.onBackPressed();
    }
}
