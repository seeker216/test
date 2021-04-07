import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ReadXlsx {
    private static FormulaEvaluator evaluator;
    private Sheet sheet;
    private int numRow;
    private int numCol;
    private int maxRow;

    public int getMaxRow() {
        return maxRow;
    }

    private int maxCol;
    public ReadXlsx(String path){
        try {
            FileInputStream fis = new FileInputStream(path);
            Workbook wb = new XSSFWorkbook(fis);
            sheet=wb.getSheetAt(0);
            maxRow=sheet.getLastRowNum();
            maxCol=maxRow>1?sheet.getRow(0).getLastCellNum():0;
            checkNull();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int checkNull(){
        int num=0;

        for(numRow = 1; numRow <= maxRow; numRow++) {
            Row row = sheet.getRow(numRow);
            if(row == null) {
                continue;
            }
            // 循环cell
            for(numCol = 0; numCol < maxCol; numCol++) {
                Cell cell = row.getCell(numCol);
                if(cell == null || getValue(cell) == "-200") {
                    num++;
                    continue;
                }
                // 打印数据
    //                    System.out.println("xlsx表格中读取的数据" + getValue(cell));
            }
        }

        numRow=0;
//        System.out.println("ok:"+num);

        return num;
    }
    public String[] getNextRow(int numRow){
        String key="";
        String value="";
        String firstCell=getValue(sheet.getRow(1).getCell(0));
        if (firstCell.length()<=10){
            for(numCol=0;numCol<2;numCol++){
                key+=getValue(sheet.getRow(numRow).getCell(numCol))+" ";
            }
            for(numCol=2;numCol<maxCol;numCol++){
                value+=getValue(sheet.getRow(numRow).getCell(numCol))+" ";
            }
        }else{
            key+=getValue(sheet.getRow(numRow).getCell(0))+" ";
            for(numCol=1;numCol<maxCol;numCol++){
                value+=getValue(sheet.getRow(numRow).getCell(numCol))+" ";
            }
        }

        return new String[]{key,value};
    }
    public static String getValue(Cell cell) {
        String val = null;
        short format=cell.getCellStyle().getDataFormat();
        switch(cell.getCellType()) {
            case FORMULA:  //公式类型
                // 先计算表达式
                val = String.valueOf(evaluator.evaluate(cell).getNumberValue());
                break;
            case BOOLEAN:  //布尔类型
                val = String.valueOf(cell.getBooleanCellValue());
                break;
            case STRING:   // 字符串类型
                val = cell.getStringCellValue().trim();
                break;
            case NUMERIC:  // 数值类型
                // 日期格式
                if(DateUtil.isCellDateFormatted(cell)) {
                    if (format==14){
                        val =   Date2Str(cell.getDateCellValue(), "yyyy-MM-dd");
                    }else if (format==176||format==21){
                        val =   Date2Str(cell.getDateCellValue(), "HH:mm:ss");
                    }else if (format==22){
                        val =   Date2Str(cell.getDateCellValue(), "yyyy-MM-dd HH:mm:ss");
                    }
                }else {
                    // 四舍五入
                    val = new DecimalFormat("#.####").format(cell.getNumericCellValue());
                }
                break;
            default: //其它类型
                break;
        }
        return val;
    }
    public static String Date2Str(Date date, String format){
        // Date -> LocalDateTime -> String
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(),zone);
        return df.format(localDateTime);
    }
}
