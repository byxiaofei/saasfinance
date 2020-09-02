package com.sinosoft.util;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.Report.ReportData;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.dto.report.ReportDataDTO;
import com.sinosoft.repository.report.ReportDataRepository;
import com.sinosoft.service.report.ReportComputeService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Excel文件操作工具类，包括读、写、合并等功能
 */
@Component
public class ExcelUtil {
    @Resource
    private ReportDataRepository reportDataRepository;

    @Resource
    private ReportComputeService reportComputeService;
    @Value("${reportAccountB}")
    private String reportAccountB;
    @Value("${reportAccountC}")
    private String reportAccountC;

    //%%%%%%%%-------常量部分 开始----------%%%%%%%%%
    /**
     * 默认的开始读取的行位置为第一行（索引值为0）
     */
    private final static int READ_START_POS = 0;

    /**
     * 默认结束读取的行位置为最后一行（索引值=0，用负数来表示倒数第n行）
     */
    private final static int READ_END_POS = 0;

    /**
     * 默认Excel内容的开始比较列位置为第一列（索引值为0）
     */
    private final static int COMPARE_POS = 0;

    /**
     * 默认多文件合并的时需要做内容比较（相同的内容不重复出现）
     */
    private final static boolean NEED_COMPARE = true;

    /**
     * 默认多文件合并的新文件遇到名称重复时，进行覆盖
     */
    private final static boolean NEED_OVERWRITE = true;

    /**
     * 默认只操作一个sheet
     */
    private final static boolean ONLY_ONE_SHEET = true;

    /**
     * 默认读取第一个sheet中（只有当ONLY_ONE_SHEET = true时有效）
     */
    private final static int SELECTED_SHEET = 0;

    /**
     * 默认从第一个sheet开始读取（索引值为0）
     */
    private final static int READ_START_SHEET = 0;

    /**
     * 默认在最后一个sheet结束读取（索引值=0，用负数来表示倒数第n行）
     */
    private final static int READ_END_SHEET = 0;

    /**
     * 默认打印各种信息
     */
    private final static boolean PRINT_MSG = true;

    //%%%%%%%%-------常量部分 结束----------%%%%%%%%%


    //%%%%%%%%-------字段部分 开始----------%%%%%%%%%
    /**
     * Excel文件路径
     */
    private String excelPath = "data.xlsx";

    /**
     * 设定开始读取的位置，默认为0
     */
    private int startReadPos = READ_START_POS;

    /**
     * 设定结束读取的位置，默认为0，用负数来表示倒数第n行
     */
    private int endReadPos = READ_END_POS;

    /**
     * 设定开始比较的列位置，默认为0
     */
    private int comparePos = COMPARE_POS;

    /**
     * 设定汇总的文件是否需要替换，默认为true
     */
    private boolean isOverWrite = NEED_OVERWRITE;

    /**
     * 设定是否需要比较，默认为true(仅当不覆写目标内容是有效，即isOverWrite=false时有效)
     */
    private boolean isNeedCompare = NEED_COMPARE;

    /**
     * 设定是否只操作第一个sheet
     */
    private boolean onlyReadOneSheet = ONLY_ONE_SHEET;

    /**
     * 设定操作的sheet在索引值
     */
    private int selectedSheetIdx = SELECTED_SHEET;

    /**
     * 设定操作的sheet的名称
     */
    private String selectedSheetName = "";

    /**
     * 设定开始读取的sheet，默认为0
     */
    private int startSheetIdx = READ_START_SHEET;

    /**
     * 设定结束读取的sheet，默认为0，用负数来表示倒数第n行
     */
    private int endSheetIdx = READ_END_SHEET;

