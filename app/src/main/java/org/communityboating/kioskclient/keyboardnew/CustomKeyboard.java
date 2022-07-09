package org.communityboating.kioskclient.keyboardnew;

import android.graphics.drawable.Drawable;

public class CustomKeyboard {

    Row[] rows;
    long keyboardID;
    float totalWidth;
    float totalHeight;

    public void calculatePositions(){
        totalWidth = 0;
        totalHeight = 0;
        float rowXPos = 0;
        float rowYPos = 0;
        float curMaxWidth=0;
        for(Row row : rows){
            row.xPos = rowXPos;
            row.yPos = rowYPos;
            row.calculatePositions();
            if(row.positionType == POSITION_TYPE_NEW_ROW){
                rowXPos=0;
                rowYPos+=row.totalHeight;
                curMaxWidth+=row.totalWidth;
                if(curMaxWidth>totalWidth)
                    totalWidth=curMaxWidth;
                curMaxWidth=0;
            }else if(row.positionType == POSITION_TYPE_ROW_LEFT){
                rowXPos+=row.totalWidth;
            }
        }
    }

    public static class PopupKey {
        String label;
        int keyCode;
    }

    public static final int POSITION_TYPE_NEW_ROW = 0;
    public static final int POSITION_TYPE_ROW_LEFT=1;
    public static final int POSITION_TYPE_ROW_TOP=2;

    public static final int PARENT_DEF=-1;

    public static class Row {
        Key[] keys;

        float xPos, yPos;

        float dKeyWidth;
        float dKeyHeight;
        float dKeyPadding;

        float totalHeight;
        float totalWidth;

        float topPadding;
        float bottomPadding;
        float leftPadding;
        float rightPadding;

        int positionType;

        private void calculatePositions(){
            totalWidth = leftPadding;
            totalHeight = topPadding;
            float maxKeyHeight = 0;
            for(Key key : keys){
                key.xPos = xPos + totalWidth;
                key.yPos = yPos + totalHeight;
                key.calculateValues(dKeyWidth, dKeyHeight, dKeyPadding);
                if(key.height > maxKeyHeight)
                    maxKeyHeight=key.height;
                totalWidth+=(key.padding+key.width);
            }
            totalWidth += rightPadding;
            totalHeight += (maxKeyHeight + bottomPadding);
        }

    }

    public static class Key {

        float xPos, yPos;

        float width=PARENT_DEF, height=PARENT_DEF;
        float padding=PARENT_DEF;
        String subLabel;
        String keyLabel;
        int keyCode;
        Drawable icon;
        PopupKey[] popupKeys;
        boolean isModifier;
        boolean isRepeatable;

        private void calculateValues(float dWidth, float dHeight, float dPadding){
            if(width==PARENT_DEF)
                width=dWidth;
            if(height==PARENT_DEF)
                height=dHeight;
            if(padding==PARENT_DEF)
                padding=dPadding;
        }

    }

}