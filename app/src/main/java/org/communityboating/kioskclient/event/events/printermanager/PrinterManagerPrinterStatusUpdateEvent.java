package org.communityboating.kioskclient.event.events.printermanager;

public class PrinterManagerPrinterStatusUpdateEvent extends PrinterManagerEvent {

    PrinterStatusUpdateEventType eventStatusType;

    private Object extraData;

    public PrinterManagerPrinterStatusUpdateEvent(PrinterStatusUpdateEventType eventStatusType, Object extraData) {
        super("Printer Status Update");
        this.eventStatusType = eventStatusType;
        this.extraData = extraData;
    }

    @Override
    public String getEventMessage() {
        return "Printer Status Update : " + eventStatusType.eventTypeDescription;
    }

    public void setExtraData(Object extraData){
        this.extraData = extraData;
    }

    public Object getExtraData(){
        return extraData;
    }

    public PrinterStatusUpdateEventType getEventStatusType(){
        return eventStatusType;
    }

    public enum PrinterStatusUpdateEventType {
        ACCESSORY_DISCONNECT("Accessory disconnected"), ACCESSORY_CONNECT_FAILURE("Accessory connection failed"), ACCESSORY_CONNECT_SUCCESSFUL("Accessory connected"),
        BARCODE_READER_RECEIVE_DATA("Barcode reader received data"), BARCODE_READER_DISCONNECT("Barcode reader disconnected"), BARCODE_READER_CONNECT("Barcode reader connected"),
        BARCODE_READER_IMPOSSIBLE("Barcode reader impossible"), CASH_DRAWER_OPEN("Cash drawer opened"), CASH_DRAWER_CLOSED("Cash drawer closed"),
        PRINTER_COVER_OPEN("Printer cover opened"), PRINTER_COVER_CLOSED("Printer cover closed"), PRINTER_PAPER_EMPTY("Printer paper empty"),
        PRINTER_PAPER_NEAR_EMPTY("Printer paper nearly empty"), PRINTER_PAPER_READY("Printer paper ready"), PRINTER_OFFLINE("Printer is offline"),
        PRINTER_ONLINE("Printer online"), PRINTER_IMPOSSIBLE("Printer impossible");
        public String eventTypeDescription;
        PrinterStatusUpdateEventType(String eventTypeDescription){
            this.eventTypeDescription = eventTypeDescription;
        }
    }

}
