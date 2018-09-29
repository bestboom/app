package com.app.dlike.models;

import com.app.dlike.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class TransferModel {
    private String from;
    private String to;
    private String amount;
    private String memo;
    private String timestamp;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "TransferModel{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", amount='" + amount + '\'' +
                ", memo='" + memo + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }


    public static ArrayList<TransferModel> sorter(JsonArray jsonArray){
        //Tools.log("TEST", Tools.accountHistory);
        ArrayList<TransferModel> transfers = new ArrayList<>();
        String timeStamp;
        try{
            for(int i = 0; i < jsonArray.size(); i++){
                JsonArray temp = new Gson().fromJson(jsonArray.get(i).toString(), JsonArray.class);
                JsonObject tempOb = (JsonObject) temp.get(1);
                if(tempOb.has("timestamp"))
                {
                    timeStamp = new Gson().fromJson(tempOb.get("timestamp").toString(), String.class);
                    if(tempOb.has("op")){
                        JsonArray ab = (JsonArray)tempOb.get("op");
                        String dd = new Gson().fromJson(ab.get(0).toString(), String.class);
                        if(dd.equals(Tools.TRANSFER)){
                            TransferModel transferModel = new Gson().fromJson(ab.get(1).toString(), TransferModel.class);
                            transferModel.setTimestamp(timeStamp);
                            transfers.add(transferModel);
                           // Tools.log("Final", transferModel.toString());
                        }
                    }
                }

            }

        }catch (Exception ex){
            Tools.log("Transfer-Sorter", ex.toString());
        }

        return transfers;
    }


    public static TransferModel gsonToTransfer(String value){
        try{
            return new Gson().fromJson(value, TransferModel.class);
        }catch (Exception ex){
            Tools.log("ConError", ex.toString());
            return null;
        }
    }
}
