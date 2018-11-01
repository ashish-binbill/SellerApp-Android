package com.binbill.seller.Dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Customer.CreditLoyaltyModel;
import com.binbill.seller.R;
import com.binbill.seller.Registration.ImagePreviewActivity_;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.binbill.seller.Verification.LinkCreditPointsAdapter;
import com.binbill.seller.Verification.RejectReasonAdapter;
import com.binbill.seller.Verification.RejectReasonModel;
import com.binbill.seller.Verification.VerificationAdapter;
import com.binbill.seller.Verification.VerificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class VerificationFragment extends Fragment implements VerificationAdapter.VerificationCardListener {

    private RecyclerView jobListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<VerificationModel> jobList;
    private VerificationAdapter mAdapter;
    private int LINK_CREDIT = 1;
    private int LINK_POINTS = 2;

    public VerificationFragment() {
    }

    public static VerificationFragment newInstance() {
        VerificationFragment fragment = new VerificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApiHelper.fetchJobsForVerification(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        jobListView = (RecyclerView) view.findViewById(R.id.rv_job_list);
        shimmerview = (LinearLayout) view.findViewById(R.id.shimmer_view_container);
        noDataLayout = (LinearLayout) view.findViewById(R.id.no_data_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sl_pull_to_refresh);

        setUpListeners();
        handleResponse();
    }


    private void setUpListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchJobsForVerification();

            }
        });

        AppButton addCustomer = noDataLayout.findViewById(R.id.btn_no_data);
        addCustomer.setVisibility(View.GONE);

        ImageView noDataImage = noDataLayout.findViewById(R.id.iv_no_data_image);
        noDataImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_job_for_verification));
    }

    private void fetchJobsForVerification() {
        jobListView.setVisibility(View.GONE);
        shimmerview.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.GONE);
        ApiHelper.fetchJobsForVerification(getActivity(), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<VerificationModel>>() {
                            }.getType();

                            ArrayList<VerificationModel> verificationList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(getActivity()).setVerificationJobList(verificationList);
                            handleResponse();
                        }

                        if (jsonObject.optJSONArray("reasons") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("reasons");
                            Type classType = new TypeToken<ArrayList<RejectReasonModel>>() {
                            }.getType();

                            ArrayList<RejectReasonModel> rejectReasonList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(getActivity()).setRejectReasonList(rejectReasonList);
                        }
                    } else {
                        jobListView.setVisibility(View.GONE);
                        shimmerview.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);

                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }
                } catch (JSONException e) {
                    jobListView.setVisibility(View.GONE);
                    shimmerview.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);

                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onErrorResponse() {

                jobListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);

                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void invokeRejectReasonDialog(final String userId, final String jobId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_show_credit_points, null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView headerTitle = (TextView) dialogView.findViewById(R.id.header);
        headerTitle.setText(getString(R.string.reason_for_rejection));

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.rv_credit_points);
        TextView noData = (TextView) dialogView.findViewById(R.id.tv_no_data);
        final AppButton linkButton = (AppButton) dialogView.findViewById(R.id.btn_link);
        linkButton.setText(getString(R.string.string_reject));
        final LinearLayout linkButtonProgress = (LinearLayout) dialogView.findViewById(R.id.btn_link_progress);
        RejectReasonAdapter mAdapter = null;

        ArrayList<RejectReasonModel> list = AppSession.getInstance(getActivity()).getRejectReasonList();
        if (list != null && list.size() > 0) {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            mAdapter = new RejectReasonAdapter(list);
            recyclerView.setAdapter(mAdapter);

            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            linkButton.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.VISIBLE);
            noData.setText(getString(R.string.no_data_in_verification_fragment));
            recyclerView.setVisibility(View.GONE);
            linkButton.setVisibility(View.GONE);
        }

        headerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (mAdapter != null) {
            final RejectReasonAdapter finalMAdapter = mAdapter;
            linkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String rejectId = "";
                    ArrayList<RejectReasonModel> latestModel = finalMAdapter.getUpdatedList();
                    for (RejectReasonModel model : latestModel) {
                        if (model.isSelected())
                            rejectId = model.getRejectId();
                    }

                    if (!Utility.isEmpty(rejectId)) {
                        linkButtonProgress.setVisibility(View.VISIBLE);
                        rejectObject(jobId, rejectId);
                        dialog.dismiss();
                    }
                }
            });
        }


        dialog.show();
    }

    private void rejectObject(String jobId, String rejectId) {
        new RetrofitHelper(getActivity()).rejectJobForVerification(jobId, rejectId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {
                        fetchJobsForVerification();
                    } else {
                        mAdapter.notifyDataSetChanged();
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    mAdapter.notifyDataSetChanged();
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                mAdapter.notifyDataSetChanged();
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void invokeFetchCreditPoints(final int type, final String userId, final String jobId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_show_credit_points, null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView headerTitle = (TextView) dialogView.findViewById(R.id.header);
        TextView noData = (TextView) dialogView.findViewById(R.id.tv_no_data);
        if (type == LINK_CREDIT) {
            headerTitle.setText(getString(R.string.link_credits));
            noData.setText(getString(R.string.no_credit));
        } else if (type == LINK_POINTS) {
            headerTitle.setText(getString(R.string.link_points));
            noData.setText(getString(R.string.no_discount));
        }

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.rv_credit_points);
        final AppButton linkButton = (AppButton) dialogView.findViewById(R.id.btn_link);
        final LinearLayout linkButtonProgress = (LinearLayout) dialogView.findViewById(R.id.btn_link_progress);
        LinkCreditPointsAdapter mAdapter = null;

        if (mCreditList != null && mCreditList.size() > 0) {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            mAdapter = new LinkCreditPointsAdapter(type, mCreditList);
            recyclerView.setAdapter(mAdapter);

            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            linkButton.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            linkButton.setVisibility(View.GONE);
        }

        headerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (mAdapter != null) {
            final LinkCreditPointsAdapter finalMAdapter = mAdapter;
            linkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String creditId = "";
                    ArrayList<CreditLoyaltyModel> latestModel = finalMAdapter.getUpdatedList();
                    for (CreditLoyaltyModel model : latestModel) {
                        if (model.isSelected())
                            creditId = model.getCreditId();
                    }

                    Log.d("SHRUTI", "Credit Id " + creditId);

                    if (!Utility.isEmpty(creditId)) {
                        linkButtonProgress.setVisibility(View.VISIBLE);
                        linkButton.setVisibility(View.GONE);
                        if (type == LINK_CREDIT) {
                            makeLinkCreditApiCall(userId, creditId, jobId);
                            if (dialog != null)
                                dialog.dismiss();
                        } else if (type == LINK_POINTS) {

                            if (!Utility.isEmpty(creditId)) {
                                makeLinkPointsApiCall(userId, creditId, jobId);
                                if (dialog != null)
                                    dialog.dismiss();
                            }
                        }
                    }
                }
            });
        }


        dialog.show();
    }

    private void makeLinkPointsApiCall(String userId, String creditId, final String jobId) {

        new RetrofitHelper(getActivity()).linkPointsWithJob(userId, creditId, jobId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {
                        ((BaseActivity) getActivity()).showSnackBar("Linking Success");
                    } else
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                } catch (JSONException e) {
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });

    }

    private void makeLinkCreditApiCall(String userId, String creditId, String jobId) {
        new RetrofitHelper(getActivity()).linkCreditWithJob(userId, creditId, jobId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {
                        ((BaseActivity) getActivity()).showSnackBar("Linking Success");
                    } else
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                } catch (JSONException e) {
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    ArrayList<CreditLoyaltyModel> mCreditList;

    private void makePointsHistoryCall(final String userId, final String jobId) {

        new RetrofitHelper(getActivity()).getUserLoyaltyPoints(userId, jobId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {
                        JSONArray userArray = jsonObject.getJSONArray("result");
                        Type classType = new TypeToken<ArrayList<CreditLoyaltyModel>>() {
                        }.getType();

                        ArrayList<CreditLoyaltyModel> creditList = new Gson().fromJson(userArray.toString(), classType);
                        mCreditList = creditList;

                        invokeFetchCreditPoints(LINK_POINTS, userId, jobId);

                    } else {
                        mCreditList = null;
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    mCreditList = null;
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                mCreditList = null;
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void makeCreditHistoryCall(final String userId, final String jobId) {

        new RetrofitHelper(getActivity()).getUserCredits(userId, jobId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        JSONArray userArray = jsonObject.getJSONArray("result");
                        Type classType = new TypeToken<ArrayList<CreditLoyaltyModel>>() {
                        }.getType();

                        ArrayList<CreditLoyaltyModel> list = new Gson().fromJson(userArray.toString(), classType);
                        mCreditList = list;

                        invokeFetchCreditPoints(LINK_CREDIT, userId, jobId);
                    } else {

                        mCreditList = null;
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }
                } catch (JSONException e) {
                    mCreditList = null;
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                mCreditList = null;
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void handleResponse() {
        ArrayList<VerificationModel> jobList = AppSession.getInstance(getActivity()).getVerificationJobList();
        if (jobList != null && jobList.size() > 0) {
            setUpData(jobList);
        } else {
            jobListView.setVisibility(View.GONE);
            shimmerview.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
            ((TextView) noDataLayout.findViewById(R.id.tv_no_data)).setText(getString(R.string.no_data_in_verification_fragment));
        }
    }

    private void setUpData(ArrayList<VerificationModel> list) {

        this.jobList = list;
        jobListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        jobListView.setLayoutManager(llm);
        mAdapter = new VerificationAdapter(list, this);
        jobListView.setAdapter(mAdapter);

        jobListView.setVisibility(View.VISIBLE);
        shimmerview.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onLinkCredits(int pos) {
        VerificationModel verificationModel = jobList.get(pos);
        makeCreditHistoryCall(verificationModel.getUserId(), verificationModel.getJobId());
    }

    @Override
    public void onLinkPoints(int pos) {
        VerificationModel verificationModel = jobList.get(pos);
        makePointsHistoryCall(verificationModel.getUserId(), verificationModel.getJobId());
    }

    @Override
    public void onViewBill(int pos) {
        VerificationModel verificationModel = jobList.get(pos);

        if (verificationModel.getJobCopyList() != null && verificationModel.getJobCopyList().size() > 0) {
            Intent intent = new Intent(getActivity(), ImagePreviewActivity_.class);
            intent.putExtra(Constants.FILE_URI, verificationModel.getJobCopyList());
            intent.putExtra(Constants.IMAGE_TYPE, Constants.TYPE_URL);
            startActivity(intent);
        } else
            ((BaseActivity) getActivity()).showSnackBar(getString(R.string.no_copies));
    }

    @Override
    public void onAccept(int pos) {
        VerificationModel verificationModel = jobList.get(pos);
        new RetrofitHelper(getActivity()).approveJobForVerification(verificationModel.getCashbackId(), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {
                        fetchJobsForVerification();
                    } else {
                        mAdapter.notifyDataSetChanged();
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    mAdapter.notifyDataSetChanged();
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                mAdapter.notifyDataSetChanged();
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    @Override
    public void onReject(int pos) {
        VerificationModel verificationModel = jobList.get(pos);
        invokeRejectReasonDialog(verificationModel.getUserId(), verificationModel.getCashbackId());
    }
}