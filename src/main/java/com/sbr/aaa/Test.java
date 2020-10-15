package com.sbr.aaa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        list.add(10);
        List<Map> mapList = new ArrayList<>();

        int size = 2;
        if (list.size() > size) {
            for (int i = 0; i < list.size(); i += 2) {
                size++;
                Map map = new HashMap();
                map.put("id", list.get(i));
                mapList.add(map);
            }
        }

        System.out.println(mapList);
    }
}
