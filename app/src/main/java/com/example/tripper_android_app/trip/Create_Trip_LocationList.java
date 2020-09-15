package com.example.tripper_android_app.trip;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.location.Location;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 建立行程2-景點選擇
 *
 * @author cooperhsieh
 * @version 2020.09.10
 */

public class Create_Trip_LocationList extends Fragment {
    private final static String TAG = "TAG_Loc";
    private RecyclerView rvSelectLoc;
    private CommonTask locGetAllTask;
    private List<ImageTask> imageTasks;
    private List<Location> locationList;
    private TextView textLocName;
    private MainActivity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        imageTasks = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_trip_location_lsit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbarForLoc);
        toolbar.setTitle("選擇景點");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        textLocName = view.findViewById(R.id.textLocName);


        SearchView searchLoc = view.findViewById(R.id.searchLoc);
        rvSelectLoc = view.findViewById(R.id.rvSelectLoc);
        searchLoc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showLocation(locationList);
                } else {
                    List<Location> searchLocation = new ArrayList<>();
                    for (Location location : locationList) {
                        if (location.getName().toUpperCase().contains(newText.toUpperCase())) {
                            searchLocation.add(location);
                        } else if (location.getAddress().toUpperCase().contains(newText.toUpperCase())) {
                            searchLocation.add(location);
                        }
                    }
                    showLocation(searchLocation);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        rvSelectLoc = view.findViewById(R.id.rvSelectLoc);
        rvSelectLoc.setLayoutManager(new LinearLayoutManager(activity));
        locationList = getLocation();
        showLocation(locationList);


    }


    private List<Location> getLocation() {
        List<Location> locationList = null;

        if (Common.netWorkConnected(activity)) {
            String url = Common.URL_SERVER + "LocationServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            locGetAllTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = locGetAllTask.execute().get();
                Type listType = new TypeToken<List<Location>>() {
                }.getType();
                locationList = new Gson().fromJson(jsonIn, listType);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No NetWork Connection available");
        }
        return locationList;
    }

    private void showLocation(List<Location> locationList) {
        if (locationList == null || locationList.isEmpty()) {
            Common.showToast(activity, "No Locations Found");
            return;
        }
        LocationAdapter locationAdapter = (LocationAdapter) rvSelectLoc.getAdapter();
        if (locationAdapter == null) {
            rvSelectLoc.setAdapter(new LocationAdapter(activity, locationList));
        } else {
            locationAdapter.setLocation(locationList);
            locationAdapter.notifyDataSetChanged();
        }
    }

    private class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Location> locationList;
        private int imageSize;

        LocationAdapter(Context context, List<Location> locationList) {
            layoutInflater = LayoutInflater.from(context);
            this.locationList = locationList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setLocation(List<Location> locationList) {
            this.locationList = locationList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textLocName, textLocAdd;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.locPic);
                textLocName = itemView.findViewById(R.id.textLocName);
                textLocAdd = itemView.findViewById(R.id.textLocAdd);
            }
        }

        @Override
        public int getItemCount() {
            return locationList == null ? 0 : locationList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_select_location, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull LocationAdapter.MyViewHolder holder, int position) {
            final Location location = locationList.get(position);
            String url = Common.URL_SERVER + "LocationServlet";

            String locId = location.getLocId();
            ImageTask imageTask = new ImageTask(url, locId, imageSize, holder.imageView);
            imageTask.execute();

            imageTasks.add(imageTask);
            holder.textLocName.setText(location.getName());
            holder.textLocAdd.setText(location.getAddress());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("locationDetail", location);
                    Navigation.findNavController(v).navigate(R.id.action_create_Trip_LocationList_to_createTripLocationDetail, bundle);
                }
            });

        }


    }


}