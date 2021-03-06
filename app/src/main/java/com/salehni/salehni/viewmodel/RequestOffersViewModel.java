package com.salehni.salehni.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.salehni.salehni.R;
import com.salehni.salehni.data.api.ApiData;
import com.salehni.salehni.data.api.InterfaceApi;
import com.salehni.salehni.data.model.ItemsInnerObject;
import com.salehni.salehni.data.model.OfferInnerObject;
import com.salehni.salehni.data.model.RequestOffersModel;
import com.salehni.salehni.data.model.SignInTokenModel;
import com.salehni.salehni.util.Constants;
import com.salehni.salehni.util.Global;
import com.salehni.salehni.util.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestOffersViewModel extends AndroidViewModel implements InterfaceApi {

    public MutableLiveData<ArrayList<RequestOffersModel>> arrayListMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> showProgressDialogMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> showToastMutableLiveData = new MutableLiveData<>();

    Context context;
    TinyDB tinyDB;

    public RequestOffersViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        tinyDB = new TinyDB(this.context);
    }

    public void getData() {

        if (Global.isNetworkAvailable(context)) {

            showProgressDialogMutableLiveData.setValue(true);

            SignInTokenModel signInTokenModel = tinyDB.getObject(Constants.login_token, SignInTokenModel.class);

            Map<String, String> headerParams = new HashMap<String, String>();

            JSONObject jsonObject = new JSONObject();

            try {
                //TODO get user id from tiny db
                jsonObject.put("user_id", signInTokenModel.getId());
                jsonObject.put("request_id", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String mRequestBody = jsonObject.toString();

            ApiData apiData = new ApiData();

            apiData.getdata(this.getApplication(), this, Constants.main_url + Constants.requestOffersScreen_Url, headerParams, mRequestBody);

        } else {

            showToastMutableLiveData.setValue(context.getResources().getString(R.string.validationInternetConnection));
        }


    }

    @Override
    public void callbackOnSuccess(String response) {

        showProgressDialogMutableLiveData.setValue(false);

        ArrayList<RequestOffersModel> requestOffersModels = new ArrayList<>();


        try {
            JSONObject jsonObject = new JSONObject(response);

            boolean status = jsonObject.getBoolean("status");
            String error = jsonObject.getString("error");
            JSONObject data = jsonObject.getJSONObject("data");

            if (status) {

                JSONArray offers = data.getJSONArray("offers");
                for (int i = 0; i < offers.length(); i++) {

                    JSONObject temp = offers.getJSONObject(i);

                    RequestOffersModel requestOffersModel = new RequestOffersModel();

                    requestOffersModel.setId(temp.getInt("id"));
                    requestOffersModel.setProvider_id(temp.getInt("provider_id"));
                    requestOffersModel.setRequest_id(temp.getInt("request_id"));
                    requestOffersModel.setFix_at(temp.getInt("fix_at"));
                    requestOffersModel.setProvider_name(temp.getString("provider_name"));
                    requestOffersModel.setTotal_price(temp.getString("total_price"));
                    requestOffersModel.setWorking_days(temp.getString("working_days"));


                    JSONObject offer = temp.getJSONObject("offer");
                    OfferInnerObject offerInnerObject = new OfferInnerObject();

                    offerInnerObject.setNote(offer.getString("note"));
                    offerInnerObject.setVoice_note(offer.getString("voice_note"));

                    ArrayList<ItemsInnerObject> itemsInnerObjects = new ArrayList<>();

                    JSONArray items = offer.getJSONArray("items");
                    for (int j = 0; j < items.length(); j++) {
                        JSONObject temp2 = items.getJSONObject(j);

                        ItemsInnerObject itemsInnerObject = new ItemsInnerObject();

                        itemsInnerObject.setId(temp2.getInt("id"));
                        itemsInnerObject.setPrice(temp2.getString("price"));
                        itemsInnerObject.setTitle_en(temp2.getString("title_en"));
                        itemsInnerObject.setTitle_ar(temp2.getString("title_ar"));
                        itemsInnerObjects.add(itemsInnerObject);
                    }
                    offerInnerObject.setItemsInnerObjects(itemsInnerObjects);

                    requestOffersModel.setOfferInnerObject(offerInnerObject);
                    requestOffersModels.add(requestOffersModel);

                }
            } else {
                showToastMutableLiveData.setValue(error);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayListMutableLiveData.setValue(requestOffersModels);

    }

    @Override
    public void callbackOnError(String response) {

        showProgressDialogMutableLiveData.setValue(false);
        showToastMutableLiveData.setValue(response);

    }
}
