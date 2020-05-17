package main;

import lists.OptimisticList;
import lists.FineList;
import lists.CoarseList;
import lists.LazyList;
import lists.LockFreeList;

import interfaces.CommonList;
import data.BD;

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

        setInitList(list);
        
        Instant ini = Instant.now();
        String numExec = "NumExec_"+numExe;

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
        
        int numMaxExecs = 3;
        int numListsToTest = 4;  // todas = 3
        int numMaxSizeList = 5;  // 2=100, 3=1000, 4=10000, 5=100000 -> pow(10,numMaxSizeList)
        int numMaxThreads = 10;   // 2=4, 3=6, 4=8 -> numMaxThreads * 2 

        BD bd = new BD();
        CommonList<Integer>[] list = new CommonList[5];

        // list = new CoarseList<Integer>();
        // list = new OptimisticList<Integer>();
        // list = new FineList<Integer>();
        T T1 = new T();
//        list[0] = new FineList<Integer>();
        list[0] = new CoarseList<Integer>();
        list[1] = new FineList<Integer>();
        list[2] = new OptimisticList<Integer>();
        list[3] = new LazyList<Integer>();
        list[4] = new LockFreeList<Integer>();

        for (int p = 0; p <= numMaxExecs; p++) { // numero de execuções
            for (int k = numListsToTest; k <= numListsToTest; k++) { //lista
                switch (k) {
                    case 0:
                        list[k] = new CoarseList<Integer>();
                        break;
                    case 1:
                        list[k] = new FineList<Integer>();
                        break;
                    case 2:
                        list[k] = new OptimisticList<Integer>();
                        break;
                    case 3:
                        list[k] = new LazyList<Integer>();
                        break;
                    case 4:
                        list[k] = new LockFreeList<Integer>();
                        break;
                    }
                for (int j = 2; j <= numMaxSizeList; j++) { // tamanho da lista
                for (int i = 1; i <= numMaxThreads; i++) { // numero de Threads

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