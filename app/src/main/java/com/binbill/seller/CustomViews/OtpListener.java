package com.binbill.seller.CustomViews;

public interface OtpListener {
    void onOtpEntered(String otp);

    void onOTPIncomplete();
}
