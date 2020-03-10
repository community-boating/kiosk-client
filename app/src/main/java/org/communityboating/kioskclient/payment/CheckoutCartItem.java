package org.communityboating.kioskclient.payment;

public class CheckoutCartItem {
    int titleResId;
    int descriptionResId;
    long priceInCents;

    public CheckoutCartItem(int titleResId, int descriptionResId, long priceInCents) {
        this.titleResId = titleResId;
        this.descriptionResId = descriptionResId;
        this.priceInCents = priceInCents;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public long getPriceInCents() {
        return priceInCents;
    }
}
