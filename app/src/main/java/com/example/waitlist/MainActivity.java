package com.example.waitlist;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.waitlist.Data.WaitListCounteract;
import com.example.waitlist.Data.WaitlistDBHelper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    FloatingActionButton add_FloatingBtn;
    ImageView image_EmptyList;
    RecyclerView list_RecyclerView;
    LinearLayoutManager linearLayoutManager;

    GuestAdapter guestAdapter;
    WaitlistDBHelper waitlistDBHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_FloatingBtn = findViewById(R.id.add_floatingBtn);
        image_EmptyList = findViewById(R.id.image_emptyList);
        list_RecyclerView = findViewById(R.id.list_RecyclerView);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        list_RecyclerView.setLayoutManager(linearLayoutManager);
        list_RecyclerView.setHasFixedSize(true);
        list_RecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext() , DividerItemDecoration.VERTICAL));

        waitlistDBHelper = new WaitlistDBHelper(getApplicationContext());

        sqLiteDatabase = waitlistDBHelper.getWritableDatabase();

        cursor = getAllGuests();

        guestAdapter = new GuestAdapter(getApplicationContext() , cursor);

        list_RecyclerView.setAdapter(guestAdapter);

        if ( cursor.getCount() !=0 )
        {
            image_EmptyList.setVisibility(View.GONE);
        }
        else
        {
            image_EmptyList.setVisibility(View.VISIBLE);
        }

        add_FloatingBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showCustomDialog();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT |ItemTouchHelper.LEFT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i)
            {
                // get id
                long id = (long) viewHolder.itemView.getTag();
                // remove from database
                removeGuest(id);
                //update the list
                guestAdapter.swapCursor(getAllGuests());
                if (getAllGuests().getCount() == 0 )
                {
                    image_EmptyList.setVisibility(View.VISIBLE);
                }


            }
        }).attachToRecyclerView(list_RecyclerView);

    }

    private long addNewGuest (String name , String partySize)
    {
        ContentValues cv = new ContentValues();
        cv.put(WaitListCounteract.WaitListEntry.COLUMNS_GUEST_NAME, name);
        cv.put(WaitListCounteract.WaitListEntry.COLUMNS_PARTY_SIZE, partySize);
        return sqLiteDatabase.insert(WaitListCounteract.WaitListEntry.TABLE_NAME, null, cv);
    }

    private Cursor getAllGuests()
    {
        Cursor cursor = sqLiteDatabase.query(
                WaitListCounteract.WaitListEntry.TABLE_NAME ,
                null ,
                null ,
                null ,
                null ,
                null ,
                WaitListCounteract.WaitListEntry.COLUMNS_TIMESTAMP
        );
        return cursor;
    }

    private void showCustomDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.newguest_dialoge);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final Button add_guest = dialog.findViewById(R.id.addGuestBtn);
        final Button back = dialog.findViewById(R.id.backBtn);

        final EditText etName_d = dialog.findViewById(R.id.etDialoge_Name);
        final EditText etSize_d = dialog.findViewById(R.id.etDialoge_Size);

        final View layout = getLayoutInflater().inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        TextView text = layout.findViewById(R.id.tvText);
        text.setTextColor(getResources().getColor(R.color.colorPrimary));
        text.setText("Please Enter Valid Data");

        CardView lyt_cord = layout.findViewById(R.id.cord);
        lyt_cord.setCardBackgroundColor(getResources().getColor(R.color.text));

        final Toast toast = new Toast(getApplicationContext());

        add_guest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String name = etName_d.getText().toString();
                String size = etSize_d.getText().toString();

                if (name.length() == 0 || size.length() == 0)
                {
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
                else
                {
                    addNewGuest(name, size);
                    guestAdapter.swapCursor(getAllGuests());
                    image_EmptyList.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                toast.cancel();

            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }


    private boolean removeGuest (long id)
    {
        return sqLiteDatabase.delete(WaitListCounteract.WaitListEntry.TABLE_NAME,
                WaitListCounteract.WaitListEntry._ID + "=" + id , null) > 0;
    }
}
