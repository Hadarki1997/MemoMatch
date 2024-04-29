package com.example.matchmemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class CardAdapter extends BaseAdapter {
    private Context context;
    private List<Card> cards;
    private LayoutInflater inflater;

    public CardAdapter(Context context, List<Card> cards) {
        this.context = context;
        this.cards = cards;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Card getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.grid_item, parent, false);
        }


        imageView = (ImageView) convertView.findViewById(R.id.imageView);

        int numColumns = 4;
        int size = parent.getWidth() / numColumns;


        imageView.setLayoutParams(new GridView.LayoutParams(size, size));

        Card card = getItem(position);
        if (card.isFlipped()) {
            imageView.setImageResource(card.getImageId());
        } else {
            imageView.setImageResource(R.drawable.ic_questionmark);
        }

        return convertView;
    }




}