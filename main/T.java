package main;

import lists.OptimisticList;
import lists.FineList;
import lists.CoarseList;
import lists.LazyList;

import interfaces.CommonList;
import data.BD;

import java.util.logging.Logger;
import java.util.Random;
import java.time.Duration;
import java.time.Instant;


public class T{

    int numItensList = 0;
    int numThreads = 0;
    int maxSizeList = 0;

    public static final int tempExecution = 60;

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
        System.out.println("numItensList : " + list.size());
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


    void ExecList(BD bd, CommonList<Integer> list,int numExe) {
        Instant ini = Instant.now();
        String numExec = "NumExec_"+numExe;

        setInitList(list);

        for (int i = 0; i < numThreads; i++) {
            new ExecutarList(list).start();
        }

        System.out.println("Number of active threads : " + (Thread.activeCount()-1) );

        while(true) {
            Instant fim = Instant.now();
            Duration duration = Duration.between(ini, fim);
            bd.setStatistics(list,numItensList,numThreads,duration.getSeconds(),numExec);

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
        CommonList<Integer>[] list = new CommonList[4];

        // list = new CoarseList<Integer>();
        // list = new OptimisticList<Integer>();
        // list = new FineList<Integer>();
        T T1 = new T();
//        list[0] = new FineList<Integer>();
        list[0] = new CoarseList<Integer>();
        list[1] = new FineList<Integer>();
        list[2] = new OptimisticList<Integer>();
        list[3] = new LazyList<Integer>();

        for (int p = 0; p <= 1; p++) { // numero de execuções
            for (int k = 0; k <= 3; k++) { //lista
                switch (k) {
                    case 0:
                        list[0] = new CoarseList<Integer>();
                        break;
                    case 1:
                        list[1] = new FineList<Integer>();
                        break;
                    case 2:
                        list[2] = new OptimisticList<Integer>();
                        break;
                    case 3:
                        list[3] = new LazyList<Integer>();
                        break;
                    }
                for (int j = 2; j <= 5; j++) { // tamanho da lista
                for (int i = 1; i <= 2; i++) { // numero de Threads

                    T1.setParamList(j,i);
                    System.out.println("nome da lista = "+list[k].getListName());
                    T1.ExecList(bd, list[k], p);

                }
            }
        }
        }

        bd.saveStatisticsFile();
        System.out.println("Procedimento finalizado !");
    }
}