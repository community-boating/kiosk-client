package org.communityboating.kioskclient.payment;

import org.communityboating.kioskclient.R;

import java.util.ArrayList;
import java.util.List;

public enum RentalBoatSpecificOptions {
    KAYAK(RentalBoatTypeOptions.PADDLE, R.string.rental_boat_specific_option_kayak_title, R.string.rental_boat_specific_option_kayak_desc, R.string.rental_boat_specific_option_kayak_desc_secondary, 1, 2, SunsetRestriction.RESTRICTION_30MIN_BEFORE_SUNSET, 45),
    STAND_UP_PADDLEBOARD(RentalBoatTypeOptions.PADDLE, R.string.rental_boat_specific_option_standuppaddleboard_title, R.string.rental_boat_specific_option_standuppaddleboard_desc, R.string.rental_boat_specific_option_standuppaddleboard_desc_secondary, 1, 1, SunsetRestriction.RESTRICTION_30MIN_BEFORE_SUNSET, 45),
    MERCYURY_16(RentalBoatTypeOptions.SAIL, R.string.rental_boat_specific_option_mercury16_title, R.string.rental_boat_specific_option_mercury16_desc, R.string.rental_boat_specific_option_mercury16_desc_secondary, 1, 4, SunsetRestriction.RESTRICTION_UNTIL_SUNSET, 89),
    RHODES_19(RentalBoatTypeOptions.SAIL, R.string.rental_boat_specific_option_rhodes19_title, R.string.rental_boat_specific_option_rhodes19_desc, R.string.rental_boat_specific_option_rhodes19_desc_secondary, 2, 5, SunsetRestriction.RESTRICTION_30MIN_BEFORE_SUNSET, 119);
    RentalBoatTypeOptions type;
    int title_str_res_id;
    int desc_str_res_id;
    int desc_secondary_str_res_id;
    int minNumberOfGuests;
    int maxNumberOfGuests;
    SunsetRestriction restriction;
    int priceInDollars;

    RentalBoatSpecificOptions(RentalBoatTypeOptions type, int title_str_res_id, int desc_str_res_id, int desc_secondary_str_res_id,
    int minNumberOfGuests, int maxNumberOfGuests, SunsetRestriction restriction, int priceInDollars){
        this.type = type;
        this.title_str_res_id = title_str_res_id;
        this.desc_str_res_id = desc_str_res_id;
        this.desc_secondary_str_res_id = desc_secondary_str_res_id;
        this.minNumberOfGuests = minNumberOfGuests;
        this.maxNumberOfGuests = maxNumberOfGuests;
        this.restriction = restriction;
        this.priceInDollars = priceInDollars;
    }

    public RentalBoatTypeOptions getType() {
        return type;
    }

    public int getTitle_str_res_id() {
        return title_str_res_id;
    }

    public int getDesc_str_res_id() {
        return desc_str_res_id;
    }

    public int getDesc_secondary_str_res_id() {
        return desc_secondary_str_res_id;
    }

    public int getMinNumberOfGuests() {
        return minNumberOfGuests;
    }

    public int getMaxNumberOfGuests() {
        return maxNumberOfGuests;
    }

    public SunsetRestriction getRestriction() {
        return restriction;
    }

    public int getPriceInDollars() {
        return priceInDollars;
    }

    public static List<RentalBoatSpecificOptions> ofType(RentalBoatTypeOptions type){
        List<RentalBoatSpecificOptions> options = new ArrayList<>();
        for(RentalBoatSpecificOptions option : values()){
            if(option.getType().equals(type))
                options.add(option);
        }
        return options;
    }

}
