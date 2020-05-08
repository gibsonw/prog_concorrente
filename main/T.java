package main;

import java.util.logging.Logger;

import lists.OptimisticList;
import lists.FineList;
import lists.CoarseList;

import interfaces.CommonList;
import data.BD;

import java.util.Random;
import java.time.Duration;
import java.time.Instant;
import java.io.*;

public class T{

    int numItensList,numThreads = 0;
    int maxSizeList = 0;

    public static final int tempExecution = 6;

    public Random rand = new Random();

    public void setListSizeBeg(int param) {

        this.numItensList = (int) Math.pow(10, param);
        this.maxSizeList = (int) (this.numItensList * 2);
    }

    public void setSizeList(CommonList<Integer> list, int numSizeList) {

        int numOperations = numSizeList;
        int numItem = 0;

        while (list.size() != numSizeList){

            numOperations = numSizeList - list.size();
            numItem = rand.nextInt(this.maxSizeList);

            if (numOperations > 0){
                list.add(numItem);
            }else {
                list.remove(numItem);
            }
        }
        list.resetCountes(); // zera contadores de operaçoes da lista
    }

    class ExecutarList extends Thread {

        //private CommonList list;
        private CommonList<Integer> list;

        //Random rand = new Random();
        int numItem, rnd = 0;
        boolean flgAdd,flgRemove,flgContains = false;
    
        Instant first = Instant.now();
        Instant second = Instant.now();
        Duration duration = Duration.between(first, second);

        public ExecutarList(CommonList<Integer> list) {
            this.list = list;
        }

        public void run() {
            //System.out.println("Tamanho inicial da lista antes de iniciar a Thread : "+ this.list.size());
            while (duration.getSeconds() < tempExecution) {
                second = Instant.now();
                duration = Duration.between(first, second);

                rnd = rand.nextInt(10);
                numItem = rand.nextInt(maxSizeList);
               // System.out.println("numItem : " + numItem);
    
                if (rnd < 4) {
                    flgAdd = list.add(numItem);
                } else if (rnd < 8) {
                    flgRemove = list.remove(numItem);
                } else {
                    flgContains = list.contains(numItem);
                }  
            }
        }

    }


    void ExecList(BD bd, CommonList<Integer> list) {
        Instant ini = Instant.now();

        for (int i = 0; i < numThreads; i++) {
            new ExecutarList(list).start();
        }

        System.out.println("Number of active threads : " + (Thread.activeCount()-1) );

        while(true) {
            Instant fim = Instant.now();
            Duration duration = Duration.between(ini, fim);
            bd.setStatistics(list,numItensList,numThreads,duration.getSeconds());

            if(Thread.activeCount() == 1) {
                System.out.println("All produtores e Consumidores have finished !" );
                break;
            }

            try {
                Thread.sleep(2000);                
            } catch (InterruptedException e) {
            }
        }
    }


    public static void main(String[] args) {

        BD bd = new BD();
        CommonList<Integer>  list;
        // list = new CoarseList<Integer>();
        // list = new OptimisticList<Integer>();
        list = new FineList<Integer>();
        String listName = "";

        for (int j = 2; j <= 2; j++) {
            for (int i = 1; i <= 8; i++) {

                T T1 = new T();
                listName = list.getListName();
                T1.setListSizeBeg(j);
                T1.numThreads =  i*2;

                System.out.println("nomeLista : " + listName);
                System.out.println("numItensList : " + T1.numItensList);

                list.add(1);
                System.out.println("size : " + list.size());


                T1.setSizeList(list, T1.numItensList);
                T1.ExecList(bd, list);

            }
        }
        bd.saveStatisticsFile(listName);
    }
}