package org.communityboating.kioskclient.progress.validator;

import org.communityboating.kioskclient.progress.ProgressState;

public abstract class ProgressStateValueValidatorProgressStateContext implements ProgressStateValueValidatorBase {
    abstract String isValueValid(String value, ProgressState context);
}
