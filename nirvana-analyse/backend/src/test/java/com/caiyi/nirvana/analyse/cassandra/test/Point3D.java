package com.caiyi.nirvana.analyse.cassandra.test;

import com.datastax.driver.mapping.annotations.UDT;

/**
 * Created by been on 2016/12/27.
 */
@UDT(name = "point3d")
public class Point3D extends Point2D {
    private int z;

    public Point3D(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
