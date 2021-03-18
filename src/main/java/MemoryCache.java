import org.apache.commons.math3.util.Pair;

import javax.xml.crypto.Data;
import java.util.*;

public class MemoryCache {
    private int maxSize = 20;
    private double[] max;
    private double[] min;
    private int numAttr;
    private static int k;

    public int getMaxSize() {
        return maxSize;
    }

    //    private int size;
    private Map<String, String> cache;

    public MemoryCache() {
        cache = new LinkedHashMap<String, String>((int) (Math.ceil(maxSize / 0.75f) + 1), 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > maxSize;
            }
        };
        max = new double[0];
        min = new double[0];
    }

    public MemoryCache(int size) {
        this.maxSize = size;
        cache = new LinkedHashMap<>();
        max = new double[0];
        min = new double[0];
    }

    public void addTuple(String[] kv) {
        String[] attrStr = kv[1].split(" ");
        numAttr = attrStr.length;
        double[] attrVal = new double[numAttr];
        for (int i = 0; i < numAttr; i++) {
            attrVal[i] = Double.valueOf(attrStr[i]);
        }
        if (max.length == 0) {
            max = new double[numAttr];
            min = new double[numAttr];
        }
        for (int i = 0; i < numAttr; i++) {
            max[i] = Math.max(max[i], attrVal[i]);
            min[i] = Math.min(min[i], attrVal[i]);
        }
        cache.put(kv[0], kv[1]);

    }

    public int getMcSize() {
        return this.cache.size();
    }

    /*
    简单压缩
     */
    public void compressMc() {
        for (int i = 0; i < getMaxSize() / 4; i++) {
            String key = cache.entrySet().iterator().next().getKey();
            cache.remove(key);
        }
    }

    /*
    nds压缩
    Ck,n 以xn为在Z中第k近的点的集合
    pk(xn) xn与Z中第k近的点的距离
    vk(xn) xn与X中第k近的点的距离
    |Z|=|X|/2
     */
    public void compressMcSimple(int iterNum, double stepSize) {
        double[] y = new double[getMaxSize()];
        Arrays.fill(y, 0.5d);

        Map<String, String> newCache = new HashMap<>();//Z
        for (int i = 0; i < iterNum; i++) {
            stepSize *= 0.95;
            for (int j = 0; j < getMaxSize() / 2; j++) {
//                y[j]=y[j]-stepSize*(sum(xi in Ck,n , y[i])+pk(xn)/vk(xn)-e^lof(xn)+)
//                y[j]=y[j]-stepSize*(computePrePk(j)/computeVk(j)-Math.exp(computeLof(j))+adjustYn(y[j])+(getSum(y)-getMaxSize()/4));

            }
        }
        Queue<Map.Entry> q = new PriorityQueue<>(new Comparator<Map.Entry>() {
            @Override
            public int compare(Map.Entry o1, Map.Entry o2) {
                return ((Integer) o2.getValue() - (Integer) o1.getValue());
            }
        });
        for (int i = y.length - 1; i > getMaxSize() / 2; i--) {
            y[i] = 1.0;
        }
        cache = newCache;
    }

    private double computeLof(int j) {
        List<DataNode> dpoints=new ArrayList<>();
        for (Map.Entry e:cache.entrySet()){
            dpoints.add(new DataNode((String) e.getKey(),(String) e.getValue()));
        }

        return 0;
    }

    private double computeVk(int j) {
        Map.Entry[] x = (Map.Entry[]) cache.entrySet().toArray();
        int len = getMaxSize();
        double[] disList = new double[len];
        for (int i = 0; i < len; i++) {
            disList[i] = compute2pointsDistance((String) x[j].getValue(), (String) x[i].getValue());
        }
        Arrays.sort(disList);
        return disList[k];
    }

    /*
    初始时计算Pk(xn)
     */
    private double computePrePk(int j) {
        Map.Entry[] x = (Map.Entry[]) cache.entrySet().toArray();
        int len = getMaxSize();
        double[] disList = new double[len];
        for (int i = 0; i < len; i++) {
            disList[i] = compute2points2Norm((String) x[j].getValue(), (String) x[i].getValue());
        }
        Arrays.sort(disList);

        return disList[getMaxSize() / 4 + k - 1];
    }

    /*
    检测错位
     */
    public void detectMisplace(String[] kv) {
        double[] disArr = computeDistance(kv);
        double origiDis = getDistance(disArr);
        double[][] disIdxArr = getSortIdxArr(disArr);
        double currDis = origiDis;
        String currVal = kv[1];
        Set<String> candidata = new HashSet<>();
        do {
            currVal = swapAttr(disIdxArr, str2val(currVal));
            if (candidata.contains(currVal)) {
                currVal = kv[1];
                break;
            }
            candidata.add(currVal);
            disIdxArr = getSortIdxArr(str2val(currVal));
//            origiDis=currDis;
            currDis = getDistance(computeDistance(new String[]{kv[0], currVal}));
        } while (currDis > origiDis);
        if (currVal != kv[1] && currDis / origiDis < 0.85) {
//        if (currVal!=kv[1]){
            String[] olds = kv[1].split(" ");
            String old = "";
            for (String s : olds) {
                old += String.format("%10s", s);
            }
            String[] news = currVal.split(" ");
            String ne = "";
            for (String s : news) {
                ne += String.format("%10s", s);
            }
            System.out.println("old:" + old);
            System.out.println("new:" + ne);
        }
    }

    private double[][] getSortIdxArr(double[] attrArr) {
        double[][] res = new double[numAttr][2];
        for (int i = 0; i < numAttr; i++) {
            res[i] = new double[]{i, attrArr[i]};
        }
        Arrays.sort(res, (o1, o2) -> {
            if (o1[1] - o2[1] > 1e-5) {
                return -1;
            } else {
                return 1;
            }
        });
        return res;
    }

    public String swapAttr(double[][] disIdxArr, double[] val) {
        double tmp = val[(int) disIdxArr[0][0]];
        val[(int) disIdxArr[0][0]] = val[(int) disIdxArr[1][0]];
        val[(int) disIdxArr[1][0]] = tmp;
        String res = "" + val[0];
        for (int i = 1; i < numAttr; i++) {
            res += " " + val[i];
        }
        return res;
    }

    /*
    计算两个元组间距离
     */
    private double compute2pointsDistance(String p1, String p2) {
        double[] valsP1 = str2val(p1);
        double[] valsP2 = str2val(p2);
        double res = 0;
        for (int i = 0; i < numAttr; i++) {
            res += Math.abs(valsP1[i] - valsP2[i]);
        }
        return res;
    }

    /*
    计算两个元组的2范式
     */
    public double compute2points2Norm(String p1, String p2) {
        double[] valsP1 = str2val(p1);
        double[] valsP2 = str2val(p2);
        double res = 0;
        for (int i = 0; i < numAttr; i++) {
            res += Math.pow(valsP1[i] - valsP2[i], 2);
        }
        return Math.sqrt(res);
    }

    /*
    计算一个元组到历史元组的平均距离，返回数组，每一维是每个维度上的距离
     */
    public double[] computeDistance(String[] kv) {
        double[] vals1 = str2val(kv[1]);
        double[] dis = new double[numAttr];
        for (Map.Entry entry : cache.entrySet()) {
            double[] vals2 = str2val(entry.getValue().toString());
            for (int i = 0; i < numAttr; i++) {
                dis[i] += (Math.abs(vals1[i] - vals2[i]) - min[i]) / (max[i] - min[i]);
            }
        }
        for (int i = 0; i < numAttr; i++) {
            dis[i] /= getMcSize();
        }
        return dis;
    }

    public double getDistance(double[] disArr) {
        double dis = 0;
        for (double d : disArr) {
            dis += d;
        }
        return dis;
    }

    public double[] str2val(String str) {
        String[] strs = str.split(" ");
        double[] vals = new double[strs.length];
        for (int i = 0; i < strs.length; i++) {
            vals[i] = Double.valueOf(strs[i]);
        }
        return vals;
    }
}
