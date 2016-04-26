package com.example.vinh.tripadvisorapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import DataPack.LocationItem;

public class LocationDetailActivity extends AppCompatActivity {

    //the images to display
    Integer[] imageIDs = {
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3
    };

    private String INFORMATION_DETAIL_NAME = "1";
    private String INFORMATION_DETAIL_ADDRESS = "2";
    private String INFORMATION_DETAIL_WEBSITE = "3";
    private String INFORMATION_DETAIL_DESCRIPTION = "4";
    private String INFORMATION_DETAIL_PHONE = "5";
    private String INFORMATION_DETAIL_X = "6";
    private String INFORMATION_DETAIL_Y = "7";
    private String INFORMATION_DETAIL_INDEX = "8";

    private LocationItem aLocationItem[];
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //aLocationItem = (LocationItem[])getIntent().getExtras().get(INFORMATION_DETAIL);
        //index = getIntent().getExtras().getInt(INFORMATION_DETAIL);

        setTitle(getIntent().getExtras().getString(INFORMATION_DETAIL_NAME));
        TextView tvAddress = (TextView)findViewById(R.id.textAddress);
        TextView tvWebsite = (TextView)findViewById(R.id.textHttp);
        TextView tvDescription = (TextView)findViewById(R.id.textDescription);

        index = getIntent().getExtras().getInt(INFORMATION_DETAIL_INDEX);

        //tvTitle.setText(getIntent().getExtras().getString(INFORMATION_DETAIL_NAME));
        tvAddress.setText(getIntent().getExtras().getString(INFORMATION_DETAIL_ADDRESS));
        tvWebsite.setText(getIntent().getExtras().getString(INFORMATION_DETAIL_WEBSITE));
        tvDescription.setText(getIntent().getExtras().getString(INFORMATION_DETAIL_DESCRIPTION));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make a phone call
                String tel = "tel:"
                        + getIntent().getExtras().getString(INFORMATION_DETAIL_PHONE);
                Uri number = Uri.parse(tel);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        // Note that Gallery view is deprecated in Android 4.1---
        Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position,long id)
            {
                Toast.makeText(getBaseContext(),"pic" + (position + 1) + " selected",
                        Toast.LENGTH_SHORT).show();
                // display the images selected
                ImageView imageView = (ImageView) findViewById(R.id.image1);
                imageView.setImageResource(imageIDs[position]);
            }
        });
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;
        public ImageAdapter(Context c)
        {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a = obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }
        // returns the number of images
        public int getCount() {
            return imageIDs.length;
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }
        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageIDs[position]);
            imageView.setLayoutParams(new Gallery.LayoutParams(300, 300));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }
}
