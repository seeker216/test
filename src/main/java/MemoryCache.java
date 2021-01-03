import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    }
    public void computeDistance(String[] kv){
        double dis=0;
        double[] vals1=str2val(kv[1]);

        for (Map.Entry entry:cache.entrySet()){
            double[] vals2=str2val(entry.getValue().toString());
            for (int i=0;i<numAttr;i++){
                dis+=(Math.abs(vals1[i]-vals2[i])-min[i])/(max[i]-min[i]);
            }
        }
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
