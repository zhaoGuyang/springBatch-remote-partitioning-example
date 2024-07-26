package com.kailing.bootbatch.partitionjob.Day4main;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @auther 高松
 * @DATE 2019/3/14  23:19
 * partitionjob
 */

public class Day4main {
    public static void main(String[] args) {
        File[] files = new File(".").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return dir.isHidden();
            }
        });
        File[] files1 = new File(".").listFiles(File::isHidden);
        new File(".").listFiles(File::isDirectory);

        ArrayList<String> strings1 = new ArrayList<String>(4) {{
            add("g");
            add("gree");
            add("red");
            add("redyellow");
        }};
        List<String> strings = filterString(strings1, a -> a.startsWith("g"));
        System.out.println(strings);
        List<Integer> integers = filterString(new ArrayList<Integer>(3) {{
            add(1);
            add(2);
            add(3);
        }}, a -> a.intValue() > 2);
        System.out.println(integers);
        strings1.stream().filter(a->a.startsWith("1")).collect(groupingBy(String::length));
        Runnable runnable = new Runnable() {
            public  final  int  a = 1;

            @Override
            public void run() {
                int a =2;
                System.out.println(this.a);
            }
        };
        runnable.run();
        File file = new File("src/main/resources/ss.txt");
        file.mkdir();//父路径必须存在否在报错
        file.mkdirs();//父路径不存在则创建
        file.exists();
        try(BufferedReader bf = new BufferedReader(new FileReader("src/main/resources/data-new.csv"))){
            bf.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        Student gaosong = new Student.Builder("gaosong", "2").age(2).build();
        ArrayList<Student> list =new ArrayList<Student>(3){{
            add(new Student.Builder("gaosong").age(2).build());
            add(new Student.Builder("fg").age(2).build());
            add(new Student.Builder("hh").age(3).build());
            add(new Student.Builder("jj").age(4).build());
        }};
        list.stream().filter(a->a.getAge()<=2).map(Student::getName).distinct().limit(2).collect(toList());

        String[] names= {"wddddsd","1234"};
        List<String> strings2 = Arrays.asList(names);
        Stream<String> stream = Arrays.stream(names);
        strings2.stream().map(a -> a.split("")).map(Arrays::stream).forEach(a-> System.out.println(a.findAny()));
        strings2.stream().map(a -> a.split("")).flatMap(Arrays::stream).forEach(a-> System.out.println(a));
        System.out.println(sum(5));
        try (FileInputStream fileInputStream = new FileInputStream(new File("src/main/resources/data.csv"));
             FileOutputStream fileOutputStream = new FileOutputStream(new File("src/main/resources/data1.csv"))) {
            int len = 0;
            byte[] chars = new byte[1024];
            while ((len = fileInputStream.read(chars)) != -1) {
                System.out.println(new String(chars, 0, len));
                fileOutputStream.write(chars, 0, len);
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
        //带缓冲的字节输入输出流，能提高效率，用的是包装的设计模式，FileOutputStream，第二个参数为true时将会在之前文件中添加而不是覆盖
        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("."));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(".",true))){
            int len=0;
            byte[] chars =new byte[1024];
            while((len=bufferedInputStream.read(chars))!=-1){
                bufferedOutputStream.write(chars,0,len);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //BufferedReader字符输入缓冲，BufferedWriter字符输出缓冲，OutputStreamWriter字符到字节的输出桥梁，InputStreamReader字节到字符的输入桥梁
       try(BufferedReader bufferedReader = new BufferedReader(new FileReader("."));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("."))){
            int len=0;
            char[] chars =new char[1024];
            while((len=bufferedReader.read(chars))!=-1){
              //  bufferedReader.readLine();BufferedReader特有方法，读一行数据
                bufferedWriter.write(chars,0,len);
                bufferedWriter.newLine();//BufferedWriter特有方法换行
            }
        }catch (Exception e){
           e.printStackTrace();
        }

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("."));
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream("."));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/resources/data.csv")));
                BufferedWriter bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/main/resources/data1.csv", true)));

        ) {
            int len = 0;
            while (len != -1) {
                char[] arr = new char[1024];
                if ((len = bufferedReader.read(arr)) != -1) {
                    bufferedWriter1.newLine();
                    bufferedWriter1.write(arr, 0, len);
                    bufferedWriter1.flush();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(()-> System.out.println("ol"));

        executorService.shutdown();

/*        Lock lock = new ReentrantLock();
        lock.lock();
        lock.unlock();
    */

        //不会释放锁所以，只要有一个线程执行完全部
/*       MyRunable myRunable = new MyRunable();
        Thread thread = new Thread(myRunable, "first");
        Thread thread1 = new Thread(myRunable, "two");
        Thread thread2 = new Thread(myRunable, "three");
        thread.start();
        thread1.start();
        thread2.start();*/
/*        MyThread xian1 = new MyThread("xian1");
        MyThread xian2 = new MyThread("xian2");
        MyThread xian3 = new MyThread("xian3");
        xian1.start();
        xian2.start();
        xian3.start();*/
//生产者消费者模式主要涉及是两个线程操作统一对象，并且利用notifyAll();唤醒线程和wait释放线程来让生产者和消费者交叉进行
     /*   Thread thread = new Thread(new productor());
        Thread thread1= new Thread(new consumer());
        thread.start();
        thread1.start();*/
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println(localHost);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //threadLocal将会以当前线程为key进行key-value存储这样解决了不同线程的变量隔绝作用
        ThreadLocal threadLocal = new ThreadLocal();
        threadLocal.set("main");
        String o =(String) threadLocal.get();
        System.out.println(o);
        new Thread(()->{ System.out.println((String) threadLocal.get());}).start();
        Optional<Student> student = Optional.ofNullable(null);
        student.map(Student::getName).ifPresent(a-> System.out.println(a));
        student.map(Student::getName).orElseGet(()->{return "s";});

    }
    static  class  MyThread1 extends  Thread{
        public  String name;
        public MyThread1(String name) {

            super(name);
            this.name=name;
        }

        @Override
        public void run() {
            if("1".equals(name)){
                synchronized ("3"){

                }
                synchronized ("4"){

                }
            }
        }
    }

    static class  MyThread extends Thread{
        private static  volatile  int a= 100;
        public  static Lock lock = new ReentrantLock();
        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true){
                lock.lock();
                if(a>0){
                    System.out.println(Thread.currentThread().getName()+"真在贩卖第"+a+"张票");
                    a--;
                }else {
                    System.out.println("已售完！");
                    lock.unlock();
                    break;
                }
                lock.unlock();//在这里释放锁将会让当前线程等待10秒后才会去获得cpu执行权，也就是可以让线程交叉进行cpu执行权
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //  lock.unlock();放在这里释放锁将会让同一线程执行多次，因为释放后他立刻有机会获得cpu执行权
            }

        }
    }
    static class  MyRunable implements  Runnable{
        private static  volatile  int a= 100;

        @Override
        public void run() {
            synchronized ("ok"){
                while (true){
                    if(a>0){
                        System.out.println(Thread.currentThread().getName()+"真在贩卖第"+a+"张票");
                        a--;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {


                        }
                    }else {
                        System.out.println("已售完！");
                        break;
                    }

                }
            }
        }


    }
    public static int sum(int i){
        if(i==1) return i;
        return i+sum(i-1);
    }
    public  static <T>List<T> filterString(List<T> list, Predicate<T> p){
        ArrayList<T> aList = new ArrayList<>(1);
        list.stream().forEach(a->{if(p.test(a)){
            aList.add(a);
        }});
        return aList;
    }


}
class  Student {
    private String name;
    private String sex;
    private int age;
    public Boolean flag=false;
    public static volatile int s = 100;
    public static volatile boolean endFlag = false;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public  Student(Builder builder){
        this.name=builder.name;
        this.sex=builder.sex;
        this.age=builder.age;
    }

