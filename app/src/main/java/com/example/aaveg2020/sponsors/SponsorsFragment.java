package com.example.aaveg2020.sponsors;

import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aaveg2020.R;
import com.example.aaveg2020.adapters.SponsorsRecyclerAdapter;
import com.example.aaveg2020.api.AavegApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SponsorsFragment extends Fragment {

    //private List<SponsorModel> sponsorModelList;
    List<SponsorModel> sponsorModelList;
    private SponsorsRecyclerAdapter sponsorsRecyclerAdapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sponsorsRecyclerAdapter = new SponsorsRecyclerAdapter();
    }

    private void feedData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AavegApi.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AavegApi api = retrofit.create(AavegApi.class);

        Call<List<SponsorModel>> call = api.getSponsors();

        call.enqueue(new Callback<List<SponsorModel>>() {
            @Override
            public void onResponse(Call<List<SponsorModel>> call, Response<List<SponsorModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT).show();
                    return;
                }
                sponsorModelList = response.body();
                init(sponsorModelList);
            }

            @Override
            public void onFailure(Call<List<SponsorModel>> call, Throwable t) {
                Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_sponsors_fragment,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_sponsor_list);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        feedData();
    }

    private void init(List<SponsorModel> sponsorModelList){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            sponsorsRecyclerAdapter = new SponsorsRecyclerAdapter(sponsorModelList);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(sponsorsRecyclerAdapter);
    }
}