package com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import com.nari.slsd.hms.hdu.common.comtrade.CmtrCfgInfo;
import com.nari.slsd.hms.hdu.common.comtrade.ComtradeData;
import com.nari.slsd.hms.hdu.common.util.ConfigUtil;
import com.nari.slsd.hms.hdu.common.util.LoggerUtil;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelect.OfflineDataSelectJPanel;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 根据工作空间来创建树
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public abstract class WorkSpaceTreeMenuPanel extends JPanel implements
		TreeSelectionListener
{
	public final static int TYPE_OPEN_SELECT = 0;// 表示的打开分拣后的文件夹
	public final static int TYPE_OPEN_ORIGIN = 1;// 表示是打开自动录波的文件夹
	private int type = 0;

	public JTree tree;
	private JScrollPane scrollpane;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode(
			HduChartUtil.getResource("OfflineWorkspace_TreeTitle"));
	private DefaultMutableTreeNode treeNode;
	DefaultMutableTreeNode node;
	Vector<WorkSpaceProp> workSpaceProps = new Vector<WorkSpaceProp>();
	String filePath;

	public void setType(int type)
	{
		this.type = type;
	}

	public Vector<WorkSpaceProp> getWorkSpaceProp()
	{
		return workSpaceProps;
	}

	public DefaultMutableTreeNode getDefaultMutableTreeNode()
	{
		return node;
	}

	public WorkSpaceTreeMenuPanel(int type)
	{
		this.type = type;
		setBackground(Color.white);

	}

	// public void clear()
	// {
	// root.removeAllChildren();
	// root.
	// workSpaceProps.clear();
	// scrollpane.updateUI();
	// this.updateUI();
	// }

	// 判断是否是有数据文件夹
	private boolean checkSelectFilePath(String path)
	{
		File file = new File(path);
		File[] files = file.listFiles();
		if (files.length == 0)
			return false;
		File[] chlidFiles = files[0].listFiles();

		Vector<String> names = new Vector<String>();
		names.add("ain");
		names.add("din");
		names.add("eig");
		names.add("wave");
		if (null == chlidFiles)// add lqj 1_27
			return false;
		for (File t : chlidFiles)
		{
			if (!names.contains(t.getName()))
			{
				return false;
			}
		}

		return true;
	}

	// 判断是否是有数据文件夹

	// 判断是否是有数据文件夹
	private boolean checkOriginFilePath(String path)
	{
		File file = new File(path + "\\定时");
		File[] chlidFiles = file.listFiles();
		if (null == chlidFiles)
		{
			return false;
		}
		Vector<String> names = new Vector<String>();
		names.add("ain");
		names.add("din");
		names.add("eig");
		names.add("wave");
		for (File t : chlidFiles)
		{
			if (!names.contains(t.getName()))
			{
				return false;
			}
		}
		return true;
	}

	public boolean init()
	{
		boolean breVal = true;
		if (TYPE_OPEN_SELECT == type)
		{
			breVal = getSelectPro();

		} else if (TYPE_OPEN_ORIGIN == type)
		{
			breVal = getOriginPro();
		}

		return breVal;
	}

	public boolean getSelectPro()
	{
		String pathString = ConfigUtil
				.getPropertiesValue(ConfigUtil.KEY_WORKSPACE_SELECTPATH);

		WorkSpaceProp workSpaceProp = new WorkSpaceProp();

		if (null == pathString)// 读取配置文件，如果没有则默认桌面
		{
			pathString = FileSystemView.getFileSystemView().getHomeDirectory()
					.getAbsolutePath();
		}
		JFileChooser fileChooser = new JFileChooser(pathString);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 选择的是文件
		int returnVal = fileChooser.showOpenDialog(fileChooser);

		if (JFileChooser.CANCEL_OPTION == returnVal)// 如果选择取消，则直接返回s
		{
			return false;
		}
		if (returnVal != JFileChooser.APPROVE_OPTION
				|| !fileChooser.getSelectedFile().getName()
						.equals(OfflineDataSelectJPanel.KEYNAME))
		{
			JOptionPane.showMessageDialog(this, "选择路径有误！");
			return false;
		}
		HashMap<String, String> map = new HashMap<String, String>();

		filePath = fileChooser.getSelectedFile().getAbsolutePath()
				.split(OfflineDataSelectJPanel.KEYNAME)[0];// 这个就是你选择的文件夹的路径

		map.put(ConfigUtil.KEY_WORKSPACE_SELECTPATH, filePath);
		ConfigUtil.updateProperties(map);// 更新配置文件

		SelectWSSearchDialog dialog = new SelectWSSearchDialog(workSpaceProp,
				filePath)
		{
			@Override
			public void commitHandle(String stationtype, String experType,
					String wavetype)
			{
				// TODO Auto-generated method stub

				String[] types = wavetype.split("_");
				String type = types[0] + "\\wave\\" + types[1];

				if (getSelectWorkSpace(workSpaceProp, filePath + "\\"
						+ stationtype + "\\" + experType, type))
				{
					Complete();
				}
			}

		};
		if (!dialog.init())
		{
			JOptionPane.showMessageDialog(this, "选择路径有误！");
			return false;
		}
		dialog.setVisible(true);

		return true;
	}

	private boolean getSelectWorkSpace(WorkSpaceProp workSpaceProp,
			String filePath, String waveType)
	{

		// 下面是获取工作空间
		workSpaceProp.workSpacePath = filePath;
		String[] namesStrings = filePath.split("\\\\");

		DefaultMutableTreeNode parentNode = root;
		// 获取站点和实验名称
		int IsSameCount = 0;
		for (int i = namesStrings.length - 2; i < namesStrings.length; i++)
		{
			int j;
			for (j = 0; j < parentNode.getChildCount(); j++)
			{
				if (parentNode.getChildAt(j).toString().equals(namesStrings[i]))
				{
					break;
				}
			}
			if (j == parentNode.getChildCount())// 没有相同的组
			{
				treeNode = new DefaultMutableTreeNode(namesStrings[i]);
				parentNode.add(treeNode);
				parentNode = treeNode;
			} else
			{
				parentNode = (DefaultMutableTreeNode) parentNode.getChildAt(j);
				IsSameCount++;// 如果站点名称和实验名称都相同则这个参数为2
			}

		}

		if (2 == IsSameCount)
		{
			return false;
		}

		workSpaceProp.stationName = namesStrings[namesStrings.length - 2];
		workSpaceProp.testName = namesStrings[namesStrings.length - 1];

		File[] sectionFiles = new File(filePath).listFiles();
		if (null == sectionFiles)
		{
			return false;
		}

		for (File f : sectionFiles)
		{
			// 将加入
			String[] temp = waveType.split("\\\\");
			String name = f.getName() + "-" + temp[0] + "-" + temp[2];

			if (!addNode(parentNode, f.getAbsolutePath() + "\\定时\\ain",
					f.getAbsolutePath() + "\\" + waveType, f.getAbsolutePath()
							+ "\\" + temp[0] + "\\" + temp[1] + "\\键相",
					workSpaceProp, name))
			{
				return false;
			}

		}

		// 将空间加入空间组
		workSpaceProps.add(workSpaceProp);

		// Vector<Integer> vector =
		// workSpaceProp.getKeyIndexData(workSpaceProp.sectionName.get(0));

		if (null != scrollpane)
		{
			this.remove(scrollpane);
			tree = new JTree(root);
			scrollpane = new JScrollPane(tree);
			this.add(scrollpane, BorderLayout.CENTER);
			tree.addTreeSelectionListener(this);
			this.updateUI();
		} else
		{
			tree = new JTree(root);
			scrollpane = new JScrollPane(tree);

			this.setLayout(new BorderLayout());
			this.add(scrollpane, BorderLayout.CENTER);
			tree.addTreeSelectionListener(this);
		}

		return true;

	}

	public boolean getOriginPro()
	{
		String pathString = ConfigUtil
				.getPropertiesValue(ConfigUtil.KEY_WORKSPACE_ORIGINPATH);

		WorkSpaceProp workSpaceProp = new WorkSpaceProp();

		if (null == pathString)// 读取配置文件，如果没有则默认桌面
		{
			pathString = FileSystemView.getFileSystemView().getHomeDirectory()
					.getAbsolutePath();
		}
		JFileChooser fileChooser = new JFileChooser(pathString);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 选择的是文件
		int returnVal = fileChooser.showOpenDialog(fileChooser);

		if (JFileChooser.CANCEL_OPTION == returnVal)// 如果选择取消，则直接返回s
		{
			return false;
		}

		if (returnVal != JFileChooser.APPROVE_OPTION
				|| !fileChooser.getSelectedFile().getName()
						.equals(OfflineDataSelectJPanel.KEYNAME))
		{
			JOptionPane.showMessageDialog(this, "选择路径有误！");
			return false;
		}
		HashMap<String, String> map = new HashMap<String, String>();

		filePath = fileChooser.getSelectedFile().getAbsolutePath()
				.split(OfflineDataSelectJPanel.KEYNAME)[0];// 这个就是你选择的文件夹的路径

		map.put(ConfigUtil.KEY_WORKSPACE_ORIGINPATH, filePath);
		ConfigUtil.updateProperties(map);// 更新配置文件

		OriginWSSearchDialog dialog = new OriginWSSearchDialog(filePath,
				workSpaceProp)
		{
			@Override
			public void commitHandle(String station, String work,
					Date startTime, Date endTime)
			{
				// TODO Auto-generated method stub

				File rootFile = new File(filePath);
				if (!rootFile.exists())
				{
					LoggerUtil.log(Level.WARNING, "rootFile is not exist");
					return;
				}
				File[] stationfiles = rootFile.listFiles();
				if (null == stationfiles)
				{
					LoggerUtil.log(Level.WARNING, "rootFile has not chlid");
					return;
				}

				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH-mm-ss");

				for (File stationFile : stationfiles)
				{
					if (station.equals(stationFile.getName()))
					{

						File[] expFiles = stationFile.listFiles();
						if (null == stationfiles)
						{
							continue;
						}

						for (File expFile : expFiles)
						{
							String[] expString = expFile.getName().split("_");
							if (2 == expString.length)
							{

								if (!expString[0].equals(work))
								{
									continue;
								}
								Date nowDate;
								try
								{
									nowDate = format.parse(expString[1]);

									if (nowDate.getTime() > startTime.getTime()
											&& nowDate.getTime() < endTime
													.getTime())
									{
										getWorkSpace(workSpaceProp,
												expFile.getAbsolutePath());
									}

								} catch (ParseException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
									LoggerUtil.log(Level.WARNING,
											e.getMessage());
								}

							}

						}
					}

				}
				
				if(workSpaceProp.allWaveCfgInfos.isEmpty())
				{
					LoggerUtil
					.log(Level.WARNING,
							"无文件，清检查数据是否正常");
				}else
				{
					if (100000 < workSpaceProp.allWaveCfgInfos.get(0).pointCount
							* workSpaceProp.allWaveCfgInfos.size())
					{
						JOptionPane.showMessageDialog(this,
								HduChartUtil.getResource("Center_OverSize"));
						LoggerUtil
								.log(Level.WARNING,
										"文件单通道中数量到达"
												+ (workSpaceProp.allWaveCfgInfos
														.get(0).pointCount * workSpaceProp.allWaveCfgInfos
														.size())
												+ HduChartUtil
														.getResource("Center_OverSize"));
					}
				}
				

				Complete();
			}

		};
		if (!dialog.init())
		{
			return false;
		}
		dialog.setVisible(true);
		return true;
	}

	private boolean getWorkSpace(WorkSpaceProp workSpaceProp, String filePath)
	{
		// 下面是获取工作空间
		workSpaceProp.workSpacePath = filePath;
		String[] namesStrings = filePath.split("\\\\");

		DefaultMutableTreeNode parentNode = root;
		// 获取站点和实验名称
		int IsSameCount = 0;
		for (int i = namesStrings.length - 2; i < namesStrings.length; i++)
		{
			int j;
			for (j = 0; j < parentNode.getChildCount(); j++)
			{
				if (parentNode.getChildAt(j).toString().equals(namesStrings[i]))
				{
					break;
				}
			}
			if (j == parentNode.getChildCount())// 没有相同的组
			{
				treeNode = new DefaultMutableTreeNode(namesStrings[i]);
				parentNode.add(treeNode);
				parentNode = treeNode;
			} else
			{
				parentNode = (DefaultMutableTreeNode) parentNode.getChildAt(j);
				IsSameCount++;// 如果站点名称和实验名称都相同则这个参数为2
			}

		}

		if (2 == IsSameCount)
		{
			return false;
		}
		workSpaceProp.stationName = namesStrings[namesStrings.length - 2];
		workSpaceProp.testName = namesStrings[namesStrings.length - 1];

		File rootfile = new File(filePath + "\\定时\\wave");
		File[] files = rootfile.listFiles();
		if (null != files)
		{
			for (File f : files)
			{
				String name = f.getName();
				if (name.equals("键相"))
				{
					continue;
				}
				if (!addNode(parentNode, filePath + "\\定时\\ain", filePath
						+ "\\定时\\wave\\" + name,

				filePath + "\\定时\\wave\\键相",

				workSpaceProp, "定时_" + name))
				{
					return false;
				}
			}
		}

		rootfile = new File(filePath + "\\整周期\\wave");
		files = rootfile.listFiles();
		if (null != files)
		{
			for (File f : files)
			{
				String name = f.getName();
				if (name.equals("键相"))
				{
					continue;
				}
				if (!addNode(parentNode, filePath + "\\定时\\ain", filePath
						+ "\\整周期\\wave\\" + name, filePath + "\\整周期\\wave\\键相",
						workSpaceProp, "整周期_" + name))
				{
					return false;
				}
			}
		}

		// 将空间加入空间组
		workSpaceProps.add(workSpaceProp);

		if (null != scrollpane)
		{
			this.remove(scrollpane);
			tree = new JTree(root);
			scrollpane = new JScrollPane(tree);
			this.add(scrollpane, BorderLayout.CENTER);
			tree.addTreeSelectionListener(this);
			this.updateUI();
		} else
		{
			tree = new JTree(root);
			scrollpane = new JScrollPane(tree);

			this.setLayout(new BorderLayout());
			this.add(scrollpane, BorderLayout.CENTER);
			tree.addTreeSelectionListener(this);
		}

		return true;

	}

	public abstract void Complete();// 当完成后触发

	/**
	 * 
	 * @param root
	 *            段名级节点
	 * @param ainfilepath
	 * @param wavefilepath
	 * @param keyfilepath
	 *            键相的文件夹路径
	 * @param workSpaceProp
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean addNode(DefaultMutableTreeNode root, String ainfilepath,
			String wavefilepath, String keyfilepath,
			WorkSpaceProp workSpaceProp, String typenamme)
	{
		treeNode = new DefaultMutableTreeNode(typenamme);

		Vector<String> wavesoucePaths = new Vector<String>();
		Vector<String> ainsoucePaths = new Vector<String>();
		Vector<String> keysoucePaths = new Vector<String>();

		File[] waveFile = new File(wavefilepath).listFiles();// 特征量
		if (null == waveFile)
		{
			return false;
		}

		File[] ainFile = new File(ainfilepath).listFiles();// 特征量
		if (null == ainFile)
		{
			return false;
		}

		File keyFile[] = new File(keyfilepath).listFiles();// 特征量
		if (null == keyFile)
		{
			return false;
		}

		Vector<String> wavenames = new Vector<String>();
		for (int i = 0; i < waveFile.length; i++)
		{
			if (!waveFile[i].isHidden())
			{
				wavenames.add(waveFile[i].getName());
			}
		}

		Vector<String> ainnames = new Vector<String>();
		for (int i = 0; i < ainFile.length; i++)
		{
			if (!ainFile[i].isHidden())
			{
				ainnames.add(ainFile[i].getName());
			}
		}

		Vector<String> keynames = new Vector<String>();
		for (int i = 0; i < keyFile.length; i++)
		{
			if (!keyFile[i].isHidden())
			{
				keynames.add(keyFile[i].getName());
			}
		}

		for (int i = 0; i < wavenames.size(); i++)
		{
			if (wavenames.get(i).matches(".*[.]cfg"))
			{
				wavesoucePaths.add((waveFile[0].getParentFile()
						.getAbsolutePath() + "\\" + wavenames.get(i))
						.split("[.]cfg")[0]);
			}
			if (ainnames.get(i).matches(".*[.]cfg"))
			{
				ainsoucePaths.add((ainFile[0].getParentFile().getAbsolutePath()
						+ "\\" + ainnames.get(i)).split("[.]cfg")[0]);
			}
			if (keynames.get(i).matches(".*[.]cfg"))
			{
				keysoucePaths.add((keyFile[0].getParentFile().getAbsolutePath()
						+ "\\" + keynames.get(i)).split("[.]cfg")[0]);
			}
		}

		if (wavesoucePaths.isEmpty() || ainsoucePaths.isEmpty()
				|| keysoucePaths.isEmpty())
		{
			// 异常处理
			return false;
		}

		workSpaceProp.sectionName.add(typenamme);
		workSpaceProp.allWaveSoucePaths.add(wavesoucePaths);
		workSpaceProp.allAinSoucePaths.add(ainsoucePaths);
		workSpaceProp.allKeyIndexSoucePaths.add(keysoucePaths);

		CmtrCfgInfo cmtrCfgInfo = ComtradeData
				.getCmtrCfgInfoFromFiles(wavesoucePaths);// 默认获取第一个文件相关数据

		workSpaceProp.allWaveCfgInfos.add(cmtrCfgInfo);
		workSpaceProp.allAinCfgInfos.add(ComtradeData
				.getCmtrCfgInfoFromFiles(ainsoucePaths));
		workSpaceProp.allKeyIndexCfgInfos.add(ComtradeData
				.getCmtrCfgInfoFromFiles(keysoucePaths));

		int channelCount = cmtrCfgInfo.getAnalogCount();

		try
		{
			DefaultMutableTreeNode[] treeNodes = new DefaultMutableTreeNode[channelCount];

			for (int i = 0; i < channelCount; i++)
			{
				treeNodes[i] = new DefaultMutableTreeNode(cmtrCfgInfo
						.getAnalogs().get(i).getName());
				treeNode.add(treeNodes[i]);
			}

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

		root.add(treeNode);
		return true;
	}

	/**
	 * 创建树枝
	 * 
	 * @param parentNode
	 * @param childFile
	 */
	private void creatTreeNode(DefaultMutableTreeNode parentNode,
			File childFile, WorkSpaceProp workSpaceProp)
	{
		Vector<String> wavesoucePaths = new Vector<String>();
		Vector<String> ainsoucePaths = new Vector<String>();

		File waveFile = new File(childFile.getAbsolutePath() + "\\wave");// 特征量
		String[] wavenames = waveFile.list();
		File ainFile = new File(childFile.getAbsolutePath() + "\\ain");// 特征量
		String[] ainnames = ainFile.list();

		for (int i = 0; i < wavenames.length; i++)
		{
			if (wavenames[i].matches(".*[.]cfg"))
			{
				wavesoucePaths
						.add((waveFile.getAbsolutePath() + "\\" + wavenames[i])
								.split("[.]")[0]);
			}
			if (ainnames[i].matches(".*[.]cfg"))
			{
				ainsoucePaths
						.add((ainFile.getAbsolutePath() + "\\" + ainnames[i])
								.split("[.]")[0]);
			}
		}

		if (wavesoucePaths.isEmpty() || ainsoucePaths.isEmpty())
		{
			// 异常处理
		}

		workSpaceProp.allWaveSoucePaths.add(wavesoucePaths);
		workSpaceProp.allAinSoucePaths.add(ainsoucePaths);

		CmtrCfgInfo cmtrCfgInfo = ComtradeData
				.getCmtrCfgInfoFromFiles(wavesoucePaths);// 默认获取第一个文件相关数据

		workSpaceProp.allWaveCfgInfos.add(cmtrCfgInfo);
		workSpaceProp.allAinCfgInfos.add(ComtradeData
				.getCmtrCfgInfoFromFiles(ainsoucePaths));

		int channelCount = cmtrCfgInfo.getAnalogCount();

		try
		{
			DefaultMutableTreeNode[] treeNodes = new DefaultMutableTreeNode[channelCount];

			for (int i = 0; i < channelCount; i++)
			{
				treeNodes[i] = new DefaultMutableTreeNode(cmtrCfgInfo
						.getAnalogs().get(i).getName());
				parentNode.add(treeNodes[i]);
			}

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
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
		if (null == e.getNewLeadSelectionPath())
			return;
		node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath()
				.getLastPathComponent();

		if (node == null)
			return;

		if (node.isLeaf())
		{
			treeSelectionHandle(node.getParent().getParent().getParent()
					.toString()
					+ ","
					+ node.getParent().getParent().toString()
					+ ","
					+ node.getParent().toString() + "," + node.toString());
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
}
