import org.apache.poi.ss.usermodel.Row;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
public class Main {
    public static void main(String[] args) {
//        String path=Main.class.getClassLoader().getResource("AirQualityUCI.xlsx").getPath();
        String path=Main.class.getClassLoader().getResource("LessAir2.xlsx").getPath();
        ReadXlsx rx=new ReadXlsx(path);
        MemoryCache mc=new MemoryCache();
        int maxRow=rx.getMaxRow();
        for (int i=1;i<maxRow;i++){
            String[] kv=rx.getNextRow(i);
            if (mc.getMcSize()>=mc.getMaxSize()/2){
                if (kv[0].startsWith("2004-04-08")){
                    mc.detectMisplace(kv);
                }
                mc.detectMisplace(kv);
            }
            mc.addTuple(kv);
            if (mc.getMcSize()==mc.getMaxSize()){
                mc.compressMc();
            }
        }
    }
}