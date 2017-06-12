package com.jogamp.opengl.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.nari.slsd.hms.hdu.utils.HduChartUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * 输出word报表
 * @author YXQ
 * 
 *
 */
public abstract class HduCreatWord
{

//	protected ResourceBundle res = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);

	private Configuration configuration = null;
	private String inputPath;
	private String outputPath;
	private String fileName;

	/**
	 * 
	 * @param inputPath 模板文件的路径
	 * @param outputPath 输出保存路径
	 * @param fileName 文件名
	 */
	public HduCreatWord(String inputPath, String outputPath, String fileName)
	{
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.fileName = fileName;
		configuration = new Configuration();
		configuration.setDefaultEncoding("UTF-8");
		createWord();
	}

	public void createWord()
	{
		if (outputPath == null)
		{
			return;
		}
		Map<String, Object> dataMap = new HashMap<String, Object>();
		getData(dataMap);
		configuration.setClassForTemplateLoading(this.getClass(), inputPath); // FTL文件所存在的位置
		File outFile = new File(outputPath);
		Writer out = null;
		Template t = null;
		try
		{
			t = configuration.getTemplate(fileName); // 文件名
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFile), "UTF-8"));
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		try
		{
			t.process(dataMap, out);
			JOptionPane.showMessageDialog(null,
					HduChartUtil.getResource("Common_CreatWord_Success"));
			out.close();
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public abstract void getData(Map<String, Object> dataMap);// 外部实现获得数据

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		HduCreatWord test = new HduCreatWord("/com", "F:/" + Math.random()
				* 10000 + ".doc", "xmlTest1.ftl")
		{

			@Override
			public void getData(Map<String, Object> dataMap)
			{
				// TODO Auto-generated method stub
				dataMap.put("title", "标题");
				dataMap.put("nian", "2012");
				dataMap.put("yue", "2");
				dataMap.put("ri", "13");
				dataMap.put("xuhao", 1);
				dataMap.put("zuoyeneirong", "内容" + 2);

				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < 10; i++)
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("xuhao", i);
					map.put("zuoyeneirong", "内容" + i);
					list.add(map);
				}
				dataMap.put("list", list);
				dataMap.put("bianzhi", "唐鑫");
				dataMap.put("shenghe", "詹文涛");
				dataMap.put("dianhua", "13020265912");
			}
		};
		// test.createWord();
	}

}
