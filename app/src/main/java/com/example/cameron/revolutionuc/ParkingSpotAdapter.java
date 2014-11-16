package com.example.cameron.revolutionuc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cameron.revolutionuc.model.ParkingSpot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Cameron on 11/15/2014.
 */
public class ParkingSpotAdapter extends ArrayAdapter<ParkingSpot> {

    private final Context context;
    private ArrayList<ParkingSpot> parkingSpots;

    public ParkingSpotAdapter(Context context, ArrayList<ParkingSpot> parkingSpots) {
        super(context, R.layout.row_parking_spot, parkingSpots);
        this.context = context;
        this.parkingSpots = parkingSpots;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_parking_spot, parent, false);

        TextView tvAddress = (TextView) rowView.findViewById(R.id.tvAddress);
        TextView tvDistance = (TextView) rowView.findViewById(R.id.tvDistance);

        DecimalFormat decimalFormat = new DecimalFormat("#0.0");

        ParkingSpot parkingSpot = parkingSpots.get(position);

        tvAddress.setText(parkingSpot.getAddress());
        tvDistance.setText(decimalFormat.format(parkingSpot.getDistance()) + " mi");

        return rowView;
    }
}
