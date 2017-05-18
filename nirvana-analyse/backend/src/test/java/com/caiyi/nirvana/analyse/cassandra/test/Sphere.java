package com.caiyi.nirvana.analyse.cassandra.test;

import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.Table;

/**
 * Created by been on 2016/12/27.
 */
@Table(keyspace = "demo", name = "sphere")
public class Sphere extends Shape implements Shape3D {
    private Point3D center;
    private double radius;

    public Sphere() {
    }

    public Sphere(Point3D center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Frozen
    public Point3D getCenter() {
        return center;
    }

    public void setCenter(Point3D center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public double getVolume() {
        return 0;
    }
}
