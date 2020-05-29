/*
PUCRS
Programação Concorrente - Prof. Fernando Dotti
Gibson Weinert
*/

package data;

import java.util.ArrayList;

import interfaces.CommonList;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BD{

    public ArrayList<String> arrayListName = new ArrayList<String>();
    public ArrayList<String> arrayDtHora = new ArrayList<String>();
    public ArrayList<Integer> arrayInitList = new ArrayList<Integer>();
    public ArrayList<Integer> arrayNumThreads = new ArrayList<Integer>();
    public ArrayList<Integer> arrayAdd = new ArrayList<Integer>();
    public ArrayList<Integer> arrayRemove = new ArrayList<Integer>();
    public ArrayList<Integer> arrayContains = new ArrayList<Integer>();
    public ArrayList<Integer> arrayListSize = new ArrayList<Integer>();
    public ArrayList<String> arraySeconds = new ArrayList<String>();

    public void setStatistics (CommonList<Integer> l,int numItensList,int numThreads,long seconds,String dtHora) {

        this.arrayListName.add(l.getListName());
        this.arrayDtHora.add(dtHora);
        this.arrayInitList.add(numItensList);
        this.arrayNumThreads.add(numThreads);
        this.arrayAdd.add(l.getAdds());
        this.arrayRemove.add(l.getRemoves());
        this.arrayContains.add(l.getContains());
        this.arrayListSize.add(l.size());
        this.arraySeconds.add(Long.toString(seconds));
    }


    public void saveStatisticsFile() {
        String dtHora = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String path = "C:\\PUCRS\\Mestrado\\Prog.Concorrente\\code\\data\\file"+"_"+dtHora+".csv";
        try {
            FileWriter csvWriter = new FileWriter(path,false);
            for (int j = 0; j < arrayAdd.size(); j++) {
                csvWriter.append(   arrayListName.get(j)+";"+
                                    arrayDtHora.get(j)+";"+
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
            System.out.println("Gerado o arquivo : "+"file"+"_"+dtHora+".csv");

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
