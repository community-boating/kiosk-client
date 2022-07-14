package org.communityboating.kioskclient.progress.validator;

import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;

public abstract class ProgressStateValueValidatorProgressContext implements ProgressStateValueValidatorBase {
    public abstract String isValueValid(String value, ProgressState contextPS, Progress context);
}