    public static class Builder{
        private String name;
        private String sex;
        private int age;

        public Builder(String name, String sex) {
            this.name = name;
            this.sex = sex;
        }
        public Builder(String name) {
            this.name = name;
        }
        public Builder age(int age){
            this.age=age;
            return this;
        }
        public Student build(){
            return new Student(this);
        }
    }
    public static Student test = new Student.Builder("gaosong", "2").age(2).build();
    public static Student getStudent(){
        return test;
    }

}
class productor implements  Runnable{
    Student student =Student.getStudent();
    @Override
    public void run() {
        while(true){
            synchronized (student){
                if(!student.flag){
                    if(  student.s>0){
                        student.s--;
                    }else {
                        System.out.println(Thread.currentThread().getName()+"结束");
                        student.endFlag = true;
                        student.notifyAll();
                        break;

                    }
                    System.out.println(Thread.currentThread().getName()+"生成一个");
                    student.flag=true;
                    student.notifyAll();

                }else{
                    try {
                        student.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
class consumer implements  Runnable{
    Student student =Student.getStudent();
    @Override
    public void run() {
        while(true) {
            synchronized (student) {
                if (student.flag) {
                    System.out.println(Thread.currentThread().getName() + "消费一个");
                    student.flag = false;
                    student.notifyAll();
                } else {
                    try {
                        if (student.endFlag) {
                            System.out.println(Thread.currentThread().getName() + "结束");
                            break;
                        }
                        student.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        }
    }

