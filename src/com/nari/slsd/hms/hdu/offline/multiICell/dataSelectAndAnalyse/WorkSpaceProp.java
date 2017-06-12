package com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse;

import java.util.Vector;

import com.nari.slsd.hms.hdu.common.comtrade.CmtrCfgInfo;
import com.nari.slsd.hms.hdu.common.comtrade.ComtradeData;

/**
 * 一个工作空间的资源描述 包含该工作空间所属水电站名称和实验名，以及实验下的所有文件信息
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class WorkSpaceProp
{
	// 工作路径的目录
	public String workSpacePath;
	public String stationName;// 水电站名称
	public String testName;// 实验名称
	public Vector<String> sectionName = new Vector<String>();// 区间的名称
	public Vector<Vector<String>> allWaveSoucePaths = new Vector<Vector<String>>();// 所有wave的文件路径
	public Vector<CmtrCfgInfo> allWaveCfgInfos = new Vector<CmtrCfgInfo>();// 所有wave的配置文件

	public Vector<Vector<String>> allAinSoucePaths = new Vector<Vector<String>>();// 所有ain的文件路径
	public Vector<CmtrCfgInfo> allAinCfgInfos = new Vector<CmtrCfgInfo>();// 所有ain的配置文件

	public Vector<Vector<String>> allKeyIndexSoucePaths = new Vector<Vector<String>>();// 所有键相的文件路径
	public Vector<CmtrCfgInfo> allKeyIndexCfgInfos = new Vector<CmtrCfgInfo>();// 所有键相的配置文件

	@SuppressWarnings("unchecked")
	public WorkSpaceProp clone()
	{
		WorkSpaceProp wProp = new WorkSpaceProp();
		wProp.workSpacePath = this.workSpacePath;
		wProp.allWaveSoucePaths = (Vector<Vector<String>>) allWaveSoucePaths
				.clone();
		wProp.allWaveCfgInfos = (Vector<CmtrCfgInfo>) allWaveCfgInfos.clone();
		wProp.sectionName = (Vector<String>) sectionName.clone();

		wProp.allAinCfgInfos = (Vector<CmtrCfgInfo>) allAinCfgInfos.clone();
		wProp.allAinSoucePaths = (Vector<Vector<String>>) allAinSoucePaths
				.clone();

		wProp.allKeyIndexCfgInfos = (Vector<CmtrCfgInfo>) allKeyIndexCfgInfos
				.clone();
		wProp.allKeyIndexSoucePaths = (Vector<Vector<String>>) allKeyIndexSoucePaths
				.clone();

		return wProp;

	}

	/**
	 * 通过workspace的信息和哪个段和通道名称来获取通道信息
	 * 
	 * @param section
	 *            分段的名称
	 * @param name
	 *            通道名称
	 * @return
	 */
	public float[] getData(String section, String name,
			Vector<Vector<String>> allSoucePaths,
			Vector<CmtrCfgInfo> allCfgInfos)
	{
		int id = getIndexFromSections(section);// 减一是由于所有段的名称是从1开始的
		Vector<String> soucePaths = allSoucePaths.get(id);
		CmtrCfgInfo cmtrCfgInfo = allCfgInfos.get(id);

		float[] temp = new float[cmtrCfgInfo.pointCount];

		for (int i = 0; i < cmtrCfgInfo.channelCount; i++)
		{

			if (name.equals(cmtrCfgInfo.channelName[i]))
			{

				temp = ComtradeData.getOneChannelDataFromFiles(soucePaths,
						cmtrCfgInfo, i);// i是第几个通达+特征量的偏移
				break;
			}
		}
		return temp;

	}

	public float[] getWaveData(String section, String name)
	{
		return this.getData(section, name, allWaveSoucePaths, allWaveCfgInfos);
	}

	public float[] getAinData(String section, String name)
	{
		return this.getData(section, name, allAinSoucePaths, allAinCfgInfos);
	}

	/**
	 * 通过workspace的信息和哪个段和通道序号来获取通道信息
	 * 
	 * @param section
	 *            分段的名称
	 * @param channelID
	 *            通道序号
	 * @return 如果返回为空则表示没有这个通道
	 */
	public float[] getData(String section, int channelID,
			Vector<Vector<String>> allSoucePaths,
			Vector<CmtrCfgInfo> allCfgInfos)
	{
		int id = getIndexFromSections(section);// 减一是由于所有段的名称是从1开始的;
		Vector<String> soucePaths = allSoucePaths.get(id);
		CmtrCfgInfo cmtrCfgInfo = allCfgInfos.get(id);
		float[] temp = null;
		temp = ComtradeData.getOneChannelDataFromFiles(soucePaths, cmtrCfgInfo,
				channelID);// i是第几个通达+特征量的偏移
		return temp;
	}

	public float[] getWaveData(String section, int channelID)
	{
		return this.getData(section, channelID, allWaveSoucePaths,
				allWaveCfgInfos);
	}

	public float[] getAinData(String section, int channelID)
	{
		return this.getData(section, channelID, allAinSoucePaths,
				allAinCfgInfos);
	}


	/**
	 *获取键相信息 
	 * @param section  段名
	 * @return 序号  
	 */
	public Vector<Integer> getKeyIndexData(String section)
	{
		// 键相点是0号通道
		int id = getIndexFromSections(section);
		
		Vector<String> soucePaths = allKeyIndexSoucePaths.get(id);
		CmtrCfgInfo cmtrCfgInfo = allKeyIndexCfgInfos.get(id);
		float[] temp = null;
//		int channelCount = cmtrCfgInfo.channelCount;
//		for(int i =0 ; i< channelCount ; i++){
//			if(cmtrCfgInfo.getAnalogs().get(i).getElement().equals("6")){
//				temp = ComtradeData.getOneChannelDataFromFiles(soucePaths, cmtrCfgInfo, i);
//				break;
//			}
//		}
		temp = ComtradeData.getOneChannelDataFromFiles(soucePaths, cmtrCfgInfo,
				0);// i是第几个通达+特征量的偏移

		// 键相数据处理
		int fre = (int) cmtrCfgInfo.getSmprateRate();
		Vector<Integer> index = new Vector<Integer>();

		for (int i = 0; i < temp.length / fre; i++)
		{
			int count = (int) temp[fre * i];// 第一个为一个周期中键相点的数量

			for (int j = 1; j <= count; j++)
			{
				if (temp[fre * i + j] > fre)
				{
					index.add((int) (temp[fre * i + j] - fre + fre * i));
				} else
				{
					index.add((int) (temp[fre * i + j] + fre * i));
				}
			}
		}

		return index;

	}

	/**
	 * 由一个工作空间集合得到工作空间
	 * 
	 * @param work
	 *            集合
	 * @param station
	 *            站点名称
	 * @param test
	 *            实验名称
	 * @return
	 */
	public static WorkSpaceProp getWorkSpace(Vector<WorkSpaceProp> work,
			String station, String test)
	{
		for (int i = 0; i < work.size(); i++)
		{
			if (work.get(i).stationName.equals(station)
					&& work.get(i).testName.equals(test))
			{
				return work.get(i);
			}
		}
		return null;
	}

	// 根据段名来获取他的下标号，以便于获取其cfg文件
	public int getIndexFromSections(String sectionname)
	{
		for (int i = 0; i < sectionName.size(); i++)
		{
			if (sectionname.equals(sectionName.get(i)))
			{
				return i;
			}
		}
		return -1;
	}
	/**
	 * 获得通道位置，在该数据段中是第几个通道，以便获得analog的信息
	 * @param section 数据段
	 * @param channelName 通道名字
	 * @return 返回通道的位置
	 */
	public int getChannelIndex(String section,String channelName,Vector<CmtrCfgInfo> allWaveCfgInfos){
		int id;
		if(section == null){
			id = 0;
		}
		else
			id = getIndexFromSections(section);// 减一是由于所有段的名称是从1开始的
		
		CmtrCfgInfo cmtrCfgInfo = allWaveCfgInfos.get(id);
		for (int i = 0; i < cmtrCfgInfo.channelCount; i++)
		{

			if (channelName.equals(cmtrCfgInfo.channelName[i]))
			{

				return i;
			}
		}
		return -1;
		
	}
	
	public int getChannelIndex(String section,String channelName){
		return this.getChannelIndex(section, channelName,allWaveCfgInfos);
	}
	
	public int getChannelIndex(String channelName){
		return this.getChannelIndex(null, channelName,allWaveCfgInfos);
	}
}