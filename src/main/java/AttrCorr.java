import java.util.*;

public class AttrCorr {
    private static int SEGMENT_LENGTH = 20;
    private static int MEMORY_SIZE = 50;
    private int numAttr;
    private double[][] preSCM;
    private Map<String,Map<String, String>> cache = new HashMap<>();
    private Map<String, String> segment = new LinkedHashMap<>();
    private Map<String,double[][]> scmList = new HashMap<>();
    private int serNum=0;
    private double theta=0.6;

    public AttrCorr() {
    }

    public AttrCorr(int num) {
        this.numAttr = num;
    }

    public AttrCorr(ReadXlsx rx, int maxRow) {

    }

    public int getSegmentLength() {
        return SEGMENT_LENGTH;
    }

//    public void setCache(Map newCache){
//        this.cache=newCache;
//    }

    public Map<String, Map<String, String>> getCache() {
        return cache;
    }

    public void init(ReadXlsx rx, int i) {
        for (i = 1; i < 5 * SEGMENT_LENGTH; i++) {
            String[] kv = rx.getNextRow(i);
            String[] attrStr = kv[1].split(" ");
            numAttr = attrStr.length;
            double[] attrVal = new double[numAttr];
            double[][] newSCM = null;
            for (int j = 0; j < numAttr; j++) {
                attrVal[j] = Double.valueOf(attrStr[j]);
            }
            segment.put(kv[0], kv[1]);
        }
        preSCM = calPartSCM(segment);
        segment.clear();
    }

    /*
    逐个处理，首先将元组存入cache，满之后会包含若干个段，对每个段进行处理，
    每个段的SCM和preSCM比较，确定发生错位的起止段，
    处理时先处理中间的，因为全都是错位，最后处理首尾的，确定开始和结束的点，
    根据相关性系数确定交换的列。
     */
    public void processTuple(String[] kv) {
        String[] attrStr = kv[1].split(" ");
        numAttr = attrStr.length;
        double[] attrVal = new double[numAttr];
        double[][] newSCM = null;
        int x=-1;
        int y=-1;
        for (int i = 0; i < numAttr; i++) {
            attrVal[i] = Double.valueOf(attrStr[i]);
        }
        segment.put(kv[0], kv[1]);
        //
        if (segment.size() == SEGMENT_LENGTH) {
//            this.cache=PAAProcess(this.cache,10);
            double[][] currSCM = calPartSCM(this.segment);
            scmList.put(String.valueOf(serNum),currSCM);

            //flush
            cache.put(String.valueOf(serNum),new LinkedHashMap<>(segment));
            segment.clear();
            serNum++;
            if (cache.size() == MEMORY_SIZE) {
                boolean find=false;
                int startNum=-1;
                int endNum=-1;
                for (String k:cache.keySet()){
                    if (!equalSCM(preSCM,scmList.get(k))){
                        startNum=startNum==-1? Integer.valueOf(k) :startNum;
                        find=true;
                    }else{
                        if (find){
                            endNum=Integer.valueOf(k)-1;
                        }
                    }
                }
                if (endNum>startNum+1){
                    int[] xy=repairSubSeq(cache.get(String.valueOf(startNum+1)),scmList.get(String.valueOf(startNum+1)));
                    x=xy[0];
                    y=xy[1];
                    //中间段的处理
                    for (int i=startNum+1;i<endNum;i++){
                        swapSubSeq(cache.get(String.valueOf(i)),x,y);
                    }
                }
                searchPoint(cache.get(String.valueOf(startNum)),x,y);
                searchPoint(cache.get(String.valueOf(endNum)),x,y);
            }
        }
    }

    /*
    对起止段进行修复
     */
    private void searchPoint(Map<String, String> map, int x, int y) {
        if (x==-1&&y==-1){

        }else{

        }
    }

    /*
    修复
     */
    private int[] repairSubSeq(Map<String, String> map, double[][] scm) {
        int targetX=0;
        int targetY=0;
        double maxDiff=0;
        for (int i=0;i<numAttr;i++){
            for (int j=0;j<numAttr;j++){
                double diff=scm[i][j]-preSCM[i][j];
                if (diff>maxDiff){
                    targetX=i;
                    targetY=j;
                }
            }
        }
        scm=swapSubSeq(map,targetX,targetY);
        return new int[]{targetX, targetY};
    }

    private double[][] swapSubSeq(Map<String, String> map, int x, int y) {
        for (String k:map.keySet()){
            String[] v=map.get(k).split(" ");
            String tmp=v[x];
            v[x]=v[y];
            v[y]=tmp;
            String newVal="";
            for (String s:v){
                newVal+=s+" ";
            }
            map.put(k,newVal.substring(0,newVal.length()-1));
        }
        double[][] scm=calPartSCM(map);
        return scm;
    }

