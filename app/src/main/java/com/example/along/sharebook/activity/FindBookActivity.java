package com.example.along.sharebook.activity;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class FindBookActivity extends AppCompatActivity{
    private SearchView searchView;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;
    private ArrayList<Book> mBooks;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_book);
        Toolbar searchToolbar = findViewById(R.id.search_toolbar);
        searchToolbar.setTitle("");
        setSupportActionBar(searchToolbar);
        searchToolbar.setTitle("Tìm sách");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mBooks = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FindBookActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        BooksAdapter emptyBooksAdapter = new BooksAdapter(mBooks,FindBookActivity.this);
        recyclerView.setAdapter(emptyBooksAdapter);

        myRef.child("books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    Book elementBook = childSnapshot.getValue(Book.class);
                    mBooks.add(elementBook);
                }
                BooksAdapter booksAdapter = new BooksAdapter(mBooks,FindBookActivity.this);
                recyclerView.setAdapter(booksAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem itemSearch = menu.findItem(R.id.search_view);
        searchView = (SearchView) itemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String keyword = query;
                ArrayList<Book> tempBooks = new ArrayList<>();
                if (keyword.compareTo(dangKhongDau(query))!=0){
                    for(int i = 0; i<mBooks.size();i++){
                        if (mBooks.get(i).bName.indexOf(keyword)>=0){
                            tempBooks.add(mBooks.get(i));
                        }
                    }
                } else {
                    keyword = dangKhongDau(query);
                    for(int i = 0; i<mBooks.size();i++){
                        if (dangKhongDau(mBooks.get(i).bName).indexOf(keyword)>=0){
                            tempBooks.add(mBooks.get(i));
                        }
                    }
                }

                if(tempBooks.size()!=0){
                    BooksAdapter booksAdapter = new BooksAdapter(tempBooks,FindBookActivity.this);
                    recyclerView.setAdapter(booksAdapter);
                } else {
                    Toast.makeText(FindBookActivity.this,"Không tìm thấy",Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    String dangKhongDau (String s){
        String temp = Normalizer.normalize(s,Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(FindBookActivity.this,HomeActivity.class);
        finish();
        startActivity(intentHome);
        super.onBackPressed();
    }
}
