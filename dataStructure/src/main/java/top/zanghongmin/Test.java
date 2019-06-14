package top.zanghongmin;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Random;

public class Test implements test11 {

    public static void main(String[] args) throws IllegalAccessException {

//        System.out.println(test1());
//        System.out.println(test3());
        System.out.println(test5());

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);


        String s = "Hello World";
        System.out.println("s = " + s); //Hello World

        //获取String类中的value字段
        Field valueFieldOfString = null;
        try {
            valueFieldOfString = String.class.getDeclaredField("value");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        //改变value属性的访问权限
        valueFieldOfString.setAccessible(true);

        //获取s对象上的value属性的值
        char[] value = (char[]) valueFieldOfString.get(s);
        //改变value所引用的数组中的第5个字符
        value[5] = '_';
        System.out.println("s = " + s);  //Hello_World



    }

    public static Cat test5(){
        Cat cat = new Cat("tom",12);
        try {
            System.out.println("try");
            return cat;
        } catch (Exception e) {

        } finally {
            cat.setName("mouse");
            cat.setAge(13);
            System.out.println("finally");

        }
        System.out.println("finally11");
        return cat;
    }

    public static int test1(){
        int a =20;
        try {
            System.out.println("test1 try block");
            return a+25;
        } catch (Exception e) {
            System.out.println("test1 catch exception");
        } finally {
            a = a+10;
            return a;
        }
        //return a;
    }
    public static Integer test3(){
        Integer a =20;
        try {
            System.out.println("test3 try block");
            return a+25;
        } catch (Exception e) {
            System.out.println("test3 catch exception");
        } finally {
            return a+10;
        }
        //return a;
    }



    public void sss( final Test s) throws Error {
        s.setYy(2);

    }
    int yy=1;

    public int getYy() {
        return yy;
    }

    public void setYy(int yy) {
        this.yy = yy;
    }

    public enum Color {
        RED("red color", 0),
        GREEN("green color", 1),
        BLUE("blue color", 2),
        YELLOW("yellow color", 3);

        Color(String name, int id) {
            _name = name;
            _id = id;
        }

        private String _name;
        private int _id;

        public String getName() {
            return _name;
        }

        public int getId() {
            return _id;
        }

        public static Color getColor(int max) {
            Random random = new Random(System.currentTimeMillis());
            int num = random.nextInt(max);
            switch (num) {
                case 0:
                    return Color.RED;
                case 1:
                    return Color.GREEN;
                case 2:
                    return Color.BLUE;
                case 3:
                    return Color.YELLOW;
                default:
                    return Color.BLUE;
            }
        }
    }

}

class Cat{
    private String name;
    private int age;

    public Cat(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
