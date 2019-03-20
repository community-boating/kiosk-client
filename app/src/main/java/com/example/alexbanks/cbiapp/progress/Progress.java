package com.example.alexbanks.cbiapp.progress;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestBegin;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestPhone;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Progress implements Parcelable {
    public List<ProgressState> states;
    int currentState;

    public static Creator<Progress> CREATOR = new Creator<Progress>(){
        @Override
        public Progress createFromParcel(Parcel source) {
            int currentState = source.readInt();
            List<ProgressState> progressStates = new LinkedList<>();
            source.readTypedList(progressStates, ProgressState.CREATOR);
            Progress progress = new Progress(progressStates, currentState);
            return progress;
        }

        @Override
        public Progress[] newArray(int size) {
            return new Progress[size];
        }

    };

    private Progress(List<ProgressState> states, int currentState){
        this.states = states;
        this.currentState = currentState;
    }

    public Progress(){
        this.states = new ArrayList<>();
        this.currentState = -1;
    }

    private Progress(ProgressState firstState) {
        states = new LinkedList<>();
        currentState = 0;
        states.add(firstState);
    }

    public void nextState(){
        ProgressState currentState = this.getCurrentProgressState();
        ProgressState nextState = currentState.createNextProgressState();
        if(nextState != null){
            this.currentState = states.size();
            this.states.add(nextState);
        }
        //TODO not handling this right now, just do nothing
    }

    public void setCurrentProgressState(int currentState){
        this.currentState = currentState;
    }

    public int checkProgressStates(){
        for(int i = 0; i < this.states.size(); i++){
            ProgressState progressState = this.states.get(i);
            if(!progressState.isProgressStateComplete()){
                return i;
            }
        }
        return -1;
    }

    public ProgressState getCurrentProgressState(){
        return states.get(currentState);
    }

    public static Progress createNewGuestProgress(){
        //return new Progress(new ProgressStateNewGuestBegin());
        return new Progress(new ProgressStateNewGuestBegin());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.currentState);
        dest.writeTypedList(this.states);
    }
}