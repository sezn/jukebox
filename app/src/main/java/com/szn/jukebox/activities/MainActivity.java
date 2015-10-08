package com.szn.jukebox.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.fitness.data.Subscription;
import com.szn.jukebox.adapters.MenuExpandableAdapter;
import com.szn.jukebox.adapters.SubMenuItem;
import com.szn.jukebox.interfaces.AdapterListener;
import com.szn.jukebox.interfaces.DialogListener;
import com.szn.jukebox.interfaces.FragmentListener;
import com.szn.jukebox.model.MenuI;
import com.szn.jukebox.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FragmentListener, AdapterListener, View.OnClickListener, DialogListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ContactoApplication app;
    // Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private String prevTag = "";
    private FragmentManager fragmentManager;
    private boolean init = true;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ExpandableListView leftDrawerList;
    private MenuExpandableAdapter exAdapter;

    //    private Map<String, List<SubMenuItem>> listSubItems = new HashMap<>();
    private List<SubMenuItem> menuList = new ArrayList<>();

    private ActionBarDrawerToggle drawerToggle;
    private ImageView profilePic;
    private TextView profileUserName;
    private User user;
    // Fragment actually displayed
    Fragment fragment;

    private final int MENU_MENU     = 32;
    private final int NO_ITEM  = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        app = ContactoApplication.getInstance();
        menuList =  MenuI.getMenuItemList(this);
        user = app.getUser();
        fragmentManager = getFragmentManager();
        initView();
        setupToolbar();
        initDrawer();
        showView(MenuI.MENU_ABO, NO_ITEM);
        registerApp();
    }

    private void initView() {

        findViewById(R.id.logout).setOnClickListener(logoutListener);
        findViewById(R.id.menu_legals).setOnClickListener(this);
        profilePic = (ImageView) findViewById(R.id.imageProfile);
        profilePic.setOnClickListener(this);
        profileUserName = (TextView) findViewById(R.id.profileUserName);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        leftDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
//        leftDrawerList.setGroupIndicator(getResources().getDrawable());


        exAdapter = new MenuExpandableAdapter(this, menuList);
//        exAdapter = new MenuExpandableAdapter(this, menuItems, listSubItems);
        leftDrawerList.setAdapter(exAdapter);

        leftDrawerList.setOnGroupClickListener(new DrawerGroupClickListener());
        leftDrawerList.setOnChildClickListener(new DrawerChildClickListener());

        if (user != null)
            setProfile();

        leftDrawerList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousItem  && previousItem > 0) {
                    leftDrawerList.collapseGroup(previousItem);
                    View img = leftDrawerList.getChildAt(previousItem).findViewById(R.id.menu_img);
                    img.setTag(false);
                    img.setBackgroundResource(R.drawable.arrow_right);
                }
                previousItem = groupPosition;
            }
        });
    }


    private void setProfile() {

        if (user.getFullName() != null)
            profileUserName.setText(user.getFullName());

        if (user.getImage() != null) {
            ImageLoader loader = app.getImageLoader();
            loader.get(user.getImage(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null)
                        profilePic.setImageBitmap(Utils.getCroppedBitmap(bitmap));

                    findViewById(R.id.pictureMask).bringToFront();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TAG, "onErrorResponse: " + error.getMessage());
                }
            });
        }
    }


    private class DrawerGroupClickListener implements ExpandableListView.OnGroupClickListener {
        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int position, long l) {

            if(menuList.get(position).getChilds() == null) {
//             if(listSubItems.get(menuItems.get(position)).size() == 0) {
                drawerLayout.closeDrawers();
            }
            else {
                // There is Children
                View img = view.findViewById(R.id.menu_img);

                boolean expanded = false;
                if(img.getTag() != null)
                    expanded = (boolean) img.getTag();

                if(expanded == true){
                    img.setTag(false);
                    img.setBackgroundResource(R.drawable.arrow_right);
                    expandableListView.collapseGroup(position);
                    exAdapter.setUnreadMessages("" + position);
                }else {
                    img.setTag(true);
                    img.setBackgroundResource(R.drawable.arrow_down);
                    expandableListView.expandGroup(position);
                }
                // Return true pour signifier que l'event est Handlé
                return true;
            }

            showView(position, NO_ITEM);
            return false;
        }
    }

    private class DrawerChildClickListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int position, int childPos, long l) {
            drawerLayout.closeDrawers();
            showView(position, childPos);
            return false;
        }
    }

    private void showView(int position, int childPosition, Object... obj) {

        Bundle data = new Bundle();
        int direction = Constants.ANIM_TO_RIGHT;

        reinitToolbar();

        String childTag = null;

        switch (position) {
            case MenuI.MENU_PROFILE:
                fragment = ProfileFragment.newInstance(data);
                break;

            case MenuI.MENU_QUIT:
//                finish();
//                return;
            case MENU_MENU:
                fragment = MenuFragment.newInstance(data);
                break;
            default:
                fragment = ProfileFragment.newInstance(data);
                break;
        }

        Log.w(TAG, "Show View: " + fragment.getClass().getSimpleName() + "  " + childPosition);
        Log.w(TAG, "Prev Tag: " + prevTag);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if( (childTag == null && !prevTag.equals(fragment.getClass().getSimpleName())) || (childTag != null && !prevTag.equals(childTag)) ) {

            if(childTag != null)
                prevTag = childTag;
            else
                prevTag = fragment.getClass().getSimpleName();

            if (!init) {
                if (position == 0 || direction == Constants.ANIM_TO_LEFT) {
                    ft.setCustomAnimations(R.animator.slide_out_left_in, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_in_left_out);
                } else {
                    ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_left_out, R.animator.slide_out_left_in, R.animator.slide_out_left);
                }
            }

            ft.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(prevTag);
            ft.commit();
        }

        init = false;
    }


    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(fragmentManager.getBackStackEntryCount() > 1){
            if(prevTag.equals(StoreFragment.TAG) || prevTag.equals(StoresSearchFragment.TAG))
                reinitToolbar();

            fragmentManager.popBackStackImmediate();
            prevTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            Log.w(TAG, "onBack: " + fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName());
        }else
            super.onBackPressed();
    }

    @Override
    public void onSelected(View v, Subscription subs, int what) {
        Log.w(TAG, "onSelected " + v.getId());

        switch (v.getId()){
            case R.id.popUpMenu:
                if(what == R.id.action_remove)
                    removeSubscription(subs);
                else
                    Log.w(TAG, "onSelected pop up: " + what);
                break;
            default:
                showView(MenuI.DISPLAY_IMG, NO_ITEM, subs.getPrescription().get(0));
                break;
        }
    }

    /**
     * Interface Générique
     * @param v
     * @param obj
     */
    @Override
    public void onSelected(View v, Object... obj) {

        if(obj != null){

            if(obj[0] instanceof Integer){
                Log.w(TAG, "onSelected " + obj[0]);
            }else if(obj[0] instanceof Produit){
                showView(MenuI.FRAGMENT_PRODUCT, NO_ITEM, (Produit) obj[0]);
            }else if(obj[0] instanceof Product){
                showView(MenuI.FRAGMENT_PRODUCT, NO_ITEM, (Product) obj[0]);
            }else if(obj[0] instanceof String){
                showView(MenuI.FRAGMENT_PRODUCT, NO_ITEM, (String) obj[0]);
            }else if(obj[0] instanceof Store){
                showView(MenuI.SHOW_STORE, NO_ITEM, (Store) obj[0]);
            }
        }else
            Log.w(TAG, "onSelected: obj[0] null");
    }

    @Override
    public void onPayBtn(Subscription subscription) {
        showView(MenuI.PAYMENT, NO_ITEM, subscription);
    }

    @Override
    public void onFavoriteSelected(Store store) {

        app.setFavoriteStore(store);
        fragmentManager.popBackStackImmediate();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            prevTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            reinitToolbar();
        }
    }


    @Override
    public void onItemClick(int from, View v) {

        Log.w(TAG, "onItemClick ");
        if(from == Constants.NEWS) {
            showView(MenuI.MENU_NEWS_ITEM, NO_ITEM, (News)v.getTag());
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.imageProfile:
                showView(MenuI.MENU_PROFILE, NO_ITEM);
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_legals:
                showView(MenuI.MENU_LEGALS, NO_ITEM);
                drawerLayout.closeDrawers();
                break;
            case R.id.changeReferentBtn:
            case R.id.popUpMenu:
                showView(MenuI.SEARCH_STORE, NO_ITEM);
                break;
            default:
                Log.w(TAG, "connais pas");
                break;

        }
    }

    @Override
    public void onClickOnView(View v, int state) {

    }

    @Override
    public void onPayed(boolean status, Subscription subscription) {
        showView(MenuI.CONFIRM_PAYMENT, NO_ITEM, subscription);
    }

    @Override
    public void onClickTakePicture(View v) {
        Log.w(TAG, "onClickTakePicture");
        dispatchTakePictureIntent();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String fileStoredPath;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }

            if (photoFile != null) {
                fileStoredPath = photoFile.getAbsolutePath();
                Log.w(TAG, "Image will be save in: " + photoFile.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (fragment instanceof MyOrderFragment) {
//                fragment.onActivityResult(requestCode, resultCode, data);
                ((MyOrderFragment) fragment).onPictureTook(fileStoredPath);
            }
        }
    }


    View.OnClickListener logoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            logout();
        }
    };

    private void logout() {
        profileUserName.setVisibility(View.INVISIBLE);
        profilePic.setImageBitmap(null);
        app.reinitUser();
        goToLoginActivity();
    }

    void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    View.OnClickListener favoriteListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            final Store store = (Store) view.getTag();
            String message;
            if(store.isFavOptician() == false){
                message = getString(R.string.alert_fav_add1) + " " + store.getName() + " "
                        + getString(R.string.alert_fav_add2);
            }else{
                message =  getString(R.string.alert_fav_remove1) + " " + store.getName() + " "
                        + getString(R.string.alert_fav_remove2);
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Light));
            builder.setTitle(getString(R.string.alert_warning));
            builder.setCancelable(true);
            builder.setMessage(message);

            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(store.isFavOptician() == false){
                        view.setBackgroundResource(R.drawable.btn_favorion);
                        store.setIsFavOptician(true);
                        app.setFavoriteStore(store);
                    }else {
                        app.removeFavoriteStore();
                        view.setBackgroundResource(R.drawable.btn_favorioff);
                        store.setIsFavOptician(false);
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.show();
        }
    };

    private void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    void setCustomToolBarForStore(Store store, boolean isFav){

        View storeBar = getLayoutInflater().inflate(R.layout.store_toolbar, toolbar);
        TextView name      = (TextView) storeBar.findViewById(R.id.name);
        ImageView storeFav = (ImageView) storeBar.findViewById(R.id.img);
        name.setText(store.getName());
        storeFav.setTag(store);

        storeFav.setOnClickListener(favoriteListener);

        if(isFav)
            storeFav.setBackgroundResource(R.drawable.btn_favorion);
        else
            storeFav.setBackgroundResource(R.drawable.btn_favorioff);
    }


    private EditText searchEdit;
    public void setCustomToolBarForSearch() {

        View storeBar = getLayoutInflater().inflate(R.layout.search_toolbar, toolbar);
        storeBar.findViewById(R.id.searchImg).setOnClickListener(searcher);
        searchEdit = (EditText) storeBar.findViewById(R.id.etSearchCode);

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO) {

                    String zipcode = v.getEditableText().toString().trim();
                    Utils.hideKeyBoard(MainActivity.this, v);
                    if (zipcode == null || zipcode.equalsIgnoreCase("")) {
                        Log.w(TAG, "Search pas bon");
                    } else {
                        if (fragment instanceof StoresSearchFragment)
                            ((StoresSearchFragment) fragment).filterStores(zipcode);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    View.OnClickListener searcher = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(fragment instanceof StoresSearchFragment)
                ((StoresSearchFragment) fragment).filterStores(searchEdit.getText().toString());
            else
                Log.w(TAG, "Pas Coool in Searcher");
        }
    };

    public void reinitToolbar(){
        toolbar.setTitle(getTitle());
        toolbar.removeView(findViewById(R.id.store_toolbar));
        toolbar.removeView(findViewById(R.id.search_toolbar));
    }

    private void removeSubscription(Subscription subs) {

        // Si Subscription, NextCommand est là :)
        if(fragment.getClass().equals(SubscriptionFragment.class))
            ((SubscriptionFragment)fragment).removeSubscription(subs);
    }


    @Override
    public void onPositive() {
        Log.w(TAG, "onPositive");
        if(fragment.getClass().equals(SubscriptionFragment.class))
            ((SubscriptionFragment)fragment).subscriptionRemoval();

    }

    @Override
    public void onNegative() {
        Log.w(TAG, "onNegative");

    }

    @Override
    public void onDismiss() {
        Log.w(TAG, "onDismiss");

    }



    /***** Nouvelle Implémentation GCM: TESTS ****/
    protected BroadcastReceiver mRegistrationBroadcastReceiver;

    private void registerApp() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.w(TAG, "mRegistrationBroadcastReceiver: " + getString(R.string.gcm_send_message));
                } else {
                    Log.w(TAG, "mRegistrationBroadcastReceiver: " + getString(R.string.token_error_message));
                }
            }
        };

        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }


  /*  void showDialog(String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Light));
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setMessage(message);

        builder.setPositiveButton(getString(R.string.ok), favListener);
        builder.setNegativeButton(getString(R.string.cancel), null);
    }*/



}
