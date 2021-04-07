import gurobi.*;

public class Other {
    public static void main(String[] args) {
        double[][] t=new double[][]{{1.1,2.2,4.4,3.3}};
        int[][] x=new int[][]{{1,0,0,0},{0,1,0,0},{0,0,0,1},{0,0,1,0}};
        double[][] tr=matrixMulti(t,x);
//        System.out.println(tr);
        String str1="2015-02-08 20:20:20";
        String str2="2015-01-08 10:10:10";
        int res=str1.compareTo(str2);
        if(res>0)
            System.out.println("str1>str2");
        else if(res==0)
            System.out.println("str1=str2");
        else
            System.out.println("str1<str2");
    }



    private static double[][] matrixMulti(double[][] t, int[][] x) {
        if (t[0].length!=x.length){
            return null;
        }
        int n=t.length;
        int m=x[0].length;
        double[][] res=new double[n][m];
        for (int i=0;i<n;i++){
            for (int j=0;j<m;j++){
                for (int k=0;k<x.length;k++){
                    res[i][j]+=t[i][k]*x[k][j];
                }
            }
        }
        return res;
    }
}
