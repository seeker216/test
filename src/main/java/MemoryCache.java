import org.apache.commons.math3.util.Pair;

import java.util.*;

public class MemoryCache {
    private int maxSize=20;
    private double[] max;
    private double[] min;
    private int numAttr;
    public int getMaxSize() {
        return maxSize;
    }

    //    private int size;
    private Map<String,String> cache;
    public MemoryCache(){
        cache=new LinkedHashMap<String, String>((int) (Math.ceil(maxSize/0.75f)+1),0.75f,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<String,String> eldest){
                return size()>maxSize;
            }
        };
        max=new double[0];
        min=new double[0];
    }
    public MemoryCache(int size) {
        this.maxSize = size;
        cache = new HashMap<>();
    }
    public void addTuple(String[] kv){
        String[] attrStr=kv[1].split(" ");
        numAttr=attrStr.length;
        double[] attrVal=new double[numAttr];
        for (int i=0;i<numAttr;i++){
            attrVal[i]=Double.valueOf(attrStr[i]);
        }
        if (max.length==0){
            max=new double[numAttr];
            min=new double[numAttr];
        }
        for (int i=0;i<numAttr;i++){
            max[i]= Math.max(max[i],attrVal[i]);
            min[i]=Math.min(min[i],attrVal[i]);
        }
        cache.put(kv[0],kv[1]);

    }

    public int getMcSize(){
        return this.cache.size();
    }

    public void compressMc(){
        for (int i=0;i<getMaxSize()/4;i++){
//            cache.keySet().
        }
    }
    public void detectMisplace(String[] kv){
        double[] disArr=computeDistance(kv);
        double origiDis=getDistance(disArr);
        double[][] disIdxArr=getSortIdxArr(disArr);
        double currDis=origiDis;
        String currVal=kv[1];
        Set<String> candidata=new HashSet<>();
        do{
            currVal=swapAttr(disIdxArr,str2val(currVal));
            if (candidata.contains(currVal)){
                currVal=kv[1];
                break;
            }
            candidata.add(currVal);
            disIdxArr=getSortIdxArr(str2val(currVal));
//            origiDis=currDis;
            currDis=getDistance(computeDistance(new String[]{kv[0],currVal}));
        }while (currDis>origiDis);
        if (currVal!=kv[1]&&currDis/origiDis<0.75){

            System.out.println("old:"+kv[1]);
            System.out.println("new:"+currVal);
        }
    }

    private double[][] getSortIdxArr(double[] attrArr) {
        double[][] res=new double[numAttr][2];
        for (int i=0;i<numAttr;i++){
            res[i]=new double[]{i,attrArr[i]};
        }
        Arrays.sort(res, (o1, o2) -> {
            if (o1[1]-o2[1]>1e-5){
                return -1;
            }else {
                return 1;
            }
        });
        return res;
    }

    public String swapAttr(double[][] disIdxArr,double[] val) {
        double tmp=val[(int)disIdxArr[0][0]];
        val[(int)disIdxArr[0][0]]=val[(int)disIdxArr[1][0]];
        val[(int)disIdxArr[1][0]]=tmp;
        String res=""+val[0];
        for (int i=1;i<numAttr;i++){
            res+=" "+val[i];
        }
        return res;
    }

    public double[] computeDistance(String[] kv){
        double[] vals1=str2val(kv[1]);
        double[] dis=new double[numAttr];
        for (Map.Entry entry:cache.entrySet()){
            double[] vals2=str2val(entry.getValue().toString());
            for (int i=0;i<numAttr;i++){
                dis[i]+=(Math.abs(vals1[i]-vals2[i])-min[i])/(max[i]-min[i]);
            }
        }
        for (int i=0;i<numAttr;i++){
            dis[i]/=getMcSize();
        }
        return dis;
    }
    public double getDistance(double[] disArr){
        double dis=0;
        for (double d:disArr){
            dis+=d;
        }
        return dis;
    }
    public double[] str2val(String str){
        String[] strs=str.split(" ");
        double[] vals=new double[strs.length];
        for (int i=0;i<strs.length;i++){
            vals[i]=Double.valueOf(strs[i]);
        }
        return vals;
    }
}
