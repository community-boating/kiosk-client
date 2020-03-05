package org.communityboating.kioskclient.payment;

import org.communityboating.kioskclient.R;

public enum RentalBoatTypeOptions {
    PADDLE(R.string.rental_boat_type_option_paddle_title, R.string.rental_boat_type_option_paddle_desc, R.string.rental_boat_type_option_paddle_specific_page_header, 1, 2, 45, 45),
    SAIL(R.string.rental_boat_type_option_sailboat_title, R.string.rental_boat_type_option_sailboat_desc, R.string.rental_boat_type_option_sailboat_specific_page_header, 1, 5, 89, 119);
    int title_str_res_id;
    int desc_str_res_id;
    int specific_boat_page_header_str_red_id;
    int minPersons;
    int maxPersons;
    int minPrice;
    int maxPrice;

    RentalBoatTypeOptions(int title_str_res_id, int desc_str_res_id, int specific_boat_page_header_str_red_id,
    int minPersons, int maxPersons, int minPrice, int maxPrice){
        this.title_str_res_id = title_str_res_id;
        this.desc_str_res_id = desc_str_res_id;
        this.specific_boat_page_header_str_red_id = specific_boat_page_header_str_red_id;
        this.minPersons = minPersons;
        this.maxPersons = maxPersons;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public int getTitle_str_res_id() {
        return title_str_res_id;
    }

    public int getDesc_str_res_id() {
        return desc_str_res_id;
    }

    public int getSpecific_boat_page_header_str_red_id() {
        return specific_boat_page_header_str_red_id;
    }

    public int getMinPersons() {
        return minPersons;
    }

    public int getMaxPersons() {
        return maxPersons;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }
}
