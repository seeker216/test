import org.apache.poi.ss.usermodel.Row;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
public class Main {
    public static void main(String[] args) {
        String path=Main.class.getClassLoader().getResource("AirQualityUCI.xlsx").getPath();
        ReadXlsx rx=new ReadXlsx(path);
        MemoryCache mc=new MemoryCache();
        int maxRow=rx.getMaxRow();
        for (int i=1;i<maxRow;i++){
            while (mc.getMcSize()>=mc.getMaxSize()/2){

            }
            String[] kv=rx.getNextRow(i);
            mc.addTuple(kv[0],kv[1]);
            if (mc.getMcSize()==mc.getMaxSize()){
                mc.compressMc();
            }
        }
    }
}