package com.szn.jukebox.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.infogene.contacto.R;
import fr.infogene.contacto.adapters.SubMenuItem;


public class MenuI {

    // Références pour les Fragments à afficher
    public static final int MENU_PROFILE     = 0;
    public static final int MENU_ORDER       = 6;
    public static final int MENU_ABO         = 1;
    public static final int MENU_COMMANDS    = 2;
        // Childs de MENU_COMMANDS
        public static final int MENU_CHILD_LENTILLES = 0;
        public static final int MENU_CHILD_PRODUCTS  = 1;
    public static final int MENU_ACTUS       = 3;
        // Childs de MENU_ACTUS
        public static final int MENU_CHILD_NEWS    = 0;
        public static final int MENU_CHILD_ADVICES = 1;
    public static final int MENU_MSG         = 4;
    public static final int MENU_QUIT        = 5;
    public static final int MENU_DEBUG       = 64;
    public static final int MENU_NEWS_ITEM   = 7;
    public static final int MENU_CATALOG     = 8;
    public static final int FRAGMENT_PRODUCT = 10;
    public static final int DISPLAY_IMG      = 11;
    public static final int MENU_LEGALS      = 12;


    public static List<SubMenuItem> getMenuItemList(Context con){

        List<SubMenuItem> listSubItems = new ArrayList<>();

        String[] menuTitles = con.getResources().getStringArray(R.array.menu_items);
        for(int i = 0; i < menuTitles.length; i++){
            SubMenuItem menuItem = new SubMenuItem(i, menuTitles[i], true, false);

            List<SubMenuItem> subList = new ArrayList<>();
            if(i == MENU_COMMANDS){
                subList.add(new SubMenuItem(i, "Mes", true, true));
                subList.add(new SubMenuItem(i, "Produits", true, true));
            }
            menuItem.setChilds(subList);

            listSubItems.add(menuItem);
        }

        return listSubItems;
    }


}
