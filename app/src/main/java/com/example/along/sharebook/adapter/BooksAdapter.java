package com.example.along.sharebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.along.sharebook.GlideApp;
import com.example.along.sharebook.R;
import com.example.along.sharebook.activity.BookDetailActivity;
import com.example.along.sharebook.model.Book;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    ArrayList<Book> books;
    Context context;

    public BooksAdapter(){

    }
    public BooksAdapter(ArrayList<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View bookView = layoutInflater.inflate(R.layout.book,parent,false);
        ViewHolder viewHolder = new ViewHolder(bookView);
        viewHolder.spinKitView.setVisibility(View.INVISIBLE);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.rlBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> bookIds = new ArrayList<>();
                for (int i = 0; i<books.size(); i++){
                    bookIds.add(books.get(i).bID);
                }
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putStringArrayListExtra("bookNames",bookIds);
                intent.putExtra("curPos",position);
                context.startActivity(intent);
            }
        });
        holder.tvBookName.setText(books.get(position).bName);
        holder.tvBookAuthor.setText(books.get(position).bAuthor);
        holder.tvBookDescription.setText(books.get(position).bDescription);
        String giaTien = String.valueOf(books.get(position).bPrice);
        holder.spinKitView.setVisibility(View.VISIBLE);
        if(books.get(position).bStatus == 2){
            holder.ivBookStatus.setVisibility(View.VISIBLE);
            GlideApp.with(context).load(R.drawable.forrent).into(holder.ivBookStatus);
            holder.tvBookPrice.setText(giaTien + " VNĐ/ngày");
        } else {
            holder.tvBookPrice.setText(giaTien + " VNĐ");
        }
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = mStorageRef.child("images/"+books.get(position).bCoverName);
        GlideApp.with(context)
                .load(imagesRef)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.spinKitView.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.spinKitView.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .error(R.drawable.book)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.ivBookCover);
    }

    @Override
    public int getItemCount() {
        if (books!=null && !books.isEmpty()){
            return books.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public SpinKitView spinKitView;
        public ImageView ivBookCover, ivBookStatus;
        public TextView tvBookName, tvBookAuthor, tvBookDescription, tvBookPrice;
        public RelativeLayout rlBook;
        public ViewHolder(View itemView) {
            super(itemView);
            spinKitView = itemView.findViewById(R.id.spinKit);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookName = itemView.findViewById(R.id.tvBookName);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvBookDescription = itemView.findViewById(R.id.tvBookDescription);
            tvBookPrice = itemView.findViewById(R.id.tvBookPrice);
            ivBookStatus = itemView.findViewById(R.id.ivBookStatus);
            rlBook = itemView.findViewById(R.id.rlBook);
        }
    }
}
