package org.communityboating.kioskclient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.payment.RentalBoatSpecificOptions;
import org.communityboating.kioskclient.payment.RentalBoatTypeOptions;

public class RentalOptionFragment extends BaseActivityFragment {

    RentalBoatTypeOptions rentalTypeOption;

    public static final String BUNDLE_KEY_RENTAL_TYPE_OPTION="rental.type";

    public static RentalOptionFragment createFragment(RentalBoatTypeOptions typeOptions){
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_RENTAL_TYPE_OPTION, typeOptions.name());
        RentalOptionFragment fragment = new RentalOptionFragment();

        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle){
        rentalTypeOption = RentalBoatTypeOptions.valueOf(bundle.getString(BUNDLE_KEY_RENTAL_TYPE_OPTION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View inflated = inflater.inflate(R.layout.rental_option_fragment, container, false);
        readBundle(getArguments());
        return inflated;
    }
}
