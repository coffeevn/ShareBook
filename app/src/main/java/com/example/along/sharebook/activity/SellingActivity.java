package com.example.along.sharebook.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

public class SellingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;
    private ArrayList<Book> mBooks;
    private ArrayList<String> mSellings;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mBooks = new ArrayList<>();
        mSellings = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewSelling);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SellingActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        BooksAdapter emptyBooksAdapter = new BooksAdapter(mBooks,SellingActivity.this);
        recyclerView.setAdapter(emptyBooksAdapter);

        myRef.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("curSell")){
                    for (DataSnapshot childSnapshot: dataSnapshot.child("curSell").getChildren()) {
                        String elementSelling = childSnapshot.getValue(String.class);
                        mSellings.add(elementSelling);
                    }
                }

                for (int i = 0; i<mSellings.size();i++) {
                    myRef.child("books").child(mSellings.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Book elementBook = dataSnapshot.getValue(Book.class);
                            mBooks.add(elementBook);
                            BooksAdapter booksAdapter = new BooksAdapter(mBooks,SellingActivity.this);
                            recyclerView.setAdapter(booksAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(SellingActivity.this,HomeActivity.class);
        finish();
        startActivity(intentHome);
        super.onBackPressed();
    }
}
