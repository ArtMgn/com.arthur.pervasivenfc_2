package com.arthur.pervasivenfc.UserInterface;

import java.util.List;

import android.content.Context;
import android.location.Address;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class MyArrayAdapter extends ArrayAdapter<Address> {
	Context mycontext;
	
	public MyArrayAdapter(Context context, int textViewResourceId,
			List<Address> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		mycontext = context;
		}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int maxAddressLineIndex = getItem(position).getMaxAddressLineIndex();
		String addressLine = "";
		for (int j = 0; j <= maxAddressLineIndex; j++){
			addressLine += getItem(position).getAddressLine(j) + ",";
			}
		TextView rowAddress = new TextView(mycontext);
		rowAddress.setText(addressLine);
		return rowAddress;
	}
}
