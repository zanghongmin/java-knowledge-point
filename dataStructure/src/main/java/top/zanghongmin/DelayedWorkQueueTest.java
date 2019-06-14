package top.zanghongmin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DelayedWorkQueueTest {

    public static void main(String[] args) {
//        List<String> list = new ArrayList<>();
//        List<String> synList = Collections.synchronizedList(list);



        Map<String, Integer> items = new HashMap<>();
        items.put("A", 10);
        items.put("B", 20);


        System.out.println(items.put("C", 30));

        items = new ConcurrentHashMap<>();

        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        //final CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        for (int i = 0 ; i < 10 ; i++ ) {
            list.add(i + "");
        }

//        for (int i = 0 ; i < list.size() ; i++ ) {
//
//            if (i == 3) {
//                list.remove(3);
//            }
//            System.out.println(list.get(i));
//        }


        Iterator<String> iterator = list.iterator();
        int i = 0 ;
        while(iterator.hasNext()) {
            if (i == 3) {
                list.remove(3);
            }
            System.out.println(iterator.next());
            i ++;
        }


    }

    public static <T>  int request2Bean(Class<T> clazz){
        clazz.getClass();

        return 1;
    }


}
