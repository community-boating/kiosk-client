package org.communityboating.kioskclient.progress.validator;

import org.communityboating.kioskclient.progress.ProgressState;

public interface ProgressStateValidator {

    public String isProgressStateValueValid(String key, String value);
    public boolean isProgressStateValid(ProgressState progressState);

}
