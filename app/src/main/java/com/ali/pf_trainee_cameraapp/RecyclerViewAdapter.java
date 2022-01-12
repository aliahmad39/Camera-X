package com.ali.pf_trainee_cameraapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    Singleton singleton = Singleton.getInstance();

    ArrayList<Uri> list = singleton.list;
    private Context mcontext;

    public RecyclerViewAdapter() {
    }

    public RecyclerViewAdapter(Context mcontext) {

        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
       Uri uri = list.get(position);
        holder.courseIV.setImageURI(uri);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = uri.toString();
                Intent intent = new Intent(mcontext , ZoomImageActivity.class);
                intent.putExtra("uriData" , string);
                mcontext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onLongClick(View view) {
                holder.itemView.setBackgroundColor(R.color.background);
                new AlertDialog.Builder(mcontext)
                        .setIcon(R.drawable.ic_delete)
                        .setTitle("Delete")
                        .setMessage("Are U Sure U want to delete this image?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.remove(uri);
                                notifyDataSetChanged();
                                String string = uri.toString();
                                singleton.savedList.remove(string);
                                saveArrayList(singleton.savedList,"myGalleryList1" );
                                deleteFile(uri);
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        holder.itemView.setBackgroundColor(R.color.white);
                        dialogInterface.dismiss();
                    }
                }).show();


                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return list.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

      //  private TextView courseTV;
        private ImageView courseIV;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
         //   courseTV = itemView.findViewById(R.id.idTVCourse);
            courseIV = itemView.findViewById(R.id.idIVcourseIV);
        }
    }

    public void saveArrayList(ArrayList<String> list, String key  ){
        SharedPreferences sp = mcontext.getSharedPreferences("PfCameraX" , MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
        // editor.commit();
    }
    public void deleteFile(Uri uri) {

        String filename = uri.getLastPathSegment();
       final File file = makeFile("Download", filename);


        if (file.exists()) {
            file.delete();
            Toast.makeText(mcontext, "file deleted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mcontext, "file not exist", Toast.LENGTH_SHORT).show();
        }
    }

    public File makeFile(String destination, String filename) {
        String root = Environment.getExternalStorageDirectory().toString();
        //  String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//        if (!isStoragePermissionGranted())
//            return null;

        File myDir = new File(root, destination);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, filename);


        //  By using this line you will be able to see saved images in the gallery view.
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file) );

        //      Toast.makeText(this, file.getPath() + "", Toast.LENGTH_SHORT).show();


        return file;
    }


}

