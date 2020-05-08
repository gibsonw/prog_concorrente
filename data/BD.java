package data;

import java.util.ArrayList;
import java.util.List;

//import lists.CoarseList;
import interfaces.CommonList;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BD{

    public ArrayList<Integer> arrayInitList = new ArrayList<Integer>();
    public ArrayList<Integer> arrayNumThreads = new ArrayList<Integer>();
    public ArrayList<Integer> arrayAdd = new ArrayList<Integer>();
    public ArrayList<Integer> arrayRemove = new ArrayList<Integer>();
    public ArrayList<Integer> arrayContains = new ArrayList<Integer>();
    public ArrayList<Integer> arrayListSize = new ArrayList<Integer>();
    public ArrayList<String> arraySeconds = new ArrayList<String>();

    public void setStatistics (CommonList<Integer> l,int numItensList,int numThreads,long seconds) {
        this.arrayInitList.add(numItensList);
        this.arrayNumThreads.add(numThreads);
        this.arrayAdd.add(l.getAdds());
        this.arrayRemove.add(l.getRemoves());
        this.arrayContains.add(l.getContains());
        this.arrayListSize.add(l.size());
        this.arraySeconds.add(Long.toString(seconds));
    }


    public void saveStatisticsFile(String lName) {
        System.out.println("Base de dados : ");
        String dtHora = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String path = "C:\\PUCRS\\Mestrado\\Prog.Concorrente\\file_"+lName+"_"+dtHora+".csv";
        try {
            FileWriter csvWriter = new FileWriter(path,false);
            for (int j = 0; j < arrayAdd.size(); j++) {
                // System.out.println(arrayNumThreads.get(j)+";"+arrayAdd.get(j) + ";"+ arrayRemove.get(j) +";"+arrayContains.get(j)+";"+arrayListSize.get(j)+";");
                csvWriter.append(   lName+";"+dtHora+";"+
                                    arrayInitList.get(j)+";"+
                                    arrayNumThreads.get(j)+";"+
                                    arrayAdd.get(j) + ";"+ 
                                    arrayRemove.get(j) +";"+
                                    arrayContains.get(j)+";"+
                                    arrayListSize.get(j)+";"+
                                    arraySeconds.get(j)
                );
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
