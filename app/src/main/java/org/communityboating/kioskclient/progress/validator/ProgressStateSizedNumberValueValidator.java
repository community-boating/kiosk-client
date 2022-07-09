package org.communityboating.kioskclient.progress.validator;

import java.util.regex.Pattern;

public class ProgressStateSizedNumberValueValidator implements ProgressStateValueValidator{

    public static final String VALID_NUMBER_REGEX="[0-9]+";

    private int numberSize;
    private String invalidEmptyText;
    private String invalidLengthText;
    private String invalidFormatText;

    public ProgressStateSizedNumberValueValidator(int numberSize, String invalidEmptyText, String invalidLengthText, String invalidFormatText){
        this.numberSize=numberSize;
        this.invalidEmptyText=invalidEmptyText;
        this.invalidLengthText=invalidLengthText;
        this.invalidFormatText=invalidFormatText;
    }

    @Override
    public String isValueValid(String value) {
        if(value == null || value.isEmpty())
            return invalidEmptyText;
        if(value.length() < numberSize)
            return invalidLengthText;
        if(!Pattern.matches(VALID_NUMBER_REGEX, value))
            return invalidFormatText;
        return null;
    }
}
