package com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse;

//生成Excel的类 
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.nari.slsd.hms.hdu.common.selecteddialog.Constant;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @author HongLiChen
 * Excel 操作的工具类
 */
public class ExcelUtil
{

	final static int img_width = 800;
	final static int img_height = 600;

	/**
	 * 把字节数组保存为一个文件对象
	 * 
	 * @param b 字节数组
	 * @param outputFile 输出的文件对象
	 * @return
	 */
	public static File getFileFromBytes(byte[] b, String outputFile)
	{
		File ret = null;
		BufferedOutputStream stream = null;
		try
		{
			ret = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(ret);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e)
		{
			// log.error("helper:get file from byte process error!");
			e.printStackTrace();
		} finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				} catch (IOException e)
				{
					// log.error("helper:get file from byte process error!");
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	/**
	 * @param chart 要导出的chart对象
	 * @param sheet 要导出到的表格
	 * @throws IOException
	 */
	public static void saveChartAsPNG(JFreeChart chart,WritableSheet sheet)
	{
		ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
		OutputStream out = new BufferedOutputStream(byteArrayOutputStream1);
		try
		{
			ChartUtilities.writeChartAsPNG(out, chart, img_width, img_height,null);
			byte[] array = byteArrayOutputStream1.toByteArray();
			// 图表的位置
			ExcelUtil.addImage(sheet, array, 15, 2);

		} 
		catch (Exception e)
		{
			System.out.println(e);
		} 
		finally
		{
			try
			{
				out.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建一个xls文件
	 * 
	 * @param filePath
	 *            要创建的xls的文件路径
	 * @return 打开后的文档对象
	 * @throws IOException
	 */
	public static WritableWorkbook createExcel(String filePath)
			throws IOException
	{
		WritableWorkbook book = Workbook.createWorkbook(new File(filePath));
		return book;
	}

	/**
	 * 向存在的xls文件中添加一张表
	 * 
	 * @param workbook
	 *            xls文件对象
	 * @param sheetTitle
	 *            表格的名字
	 * @param index
	 *            表格的序号，按照升序排列
	 * @return
	 */
	public static WritableSheet createSheet(WritableWorkbook workbook,
			String sheetTitle, int index)
	{
		return workbook.createSheet(sheetTitle, index);
	}

	/**
	 * 向表格添加一个字符串
	 * 
	 * @param sheet
	 *            要添加数据的表格
	 * @param row
	 *            要添加的行
	 * @param col
	 *            要添加的列
	 * @param text
	 *            要添加的字符串
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public static void addString(WritableSheet sheet, int row, int col,
			String text) throws RowsExceededException, WriteException
	{
		WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 10,
				WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE);

		WritableCellFormat headerFormat = new WritableCellFormat(
				NumberFormats.TEXT);
		// 添加字体设置
		headerFormat.setFont(font);
        //水平居中对齐
        headerFormat.setAlignment(Alignment.CENTRE);
        //竖直方向居中对齐
        headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        
		Label label = new Label(col, row, text,headerFormat);
		sheet.addCell(label);
	}
	
	/**
	 * 向表格添加一个字符串
	 * 
	 * @param sheet
	 *            要添加数据的表格
	 * @param row
	 *            要添加的行
	 * @param col
	 *            要添加的列
	 * @param text
	 *            要添加的字符串
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public static void addString(WritableSheet sheet, int row, int col,
			String text, boolean BOLD) throws RowsExceededException,
			WriteException
	{

		if (BOLD == false)
		{
			addString(sheet, row, col, text);
			return;
		}
		WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 10,
				WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE);

		WritableCellFormat headerFormat = new WritableCellFormat(
				NumberFormats.TEXT);
		// 添加字体设置
		headerFormat.setFont(font);
        //水平居中对齐
        headerFormat.setAlignment(Alignment.CENTRE);
        //竖直方向居中对齐
        headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

		Label label = new Label(col, row, text, headerFormat);
		sheet.addCell(label);
	}

	/**
	 * 向表格添加一张图片
	 * 
	 * @param sheet
	 *            要添加数据的表格
	 * @param imagePath
	 *            需要添加的图片路径
	 * @param row
	 *            要添加的行
	 * @param col
	 *            要添加的列
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addImage(WritableSheet sheet, String imagePath, int row,
			int col) throws FileNotFoundException, IOException
	{
		File picture = new File(imagePath);
		addImage(sheet, picture, row, col);
	}

	/**
	 * 向表格添加一张图片
	 * 
	 * @param sheet
	 *            要添加数据的表格
	 * @param picture
	 *            需要添加的图片文件
	 * @param row
	 *            要添加的行
	 * @param col
	 *            要添加的列
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addImage(WritableSheet sheet, File picture, int row,
			int col) throws FileNotFoundException, IOException
	{
		BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
		// 输出图片，并且保证图标的长宽比保持一致，3.4的倍数是因为这里的长度是单元格的个数，长是宽的3.4倍
		WritableImage ri = new WritableImage(8, 5, sourceImg.getWidth() / 100,
				3.4 * sourceImg.getHeight() / 100, picture);
		sheet.addImage(ri);

	}

	/**
	 * 向表格添加一张图片
	 * 
	 * @param sheet
	 *            要添加数据的表格
	 * @param imageData
	 *            需要添加的图片文件数据
	 * @param row
	 *            要添加的行
	 * @param col
	 *            要添加的列
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addImage(WritableSheet sheet, byte[] imageData, int row,
			int col) throws FileNotFoundException, IOException
	{
		// 将b作为输入流;
		ByteArrayInputStream in = new ByteArrayInputStream(imageData);
		// 将in作为输入流，读取图片存入image中，而这里in可以为ByteArrayInputStream();
		BufferedImage sourceImg = ImageIO.read(in);
		// 输出图片，并且保证图标的长宽比保持一致，4的倍数是因为这里的长度是单元格的个数，长是宽的4倍
		WritableImage ri = new WritableImage(col,row , sourceImg.getWidth() / 100,
				4 * sourceImg.getHeight() / 100, imageData);
		sheet.addImage(ri);

	}

	/**
	 * 向表格添加数据
	 * 
	 * @param sheet
	 *            要添加数据的表格
	 * @param row
	 *            要添加的行
	 * @param col
	 *            要添加的列
	 * @param num
	 *            需要添加的数值，默认为double
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void addData(WritableSheet sheet, int row, int col, double num)
			throws RowsExceededException, WriteException
	{
		WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 10,
				WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE);

		WritableCellFormat headerFormat = new WritableCellFormat(
				NumberFormats.FLOAT);
		// 添加字体设置
		headerFormat.setFont(font);
        //水平居中对齐
        headerFormat.setAlignment(Alignment.CENTRE);
        //竖直方向居中对齐
        headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        
		jxl.write.Number number = new jxl.write.Number(col, row, num,headerFormat);
		sheet.addCell(number);
	}

	/**
	 * 保存数据并关闭
	 * 
	 * @param workbook
	 *            需要保存的xls文档对象
	 * @throws IOException
	 * @throws WriteException
	 */
	public static void saveFile(WritableWorkbook workbook) throws IOException,
			WriteException
	{
		workbook.write();
		workbook.close();
	}
	
	public static void setColSize(WritableSheet sheet,int col,int size){
		for (int i = 0; i < col; i++)
		{
			sheet.setColumnView(i, size);
		}
	}

	/*
	// 输出测试
	public static void main(String args[])
	{
		try
		{
			ExcelUtil createExcel = new ExcelUtil();
			WritableWorkbook workbook = createExcel
					.createExcel("D://Javaprogram//workspacest//testjxl//test1.xls");
			WritableSheet sheet = createExcel.createSheet(workbook, "表格1", 0);
			createExcel.addData(sheet, 0, 0, 123456);
			createExcel.addString(sheet, 1, 1, "哈哈哈");
			createExcel.addImage(sheet,
					"D://Javaprogram//workspacest//testjxl//1.png", 5, 6);
			createExcel.saveFile(workbook);
		} catch (Exception e)
		{
			System.out.println(e);
		}
	}
	*/
}