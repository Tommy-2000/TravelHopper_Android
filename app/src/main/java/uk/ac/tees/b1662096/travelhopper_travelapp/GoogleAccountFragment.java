package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentGoogleAccountBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth.FirebaseAuthViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoogleAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleAccountFragment extends Fragment {

    private FragmentGoogleAccountBinding fragmentGoogleAccountBinding;

    private View rootFragmentView;

    private FirebaseAuthViewModel firebaseAuthViewModel;

    private MaterialButton signOutButton, removeGoogleAccountButton;

    public GoogleAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment GoogleAccountFragment.
     */
    public static GoogleAccountFragment newInstance() {
        GoogleAccountFragment googleAccountFragment = new GoogleAccountFragment();
        Bundle fragmentBundle = new Bundle();
        googleAccountFragment.setArguments(fragmentBundle);
        return googleAccountFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentGoogleAccountBinding = FragmentGoogleAccountBinding.inflate(fragmentInflater, container, false);
        rootFragmentView = fragmentGoogleAccountBinding.getRoot();
        return rootFragmentView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView googleAccountProfileIcon = fragmentGoogleAccountBinding.googleAccountProfileIcon;
        MaterialTextView googleAccountID = fragmentGoogleAccountBinding.googleAccountID;
        MaterialTextView googleAccountDisplayName = fragmentGoogleAccountBinding.googleAccountDisplayName;
        MaterialTextView googleAccountGivenName = fragmentGoogleAccountBinding.googleAccountGivenName;
        MaterialTextView googleAccountEmail = fragmentGoogleAccountBinding.googleAccountEmail;

        // Get the user's details from their Google account
        googleAccountID.setText(getGoogleAccountProfileInfo().getId());
        googleAccountDisplayName.setText(getGoogleAccountProfileInfo().getDisplayName());
        googleAccountGivenName.setText(getGoogleAccountProfileInfo().getGivenName());
        googleAccountEmail.setText(getGoogleAccountProfileInfo().getEmail());
        googleAccountProfileIcon.setImageDrawable(getGoogleAccountProfileDrawable());

        // Get the buttons from the XML view
        signOutButton = fragmentGoogleAccountBinding.signOutButton;
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutGoogleAccount();
            }
        });
        removeGoogleAccountButton = fragmentGoogleAccountBinding.removeGoogleAccountButton;

    }

    private GoogleSignInAccount getGoogleAccountProfileInfo() {
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(requireActivity().getApplicationContext());
        // Ensure that the Google Account is not null
        if (googleAccount != null) {
            return googleAccount;
        } else {
            Log.e("GOOGLE_ACCOUNT_ERROR", "Unable to retrieve Google Account details");
        }
        return null;
    }


    private Drawable getGoogleAccountProfileDrawable() {
        Uri googleAccountProfileUri = Objects.requireNonNull(getGoogleAccountProfileInfo()).getPhotoUrl();
        Drawable googleAccountProfileDrawable;
        try {
            InputStream uriInputStream = requireActivity().getContentResolver().openInputStream(googleAccountProfileUri);
            googleAccountProfileDrawable = Drawable.createFromStream(uriInputStream, googleAccountProfileUri.toString());
        } catch (FileNotFoundException e) {
            googleAccountProfileDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_account_circle_icon, requireActivity().getTheme());
            e.printStackTrace();
        }
        return googleAccountProfileDrawable;
    }

    private void signOutGoogleAccount() {
        // Sign out the user from the app itself using Firebase Auth
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentGoogleAccountBinding = null;
    }
}