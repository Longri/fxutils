package de.longri.gdx_utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringMapTest {

    @Test
    void StringTest() {
        StringMap<String> map = new StringMap<String>();

        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value4");
        map.put("key5", "value5");
        map.put("key6", "value6");

        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
        assertEquals("value3", map.get("key3"));
        assertEquals("value4", map.get("key4"));
        assertEquals("value5", map.get("key5"));
        assertEquals("value6", map.get("key6"));

    }


    @Test
    void intTest() {
        StringMap<Integer> map = new StringMap<Integer>();

        map.put("key1", 10);
        map.put("key2", 20);
        map.put("key3", 30);
        map.put("key4", 40);
        map.put("key5", 50);
        map.put("key6", 60);

        assertEquals(10, map.get("key1"));
        assertEquals(20, map.get("key2"));
        assertEquals(30, map.get("key3"));
        assertEquals(40, map.get("key4"));
        assertEquals(50, map.get("key5"));
        assertEquals(60, map.get("key6"));

    }


}