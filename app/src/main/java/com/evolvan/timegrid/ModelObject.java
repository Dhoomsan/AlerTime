package com.evolvan.timegrid;

public enum ModelObject {

    RED(com.evolvan.timegrid.R.string.app_name, com.evolvan.timegrid.R.layout.view_add),
    BLUE(com.evolvan.timegrid.R.string.app_name, com.evolvan.timegrid.R.layout.view_update),
    GREEN(com.evolvan.timegrid.R.string.app_name, com.evolvan.timegrid.R.layout.view_delete),
    PINK(com.evolvan.timegrid.R.string.app_name, com.evolvan.timegrid.R.layout.view_create_grid),
    YELLOW(com.evolvan.timegrid.R.string.app_name, com.evolvan.timegrid.R.layout.view_landscape);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}