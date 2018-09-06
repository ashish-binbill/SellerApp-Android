package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/22/18.
 */

public class StateCityModel implements Serializable {

    @SerializedName("id")
    String stateId;

    @SerializedName("state_name")
    String stateName;

    public String getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public class CityModel implements Serializable{

        @SerializedName("id")
        String cityId;

        @SerializedName("name")
        String cityName;

        @SerializedName("state_id")
        String stateId;

        public String getStateId() {
            return stateId;
        }

        public String getCityId() {
            return cityId;
        }

        public String getCityName() {
            return cityName;
        }
    }

    public class LocalityModel implements Serializable{

        @SerializedName("id")
        String localityId;

        @SerializedName("name")
        String localityName;

        @SerializedName("state_id")
        String stateId;

        @SerializedName("city_id")
        String cityId;

        @SerializedName("pin_code")
        String pinCode;

        public String getLocalityId() {
            return localityId;
        }

        public String getLocalityName() {
            return localityName;
        }

        public String getCityId() {
            return cityId;
        }

        public String getStateId() {
            return stateId;
        }

        public String getPinCode() {
            return pinCode;
        }

    }
}
