package com.szn.jukebox.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.szn.jukebox.JukeApplication;
import com.szn.jukebox.R;
import com.szn.jukebox.adapters.MenuExpandableAdapter;
import com.szn.jukebox.adapters.SubMenuItem;
import com.szn.jukebox.fragments.LoginFragment;
import com.szn.jukebox.interfaces.AdapterListener;
import com.szn.jukebox.interfaces.FragmentListener;
import com.szn.jukebox.interfaces.LoginListener;
import com.szn.jukebox.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class MainActivity extends AppCompatActivity implements LoginListener,FragmentListener, AdapterListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private JukeApplication app;
    // Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private String prevTag = "";
    private FragmentManager fragmentManager;
    private boolean init = true;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private String[] menuTitles;
    private List<String> menuItems = new ArrayList<>();
    private Map<String, List<SubMenuItem>> listSubItems = new HashMap<>();


    private ExpandableListView leftDrawerList;
    private MenuExpandableAdapter exAdapter;

//    private ListView leftDrawerList;
//    private ArrayAdapter<String> navigationDrawerAdapter;

    private ActionBarDrawerToggle drawerToggle;

    private ImageView profilePic;
    private TextView profileUserName;
    private User user;


    // Fragment actually displayed
    Fragment fragment;

    private final int MENU_PROFILE     = 0;
    private final int MENU_CATALOG     = 1;
    private final int MENU_ABO         = 2;
    private final int MENU_COMMANDS    = 3;
    private final int MENU_ACTU        = 4;
    private final int MENU_QUIT        = 5;
    private final int MENU_DEBUG       = 9;
    private final int FRAGMENT_PRODUCT = 10;
    private final int DISPLAY_IMG      = 11;
    private final int LEGALS           = 12;
    private final int SEARCH_STORE     = 15;
    private final int SHOW_STORE       = 16;
    private final int PAYMENT          = 20;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        app = JukeApplication.getInstance();
        menuTitles = getResources().getStringArray(R.array.menu_items);
        user = app.getUser();


        for(int i = 0; i < menuTitles.length; i++){

            List<SubMenuItem> subList = new ArrayList<>();
            menuItems.add(menuTitles[i]);

            if(i == 3){
                subList.add(new SubMenuItem(0, "Mes Lentilles"));
                subList.add(new SubMenuItem(1, "Produits"));
                Log.w(TAG, "Adding child for " + menuTitles[i] );
            }

            listSubItems.put(menuTitles[i], subList);
        }


        fragmentManager = getFragmentManager();

        initView();

        setupToolbar();

//        if (toolbar != null)
//            toolbar.setTitle(getTitle());

        initDrawer();
        showView(0);

    }

    private void initView() {

        findViewById(R.id.logout).setOnClickListener(logoutListener);
        findViewById(R.id.menu_legals).setOnClickListener(this);
        profilePic = (ImageView) findViewById(R.id.imageProfile);
        profilePic.setOnClickListener(this);
        profileUserName = (TextView) findViewById(R.id.profileUserName);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

//        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
//        navigationDrawerAdapter = new ArrayAdapter<>(this, R.layout.menu_item, menuTitles);
//        leftDrawerList.setAdapter(navigationDrawerAdapter);

        leftDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
//        leftDrawerList.setGroupIndicator(getResources().getDrawable());

        exAdapter = new MenuExpandableAdapter(this, menuItems, listSubItems);
        leftDrawerList.setAdapter(exAdapter);

//        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        leftDrawerList.setOnGroupClickListener(new DrawerGroupClickListener());
        leftDrawerList.setOnChildClickListener(new DrawerChildClickListener());


        if (user != null)
            setProfile();
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
                    Log.w(TAG, "onResponse got bitmap");
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TAG, "onErrorResponse: " + error.getMessage());
                }
            });
        }
    }

    /* The click listener for ListView in the navigation drawer */
