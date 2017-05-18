package com.caiyi.nirvana.analyse.enums;

/**
 * Created by pc on 2017/3/8.
 */
public enum LevelEnum {
    INFO("普通信息", 1), NOTICE("注意", 2), WARNING("警告", 3), ERROR("错误", 4), FATAL("严重错误", 5);

    private String name;
    private int index;

    private LevelEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }


    public static String getName(int index) {
        for (LevelEnum c : LevelEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
