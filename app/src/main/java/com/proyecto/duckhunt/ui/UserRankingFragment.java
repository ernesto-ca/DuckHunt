package com.proyecto.duckhunt.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.duckhunt.R;
import com.proyecto.duckhunt.common.Constantes;
import com.proyecto.duckhunt.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserRankingFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    List<User> userList;
    MyUserRecyclerViewAdapter adapter;
    FirebaseFirestore db;
    RecyclerView recyclerView;

    public UserRankingFragment() {
    }


    public static UserRankingFragment newInstance(int columnCount) {
        UserRankingFragment fragment = new UserRankingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_ranking_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }


            db.collection(Constantes.USERS_FIRESTORE)
                    .orderBy(Constantes.DOC_DUCKS, Query.Direction.DESCENDING)
                    .limit(5)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            userList = new ArrayList<>();
                            for(DocumentSnapshot documentSnapshot: task.getResult()){
                                User userItem = documentSnapshot.toObject(User.class);
                                userList.add(userItem);
                                adapter = new MyUserRecyclerViewAdapter(userList,getActivity());
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });

        }
        return view;
    }
}