/*    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.w(TAG, "DrawerItemClickListener: onItemClick");
            drawerLayout.closeDrawers();
            showView(position);
        }
    }*/


    private class DrawerGroupClickListener implements ExpandableListView.OnGroupClickListener {
         @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int position, long l) {
             Log.w(TAG, "DrawerChildClickListener: onGroupClick " + position);

             if(listSubItems.get(menuItems.get(position)).size() == 0) {
                 drawerLayout.closeDrawers();
             }
             else {
                 // There is Children
                 View img = view.findViewById(R.id.img);
                 boolean collapsed = false;
                 if(img.getTag() != null)
                     collapsed = (boolean) img.getTag();

                 if(collapsed == true){
                     img.setTag(false);
                     img.setBackgroundResource(R.drawable.arrow_right);
                     expandableListView.collapseGroup(position);
                 }else {
                     img.setTag(true);
                     expandableListView.expandGroup(position);
                     img.setBackgroundResource(R.drawable.arrow_down);
                 }
                // Return true pour signifier que l'event est Handlé
                return true;
             }

            showView(position);
            return false;
        }
    }

    private class DrawerChildClickListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int position, int i1, long l) {
            drawerLayout.closeDrawers();
            showView(position);
            return false;
        }
    }

    private void showView(int position, Object... obj) {

        Bundle data = new Bundle();
        int direction = Constants.ANIM_TO_RIGHT;

//        Fragment oldFragment = fragment;
//        Button sharedImg = null;

        // TMP pour pas crasher si no fragment..
        fragment = ProfileFragment.newInstance(data);
        reinitToolbar();

        switch (position) {
            case MENU_PROFILE:
                fragment = ProfileFragment.newInstance(data);
                break;
            case MENU_CATALOG:
                fragment = ProductsListFragment.newInstance(data);
                break;
            case MENU_COMMANDS:
                fragment = CommandsFragment.newInstance(data);
                break;
            case MENU_ACTU:
//                fragment = AboFragment.newInstance(data);
                break;
            case MENU_QUIT:
                finish();
                return;
            case MENU_DEBUG:
                fragment = LoginFragment.newInstance(data);
                break;
            case FRAGMENT_PRODUCT:
                if(obj[0] instanceof Product) {
                    data.putParcelable(Constants.PRODUCT, (Product) obj[0]);
                }
                fragment = ProductFragment.newInstance(data);
                break;
            case MENU_ABO:
                fragment = SubscriptionFragment.newInstance(data);
                break;
            case DISPLAY_IMG:
                if(obj[0] instanceof Prescription)
                    data.putParcelable(Constants.PRESCRIPTION, (Prescription) obj[0]);

                fragment = ContentFragment.newInstance(data);
                break;
            case SEARCH_STORE:
                fragment = StoresSearchFragment.newInstance(data);
//                setCustomToolBarForSearch();

               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // On applique les SharedTransitions au vieux
                    oldFragment.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform));
                    oldFragment.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.explode));

                    // Puis au nouveau
                    fragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform));
                    fragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.explode));

                    sharedImg = (Button) oldFragment.getView().findViewById(R.id.changeReferentBtn);
                }*/
                break;

            case SHOW_STORE:
                if(obj != null && obj[0] != null && obj[0] instanceof Store){
                    Store store = (Store) obj[0];
                    data.putParcelable(Constants.STORE, store);

                    setCustomToolBarForStore(store, store.isFavOptician);
                }


                fragment = StoreFragment.newInstance(data);
                break;
            case LEGALS:
                fragment = LegalsInfoFragment.newInstance(data);
                break;
            case PAYMENT:
                if(obj != null && obj[0] != null && obj[0] instanceof Float){
                    data.putFloat(Constants.PRICE, (Float) obj[0]);
                }

                fragment = PayFragment.newInstance(data);
                break;
            default:
//                fragment = AboFragment.newInstance(data);
//                fragment = HomeFragment.newInstance(data);
                break;
        }

        Log.w(TAG, "Show View: " + fragment.getClass().getSimpleName());
        Log.w(TAG, "Prev Tag: " + prevTag);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if(!prevTag.equals(fragment.getClass().getSimpleName())) {
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

//            if(sharedImg != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                ft.addSharedElement(sharedImg, getString(R.string.searchTransition));
//            }
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
    public void onLogged() {
        Log.w(TAG, "onLogged");
    }

    @Override
    public void onLogError(VolleyError error) {
        Log.w(TAG, "onLogError");
    }

    @Override
    public void onSelected(View v, int pos) {
        Log.w(TAG, "onSelected");

    }

    @Override
    public void onSelected(View v, Product lens) {
        Log.w(TAG, "onSelected " + v.getId());
        showView(FRAGMENT_PRODUCT, lens);
    }

    @Override
    public void onSelected(View v, Subscription subs) {
        Log.w(TAG, "onSelected " + v.getId());
        showView(DISPLAY_IMG, subs.getPrescription().get(0));
    }

    @Override
    public void onSelected(View v, Store store) {
        Log.w(TAG, "onSelected " + v.getId() + store.getName());
        showView(SHOW_STORE, store);
    }

    @Override
    public void onPayBtn(View v, float price) {
        Log.w(TAG, "onPayBtn");
        showView(PAYMENT, price);
    }

    @Override
    public void onFavoriteSelected(Store store) {

        app.setFavoriteStore(store);
        fragmentManager.popBackStackImmediate();

        if(fragmentManager.getBackStackEntryCount() > 0)
            prevTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.imageProfile:
                showView(MENU_PROFILE);
                drawerLayout.closeDrawers();
                break;
            case R.id.menu_legals:
                showView(LEGALS);
                drawerLayout.closeDrawers();
                break;
            case R.id.changeReferentBtn:
                showView(SEARCH_STORE);
                break;
            default:
                Log.w(TAG, "connais pas");
                break;

        }
    }

    @Override
    public void onClickOnView(View v, int state) {

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

    View.OnClickListener favoriteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Store store = (Store) view.getTag();

            if(store.isFavOptician() == false){
                view.setBackgroundResource(R.drawable.btn_favorion);
                store.setIsFavOptician(true);
                app.setFavoriteStore(store);
            }else {
                view.setBackgroundResource(R.drawable.btn_favorioff);
                store.setIsFavOptician(false);
            }
        }
    };

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
                        if(fragment instanceof StoresSearchFragment)
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
        Log.w(TAG, "Reinit Toolbar");
        toolbar.removeView(findViewById(R.id.store_toolbar));
        toolbar.removeView(findViewById(R.id.search_toolbar));
    }


}
