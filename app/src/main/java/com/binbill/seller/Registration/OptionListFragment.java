package com.binbill.seller.Registration;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.binbill.seller.Adapter.CityAdapter;
import com.binbill.seller.Adapter.DeliveryDistanceAdapter;
import com.binbill.seller.Adapter.LocalityAdapter;
import com.binbill.seller.Adapter.MainCategoryAdapter;
import com.binbill.seller.Adapter.StateAdapter;
import com.binbill.seller.Constants;
import com.binbill.seller.Interface.ItemSelectedInterface;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.StateCityModel;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import java.util.ArrayList;

public class OptionListFragment extends Fragment implements SearchView.OnQueryTextListener{
    private static final String ARG_OBJECT_LIST = "listToDisplay";
    private static final String ARG_IDENTIFIER = "indentifier";

    private ArrayList<MainCategory> mDisplayList;
    private ArrayList<String> mDeliveryDistance;
    private ArrayList<StateCityModel> mStateCityModel;
    private ArrayList<StateCityModel.CityModel> mCityList;
    private ArrayList<StateCityModel.LocalityModel> mLocalityList;
    private int mIdentifier;

    private OnOptionListInteractionListener mListener;
    private StateAdapter mStateAdapter;
    private CityAdapter mCityAdapter;
    private LocalityAdapter mLocalityAdapter;

    public OptionListFragment() {
    }

    public static OptionListFragment newInstance(ArrayList<?> list, int identifier) {
        OptionListFragment fragment = new OptionListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OBJECT_LIST, list);
        args.putInt(ARG_IDENTIFIER, identifier);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIdentifier = getArguments().getInt(ARG_IDENTIFIER);
            switch (mIdentifier) {
                case Constants.MAIN_CATEGORY:
                    mDisplayList = (ArrayList<MainCategory>) getArguments().getSerializable(ARG_OBJECT_LIST);
                    break;
                case Constants.DELIVERY_DISTANCE:
                    mDeliveryDistance = (ArrayList<String>) getArguments().getSerializable(ARG_OBJECT_LIST);
                    break;
                case Constants.STATES:
                    mStateCityModel = (ArrayList<StateCityModel>) getArguments().getSerializable(ARG_OBJECT_LIST);
                    break;
                case Constants.CITIES:
                    mCityList = (ArrayList<StateCityModel.CityModel>) getArguments().getSerializable(ARG_OBJECT_LIST);
                    break;
                case Constants.LOCALITY:
                    mLocalityList = (ArrayList<StateCityModel.LocalityModel>) getArguments().getSerializable(ARG_OBJECT_LIST);
                    break;
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_option_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.cancel);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener != null)
                    mListener.onCancel();
            }
        });

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        switch (mIdentifier) {
            case Constants.MAIN_CATEGORY: {

                searchView.setVisibility(View.GONE);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                MainCategoryAdapter mAdapter = new MainCategoryAdapter(mDisplayList, new ItemSelectedInterface() {
                    @Override
                    public void onItemSelected(Object object) {
                        if (mListener != null) {
                            mListener.onListItemSelected(object, mIdentifier);
                        }
                    }
                });

                recyclerView.setAdapter(mAdapter);
            }

            break;

            case Constants.DELIVERY_DISTANCE: {

                searchView.setVisibility(View.GONE);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                DeliveryDistanceAdapter mAdapter = new DeliveryDistanceAdapter(mDeliveryDistance, new ItemSelectedInterface() {
                    @Override
                    public void onItemSelected(Object object) {
                        if (mListener != null) {
                            mListener.onListItemSelected(object, mIdentifier);
                        }
                    }
                });

                recyclerView.setAdapter(mAdapter);
            }
            break;

            case Constants.STATES: {

                searchView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
               mStateAdapter = new StateAdapter(mStateCityModel, new ItemSelectedInterface() {
                    @Override
                    public void onItemSelected(Object object) {
                        if (mListener != null) {
                            mListener.onListItemSelected(object, mIdentifier);
                        }
                    }
                });

                recyclerView.setAdapter(mStateAdapter);
            }
            break;
            case Constants.CITIES: {

                searchView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                mCityAdapter = new CityAdapter(mCityList, new ItemSelectedInterface() {
                    @Override
                    public void onItemSelected(Object object) {
                        if (mListener != null) {
                            mListener.onListItemSelected(object, mIdentifier);
                        }
                    }
                });

                recyclerView.setAdapter(mCityAdapter);
            }
            break;
            case Constants.LOCALITY: {

                searchView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                mLocalityAdapter = new LocalityAdapter(mLocalityList, new ItemSelectedInterface() {
                    @Override
                    public void onItemSelected(Object object) {
                        if (mListener != null) {
                            mListener.onListItemSelected(object, mIdentifier);
                        }
                    }
                });

                recyclerView.setAdapter(mLocalityAdapter);
            }
            break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOptionListInteractionListener) {
            mListener = (OnOptionListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOptionListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        switch (mIdentifier){
            case Constants.STATES:
                mStateAdapter.getFilter().filter(newText);
                break;
            case Constants.CITIES:
                mCityAdapter.getFilter().filter(newText);
                break;
            case Constants.LOCALITY:
                mLocalityAdapter.getFilter().filter(newText);
                break;
        }
        return true;
    }

    public interface OnOptionListInteractionListener {
        void onListItemSelected(Object item, int identifier);

        void onCancel();
    }
}
