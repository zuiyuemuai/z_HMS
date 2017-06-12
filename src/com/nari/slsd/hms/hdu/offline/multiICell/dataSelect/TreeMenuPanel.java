package com.nari.slsd.hms.hdu.offline.multiICell.dataSelect;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.swt.widgets.Tree;

import com.nari.slsd.hms.hdu.common.comtrade.CmtrCfgInfo;
import com.nari.slsd.hms.hdu.common.comtrade.ComtradeData;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.util.ConfigUtil;
import com.nari.slsd.hms.hdu.common.util.LoggerUtil;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 待分拣的数据生成的树
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public abstract class TreeMenuPanel extends JPanel implements
		TreeSelectionListener
{
	public JTree tree;
	private JScrollPane scrollpane;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode(
			HduChartUtil.getResource("OfflineDataSelect_TreeTitle"));
	private DefaultMutableTreeNode treeNode;

	public CmtrCfgInfo cmtrCfgInfo = null;

	private String filePath;// 总文件加路径

	public String channelName;//当前正在显示的通道名
	
	public CmtrCfgInfo getCmtrCfgInfo()
	{
		return cmtrCfgInfo;
	}

	// 所有的文件路径
	protected Vector<String> soucePaths = new Vector<String>();

	public TreeMenuPanel(String filePath)
	{
		this.filePath = filePath;
		setBackground(Color.white);

	}
	
	public boolean init()
	{
		// TODO Auto-generated method stub
		
		String[] namesStrings = filePath.split("\\\\");
		if (null == namesStrings)
		{
			LoggerUtil.log(Level.WARNING, "filePath is null");
			return false;
		}

		DefaultMutableTreeNode parentNode = root;
		for (int i = namesStrings.length - 2; i < namesStrings.length; i++)
		{
			treeNode = new DefaultMutableTreeNode(namesStrings[i]);
			parentNode.add(treeNode);
			parentNode = treeNode;
		}

		File eigFile = new File(filePath + "\\定时\\eig");// 特征量

		if (!eigFile.exists())
		{
			LoggerUtil.log(Level.WARNING, "定时\\eigFile is not exist");
			return false;
		}

		File[] eigCfgFiles = eigFile.listFiles();
		if (null == eigCfgFiles)
		{
			LoggerUtil.log(Level.WARNING, "定时\\eigFile has not chlid");
			return false;
		}

		tree = new JTree(root);
		scrollpane = new JScrollPane(tree);

		super.setLayout(new BorderLayout());
		super.add(scrollpane, BorderLayout.CENTER);

		for (File f : eigCfgFiles)
		{
			if (f.getName().matches(".*[.]cfg") && !f.isHidden())//modified by lqj 2015/6/8 解决隐藏文件造成的无法加载问题
			{
				soucePaths.add((f.getAbsolutePath()).split("[.]cfg")[0]);
			}
		}

		if (soucePaths.isEmpty())
		{
			// 异常处理
			LoggerUtil.log(Level.INFO, "Select File has not cfg");
		}

		cmtrCfgInfo = ComtradeData.getCmtrCfgInfoFromFiles(soucePaths);// 默认获取第一个文件相关数据

		if(cmtrCfgInfo == null)
		{
			return false;
		}
		creatTree(parentNode, cmtrCfgInfo);

		tree.addTreeSelectionListener(this);
		
		return true;
	}

	private void creatTree(DefaultMutableTreeNode root, CmtrCfgInfo cmtrCfgInfo)
	{
		int channelCount = cmtrCfgInfo.getAnalogCount();

		try
		{
			//为什么要/5?
			DefaultMutableTreeNode[] treeNodes = new DefaultMutableTreeNode[channelCount / 5];

			for (int i = 0; i < channelCount / 5; i++)
			{
				treeNodes[i] = new DefaultMutableTreeNode(cmtrCfgInfo
						.getAnalogs().get(i * 5).getName());
				root.add(treeNodes[i]);
			}

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
			LoggerUtil.log(Level.WARNING, e.getMessage());
		}

	}

	/**
	 * Describe :JTree这个控件中Item选中的监听器，�?中不同的Item，便显示不同通道的波�?
	 * 入口参数:传入触发的事件，就是右键菜单中被选中的项目事�? 返回值：�?
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		// TODO Auto-generated method stub

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
				.getNewLeadSelectionPath().getLastPathComponent();

		if (node == null)
			return;
		if (node.isLeaf())
		{
			channelName = node.toString();
			treeSelectionHandle(node.toString());
		}

	}

	/**
	 * 外部树的处理
	 * 
	 * @param e
	 *            树的事件
	 * @param cmtrContent
	 *            cmtr的内容
	 */
	public abstract void treeSelectionHandle(String name);

	public PlaneXY updateLineChartData(String name, int Feature)
	{
		float div = 1;
		String value = ConfigUtil.getPropertiesValue(ConfigUtil.KEY_SELECTDIV);
		if(null == value)
		{
			LoggerUtil.log(Level.WARNING, ConfigUtil.KEY_SELECTDIV+" not exit, using 1");
			div = 1f;
		}else {
			div = new Float(value);
		}
		
		
		float[] tempX = new float[(int) (cmtrCfgInfo.pointCount/div)];
		float[] tempY = new float[(int) (cmtrCfgInfo.pointCount/div)];

		PlaneXY planeXY = new PlaneXY();

		for (int i = 0; i < cmtrCfgInfo.channelCount; i += 5)
		{

			if (name.equals(cmtrCfgInfo.channelName[i]))
			{

				float[] tY = ComtradeData.getOneChannelDataFromFiles(soucePaths,
						cmtrCfgInfo, i + Feature);// i是第几个通达+特征量的偏移

				for (int j = 0; j < (int)(cmtrCfgInfo.pointCount/div); j++)//zgw2015年6月8日16:16:09
				{
					tempX[j] = j;
					tempY[j] = tY[(int) (div*j)];
				}
				planeXY.setX(tempX);
				planeXY.setY(tempY);

				break;
			}
		}

		return planeXY;

	}

}
