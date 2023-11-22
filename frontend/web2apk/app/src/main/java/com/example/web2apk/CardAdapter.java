package com.example.web2apk;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.github.pavlospt.roundedletterview.RoundedLetterView;

import java.util.ArrayList;
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>
{
    ArrayList<String> app_names = new ArrayList<>();

    ArrayList<String> app_links = new ArrayList<>();
    Context context;

    DBHandler dbHandler;

    public void loadDB()
    {
        dbHandler = new DBHandler(context);

        Cursor cursor = dbHandler.getValues();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
//                    @SuppressLint("Range") Integer id = cursor.getInt(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") String appname = cursor.getString(cursor.getColumnIndex("appname"));
                    @SuppressLint("Range") String applink = cursor.getString(cursor.getColumnIndex("apkLink"));

                    this.app_names.add(appname);
                    this.app_links.add(applink);

//                    Toast.makeText(context,courseName+" "+courseDescription+" "+coursePrice,Toast.LENGTH_LONG).show();

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    public CardAdapter(Context context)
    {
        this.context = context;
        loadDB();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView app_name;

        RoundedLetterView roundedLetterView;

        ImageButton qr_btn;

        ImageButton delete_btn;

        ImageButton copy_btn;

        ViewHolder(View itemView)
        {
            super(itemView);

            app_name = itemView.findViewById(R.id.card_appname);
            roundedLetterView = itemView.findViewById(R.id.rounded_profile);
            qr_btn = itemView.findViewById(R.id.QRcode_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            copy_btn = itemView.findViewById(R.id.copy_btn);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.app_name.setText(app_names.get(position));
        holder.roundedLetterView.setTitleText(app_names.get(position).substring(0,1));

        holder.qr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,QRCodeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("APK_LINK", app_links.get(position));
                intent.putExtra("APP_NAME", app_names.get(position));
                context.startActivity(intent);
            }
        });

        holder.copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a ClipData object with the text to copy
                ClipData clip = ClipData.newPlainText("label", app_links.get(position));

                // Set the data to the clipboard
                clipboard.setPrimaryClip(clip);

                // Show a toast indicating that the text has been copied
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbHandler.deleteDB(app_names.get(position));

                // Trigger the refresh when needed
                Intent newintent = new Intent("com.example.web2apk.ACTION_REFRESH");
                LocalBroadcastManager.getInstance(context).sendBroadcast(newintent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.app_names.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_card, parent, false);
        return new ViewHolder(view);
    }
}
