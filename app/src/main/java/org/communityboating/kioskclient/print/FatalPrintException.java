package org.communityboating.kioskclient.print;

import java.text.ParseException;

public class FatalPrintException extends Exception {
    public FatalPrintException(String message){
        super(message);
    }
}
