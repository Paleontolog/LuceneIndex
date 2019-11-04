package metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NDCG {

    private static double log2(int x) {
        return Math.log(x) / Math.log(2);
    }

    public static Double getDCG(Integer[] ratings, int p) {
        double dcgScore = 0.0;
//        for (int i = 1; i < p; i++) {
//            double logPosition = log2(i + 1);
//            double dcgAdder = ratings[i - 1] / logPosition;
//            dcgScore += dcgAdder;
//        }
        for (int i = 1; i < p; i++) {
            double logPosition = log2(i + 1);
            double dcgAdder = (Math.pow(2, ratings[i - 1]) - 1) / logPosition;
            dcgScore += dcgAdder;
        }
        return dcgScore;
    }

    public static double getNDCG(Integer[] ratings, int p) {
        double dcgScore = getDCG(ratings, p);
        List<Integer> res = Arrays.asList(ratings);
        res.sort((e1, e2) -> -e1.compareTo(e2));
        double idcgScore = getDCG((Integer[]) res.toArray(), p);
        return dcgScore / idcgScore;
    }

    public static void main(String[] args) {
//        List<Integer> arr = Arrays.asList(1, 2, 3, 4);
//        arr.sort((e1, e2) -> -e1.compareTo(e2));
//        System.out.println(arr);
    }
}
