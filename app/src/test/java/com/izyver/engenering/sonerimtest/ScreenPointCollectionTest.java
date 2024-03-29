package com.izyver.engenering.sonerimtest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScreenPointCollectionTest {
    private FakeVPoint[] points;

    @Before
    public void before() {
        points = new FakeVPoint[]{
                new FakeVPoint(0, 10, 10),
                new FakeVPoint(0, 15, 10),
                new FakeVPoint(0, 35, 10),
                new FakeVPoint(0, 45, 10),
                new FakeVPoint(0, 20, 25),
                new FakeVPoint(0, 25, 30),
                new FakeVPoint(0, 45, 35),
                new FakeVPoint(0, 35, 45),
                new FakeVPoint(0, 15, 45),
                new FakeVPoint(0, 10, 45),
                new FakeVPoint(0, 10, 50),
                new FakeVPoint(0, 5, 50)
        };

    }

    @Test
    public void hasNextNextTest() {
        VPoint[] points = new VPoint[10];
        for (int i = 0; i < points.length; i++) {
            points[i] = new VPoint(0, i, i);
        }
        ScreenPointCollection collection = new ScreenPointCollection(points);

        int i = 0;
        while (collection.hasNext()) {
            VPoint next = collection.next();
            assertEquals(points[i].x, next.x);
            assertEquals(points[i].y, next.y);
            i++;
        }
        assertEquals(points.length, i);
    }

    @Test
    public void getAroundTest() {
        int distance = 10;

        ScreenPointCollection collection = new ScreenPointCollection(points);
        VPoint[] around = collection.getAround(points[0], distance, false);
        assertEquals(1, around.length);
        assertEquals(points[1], around[0]);

        around = collection.getAround(points[2], distance, false);
        assertEquals(1, around.length);
        assertEquals(points[3], around[0]);

        around = collection.getAround(points[3], distance, false);
        assertEquals(1, around.length);
        assertEquals(points[2], around[0]);

        around = collection.getAround(points[7], distance, false);
        assertEquals(0, around.length);

        around = collection.getAround(points[9], distance, false);
        assertEquals(3, around.length);
        assertTrue(contains(around, points[11]));
        assertTrue(contains(around, points[10]));
        assertTrue(contains(around, points[8]));
    }

    @Test
    public void moveBackTest() {
        FakeVPoint[] points = new FakeVPoint[]{
                new FakeVPoint(1, 5, 5),
                new FakeVPoint(2, 10, 5),
                new FakeVPoint(3, 5, 10),
                new FakeVPoint(4, 10, 10),
                new FakeVPoint(5, 30, 30)
        };

        FakeVPoint[] copy = new FakeVPoint[points.length];
        System.arraycopy(points, 0, copy, 0, points.length);
        ScreenPointCollection collection = new ScreenPointCollection(copy);

        assertTrue(collection.hasNext());
        VPoint next = collection.next();
        assertEquals(points[0], next);
        VPoint[] around = collection.getAround(next, 10, true);
        assertTrue(contains(around, points[1]));
        assertTrue(contains(around, points[2]));
        assertTrue(contains(around, points[3]));
        FakeVPoint splitPoint = splitPoints(next, around);
        collection.setInstead(splitPoint);
        collection.moveBack();
        assertTrue(collection.hasNext());
        next = collection.next();
        assertEquals(splitPoint, next);


    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void nextTest(){
        ScreenPointCollection collection = new ScreenPointCollection(points);

        assertEquals(points[0], collection.next());
        points[1] = points[2] = points[3] = null;
        assertEquals(points[4], collection.next());
        points[5] = points[6] = points[7] = points[8] = points[9] = points[10] = null;
        assertEquals(points[11], collection.next());
        collection.next();//must be index of bound
    }

    @Test
    public void fullTest() {
        ScreenPointCollection collection = new ScreenPointCollection(points);
        while (collection.hasNext()) {
            VPoint next = collection.next();
            VPoint[] around = collection.getAround(next, 10, true);
            if (around.length > 0) {
                FakeVPoint splitPoint = splitPoints(next, around);
                collection.setInstead(splitPoint);
                collection.moveBack();
            }
        }
        VPoint[] vPoints = collection.toArray();
        assertEquals(6, vPoints.length);
        assertTrue(contains(vPoints, points[6]));
        assertTrue(contains(vPoints, points[7]));
        assertTrue(contains(vPoints, new FakeVPoint(0, 12, 10)));
        assertTrue(contains(vPoints, new FakeVPoint(0, 22, 27)));
        assertTrue(contains(vPoints, new FakeVPoint(0, 35, 45)));
        assertTrue(contains(vPoints, new FakeVPoint(0, 45, 35)));
    }

    private FakeVPoint splitPoints(VPoint _point, VPoint[] points) {
        int x = _point.x, y = _point.y, value = _point.value;
        for (VPoint point : points) {
            value += point.value;
            x += point.x;
            y += point.y;
        }
        int itemCount = points.length + 1;
        return new FakeVPoint(value / itemCount, x / itemCount, y / itemCount);
    }

    private boolean contains(VPoint[] arr, VPoint point) {
        for (VPoint vPoint : arr) {
            if (vPoint.x == point.x && vPoint.y == point.y) return true;
        }
        return false;
    }

    class FakeVPoint extends VPoint {
        FakeVPoint(int value, int x, int y) {
            super(value, x, y);
            this.value = value;
            this.x = x;
            this.y = y;
        }
    }
}