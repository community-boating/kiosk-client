package org.communityboating.kioskclient.progress;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestBegin;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestFinish;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestRegistrationType;
import org.communityboating.kioskclient.progress.newguest.ProgressStateRentalBoatTypeChoose;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Progress implements Parcelable {
    public List<ProgressState> states;
    int currentState;

    int totalCompletionCount;
    float previousCompletionPercentage;

    public static Creator<Progress> CREATOR = new Creator<Progress>(){
        @Override
        public Progress createFromParcel(Parcel source) {
            int currentState = source.readInt();
            int totalCompletionCount = source.readInt();
            float previousCompletionPercentage = source.readFloat();
            List<ProgressState> progressStates = new LinkedList<>();
            source.readTypedList(progressStates, ProgressState.CREATOR);
            Progress progress = new Progress(progressStates, currentState, totalCompletionCount, previousCompletionPercentage);
            return progress;
        }

        @Override
        public Progress[] newArray(int size) {
            return new Progress[size];
        }

    };

    private Progress(List<ProgressState> states, int currentState, int totalCompletionCount, float previousCompletionPercentage){
        this.states = states;
        this.currentState = currentState;
        this.totalCompletionCount = totalCompletionCount;
        this.previousCompletionPercentage = previousCompletionPercentage;
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

    public void setTotalCompletionCount(int totalCompletionCount){
        this.totalCompletionCount = totalCompletionCount;
    }

    public void nextState(){
        ProgressState currentState = this.getCurrentProgressState();
        if(this.currentState == this.states.size() - 1) {
            ProgressState nextState = currentState.createNextProgressState(this);

            if (nextState != null) {
                this.currentState = states.size();
                this.states.add(nextState);
                Log.d("derpderp", "adding : " +  this.currentState + " : " + this.states.size());
            }
            else{
                Log.d("derpderp", "well isn't this unpleasant");
            }
            //TODO not handled currently, end of progress
        }else{
            this.currentState++;
        }
    }

    public void previousState(){
        if(this.currentState > 0){
            this.currentState--;
        }
    }

    public void clearAfterActiveProgressState(){
        this.states = states.subList(0, currentState + 1);
    }

    public void setCurrentProgressState(int currentState){
        this.currentState = currentState;
    }

    public int checkPreviousProgressStates(){
        for(int i = 0; i <= this.currentState && i < this.states.size(); i++){
            ProgressState progressState = this.states.get(i);
            if(!ProgressStateValidatorManager.isProgressStateValid(progressState)){
                return i;
            }
        }
        return -1;
    }

    public float getPreviousCompletionPercentage(){
        return previousCompletionPercentage;
    }

    public float computeCompletionPercentage(){
        int completionCount = 0;
        for(int i = 0; i < this.states.size() && i < this.currentState; i++){
            ProgressState progressState = this.states.get(i);
            if(ProgressStateValidatorManager.isProgressStateValid(progressState)){
                completionCount += progressState.getCompletionCount();
            }
        }
        float percentage = ((float)completionCount) / ((float)getTotalCompletionCount());
        previousCompletionPercentage = percentage;
        return percentage;
    }

    public int getTotalCompletionCount(){
        return totalCompletionCount;
    }

    public int checkAllProgressStates(){
        for(int i = 0; i < this.states.size(); i++){
            ProgressState progressState = this.states.get(i);
            if(!ProgressStateValidatorManager.isProgressStateValid(progressState)){
                return i;
            }
        }
        return -1;
    }

    public <T extends ProgressState> T findByProgressStateType(Class<T> progressStateType, int offset){
        int count = 0;
        for(int i = 0; i < this.states.size(); i++){
            ProgressState progressState = this.states.get(i);
            if(progressState.getClass().equals(progressStateType))
                if(count >= offset)
                    return (T)progressState;
                else
                    count++;
        }
        return null;
    }

    public <T extends ProgressState> T findByProgressStateType(Class<T> progressStateType){
        return findByProgressStateType(progressStateType, 0);
    }


    public ProgressState getCurrentProgressState(){
        return states.get(currentState);
    }

    public static Progress createNewGuestProgress(){
        return new Progress(new ProgressStateRentalBoatTypeChoose());
        //return new Progress(new ProgressStateNewGuestBegin());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.currentState);
        dest.writeInt(this.totalCompletionCount);
        dest.writeFloat(this.previousCompletionPercentage);
        dest.writeTypedList(this.states);
    }
}