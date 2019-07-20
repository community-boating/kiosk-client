package org.communityboating.kioskclient.print.event;

public class PrinterManagerStarIOExtConnectionEvent extends PrinterManagerEvent {

    private int connectionAttempt;
    private int eventResult;

    public static final int EVENT_RESULT_SUCCESSFUL=0;
    public static final int EVENT_RESULT_FAILED=1;
    public static final int EVENT_RESULT_FAILURE_FINAL=2;

    public PrinterManagerStarIOExtConnectionEvent(int connectionAttempt, int eventResult) {
        super("StarIO Extension Connection");
        this.connectionAttempt = connectionAttempt;
        this.eventResult = eventResult;
    }

    @Override
    public String getEventMessage() {
        switch(eventResult){
            case EVENT_RESULT_SUCCESSFUL:
                return "StarIO extension manager connected successfully";
            case EVENT_RESULT_FAILED:
                return "StarIO extension manager failed to connect (" + connectionAttempt + ")";
            case EVENT_RESULT_FAILURE_FINAL:
                return "StarIO extension manager unable to connect to port";
        }
        return null;
    }
}
