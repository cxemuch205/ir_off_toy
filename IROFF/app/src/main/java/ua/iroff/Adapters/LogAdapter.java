package ua.iroff.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.iroff.Models.CodeData;
import ua.iroff.R;

/**
 * Created by daniil on 11/19/14.
 */
public class LogAdapter extends ArrayAdapter<CodeData> {

    private Context context;
    private ArrayList<CodeData> data;

    public LogAdapter(Context context, ArrayList<CodeData> data) {
        super(context, R.layout.item_log, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_log, null);

            holder = new ViewHolder();
            holder.tvBrand = (TextView) view.findViewById(R.id.tv_brand);
            holder.tvCode = (TextView) view.findViewById(R.id.tv_code);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        CodeData item = data.get(position);
        holder.tvBrand.setText(item.brand);
        holder.tvCode.setText(item.code);
        if (item.color != -1) {
            view.setBackgroundColor(item.color);
        } else {
            view.setBackground(null);
        }

        return view;
    }

    private static class ViewHolder {
        TextView tvBrand;
        TextView tvCode;
    }
}
