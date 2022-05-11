package com.sanni.test.algorithm;



import java.util.ArrayList;

public class AlgorithmSolutions {



    static ArrayList<Inputs> mergeIntervals(ArrayList<Inputs> v) {

        if(v == null || v.size() == 0) {
            return null;
        }

        ArrayList<Inputs> result = new ArrayList<Inputs>();

        result.add(new Inputs(v.get(0).val1, v.get(0).val2));

        for(int i = 1 ; i < v.size(); i++) {
            int x1 = v.get(i).val1;
            int y1 = v.get(i).val2;
            int x2 = result.get(result.size() - 1).val1;
            int y2 = result.get(result.size() - 1).val2;

            if(y2 >= x1) {
                result.get(result.size() - 1).val2 = Math.max(y1, y2);
            } else {
                result.add(new Inputs(x1, y1));
            }
        }

        return result;
    }
    public static void main(String[] args) {
        ArrayList<Inputs> v = new ArrayList<Inputs>();


        v.add(new Inputs(6, 8));
        v.add(new Inputs(10, 12));
        v.add(new Inputs(11, 13));



        ArrayList<Inputs> result = mergeIntervals(v);

        for(int i=0; i<result.size(); i++){
            System.out.print(String.format("[%d, %d] ", result.get(i).val1, result.get(i).val2));
        }
    }
}
