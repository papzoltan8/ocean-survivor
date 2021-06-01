package com.zoli.survivor.internal;

import com.zoli.survivor.game.AI;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ShortestPathTest {

    @Test
    public void testGetDistance() {
        assertEquals(ShortestPath.getDistance(0, 0, 3, 2), 5);
    }

}