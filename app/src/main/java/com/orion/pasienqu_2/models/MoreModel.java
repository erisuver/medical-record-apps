package com.orion.pasienqu_2.models;

public class MoreModel {
    private int menuId;
    private String menuName;
    private int icon;

    public MoreModel(int menuId, String menuName, int icon) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.icon = icon;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
