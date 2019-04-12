package com.example.waitlist;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.waitlist.Data.WaitListCounteract;
import com.example.waitlist.GuestAdapter.GuestViewHolder;

public class GuestAdapter extends RecyclerView.Adapter<GuestViewHolder> {
    private Context context;
    private Cursor cursor;

    public GuestAdapter(Context context, Cursor cursor) {

        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        return new GuestViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder GuestViewHolder, int i) {

        if (!cursor.moveToPosition(i)) {
            return;
        }

        String name = cursor.getString(cursor.getColumnIndex(WaitListCounteract.WaitListEntry.COLUMNS_GUEST_NAME));
        String size = cursor.getString(cursor.getColumnIndex(WaitListCounteract.WaitListEntry.COLUMNS_PARTY_SIZE));

        long id = cursor.getLong(cursor.getColumnIndex(WaitListCounteract.WaitListEntry._ID));

        GuestViewHolder.name.setText(name);
        GuestViewHolder.size.setText(size);
        GuestViewHolder.itemView.setTag(id);

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // we need to be sure that the previous cursor close
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;

        if (newCursor != null) {
            // force the recyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    class GuestViewHolder extends RecyclerView.ViewHolder {
        TextView name, size;

        GuestViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvName);
            size = itemView.findViewById(R.id.tvSize);

        }
    }

}
