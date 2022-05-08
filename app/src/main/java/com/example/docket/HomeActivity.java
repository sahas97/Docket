package com.example.docket;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Variables
    private RecyclerView recyclerView;
    public FloatingActionButton floatingActionButton;

    private DatabaseReference reference;
    public FirebaseAuth mAuth;
    public FirebaseUser fUser;
    public String onlineUserId;

    private ProgressDialog loader;

    private String key = "";
    private String task;
    private String description;

    //Navigation Drawer menu Variables
    static final float END_SCALE = 0.7f;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // hide the statusBar
        setContentView(R.layout.activity_home);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Navigation Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);

        navigationDrawer(); // call the navigation drawer method

        // Hooks
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.reView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        fUser = mAuth.getCurrentUser();
        assert fUser != null;
        onlineUserId = fUser.getUid(); // get the user id from database

        /* in hear for calling the database i use database link because in adding realtime database to app before i
        create the config jason file so then i got error in reviewed data from database because my data base in
        Singapore data bse asia pacific but in calling, it call USA default database. so preventing from this the
        only solution is generate again jason file or add database link to getInstance() i did easiest thing*/
        reference = FirebaseDatabase.getInstance("https://docket-test-31e20-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("tasks").child(onlineUserId);

        floatingActionButton = findViewById(R.id.addFab);

        // when clicking floating action button
        floatingActionButton.setOnClickListener(view -> {
            addTask(); // call add task method
        });
    }

    // Navigation drawer functions
    private void navigationDrawer() {
        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_start); // item highlighted

        // when click the menu icon
        menuIcon.setOnClickListener(view -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else drawerLayout.openDrawer(GravityCompat.START);
        });

        animateNavigationDrawer(); // call the animateNavigation method
    }

    //animateNavigation method
    private void animateNavigationDrawer() {

        drawerLayout.setScrimColor(getResources().getColor(R.color.light_yellow));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);

            }
        });
    }

    // when back button press
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    // navigation menu icon on clicks
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // get start button click
            case R.id.nav_start:
                // if user want stat again he will sign out and redirect to signup page
                startAgain(); // call the startAgain method
                break;
            //when sign out clicked
            case R.id.nav_signOut:
                signOut(); // call the signOut method
                break;
            // about clicked
            case R.id.nav_about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                break;
        }
        return true;
    }

    // adding task
    private void addTask() {
        // Add popup dialog
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.add_task_file, null);
        myDialog.setView(myView);

        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
        //dialog.show();

        // Hooks
        final EditText task = myView.findViewById(R.id.task_in);
        final EditText description = myView.findViewById(R.id.des);
        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);
        ImageView cancelPopUp = myView.findViewById(R.id.add_task_ignore);

        cancelPopUp.setOnClickListener(view -> dialog.dismiss());

        // cancel task
        cancel.setOnClickListener(view -> dialog.dismiss());

        // save Task
        save.setOnClickListener(view -> {
            String mTask = task.getText().toString().trim();
            String mDes = description.getText().toString().trim();
            String id = reference.push().getKey();
            String date = DateFormat.getDateTimeInstance().format(new Date());

            if (TextUtils.isEmpty(mTask)) {
                task.setError("Task Required");
                return;
            }
            if (TextUtils.isEmpty(mDes)) {
                description.setError("Description Required");
                return;
            } else {
                loader.setMessage("Adding your data");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                Model model = new Model(mTask, mDes, id, date);
                assert id != null;
                reference.child(id).setValue(model).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(HomeActivity.this, "Task has been listed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        String error = Objects.requireNonNull(task1.getException()).toString();
                        Toast.makeText(HomeActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                });
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference, Model.class).build();

        FirebaseRecyclerAdapter<Model, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDesc(model.getDescription());

                // when clicking update button
                holder.mView.setOnClickListener(view -> {
                    key = getRef(position).getKey();
                    task = model.getTask();
                    description = model.getDescription();

                    updateTask();
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    // update
    private void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this, R.style.RetrievedLayoutDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_tasks, null);
        myDialog.setView(view);

        final AlertDialog dialog = myDialog.create();
        final EditText mTask = view.findViewById(R.id.mEdTeTask);
        final EditText mDescription = view.findViewById(R.id.mEdDes);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        Button delButton = view.findViewById(R.id.delBtn);
        Button updateButton = view.findViewById(R.id.updateBtn);
        ImageView update_cancel = view.findViewById(R.id.update_task_ignore);

        update_cancel.setOnClickListener(view1 -> dialog.dismiss());

        // when update button click
        updateButton.setOnClickListener(view12 -> {
            task = mTask.getText().toString().trim();
            description = mDescription.getText().toString().trim();
            String date = DateFormat.getDateTimeInstance().format(new Date());

            Model model = new Model(task, description, key, date);

            reference.child(key).setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(HomeActivity.this, "Data has been updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(HomeActivity.this, "Failed to update " + error, Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        // when delete Button clicked
        delButton.setOnClickListener(view13 -> {
            reference.child(key).removeValue().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Toast.makeText(HomeActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(HomeActivity.this, "Failed to delete task" + error, Toast.LENGTH_SHORT).show();
                }

                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            });

            dialog.dismiss();
        });
        dialog.show();
    }

    // sign out method
    private void signOut() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this, R.style.signOutLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.sign_out_layout, null);
        myDialog.setView(view);

        // create alert dialog
        final AlertDialog dialog = myDialog.create();

        // Hooks
        Button yesButton = view.findViewById(R.id.YesBtn);
        Button noButton = view.findViewById(R.id.NoBtn);
        ImageView signOut_cancel = view.findViewById(R.id.signOut_task_ignore);

        signOut_cancel.setOnClickListener(view1 -> dialog.dismiss());

        // when yes button click
        yesButton.setOnClickListener(view12 -> {
            // Invisible dialog then sign out the user and og to start page
            dialog.dismiss();
            mAuth.signOut();
            Toast.makeText(getApplicationContext(), "Have a nice day, Lodged Out....", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, GetStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // when no Button clicked
        noButton.setOnClickListener(view13 -> {
            dialog.cancel(); // Invisible dialog
        });

        // show the dialog
        dialog.show();

    }

    // start again method
    private void startAgain() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this, R.style.signOutLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.start_again_layout, null);
        myDialog.setView(view);

        // create alert dialog
        final AlertDialog dialog = myDialog.create();

        // Hooks
        Button yesButton = view.findViewById(R.id.YesBtn);
        Button noButton = view.findViewById(R.id.NoBtn);
        ImageView start_again_cancel = view.findViewById(R.id.start_again_ignore);

        start_again_cancel.setOnClickListener(view1 -> dialog.dismiss());

        // when yes button click
        yesButton.setOnClickListener(view12 -> {
            // Invisible dialog then sign out the user and og to signup page
            dialog.dismiss();
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            Toast.makeText(getApplicationContext(), "Wish you a great tour!", Toast.LENGTH_SHORT).show();
            finish();
        });

        // when no Button clicked
        noButton.setOnClickListener(view13 -> {
            dialog.cancel(); // Invisible dialog
        });

        // show the dialog
        dialog.show();

    }


    // extends getters form my view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTask(String task) {
            TextView taskTextView = mView.findViewById(R.id.taskTv);
            taskTextView.setText(task);
        }

        public void setDesc(String desc) {
            TextView descTextView = mView.findViewById(R.id.desTv);
            descTextView.setText(desc);
        }

        public void setDate(String date) {
            TextView dateTextView = mView.findViewById(R.id.dTv);
            dateTextView.setText(date);
        }
    }
}