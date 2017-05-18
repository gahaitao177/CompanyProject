package com.caiyi.nirvana.analyse.cassandra.test;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

/**
 * Created by been on 2016/12/27.
 */

@Table(keyspace = "demo", name = "rectangle")
public class Rectangle extends Shape implements Shape2D {
    private Point2D bottomLeft;
    private Point2D topRight;

    public Rectangle() {
    }

    public Rectangle(Point2D bottomLeft, Point2D topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }

    @Column(name = "bottom_left")
    @Frozen
    public Point2D getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(Point2D bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    @Column(name = "top_right")
    @Frozen
    public Point2D getTopRight() {
        return topRight;
    }

    public void setTopRight(Point2D topRight) {
        this.topRight = topRight;
    }

    @Transient
    public double getWidth() {
        return Math.abs(topRight.getX() - bottomLeft.getX());
    }

    @Transient
    public double getHeight() {
        return Math.abs(topRight.getY() - bottomLeft.getY());
    }

    @Override
    public double getArea() {
        return getWidth() * getHeight();
    }
}
