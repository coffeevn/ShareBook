package com.example.along.sharebook.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.along.sharebook.GlideApp;
import com.example.along.sharebook.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("Trang chủ");
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        View view = navigationView.getHeaderView(0);
        ImageView imageView = view.findViewById(R.id.ivMenuImage);
        Glide.with(HomeActivity.this).load(R.drawable.book).into(imageView);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.nav_findbook:
                                menuItem.setChecked(true);
                                mDrawerLayout.closeDrawers();
                                Intent intentFind = new Intent(HomeActivity.this,FindBookActivity.class);
                                finish();
                                startActivity(intentFind);
                                break;
                            case R.id.nav_newbook:
                                menuItem.setChecked(true);
                                mDrawerLayout.closeDrawers();
                                Intent intentNewBook = new Intent(HomeActivity.this,AddBookActivity.class);
                                startActivity(intentNewBook);
                                break;
                            case R.id.nav_mybook:
                                menuItem.setChecked(true);
                                mDrawerLayout.closeDrawers();
                                Intent intentMyBook = new Intent(HomeActivity.this,SellingActivity.class);
                                finish();
                                startActivity(intentMyBook);
                                break;
                            case R.id.nav_user:
                                menuItem.setChecked(true);
                                mDrawerLayout.closeDrawers();
                                Intent intentUser = new Intent(HomeActivity.this,AccountActivity.class);
                                finish();
                                startActivity(intentUser);
                                break;
                            case R.id.nav_logout:
                                menuItem.setChecked(true);
                                mDrawerLayout.closeDrawers();
                                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                                mAuth.signOut();
                                finish();
                                startActivity(intent);
                                break;
                            default:
                                mDrawerLayout.closeDrawers();
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
