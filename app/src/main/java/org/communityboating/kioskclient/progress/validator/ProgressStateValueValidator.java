package org.communityboating.kioskclient.progress.validator;

import org.communityboating.kioskclient.progress.ProgressState;

public abstract class ProgressStateValueValidator implements ProgressStateValueValidatorBase{

    public abstract String isValueValid(String value);

}
