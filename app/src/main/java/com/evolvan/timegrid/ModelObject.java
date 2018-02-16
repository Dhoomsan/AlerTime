package com.evolvan.evo09.timegrid;

public enum ModelObject {

    RED(com.evolvan.evo09.timegrid.R.string.app_name, com.evolvan.evo09.timegrid.R.layout.view_add),
    BLUE(com.evolvan.evo09.timegrid.R.string.app_name, com.evolvan.evo09.timegrid.R.layout.view_update),
    GREEN(com.evolvan.evo09.timegrid.R.string.app_name, com.evolvan.evo09.timegrid.R.layout.view_delete),
    PINK(com.evolvan.evo09.timegrid.R.string.app_name, com.evolvan.evo09.timegrid.R.layout.view_create_grid),
    YELLOW(com.evolvan.evo09.timegrid.R.string.app_name, com.evolvan.evo09.timegrid.R.layout.view_landscape);

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