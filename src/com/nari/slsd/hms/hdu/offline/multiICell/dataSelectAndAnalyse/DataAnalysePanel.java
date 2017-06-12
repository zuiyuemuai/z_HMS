package com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.jfree.ui.RefineryUtilities;

import com.nari.slsd.hms.hdu.common.algorithm.CalResult;
import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.comtrade.CmtrCfgInfo;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.util.ConfigHelp;
import com.nari.slsd.hms.hdu.common.util.LoggerUtil;
import com.nari.slsd.hms.hdu.offline.multiICell.airGapAnalyse.AirGapAnalysePanel;
import com.nari.slsd.hms.hdu.offline.multiICell.axesorbit.AxesorbitPropDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.axesorbit.OfflineAxesorbitJpanel;
import com.nari.slsd.hms.hdu.offline.multiICell.balanceAnalyse.BalanceAnalyse;
import com.nari.slsd.hms.hdu.offline.multiICell.bode.OfflineBodePanel;
import com.nari.slsd.hms.hdu.offline.multiICell.cascad.CascadPropDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.cascad.OfflineCascadPanel;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelect.OfflineDataSelectJPanel;
import com.nari.slsd.hms.hdu.offline.multiICell.historySearch.OfflineHistorySearchPanel;
import com.nari.slsd.hms.hdu.offline.multiICell.magneticBalance.MagneticBalance;
import com.nari.slsd.hms.hdu.offline.multiICell.panChe.PanChePanel;
import com.nari.slsd.hms.hdu.offline.multiICell.polar.OfflinePolarPanel;
import com.nari.slsd.hms.hdu.offline.multiICell.spatialAxis.OfflineSpatialAxisJpanel;
import com.nari.slsd.hms.hdu.offline.multiICell.spatialAxis.SpatialAxisPropDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse.OfflineTrendAnalysisPanel;
import com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse.TrendAnalysePropDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.waterfall.OffLineWaterfallJpanel;
import com.nari.slsd.hms.hdu.offline.multiICell.waterfall.WaterfallPropDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse.OfflineWaveAnalysePanel;
import com.nari.slsd.hms.hdu.online.OnlineICellInterface;
import com.nari.slsd.hms.hdu.online.multiICell.airgap.OnlineAirGapJpanel;
import com.nari.slsd.hms.hdu.online.multiICell.airgapbar.OnlineAirGapBar;
import com.nari.slsd.hms.hdu.online.multiICell.axesorbit.OnlineAxesorbitJPanel;
import com.nari.slsd.hms.hdu.online.multiICell.partialDischarge.OnlinePartialJpanel;
import com.nari.slsd.hms.hdu.online.multiICell.powerEggGraph.OnlinePowerEggGraphPanel;
import com.nari.slsd.hms.hdu.online.multiICell.spatialAxis.OnlineSpatialAxisJpanel;
import com.nari.slsd.hms.hdu.online.multiICell.spectrogram.OnlineSpectrogramPanel;
import com.nari.slsd.hms.hdu.online.multiICell.stattable.Stattablepanel;
import com.nari.slsd.hms.hdu.online.multiICell.waterfall.OnlineWaterfallJpanel;
import com.nari.slsd.hms.hdu.online.multiICell.waveform.OnlineWaveformPanel;
import com.nari.slsd.hms.hdu.serverManger.view.NodeMange;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 南瑞水电站监护系统离线主界面 是一个桌面，上面菜单栏选择功能
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class DataAnalysePanel extends JPanel implements WindowListener
{

	// private Font textFont = new Font(
	// PropertiesUtil.getPropertiesValue(PropertiesUtil.KEY_T`EXT_FONT),
	// Integer.parseInt(PropertiesUtil
	// .getPropertiesValue(PropertiesUtil.KEY_TEXT_FONT_STYLE)),
	// Integer.parseInt(PropertiesUtil
	// .getPropertiesValue(PropertiesUtil.KEY_TEXT_FONT_SIZE)));

	private boolean onLineSwitch = ConfigHelp.getOnlineSwitch();
	private boolean serverNodeSwitch = ConfigHelp.getServerNodeSwitch();


	private Font textFont = new Font("宋体", Font.PLAIN, 20);

	private Vector<WorkSpaceProp> workSpaceProps = new Vector<WorkSpaceProp>();

	/** 声明窗口菜单 */
	public OperationMenu operationMenu;

	private OfflineWaveAnalysePanel analyseWavePanel;

	private JPanel thisJPanel = this;

	/** 轴心轨迹 */
	private OfflineAxesorbitJpanel wareForm;

	/** 三维姿态图 */
	private OfflineSpatialAxisJpanel postureJpanel;

	/** 三维瀑布图 */
	private OffLineWaterfallJpanel waterfalljJpanel;

	/** 分拣图 */
	private OfflineDataSelectJPanel sortingPanel;

	/** 级联图 **/
	private OfflineCascadPanel cascadPanel;

	/** 动平衡分析图 **/
	private BalanceAnalyse banlanceAnalysePanel;

	/** 盘车试验 **/
	private PanChePanel panchePanel;

	/** 磁拉力平衡分析 **/
	private MagneticBalance magneticPanel;

	/** 伯德分析 **/
	private OfflineBodePanel bodePanel;

	/** 极坐标分析 **/
	private OfflinePolarPanel polarPanel;

	/** 趋势分析 **/
	private OfflineTrendAnalysisPanel trendAnalysisPanel;

	/** 离线气隙图 **/
	private AirGapAnalysePanel airGapAnalysePanel;

	/** 历史查询 **/
	private OfflineHistorySearchPanel historySearchPanel;

	private OnlineAirGapJpanel onlineAirGapJpanel;
	private OnlineAxesorbitJPanel onlineAxesorbitJPanel;
	private OnlineAirGapBar onlineAirGapBarPanel;
	private OnlinePartialJpanel onlinePartialJpanel;
	private OnlinePowerEggGraphPanel onlinePowerEggGraphPanel;
	private OnlineSpatialAxisJpanel onlineSpatialAxisJpanel;
	private OnlineSpectrogramPanel onlineSpectrogramPanel;
	private OnlineWaterfallJpanel onlineWaterfallJpanel;
	private OnlineWaveformPanel onlineWaveformPanell;

	private Stattablepanel stattablepanel;

	private WorkSpaceTreeMenuPanel treeMenuPanel = null;

	private JDesktopPane desktopPane;// 桌面

	private final int TreeMenuWide = 200;

	/** 命令 **/
	public static final String CMD_ANALYSEOPEN_SELECTFILE = "分析打开检波文件";
	public static final String CMD_ANALYSEOPEN_ORIGINFILE = "分析打开录波文件";
	public static final String CMD_ANALYSEOPEN_CLEAE = "清除空间";

	public static final String CMD_SORTINGOPENFILE = "分拣打开文件";

	public static final String CMD_NEWPOSUTRE = "新建姿态图";
	public static final String CMD_NEWWATERFALL = "新建三维瀑布图";
	public static final String CMD_NEWCENTER_TRACE = "新建轴心轨迹图";
	public static final String CMD_NEWCASCAD = "新建级联图";
	public static final String CMD_NEWBODE = "新建伯德图";
	public static final String CMD_NEWPOLAR = "新建极坐标图";
	public static final String CMD_NEWTREND = "新建趋势图";
	public static final String CMD_NEWBALANCE = "新建动平衡";
	public static final String CMD_NEWEFFICIENCY = "新建工况图";
	public static final String CMD_NEWPANCHE = "新建盘车试验";
	public static final String CMD_NEWMAGNETIC = "新建磁拉力平衡分析";
	public static final String CMD_NEWHISTORY = "新建历史查询";
	public static final String CMD_NEWAIRGAPANALYSE = "新建气隙图";

	public static final String CMD_ONLINE_AirGap = "ONLINE_AirGap";
	public static final String CMD_ONLINE_Axesorbit = "ONLINE_Axesorbit";
	public static final String CMD_ONLINE_AirGapBar = "ONLINE_AirGapBar";
	public static final String CMD_ONLINE_Partial = "ONLINE_Partial";
	public static final String CMD_ONLINE_PowerEgg = "ONLINE_PowerEgg";
	public static final String CMD_ONLINE_SpatialAxis = "ONLINE_SpatialAxis";
	public static final String CMD_ONLINE_Spectrogram = "ONLINE_Spectrogram";
	public static final String CMD_ONLINE_Waterfall = "ONLINE_Waterfall";
	public static final String CMD_ONLINE_Waveform = "ONLINE_Waveform";

	public static final String CMD_ONLINE_Stattable = "ONLINE_Stattable";

	public static final String CMD_SEVERNODE = "CMD_SEVERNODE";// 打开节点配置

	public DataAnalysePanel()
	{
		this.setLayout(new BorderLayout());
		/** 创建窗口菜单 */
		operationMenu = new OperationMenu();
		this.add(operationMenu, BorderLayout.NORTH);

		/** 在窗口菜单中添加监听器 */
		operationMenu.servernodItem.addActionListener(menuActionListener);
		operationMenu.servernodItem.setActionCommand(CMD_SEVERNODE);

		operationMenu.openSelectFileItem.addActionListener(menuActionListener);
		operationMenu.openSelectFileItem
				.setActionCommand(CMD_ANALYSEOPEN_SELECTFILE);

		operationMenu.openOrigFileItem.addActionListener(menuActionListener);
		operationMenu.openOrigFileItem
				.setActionCommand(CMD_ANALYSEOPEN_ORIGINFILE);

		operationMenu.clearWSItem.addActionListener(menuActionListener);
		operationMenu.clearWSItem.setActionCommand(CMD_ANALYSEOPEN_CLEAE);

		operationMenu.newCenterTraceItem.addActionListener(menuActionListener);
		operationMenu.newCenterTraceItem.setActionCommand(CMD_NEWCENTER_TRACE);

		operationMenu.newPostureItem.addActionListener(menuActionListener);
		operationMenu.newPostureItem.setActionCommand(CMD_NEWPOSUTRE);

		operationMenu.newWaterfallItem.addActionListener(menuActionListener);
		operationMenu.newWaterfallItem.setActionCommand(CMD_NEWWATERFALL);

		operationMenu.opensortingItem.addActionListener(menuActionListener);
		operationMenu.opensortingItem.setActionCommand(CMD_SORTINGOPENFILE);

		operationMenu.newCascadItem.addActionListener(menuActionListener);
		operationMenu.newCascadItem.setActionCommand(CMD_NEWCASCAD);

		operationMenu.newBodeItem.addActionListener(menuActionListener);
		operationMenu.newBodeItem.setActionCommand(CMD_NEWBODE);

		operationMenu.newPolarItem.addActionListener(menuActionListener);
		operationMenu.newPolarItem.setActionCommand(CMD_NEWPOLAR);

		operationMenu.newTrendItem.addActionListener(menuActionListener);
		operationMenu.newTrendItem.setActionCommand(CMD_NEWTREND);

		operationMenu.newBalanceItem.addActionListener(menuActionListener);
		operationMenu.newBalanceItem.setActionCommand(CMD_NEWBALANCE);

		operationMenu.newPanCheItem.addActionListener(menuActionListener);
		operationMenu.newPanCheItem.setActionCommand(CMD_NEWPANCHE);

		operationMenu.newMagneticItem.addActionListener(menuActionListener);
		operationMenu.newMagneticItem.setActionCommand(CMD_NEWMAGNETIC);

		operationMenu.newAirGapItem.addActionListener(menuActionListener);
		operationMenu.newAirGapItem.setActionCommand(CMD_NEWAIRGAPANALYSE);

		operationMenu.newHistroyItem.addActionListener(menuActionListener);
		operationMenu.newHistroyItem.setActionCommand(CMD_NEWHISTORY);

		operationMenu.onlineAirGapItem.addActionListener(menuActionListener);
		operationMenu.onlineAirGapItem.setActionCommand(CMD_ONLINE_AirGap);

		operationMenu.onlineAirGapBarItem.addActionListener(menuActionListener);
		operationMenu.onlineAirGapBarItem
				.setActionCommand(CMD_ONLINE_AirGapBar);

		operationMenu.onlineAxesorbitItem.addActionListener(menuActionListener);
		operationMenu.onlineAxesorbitItem
				.setActionCommand(CMD_ONLINE_Axesorbit);

		operationMenu.onlineWaterfallItem.addActionListener(menuActionListener);
		operationMenu.onlineWaterfallItem
				.setActionCommand(CMD_ONLINE_Waterfall);

		operationMenu.onlinePartialItem.addActionListener(menuActionListener);
		operationMenu.onlinePartialItem.setActionCommand(CMD_ONLINE_Partial);

		operationMenu.onlinePowerEggGraphItem
				.addActionListener(menuActionListener);
		operationMenu.onlinePowerEggGraphItem
				.setActionCommand(CMD_ONLINE_PowerEgg);

		operationMenu.onlineSpatialAxisItem
				.addActionListener(menuActionListener);
		operationMenu.onlineSpatialAxisItem
				.setActionCommand(CMD_ONLINE_SpatialAxis);

		operationMenu.onlineWaveformItem.addActionListener(menuActionListener);
		operationMenu.onlineWaveformItem.setActionCommand(CMD_ONLINE_Waveform);

		operationMenu.onlineStattableItem.addActionListener(menuActionListener);
		operationMenu.onlineStattableItem
				.setActionCommand(CMD_ONLINE_Stattable);

		operationMenu.onlineSpectrogramItem
				.addActionListener(menuActionListener);
		operationMenu.onlineSpectrogramItem
				.setActionCommand(CMD_ONLINE_Spectrogram);

		desktopPane = new JDesktopPane();
		this.add(desktopPane, BorderLayout.CENTER);
	}

	private JInternalFrame TreeinternalFrame = null;

	private void clearWSStart()
	{
		workSpaceProps.clear();
		try
		{
			if(null != TreeinternalFrame)
				TreeinternalFrame.setClosed(true);
			TreeinternalFrame = null;
		} catch (PropertyVetoException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			LoggerUtil.log(
					Level.SEVERE,
					"close treeInernal err when clearWorkSpace "
							+ e.getMessage());
		}

	}

	/**
	 * 开始波形分析， 树打开
	 */
	private void workspaceOpenStart(int type)
	{

		if (workSpaceProps.isEmpty())
		{
			treeMenuPanel = new WorkSpaceTreeMenuPanel(type)
			{
				@Override
				public void treeSelectionHandle(String name)
				{
					// TODO Auto-generated method stub
					// station test section node
					String[] names = name.split(",");

					WorkSpaceProp prop = WorkSpaceProp.getWorkSpace(
							workSpaceProps, names[0], names[1]);
					if (null != prop)
					{
						waveDanalyseStart(names[3],

						prop.allWaveCfgInfos.get(prop
								.getIndexFromSections(names[2])),
								prop.allWaveSoucePaths.get(prop
										.getIndexFromSections(names[2])));// 本來沒有-1，運行后報錯

					}
				}

				@Override
				public void Complete()
				{
					// TODO Auto-generated method stub

					complite();

					// 产生一个可关闭、可改变大小、具有标题、可最大化与最小化的Internal
					// Frame.
					if (null != TreeinternalFrame)
					{
						return;
					}
					TreeinternalFrame = new JInternalFrame("Tree", true, true,
							false, true);

					TreeinternalFrame.setLocation(0, 0);
					TreeinternalFrame.setSize(
							TreeMenuWide,
							desktopPane.getHeight() - 20 > 0 ? desktopPane
									.getHeight() - 30 : 0);

					TreeinternalFrame.add(treeMenuPanel);
					desktopPane.add(TreeinternalFrame, "Center");
					TreeinternalFrame.setVisible(true);

					// 添加对于关闭的监听为了关闭动画线程的资源
					InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
					{

						public void internalFrameClosed(InternalFrameEvent e)
						{
							// after you close it.
							clearWSStart();
						}

					};
					TreeinternalFrame.addInternalFrameListener(frameAdapter);

				}
			};

			if (!treeMenuPanel.init())
				return;

		} else
		{
			treeMenuPanel.setType(type);
			if (!treeMenuPanel.init())
				return;
		}

	}

	private void complite()
	{
		workSpaceProps = treeMenuPanel.getWorkSpaceProp();// 获取工作空间信息
	}

	private void waveDanalyseStart(String name, CmtrCfgInfo cmtrCfgInfo,
			Vector<String> soucePaths)
	{
		// 产生一个可关闭、可改变大小、具有标题、可最大化与最小化的Internal Frame.
		JInternalFrame internalFrame = new JInternalFrame("waveAnalyse", true,
				true, true, true);

		internalFrame.setLocation(TreeMenuWide, 0);
		internalFrame.setSize(
				desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
						.getWidth() - TreeMenuWide : 0,
				desktopPane.getHeight() - 20 > 0 ? desktopPane.getHeight() - 30
						: 0);

		analyseWavePanel = new OfflineWaveAnalysePanel(workSpaceProps, name,
				cmtrCfgInfo, soucePaths,
				treeMenuPanel.getDefaultMutableTreeNode());

		internalFrame.add(analyseWavePanel);

		desktopPane.add(internalFrame, "Center");
		internalFrame.setVisible(true);

	}

	private void AxesorbitStart()
	{
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
					HduChartUtil.getResource("Centre_PleaseAddWorkspace"));
			return;
		}

		AxesorbitPropDialog dialog = new AxesorbitPropDialog(workSpaceProps)
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void CommitHandle(Vector<JComboBox> boxs)
			{
				// TODO Auto-generated method stub
				WorkSpaceProp workSpaceProp = WorkSpaceProp.getWorkSpace(
						workSpaceProps, (String) boxs.get(0).getSelectedItem(),
						(String) boxs.get(1).getSelectedItem());
				String section = (String) boxs.get(2).getSelectedItem();
				String[] gallerys = new String[] {
						(String) boxs.get(3).getSelectedItem(),
						(String) boxs.get(4).getSelectedItem() };
				JInternalFrame internalFrame = new JInternalFrame("Axesorbit",
						true, true, true, true);
				internalFrame.setLocation(TreeMenuWide, 0);
				internalFrame.setSize(
						desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
								.getWidth() - TreeMenuWide : 0,
						desktopPane.getHeight() - 20 > 0 ? desktopPane
								.getHeight() - 30 : 0);
				String[] unit = new String[2];// X,Y轴的单位
				unit[0] = workSpaceProp.allWaveCfgInfos
						.get(workSpaceProp.getIndexFromSections(section))
						.getAnalogs()
						.get(workSpaceProp.getChannelIndex(section,
								gallerys[0], workSpaceProp.allWaveCfgInfos))
						.getUnit();
				unit[1] = workSpaceProp.allWaveCfgInfos
						.get(workSpaceProp.getIndexFromSections(section))
						.getAnalogs()
						.get(workSpaceProp.getChannelIndex(section,
								gallerys[1], workSpaceProp.allWaveCfgInfos))
						.getUnit();
				float[] xdata;
				float[] ydata;
				xdata = workSpaceProp.getWaveData(section, gallerys[0]);
				ydata = workSpaceProp.getWaveData(section, gallerys[1]);

				wareForm = new OfflineAxesorbitJpanel(xdata, ydata,
						gallerys[0], gallerys[1], unit[0], unit[1]);

				internalFrame.add(wareForm);
				desktopPane.add(internalFrame, "Center");
				internalFrame.setVisible(true);
			}

			@Override
			public void CommitHandle(JTable table)
			{
				// TODO Auto-generated method stub

			}

		};
		dialog.setVisible(true);

	}

	private void postureStart()
	{

		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
					HduChartUtil.getResource("Centre_PleaseAddWorkspace"));
			return;
		}

		SpatialAxisPropDialog dialog = new SpatialAxisPropDialog(workSpaceProps)
		{

			@Override
			public void CommitHandle(Vector<JComboBox> boxs)
			{
				WorkSpaceProp workSpaceProp = WorkSpaceProp.getWorkSpace(
						workSpaceProps, (String) boxs.get(0).getSelectedItem(),
						(String) boxs.get(1).getSelectedItem());
				JInternalFrame internalFrame = new JInternalFrame("Posture ",
						true, true, true, false);
				internalFrame.setLocation(TreeMenuWide, 0);
				internalFrame.setSize(
						desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
								.getWidth() - TreeMenuWide : 0,
						desktopPane.getHeight() - 20 > 0 ? desktopPane
								.getHeight() - 30 : 0);

				String section = (String) boxs.get(2).getSelectedItem();

				float[][] data = new float[6][];

				for (int i = 0; i < 6; i++)
				{
					data[i] = workSpaceProp.getWaveData(section, (String) boxs
							.get(3 + i).getSelectedItem());
				}
				String[] names = new String[6];
				for (int i = 0; i < names.length; i++)
				{
					names[i] = (String) boxs.get(3 + i).getSelectedItem();
				}

				String[] unit = new String[6];
				for (int j = 0; j < 6; j++)
				{
					unit[j] = workSpaceProp.allWaveCfgInfos
							.get(workSpaceProp.getIndexFromSections(section))
							.getAnalogs()
							.get(workSpaceProp.getChannelIndex(section,
									names[j], workSpaceProp.allWaveCfgInfos))
							.getUnit();
				}
				postureJpanel = new OfflineSpatialAxisJpanel(names, data, unit);
				internalFrame.add(postureJpanel.getJPanel());
				desktopPane.add(internalFrame, "Center");
				internalFrame.setVisible(true);

				// 添加对于关闭的监听为了关闭动画线程的资源
				InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
				{

					public void internalFrameClosed(InternalFrameEvent e)
					{
						// after you close it.
						postureJpanel.close();
						postureJpanel = null;
					}

				};
				internalFrame.addInternalFrameListener(frameAdapter);

			}

			@Override
			public void CommitHandle(JTable table)
			{
				// TODO Auto-generated method stub

			}
		};

		dialog.setVisible(true);

	}

	private void waterfallStart()
	{
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
					HduChartUtil.getResource("Centre_PleaseAddWorkspace"));
			return;
		}
		WaterfallPropDialog dialog = new WaterfallPropDialog(workSpaceProps)
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void CommitHandle(Vector<JComboBox> boxs)
			{
				JInternalFrame internalFrame = new JInternalFrame("Waterfall ",
						true, true, true, false);
				internalFrame.setLocation(TreeMenuWide, 0);
				internalFrame.setSize(
						desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
								.getWidth() - TreeMenuWide : 0,
						desktopPane.getHeight() - 20 > 0 ? desktopPane
								.getHeight() - 30 : 0);

				HashMap<String, Object> map = getDataMap(boxs);
				@SuppressWarnings("unchecked")
				Vector<PlaneXY> planeXYs = (Vector<PlaneXY>) map.get("data");
				float[] ref = (float[]) map.get("ref");

				waterfalljJpanel = new OffLineWaterfallJpanel(planeXYs, ref,
						(String) boxs.get(2).getSelectedItem(), (String) boxs
								.get(3).getSelectedItem());
				internalFrame.add(waterfalljJpanel);
				desktopPane.add(internalFrame, "Center");
				internalFrame.setVisible(true);

				// 添加对于关闭的监听为了关闭动画线程的资源
				InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
				{

					public void internalFrameClosed(InternalFrameEvent e)
					{
						// after you close it.
						waterfalljJpanel.close();
						waterfalljJpanel = null;

					}

				};
				internalFrame.addInternalFrameListener(frameAdapter);
			}

			@Override
			public void CommitHandle(JTable table)
			{
				// TODO Auto-generated method stub

			}
		};
		dialog.setVisible(true);

	}

	/**
	 * 分拣开始
	 */
	private void selectStart()
	{

		sortingPanel = new OfflineDataSelectJPanel()
		{

			@Override
			public void Complete()
			{
				// TODO Auto-generated method stub
				JInternalFrame internalFrame = new JInternalFrame("Select ",
						true, true, true, true);

				internalFrame.setLocation(0, 0);
				internalFrame.setSize(
						desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
								.getWidth() - TreeMenuWide : 0, desktopPane
								.getHeight() > 0 ? desktopPane.getHeight() : 0);

				internalFrame.add(sortingPanel);
				desktopPane.add(internalFrame, "Center");
				internalFrame.setVisible(true);

			}

		};

		if (!sortingPanel.init())
		{

			return;
		}

	}

	/**
	 * 级联图开始
	 */
	private void cascadStart()
	{
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
					HduChartUtil.getResource("Centre_PleaseAddWorkspace"));
			return;
		}
		CascadPropDialog dialog = new CascadPropDialog(workSpaceProps)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void CommitHandle(Vector<JComboBox> boxs)
			{
				JInternalFrame internalFrame = new JInternalFrame("Cascad ",
						true, true, true, false);
				internalFrame.setLocation(TreeMenuWide, 0);
				internalFrame.setSize(
						desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
								.getWidth() - TreeMenuWide : 0,
						desktopPane.getHeight() - 20 > 0 ? desktopPane
								.getHeight() - 30 : 0);

				HashMap<String, Object> map = getDataMapCascad(boxs);
				@SuppressWarnings("unchecked")
				Vector<PlaneXY> planeXYs = (Vector<PlaneXY>) map.get("data");
				float[] ref = (float[]) map.get("ref");
				cascadPanel = new OfflineCascadPanel((String) boxs.get(3)
						.getSelectedItem(), (String) boxs.get(2)
						.getSelectedItem(), ref, planeXYs);
				internalFrame.add(cascadPanel);
				desktopPane.add(internalFrame, "Center");
				internalFrame.setVisible(true);

				// 添加对于关闭的监听为了关闭动画线程的资源
				InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
				{

					public void internalFrameClosed(InternalFrameEvent e)
					{
						// after you close it.
						cascadPanel = null;
					}

				};
				internalFrame.addInternalFrameListener(frameAdapter);

			}

			@Override
			public void CommitHandle(JTable table)
			{
				// TODO Auto-generated method stub

			}
		};

		dialog.setVisible(true);
	}

	private void bodeStart()
	{
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
					HduChartUtil.getResource("Centre_PleaseAddWorkspace"));
			return;
		}
		CascadPropDialog dialog = new CascadPropDialog(workSpaceProps)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void CommitHandle(Vector<JComboBox> boxs)
			{
				JInternalFrame internalFrame = new JInternalFrame("Bode", true,
						true, true, true);
				internalFrame.setLocation(TreeMenuWide, 0);
				internalFrame.setSize(
						desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
								.getWidth() - TreeMenuWide : 0,
						desktopPane.getHeight() - 20 > 0 ? desktopPane
								.getHeight() - 30 : 0);

				HashMap<String, Object> map = getDataMapBode(boxs);

				PlaneXY phaXy = (PlaneXY) map.get("pha");
				PlaneXY absXy = (PlaneXY) map.get("abs");

				bodePanel = new OfflineBodePanel((String) boxs.get(3)
						.getSelectedItem(), (String) boxs.get(2)
						.getSelectedItem(), phaXy, absXy);
				internalFrame.add(bodePanel);
				desktopPane.add(internalFrame, "Center");
				internalFrame.setVisible(true);

				// 添加对于关闭的监听为了关闭动画线程的资源
				InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
				{

					public void internalFrameClosed(InternalFrameEvent e)
					{
						// after you close it.
						bodePanel = null;
					}

				};
				internalFrame.addInternalFrameListener(frameAdapter);

			}

			@Override
			public void CommitHandle(JTable table)
			{
				// TODO Auto-generated method stub

			}
		};

		dialog.setVisible(true);

	}

	private void polarStart()
	{
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
					HduChartUtil.getResource("Centre_PleaseAddWorkspace"));
			return;
		}
		CascadPropDialog dialog = new CascadPropDialog(workSpaceProps)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void CommitHandle(Vector<JComboBox> boxs)
			{

				JInternalFrame internalFrame = new JInternalFrame("Polar",
						true, true, true, true);
				internalFrame.setLocation(TreeMenuWide, 0);
				internalFrame.setSize(
						desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
								.getWidth() - TreeMenuWide : 0,
						desktopPane.getHeight() - 20 > 0 ? desktopPane
								.getHeight() - 30 : 0);

				HashMap<String, Object> map = getDataMapPolar(boxs);
				@SuppressWarnings("unchecked")
				PlaneXY planeXYs = (PlaneXY) map.get("data");
				float[] ref = (float[]) map.get("ref");
				polarPanel = new OfflinePolarPanel((String) boxs.get(3)
						.getSelectedItem(), (String) boxs.get(2)
						.getSelectedItem(), planeXYs, ref);
				internalFrame.add(polarPanel);
				desktopPane.add(internalFrame, "Center");
				internalFrame.setVisible(true);

				// 添加对于关闭的监听为了关闭动画线程的资源
				InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
				{

					public void internalFrameClosed(InternalFrameEvent e)
					{
						// after you close it.
						polarPanel = null;
					}

				};
				internalFrame.addInternalFrameListener(frameAdapter);

			}

			@Override
			public void CommitHandle(JTable table)
			{
				// TODO Auto-generated method stub

			}
		};

		dialog.setVisible(true);
	}

	private void trendStart()
	{
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
					HduChartUtil.getResource("Centre_PleaseAddWorkspace"));
			return;
		}
		TrendAnalysePropDialog dialog = new TrendAnalysePropDialog(
				workSpaceProps)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void CommitHandle(Vector<JComboBox> boxs)
			{
				WorkSpaceProp workSpaceProp = WorkSpaceProp.getWorkSpace(
						workSpaceProps, (String) boxs.get(0).getSelectedItem(),
						(String) boxs.get(1).getSelectedItem());

				JInternalFrame internalFrame = new JInternalFrame("Trend",
						true, true, true, true);
				internalFrame.setLocation(TreeMenuWide, 0);
				internalFrame.setSize(
						desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
								.getWidth() - TreeMenuWide : 0,
						desktopPane.getHeight() - 20 > 0 ? desktopPane
								.getHeight() - 30 : 0);

				String[] unit = new String[2];
				unit[0] = workSpaceProp.allWaveCfgInfos
						.get(0)
						.getAnalogs()
						.get(workSpaceProp.getChannelIndex(null, (String) boxs
								.get(3).getSelectedItem(),
								workSpaceProp.allWaveCfgInfos)).getUnit();
				unit[1] = workSpaceProp.allWaveCfgInfos
						.get(0)
						.getAnalogs()
						.get(workSpaceProp.getChannelIndex(null, (String) boxs
								.get(3).getSelectedItem(),
								workSpaceProp.allWaveCfgInfos)).getUnit();

				trendAnalysisPanel = new OfflineTrendAnalysisPanel(
						workSpaceProp, (String) boxs.get(2).getSelectedItem(),
						(String) boxs.get(3).getSelectedItem(), unit);
				internalFrame.add(trendAnalysisPanel);
				desktopPane.add(internalFrame, "Center");
				internalFrame.setVisible(true);

				// 添加对于关闭的监听为了关闭动画线程的资源
				InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
				{

					public void internalFrameClosed(InternalFrameEvent e)
					{
						// after you close it.
						trendAnalysisPanel = null;
					}

				};
				internalFrame.addInternalFrameListener(frameAdapter);

			}

			@Override
			public void CommitHandle(JTable table)
			{
				// TODO Auto-generated method stub

			}
		};

		dialog.setVisible(true);
	}

	// 动平衡分析启动
	private void balanceStart()
	{
		JInternalFrame internalFrame = new JInternalFrame("BalanceAnalyse ",
				true, true, true, true);
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(null, "请先加载数据！");
			return;
		}
		banlanceAnalysePanel = new BalanceAnalyse(workSpaceProps);
		internalFrame.setLocation(TreeMenuWide, 0);
		internalFrame.setSize(
				desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
						.getWidth() - TreeMenuWide : 0,
				desktopPane.getHeight() - 20 > 0 ? desktopPane.getHeight() - 30
						: 0);
		internalFrame.add(banlanceAnalysePanel);
		desktopPane.add(internalFrame, "Center");
		internalFrame.setVisible(true);

		// 添加对于关闭的监听为了关闭动画线程的资源
		InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
		{

			public void internalFrameClosed(InternalFrameEvent e)
			{
				// after you close it.
				// banlanceAnalysePanel = null;
			}

		};
		internalFrame.addInternalFrameListener(frameAdapter);

	}

	// 盘车试验开始
	private void panCheAnalyseStart()
	{
		JInternalFrame internalFrame = new JInternalFrame("PanChe ", true,
				true, true, true);
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(null, "请先加载数据！");
			return;
		}
		panchePanel = new PanChePanel();
		internalFrame.setLocation(TreeMenuWide, 0);
		internalFrame.setSize(
				desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
						.getWidth() - TreeMenuWide : 0,
				desktopPane.getHeight() - 20 > 0 ? desktopPane.getHeight() - 30
						: 0);
		internalFrame.add(panchePanel);
		desktopPane.add(internalFrame, "Center");
		internalFrame.setVisible(true);

		// 添加对于关闭的监听为了关闭动画线程的资源
		InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
		{

			public void internalFrameClosed(InternalFrameEvent e)
			{
				// after you close it.
				// banlanceAnalysePanel = null;
			}

		};
		internalFrame.addInternalFrameListener(frameAdapter);

	}

	// 磁拉力动平衡分析启动
	private void magneticStart()
	{
		JInternalFrame internalFrame = new JInternalFrame("MagneticAnalyse ",
				true, true, true, true);
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(null, "请先加载数据！");
			return;
		}
		magneticPanel = new MagneticBalance(workSpaceProps);
		internalFrame.setLocation(TreeMenuWide, 0);
		internalFrame.setSize(
				desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
						.getWidth() - TreeMenuWide : 0,
				desktopPane.getHeight() - 20 > 0 ? desktopPane.getHeight() - 30
						: 0);
		internalFrame.add(magneticPanel);
		desktopPane.add(internalFrame, "Center");
		internalFrame.setVisible(true);

		// 添加对于关闭的监听为了关闭动画线程的资源
		InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
		{

			public void internalFrameClosed(InternalFrameEvent e)
			{
				// after you close it.
				// banlanceAnalysePanel = null;
			}

		};
		internalFrame.addInternalFrameListener(frameAdapter);

	}

	private void airGapAnalyseStart()
	{
		JInternalFrame internalFrame = new JInternalFrame("AirGapAnalyse ",
				true, true, true, true);
		if (workSpaceProps.isEmpty())
		{
			JOptionPane.showMessageDialog(null, "请先加载数据！");
			return;
		}
		airGapAnalysePanel = new AirGapAnalysePanel(workSpaceProps);
		internalFrame.setLocation(TreeMenuWide, 0);
		internalFrame.setSize(
				desktopPane.getWidth() - TreeMenuWide > 0 ? desktopPane
						.getWidth() - TreeMenuWide : 0,
				desktopPane.getHeight() - 20 > 0 ? desktopPane.getHeight() - 30
						: 0);
		internalFrame.add(airGapAnalysePanel);
		desktopPane.add(internalFrame, "Center");
		internalFrame.setVisible(true);

		// 添加对于关闭的监听为了关闭动画线程的资源
		InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
		{

			public void internalFrameClosed(InternalFrameEvent e)
			{
				// after you close it.
				// banlanceAnalysePanel = null;
			}

		};
		internalFrame.addInternalFrameListener(frameAdapter);

	}

	// 历史检索启动
	private void historySearchStart()
	{
		JInternalFrame internalFrame = new JInternalFrame("HistorySearch ",
				true, true, true, true);

		historySearchPanel = new OfflineHistorySearchPanel();
		if (!historySearchPanel.init())
			return;
		internalFrame.setLocation(0, 0);
		internalFrame.setSize(
				desktopPane.getWidth() > 0 ? desktopPane.getWidth() : 0,
				desktopPane.getHeight() - 20 > 0 ? desktopPane.getHeight() - 30
						: 0);
		internalFrame.add(historySearchPanel);
		desktopPane.add(internalFrame, "Center");
		internalFrame.setVisible(true);

		// 添加对于关闭的监听为了关闭动画线程的资源
		InternalFrameAdapter frameAdapter = new InternalFrameAdapter()
		{

			public void internalFrameClosed(InternalFrameEvent e)
			{
				// after you close it.
				// banlanceAnalysePanel = null;
			}

		};
		internalFrame.addInternalFrameListener(frameAdapter);

	}

	private JInternalFrame newInternal(Component comp, String name)
	{
		JInternalFrame internalFrame = new JInternalFrame(name, true, true,
				true, true);

		internalFrame.setLocation(0, 0);
		internalFrame.setSize(
				desktopPane.getWidth() > 0 ? desktopPane.getWidth() : 0,
				desktopPane.getHeight() - 20 > 0 ? desktopPane.getHeight() - 30
						: 0);
		internalFrame.add(comp);
		desktopPane.add(internalFrame, "Center");
		internalFrame.setVisible(true);

		return internalFrame;

	}

	private void onlineAirGapStart()
	{
		onlineAirGapJpanel = new OnlineAirGapJpanel();
		JInternalFrame internalFrame = newInternal(onlineAirGapJpanel,
				"onlineAirGap");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				onlineAirGapJpanel);

		internalFrame.addInternalFrameListener(frameAdapter);
	}

	private void onlineAxesorbitStart()
	{
		onlineAxesorbitJPanel = new OnlineAxesorbitJPanel();
		JInternalFrame internalFrame = newInternal(onlineAxesorbitJPanel,
				"onlineAxesorbit");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				onlineAxesorbitJPanel);

		internalFrame.addInternalFrameListener(frameAdapter);
	}

	private void onlineAirGapBarStart()
	{
		onlineAirGapBarPanel = new OnlineAirGapBar();
		JInternalFrame internalFrame = newInternal(onlineAirGapBarPanel,
				"onlineAirGapBar");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				onlineAirGapBarPanel);

		internalFrame.addInternalFrameListener(frameAdapter);
	}

	private void onlinePartialStart()
	{
		onlinePartialJpanel = new OnlinePartialJpanel();
		JInternalFrame internalFrame = newInternal(onlinePartialJpanel,
				"onlinePartial");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				onlinePartialJpanel);

		internalFrame.addInternalFrameListener(frameAdapter);
	}

	private void onlinePowerEggStart()
	{
		onlinePowerEggGraphPanel = new OnlinePowerEggGraphPanel();
		JInternalFrame internalFrame = newInternal(onlinePowerEggGraphPanel,
				"onlinePowerEgg");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				onlinePowerEggGraphPanel);

		internalFrame.addInternalFrameListener(frameAdapter);
	}

	private void onlineSpatialAxisStart()
	{
		onlineSpatialAxisJpanel = new OnlineSpatialAxisJpanel();
		JInternalFrame internalFrame = newInternal(onlineSpatialAxisJpanel,
				"onlineSpatial");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				onlineSpatialAxisJpanel);

		internalFrame.addInternalFrameListener(frameAdapter);
	}

	private void onlineSpectrogramStart()
	{
		onlineSpectrogramPanel = new OnlineSpectrogramPanel();
		JInternalFrame internalFrame = newInternal(onlineSpectrogramPanel,
				"onlineSpectrogram");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				onlineSpectrogramPanel);

		internalFrame.addInternalFrameListener(frameAdapter);
	}

	private void onlineWaterfallStart()
	{
		onlineWaterfallJpanel = new OnlineWaterfallJpanel();
		JInternalFrame internalFrame = newInternal(onlineWaterfallJpanel,
				"onlineWaterfall");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				onlineWaterfallJpanel);

		internalFrame.addInternalFrameListener(frameAdapter);
	}

	private void onlineWaveformStart()
	{
		onlineWaveformPanell = new OnlineWaveformPanel();
		JInternalFrame internalFrame = newInternal(onlineWaveformPanell,
				"onlineWaveform");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				onlineWaveformPanell);

		internalFrame.addInternalFrameListener(frameAdapter);
	}
	private void startStattable()
	{
		stattablepanel = new Stattablepanel();
		JInternalFrame internalFrame = newInternal(stattablepanel,
				"onlineWaveform");
		// 添加对于关闭的监听为了关闭动画线程的资源
		OnlineInternalFrameAdapter frameAdapter = new OnlineInternalFrameAdapter(
				stattablepanel);
		
		internalFrame.addInternalFrameListener(frameAdapter);
	}

	class OnlineInternalFrameAdapter extends InternalFrameAdapter
	{
		OnlineICellInterface onlineCell;

		public OnlineInternalFrameAdapter(OnlineICellInterface onlineCell)
		{
			super();
			this.onlineCell = onlineCell;
		}

		public void internalFrameClosed(InternalFrameEvent e)
		{
			// after you close it.
			onlineCell.close();
		}

	};

	private ActionListener menuActionListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			String cmdString = e.getActionCommand();

			if (CMD_ANALYSEOPEN_SELECTFILE == cmdString)/** 打开数据文件的监听器 */
			{
				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						workspaceOpenStart(WorkSpaceTreeMenuPanel.TYPE_OPEN_SELECT);
					}

				}.start();

			} else if (CMD_NEWCENTER_TRACE == cmdString)// 轴心轨迹监听器
			{
				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						AxesorbitStart();
					}

				}.start();

			} else if (CMD_NEWPOSUTRE == cmdString)
			{
				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						postureStart();
					}

				}.start();

			} else if (CMD_NEWWATERFALL == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						waterfallStart();
					}

				}.start();

			} else if (CMD_SORTINGOPENFILE == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						selectStart();
					}

				}.start();
			} else if (CMD_NEWCASCAD == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						cascadStart();
					}

				}.start();
			} else if (CMD_NEWBODE == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						bodeStart();
					}

				}.start();
			} else if (CMD_NEWPOLAR == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						polarStart();
					}

				}.start();
			} else if (CMD_NEWTREND == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						trendStart();
					}

				}.start();
			} else if (CMD_NEWBALANCE == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						balanceStart();
					}

				}.start();
			} else if (CMD_NEWPANCHE == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						panCheAnalyseStart();
					}

				}.start();
			} else if (CMD_NEWMAGNETIC == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						magneticStart();
					}

				}.start();
			} else if (CMD_ANALYSEOPEN_ORIGINFILE == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						workspaceOpenStart(WorkSpaceTreeMenuPanel.TYPE_OPEN_ORIGIN);
					}

				}.start();
			} else if (CMD_NEWAIRGAPANALYSE == cmdString)
			{
				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						airGapAnalyseStart();
					}

				}.start();
			} else if (CMD_NEWHISTORY == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						historySearchStart();
					}

				}.start();
			} else if (CMD_ONLINE_AirGap == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						onlineAirGapStart();
					}

				}.start();
			} else if (CMD_ONLINE_Axesorbit == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						onlineAxesorbitStart();
					}

				}.start();
			} else if (CMD_ONLINE_AirGapBar == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						onlineAirGapBarStart();
					}

				}.start();
			} else if (CMD_ONLINE_Partial == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						onlinePartialStart();
					}

				}.start();
			} else if (CMD_ONLINE_PowerEgg == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						onlinePowerEggStart();
					}

				}.start();
			} else if (CMD_ONLINE_SpatialAxis == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						onlineSpatialAxisStart();
					}

				}.start();
			} else if (CMD_ONLINE_Spectrogram == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						onlineSpectrogramStart();
					}

				}.start();
			} else if (CMD_ONLINE_Waterfall == cmdString)
			{

				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						onlineWaterfallStart();
					}

				}.start();
			} else if (CMD_ONLINE_Waveform == cmdString)
			{
				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						onlineWaveformStart();
					}

				}.start();
			} else if (CMD_ANALYSEOPEN_CLEAE == cmdString)
			{
				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						clearWSStart();
					}

				}.start();
			} else if (CMD_SEVERNODE == cmdString)
			{
				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						new NodeMange().setVisible(true);
					}

				}.start();
			} else if (CMD_ONLINE_Stattable == cmdString)
			{
				new Thread()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						super.run();
						startStattable();
					}

				}.start();
			}

		}

	};
	

	/**
	 * 更加dialog返回的信息获取数据
	 * 
	 * @param boxs
	 * @return
	 */
	private HashMap<String, Object> getDataMap(Vector<JComboBox> boxs)
	{
		WorkSpaceProp workSpaceProp = WorkSpaceProp.getWorkSpace(
				workSpaceProps, (String) boxs.get(0).getSelectedItem(),
				(String) boxs.get(1).getSelectedItem());

		String[] gallerys = new String[] {
				(String) boxs.get(2).getSelectedItem(),
				(String) boxs.get(3).getSelectedItem() };

		Vector<PlaneXY> planeXYs = new Vector<PlaneXY>();
		float[] ref = new float[workSpaceProp.sectionName.size()];

		for (int i = 0; i < workSpaceProp.sectionName.size(); i++)
		{
			String s = workSpaceProp.sectionName.get(i);
			float[] data = workSpaceProp.getWaveData(s, gallerys[0]);
			float[] refdata = workSpaceProp.getWaveData(s, gallerys[1]);
			CalResult result = new Calculate(data, true);
			float[] abs = result.getFFTAbs();// 计算fft
			float[] fre = new float[abs.length];
			for (int j = 0; j < fre.length; j++)
			{
				fre[j] = (float) (j * ((double) workSpaceProp.allWaveCfgInfos
						.get(i).smprateRate / 2 / (double) fre.length));
			}

			planeXYs.add(new PlaneXY(fre, abs));// 获取频谱图
			ref[i] = Calculate.getAve(refdata);// 获取特征值的平均值
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("data", planeXYs);
		map.put("ref", ref.clone());
		return map;
	}

	/**
	 * 更加dialog返回的信息获取数据
	 * 
	 * @param boxs
	 * @return
	 */
	private HashMap<String, Object> getDataMapCascad(Vector<JComboBox> boxs)
	{
		WorkSpaceProp workSpaceProp = WorkSpaceProp.getWorkSpace(
				workSpaceProps, (String) boxs.get(0).getSelectedItem(),
				(String) boxs.get(1).getSelectedItem());

		String[] gallerys = new String[] {
				(String) boxs.get(2).getSelectedItem(),
				(String) boxs.get(3).getSelectedItem() };

		Vector<PlaneXY> planeXYs = new Vector<PlaneXY>();
		float[] ref = new float[workSpaceProp.sectionName.size()];

		for (int i = 0; i < workSpaceProp.sectionName.size(); i++)
		{
			String s = workSpaceProp.sectionName.get(i);
			float[] data = workSpaceProp.getWaveData(s, gallerys[0]);
			float[] refdata = workSpaceProp.getAinData(s, gallerys[1]);
			CalResult result = new Calculate(data, true);
			float[] abs = result.getFFTAbs();// 计算fft
			float[] fre = new float[abs.length];
			float frebase = ((float) workSpaceProp.allWaveCfgInfos.get(i).smprateRate / 2 / (float) abs.length);// 每个点间隔多少频率
			for (int j = 0; j < fre.length; j++)
			{
				fre[j] = (float) (j * frebase);
			}

			planeXYs.add(new PlaneXY(fre, abs));// 获取频谱图
			ref[i] = Calculate.getAve(refdata);// 获取特征值的平均值
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("data", planeXYs);
		map.put("ref", ref.clone());
		return map;
	}

	private HashMap<String, Object> getDataMapBode(Vector<JComboBox> boxs)
	{
		WorkSpaceProp workSpaceProp = WorkSpaceProp.getWorkSpace(
				workSpaceProps, (String) boxs.get(0).getSelectedItem(),
				(String) boxs.get(1).getSelectedItem());

		String[] gallerys = new String[] {
				(String) boxs.get(2).getSelectedItem(),
				(String) boxs.get(3).getSelectedItem() };

		PlaneXY planeABS = new PlaneXY();
		PlaneXY planePHA = new PlaneXY();

		float[] ref = new float[workSpaceProp.sectionName.size()];
		float[] ABS = new float[workSpaceProp.sectionName.size()];
		float[] PHA = new float[workSpaceProp.sectionName.size()];

		for (int i = 0; i < workSpaceProp.sectionName.size(); i++)
		{
			String s = workSpaceProp.sectionName.get(i);
			float[] data = workSpaceProp.getWaveData(s, gallerys[0]);
			float[] refdata = workSpaceProp.getAinData(s, gallerys[1]);
			CalResult result = new Calculate(data, true);
			ref[i] = Calculate.getAve(refdata);// 获取特征值的平均值\

			float[] abs = result.getFFTAbs();

			// 获取跟转速通道频率最接近的相位和幅值
			float frebase = ((float) workSpaceProp.allWaveCfgInfos.get(i).smprateRate / 2 / (float) abs.length);// 每个点间隔多少频率
			float f = ref[i] / 60 / frebase;// 因为转速时每分钟多少转所以/60
			int it = (f - (int) f) >= 0.5 ? 1 : 0;
			int index = (int) f + it;
			if (index >= abs.length)
				index = abs.length - 1;
			ABS[i] = abs[index];
			PHA[i] = result.getFFTPhase()[index];

		}
		planeABS.setX(ref);
		planeABS.setY(ABS);
		planePHA.setX(ref);
		planePHA.setY(PHA);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("abs", planeABS);
		map.put("pha", planePHA);
		return map;
	}

	private HashMap<String, Object> getDataMapPolar(Vector<JComboBox> boxs)
	{

		WorkSpaceProp workSpaceProp = WorkSpaceProp.getWorkSpace(
				workSpaceProps, (String) boxs.get(0).getSelectedItem(),
				(String) boxs.get(1).getSelectedItem());
		String[] gallerys = new String[] {
				(String) boxs.get(2).getSelectedItem(),
				(String) boxs.get(3).getSelectedItem() };

		PlaneXY planeXY = new PlaneXY();

		float[] ref = new float[workSpaceProp.sectionName.size()];
		float[] ABS = new float[workSpaceProp.sectionName.size()];
		float[] PHA = new float[workSpaceProp.sectionName.size()];

		for (int i = 0; i < workSpaceProp.sectionName.size(); i++)
		{
			String s = workSpaceProp.sectionName.get(i);
			float[] data = workSpaceProp.getWaveData(s, gallerys[0]);
			float[] refdata = workSpaceProp.getWaveData(s, gallerys[1]);
			CalResult result = new Calculate(data, true);
			ABS[i] = result.getAvgAbs();
			PHA[i] = result.getAvgPhase();
			ref[i] = Calculate.getAve(refdata);// 获取特征值的平均值
		}
		planeXY.setX(PHA);
		planeXY.setY(ABS);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("data", planeXY);
		map.put("ref", ref.clone());
		return map;
	}

	private class OperationMenu extends JPanel
	{

		private static final long serialVersionUID = 1L;

		private ImageIcon icon;

		private JMenu openWorkSpaceMenu;// 打开工作空间
		// private JMenu sortingMenu;// 数据分拣
		private JMenu onlineJMenu;
		private JMenu offlineJMenu;
		private JMenu severNodeJMenu;

		private JToolBar toolBar;
		private JMenuBar menuBar;

		public JButton openSelectFileItem;// 打开检波文件目录
		public JButton openOrigFileItem;// 打开原始录波文件目录
		public JButton clearWSItem;// 打开原始录波文件目录

		public JButton newCenterTraceItem;
		public JButton newPostureItem;
		public JButton newWaterfallItem;
		public JButton opensortingItem;// 分拣打开文件
		public JButton newCascadItem;// 级联图
		public JButton newBodeItem;// 伯德图
		public JButton newPolarItem;// 极坐标
		public JButton newTrendItem;//
		public JButton newBalanceItem;// 动平衡
		public JButton newPanCheItem;// 盘车
		public JButton newMagneticItem;// 磁拉力
		public JButton newHistroyItem;// 历史查询
		public JButton newAirGapItem;// 气隙图

		public JButton onlineAirGapItem;
		public JButton onlineAirGapBarItem;
		public JButton onlineAxesorbitItem;
		public JButton onlineWaterfallItem;
		public JButton onlinePartialItem;
		public JButton onlinePowerEggGraphItem;
		public JButton onlineSpatialAxisItem;
		public JButton onlineWaveformItem;
		public JButton onlineSpectrogramItem;
		public JButton onlineStattableItem;

		public JButton servernodItem;

		public OperationMenu()
		{
			menuBar = new JMenuBar();
			toolBar = new JToolBar();
			intion();

			// 关闭在线分析和节点管理
			menuBar.add(openWorkSpaceMenu);
			// menuBar.add(sortingMenu);
			menuBar.add(offlineJMenu);
			
			if (onLineSwitch)
			{
				menuBar.add(onlineJMenu);
			}
			if (serverNodeSwitch)
			{
				menuBar.add(severNodeJMenu);
			}
			

			choiceWorkpaceMenu();

			this.setLayout(new BorderLayout());
			this.add(menuBar, BorderLayout.NORTH);
			this.add(toolBar, BorderLayout.SOUTH);

		}

		private ChangeListener menuActionListener = new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				// TODO Auto-generated method stub
				if (e.getSource() == openWorkSpaceMenu)
				{
					choiceWorkpaceMenu();
				} else if (e.getSource() == onlineJMenu)
				{
					choiceOnlineJMenu();
				} else if (e.getSource() == offlineJMenu)
				{
					choiceOfflineJMenu();
				} else if (e.getSource() == severNodeJMenu)
				{
					choiceSeverNodeJMenu();
				} else
				{

				}
			}

		};

		private void choiceWorkpaceMenu()
		{
			toolBar.removeAll();
			toolBar.add(openSelectFileItem);
			toolBar.add(openOrigFileItem);
			toolBar.add(opensortingItem);
			toolBar.add(clearWSItem);
			toolBar.updateUI();
		}

		private void choiceSortingMenu()
		{
			toolBar.removeAll();
			toolBar.add(opensortingItem);
			toolBar.updateUI();
		}

		private void choiceOnlineJMenu()
		{
			toolBar.removeAll();

			toolBar.add(onlineWaveformItem);
			toolBar.add(onlineSpectrogramItem);
			toolBar.add(onlineAxesorbitItem);
			toolBar.add(onlineSpatialAxisItem);
			toolBar.add(onlineWaterfallItem);
			toolBar.add(onlineAirGapItem);
			toolBar.add(onlineAirGapBarItem);
			toolBar.add(onlinePartialItem);
			toolBar.add(onlinePowerEggGraphItem);
			toolBar.add(onlineStattableItem);

			toolBar.updateUI();

		}

		private void choiceOfflineJMenu()
		{
			toolBar.removeAll();

			toolBar.add(newCenterTraceItem);
			toolBar.add(newPostureItem);
			toolBar.add(newWaterfallItem);
			toolBar.add(newCascadItem);
			toolBar.add(newBodeItem);
			toolBar.add(newPolarItem);
			toolBar.add(newTrendItem);
			toolBar.add(newBalanceItem);
			toolBar.add(newPanCheItem);
			toolBar.add(newMagneticItem);
			toolBar.add(newAirGapItem);
			toolBar.add(newHistroyItem);

			toolBar.updateUI();
		}

		private void choiceSeverNodeJMenu()
		{
			toolBar.removeAll();

			toolBar.add(servernodItem);

			toolBar.updateUI();
		}

		private void intion()
		{
			// icon = new ImageIcon("HMS_ClientChartPrp\\Icon\\open.ico");

			Font font = ConfigHelp.getMenuFont();
			
			
			openWorkSpaceMenu = new JMenu(
					HduChartUtil.getResource("Centre_OpenWorkSpace"));
			openWorkSpaceMenu.setFont(font);
			
			// sortingMenu = new
			// JMenu(HduChartUtil.getResource("Centre_Sorting"));
			onlineJMenu = new JMenu(HduChartUtil.getResource("Centre_Online"));
			onlineJMenu.setFont(font);
			
			offlineJMenu = new JMenu(HduChartUtil.getResource("Centre_Offline"));
			offlineJMenu.setFont(font);
			
			severNodeJMenu = new JMenu(
					HduChartUtil.getResource("Centre_SeverNode"));
			severNodeJMenu.setFont(font);
			
			openWorkSpaceMenu.addChangeListener(menuActionListener);
			// sortingMenu.addChangeListener(menuActionListener);
			onlineJMenu.addChangeListener(menuActionListener);
			offlineJMenu.addChangeListener(menuActionListener);
			severNodeJMenu.addChangeListener(menuActionListener);

			openSelectFileItem = new JButton(
					HduChartUtil.getResource("Centre_OpenWorkSpace_Select"));
			openSelectFileItem.setFont(font);
			
			openOrigFileItem = new JButton(
					HduChartUtil.getResource("Centre_OpenWorkSpace_Original"));
			openOrigFileItem.setFont(font);
			
			clearWSItem = new JButton(
					HduChartUtil.getResource("Centre_OpenWorkSpace_Clear"));
			clearWSItem.setFont(font);
			
			opensortingItem = new JButton(
					HduChartUtil.getResource("Centre_OpenWorkSpace_Sort"));
			opensortingItem.setFont(font);
			
			newCenterTraceItem = new JButton(
					HduChartUtil.getResource("Centre_CentreTrace"));
			newCenterTraceItem.setFont(font);
			
			newPostureItem = new JButton(
					HduChartUtil.getResource("Centre_SpatialAxis"));
			newPostureItem.setFont(font);
			
			newWaterfallItem = new JButton(
					HduChartUtil.getResource("Centre_waterfall"));
			newWaterfallItem.setFont(font);
			
			newCascadItem = new JButton(
					HduChartUtil.getResource("Centre_Cascad"));
			newCascadItem.setFont(font);
			
			newBodeItem = new JButton(HduChartUtil.getResource("Centre_Bode"));
			newBodeItem.setFont(font);
			
			newPolarItem = new JButton(HduChartUtil.getResource("Centre_Polar"));
			newPolarItem.setFont(font);
			
			newTrendItem = new JButton(HduChartUtil.getResource("Centre_Trend"));
			newTrendItem.setFont(font);
			
			newBalanceItem = new JButton(
					HduChartUtil.getResource("Centre_Balance"));
			newBalanceItem.setFont(font);
			
			newPanCheItem = new JButton(
					HduChartUtil.getResource("Centre_PanChe"));
			newPanCheItem.setFont(font);
			
			newMagneticItem = new JButton(
					HduChartUtil.getResource("Centre_Magnetic"));
			newMagneticItem.setFont(font);
			
			newAirGapItem = new JButton(
					HduChartUtil.getResource("Centre_AirGapAnalyse"));
			newAirGapItem.setFont(font);
			
			newHistroyItem = new JButton(
					HduChartUtil.getResource("Centre_Histroy"));
			newHistroyItem.setFont(font);
			
			onlineAirGapItem = new JButton(
					HduChartUtil.getResource("Centre_AirGap"));
			onlineAirGapItem.setFont(font);
			onlineAirGapBarItem = new JButton(
					HduChartUtil.getResource("Centre_AirGapBar"));
			onlineAirGapBarItem.setFont(font);
			
			onlineAxesorbitItem = new JButton(
					HduChartUtil.getResource("Centre_CentreTrace"));
			onlineAxesorbitItem.setFont(font);
			
			onlineSpatialAxisItem = new JButton(
					HduChartUtil.getResource("Centre_SpatialAxis"));
			onlineSpatialAxisItem.setFont(font);
			
			onlineWaterfallItem = new JButton(
			
					HduChartUtil.getResource("Centre_waterfall"));
			onlineWaterfallItem.setFont(font);
			
			
			onlinePartialItem = new JButton(
					HduChartUtil.getResource("Centre_Partial"));
			onlinePartialItem.setFont(font);
			
			onlinePowerEggGraphItem = new JButton(
					HduChartUtil.getResource("Centre_PowerEgg"));
			onlinePowerEggGraphItem.setFont(font);
			
			onlineWaveformItem = new JButton(
					HduChartUtil.getResource("Centre_Waveform"));
			onlineWaveformItem.setFont(font);
			
			onlineSpectrogramItem = new JButton(
					HduChartUtil.getResource("Centre_Spectrogram"));
			onlineSpectrogramItem.setFont(font);
			
			servernodItem = new JButton(HduChartUtil.getResource("Centre_Open"));
			servernodItem.setFont(font);
			
			onlineStattableItem = new JButton(
					HduChartUtil.getResource("Centre_DataList"));
			onlineStattableItem.setFont(font);
			
		}
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		JFrame dataAnalyseFrame = new JFrame("DataAnalyse");
		dataAnalyseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dataAnalyseFrame.setSize(1200, 700);

		RefineryUtilities.centerFrameOnScreen(dataAnalyseFrame);

		dataAnalyseFrame.add(new DataAnalysePanel());
		dataAnalyseFrame.setVisible(true);

	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

}
