package org.communityboating.kioskclient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.payment.RentalBoatSpecificOptions;
import org.communityboating.kioskclient.payment.RentalBoatTypeOptions;

public class RentalOptionFragment extends BaseActivityFragment {

    RentalBoatTypeOptions rentalTypeOption;
    RentalBoatSpecificOptions rentalBoatSpecificOption;
    int rentalType;

    public static final String BUNDLE_KEY_RENTAL_TYPE_OPTION="rental.type";
    public static final String BUNDLE_KEY_RENTAL_SPECIFIC_OPTION="rental.specific";
    public static final String BUNDLE_KEY_FRAGMENT_TYPE="type";

    public static final int RENTAL_TYPE_BOAT_TYPE = 0;
    public static final int RENTAL_TYPE_BOAT_SPECIFIC=1;

    public static RentalOptionFragment createFragment(RentalBoatTypeOptions typeOptions){
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_RENTAL_TYPE_OPTION, typeOptions.name());
        bundle.putInt(BUNDLE_KEY_FRAGMENT_TYPE, RENTAL_TYPE_BOAT_TYPE);
        RentalOptionFragment fragment = new RentalOptionFragment();

        fragment.setArguments(bundle);
        return fragment;
    }

    public static RentalOptionFragment createFragment(RentalBoatSpecificOptions specificOptions){
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_RENTAL_SPECIFIC_OPTION, specificOptions.name());
        bundle.putInt(BUNDLE_KEY_FRAGMENT_TYPE, RENTAL_TYPE_BOAT_SPECIFIC);
        RentalOptionFragment fragment = new RentalOptionFragment();

        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle){
        switch(rentalType = bundle.getInt(BUNDLE_KEY_FRAGMENT_TYPE)){
            case RENTAL_TYPE_BOAT_TYPE:
                rentalTypeOption = RentalBoatTypeOptions.valueOf(bundle.getString(BUNDLE_KEY_RENTAL_TYPE_OPTION));
                break;
            case RENTAL_TYPE_BOAT_SPECIFIC:
                rentalBoatSpecificOption = RentalBoatSpecificOptions.valueOf(bundle.getString(BUNDLE_KEY_RENTAL_SPECIFIC_OPTION));
                break;
        }
    }

    private void populateView(View view, boolean showSunset, boolean showDescriptionSecondary, int titleTextRedId, int descriptionTextResId, int descriptionSecondaryTextResId,
                              int infoPersonCountMin, int infoPersonCountMax, int infoSunsetTextResId, int minPriceDollars, int maxPriceDollars){
        TextView titleText = view.findViewById(R.id.rental_fragment_title_text);
        TextView descriptionText = view.findViewById(R.id.rental_fragment_description_text);
        TextView descriptionSecondaryText = view.findViewById(R.id.rental_fragment_description_text_secondary);
        ImageView clockIconImage = view.findViewById(R.id.rental_fragment_clock_icon_image);
        TextView infoPersonCountText = view.findViewById(R.id.rental_fragment_info_person_count_text);
        TextView infoSunsetText = view.findViewById(R.id.rental_fragment_info_sunset_text);
        TextView infoPriceText = view.findViewById(R.id.rental_fragment_info_price_text);
        titleText.setText(titleTextRedId);
        descriptionText.setText(descriptionTextResId);
        descriptionSecondaryText.setVisibility(showDescriptionSecondary ? View.VISIBLE : View.GONE);
        descriptionSecondaryText.setText(descriptionSecondaryTextResId);
        infoPersonCountText.setText(infoPersonCountMin == infoPersonCountMax ? Integer.toString(infoPersonCountMin) : (infoPersonCountMin + "-" + infoPersonCountMax));
        clockIconImage.setVisibility(showSunset ? View.VISIBLE : View.GONE);
        infoSunsetText.setVisibility(showSunset ? View.VISIBLE : View.GONE);
        infoSunsetText.setText(infoSunsetTextResId);
        infoPriceText.setText(minPriceDollars == maxPriceDollars ? Integer.toString(minPriceDollars) : (minPriceDollars + "-" + maxPriceDollars));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View inflated = inflater.inflate(R.layout.rental_option_fragment, container, false);
        readBundle(getArguments());
        switch (rentalType){
            case RENTAL_TYPE_BOAT_TYPE:
                populateView(inflated, false, false, rentalTypeOption.getTitle_str_res_id(), rentalTypeOption.getTitle_str_res_id(), -1,
                        rentalTypeOption.getMinPersons(), rentalTypeOption.getMaxPersons(), -1, rentalTypeOption.getMinPrice(), rentalTypeOption.getMaxPrice());
                break;
            case RENTAL_TYPE_BOAT_SPECIFIC:
                populateView(inflated, true, true, rentalBoatSpecificOption.getTitle_str_res_id(), rentalBoatSpecificOption.getDesc_str_res_id(), rentalBoatSpecificOption.getDesc_secondary_str_res_id(),
                        rentalBoatSpecificOption.getMinNumberOfGuests(), rentalBoatSpecificOption.getMaxNumberOfGuests(), rentalBoatSpecificOption.getRestriction().getSunsetDescResId(), rentalBoatSpecificOption.getPriceInDollars(), rentalBoatSpecificOption.getPriceInDollars());
        }
        return inflated;
    }
}
