package com.example.aaveg2020.Home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.aaveg2020.LogOutInterface;
import com.example.aaveg2020.MainActivity;
import com.example.aaveg2020.R;
import com.example.aaveg2020.Scoreboard.EventsModel;
import com.example.aaveg2020.Scoreboard.ScoreboardModel;
import com.example.aaveg2020.Scoreboard.ScoreboardPresenter;
import com.example.aaveg2020.Scoreboard.ScoreboardPresenterImpl;
import com.example.aaveg2020.Scoreboard.ui.Overall.OverallModel;
import com.example.aaveg2020.UserUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements HomeView {

    ScoreboardPresenter presenter;
    ScoreboardModel scoreboardModel;
    RecyclerView recentEvents;
    RecentEventsAdapter adapter;
    ImageView hostelImage;
    ArrayList<EventsModel> events;
    SnapHelper helper;
    TextView overallPlace,culturalsPlace,sportsPlace,spectrumPlace, madeWith;
    OverallModel total,culturals,sports,spectrum;
    final int spacing = 20;
    int count=0,mCount=0,hCount;
    View dialog;
    AlertDialog loadingDialog;
    Context context;
    TextView aananth;
    CardView cult,sport,spec;
    SharedPreferences pref;
    String hostel;
    Toolbar toolbar;
    ImageView logOut;
    LogOutInterface logOutInterface;
    Runnable runnable;
    OpenScoreboard openScoreboard;
    Snackbar snackbar;

    public HomeFragment(LogOutInterface logOutInterface) {
        this.logOutInterface = logOutInterface;
    }

//    public HomeFragment(OpenScoreboard openScoreboard) {
//        this.openScoreboard=openScoreboard;
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        TextView tv = dialog.findViewById(R.id.progressDialog_textView);
        tv.setText("Loading...");
        loadingDialog = new AlertDialog.Builder(context).setView(dialog).setCancelable(false).create();
        loadingDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.fragment_home,container,false);
        pref= getContext().getSharedPreferences("Aaveg2020", MODE_PRIVATE);
        hostel=pref.getString("hostel",null);
        presenter = new ScoreboardPresenterImpl(this);
        presenter.getTotal();
        recentEvents=mView.findViewById(R.id.home_hostel_events);
        overallPlace=mView.findViewById(R.id.overall_place);
        culturalsPlace=mView.findViewById(R.id.culturals_place);
        sportsPlace=mView.findViewById(R.id.sports_place);
        aananth=mView.findViewById(R.id.reccent_events_text);
        spectrumPlace=mView.findViewById(R.id.spectrum_place);
        hostelImage=mView.findViewById(R.id.home_hostel_img);
        toolbar = mView.findViewById(R.id.toolbar_main);
        logOut=toolbar.findViewById(R.id.logout);
        cult=mView.findViewById(R.id.culturals_place_card);
        sport=mView.findViewById(R.id.sports_place_card);
        spec=mView.findViewById(R.id.spectrum_place_card);
        madeWith = mView.findViewById(R.id.tv_made_with_home);
        count=0;

        madeWith.setClickable(true);
        madeWith.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "Made with ♥ by <a href=\"https://delta.nitt.edu\" target=\"_blank\">DeltaForce</a> and Aaveg Design Team";
        madeWith.setText(Html.fromHtml(text));
        madeWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hCount++;
                if(hCount>=5)
                {
                    hCount=0;
                    Intent intent=new Intent(getContext(),HameedActivity.class);
                    startActivity(intent);
                }
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogOutClicked();
            }
        });
        aananth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if(count>=5)
                {
                    count=0;
                    Intent intent=new Intent(getContext(),HopeThisWorks.class);
                    startActivity(intent);
                }
            }
        });
        hostelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCount++;
                if(mCount>=5)
                {
                    mCount=0;
                    Intent intent=new Intent(getContext(),MarioActivity.class);
                    startActivity(intent);
                }
            }
        });
        cult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.openScoreboard(0);
            }
        });
        sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.openScoreboard(2);
            }
        });
        spec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.openScoreboard(1);
            }
        });

        setHostelImg();
        return mView;
    }

    private void onLogOutClicked() {
        logOutInterface.doLogout();
    }

    @Override
    public void onGetScoreboardSuccess(ScoreboardModel scoreboardModel) {
        this.scoreboardModel=scoreboardModel;
        try {
            this.setHostelEvents();
            this.calculateTotal();
            this.setPositions();
        }
        catch (Exception e){
            Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();

        }
    }
    private void setHostelImg(){
        switch (hostel){
            case "Agate":
                hostelImage.setImageResource(R.drawable.agatelogo);
                break;
            case "Azurite":
                hostelImage.setImageResource(R.drawable.azuritelogo);
                break;
            case "Bloodstone":
                hostelImage.setImageResource(R.drawable.bloodstonelogo);
                break;
            case "Cobalt":
                hostelImage.setImageResource(R.drawable.cobaltlogo);
                break;
            case "Opal":
                hostelImage.setImageResource(R.drawable.opallogo);
                break;
        }
    }
    private void assignDataToRecycler()
    {
        adapter=new RecentEventsAdapter(events);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        helper = new LinearSnapHelper();
        recentEvents.setLayoutManager(layoutManager);
        recentEvents.setAdapter(adapter);
        helper.attachToRecyclerView(recentEvents);
        layoutManager.scrollToPosition(1);
        recentEvents.setPadding(spacing, spacing, spacing, spacing);
        recentEvents.setClipToPadding(false);
        recentEvents.setClipChildren(false);
        recentEvents.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });
        //recentEvents.smoothScrollToPosition(1);
        loadingDialog.dismiss();
    }
    private void setHostelEvents()
    {
        if(hostel!=null){

            switch (hostel){
                case "Agate":
                    events=scoreboardModel.getRecents().getAgate();
                    break;
                case "Azurite":
                    events=scoreboardModel.getRecents().getAzurite();
                    break;
                case "Bloodstone":
                    events=scoreboardModel.getRecents().getBloodstone();
                    break;
                case "Cobalt":
                    events=scoreboardModel.getRecents().getCobalt();
                    break;
                case "Opal":
                    events=scoreboardModel.getRecents().getOpal();
                    break;
                default:
                    events=scoreboardModel.getRecents().getOpal();
            }

        }
        else {
            events=scoreboardModel.getRecents().getAgate();
        }
        assignDataToRecycler();
    }
    private void calculateTotal()
    {
        total=new OverallModel(0,0,0,0,0);
        culturals=scoreboardModel.getStandings().getCulturals();
        sports=scoreboardModel.getStandings().getSports();
        spectrum=scoreboardModel.getStandings().getSpectrum();
        total.setAgate(culturals.getAgate()+spectrum.getAgate());
        total.setAzurite(culturals.getAzurite()+spectrum.getAzurite());
        total.setBloodstone(culturals.getBloodstone()+spectrum.getBloodstone());
        total.setCobalt(culturals.getCobalt()+spectrum.getCobalt());
        total.setOpal(culturals.getOpal()+spectrum.getOpal());
    }
    private int getPosition(OverallModel overallModel)
    {
        int pos = -1;
        ArrayList<HostelScore> scores=new ArrayList<HostelScore>();
        scores.add(new HostelScore("Agate",overallModel.getAgate()));
        scores.add(new HostelScore("Azurite",overallModel.getAzurite()));
        scores.add(new HostelScore("Bloodstone",overallModel.getBloodstone()));
        scores.add(new HostelScore("Cobalt",overallModel.getCobalt()));
        scores.add(new HostelScore("Opal",overallModel.getOpal()));
        scores=this.doSelectionSort(scores);
        for (int i=0;i<5;i++)
        {
            if(scores.get(i).name.equals(hostel)){
                pos=i;
            }
        }
        return pos;
    }
    private void setPositions(){
        this.setSuffix(getPosition(total),overallPlace);
        this.setSuffix(getPosition(culturals),culturalsPlace);
        this.setSuffix(getPosition(sports),sportsPlace);
        this.setSuffix(getPosition(spectrum),spectrumPlace);
    }
    public static ArrayList<HostelScore> doSelectionSort(ArrayList<HostelScore> arr) {
        for (int i = 0; i < arr.size(); i++) {
            // find position of smallest num between (i + 1)th element and last element
            int pos = i;
            for (int j = i; j < arr.size(); j++) {
                if (arr.get(j).score > arr.get(pos).score)
                    pos = j;
            }
            // Swap min (smallest num) to current position on array
            HostelScore min = arr.get(pos);
            arr.set(pos, arr.get(i));
            arr.set(i, min);
        }
        return arr;
    }
    private void setSuffix(int pos,TextView tv){
        switch (pos){
            case 0:
                tv.setText(Html.fromHtml(Integer.toString(pos+1)+"<sup><small>st</small></sup>"));
                break;
            case 1:
                tv.setText(Html.fromHtml(Integer.toString(pos+1)+"<sup><small>nd</small></sup>"));
                break;
            case 2:
                tv.setText(Html.fromHtml(Integer.toString(pos+1)+"<sup><small>rd</small></sup>"));
                break;
            default:
                tv.setText(Html.fromHtml(Integer.toString(pos+1)+"<sup><small>th</small></sup>"));
                break;
        }
    }
    public static class HostelScore{
        String name;
        int score;

        public HostelScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }

}