package com.caiyi.nirvana.analyse.cassandra.test;

import com.datastax.driver.mapping.annotations.UDT;

/**
 * Created by been on 2016/12/27.
 */
@UDT(keyspace = "demo", name = "point2d")
public class Point2D {
    private int x;
    private int y;

    public Point2D() {
    }

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
