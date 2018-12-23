package org.tron.common;

import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;

public class Test {

    static class B {
        private String name;
        private Integer index;

        public B(String _name){
            this.name = _name;
        }

        public B(String _name, int _index){
            this.name = _name;
            this.index = _index;
        }

        public B(String _name, Integer _index){
            this.name = _name;
            this.index = _index;
        }

        public String getName(){
            return this.name;
        }

        public Integer getIndex(){
            return this.index;
        }
    }

    public static long timeDiff(long old) {
        return System.nanoTime() - old;
    }

    public static void main(String args[]) throws Exception {

        int numTrials = 10000000;
        long nanos;
        B[] bees = new B[numTrials];
        for (int i = 0; i < numTrials; i++) {
            bees[i] = new B("小明");
        }

        nanos = System.nanoTime();
        for (int i = 0; i < numTrials; i++) {
            bees[i] = new B("小明", i);
        }

        System.out.println("Normal instaniation took: " + TimeUnit.NANOSECONDS.toMillis(timeDiff(nanos)) + "ms");
        System.out.println("Normal: " + bees[0].getName() + ", Index: " + bees[0].getIndex());


        Class<B> c = B.class;
        Constructor<B> ctor = c.getConstructor(String.class, Integer.class);
        for (int i = 0; i < numTrials; i++) {
            bees[i] = ctor.newInstance("小明", i);
        }

        nanos = System.nanoTime();
        for (int i = 0; i < numTrials; i++) {
            bees[i] = ctor.newInstance("小明", i);
        }
        System.out.println("\nReflecting [A] instantiation took:" + TimeUnit.NANOSECONDS.toMillis(timeDiff(nanos)) + "ms");
        System.out.println("Reflecting: " + bees[bees.length - 1].getName() + ", Index: " + bees[bees.length - 1].getIndex());


        //////////////
        ctor = c.getConstructor(String.class, int.class);
        for (int i = 0; i < numTrials; i++) {
            bees[i] = ctor.newInstance("大飞", i);
        }

        nanos = System.nanoTime();
        for (int i = 0; i < numTrials; i++) {
            bees[i] = ctor.newInstance("大飞", i);
        }
        System.out.println("\nReflecting [B] instantiation took:" + TimeUnit.NANOSECONDS.toMillis(timeDiff(nanos)) + "ms");
        System.out.println("Reflecting: " + bees[bees.length - 1].getName() + ", Index: " + bees[bees.length - 1].getIndex());

    }

}