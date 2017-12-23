package com.jt28.a6735.jmreceipt;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by a6735 on 2017/7/20.
 */

public class Ini {
    private static final int MAGA = 64;

    private String[][] items;
    private int groupsCount;
    private int itemsSize;

    public Ini() {
        items = new String[MAGA][3] ;
        groupsCount = 0;
        itemsSize = 0;
    }

    public String[] getGroups() {
        String[] ret = new String[groupsCount];
        for (int i = 0, j = 0; i < itemsSize; i++) {
            if (j >= groupsCount) break;
            if (!items[i][0].equals(ret[j])) {
                ret[j] = items[i][0];
                j++;
            }
        }
        return ret;
    }

    public String[] getKeys(String group) {
        int keysCount = 0;
        for (int i = 0; i < itemsSize; i++) {
            if(items[i][0].equals(group)) {
                keysCount++;
            }
        }
        if (keysCount == 0) return new String[]{};
        String[] ret = new String[keysCount];
        for (int i = 0, j = 0; i < itemsSize; i++) {
            if(items[i][0].equals(group)) {
                ret[j] = items[i][1];
                j++;
            }
        }
        return ret;
    }

    public String getValue(String group, String key) {
        for (int i = 0; i < itemsSize; i++) {
            if (items[i][0].equals(group) && items[i][1].equals(key)) {
                return items[i][2];
            }
        }
        return null;
    }

    public void load(InputStream source) throws IOException {
        String currentGroup = "♂";

        String line;
        while ((line = IOUtil.readLine(source)) != null) {
            if (line.trim().startsWith("[")) {
                line = line.trim().substring(1);
                if (line.endsWith("]")) {
                    line = line.substring(0, line.length()-1);
                }
                groupsCount++;
                currentGroup = line;
            } else if (line.trim().startsWith(";")) {
                continue;
            } else {
                int equalIndex = line.indexOf("=");
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String val = line.substring(equalIndex+1);
                    if (currentGroup.equals("♂") && groupsCount == 0) groupsCount++;
                    addItem(currentGroup, key, val);
                }
            }
        }
    }

    private void addItem(String group, String key, String val) {
        if (itemsSize >= items.length)
            resizeItems();
        items[itemsSize][0] = group;
        items[itemsSize][1] = key;
        items[itemsSize][2] = val;
        itemsSize++;
    }

    private void resizeItems() {
        String[][] temp = new String[items.length*2][3];
        for (int i = 0; i < items.length; i++) {
            temp[i] = new String[] {
                    items[i][0],
                    items[i][1],
                    items[i][2]
            };
        }
        items = temp;
    }
}
