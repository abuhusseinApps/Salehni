package com.salehni.salehni.view.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.salehni.salehni.R;

import com.salehni.salehni.data.model.MyRequestFragModel;
import com.salehni.salehni.data.model.MyRequestModel;

import com.salehni.salehni.data.model.SignInTokenModel;
import com.salehni.salehni.util.Constants;
import com.salehni.salehni.util.Global;
import com.salehni.salehni.util.TinyDB;
import com.salehni.salehni.view.activities.MainPageCustomerActivity;
import com.salehni.salehni.view.adapters.MyRequestAdapter;
import com.salehni.salehni.viewmodel.MyRequestViewModel;

import net.skoumal.fragmentback.BackFragment;

import java.util.ArrayList;


public class MyRequestFragment extends Fragment implements AdapterView.OnItemClickListener {

    RecyclerView requests_Rv;
    MyRequestAdapter myRequestAdapter;
    ArrayList<MyRequestModel> requestModels;
    ArrayList<MyRequestFragModel> myRequestFragModels;

    MyRequestViewModel myRequestViewModel;
    MyRequestFragModel myRequestFragModel;

    SwipeRefreshLayout swipeRefreshLayout;

    boolean progress = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestModels = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_request, container, false);

        requests_Rv = (RecyclerView) view.findViewById(R.id.requests_Rv);
        //testingData();


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

// listen refresh event
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress = false;
                myRequestViewModel.getData();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.orange,
                R.color.orange,
                R.color.orange,
                R.color.orange);

        myRequestViewModel = ViewModelProviders.of(requireActivity()).get(MyRequestViewModel.class);
        myRequestViewModel.showProgressDialogMutableLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (progress) {
                    if (aBoolean) {
                        Global.progress(getActivity());
                    } else {
                        Global.progressDismiss();
                    }
                }


            }
        });

        myRequestViewModel.showToastMutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Global.toast(getActivity().getApplicationContext(), s);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        myRequestViewModel.arrayListMutableLiveData.observe(this, new Observer<ArrayList<MyRequestModel>>() {
            @Override
            public void onChanged(ArrayList<MyRequestModel> myRequestModels) {

                swipeRefreshLayout.setRefreshing(false);

                if (myRequestModels != null) {

                    requestModels.clear();

                    requestModels.addAll(myRequestModels);

                    if (myRequestAdapter != null) {

                        myRequestAdapter.notifyDataSetChanged();
                    } else {

                        intiRecView(requestModels);
                    }

                }

            }


        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        myRequestViewModel.getData();

        MainPageCustomerActivity.title_Tv.setText(getResources().getString(R.string.my_request));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        RequestOffersFragment requestOffersFragment = new RequestOffersFragment();
        setFragment(requestOffersFragment, "requestOffersFragment");

    }

//    private void testingData() {
//
//        myRequestFragModels = new ArrayList<>();
//
//        for (int i = 0; i < 20; i++) {
//            MyRequestModel myRequestModel = new MyRequestModel();
//            myRequestModel.setId(i + 1);
//
//            myRequestFragModels.add(myRequestModel);
//        }
//
//        intiRecView(myRequestFragModels);
//    }

    public void intiRecView(ArrayList<MyRequestModel> myRequestModels) {

        requests_Rv.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        requests_Rv.setLayoutManager(layoutManager);

        myRequestAdapter = new MyRequestAdapter(getActivity(), myRequestModels, this);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_recycleview_divider_height));

        requests_Rv.addItemDecoration(itemDecorator);


        requests_Rv.setAdapter(myRequestAdapter);


    }

    public void setFragment(Fragment fragment, String tag) {

        myRequestAdapter = null;
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainFrameLayout, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setData(MyRequestFragModel myRequestFragModel) {
        // team_name_tv.setText(myRequestFragModel.team_name);


    }
}