    /*
    判断scm相等
     */
    private boolean equalSCM(double[][] scm1, double[][] scm2) {
        int row=scm1.length;
        int col=scm1[0].length;
        if (scm1.length!=scm2.length||scm1[0].length!=scm2[0].length){
            return false;
        }
        for (int i=0;i<row;i++){
            for (int j=0;j<col;j++){
                int num1=scm1[i][j]>=theta?1:0;
                int num2=scm2[i][j]>=theta?1:0;
                if (num1!=num2){
                    return false;
                }
            }
        }
        return true;
    }

    /*
    PAA处理
     */
    public Map PAAProcess(Map oldSeries, int w) {
        int len = oldSeries.size();
        double[][] data = new double[numAttr][len];
        Map<String, double[]> newSeries = new HashMap<>();
        Map<String, String> tmpMap = new HashMap<>();
        Set<Map.Entry> oldSet = oldSeries.entrySet();
        int row = 0;
        for (Map.Entry me : oldSet) {
            String[] vals = me.getValue().toString().split(" ");
            for (int i = 0; i < numAttr; i++) {
                data[i][row] = Double.valueOf(vals[i]);
            }
            row++;
            tmpMap.put(String.valueOf(row), (String) me.getKey());
        }
        for (int i = 0; i < numAttr; i++) {
            double[] newData = new double[w];
            getZScore(data[i]);
            for (int j = 0; j < w; j++) {
                int sum = 0;
                for (int l = len / w * (j - 1); l < len / w * i; l++) {
                    sum += data[i][l];
                }
                newData[j] = sum * w / len;
            }
            newSeries.put(tmpMap.get(i), newData);
        }
        return newSeries;
    }

    /*
    计算一段上的时间序列相关性矩阵SCM
     */
    private double[][] calPartSCM(Map kSeries) {
        int len = kSeries.size();
        double[][] corrMatr = new double[numAttr][numAttr];
        double[] avg = new double[numAttr];
        double[][] data = new double[numAttr][len];
        Set<Map.Entry> set = kSeries.entrySet();
        int col = 0;
        for (Map.Entry me : set) {
            double[] val = (double[]) me.getValue();
            for (int i = 0; i < numAttr; i++) {
                data[i][col] = val[i];
            }
            col++;
        }
        for (int i = 0; i < numAttr; i++) {
            avg[i] = Arrays.stream(data[i]).average().orElse(Double.NaN);
        }
        for (int i = 0; i < numAttr; i++) {
            for (int j = 0; j < numAttr; j++) {
                for (int k = 0; k < len; k++) {
                    corrMatr[i][j] += (data[i][k] - avg[i]) * (data[j][k] - avg[j]);
                }
                corrMatr[i][j] /= (len - 1);
            }
        }

        return corrMatr;
    }

    /*
    合并SCM
     */
    private double[][] mergeSCM(List list) {
        int len = list.size();
        double[][] finalSCM = new double[numAttr][numAttr];
        for (Object o : list) {
            double[][] scm = (double[][]) o;
            for (int i = 0; i < numAttr; i++) {
                for (int j = 0; j < numAttr; j++) {
                    finalSCM[i][j] += scm[i][j];
                }
            }
        }
        for (int i = 0; i < numAttr; i++) {
            for (int j = 0; j < numAttr; j++) {
                finalSCM[i][j] /= len;
            }
        }
        return finalSCM;
    }

    /*
    计算时间序列相关性图
     */
    private int[][] getSCG(double[][] SCM, double theta) {
        int[][] SCG = new int[numAttr][numAttr];
        for (int i = 0; i < numAttr; i++) {
            for (int j = i + 1; j < numAttr; j++) {
                if (Math.abs(SCM[i][j]) > theta) {
                    SCG[i][j] = 1;
                }
            }
        }
        return SCG;
    }

    /*
    划分时间序列团
     */
    private List divideGroup(int[][] SCG, double theta) {
        List<List<Integer>> groupSet = new ArrayList<>();
        boolean[] visited = new boolean[numAttr];
//        Set<Integer> prunedAttr=
        return groupSet;
    }

    private void getZScore(double[] data) {
        double avg = Arrays.stream(data).average().orElse(Double.NaN);
        double sd = 0;
        for (double x : data) {
            sd += (x - avg) * (x - avg);
        }
        sd = Math.sqrt(sd / data.length);
        for (int i = 0; i < data.length; i++) {
            data[i] = (data[i] - avg) / sd;
        }
    }
}
