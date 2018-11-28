package project.csci6365.securesmsapp;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends Adapter<MyAdapter.MyViewHolder> {
    private List<String> dataset;
    private int pos;
    private boolean visible;
    private View.OnClickListener myOnClickListener = new MyOnClickListener();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends ViewHolder {
        // each data item is just a string in this case
        TextView textView;
        TextView textView2;
        MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.userid);
            textView2 = v.findViewById(R.id.new_message);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    MyAdapter(List<String> dataset) {
        this.dataset = dataset;
        this.pos = -1;
    }

    MyAdapter(List<String> dataset, int pos) {
        this.dataset = dataset;
        this.pos = pos;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        v.setOnClickListener(myOnClickListener);

        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(dataset.get(position));
        if (pos == position)
            holder.textView2.setVisibility(View.VISIBLE);
        else
            holder.textView2.setVisibility(View.INVISIBLE);
        holder.textView2.setText("New Message");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
