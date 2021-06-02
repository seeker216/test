import org.apache.poi.ss.usermodel.Row;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
public class Main {
    public static void main(String[] args) {

        q1test();

//        q2test();
    }

    public static void q1test(){
        String path=Main.class.getClassLoader().getResource("LessAir2.xlsx").getPath();
        ReadXlsx rx=new ReadXlsx(path);
        MemoryCache mc=new MemoryCache();
        int maxRow=rx.getMaxRow();
        for (int i=1;i<maxRow;i++){
            String[] kv=rx.getNextRow(i);
            if (mc.getMcSize()>=mc.getMaxSize()/2){
//                if (kv[0].startsWith("2004-04-08")){
//                    mc.detectMisplace(kv);
//                }
                mc.detectMisplace(kv);
            }
            mc.addTuple(kv);
            if (mc.getMcSize()==mc.getMaxSize()){
//                mc.compressMc();
                mc.compressMcSimple(10,1);
            }
        }
        System.out.println("avg RMS:"+mc.getRms()/rx.getMaxRow());
    }

    public static void q2test(){
        String path=Main.class.getClassLoader().getResource("LessAir2.xlsx").getPath();
        ReadXlsx rx=new ReadXlsx(path);
        int maxRow=rx.getMaxRow();
        AttrCorr ac=new AttrCorr(rx,maxRow);
        int i=1;
        ac.init(rx,i);
        for (;i<maxRow;i++){
            String[] kv=rx.getNextRow(i);
            ac.processTuple(kv);
        }
    }

}