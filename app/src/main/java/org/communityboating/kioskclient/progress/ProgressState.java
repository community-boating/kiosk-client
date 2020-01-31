package org.communityboating.kioskclient.progress;

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
            Map<String, String> map = new TreeMap<>();
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

    public Character getCharacter(String key){
        if(contains(key))
            return get(key).charAt(0);
        else
            return null;
    }

    public Float getFloat(String key){
        if(contains(key))
            return Float.parseFloat(get(key));
        else
            return null;
    }

    public Double getDouble(String key){
        if(contains(key))
            return Double.parseDouble(get(key));
        else
            return null;
    }

    public Integer getInteger(String key){
        if(contains(key))
            return Integer.parseInt(get(key));
        else
            return null;
    }

    public Boolean getBoolean(String key){
        if(contains(key))
            return get(key).equals("1");
        return null;
    }

    public String get(String key){
        return this.valueMap.get(key);
    }

    public String put(String key, String value){
        return this.valueMap.put(key, value);
    }

    public String putCharacter(String key, Character value){
        if(value == null) {
            if (contains(key)) remove(key);
        }else {
            return put(key, value.toString());
        }
        return null;
    }

    public String putBoolean(String key, Boolean value){
        if(value == null) {
            if (contains(key)) remove(key);
        }else {
            return put(key, value?"1":"0");
        }
        return null;
    }

    public String putFloat(String key, Float value){
        if(value == null) {
            if (contains(key)) remove(key);
        }else {
            return put(key, value.toString());
        }
        return null;
    }

    public String putDouble(String key, Double value){
        if(value == null) {
            if (contains(key)) remove(key);
        }else {
            return put(key, value.toString());
        }
        return null;
    }

    public String putInteger(String key, Integer value){
        if(value == null) {
            if (contains(key)) remove(key);
        }else {
            return put(key, value.toString());
        }
        return null;
    }

    public String remove(String key){
        return this.valueMap.remove(key);
    }

    public boolean contains(String key){
        return this.valueMap.containsKey(key);
    }

    //Should be overwritten by child class
    public Class<? extends Activity> getActivityClass(){
        return null;
    }

    public int getCompletionCount(){
        return 1;
    }

    public ProgressState createNextProgressState(Progress progress){
        return null;
    }

    //public boolean isProgressStateComplete() {return false;}

    public Map<String, String> getValueMap(){
        return valueMap;
    }

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