    /**
     * 设定是否打印消息
     */
    private boolean printMsg = PRINT_MSG;

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);


    //%%%%%%%%-------字段部分 结束----------%%%%%%%%%


    /**
     * 打印消息，
     *
     * @param msg 消息内容
     */
    private void out(String msg) {
        if (printMsg) {
            out(msg, true);
        }
    }

    /**
     * 打印消息，
     *
     * @param msg 消息内容
     * @param tr  换行
     */
    private void out(String msg, boolean tr) {
        if (printMsg) {
            System.out.print(msg + (tr ? "\n" : ""));
        }
    }

    public String getExcelPath() {
        return this.excelPath;
    }

    public void setExcelPath(String excelPath) {
        this.excelPath = excelPath;
    }

    public boolean isNeedCompare() {
        return isNeedCompare;
    }

    public void setNeedCompare(boolean isNeedCompare) {
        this.isNeedCompare = isNeedCompare;
    }

    public int getComparePos() {
        return comparePos;
    }

    public void setComparePos(int comparePos) {
        this.comparePos = comparePos;
    }

    public int getStartReadPos() {
        return startReadPos;
    }

    public void setStartReadPos(int startReadPos) {
        this.startReadPos = startReadPos;
    }

    public int getEndReadPos() {
        return endReadPos;
    }

    public void setEndReadPos(int endReadPos) {
        this.endReadPos = endReadPos;
    }

    public boolean isOverWrite() {
        return isOverWrite;
    }

    public void setOverWrite(boolean isOverWrite) {
        this.isOverWrite = isOverWrite;
    }

    public boolean isOnlyReadOneSheet() {
        return onlyReadOneSheet;
    }

    public void setOnlyReadOneSheet(boolean onlyReadOneSheet) {
        this.onlyReadOneSheet = onlyReadOneSheet;
    }

    public int getSelectedSheetIdx() {
        return selectedSheetIdx;
    }

    public void setSelectedSheetIdx(int selectedSheetIdx) {
        this.selectedSheetIdx = selectedSheetIdx;
    }

    public String getSelectedSheetName() {
        return selectedSheetName;
    }

    public void setSelectedSheetName(String selectedSheetName) {
        this.selectedSheetName = selectedSheetName;
    }

    public int getStartSheetIdx() {
        return startSheetIdx;
    }

    public void setStartSheetIdx(int startSheetIdx) {
        this.startSheetIdx = startSheetIdx;
    }

    public int getEndSheetIdx() {
        return endSheetIdx;
    }

    public void setEndSheetIdx(int endSheetIdx) {
        this.endSheetIdx = endSheetIdx;
    }

    public boolean isPrintMsg() {
        return printMsg;
    }

    public void setPrintMsg(boolean printMsg) {
        this.printMsg = printMsg;
    }

    /**
     * 通用读取Excel
     *
     * @param wb
     * @return
     * @Title: readExcel
     */
    private List<Row> readExcel(Workbook wb) {
        List<Row> rowList = new ArrayList<Row>();

        int sheetCount = 1;//需要操作的sheet数量

        Sheet sheet = null;
        if (onlyReadOneSheet) {   //只操作一个sheet
            // 获取设定操作的sheet(如果设定了名称，按名称查，否则按索引值查)
            System.out.println("com.sinosoft.util.readExcel(Workbook wb)——>正在获取selectedSheetName");
            sheet = selectedSheetName.equals("") ? wb.getSheetAt(selectedSheetIdx) : wb.getSheet(selectedSheetName);
            System.out.println("com.sinosoft.util.readExcel(Workbook wb)——>已获取获取selectedSheetName");
            if (sheet == null) {
                logger.error("无法找到【" + selectedSheetName + "】");
                throw new RuntimeException("无法找到【" + selectedSheetName + "】");
            }
            System.out.println("com.sinosoft.util.readExcel(Workbook wb)——>存在selectedSheetName：" + sheet.toString());
        } else {                          //操作多个sheet
            sheetCount = wb.getNumberOfSheets();//获取可以操作的总数量
        }

        // 获取sheet数目
        for (int t = startSheetIdx; t < sheetCount + endSheetIdx; t++) {
            // 获取设定操作的sheet
            if (!onlyReadOneSheet) {
                sheet = wb.getSheetAt(t);
            }

            //获取最后行号
            int lastRowNum = sheet.getLastRowNum();

            if (lastRowNum > 0) {    //如果>0，表示有数据
                out("\n开始读取名为【" + sheet.getSheetName() + "】的内容：");
            }

            Row row = null;
            // 循环读取
            for (int i = startReadPos; i <= lastRowNum + endReadPos; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    // if (row.getCell(2) != null) {
                    // if (Cell.CELL_TYPE_BLANK == row.getCell(2)
                    // .getCellType()) {
                    // row.getCell(2).setCellValue(" ");
                    // }
                    // }
//					if (row.getCell(2). != null) {
//                	String cellValue = getCellValue(row.getCell(2));
//							System.out.println(cellValue);
//					}
                    rowList.add(row);
                    /*out("第"+(i+1)+"行：",false);
                     // 获取每一单元格的值
                     for (int j = 0; j < row.getLastCellNum(); j++) {
                         String value = getCellValue(row.getCell(j));
                         //row.getCell(j).setCellValue(value);
                         if (!value.equals("")) {
                             out(value + " | ",false);
                         }
                     }
                     out(""); */
                }
            }
        }
        return rowList;
    }

    /**
     * 复制一个单元格样式到目的单元格样式
     *
     * @param fromStyle
     * @param toStyle
     */
    public static void copyCellStyle(CellStyle fromStyle, CellStyle toStyle) {
        toStyle.setAlignment(fromStyle.getAlignment());
        // 边框和边框颜色
        toStyle.setBorderBottom(fromStyle.getBorderBottom());
        toStyle.setBorderLeft(fromStyle.getBorderLeft());
        toStyle.setBorderRight(fromStyle.getBorderRight());
        toStyle.setBorderTop(fromStyle.getBorderTop());
        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

        // 背景和前景
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

        // 数据格式
        toStyle.setDataFormat(fromStyle.getDataFormat());
        toStyle.setFillPattern(fromStyle.getFillPattern());
        // toStyle.setFont(fromStyle.getFont(null));
        toStyle.setHidden(fromStyle.getHidden());
        toStyle.setIndention(fromStyle.getIndention());// 首行缩进
        toStyle.setLocked(fromStyle.getLocked());
        toStyle.setRotation(fromStyle.getRotation());// 旋转
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
        toStyle.setWrapText(fromStyle.getWrapText());

    }

    public void exportu(HttpServletRequest request,
                        HttpServletResponse response, String name, String cols,
                        List<?> dataList) {
        if (name == null || cols == null || dataList == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(
                    cols.replaceAll("&quot;", "\""),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);
            HSSFCellStyle titleStyle = wb.createCellStyle();
            //titleStyle:左右居中、粗体
            titleStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);// 颜色
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);

            //无背景色
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle odd_left_Style = wb.createCellStyle();
            odd_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            odd_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            //背景填充
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle even_left_Style = wb.createCellStyle();
            even_left_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_left_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            even_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            Map<String, Object> col, data;
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(col.get("title").toString());
                // int a=(int) (Integer.parseInt(col.get("width").toString()) *
                // 40);
                byte[] b = col.get("title").toString().getBytes("gbk");
                // 设置execl列宽为标题的2倍
                int d = b.length * 200;
                if (i == 2 || i == 3 || i == 9) {
                    d = 255 * 42;
                    sheet.setColumnWidth(i, d);
                } else {
                    sheet.setColumnWidth(i, d * 2);
                }

            }
            Object value;
            for (int i = 0; i < dataList.size(); i++) {//行
                data = (Map<String, Object>) dataList.get(i);
                row = sheet.createRow(i + 1);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {//列
                    col = colList.get(j);
                    cell = row.createCell(j);
                    if (i % 2 == 0 && (j == 1 || j == 2 || j == 3 || j == 9)) {//单行
                        cell.setCellStyle(odd_left_Style);
                    } else if (i % 2 == 0 && !(j == 1 || j == 2 || j == 3 || j == 9)) {
                        cell.setCellStyle(oddStyle);
                    } else if (i % 2 != 0 && (j == 1 || j == 2 || j == 3 || j == 9)) {
                        cell.setCellStyle(even_left_Style);
                    } else if (i % 2 != 0 && !(j == 1 || j == 2 || j == 3 || j == 9)) {
                        cell.setCellStyle(evenStyle);
                    }
                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);// 指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(
                    response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void exportu_(HttpServletRequest request,
                         HttpServletResponse response, String name, String cols,
                         List<?> dataList) {
        if (name == null || cols == null || dataList == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(
                    cols.replaceAll("&quot;", "\""),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);
            HSSFCellStyle titleStyle = wb.createCellStyle();
            //titleStyle:左右居中、粗体
            titleStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);// 颜色
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);

            //无背景色
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle odd_left_Style = wb.createCellStyle();
            odd_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            odd_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle odd_right_Style = wb.createCellStyle();
            odd_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            odd_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            //背景填充
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle even_left_Style = wb.createCellStyle();
            even_left_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_left_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            even_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle even_right_Style = wb.createCellStyle();
            even_right_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_right_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            even_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            Map<String, Object> col, data;
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(col.get("title").toString());
                // int a=(int) (Integer.parseInt(col.get("width").toString()) *
                // 40);
                byte[] b = col.get("title").toString().getBytes("gbk");
                // 设置execl列宽为标题的2倍
                int d = b.length * 200;
                if (i == 2 || i == 3) {
                    d = 255 * 42;
                    sheet.setColumnWidth(i, d);
                } else if (i == 4 || i == 5 || i == 6) {
                    sheet.setColumnWidth(i, d * 4);
                } else {
                    sheet.setColumnWidth(i, d * 2);
                }

            }
            Object value;
            for (int i = 0; i < dataList.size(); i++) {//行
                data = (Map<String, Object>) dataList.get(i);
                row = sheet.createRow(i + 1);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {//列
                    col = colList.get(j);
                    cell = row.createCell(j);
                    if (i % 2 == 0) {//单行
                        if (j == 1 || j == 2 || j == 3 || j == 4) {//靠左
                            cell.setCellStyle(odd_left_Style);
                        } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 11) {//靠右
                            cell.setCellStyle(odd_right_Style);
                        } else {//居中
                            cell.setCellStyle(oddStyle);
                        }
                    } else {
                        if (j == 1 || j == 2 || j == 3 || j == 4) {//靠左
                            cell.setCellStyle(even_left_Style);
                        } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 11) {//靠右
                            cell.setCellStyle(even_right_Style);
                        } else {//居中
                            cell.setCellStyle(evenStyle);
                        }

                    }

                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);// 指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(
                    response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void exportu_voucher(HttpServletRequest request,
                                HttpServletResponse response, String name, String cols,
                                List<?> dataList) {
        if (name == null || cols == null || dataList == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(
                    cols.replaceAll("&quot;", "\""),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);
            HSSFCellStyle titleStyle = wb.createCellStyle();
            //titleStyle:左右居中、粗体
            titleStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);// 颜色
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);

            //无背景色
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle odd_left_Style = wb.createCellStyle();
            odd_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            odd_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle odd_right_Style = wb.createCellStyle();
            odd_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            odd_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            //背景填充
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle even_left_Style = wb.createCellStyle();
            even_left_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_left_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            even_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle even_right_Style = wb.createCellStyle();
            even_right_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_right_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            even_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            Map<String, Object> col, data;
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(col.get("title").toString());
                // int a=(int) (Integer.parseInt(col.get("width").toString()) *
                // 40);
                byte[] b = col.get("title").toString().getBytes("gbk");
                // 设置execl列宽为标题的2倍
                int d = b.length * 200;
                if (i == 1 || i == 4 || i == 5 || i == 6 || i == 7) {
                    sheet.setColumnWidth(i, d * 4);
                } else {
                    sheet.setColumnWidth(i, d * 2);
                }

            }
            Object value;
            for (int i = 0; i < dataList.size(); i++) {//行
                data = (Map<String, Object>) dataList.get(i);
                row = sheet.createRow(i + 1);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {//列
                    col = colList.get(j);
                    cell = row.createCell(j);
                    if (i % 2 == 0) {//单行
                        if (j == 5 || j == 2 || j == 3 || j == 4) {//靠左
                            cell.setCellStyle(odd_left_Style);
                        } else if (j == 9 || j == 6 || j == 7 || j == 8 || j == 12) {//靠右
                            cell.setCellStyle(odd_right_Style);
                        } else {//居中
                            cell.setCellStyle(oddStyle);
                        }
                    } else {
                        if (j == 5 || j == 2 || j == 3 || j == 4) {//靠左
                            cell.setCellStyle(even_left_Style);
                        } else if (j == 9 || j == 6 || j == 7 || j == 8 || j == 12) {//靠右
                            cell.setCellStyle(even_right_Style);
                        } else {//居中
                            cell.setCellStyle(evenStyle);
                        }

                    }

                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);// 指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(
                    response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void exportu_cardselect(HttpServletRequest request,
                                   HttpServletResponse response, String name, String cols,
                                   List<?> dataList) {
        if (name == null || cols == null || dataList == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(
                    cols.replaceAll("&quot;", "\""),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);
            HSSFCellStyle titleStyle = wb.createCellStyle();
            //titleStyle:左右居中、粗体
            titleStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);// 颜色
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);

            //无背景色
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle odd_left_Style = wb.createCellStyle();
            odd_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            odd_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle odd_right_Style = wb.createCellStyle();
            odd_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            odd_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            //背景填充
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle even_left_Style = wb.createCellStyle();
            even_left_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_left_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            even_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle even_right_Style = wb.createCellStyle();
            even_right_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_right_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            even_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            Map<String, Object> col, data;
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(col.get("title").toString());
                // int a=(int) (Integer.parseInt(col.get("width").toString()) *
                // 40);
                byte[] b = col.get("title").toString().getBytes("gbk");
                // 设置execl列宽为标题的2倍
                int d = b.length * 200;
                if (i >= 2 && i <= 6) {
                    sheet.setColumnWidth(i, d * 4);
                } else {
                    sheet.setColumnWidth(i, d * 2);
                }

            }
            Object value;
            for (int i = 0; i < dataList.size(); i++) {//行
                data = (Map<String, Object>) dataList.get(i);
                row = sheet.createRow(i + 1);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {//列
                    col = colList.get(j);
                    cell = row.createCell(j);
                    if (i % 2 == 0) {//单行
                        if (j == 1 || j == 2 || j == 3 || j == 4) {//靠左
                            cell.setCellStyle(odd_left_Style);
                        } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 11) {//靠右
                            cell.setCellStyle(odd_right_Style);
                        } else {//居中
                            cell.setCellStyle(oddStyle);
                        }
                    } else {
                        if (j == 1 || j == 2 || j == 3 || j == 4) {//靠左
                            cell.setCellStyle(even_left_Style);
                        } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 11) {//靠右
                            cell.setCellStyle(even_right_Style);
                        } else {//居中
                            cell.setCellStyle(evenStyle);
                        }

                    }

                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);// 指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(
                    response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void exportu_intangcard(HttpServletRequest request,
                                   HttpServletResponse response, String name, String cols,
                                   List<?> dataList) {
        if (name == null || cols == null || dataList == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(
                    cols.replaceAll("&quot;", "\""),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);
            HSSFCellStyle titleStyle = wb.createCellStyle();
            //titleStyle:左右居中、粗体
            titleStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);// 颜色
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);

            //无背景色
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle odd_left_Style = wb.createCellStyle();
            odd_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            odd_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle odd_right_Style = wb.createCellStyle();
            odd_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            odd_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            //背景填充
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle even_left_Style = wb.createCellStyle();
            even_left_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_left_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            even_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle even_right_Style = wb.createCellStyle();
            even_right_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_right_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            even_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            Map<String, Object> col, data;
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(col.get("title").toString());
                // int a=(int) (Integer.parseInt(col.get("width").toString()) *
                // 40);
                byte[] b = col.get("title").toString().getBytes("gbk");
                // 设置execl列宽为标题的2倍
                int d = b.length * 200;
                if (i == 2 || i == 3) {
                    d = 255 * 42;
                    sheet.setColumnWidth(i, d);
                } else if (i == 4 || i == 5 || i == 6) {
                    sheet.setColumnWidth(i, d * 4);
                } else if (i == 15) {
                    sheet.setColumnWidth(i, d * 8);
                } else {
                    sheet.setColumnWidth(i, d * 2);
                }

            }
            Object value;
            for (int i = 0; i < dataList.size(); i++) {//行
                data = (Map<String, Object>) dataList.get(i);
                row = sheet.createRow(i + 1);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {//列
                    col = colList.get(j);
                    cell = row.createCell(j);
                    if (i % 2 == 0) {//单行
                        if (j == 1 || j == 2 || j == 3 || j == 4 || j == 15) {//靠左
                            cell.setCellStyle(odd_left_Style);
                        } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 11) {//靠右
                            cell.setCellStyle(odd_right_Style);
                        } else {//居中
                            cell.setCellStyle(oddStyle);
                        }
                    } else {
                        if (j == 1 || j == 2 || j == 3 || j == 4 || j == 15) {//靠左
                            cell.setCellStyle(even_left_Style);
                        } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 11) {//靠右
                            cell.setCellStyle(even_right_Style);
                        } else {//居中
                            cell.setCellStyle(evenStyle);
                        }

                    }

                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);// 指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(
                    response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void exportu_intangvoucher(HttpServletRequest request,
                                      HttpServletResponse response, String name, String cols,
                                      List<?> dataList) {
        if (name == null || cols == null || dataList == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(
                    cols.replaceAll("&quot;", "\""),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);
            HSSFCellStyle titleStyle = wb.createCellStyle();
            //titleStyle:左右居中、粗体
            titleStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);// 颜色
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);

            //无背景色
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle odd_left_Style = wb.createCellStyle();
            odd_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            odd_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle odd_right_Style = wb.createCellStyle();
            odd_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            odd_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            //背景填充
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle even_left_Style = wb.createCellStyle();
            even_left_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_left_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            even_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle even_right_Style = wb.createCellStyle();
            even_right_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_right_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            even_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            Map<String, Object> col, data;
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(col.get("title").toString());
                // int a=(int) (Integer.parseInt(col.get("width").toString()) *
                // 40);
                byte[] b = col.get("title").toString().getBytes("gbk");
                // 设置execl列宽为标题的2倍
                int d = b.length * 200;
                if (i == 1 || i == 4 || i == 5 || i == 6 || i == 7) {
                    sheet.setColumnWidth(i, d * 4);
                } else if (i == 16) {
                    sheet.setColumnWidth(i, d * 8);
                } else {
                    sheet.setColumnWidth(i, d * 2);
                }

            }
            Object value;
            for (int i = 0; i < dataList.size(); i++) {//行
                data = (Map<String, Object>) dataList.get(i);
                row = sheet.createRow(i + 1);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {//列
                    col = colList.get(j);
                    cell = row.createCell(j);
                    if (i % 2 == 0) {//单行
                        if (j == 5 || j == 2 || j == 3 || j == 4 || j == 16) {//靠左
                            cell.setCellStyle(odd_left_Style);
                        } else if (j == 9 || j == 6 || j == 7 || j == 8 || j == 12) {//靠右
                            cell.setCellStyle(odd_right_Style);
                        } else {//居中
                            cell.setCellStyle(oddStyle);
                        }
                    } else {
                        if (j == 5 || j == 2 || j == 3 || j == 4 || j == 16) {//靠左
                            cell.setCellStyle(even_left_Style);
                        } else if (j == 9 || j == 6 || j == 7 || j == 8 || j == 12) {//靠右
                            cell.setCellStyle(even_right_Style);
                        } else {//居中
                            cell.setCellStyle(evenStyle);
                        }

                    }

                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);// 指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(
                    response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    public void exportu_intangcardselect(HttpServletRequest request,
                                         HttpServletResponse response, String name, String cols,
                                         List<?> dataList) {
        if (name == null || cols == null || dataList == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(
                    cols.replaceAll("&quot;", "\""),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);


            HSSFCellStyle titleStyle = wb.createCellStyle();
            //titleStyle:左右居中、粗体
            titleStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);// 颜色
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);

            //无背景色
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle odd_left_Style = wb.createCellStyle();
            odd_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            odd_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle odd_right_Style = wb.createCellStyle();
            odd_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            odd_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            odd_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            //背景填充
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            HSSFCellStyle even_left_Style = wb.createCellStyle();
            even_left_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_left_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            even_left_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_LEFT);

            HSSFCellStyle even_right_Style = wb.createCellStyle();
            even_right_Style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            even_right_Style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            even_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            even_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            even_right_Style.setVerticalAlignment(HSSFCellStyle.ALIGN_RIGHT);

            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            Map<String, Object> col, data;
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(col.get("title").toString());
                // int a=(int) (Integer.parseInt(col.get("width").toString()) *
                // 40);
                byte[] b = col.get("title").toString().getBytes("gbk");
                // 设置execl列宽为标题的2倍
                int d = b.length * 200;
                if (i >= 2 && i <= 6) {
                    sheet.setColumnWidth(i, d * 4);
                } else if (i == 15) {
                    sheet.setColumnWidth(i, d * 8);
                } else {
                    sheet.setColumnWidth(i, d * 2);
                }

            }
            Object value;
            for (int i = 0; i < dataList.size(); i++) {//行
                data = (Map<String, Object>) dataList.get(i);
                row = sheet.createRow(i + 1);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {//列
                    col = colList.get(j);
                    cell = row.createCell(j);
                    if (i % 2 == 0) {//单行
                        if (j == 1 || j == 2 || j == 3 || j == 4 || j == 15) {//靠左
                            cell.setCellStyle(odd_left_Style);
                        } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 11) {//靠右
                            cell.setCellStyle(odd_right_Style);
                        } else {//居中
                            cell.setCellStyle(oddStyle);
                        }
                    } else {
                        if (j == 1 || j == 2 || j == 3 || j == 4 || j == 15) {//靠左
                            cell.setCellStyle(even_left_Style);
                        } else if (j == 5 || j == 6 || j == 7 || j == 8 || j == 11) {//靠右
                            cell.setCellStyle(even_right_Style);
                        } else {//居中
                            cell.setCellStyle(evenStyle);
                        }

                    }

                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);// 指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(
                    response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 导出特定格式Excel工具类
     *
     * @param request
     * @param response
     * @param name
     * @param cols
     * @param datas
     */
    @SuppressWarnings("unchecked")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String riskName, String cols, List<?> datas) {
        if (name == null || cols == null || datas == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(cols.replaceAll("&quot;", "\""), new TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> nowList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> map : colList) {
                if (map != null) {
                    nowList.add(map);
                }
            }
            colList = nowList;
            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);
            HSSFCellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            Map<String, Object> col, data;
            HSSFRow row = sheet.createRow(0);//第一行
            HSSFRow rowTwo = sheet.createRow(1);//第二行
            row.setHeight((short) 300);
            rowTwo.setHeight((short) 300);
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                if (i == 0 || i == 1) {
                    if (i == 0) {
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
                        cell = row.createCell(i);
                        cell.setCellStyle(titleStyle);
                        cell.setCellValue(riskName);
                    }
                    cell = rowTwo.createCell(i);
                    cell.setCellStyle(titleStyle);
                    cell.setCellValue(col.get("title").toString());
                } else {
                    sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
                    cell = row.createCell(i);
                    cell.setCellStyle(titleStyle);
                    cell.setCellValue(col.get("title").toString());
                }
                //sheet.setColumnWidth(i, (int) (Integer.parseInt(col.get("width").toString()) * 40));
                sheet.setColumnWidth(i, (int) (Integer.parseInt(col.get("width").toString().replace("%", "")) * 400));

            }
            for (int i = 2; i < colList.size(); i++) {
                cell = rowTwo.createCell(i);
                cell.setCellStyle(titleStyle);
            }

            Object value;
            for (int i = 0; i < datas.size(); i++) {
                data = (Map<String, Object>) datas.get(i);
                row = sheet.createRow(i + 2);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {
                    col = colList.get(j);
                    cell = row.createCell(j);
                    cell.setCellStyle(i % 2 == 0 ? oddStyle : evenStyle);
                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);//指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导出excel工具类
     *
     * @param request
     * @param response
     * @param name
     * @param cols
     * @param datas
     */
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String cols, String datas) {
        if (name == null || cols == null || datas == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(cols.replaceAll("&quot;", "\""), new TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> dataList = new ObjectMapper().readValue(datas.replaceAll("&quot;", "\""), new TypeReference<List<Map<String, Object>>>() {
            });
            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);
            HSSFCellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFillForegroundColor(HSSFColor.GREY_80_PERCENT.index);
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);


            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            Map<String, Object> col, data;
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(col.get("title").toString());
                //   	int a=(int) (Integer.parseInt(col.get("width").toString()) * 40);
                byte[] b = col.get("title").toString().getBytes("gbk");
                //设置execl列宽为标题的2倍
                int d = b.length * 256 * 2;
                if (b.length >= 255) {
                    d = 255 * 256;
                }
                sheet.setColumnWidth(i, d);
            }
            Object value;
            for (int i = 0; i < dataList.size(); i++) {
                data = dataList.get(i);
                row = sheet.createRow(i + 1);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {
                    col = colList.get(j);
                    cell = row.createCell(j);
                    cell.setCellStyle(i % 2 == 0 ? oddStyle : evenStyle);
                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);//指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            //out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导出excel工具类 更改表头颜色，表头数据居中
     *
     * @param request
     * @param response
     * @param name
     * @param cols
     * @param datas
     */
    public void export2(HttpServletRequest request, HttpServletResponse response, String name, String cols, String datas) {
        if (name == null || cols == null || datas == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {
            cols = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(cols).replaceAll("");
            List<Map<String, Object>> colList = new ObjectMapper().readValue(cols.replaceAll("&quot;", "\""), new TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> dataList = new ObjectMapper().readValue(datas.replaceAll("&quot;", "\""), new TypeReference<List<Map<String, Object>>>() {
            });
            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);
            HSSFCellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);//颜色
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColor.WHITE.index);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titleStyle.setFont(font);
            HSSFCellStyle oddStyle = wb.createCellStyle();
            oddStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            oddStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            oddStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFCellStyle evenStyle = wb.createCellStyle();
            evenStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            evenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            evenStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            evenStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            evenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);


            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            Map<String, Object> col, data;
            HSSFCell cell;
            for (int i = 0; i < colList.size(); i++) {
                col = colList.get(i);
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(col.get("title").toString());
                //   	int a=(int) (Integer.parseInt(col.get("width").toString()) * 40);
                byte[] b = col.get("title").toString().getBytes("gbk");
                //设置execl列宽为标题的2倍
                int d = b.length * 256 * 2;
                if (b.length >= 255) {
                    d = 255 * 256;
                }
                sheet.setColumnWidth(i, d);
            }
            Object value;
            for (int i = 0; i < dataList.size(); i++) {
                data = dataList.get(i);
                row = sheet.createRow(i + 1);
                row.setHeight((short) 320);
                for (int j = 0; j < colList.size(); j++) {
                    col = colList.get(j);
                    cell = row.createCell(j);
                    cell.setCellStyle(i % 2 == 0 ? oddStyle : evenStyle);
                    value = data.get(col.get("field"));
                    if (value != null) {
                        if (value instanceof Double) {
                            cell.setCellValue(((Double) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell.setCellValue(((Integer) value).intValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            String fileName = name + ".xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);//指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void exportFourReport(HttpServletRequest request, HttpServletResponse response, List<ReportDataDTO> dtos, String[] account, String path,String JJreportName,String iSMerge) {
        try {

            String modelPath = path;
            File file = null;
            String fileName = "";
            int[][] yearCell2 = {{4, 2},  {3, 2}, {4, 1}};
            int[][] comCell2 = {{4, 0},  {3, 0}, {4, 0}};
            Integer mergeColIndex = null;

            file = new File(modelPath + "reportAccountA_1_V1.xls");
            mergeColIndex = 1;

            fileName = dtos.get(0).getYearMonthDate() + "会计月度_" + "报表.xls";

            FileInputStream fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);
            //遍历所有报表挨个塞数据

            for (int d = 0; d < dtos.size(); d++) {
                ReportDataDTO dto = dtos.get(d);
                //获取结果表数据
                List<ReportData> datas = dto.getDataList();
                String sheetName = dto.getSheetName();//获取sheet名
                int rownum = dto.getRowNum();//获取开始行号
                int cellnum = dto.getCellNum();//获取一共塞多少列的数
                Sheet sheet = wb.getSheet(sheetName);
                if (JJreportName != null && !JJreportName.equals("") && !dto.getReportCode().equals(JJreportName)){
                    wb.setSheetHidden(wb.getSheetIndex(sheetName), 1);
                }
                //定义sheet里面的行
                Row row;
                //定义sheet里面的列
                Cell cell;
                Object value;
                if(  mergeColIndex==d ){
                    //表示是利润表
                    for (int i = 0; i < datas.size(); i++) {
                        row = sheet.getRow(i + rownum);
                        ReportData data = datas.get(i);
                        for (int m = 0; m < cellnum; m++) {
                            if (m==1 || m==3 || m==5) {
                                continue;
                            }
                            cell = row.getCell(m);
                            int n = m;
                            if (m==2) {
                                n--;
                            }
                            if (m==4) {
                                n-=2;
                            }
                            String cellData = getReportData(n + 1, data);
                            if (cellData != null && !"".equals(cellData))
                                cell.setCellValue(cellData);
                        }
                    }
                } else {
                    for (int i = 0; i < datas.size(); i++) {
                        row = sheet.getRow(i + rownum);
                        ReportData data = datas.get(i);
                        for (int m = 0; m < cellnum; m++) {
                            cell = row.getCell(m);
                            String cellData = getReportData(m + 1, data);
                            if (cellData != null && !"".equals(cellData))
                                cell.setCellValue(cellData);
                        }
                    }
                }
                //设置报表时间
                Cell c;
                Cell u;
                Cell createUser ;
                Cell comName ;
                String yearMonthDate = dto.getYearMonthDate();
                String unit = dto.getUnit();

                c = sheet.getRow(yearCell2[d][0]).getCell(yearCell2[d][1]);
                c.setCellValue(yearMonthDate.substring(0,4)+"年"+yearMonthDate.substring(4,6)+"月");
                u = sheet.getRow(yearCell2[d][0]).getCell(yearCell2[d][1]+2);
                if(dtos.get(d).getUnit().equals("1")){
                    u.setCellValue("单位：元");
                }else{
                    u.setCellValue("单位：万元");
                }
                createUser = sheet.getRow(rownum+datas.size()).getCell(yearCell2[d][1]+2);
                createUser.setCellValue("制表人：" + CurrentUser.getCurrentUser().getUserName());
                comName = sheet.getRow(comCell2[d][0]).getCell(comCell2[d][1]);
                comName.setCellValue("报表单位：" + CurrentUser.getCurrentManageBranchName());


            }

            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);//指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getReportData(int m, ReportData data) {
        switch (m) {
            case 1:
                return data.getD1();
            case 2:
                return data.getD2();
            case 3:
                return data.getD3();
            case 4:
                return data.getD4();
            case 5:
                return data.getD5();
            case 6:
                return data.getD6();
            case 7:
                return data.getD7();
            case 8:
                return data.getD8();
            case 9:
                return data.getD9();
            case 10:
                return data.getD10();
            case 11:
                return data.getD11();
            case 12:
                return data.getD12();
            case 13:
                return data.getD13();
            case 14:
                return data.getD14();
            case 15:
                return data.getD15();
            case 16:
                return data.getD16();
            case 17:
                return data.getD17();
            case 18:
                return data.getD18();
            case 19:
                return data.getD19();
            case 20:
                return data.getD20();
        }
        return null;
    }

    /**
     * 根据会计期间得到该月最后一天
     *
     * @param yearMonthDate
     * @param JJreportType
     * @return
     */
    public String getLastDate(String yearMonthDate, String JJreportType) {
        String year = yearMonthDate.substring(0, 4);
        String month = yearMonthDate.substring(4);
        if ("1".equals(JJreportType)) {
            switch (month) {
                case "01":
                    return year + "年" + month + "月" + "31日";
                case "03":
                    return year + "年" + month + "月" + "31日";
                case "05":
                    return year + "年" + month + "月" + "31日";
                case "07":
                    return year + "年" + month + "月" + "31日";
                case "08":
                    return year + "年" + month + "月" + "31日";
                case "10":
                    return year + "年" + month + "月" + "31日";
                case "12":
                    return year + "年" + month + "月" + "31日";
                case "04":
                    return year + "年" + month + "月" + "30日";
                case "06":
                    return year + "年" + month + "月" + "30日";
                case "09":
                    return year + "年" + month + "月" + "30日";
                case "11":
                    return year + "年" + month + "月" + "30日";
            }
            if ("02".equals(month)) {
                if ((Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 400 == 0) || (Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0)) {
                    //闰年
                    return year + "年" + month + "月" + "29日";
                } else {
                    return year + "年" + month + "月" + "28日";
                }
            }
        } else if ("2".equals(JJreportType)) {
            return year + "年" + month + "月";
        }
        return "";
    }

    /**
     * 科目总账查询结果信息导出
     *
     * @param request
     * @param response
     * @param name
     * @param result
     */
    public void exportAccSum(HttpServletRequest request, HttpServletResponse response, String name, List<?> result) {
        if (name == null || result == null) {
            return;
        }
        BufferedOutputStream out = null;
        try {

            @SuppressWarnings("resource")
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(name);

            for (int i = 0; i < 6; i++) {
                sheet.setColumnWidth(i, 6000); //第一个参数代表列id(从0开始),第2个参数代表宽度值
            }

            HSSFFont titlefont = wb.createFont();
            titlefont.setFontName("楷体_GB2312");
            titlefont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            titlefont.setFontHeightInPoints((short) 20);

            HSSFFont font = wb.createFont();
            font.setFontName("楷体_GB2312");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            font.setFontHeightInPoints((short) 14);
            //设置标题格式
            HSSFCellStyle titleStyle = wb.createCellStyle();
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//左右居中
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            titleStyle.setFont(titlefont);
            //设置标题下的科目格式
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//左右居中
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            style.setFont(font);
            //设置单元格边框

            HSSFCellStyle cellStyle_center = wb.createCellStyle();
            cellStyle_center.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格左右居中;
            cellStyle_center.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle_center.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle_center.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle_center.setBorderTop(HSSFCellStyle.BORDER_THIN);


            HSSFCellStyle cellStyle_right = wb.createCellStyle();
            cellStyle_right.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格靠右
            cellStyle_right.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle_right.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle_right.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle_right.setBorderTop(HSSFCellStyle.BORDER_THIN);

            HSSFCellStyle c1 = wb.createCellStyle();
            c1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            HSSFCellStyle c2 = wb.createCellStyle();
            c2.setAlignment(HSSFCellStyle.ALIGN_RIGHT);


            //.....各种设置格式

            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 320);
            List<Map<String, Object>> data;
            HSSFCell cell;

            //记录行号
            int rownum = 1;
            //获取第一层：科目列表
            for (int i = 0; i < result.size(); i++) {
                List<Object> data1 = (List<Object>) result.get(i);

                //获取科目完整代码
                String subjectCode = data1.get(0).toString();
                //获取一级科目名称
                String superName = data1.get(1).toString();
                //获取科目名称
                String subjectName = data1.get(2).toString();
                //获取核算单位
                String centerCode = data1.get(3).toString();
                //获取行数据信息
                List<Map<String, Object>> data2 = (List<Map<String, Object>>) data1.get(4);

                row = sheet.createRow(rownum);
                row.setHeightInPoints(30);
                cell = row.createCell(0);
                cell.setCellValue(CurrentUser.getCurrentManageBranchName());
                // 合并日期占两行(4个参数，分别为起始行，结束行，起始列，结束列)
                // 行和列都是从0开始计数，且起始结束都会合并
                // 这里是合并excel中日期的两行为一行
                CellRangeAddress region = new CellRangeAddress(rownum, rownum, 0, 5);
                sheet.addMergedRegion(region);
                cell.setCellStyle(titleStyle);

                rownum += 1;

                row = sheet.createRow(rownum);
                cell = row.createCell(0);
                cell.setCellValue(superName + "总账");
                CellRangeAddress region1 = new CellRangeAddress(rownum, rownum, 0, 5);
                sheet.addMergedRegion(region1);
                cell.setCellStyle(style);
                rownum += 1;

                row = sheet.createRow(rownum);
                cell = row.createCell(0);
                cell.setCellValue("科目：" + subjectName + "(" + subjectCode + ")");
                CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 0, 5);
                sheet.addMergedRegion(region2);
                cell.setCellStyle(style);
                rownum += 2;

                row = sheet.createRow(rownum);
                CellRangeAddress region3 = new CellRangeAddress(rownum, rownum, 0, 1);
                sheet.addMergedRegion(region3);
                CellRangeAddress region4 = new CellRangeAddress(rownum, rownum, 2, 3);
                sheet.addMergedRegion(region4);
                cell = row.createCell(0);
                cell.setCellValue("核算单位：行政");
                cell = row.createCell(2);
                cell.setCellStyle(c1);
                cell.setCellValue("区间：自" + data2.get(1).get("yearMonthDate") + "-" + data2.get(data2.size() - 1).get("yearMonthDate"));
                cell = row.createCell(4);
                cell.setCellValue("单位：人民币元");
                cell = row.createCell(5);
                cell.setCellStyle(c2);
                cell.setCellValue("页码：" + (i + 1) + "/" + result.size());
                rownum += 1;

                row = sheet.createRow(rownum);
                cell = row.createCell(0);
                cell.setCellValue("月份");
                cell.setCellStyle(cellStyle_center);
                cell = row.createCell(1);
                cell.setCellStyle(cellStyle_center);
                cell.setCellValue("摘要");
                cell = row.createCell(2);
                cell.setCellStyle(cellStyle_center);
                cell.setCellValue("借方发生额");
                cell = row.createCell(3);
                cell.setCellStyle(cellStyle_center);
                cell.setCellValue("贷方发生额");
                cell = row.createCell(4);
                cell.setCellStyle(cellStyle_center);
                cell.setCellValue("方向");
                cell = row.createCell(5);
                cell.setCellStyle(cellStyle_center);
                cell.setCellValue("余额");

                rownum += 1;


                //获取第二层：行数据信息（按会计期间分）
                for (int j = 0; j < data2.size(); j++) {

                    row = sheet.createRow(rownum);
                    cell = row.createCell(0);
                    cell.setCellStyle(cellStyle_center);
                    cell.setCellValue(data2.get(j).get("yearMonthDate").toString());//会计期间
                    cell = row.createCell(1);
                    cell.setCellStyle(cellStyle_center);
                    cell.setCellValue(data2.get(j).get("remarkName").toString());//余额
                    cell = row.createCell(2);
                    cell.setCellStyle(cellStyle_right);
                    cell.setCellValue(data2.get(j).get("debitDest").toString());//借方
                    cell = row.createCell(3);
                    cell.setCellStyle(cellStyle_right);
                    cell.setCellValue(data2.get(j).get("creditDest").toString());//贷方
                    cell = row.createCell(4);
                    cell.setCellStyle(cellStyle_center);
                    cell.setCellValue(data2.get(j).get("balanceFX").toString());//余额方向
                    cell = row.createCell(5);
                    cell.setCellStyle(cellStyle_right);
                    cell.setCellValue(data2.get(j).get("balanceDest").toString());//余额


                    rownum++;
                }
                rownum++;
                row = sheet.createRow(rownum);
                CellRangeAddress region5 = new CellRangeAddress(rownum, rownum, 0, 1);
                sheet.addMergedRegion(region5);
                CellRangeAddress region6 = new CellRangeAddress(rownum, rownum, 2, 3);
                sheet.addMergedRegion(region6);
                CellRangeAddress region7 = new CellRangeAddress(rownum, rownum, 4, 5);
                sheet.addMergedRegion(region7);
                cell = row.createCell(0);
                cell.setCellValue("财务主管：");
                cell = row.createCell(2);
                cell.setCellValue("操作员：" + CurrentUser.getCurrentUser().getUserName());
                cell = row.createCell(4);
                cell.setCellValue("打印日期：" + CurrentTime.getCurrentDate());
                rownum += 2;

            }
            String filename = name + ".xls";
            filename = new String(filename.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);//指定下载的文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no--cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    //现金流量明细表导出
    public void exportCashFlow(HttpServletRequest request, HttpServletResponse response, List<?> result, String Date1, String Date2, String path) {
        try {
            String modelPath = path;
            File file = new File(modelPath + "CashFlowTable_1.xls");
            String fileName = Date1 + "-" + Date2 + "现金流量明细表.xls";
            FileInputStream fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);
            Sheet sheet = wb.getSheet("现金流量明细表");
            Row row;
            Cell cell;

            cell = sheet.getRow(0).getCell(0);//核算单位
            cell.setCellValue(CurrentUser.getCurrentManageBranchName());
            cell = sheet.getRow(2).getCell(0);
            Map<String, Object> m = (Map<String, Object>) result.get(0);
            String centerCode = m.get("centerCode").toString();
            cell.setCellValue("核算单位：" + centerCode);

            cell = sheet.getRow(2).getCell(2);
            cell.setCellValue("期间：自" + Date1 + "至" + Date2);
            int rownum = 4;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 10);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格左右居中;
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);

            CellStyle cell_left_Style = wb.createCellStyle();
            cell_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格左右居中;
            cell_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setFont(font);

            CellStyle cell_right_Style = wb.createCellStyle();
            cell_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格左右居中;
            cell_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setFont(font);


            for (int i = 0; i < result.size(); i++) {
                row = sheet.createRow(rownum);
                Map<String, String> map = (Map<String, String>) result.get(i);
                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(map.get("voucherDate"));

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(map.get("voucherNo"));

                cell = row.createCell(2);
                cell.setCellStyle(cell_left_Style);
                cell.setCellValue(map.get("specialName"));

                cell = row.createCell(3);
                cell.setCellStyle(cell_left_Style);
                cell.setCellValue(map.get("remark"));

                cell = row.createCell(4);
                cell.setCellStyle(cell_right_Style);
                cell.setCellValue(map.get("debit"));

                cell = row.createCell(5);
                cell.setCellStyle(cell_right_Style);
                cell.setCellValue(map.get("credit"));
                rownum++;
            }
            rownum++;
            row = sheet.createRow(rownum);
            CellRangeAddress region = new CellRangeAddress(rownum, rownum, 0, 1);
            sheet.addMergedRegion(region);
            CellRangeAddress region1 = new CellRangeAddress(rownum, rownum, 2, 3);
            sheet.addMergedRegion(region1);
            CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 4, 5);
            sheet.addMergedRegion(region2);
            cell = row.createCell(0);
            cell.setCellValue("财务主管：");
            cell = row.createCell(2);
            cell.setCellValue("操作员：" + CurrentUser.getCurrentUser().getUserName());
            cell = row.createCell(4);
            cell.setCellValue("打印日期：" + CurrentTime.getCurrentDate());


            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //明细账查询导出
    public void exportDetialAccount(HttpServletRequest request, HttpServletResponse response, List<?> result, String Date1, String Date2, String path) {
        try {
            String modelPath = path;
            File file = new File(modelPath + "DetailAccount_1.xls");
            String fileName = "明细账查询信息表.xls";
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);

            Sheet sheet = wb.getSheet("明细账查询信息表");
            Row row;
            Cell cell;
            int rownum = 4;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 10);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格左右居中;
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);

            CellStyle cell_right_Style = wb.createCellStyle();
            cell_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格左右居右;
            cell_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setFont(font);

            CellStyle cell_left_Style = wb.createCellStyle();
            cell_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格左右居左;
            cell_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setFont(font);

            cell = sheet.getRow(0).getCell(0);//核算单位
            cell.setCellValue(CurrentUser.getCurrentManageBranchName());

            Map<String, Object> m = (Map<String, Object>) result.get(0);
            String centerCode = m.get("centerCode").toString();
            cell = sheet.getRow(2).getCell(0);//核算单位
            cell.setCellValue("核算单位：" + centerCode);
            cell = sheet.getRow(2).getCell(3);
            cell.setCellValue("期间：自" + Date1 + "--" + Date2);

            for (int i = 0; i < result.size(); i++) {
                row = sheet.createRow(rownum);
                Map<String, Object> map = (Map<String, Object>) result.get(i);

                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                if (map.get("voucherDate") != null) {
                    cell.setCellValue(map.get("voucherDate").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                if (map.get("voucherNo") != null) {
                    cell.setCellValue(map.get("voucherNo").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(2);
                cell.setCellStyle(cell_left_Style);

                if (map.get("directionIdx") != null) {
                    cell.setCellValue(map.get("directionIdx").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(3);
                cell.setCellStyle(cell_left_Style);
                if (map.get("directionIdxName") != null) {
                    cell.setCellValue(map.get("directionIdxName").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(4);
                cell.setCellStyle(cell_left_Style);
                if (map.get("remarkName") != null) {
                    cell.setCellValue(map.get("remarkName").toString());
                } else {
                    cell.setCellValue("");
                }
                cell = row.createCell(5);
                if (i > 0) {
                    cell.setCellStyle(cell_right_Style);
                } else {
                    cell.setCellStyle(cellStyle);
                }
                if (map.get("debitDest") != null) {
                    cell.setCellValue(map.get("debitDest").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(6);
                if (i > 0) {
                    cell.setCellStyle(cell_right_Style);
                } else {
                    cell.setCellStyle(cellStyle);
                }
                if (map.get("creditDest") != null) {
                    cell.setCellValue(map.get("creditDest").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(7);
                cell.setCellStyle(cellStyle);
                if (map.get("balanceFX") != null) {
                    cell.setCellValue(map.get("balanceFX").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(8);
                cell.setCellStyle(cell_right_Style);

                if (map.get("balanceDest") != null) {
                    cell.setCellValue(map.get("balanceDest").toString());
                } else {
                    cell.setCellValue("");
                }

                rownum++;
            }
            rownum++;
            row = sheet.createRow(rownum);
            CellRangeAddress region = new CellRangeAddress(rownum, rownum, 0, 2);
            sheet.addMergedRegion(region);
            CellRangeAddress region1 = new CellRangeAddress(rownum, rownum, 3, 4);
            sheet.addMergedRegion(region1);
            CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 5, 8);
            sheet.addMergedRegion(region2);
            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("财务主管：");
            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("操作员：" + CurrentUser.getCurrentUser().getUserName());
            cell = row.createCell(4);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("打印日期：" + CurrentTime.getCurrentDate());
            cell = row.createCell(6);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);

            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //联查明细账导出
    public void exportDatadetailaccount(HttpServletRequest request, HttpServletResponse response, List<?> result, String beginDate, String endDate, String directionIdx, String detaiItemName, String dateText, String path) {
        try {
            String modelPath = path;
            File file = new File(modelPath + "Datadetailaccount_1.xls");
            String fileName = "联查明细账查询.xls";
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);

            Sheet sheet = wb.getSheet("联查明细账");
            Row row;
            Cell cell;
            int rownum = 4;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 10);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格左右居中;
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);

            CellStyle cell_right_Style = wb.createCellStyle();
            cell_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格左右居右;
            cell_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setFont(font);

            CellStyle cell_left_Style = wb.createCellStyle();
            cell_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格左右居左;
            cell_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setFont(font);

            //Map<String, Object> m = (Map<String, Object>) result.get(0);
            cell = sheet.getRow(2).getCell(0);//核算单位
            cell.setCellValue("科目：" + directionIdx + "(" + detaiItemName + ")");
            cell = sheet.getRow(2).getCell(5);
            cell.setCellValue("日期：" + dateText);

            for (int i = 0; i < result.size(); i++) {
                row = sheet.createRow(rownum);
                Map<String, Object> map = (Map<String, Object>) result.get(i);

                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                if (map.get("voucherDate") != null) {
                    cell.setCellValue(map.get("voucherDate").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                if (map.get("voucherNo") != null) {
                    cell.setCellValue(map.get("voucherNo").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(2);
                cell.setCellStyle(cell_left_Style);

                if (map.get("remark") != null) {
                    cell.setCellValue(map.get("remark").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(3);
                cell.setCellStyle(cell_left_Style);
                if (map.get("unitPrice") != null) {
                    cell.setCellValue(map.get("unitPrice").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(4);
                cell.setCellStyle(cell_left_Style);
                if (map.get("amount") != null) {
                    cell.setCellValue(map.get("amount").toString());
                } else {
                    cell.setCellValue("");
                }
                cell = row.createCell(5);
                if (i > 0) {
                    cell.setCellStyle(cell_right_Style);
                } else {
                    cell.setCellStyle(cellStyle);
                }
                if (map.get("debitDest") != null) {
                    cell.setCellValue(map.get("debitDest").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(6);
                if (i > 0) {
                    cell.setCellStyle(cell_right_Style);
                } else {
                    cell.setCellStyle(cellStyle);
                }
                if (map.get("creditDest") != null) {
                    cell.setCellValue(map.get("creditDest").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(7);
                cell.setCellStyle(cellStyle);
                if (map.get("balanceFX") != null) {
                    cell.setCellValue(map.get("balanceFX").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(8);
                cell.setCellStyle(cell_right_Style);

                if (map.get("balanceDest") != null) {
                    cell.setCellValue(map.get("balanceDest").toString());
                } else {
                    cell.setCellValue("");
                }
                cell = row.createCell(9);
                cell.setCellStyle(cell_right_Style);

                if (map.get("flag") != null) {
                    cell.setCellValue(map.get("flag").toString());
                } else {
                    cell.setCellValue("");
                }
                rownum++;
            }
            rownum++;
            row = sheet.createRow(rownum);
            CellRangeAddress region = new CellRangeAddress(rownum, rownum, 0, 2);
            sheet.addMergedRegion(region);
            CellRangeAddress region1 = new CellRangeAddress(rownum, rownum, 3, 5);
            sheet.addMergedRegion(region1);
            CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 6, 9);
            sheet.addMergedRegion(region2);
            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("财务主管：");
            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("操作员：" + CurrentUser.getCurrentUser().getUserName());
            cell = row.createCell(4);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("打印日期：" + CurrentTime.getCurrentDate());
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //联查辅助明细账导出
    public void exportDatadetailaccountfz(HttpServletRequest request, HttpServletResponse response, List<?> result, String itemName, String dateText, String otherName, String directionOtherName, String directionOthers, String directionIdx, String path) {
        try {
            String modelPath = path;
            File file = new File(modelPath + "DatadetailaccountFZ_1.xls");
            String fileName = "联查辅助明细账查询.xls";
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);

            Sheet sheet = wb.getSheet("联查辅助明细账");
            Row row;
            Cell cell;
            int rownum = 4;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 10);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格左右居中;
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);

            CellStyle cell_right_Style = wb.createCellStyle();
            cell_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格左右居右;
            cell_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setFont(font);

            CellStyle cell_left_Style = wb.createCellStyle();
            cell_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格左右居左;
            cell_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setFont(font);

            //Map<String, Object> m = (Map<String, Object>) result.get(0);
            cell = sheet.getRow(2).getCell(0);//核算单位
            cell.setCellValue("科目：" + itemName + "(" + directionIdx + ")");
            cell = sheet.getRow(2).getCell(3);
            cell.setCellValue("日期：" + dateText);
            cell = sheet.getRow(2).getCell(7);
            cell.setCellValue(otherName + "：" + directionOtherName + "(" + directionOthers + ")");
            for (int i = 0; i < result.size(); i++) {
                row = sheet.createRow(rownum);
                Map<String, Object> map = (Map<String, Object>) result.get(i);

                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                if (map.get("voucherDate") != null) {
                    cell.setCellValue(map.get("voucherDate").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                if (map.get("voucherNo") != null) {
                    cell.setCellValue(map.get("voucherNo").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(2);
                cell.setCellStyle(cell_left_Style);

                if (map.get("remark") != null) {
                    cell.setCellValue(map.get("remark").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(3);
                cell.setCellStyle(cell_left_Style);
                if (map.get("unitPrice") != null) {
                    cell.setCellValue(map.get("unitPrice").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(4);
                cell.setCellStyle(cell_left_Style);
                if (map.get("amount") != null) {
                    cell.setCellValue(map.get("amount").toString());
                } else {
                    cell.setCellValue("");
                }
                cell = row.createCell(5);
                if (i > 0) {
                    cell.setCellStyle(cell_right_Style);
                } else {
                    cell.setCellStyle(cellStyle);
                }
                if (map.get("debitDest") != null) {
                    cell.setCellValue(map.get("debitDest").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(6);
                if (i > 0) {
                    cell.setCellStyle(cell_right_Style);
                } else {
                    cell.setCellStyle(cellStyle);
                }
                if (map.get("creditDest") != null) {
                    cell.setCellValue(map.get("creditDest").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(7);
                cell.setCellStyle(cellStyle);
                if (map.get("balanceFX") != null) {
                    cell.setCellValue(map.get("balanceFX").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(8);
                cell.setCellStyle(cell_right_Style);

                if (map.get("balanceDest") != null) {
                    cell.setCellValue(map.get("balanceDest").toString());
                } else {
                    cell.setCellValue("");
                }
                cell = row.createCell(9);
                cell.setCellStyle(cell_right_Style);

                if (map.get("flag") != null) {
                    cell.setCellValue(map.get("flag").toString());
                } else {
                    cell.setCellValue("");
                }
                rownum++;
            }
            rownum++;
            row = sheet.createRow(rownum);
            CellRangeAddress region = new CellRangeAddress(rownum, rownum, 0, 2);
            sheet.addMergedRegion(region);
            CellRangeAddress region1 = new CellRangeAddress(rownum, rownum, 3, 5);
            sheet.addMergedRegion(region1);
            CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 6, 9);
            sheet.addMergedRegion(region2);
            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("财务主管：");
            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("操作员：" + CurrentUser.getCurrentUser().getUserName());
            cell = row.createCell(4);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("打印日期：" + CurrentTime.getCurrentDate());
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ;

    //科目余额查询的导出
    public void exportAccountDetail(HttpServletRequest request, HttpServletResponse response, List<?> result, String Date1, String Date2, String cumulativeAmount, String path) {

        try {
            String modelPath = path;
            File file = null;
            String fileName = "";
            if (cumulativeAmount.equals("0")) {
                file = new File(modelPath + "AccountBalance_1.xls");
                fileName = "科目余额查询.xls";
            } else {
                file = new File(modelPath + "AccountBalanceSum_1.xls");
                fileName = "科目余额查询_累计.xls";
            }

            FileInputStream fis = null;
            fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);
            Sheet sheet = null;
            if (cumulativeAmount.equals("0")) {
                sheet = wb.getSheet("科目余额查询");
            } else {
                sheet = wb.getSheet("科目余额查询_累计");
            }

            Row row;
            Cell cell;
            int rownum = 5;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 10);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格左右居左;
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);

            CellStyle cell_right_Style = wb.createCellStyle();
            cell_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格左右居右;
            cell_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setFont(font);

            cell = sheet.getRow(0).getCell(0);//核算单位
            cell.setCellValue(CurrentUser.getCurrentManageBranchName());
            Map<String, Object> m = (Map<String, Object>) result.get(0);
            String centerCode = m.get("centerCode").toString();
            cell = sheet.getRow(2).getCell(0);//核算单位
            cell.setCellValue("核算单位：" + centerCode);
            cell = sheet.getRow(2).getCell(2);
            cell.setCellValue("期间：自" + Date1 + "--" + Date2);

            for (int i = 0; i < result.size(); i++) {
                row = sheet.createRow(rownum);
                Map<String, Object> map = (Map<String, Object>) result.get(i);

                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                if (map.get("itemCode") != null) {
                    cell.setCellValue(map.get("itemCode").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                if (map.get("itemName") != null) {
                    String itemName = map.get("itemName").toString();
                    if (itemName.contains("&nbsp;")) {
                        itemName = itemName.replaceAll("&nbsp;", " ");
                    }
                    cell.setCellValue(itemName);
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(2);
                cell.setCellStyle(cell_right_Style);
                cell.setCellValue(map.get("debitDest_Qc").toString());


                cell = row.createCell(3);
                cell.setCellStyle(cell_right_Style);
                cell.setCellValue(map.get("creditDest_Qc").toString());


                cell = row.createCell(4);
                cell.setCellStyle(cell_right_Style);
                cell.setCellValue(map.get("debitDest_Bq").toString());

                cell = row.createCell(5);
                cell.setCellStyle(cell_right_Style);
                cell.setCellValue(map.get("creditDest_Bq").toString());

                if (cumulativeAmount.equals("0")) {
                    cell = row.createCell(6);
                    cell.setCellStyle(cell_right_Style);
                    cell.setCellValue(map.get("debitDest_Qm").toString());

                    cell = row.createCell(7);
                    cell.setCellStyle(cell_right_Style);
                    cell.setCellValue(map.get("creditDest_Qm").toString());
                } else {
                    cell = row.createCell(6);
                    cell.setCellStyle(cell_right_Style);
                    cell.setCellValue(map.get("debitDest_Bn").toString());

                    cell = row.createCell(7);
                    cell.setCellStyle(cell_right_Style);
                    cell.setCellValue(map.get("creditDest_Bn").toString());

                    cell = row.createCell(8);
                    cell.setCellStyle(cell_right_Style);
                    cell.setCellValue(map.get("debitDest_Qm").toString());

                    cell = row.createCell(9);
                    cell.setCellStyle(cell_right_Style);
                    cell.setCellValue(map.get("creditDest_Qm").toString());
                }


                rownum++;
            }
            rownum++;
            row = sheet.createRow(rownum);
            CellRangeAddress region = new CellRangeAddress(rownum, rownum, 0, 1);
            sheet.addMergedRegion(region);
            CellRangeAddress region1 = new CellRangeAddress(rownum, rownum, 2, 4);
            sheet.addMergedRegion(region1);
            CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 5, 7);
            sheet.addMergedRegion(region2);

            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("财务主管：");
            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);


            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("操作员：" + CurrentUser.getCurrentUser().getUserName());
            cell = row.createCell(3);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(4);
            cell.setCellStyle(cellStyle);

            cell = row.createCell(5);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("打印日期：" + CurrentTime.getCurrentDate());
            cell = row.createCell(6);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            if (cumulativeAmount.equals("1")) {
                cell = row.createCell(8);
                cell.setCellStyle(cellStyle);
                cell = row.createCell(9);
                cell.setCellStyle(cellStyle);
            }

            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //专项科目明细账的导出
    public void exportSpecialSubjectDetail(HttpServletRequest request, HttpServletResponse response, List<?> result, String accounRules, String Date1, String Date2, String path) {
        try {
            String modelPath = path;
            File file = null;
            String fileName = "";
            if (accounRules.equals("1")) {//1科目2专项
                file = new File(modelPath + "SpecialSubjectDetail_Subject_1.xls");
                fileName = "专项科目明细账_科目.xls";
            } else {
                file = new File(modelPath + "SpecialSubjectDetail_Special_1.xls");
                fileName = "专项科目明细账_专项.xls";
            }

            FileInputStream fis = null;
            fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);
            Sheet sheet = null;
            if (accounRules.equals("1")) {
                sheet = wb.getSheet("专项科目明细账_科目");
            } else {
                sheet = wb.getSheet("专项科目明细账_专项");
            }

            Row row;
            Cell cell;
            int rownum = 5;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 10);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格左右居中;
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);

            CellStyle cell_left_Style = wb.createCellStyle();
            cell_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格左右居左;
            cell_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setFont(font);

            CellStyle cell_right_Style = wb.createCellStyle();
            cell_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格左右居右;
            cell_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setFont(font);

            cell = sheet.getRow(0).getCell(0);//核算单位
            cell.setCellValue(CurrentUser.getCurrentManageBranchName());

            Map<String, Object> m = (Map<String, Object>) result.get(0);
            String centerCode = m.get("centerCode").toString();
            cell = sheet.getRow(2).getCell(0);//核算单位
            cell.setCellValue("核算单位：" + centerCode);
            cell = sheet.getRow(2).getCell(3);
            cell.setCellValue("期间：自" + Date1 + "--" + Date2);

            for (int i = 0; i < result.size(); i++) {
                row = sheet.createRow(rownum);
                Map<String, Object> map = (Map<String, Object>) result.get(i);

                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                if (map.get("voucherDate") != null) {
                    cell.setCellValue(map.get("voucherDate").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                if (map.get("voucherNo") != null) {
                    cell.setCellValue(map.get("voucherNo").toString());
                } else {
                    cell.setCellValue("");
                }

                if (accounRules.equals("1")) {
                    cell = row.createCell(2);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("subjectCode") != null) {
                        cell.setCellValue(map.get("subjectCode").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(3);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("subjectName") != null) {
                        cell.setCellValue(map.get("subjectName").toString());
                    } else {
                        cell.setCellValue("");
                    }


                    cell = row.createCell(4);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("specialCode") != null) {
                        cell.setCellValue(map.get("specialCode").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(5);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("specialName") != null) {
                        cell.setCellValue(map.get("specialName").toString());
                    } else {
                        cell.setCellValue("");
                    }

                } else {
                    cell = row.createCell(2);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("specialCode") != null) {
                        cell.setCellValue(map.get("specialCode").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(3);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("specialName") != null) {
                        cell.setCellValue(map.get("specialName").toString());
                    } else {
                        cell.setCellValue("");
                    }


                    cell = row.createCell(4);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("subjectCode") != null) {
                        cell.setCellValue(map.get("subjectCode").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(5);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("subjectName") != null) {
                        cell.setCellValue(map.get("subjectName").toString());
                    } else {
                        cell.setCellValue("");
                    }

                }


                cell = row.createCell(6);
                cell.setCellStyle(cell_left_Style);
                if (map.get("remarkName") != null) {
                    cell.setCellValue(map.get("remarkName").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(7);
                cell.setCellStyle(cell_right_Style);
                if (map.get("debitDest") != null) {
                    cell.setCellValue(map.get("debitDest").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(8);
                cell.setCellStyle(cell_right_Style);
                if (map.get("creditDest") != null) {
                    cell.setCellValue(map.get("creditDest").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(9);
                cell.setCellStyle(cellStyle);
                if (map.get("balanceFX") != null) {
                    cell.setCellValue(map.get("balanceFX").toString());
                } else {
                    cell.setCellValue("");
                }


                cell = row.createCell(10);
                cell.setCellStyle(cell_right_Style);
                if (map.get("balanceDest") != null) {
                    cell.setCellValue(map.get("balanceDest").toString());
                } else {
                    cell.setCellValue("");
                }


                rownum++;
            }
            rownum++;
            row = sheet.createRow(rownum);
            CellRangeAddress region = new CellRangeAddress(rownum, rownum, 0, 2);
            sheet.addMergedRegion(region);
            CellRangeAddress region1 = new CellRangeAddress(rownum, rownum, 3, 6);
            sheet.addMergedRegion(region1);
            CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 7, 10);
            sheet.addMergedRegion(region2);

            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("财务主管：");
            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);

            cell = row.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("操作员：" + CurrentUser.getCurrentUser().getUserName());

            cell = row.createCell(4);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellStyle(cellStyle);

            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("打印日期：" + CurrentTime.getCurrentDate());


            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(10);
            cell.setCellStyle(cellStyle);

            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //专项科目余额表的导出
    public void exportSpecialSubjectBalance(HttpServletRequest request, HttpServletResponse response, List<?> result, String accounRules, String cumulativeAmount, String Date1, String Date2, String path) {
        try {
            String modelPath = path;
            File file = null;
            String fileName = "";
            if (cumulativeAmount.equals("0")) {
                file = new File(modelPath + "SpecialSubjectBalance_1.xls");
                fileName = "专项科目余额表.xls";
            } else {
                file = new File(modelPath + "SpecialSubjectBalanceSum_1.xls");
                fileName = "专项科目余额表_累计.xls";
            }

            FileInputStream fis = null;
            fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);
            Sheet sheet = null;
            if (cumulativeAmount.equals("0")) {
                sheet = wb.getSheet("专项科目余额表");
            } else {
                sheet = wb.getSheet("专项科目余额表_累计");
            }

            Row row;
            Cell cell;
            int rownum = 5;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 10);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格左右居中;
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);
            CellStyle cell_left_Style = wb.createCellStyle();
            cell_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格左右居左;
            cell_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setFont(font);
            CellStyle cell_right_Style = wb.createCellStyle();
            cell_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格左右居右;
            cell_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setFont(font);

            cell = sheet.getRow(0).getCell(0);//核算单位
            cell.setCellValue(CurrentUser.getCurrentManageBranchName());
            Map<String, Object> m = (Map<String, Object>) result.get(0);
            String centerCode = m.get("centerCode").toString();
            cell = sheet.getRow(2).getCell(0);//核算单位
            cell.setCellValue("核算单位：" + centerCode);
            cell = sheet.getRow(2).getCell(2);
            cell.setCellValue("期间：自" + Date1 + "--" + Date2);

            for (int i = 0; i < result.size(); i++) {
                row = sheet.createRow(rownum);
                Map<String, Object> map = (Map<String, Object>) result.get(i);

                if (accounRules.equals("1")) {//科目
                    cell = row.createCell(0);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("subjectCode") != null) {
                        cell.setCellValue(map.get("subjectCode").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(1);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("subjectName") != null) {
                        cell.setCellValue(map.get("subjectName").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(2);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("detailCode") != null) {
                        cell.setCellValue(map.get("detailCode").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(3);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("detailName") != null) {
                        cell.setCellValue(map.get("detailName").toString());
                    } else {
                        cell.setCellValue("");
                    }
                } else {//专项
                    cell = sheet.getRow(3).getCell(0);
                    cell.setCellValue("专项");
                    cell = sheet.getRow(3).getCell(2);
                    cell.setCellValue("科目");
                    cell = row.createCell(0);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("detailCode") != null) {
                        cell.setCellValue(map.get("detailCode").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(1);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("detailName") != null) {
                        cell.setCellValue(map.get("detailName").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(2);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("subjectCode") != null) {
                        cell.setCellValue(map.get("subjectCode").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(3);
                    cell.setCellStyle(cell_left_Style);
                    if (map.get("subjectCode") != null) {
                        cell.setCellValue(map.get("subjectCode").toString());
                    } else {
                        cell.setCellValue("");
                    }
                }


                cell = row.createCell(4);
                cell.setCellStyle(cellStyle);
                if (map.get("directionQc") != null) {
                    cell.setCellValue(map.get("directionQc").toString());
                } else {
                    cell.setCellValue("");
                }


                cell = row.createCell(5);
                cell.setCellStyle(cell_right_Style);
                if (map.get("balanceQc") != null) {
                    cell.setCellValue(map.get("balanceQc").toString());
                } else {
                    cell.setCellValue("");
                }
                //cell.setCellValue(map.get("balanceQc").toString());

                cell = row.createCell(6);
                cell.setCellStyle(cell_right_Style);
                cell.setCellValue(map.get("debitBq").toString());

                cell = row.createCell(7);
                cell.setCellStyle(cell_right_Style);
                cell.setCellValue(map.get("creditBq").toString());


                if (cumulativeAmount.equals("0")) {

                    cell = row.createCell(8);
                    cell.setCellStyle(cellStyle);
                    if (map.get("directionQm") != null) {
                        cell.setCellValue(map.get("directionQm").toString());
                    } else {
                        cell.setCellValue("");
                    }


                    cell = row.createCell(9);
                    cell.setCellStyle(cell_right_Style);
                    if (map.get("balanceQm") != null) {
                        cell.setCellValue(map.get("balanceQm").toString());
                    } else {
                        cell.setCellValue("");
                    }
                    // cell.setCellValue(map.get("balanceQm").toString());

                } else {
                    cell = row.createCell(8);
                    cell.setCellStyle(cell_right_Style);
                    cell.setCellValue(map.get("debitBn").toString());

                    cell = row.createCell(9);
                    cell.setCellStyle(cell_right_Style);
                    cell.setCellValue(map.get("creditBn").toString());

                    cell = row.createCell(10);
                    cell.setCellStyle(cellStyle);
                    if (map.get("directionQm") != null) {
                        cell.setCellValue(map.get("directionQm").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = row.createCell(11);
                    cell.setCellStyle(cell_right_Style);
                    if (map.get("balanceQm") != null) {
                        cell.setCellValue(map.get("balanceQm").toString());
                    } else {
                        cell.setCellValue("");
                    }
                    //  cell.setCellValue(map.get("balanceQm").toString());
                }

                rownum++;
            }
            rownum++;
            row = sheet.createRow(rownum);
            CellRangeAddress region = new CellRangeAddress(rownum, rownum, 0, 2);
            sheet.addMergedRegion(region);
            CellRangeAddress region1 = new CellRangeAddress(rownum, rownum, 3, 7);
            if (cumulativeAmount.equals("0")) {
                sheet.addMergedRegion(region1);
                CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 8, 9);
                sheet.addMergedRegion(region2);
            } else {
                sheet.addMergedRegion(region1);
                CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 8, 11);
                sheet.addMergedRegion(region2);
            }


            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("财务主管：");
            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);

            cell = row.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("操作员：" + CurrentUser.getCurrentUser().getUserName());
            cell = row.createCell(4);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);

            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("打印日期：" + CurrentTime.getCurrentDate());
            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);

            if (cumulativeAmount.equals("1")) {
                cell = row.createCell(10);
                cell.setCellStyle(cellStyle);
                cell = row.createCell(11);
                cell.setCellStyle(cellStyle);
            }


            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //单个凭证导出
    public void exportVoucher(HttpServletRequest request, HttpServletResponse response, Map<String, Object> result,String path) {

        try {
            String modelPath = path;
            File file = new File(modelPath + "voucherExport_1.xls");
            String fileName = result.get("voucherNo") + "凭证信息表.xls";
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);

            Sheet sheet = wb.getSheet("凭证信息");
            sheet.setDefaultRowHeight((short) 500);//设置默认行高
            Row row;
            Cell cell;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 11);

            CellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            //设置标题单元格格式-title3
            CellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            titleStyle.setFillBackgroundColor((short) 40);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格居中
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());//设置单元格背景色
            titleStyle.setFont(font);
            //设置文本单元格
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格靠左
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);

            CellStyle balanceStyle = wb.createCellStyle();
            balanceStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格靠右
            balanceStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            balanceStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            balanceStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            balanceStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

            CellStyle totalStyle = wb.createCellStyle();
            totalStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格靠左
            totalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            totalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            totalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            totalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            totalStyle.setFont(font);

            cell = sheet.getRow(1).getCell(2);//凭证号
            if (result.get("voucherNo") != null) {
                cell.setCellValue(result.get("voucherNo").toString());
            } else {
                cell.setCellValue("");
            }

            cell = sheet.getRow(1).getCell(5);//会计期间
            cell.setCellStyle(style);
            if (result.get("yearMonthDate") != null) {
                cell.setCellValue(result.get("yearMonthDate").toString());
            } else {
                cell.setCellValue("");
            }

            cell = sheet.getRow(2).getCell(2);//制单日期
            if (result.get("voucherDate") != null) {
                cell.setCellValue(result.get("voucherDate").toString());
            } else {
                cell.setCellValue("");
            }

            cell = sheet.getRow(2).getCell(5);//附件张数
            cell.setCellStyle(style);
            if (result.get("auxNumber") != null) {
                cell.setCellValue(result.get("auxNumber").toString());
            } else {
                cell.setCellValue("");
            }

            cell = sheet.getRow(3).getCell(2);//制单人
            if (result.get("createBy") != null) {
                cell.setCellValue(result.get("createBy").toString());
            } else {
                cell.setCellValue("");
            }

            cell = sheet.getRow(3).getCell(5);//凭证状态
            cell.setCellStyle(style);
            if (result.get("voucherFlag") != null) {
                cell.setCellValue(result.get("voucherFlag").toString());
            } else {
                cell.setCellValue("");
            }

            cell = sheet.getRow(4).getCell(2);//复核人
            if (result.get("approveBy") != null) {
                cell.setCellValue(result.get("approveBy").toString());
            } else {
                cell.setCellValue("");
            }

            cell = sheet.getRow(4).getCell(5);//记账人
            cell.setCellStyle(style);
            if (result.get("geneBy") != null) {
                cell.setCellValue(result.get("geneBy").toString());
            } else {
                cell.setCellValue("");
            }

            int rownum = 7;
            List<Map<String, Object>> data1 = (List<Map<String, Object>>) result.get("data1");
            BigDecimal totalDebit = new BigDecimal("0.00");
            BigDecimal totalCredit = new BigDecimal("0.00");

            for (int i = 0; i < data1.size(); i++) {
                row = sheet.createRow(rownum);
                row.setHeight((short) 500);//通过row设置行高
                cell = row.createCell(0);//序号
                cell.setCellStyle(cellStyle);
                cell.setCellValue(i + 1);
                cell = sheet.getRow(rownum).createCell(1);//标注
                cell.setCellStyle(cellStyle);
                if (data1.get(i).get("tagCode") != null) {
                    cell.setCellValue(data1.get(i).get("tagCode").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = sheet.getRow(rownum).createCell(2);//摘要
                cell.setCellStyle(cellStyle);
                if (data1.get(i).get("remark") != null) {
                    cell.setCellValue(data1.get(i).get("remark").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = sheet.getRow(rownum).createCell(3);//科目代码
                cell.setCellStyle(cellStyle);
                if (data1.get(i).get("subjectCode") != null) {
                    cell.setCellValue(data1.get(i).get("subjectCode").toString());
                } else {
                    cell.setCellValue("");
                }
                cell = sheet.getRow(rownum).createCell(4);//科目名称
                cell.setCellStyle(cellStyle);
                if (data1.get(i).get("subjectName") != null) {
                    cell.setCellValue(data1.get(i).get("subjectName").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = sheet.getRow(rownum).createCell(5);//借方金额
                cell.setCellStyle(balanceStyle);
                if (data1.get(i).get("debitDest") != null) {
                    cell.setCellValue(data1.get(i).get("debitDest").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = sheet.getRow(rownum).createCell(6);//贷方金额
                cell.setCellStyle(balanceStyle);
                if (data1.get(i).get("creditDest") != null) {
                    cell.setCellValue(data1.get(i).get("creditDest").toString());
                } else {
                    cell.setCellValue("");
                }

                totalDebit = totalDebit.add((BigDecimal) data1.get(i).get("debitDest"));
                totalCredit = totalCredit.add((BigDecimal) data1.get(i).get("creditDest"));
                rownum++;
            }

            CellRangeAddress region3 = new CellRangeAddress(rownum, rownum, 0, 6);
            sheet.addMergedRegion(region3);
            row = sheet.createRow(rownum);
            row.setHeight((short) 500);//通过row设置行高
            cell = row.createCell(0);//当前凭证合计
            cell.setCellValue("当前凭证合计：     借方金额：" + totalDebit + "      贷方金额：" + totalCredit);
            cell.setCellStyle(totalStyle);
            cell = row.createCell(1);
            cell.setCellStyle(totalStyle);
            cell = row.createCell(2);
            cell.setCellStyle(totalStyle);
            cell = row.createCell(3);
            cell.setCellStyle(totalStyle);
            cell = row.createCell(4);
            cell.setCellStyle(totalStyle);
            cell = row.createCell(5);
            cell.setCellStyle(totalStyle);
            cell = row.createCell(6);
            cell.setCellStyle(totalStyle);
            rownum += 1;

            //凭证专项分录标题栏
            CellRangeAddress region = new CellRangeAddress(rownum, rownum, 0, 6);
            sheet.addMergedRegion(region);
            row = sheet.createRow(rownum);
            row.setHeight((short) 500);//通过row设置行高
            cell = row.createCell(0);
            cell.setCellValue("关联专项信息");
            cell.setCellStyle(titleStyle);
            cell = row.createCell(1);
            cell.setCellStyle(titleStyle);
            cell = row.createCell(2);
            cell.setCellStyle(titleStyle);
            cell = row.createCell(3);
            cell.setCellStyle(titleStyle);
            cell = row.createCell(4);
            cell.setCellStyle(titleStyle);
            cell = row.createCell(5);
            cell.setCellStyle(titleStyle);
            cell = row.createCell(6);
            cell.setCellStyle(titleStyle);
            rownum += 1;

            row = sheet.createRow(rownum);
            row.setHeight((short) 500);//通过row设置行高
            cell = row.createCell(0);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("序号");

            cell = sheet.getRow(rownum).createCell(1);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("科目代码");
            cell = sheet.getRow(rownum).createCell(2);
            cell.setCellStyle(titleStyle);
            cell.setCellValue("科目名称");

            CellRangeAddress region1 = new CellRangeAddress(rownum, rownum, 3, 6);
            sheet.addMergedRegion(region1);
            cell = sheet.getRow(rownum).createCell(3);
            cell.setCellValue("专项信息");
            cell.setCellStyle(titleStyle);

            cell = row.createCell(4);
            cell.setCellStyle(titleStyle);
            cell = row.createCell(5);
            cell.setCellStyle(titleStyle);
            cell = row.createCell(6);
            cell.setCellStyle(titleStyle);
            rownum += 1;

            List<Map<String, Object>> data2 = (List<Map<String, Object>>) result.get("data2");
            //凭证专项分录内容
            for (int j = 0; j < data2.size(); j++) {

                row = sheet.createRow(rownum);
                row.setHeight((short) 500);//通过row设置行高
                cell = row.createCell(0);//序号
                cell.setCellStyle(cellStyle);
                cell.setCellValue(j + 1);
                cell = sheet.getRow(rownum).createCell(1);//科目代码
                cell.setCellStyle(cellStyle);
                if (data2.get(j).get("subjectCode") != null) {
                    cell.setCellValue(data2.get(j).get("subjectCode").toString());
                } else {
                    cell.setCellValue("");
                }

                cell = sheet.getRow(rownum).createCell(2);//科目名称
                cell.setCellStyle(cellStyle);
                if (data2.get(j).get("subjectName") != null) {
                    cell.setCellValue(data2.get(j).get("subjectName").toString());
                } else {
                    cell.setCellValue("");
                }

                CellRangeAddress region2 = new CellRangeAddress(rownum, rownum, 3, 6);
                sheet.addMergedRegion(region2);
                cell = sheet.getRow(rownum).createCell(3);//专项信息
                if (data2.get(j).get("specialMessage") != null) {
                    cell.setCellValue(data2.get(j).get("specialMessage").toString());
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(cellStyle);

                cell = row.createCell(4);
                cell.setCellStyle(cellStyle);
                cell = row.createCell(5);
                cell.setCellStyle(cellStyle);
                cell = row.createCell(6);
                cell.setCellStyle(cellStyle);
                rownum++;
            }

            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *  凭证管理分录明细批量
     */
    public void exportVoucherAboutDetails(HttpServletRequest request, HttpServletResponse response, List<Map<String, Object>> result,String path ){
        try {
            String modelPath = path;
            File file = new File(modelPath + "voucherExportDetails.xls");
            String fileName = UUID.randomUUID() + "凭证信息表.xls";
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);

            Sheet sheet = wb.getSheet("凭证信息");
            sheet.setDefaultRowHeight((short) 500);//设置默认行高
            Row row;
            Cell cell;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 11);

            CellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            //设置标题单元格格式-title3
            CellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            titleStyle.setFillBackgroundColor((short) 40);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格居中
            titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            titleStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());//设置单元格背景色
            titleStyle.setFont(font);
            //设置文本单元格
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格靠左
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);

            CellStyle balanceStyle = wb.createCellStyle();
            balanceStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格靠右
            balanceStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            balanceStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            balanceStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            balanceStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

            CellStyle totalStyle = wb.createCellStyle();
            totalStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格靠左
            totalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);//边框全包
            totalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            totalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            totalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            totalStyle.setFont(font);

            //开始写样式  从第三行开始写入数据信息
            int rownum = 3;
            for (Map<String,Object> resultMaps : result) {
                // 凭证号
                String voucherNo =resultMaps.get("voucherNo").toString();
                // 制单日期
                String voucherDate = resultMaps.get("voucherDate").toString();
                // 会计月度
                String yearMonthDate = resultMaps.get("yearMonthDate").toString();
                resultMaps.get("data2");
                List<Map<String, Object>> data1 = (List<Map<String, Object>>) resultMaps.get("data2");
                for(int i = 0 ; i < data1.size(); i ++){
                    String remark = data1.get(i).get("remark").toString();

                    row = sheet.createRow(rownum);
                    row.setHeight((short) 500);//通过row设置行高
                    cell = sheet.getRow(rownum).createCell(0);//凭证号
                    cell.setCellStyle(cellStyle);
                    if (voucherNo != null) {
                        cell.setCellValue(voucherNo);
                    } else {
                        cell.setCellValue("");
                    }

                    cell = sheet.getRow(rownum).createCell(1);//日期
                    cell.setCellStyle(cellStyle);
                    if (voucherDate != null) {
                        cell.setCellValue(voucherDate);
                    } else {
                        cell.setCellValue("");
                    }

                    cell = sheet.getRow(rownum).createCell(2);// 摘要
                    cell.setCellStyle(cellStyle);
                    if(data1.get(i).get("remark") != null){
                        cell.setCellValue(data1.get(i).get("remark").toString());
                    }else{
                        cell.setCellValue("");
                    }

                    cell = sheet.getRow(rownum).createCell(3);//科目代码
                    cell.setCellStyle(cellStyle);
                    if (data1.get(i).get("subjectCode") != null) {
                        cell.setCellValue(data1.get(i).get("subjectCode").toString());
                    } else {
                        cell.setCellValue("");
                    }
                    cell = sheet.getRow(rownum).createCell(4);//科目名称
                    cell.setCellStyle(cellStyle);
                    if (data1.get(i).get("subjectName") != null) {
                        cell.setCellValue(data1.get(i).get("subjectName").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = sheet.getRow(rownum).createCell(5);//借方金额
                    cell.setCellStyle(balanceStyle);
                    if (data1.get(i).get("debitDest") != null) {
                        cell.setCellValue(data1.get(i).get("debitDest").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = sheet.getRow(rownum).createCell(6);//贷方金额
                    cell.setCellStyle(balanceStyle);
                    if (data1.get(i).get("creditDest") != null) {
                        cell.setCellValue(data1.get(i).get("creditDest").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = sheet.getRow(rownum).createCell(7);//专项代码
                    cell.setCellStyle(balanceStyle);
                    if (data1.get(i).get("specialCode") != null) {
                        cell.setCellValue(data1.get(i).get("specialCode").toString());
                    } else {
                        cell.setCellValue("");
                    }

                    cell = sheet.getRow(rownum).createCell(8);//专项名称
                    cell.setCellStyle(balanceStyle);
                    if (data1.get(i).get("specialMessage") != null) {
                        cell.setCellValue(data1.get(i).get("specialMessage").toString());
                    } else {
                        cell.setCellValue("");
                    }
                    rownum++;//加一行
                }
                rownum++;// 当一条凭证分录记录后，新加一行作为空行进行分割。
            }



            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();

        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //水电费查询导出
    public void exportAgeAnalysis(HttpServletRequest request, HttpServletResponse response, List<?> result, String path, String computeDate, Integer version, String ageAnalysisTypeName, String centerCodeName, String accBookCodeName, String unit) {

        try {
            String modelPath = path;
            File file = new File(modelPath + "AgeAnalysisTable_"+version+".xls");
            String fileName = computeDate + " " + ageAnalysisTypeName + "账龄分析.xls";
            FileInputStream fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);
            Sheet sheet = wb.getSheet("账龄报表");
            Row row;
            Cell cell;
            int rownum = 6;

            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 10);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//单元格左右居中;
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(font);

            CellStyle cell_left_Style = wb.createCellStyle();
            cell_left_Style.setAlignment(HSSFCellStyle.ALIGN_LEFT);//单元格左右居左;
            cell_left_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_left_Style.setFont(font);


            CellStyle cell_right_Style = wb.createCellStyle();
            cell_right_Style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//单元格左右居右;
            cell_right_Style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cell_right_Style.setFont(font);

            cell = sheet.getRow(1).getCell(1);//账套
            cell.setCellValue(centerCodeName+"_"+accBookCodeName);
            cell = sheet.getRow(2).getCell(1);//账龄类型
            cell.setCellValue(ageAnalysisTypeName);
            cell = sheet.getRow(3).getCell(1);//账龄计算时间
            cell.setCellValue(computeDate);

            for (int i = 0; i < result.size(); i++) {

                row = sheet.createRow(rownum);

                Map<String, Object> map = (Map<String, Object>) result.get(i);
                int mapSize = map.size();
                for (int j=0;j<mapSize;j++) {
                    cell = row.createCell(j);
                    if (j==0 || j==7) {
                        cell.setCellStyle(cellStyle);
                    } else if (j==1 || j==2 || j==3 || j==4 || j==5) {
                        cell.setCellStyle(cell_left_Style);
                    } else {
                        cell.setCellStyle(cell_right_Style);
                    }
                    String str = (String) map.get("name"+(j+1));
                    if (str!=null && !"".equals(str)) {
                        cell.setCellValue(str);
                    }
                }
                rownum++;
            }

            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "sttachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            wb.write(out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}