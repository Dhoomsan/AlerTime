package com.example.evo09.timetablemanager;

public enum ModelObject {

    RED(R.string.app_name, R.layout.view_add),
    BLUE(R.string.app_name, R.layout.view_update),
    GREEN(R.string.app_name, R.layout.view_delete),
    PINK(R.string.app_name, R.layout.view_create_grid),
    YELLOW(R.string.app_name, R.layout.view_landscape);

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