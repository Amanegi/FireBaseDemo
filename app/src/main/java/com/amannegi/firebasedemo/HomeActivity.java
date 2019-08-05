package com.amannegi.firebasedemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton fab;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("All Data").child(uid);

        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        ;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });
    }

    private void addData() {

        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View mView = inflater.inflate(R.layout.input_layout, null);
        mDialog.setView(mView);

        final AlertDialog alertDialog = mDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        final EditText edtName = mView.findViewById(R.id.edtName);
        final EditText edtDescription = mView.findViewById(R.id.edtDescription);
        MaterialButton btnSave = mView.findViewById(R.id.btnSave);
        MaterialButton btnCancel = mView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    edtName.setError("Field Required!");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    edtName.setError("Field Required!");
                    return;
                }

                String id = mDatabase.push().getKey(); //get a random key from database
                String date = DateFormat.getDateInstance().format(new Date());

                DataModel dataModel = new DataModel(name, description, id, date);
                mDatabase.child(id).setValue(dataModel); // set the data to the id
                Toast.makeText(HomeActivity.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = mDatabase.orderByKey();
        FirebaseRecyclerOptions<DataModel> options = new FirebaseRecyclerOptions.Builder<DataModel>().setQuery(query, DataModel.class).build();

        FirebaseRecyclerAdapter<DataModel, MyViewHolder> adapter = new FirebaseRecyclerAdapter<DataModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position, @NonNull final DataModel dataModel) {
                myViewHolder.setName(dataModel.getName());
                myViewHolder.setDescription(dataModel.getDescription());
                myViewHolder.setDate(dataModel.getDate());
                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        String name = dataModel.getName();
                        String description = dataModel.getDescription();
                        updateData(id, name, description);
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void updateData(final String postId, final String name, final String description) {
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View mView = inflater.inflate(R.layout.input_layout, null);
        mDialog.setView(mView);

        final AlertDialog alertDialog = mDialog.create();

        final EditText edtName = mView.findViewById(R.id.edtName);
        final EditText edtDescription = mView.findViewById(R.id.edtDescription);
        TextView textView = mView.findViewById(R.id.textView);
        MaterialButton btnUpdate = mView.findViewById(R.id.btnSave);
        MaterialButton btnDelete = mView.findViewById(R.id.btnCancel);

        textView.setText("Edit Data");
        edtName.setText(name);
        edtName.setSelection(name.length()); //editing starts from end
        edtDescription.setText(description);

        btnUpdate.setText("Update");
        btnDelete.setText("Delete");

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = edtName.getText().toString().trim();
                String uDescription = edtDescription.getText().toString().trim();
                String uDate = DateFormat.getDateInstance().format(new Date());

                DataModel dataModel = new DataModel(uName,uDescription,postId,uDate);
                mDatabase.child(postId).setValue(dataModel);

                Toast.makeText(HomeActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(postId).removeValue();
                Toast.makeText(HomeActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });


        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_logout){
            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this,MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView itemName = mView.findViewById(R.id.item_name);
            itemName.setText(name);
        }

        public void setDescription(String description) {
            TextView itemDescription = mView.findViewById(R.id.item_description);
            itemDescription.setText(description);
        }

        public void setDate(String date) {
            TextView itemDate = mView.findViewById(R.id.item_date);
            itemDate.setText(date);
        }
    }


}
