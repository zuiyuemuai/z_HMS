package com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class testExcel {

	private final static testExcel jxlTable = new testExcel();

	public static testExcel getInstance() {
		return jxlTable;
	}
	
	public testExcel(){}
	
	/**
	 * 根据输入的内容创建一个表格
	 * 要求：
	 * 表头表格线为粗线，表体表格线为细线；
	 * 表头背景色为黄色且表头字体加粗居中显示，表体为无色；
	 * 表头以及表体内容可以按照一定的格式输入；
	 * 
	 * 保留一个sheet且sheet的背景为无网格线;
	 * 
	 * @return 创建成功：true；创建失败：false；
	 */
	public boolean createTable(String header,String[] body,String filePath){
		boolean createFlag = true;
		
		WritableWorkbook book;
		try {
			//根据路径生成excel文件
			book = Workbook.createWorkbook(new File(filePath));
			//创建一个sheet名为"表格"
			WritableSheet sheet = book.createSheet("表格", 0);
			
			//设置NO列宽度
			sheet.setColumnView(1, 5);
			//去掉整个sheet中的网格线
			sheet.getSettings().setShowGridLines(false);
			
			Label tempLabel = null;
			//表头输出
			String[] headerArr = header.split(",");
			int headerLen = headerArr.length;
			//循环写入表头内容
			for(int i=0; i < headerLen; i ++){
				tempLabel = new Label(1+i,1,headerArr[i],getHeaderCellStyle());
				sheet.addCell(tempLabel);
			}
			//表体输出
			int bodyLen = body.length;
			//循环写入表体内容
			for(int j=0; j < bodyLen; j ++){
				String[] bodyTempArr = body[j].split(",");
				for(int k=0; k < bodyTempArr.length; k ++){
					WritableCellFormat tempCellFormat = null;
					/*
					 * 表体内容的对齐设置
					 * 这里将序号NO以及年龄居中对齐，姓名以及性别默认对齐方式
					 */
					tempCellFormat = getBodyCellStyle();
					if(tempCellFormat != null){
						if(k == 0 || k == (bodyTempArr.length -1)){
							tempCellFormat.setAlignment(Alignment.CENTRE);
						}
					}
					tempLabel = new Label(1+k,2+j,bodyTempArr[k],tempCellFormat);
					sheet.addCell(tempLabel);
				}
				
			}
			book.write();
			book.close();
		} catch (IOException e) {
			createFlag = false;
			System.out.println("EXCEL创建失败！");
			e.printStackTrace();
			
		}catch (RowsExceededException e) {
			createFlag = false;
			System.out.println("EXCEL单元设置创建失败！");
			e.printStackTrace();
		} catch (WriteException e) {
			createFlag = false;
			System.out.println("EXCEL写入失败！");
			e.printStackTrace();
		}

		return createFlag;
		
	}
	/**
	 * 表头单元格样式的设定
	 */
	public WritableCellFormat getHeaderCellStyle(){
		
		/*
		 * WritableFont.createFont("宋体")：设置字体为宋体
		 * 10：设置字体大小
		 * WritableFont.BOLD:设置字体加粗（BOLD：加粗     NO_BOLD：不加粗）
		 * false：设置非斜体
		 * UnderlineStyle.NO_UNDERLINE：没有下划线
		 */
		WritableFont font = new WritableFont(WritableFont.createFont("宋体"),
			                                 10, 
			                                 WritableFont.BOLD, 
			                                 false,
			                                 UnderlineStyle.NO_UNDERLINE);
		
		WritableCellFormat headerFormat = new WritableCellFormat(NumberFormats.TEXT);
		try {
			//添加字体设置
			headerFormat.setFont(font);
			//设置单元格背景色：表头为黄色
			headerFormat.setBackground(Colour.YELLOW);
			//设置表头表格边框样式
			//整个表格线为粗线、黑色
			headerFormat.setBorder(Border.ALL, BorderLineStyle.THICK, Colour.BLACK);
			//表头内容水平居中显示
			headerFormat.setAlignment(Alignment.CENTRE);	
		} catch (WriteException e) {
			System.out.println("表头单元格样式设置失败！");
		}
		return headerFormat;
	}
	/**
	 * 表头单元格样式的设定
	 */
	public WritableCellFormat getBodyCellStyle(){
		
		/*
		 * WritableFont.createFont("宋体")：设置字体为宋体
		 * 10：设置字体大小
		 * WritableFont.NO_BOLD:设置字体非加粗（BOLD：加粗     NO_BOLD：不加粗）
		 * false：设置非斜体
		 * UnderlineStyle.NO_UNDERLINE：没有下划线
		 */
		WritableFont font = new WritableFont(WritableFont.createFont("宋体"),
			                                 10, 
			                                 WritableFont.NO_BOLD, 
			                                 false,
			                                 UnderlineStyle.NO_UNDERLINE);
		
		WritableCellFormat bodyFormat = new WritableCellFormat(font);
		try {
			//设置单元格背景色：表体为白色
			bodyFormat.setBackground(Colour.WHITE);
			//设置表头表格边框样式
			//整个表格线为细线、黑色
			bodyFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			
		} catch (WriteException e) {
			System.out.println("表体单元格样式设置失败！");
		}
		return bodyFormat;
	}
	
	public static void main(String[] args) {
		String header = "NO,姓名,性别,年龄";
		String[] body = new String[4];
		body[0] = "1,欧阳锋,男,68";
		body[1] = "2,黄药师,男,67";
		body[2] = "3,洪七公,男,70";
		body[3] = "4,郭靖,男,32";
		String filePath = "e:/test.xls";
		
		testExcel testJxl = testExcel.getInstance();
		
		boolean flag = testJxl.createTable(header, body, filePath);
		if(flag){
			System.out.println("表格创建成功！！");
		}
	}
}