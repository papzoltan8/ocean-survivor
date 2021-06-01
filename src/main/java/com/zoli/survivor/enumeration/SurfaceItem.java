package com.zoli.survivor.enumeration;

public enum SurfaceItem {

    PLANK,
    LEAF,
    WASTE,
    BARREL,
    NET,
    RAFT;

    public boolean isResource() {
        return SurfaceItem.PLANK == this || SurfaceItem.LEAF == this || SurfaceItem.WASTE == this || SurfaceItem.BARREL == this;
    }

    public boolean isFixed() {
        return SurfaceItem.RAFT == this || SurfaceItem.NET == this;
    }

}
