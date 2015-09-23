package com.szn.jukebox.fragments;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import fr.infogene.contacto.BuildConfig;
import fr.infogene.contacto.JukeApplication;
import fr.infogene.contacto.R;
import fr.infogene.contacto.interfaces.LoginListener;
import fr.infogene.contacto.model.Constants;
import fr.infogene.contacto.model.User;
import fr.infogene.contacto.request.GsonRequest;

/**
 * Fragment Affichage Infos Profil
 * Created by Julien Sezn on 13/07/2015.
 *
 */
public class LoginFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = LoginFragment.class.getSimpleName();
    private JukeApplication app;
    private TextView appName;
    private EditText loginUsername, loginUserPass;
    private Button loginBtn;
    private LoginListener listener;



    public static LoginFragment newInstance(Bundle bdl) {
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(bdl);
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = JukeApplication.getInstance();

//        getProfile("C00001", "mkh@infogene.fr");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);


        appName       = (TextView) v.findViewById(R.id.appName);
        loginUsername = (EditText) v.findViewById(R.id.loginUsername);
        loginUserPass = (EditText) v.findViewById(R.id.loginPassword);
        loginBtn      = (Button) v.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        return v;
    }



    private void onLoginSuccess() {

        AnimatorSet animatorSet = new AnimatorSet();

        Animation translateTop    = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_to_top);
        Animation translateLeft   = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_to_left);
        Animation translateRight  = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_to_right);
        Animation translateBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_to_bottom);

        appName.startAnimation(translateTop);
        loginUsername.startAnimation(translateLeft);
        loginUserPass.startAnimation(translateRight);
        loginBtn.startAnimation(translateBottom);


        translateBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onLogged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (LoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    private void getProfile(String storeCode, String email) {

        String url = Constants.PROFILE_URL + storeCode + Constants.GET_BY_EMAIL + email + Constants.JSON_FORMAT;
        Log.w(TAG, "Calling url: " + url);

        GsonRequest<User> userRequest = new GsonRequest<>(url, User.class, null,
                new Response.Listener<User>() {
                    @Override
                    public void onResponse(User response) {
                        onLoginSuccess();
                        app.setUser(response);
//                        listener.onLogged();
                        if(BuildConfig.DEBUG) Log.w(TAG, "got User " + response.getFullName());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(BuildConfig.DEBUG) Log.w(TAG, "Erreur: " + error.getMessage());
                    }
                }
        );

        app.addToRequestQueue(userRequest);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.loginBtn)
            getProfile("C00001", loginUsername.getText().toString());
    }
}
