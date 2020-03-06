package org.communityboating.kioskclient.payment;

import org.communityboating.kioskclient.R;

public enum SunsetRestriction {
    RESTRICTION_UNTIL_SUNSET(R.string.rental_boat_sunset_restriction_at_sunset), RESTRICTION_30MIN_BEFORE_SUNSET(R.string.rental_boat_sunset_restriction_30_min_before_sunset);

    int sunsetDescResId;
    SunsetRestriction(int sunsetDescResId){
        this.sunsetDescResId = sunsetDescResId;
    }

    public int getSunsetDescResId() {
        return sunsetDescResId;
    }
}
