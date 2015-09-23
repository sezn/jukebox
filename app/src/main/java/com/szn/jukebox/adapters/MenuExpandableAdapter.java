package com.szn.jukebox.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.infogene.contacto.R;

/**
 * Adapter pour le DrawerLayout (Menu)
 * <p>Affiche des groupes, expandables / collapsed si Childs</p>
 * Created by Julien Sezn on 22/09/2015.
 *
 */
public class MenuExpandableAdapter extends BaseExpandableListAdapter {

    private static String TAG = MenuExpandableAdapter.class.getSimpleName();
    private Context context;
    private List<String> groups;
    private Map<String, List<SubMenuItem>> children = new HashMap<>();


    public MenuExpandableAdapter(Context con, List<String> groups, Map<String, List<SubMenuItem>> listItems) {
        this.context  = con;
        this.groups   = groups;
        this.children = listItems;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public SubMenuItem getChild(int groupPosition, int childPosition) {
        return children.get(groups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return  children.get(groups.get(groupPosition)).size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView textView = getGenericView();
        textView.setText(getChild(groupPosition, childPosition).getName());

        Drawable leftD = context.getResources().getDrawable(R.drawable.circle_white_sub);
        textView.setCompoundDrawablePadding(Math.round(context.getResources().getDimension(R.dimen.margina)));
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(leftD, null, null, null);
        textView.setPadding(Math.round(context.getResources().getDimension(R.dimen.menuItemHeight)), 0, 0, 0);
        return textView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_item, null);

        ((TextView) convertView.findViewById(R.id.text)).setText(getGroup(groupPosition).toString());

        if(getChildrenCount(groupPosition) > 0)
           convertView.findViewById(R.id.img).setVisibility(View.VISIBLE);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public TextView getGenericView() {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(context.getResources().getDimension(R.dimen.menuItemHeight)));
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(Math.round(context.getResources().getDimension(R.dimen.margina)), 0, 0, 0);
        return textView;
    }
}
