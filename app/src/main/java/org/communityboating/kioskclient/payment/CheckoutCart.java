package org.communityboating.kioskclient.payment;

import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.newguest.ProgressStateRentalBoatSpecificChoose;

import java.util.ArrayList;
import java.util.List;

public class CheckoutCart {
    List<CheckoutCartItem> cartItems;

    public CheckoutCart() {
        this.cartItems = new ArrayList<>();
    }

    public long getTotalPriceInCents(){
        long total = 0;
        for(CheckoutCartItem item : cartItems){
            total += item.priceInCents;
        }
        return total;
    }

    public static CheckoutCart getCheckoutCartFromProgress(Progress progress){
        CheckoutCart cart = new CheckoutCart();
        ProgressStateRentalBoatSpecificChoose progressStateRentalBoatSpecificChoose = progress.findByProgressStateType(ProgressStateRentalBoatSpecificChoose.class);
        RentalBoatSpecificOptions specificOption = progressStateRentalBoatSpecificChoose.getChosenRentalOption();
        CheckoutCartItem cartItem = new CheckoutCartItem(specificOption.getTitle_str_res_id(), specificOption.getDesc_str_res_id(), specificOption.getPriceInDollars() * 100);
        cart.cartItems.add(cartItem);
        return cart;
    }

}
