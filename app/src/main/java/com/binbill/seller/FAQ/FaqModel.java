package com.binbill.seller.FAQ;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class FaqModel implements Serializable{

    @SerializedName("faq")
    ArrayList<FaqListItem> faq;

    public ArrayList<FaqListItem> getFaq() {
        return faq;
    }

    class FaqListItem implements Serializable {
        @SerializedName("id")
        int id;

        @SerializedName("question")
        String question;

        @SerializedName("answer")
        String answer;

        public int getId() {
            return id;
        }

        public String getAnswer() {
            return answer;
        }

        public String getQuestion() {
            return question;
        }
    }
}
