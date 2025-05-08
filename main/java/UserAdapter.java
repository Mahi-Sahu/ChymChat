package com.example.chymchatapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//create a UserAdapter that extends RecyclerView
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {
    MainActivity mainActivity;
    ArrayList<Users> usersArrayList;
    public UserAdapter(MainActivity mainActivity, ArrayList<Users> usersArrayList) {
        this.mainActivity=mainActivity;
        this.usersArrayList=usersArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //get the layout inflater from mainActivity
        //convert xml to view object using inflate()
        View view= LayoutInflater.from(mainActivity).inflate(R.layout.user_item,parent,false);
        //create a new holder of this view object and return it
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {
        //This method is called whenever a new item is about to be displayed.
        //It takes data from the dataset (ArrayList or List) and binds it to views inside hold.
        //So first retrieve the data. Since data is retrieved always in Users class as it has getter and setters and the data to be retrieved is in userArrayList
        Users users=usersArrayList.get(position);
        Log.d("BindView", "Binding user: " + users.username);
        holder.userName.setText(users.username);//gets the username from Usr class and sets it in the recycler View
        holder.userStatus.setText(users.status);
        //for setting image use picasso for that first dependency is needed to be added
        Picasso.get().load(users.profilepic).into(holder.userImage);


        //for redirecting to chat window
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mainActivity, ChatWindow.class);
                intent.putExtra("nameee", users.getUsername() != null ? users.getUsername() : "");
                intent.putExtra("receiverImg", users.getProfilepic() != null ? users.getProfilepic() : "");
                intent.putExtra("uid", users.getUserId() != null ? users.getUserId() : "");
                intent.putExtra("statusss", users.getStatus() != null ? users.getStatus() : "");
                mainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //return total number of items in the recycler view
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName,userStatus;
        public viewholder(@NonNull View itemView) {

            super(itemView);
            userImage=itemView.findViewById(R.id.userImage_user_item);//itemView refers to the individual row layout (or list item) that is displayed inside the RecyclerView.
            userName=itemView.findViewById(R.id.username_user_item);
            userStatus=itemView.findViewById(R.id.user_status_user_item);
        }
    }
}
