package main;

import lists.OptimisticList;
import lists.FineList;
import lists.CoarseList;
import lists.LazyList;

import interfaces.CommonList;
import data.BD;

import java.util.logging.Logger;
import java.util.Random;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;


public class T{

    int numItensList = 0;
    int numThreads = 0;
    int maxSizeList = 0;

    public static final int tempExecution = 6;

    public Random rand = new Random();

    public void setParamList(int paramSizeList,int paramThreads) {

        this.numItensList = (int) Math.pow(10, paramSizeList);
        this.maxSizeList  = (int) (this.numItensList * 2); // number max used in random ganerator, double size of initial list keep list balanced if add e removes are equal
        this.numThreads   = (int) (paramThreads * 2);
    }

    public void setInitList(CommonList<Integer> list) {

        int numOperations = this.numItensList;
        int numItem = 0;

        while (list.size() != this.numItensList){

            numOperations = this.numItensList - list.size();
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
        String dtHora = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());

        for (int i = 0; i < numThreads; i++) {
            new ExecutarList(list).start();
        }

        System.out.println("Number of active threads : " + (Thread.activeCount()-1) );

        while(true) {
            Instant fim = Instant.now();
            Duration duration = Duration.between(ini, fim);
            bd.setStatistics(list,numItensList,numThreads,duration.getSeconds(),dtHora);

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
        // list = new FineList<Integer>();
        list = new LazyList<Integer>();
        T T1 = new T();

        for (int k = 1; k <= 4; k++) {
            for (int j = 2; j <= 4; j++) {
                for (int i = 1; i <= 2; i++) {

                    T1.setParamList(j,i);

                    System.out.println("nomeLista : " + list.getListName());
                    System.out.println("numItensList : " + list.size());

                    T1.setInitList(list);
                    T1.ExecList(bd, list);

                }
            }
        }
        bd.saveStatisticsFile(list.getListName());
        System.out.println("Procedimento finalizado !");
    }
}