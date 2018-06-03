package com.example.along.sharebook.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.along.sharebook.GlideApp;
import com.example.along.sharebook.R;
import com.example.along.sharebook.adapter.BooksAdapter;
import com.example.along.sharebook.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BookDetailActivity extends AppCompatActivity {
    private int curPos;
    private ArrayList<String> bookIDs;
    private ArrayList<Book> books;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;
    private TextView tvBDName, tvBDAuthor, tvBDDescription, tvBDPrice, tvBDSeller, tvBDDate, tvBDKhuVuc, tvBDQuan, tvBDSDT;
    private ImageView ivNext, ivPrevious, ivBDCover, ivBDAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        tvBDName = findViewById(R.id.tvBDName);
        tvBDAuthor = findViewById(R.id.tvBDAuthor);
        tvBDDescription = findViewById(R.id.tvBDDescription);
        tvBDPrice = findViewById(R.id.tvBDPrice);
        tvBDSeller = findViewById(R.id.tvBDSeller);
        tvBDDate = findViewById(R.id.tvBDDate);
        ivBDCover = findViewById(R.id.ivBDBookCover);
        ivBDAvatar = findViewById(R.id.ivBDAvatar);
        tvBDKhuVuc = findViewById(R.id.tvBDKhuVuc);
        tvBDQuan = findViewById(R.id.tvBDQuan);
        tvBDSDT = findViewById(R.id.tvBDSdt);
        ivPrevious = findViewById(R.id.ivBDPrevious);
        ivNext = findViewById(R.id.ivBDNext);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        bookIDs = intent.getStringArrayListExtra("bookNames");
        curPos = intent.getIntExtra("curPos",0);

        books = new ArrayList<>();
        hienThi(curPos);

        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curPos>0){
                    curPos--;
                    hienThi(curPos);
                }
            }
        });
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curPos<bookIDs.size()-1){
                    curPos++;
                    hienThi(curPos);
                }
            }
        });

    }

    void hienThi(final int position){
        myRef.child("books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (books.size()==0){
                    for(int i = 0; i<bookIDs.size();i++){
                        Book elementBook = dataSnapshot.child(bookIDs.get(i)).getValue(Book.class);
                        books.add(elementBook);
                    }
                }

                tvBDName.setText(books.get(position).bName);
                tvBDAuthor.setText("Tác giả: "+books.get(position).bAuthor);
                tvBDDescription.setText(books.get(position).bDescription);
                tvBDSeller.setText(books.get(position).bUserSellUid);
                tvBDDate.setText(books.get(position).bDate);
                String giaTien = String.valueOf(books.get(position).bPrice);
                if(books.get(position).bStatus == 2){
                    tvBDPrice.setText("Cho thuê giá: "+ giaTien + " VNĐ/ngày");
                } else {
                    tvBDPrice.setText("Bán với giá: "+ giaTien + " VNĐ");
                }

                StorageReference imagesRef = mStorageRef.child("images/"+books.get(position).bCoverName);
                GlideApp.with(BookDetailActivity.this)
                        .load(imagesRef)
                        .error(R.drawable.book)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivBDCover);

                myRef.child("users").child(books.get(position).bUserSellUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String temp = dataSnapshot.child("username").getValue(String.class);
                        tvBDSeller.setText("Được đăng bởi "+temp);
                        String temp2 = dataSnapshot.child("hinhDaiDien").getValue(String.class);
                        StorageReference imagesRef = mStorageRef.child("avatars/"+temp2);
                        GlideApp.with(BookDetailActivity.this)
                                .load(imagesRef)
                                .error(R.drawable.book)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(ivBDAvatar);
                        String temp3 = dataSnapshot.child("sdt").getValue(String.class);
                        tvBDSDT.setText("SDT: "+temp3);
                        int KV = dataSnapshot.child("khuVuc").getValue(Integer.class);
                        int Quan = dataSnapshot.child("quan").getValue(Integer.class);
                        setKVvaQuan(KV,Quan);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void setKVvaQuan(int KV, int Quan){
        if(KV==0) {
            tvBDKhuVuc.setText("Hồ Chí Minh");
            switch (Quan) {
                case 0:
                    tvBDQuan.setText("Quận 1");
                    break;
                case 1:
                    tvBDQuan.setText("Quận 2");
                    break;
                case 2:
                    tvBDQuan.setText("Quận 3");
                    break;
                case 3:
                    tvBDQuan.setText("Quận 4");
                    break;
                case 4:
                    tvBDQuan.setText("Quận 5");
                    break;
                case 5:
                    tvBDQuan.setText("Quận 6");
                    break;
                case 6:
                    tvBDQuan.setText("Quận 7");
                    break;
                case 7:
                    tvBDQuan.setText("Quận 8");
                    break;
                case 8:
                    tvBDQuan.setText("Quận 9");
                    break;
                case 9:
                    tvBDQuan.setText("Quận 10");
                    break;
                case 10:
                    tvBDQuan.setText("Quận 11");
                    break;
                case 11:
                    tvBDQuan.setText("Quận 12");
                    break;
            }
        } else {
            tvBDKhuVuc.setText("Hà Nội");
            switch (Quan) {
                case 0:
                    tvBDQuan.setText("Ba Đình");
                    break;
                case 1:
                    tvBDQuan.setText("Hoàn Kiếm");
                    break;
                case 2:
                    tvBDQuan.setText("Hai Bà Trưng");
                    break;
                case 3:
                    tvBDQuan.setText("Đống Đa");
                    break;
                case 4:
                    tvBDQuan.setText("Tây Hồ");
                    break;
                case 5:
                    tvBDQuan.setText("Cầu Giấy");
                    break;
                case 6:
                    tvBDQuan.setText("Thanh Xuân");
                    break;
                case 7:
                    tvBDQuan.setText("Hoàng Mai");
                    break;
                case 8:
                    tvBDQuan.setText("Long Biên");
                    break;
                case 9:
                    tvBDQuan.setText("Hà Đông");
                    break;
            }
        }
    }
}
