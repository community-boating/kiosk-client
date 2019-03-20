package com.example.alexbanks.cbiapp.progress;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;
import java.util.TreeMap;

public class ProgressState implements Parcelable {

    public ProgressState(){
        this.valueMap = new TreeMap<>();
    }

    protected Map<String, String> valueMap;

    public static Creator<ProgressState> CREATOR = new Creator<ProgressState>(){
        @Override
        public ProgressState[] newArray(int size) {
            return new ProgressState[size];
        }

        @Override
        public ProgressState createFromParcel(Parcel source) {
            Class<? extends ProgressState> progressStateClass = (Class<? extends ProgressState>)source.readSerializable();
            int size = source.readInt();
            Map<String, String> map = new TreeMap<String, String>();
            for(int i = 0; i < size; i++){
                String key = source.readString();
                String value = source.readString();
                map.put(key, value);
            }
            ProgressState newState;
            try {
                newState = progressStateClass.newInstance();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
            newState.valueMap = map;
            return progressStateClass.cast(newState);
        }
    };

    public String get(String key){
        return this.valueMap.get(key);
    }

    public String put(String key, String value){
        return this.valueMap.put(key, value);
    }

    public String remove(String key){
        return this.valueMap.remove(key);
    }



    //Should be overwritten by child class
    public Class<? extends Activity> getActivityClass(){
        return null;
    }

    public ProgressState createNextProgressState(){
        return null;
    }

    public boolean isProgressStateComplete() {return false;}



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.getClass());
        dest.writeInt(this.valueMap.size());
        for(Map.Entry<String, String> entry : this.valueMap.entrySet()){
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }
}