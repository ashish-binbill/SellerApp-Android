package com.binbill.seller.Dashboard;

import com.binbill.seller.Adapter.ManageFruitVegAdapter;
import com.binbill.seller.Model.SkuMeasurement;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateValues {

    HashMap<String, String> mapItem = new HashMap<>();
    HashMap<String, String> mapValue = new HashMap<>();
    ArrayList<SkuMeasurement> details = new ArrayList<>();

    public UpdateValues(HashMap<String, String> itemId, HashMap<String, String> values) {
        this.mapItem = itemId;
        this.mapValue = values;
    }

    public void removeDuplicateElements(){

        // ====================== removing redundancy of ItemsIds ==============================//
        HashMap<String, String> hMap;
        String valueCheck = "";
        String checkKey = "";

        for(int i = 0; i < ManageFruitVegAdapter.itemChange_ids.size(); i ++){
            hMap = ManageFruitVegAdapter.itemChange_ids.get(i);
            for (String key : hMap.keySet()) {
                System.out.println(key);
                checkKey = key;
            }
            if(checkKey.equalsIgnoreCase(valueCheck)){
                ManageFruitVegAdapter.itemChange_ids.remove(i);
            }
            valueCheck = checkKey;
        }

        // ====================== removing redundancy of ValueIds ==============================//

        HashMap<String, String> hMapValue;
        String valueCheckValue = "";
        String checkKeyValue = "";
        for(int i = 0; i < ManageFruitVegAdapter.valueChange_ids.size(); i ++){
            hMapValue = ManageFruitVegAdapter.valueChange_ids.get(i);
            for (String key : hMapValue.keySet()) {
                System.out.println(key);
                checkKeyValue = key;
            }
            if(checkKeyValue.equalsIgnoreCase(valueCheckValue)){
                ManageFruitVegAdapter.valueChange_ids.remove(i);
            }
            valueCheckValue = checkKeyValue;
        }
    }

    public void arrangeCorrectData(HashMap<String, String> itemIdMap,
                                   HashMap<String, String> ValueIdMap){

        // ============================= Set ItemIds =================================//
        HashMap<String, String> hMap;
        String checkKey = "";

        for(int i = 0; i < ManageFruitVegAdapter.itemChange_ids.size(); i ++){
            hMap = ManageFruitVegAdapter.itemChange_ids.get(i);
            for (String key : hMap.keySet()) {
                System.out.println(key);
                checkKey = key;
            }
            if(checkKey.equalsIgnoreCase(itemIdMap.keySet().toString())){
                ManageFruitVegAdapter.itemChange_ids.remove(i);
                ManageFruitVegAdapter.itemChange_ids.add(itemIdMap);
            }
        }

        // ============================= Set ValueIds =================================//

        HashMap<String, String> hMapValue;
        String checkKeyValue = "";
        String KEY = "";

        for (String key : ValueIdMap.keySet()) {
            System.out.println(key);
            KEY = key;
            break;
        }

        for(int i = 0; i < ManageFruitVegAdapter.valueChange_ids.size(); i ++){
            hMapValue = ManageFruitVegAdapter.valueChange_ids.get(i);
            for (String key : hMapValue.keySet()) {
                System.out.println(key);
                checkKeyValue = key;
            }
            if(checkKeyValue.equalsIgnoreCase(KEY)){
                ManageFruitVegAdapter.valueChange_ids.remove(i);
                ManageFruitVegAdapter.valueChange_ids.add(ValueIdMap);
            }
        }
    }
}
