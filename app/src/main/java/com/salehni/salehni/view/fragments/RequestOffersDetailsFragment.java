package com.salehni.salehni.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import com.salehni.salehni.R;

import com.salehni.salehni.data.model.ItemsInnerObject;

import com.salehni.salehni.data.model.RequestOffersModel;
import com.salehni.salehni.util.Constants;
import com.salehni.salehni.util.Global;
import com.salehni.salehni.view.activities.MainPageCustomerActivity;
import com.salehni.salehni.view.adapters.RequestOffersDetailsAdapter;
import com.salehni.salehni.viewmodel.AcceptOfferViewModel;
import com.salehni.salehni.viewmodel.CustomRequestViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class RequestOffersDetailsFragment extends Fragment implements AdapterView.OnItemClickListener {

    RecyclerView items_Rv;
    RequestOffersDetailsAdapter requestOffersDetailsAdapter;
//    ArrayList<RequestOffersDetailsModel> requestOffersDetailsModels;

    LinearLayout acceptOffer_Ll;

    RequestOffersModel requestOffersModel;

    TextView totalPrice_Tv;
    TextView working_days_Tv;
    TextView note_Tv;
    TextView sumPrice_Tv;

    int fix_at = 1;

    AcceptOfferViewModel acceptOfferViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_offers_details, container, false);
        items_Rv = (RecyclerView) view.findViewById(R.id.items_Rv);
        acceptOffer_Ll = (LinearLayout) view.findViewById(R.id.acceptOffer_Ll);

        totalPrice_Tv = (TextView) view.findViewById(R.id.totalPrice_Tv);
        working_days_Tv = (TextView) view.findViewById(R.id.working_days_Tv);
        note_Tv = (TextView) view.findViewById(R.id.note_Tv);
        sumPrice_Tv = (TextView) view.findViewById(R.id.sumPrice_Tv);

        acceptOffer_Ll.requestFocus();

        getExtra();
        setData();
        initialItemsList();

        acceptOffer_Ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptOfferViewModel.getData(requestOffersModel);
            }
        });

        acceptOfferViewModel = ViewModelProviders.of(getActivity()).get(AcceptOfferViewModel.class);
        acceptOfferViewModel.showProgressDialogMutableLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    Global.progress(getActivity());
                } else {
                    Global.progressDismiss();
                }

            }
        });

        acceptOfferViewModel.showToastMutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Global.toast(getActivity().getApplicationContext(), s);

            }
        });

        acceptOfferViewModel.acceptOfferStatusModelMutableLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean status) {

                if (status) {

                    if (fix_at == 0) {

                    } else {
                        AcceptedOfferFragment acceptedOfferFragment = new AcceptedOfferFragment();
                        setFragment(acceptedOfferFragment, "acceptedOfferFragment");
                    }
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        MainPageCustomerActivity.title_Tv.setText(getResources().getString(R.string.request_details));

    }

    private void initialItemsList() {

        intiRecView(requestOffersModel.getOfferInnerObject().getItemsInnerObjects());
    }

    public void intiRecView(ArrayList<ItemsInnerObject> itemsInnerObjects) {

        items_Rv.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        items_Rv.setLayoutManager(layoutManager);

        requestOffersDetailsAdapter = new RequestOffersDetailsAdapter(getActivity(), itemsInnerObjects, this);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_recycleview_divider_height));

        items_Rv.addItemDecoration(itemDecorator);


        items_Rv.setAdapter(requestOffersDetailsAdapter);

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void setFragment(Fragment fragment, String tag) {
        requestOffersDetailsAdapter = null;
        FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainFrameLayout, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void getExtra() {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            requestOffersModel = bundle.getParcelable(Constants.selectedRequest);

            fix_at = requestOffersModel.getFix_at();
        }
    }

    public void setData() {
        totalPrice_Tv.setText(requestOffersModel.getTotal_price());
        working_days_Tv.setText(requestOffersModel.getWorking_days());
        note_Tv.setText(requestOffersModel.getOfferInnerObject().getNote());
        sumPrice_Tv.setText(setSumPrice());
    }

    public String setSumPrice() {
        int sum = 0;
        for (int i = 0; i < requestOffersModel.getOfferInnerObject().getItemsInnerObjects().size(); i++) {
            sum = sum + Integer.parseInt(requestOffersModel.getOfferInnerObject().getItemsInnerObjects().get(i).getPrice());
        }
        return sum + "";
    }
}
