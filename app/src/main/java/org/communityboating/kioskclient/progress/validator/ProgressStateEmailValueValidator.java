package org.communityboating.kioskclient.progress.validator;

import java.util.regex.Pattern;

public class ProgressStateEmailValueValidator implements ProgressStateValueValidator {

    public static final Pattern VALID_EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public String isValueValid(String value) {
        if(value == null || value.isEmpty())
            return "Email should not be empty";
        if(!VALID_EMAIL_REGEX.matcher(value).find())
            return "Email is not valid";
        return null;
    }
}
