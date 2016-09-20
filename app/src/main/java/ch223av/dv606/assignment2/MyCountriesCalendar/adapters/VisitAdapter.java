package ch223av.dv606.assignment2.MyCountriesCalendar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ch223av.dv606.assignment2.R;
import ch223av.dv606.assignment2.MyCountriesCalendar.Visit;
import ch223av.dv606.assignment2.MyCountriesCalendar.CalendarProviderClient;


public class VisitAdapter extends ArrayAdapter<Visit> {
    private Visit data[];

    public VisitAdapter(Context context, int resource, ArrayList<Visit> data) {
        super(context, resource, data);
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        View view = rowView;
        ViewHolder holder;

        if (rowView == null) { // create new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.country_year_item, parent, false);

            holder = new ViewHolder();
            holder.year = (TextView) view.findViewById(R.id.yearTextView);
            holder.country = (TextView) view.findViewById(R.id.countyTextView);

            view.setTag(holder);

        } else { // reuse old view
            holder = (ViewHolder) view.getTag();
        }

        Visit visit = getItem(position);
        holder.year.setText(visit.getYear()+"");
        holder.country.setText(visit.getCountry());
        return view;
    }


    static class ViewHolder {
        TextView year;
        TextView country;
    }
}