package org.communityboating.kioskclient.payment;

public enum SunsetRestriction {
    RESTRICTION_UNTIL_SUNSET(), RESTRICTION_30MIN_BEFORE_SUNSET;

    int sunsetDescResId;
    SunsetRestriction(int sunsetDescResId){
        this.sunsetDescResId = sunsetDescResId;
    }
}
