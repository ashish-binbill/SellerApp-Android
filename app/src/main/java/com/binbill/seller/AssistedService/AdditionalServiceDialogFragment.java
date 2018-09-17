package com.binbill.seller.AssistedService;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/3/18.
 */

public class AdditionalServiceDialogFragment extends DialogFragment {
    private AdditionalServiceClickInterface listener;

    public static AdditionalServiceDialogFragment newInstance(String assistedServiceId, AssistedUserModel model) {
        AdditionalServiceDialogFragment f = new AdditionalServiceDialogFragment();
        Bundle args = new Bundle();
        args.putString("ASSISTED_SERVICE_ID", assistedServiceId);
        args.putSerializable("ASSISTED_USER", model);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_added_assisted_service, null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        view.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        alertDialog.setContentView(view);

        TextView cancelButton = (TextView) view.findViewById(R.id.header);
        cancelButton.setText(getString(R.string.add_additional_service_header));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final Spinner type = (Spinner) view.findViewById(R.id.et_type_of_service);
        final EditText price = (EditText) view.findViewById(R.id.et_price);
        final EditText priceOver = (EditText) view.findViewById(R.id.et_price_over);

        final ArrayList<AssistedUserModel.ServiceType> mServiceTypes = AppSession.getInstance(getActivity()).getAssistedServiceTypes();

        if (mServiceTypes != null && mServiceTypes.size() > 0) {
            ArrayList<String> serviceList = new ArrayList<>();
            for (AssistedUserModel.ServiceType serviceType : mServiceTypes)
                serviceList.add(serviceType.getName());

            AssistedCustomAdapter adapter = new AssistedCustomAdapter(getActivity(),
                    R.layout.item_text_view, serviceList);
            type.setAdapter(adapter);
        }

        final LinearLayout submitProgress = (LinearLayout) view.findViewById(R.id.btn_submit_progress);
        final String assistedServiceId = getArguments().getString("ASSISTED_SERVICE_ID");
        final AssistedUserModel assistedUserModel = (AssistedUserModel) getArguments().getSerializable("ASSISTED_USER");
        final String linkId;
        final AppButton submit = (AppButton) view.findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.getSelectedItem() != null && !Utility.isEmpty(price.getText().toString())) {

                    submitProgress.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);
                    String typeId = "";
                    ArrayList<AssistedUserModel.ServiceType> mServiceTypes = AppSession.getInstance(getActivity()).getAssistedServiceTypes();
                    if (mServiceTypes != null && mServiceTypes.size() > 0) {
                        for (AssistedUserModel.ServiceType serviceType : mServiceTypes) {
                            if (serviceType.getName().equalsIgnoreCase(String.valueOf(type.getSelectedItem()))) {
                                typeId = serviceType.getId();
                            }
                        }
                    }

                    String linkId = checkIfLinkIdRequired((String) type.getSelectedItem(), assistedUserModel);

                    if (listener != null && !Utility.isEmpty(typeId)) {
                        listener.onAddService(assistedServiceId, linkId, typeId, price.getText().toString(), priceOver.getText().toString());
                        dismiss();
                    } else
                        dismiss();

                    Utility.hideKeyboard(getActivity(), submitProgress);
                }
            }
        });

        return alertDialog;
    }

    private String checkIfLinkIdRequired(String selectedItem, AssistedUserModel model) {

        ArrayList<AssistedUserModel.ServiceType> serviceTypes = model.getServiceTypes();
        if (serviceTypes != null && serviceTypes.size() > 0) {
            for (AssistedUserModel.ServiceType service : serviceTypes) {
                if (service.getServiceType().equalsIgnoreCase(selectedItem))
                    return service.getId();
            }
            return null;
        } else
            return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdditionalServiceClickInterface) {
            listener = (AdditionalServiceClickInterface) context;
        } else {
            throw new IllegalStateException();
        }
    }

    public interface AdditionalServiceClickInterface {
        void onAddService(String assistedServiceId, String linkId, String serviceTypeId, String price, String overTimePrice);
    }
}
