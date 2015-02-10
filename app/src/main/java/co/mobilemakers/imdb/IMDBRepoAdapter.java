package co.mobilemakers.imdb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by david.burgos on 10/02/2015.
 */
public class IMDBRepoAdapter extends ArrayAdapter<IMDBRepo>{

    List<IMDBRepo> IMDBRepoList;

    public class ViewHolder{
        public final TextView textViewTitle;
        public final TextView textViewPlot;
        public final TextView textViewYear;
        public final ImageView ImageViewPoster;

        public ViewHolder(View view){
            textViewTitle   = (TextView) view.findViewById(R.id.text_view_title_movie);
            textViewYear    = (TextView) view.findViewById(R.id.text_view_year);
            textViewPlot    = (TextView) view.findViewById(R.id.text_view_plot);
            ImageViewPoster = (ImageView)view.findViewById(R.id.image_view_poster);
        }
    }

    public IMDBRepoAdapter(Context context, List<IMDBRepo> repos) {
        super(context, R.layout.list_item_entry, repos);
        IMDBRepoList = repos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = reuseOrGenerateRowView(convertView, parent);
        displayRepoInRow(position, rowView);
        return rowView;
    }

    private void displayRepoInRow(int position, View rowView) {
        ViewHolder viewHolder = (ViewHolder)rowView.getTag();
        viewHolder.textViewTitle.setText(IMDBRepoList.get(position).getTitle());
        viewHolder.textViewPlot.setText(IMDBRepoList.get(position).getPlot());
        viewHolder.textViewYear.setText(IMDBRepoList.get(position).getYear());
        viewHolder.ImageViewPoster.setImageBitmap(IMDBRepoList.get(position).getImage());
    }

    private View reuseOrGenerateRowView(View convertView, ViewGroup parent) {
        View rowView;
        if(convertView != null){
            rowView = convertView;
        }else{
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_entry, parent, false);
            ViewHolder viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        }
        return rowView;
    }
}
