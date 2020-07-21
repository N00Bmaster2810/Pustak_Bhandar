package com.example.pustakbhandar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        final Book book = getItem(position);


        assert book != null;


        TextView title = listItemView.findViewById(R.id.book_name);
        title.setText(book.getTitle());

        TextView author = listItemView.findViewById(R.id.author_name);
        author.setText(book.getAuthor());

        TextView language = listItemView.findViewById(R.id.language);
        language.setText(book.getLanguage());

        TextView price = listItemView.findViewById(R.id.price);
        price.setText(book.getPrice());

        ImageView image = listItemView.findViewById(R.id.image);
        Picasso.get().load(book.getImageLink()).placeholder(R.drawable.icon).into(image);


        return listItemView;

    }
}
