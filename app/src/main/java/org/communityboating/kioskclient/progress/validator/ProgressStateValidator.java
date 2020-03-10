package org.communityboating.kioskclient.progress.validator;

import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;

public interface ProgressStateValidator {

    public String isProgressStateValueValid(String key, String value, ProgressState progressState, Progress progress);
    public boolean isProgressStateValid(ProgressState progressState, Progress progress);

}
