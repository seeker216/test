import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache {
    private int maxSize=20;

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
    }
    public MemoryCache(int size) {
        this.maxSize = size;
        cache = new HashMap<>();
    }
    public void addTuple(String key,String value){
        cache.put(key,value);
    }

    public int getMcSize(){
        return this.cache.size();
    }

    public void compressMc(){

    }

    public void computeDistance(String[] kv){
        int numAttr=kv[1].split(" ").length;
        double[] max=new double[numAttr];
        double[] min=new double[numAttr];
        for (Map.Entry entry:cache.entrySet()){
            for (int i=0;i<numAttr;i++){

            }
        }
    }
}
