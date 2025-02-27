package blkjweb.utils;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
//首先使用了cell.getCellStyle().getDataFormatString()判断，如果能得到就返回“yyyy/MM/dd”格式的日期。如果得不到，最后都返回cell.toString()。

public class CellUtil {
    
    public static String returnCellValue(Cell cell){
        String cellvalue = "";
        if (null != cell) {
        	/**poi获取单元格类型方法getCellTypeEnum()在3.15版本中过时 
        	  * 返回 int 的在 4.0 版本后将要替换成返回 CellType，但是名字还是 getCellType 
        	*/
            CellType type = cell.getCellTypeEnum();
           
            switch (type) {   
            case NUMERIC: // 数字
            	 String value  = null;
            	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
            	 if("General".equals(cell.getCellStyle().getDataFormatString())){  
                     value = sdf.format(cell.getNumericCellValue());  
                 }
            	 else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){  
                     value = sdf.format(cell.getDateCellValue());  
                 }
            	 else{  
                     value = sdf.format(cell.getNumericCellValue());  
                 }  
                return String.valueOf(value).trim();
            case STRING: // 字符串   
                return String.valueOf(cell.getRichStringCellValue().getString()).trim();
            case BOOLEAN: // Boolean   
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA: // 公式   
                return String.valueOf(cell.getCellFormula()).trim();  
            case BLANK: // 空值   
                return "";  
            case ERROR: // 故障   
                return "";
            default:   
                return "";   
            }   
        } else {
        }  
        return cellvalue;
    }
    
    //避免cell.setCellValue(checkOrderQmSave.getSellOrderNo())中参数为空就会报错
    public static void setCellValue(HSSFCell cell, Object object){
        if(object == null){
            cell.setCellValue("");  
        }else{
            if (object instanceof String) {
                cell.setCellValue(String.valueOf(object));  
            }else if(object instanceof Long){
                Long temp = (Long)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);  
            }else if(object instanceof Double){
                Double temp = (Double)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);  
            }else if(object instanceof Float){
                Float temp = (Float)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);  
            }else if(object instanceof Integer){
                Integer temp = (Integer)object;
                cell.setCellValue(temp.intValue());  
            }else if(object instanceof BigDecimal){
                BigDecimal temp = (BigDecimal)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);  
            }else{
                cell.setCellValue("");  
            }
        }
    }
    public static void setCellValue(HSSFCell cell, Object object, String model){
        if(object == null){
            cell.setCellValue("");  
        }else{
            if (object instanceof String) {
                cell.setCellValue(String.valueOf(object));  
            }else if(object instanceof Long){
                Long temp = (Long)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);  
            }else if(object instanceof Double){
                Double temp = (Double)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);  
            }else if(object instanceof Float){
                Float temp = (Float)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);  
            }else if(object instanceof Integer){
                Integer temp = (Integer)object;
                cell.setCellValue(temp.intValue());  
            }else if(object instanceof BigDecimal){
                BigDecimal temp = (BigDecimal)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);  
            }else if(object instanceof java.sql.Date){
                cell.setCellValue(new SimpleDateFormat(model).format(object));  
            }else if(object instanceof java.util.Date){
                cell.setCellValue(new SimpleDateFormat(model).format(object));  
            }else{
                cell.setCellValue("");  
            }
        }
    }
    public static void setCellValue(HSSFCell cell, String object){
        if(object == null){
            cell.setCellValue("");  
        }else{
            cell.setCellValue(object);  
        }
    }
    public static void setCellValue(HSSFCell cell, Long object){
        if(object == null){
            cell.setCellValue("");  
        }else{
            cell.setCellValue(object.doubleValue());  
        }
    }
    public static void setCellValue(HSSFCell cell, Double object){
        if(object == null){
            cell.setCellValue("");  
        }else{
            cell.setCellValue(object.doubleValue());  
        }
    }
    public static void setCellValue(HSSFCell cell, double object){
        
            cell.setCellValue(object);  
        
    }
    public static void setCellValue(HSSFCell cell, Date object, String model){
        if(object == null){
            cell.setCellValue("");  
        }else{
            cell.setCellValue(new SimpleDateFormat(model).format(object));  
        }
    }
    public static void setCellValue(HSSFCell cell, java.util.Date object, String model){
        if(object == null){
            cell.setCellValue("");  
        }else{
            cell.setCellValue(new SimpleDateFormat(model).format(object));  
        }
    }
    public static void setCellValue(HSSFCell cell, BigDecimal object){
        if(object == null){
            cell.setCellValue("");  
        }else{
            cell.setCellValue(object.toString());  
        }
    }
    
    //判断EXCEL表格高度用 row.setHeight((short) CellUtil.getExcelCellAutoHeight(TAR_VAL_ALL_STRING, 280, 30));
    public static float getExcelCellAutoHeight(String str,float defaultRowHeight, int fontCountInline) {
        int defaultCount = 0;
        for (int i = 0; i < str.length(); i++) {
            int ff = getregex(str.substring(i, i + 1));
            defaultCount = defaultCount + ff;
        }
        if (defaultCount > fontCountInline){
            return ((int) (defaultCount / fontCountInline) + 1) * defaultRowHeight;//计算
        } else {
            return defaultRowHeight;
        }
    }
    public static int getregex(String charStr) {
        if("".equals(charStr) || charStr == null){
            return 1;
        }
        // 判断是否为字母或字符
        if (Pattern.compile("^[A-Za-z0-9]+$").matcher(charStr).matches()) {
            return 1;
        }
        // 判断是否为全角
        if (Pattern.compile("[\u4e00-\u9fa5]+$").matcher(charStr).matches()) {
            return 2;
        }
        //全角符号 及中文
        if (Pattern.compile("[^x00-xff]").matcher(charStr).matches()) {
            return 2;
        }
        return 1;
    }
}
