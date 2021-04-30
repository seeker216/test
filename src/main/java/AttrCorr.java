import java.util.*;

public class AttrCorr {
    private int numAttr;
    public AttrCorr(int num){
        this.numAttr=num;
    }

    public Map PAAProcess(Map oldSeries,int w){
        int len=oldSeries.size();
        double[][] data=new double[numAttr][len];
        Map<String,double[]> newSeries=new HashMap<>();
        Map<String,String> tmpMap=new HashMap<>();
        Set<Map.Entry> oldSet=oldSeries.entrySet();
        int row=0;
        for (Map.Entry me:oldSet){
            String[] vals=me.getValue().toString().split(" ");
            for (int i=0;i<numAttr;i++){
                data[i][row]=Double.valueOf(vals[i]);
            }
            row++;
            tmpMap.put(String.valueOf(row),(String) me.getKey());
        }
        for (int i=0;i<numAttr;i++){
            double[] newData=new double[w];
            getZScore(data[i]);
            for (int j=0;j<w;j++){
                int sum=0;
                for (int l=len/w*(j-1);l<len/w*i;l++){
                    sum+=data[i][l];
                }
                newData[j]=sum*w/len;
            }
            newSeries.put(tmpMap.get(i),newData);
        }
        return newSeries;
    }

    private double[][] calPartSCM(Map kSeries){
        int len=kSeries.size();
        double[][] corrMatr=new double[numAttr][numAttr];
        double[] avg=new double[numAttr];
        double[][] data=new double[numAttr][len];
        Set<Map.Entry> set=kSeries.entrySet();
        int col=0;
        for (Map.Entry me:set){
            double[] val= (double[]) me.getValue();
            for (int i=0;i<numAttr;i++){
                data[i][col]=val[i];
            }
            col++;
        }
        for (int i=0;i<numAttr;i++){
            avg[i]=Arrays.stream(data[i]).average().orElse(Double.NaN);
        }
        for (int i=0;i<numAttr;i++){
            for (int j=0;j<numAttr;j++){
                for (int k=0;k<len;k++){
                    corrMatr[i][j]+=(data[i][k]-avg[i])*(data[j][k]-avg[j]);
                }
                corrMatr[i][j]/=(len-1);
            }
        }

        return corrMatr;
    }

    private double[][] mergeSCM(List list){
        int len=list.size();
        double[][] finalSCM=new double[numAttr][numAttr];
        for (Object o:list){
            double[][] scm=(double[][])o;
            for (int i=0;i<numAttr;i++){
                for (int j=0;j<numAttr;j++) {
                    finalSCM[i][j]+=scm[i][j];
                }
            }
        }
        for (int i=0;i<numAttr;i++){
            for (int j=0;j<numAttr;j++) {
                finalSCM[i][j]/=len;
            }
        }
        return finalSCM;
    }

    private int[][] getSCG(double[][] SCM,double theta){
        int[][] SCG=new int[numAttr][numAttr];
        for (int i=0;i<numAttr;i++){
            for (int j=i+1;j<numAttr;j++){
                if (Math.abs(SCM[i][j])>theta){
                    SCG[i][j]=1;
                }
            }
        }
        return SCG;
    }

    private List divideGroup(int[][] SCG,double theta){
        List<List<Integer>> groupSet=new ArrayList<>();
        boolean[] visited=new boolean[numAttr];
//        Set<Integer> prunedAttr=
        return groupSet;
    }

    private void getZScore(double[] data) {
        double avg= Arrays.stream(data).average().orElse(Double.NaN);
        double sd=0;
        for (double x:data){
            sd+=(x-avg)*(x-avg);
        }
        sd=Math.sqrt(sd/data.length);
        for (int i=0;i<data.length;i++){
            data[i]=(data[i]-avg)/sd;
        }
    }
}
