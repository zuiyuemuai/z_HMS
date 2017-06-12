package com.nari.slsd.hms.hdu.offline.multiICell.historySearch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

import com.nari.slsd.hms.hdu.common.iCell.PieChartPanel;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.common.util.LoggerUtil;
import com.nari.slsd.hms.hdu.common.util.ConfigUtil;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

public class OfflineHistorySearchPanel extends JPanel implements ActionListener
{
	public static String filename = "WorkRecords.info";
	private static String CMD_SELECT = "搜素";
	private static String CMD_OUT = "输出";

	private Vector<Vector<String>> tabledata = new Vector<Vector<String>>();
	private JTable table = new JTable();
	private DateChooserJButton startTimeButton = new DateChooserJButton();
	private DateChooserJButton endTimeButton = new DateChooserJButton();
	private PieChartPanel countPiePanel = new PieChartPanel(
			HduChartUtil.getResource("OfflineHistorySearch_WorkTimes"));// 次数
	private PieChartPanel timePiePanel = new PieChartPanel(
			HduChartUtil.getResource("OfflineHistorySearch_WorkTime"));// 时间
	private JComboBox sectionBox;
	private DefaultTableModel model;

	protected class WorkMessage
	{
		public int type;
		public String typename = null;
		public String startTime = null;
		public String endsTime = null;
		public String stationName = null;
		public Date startDate = null;
		public Date endDate = null;
		public int time;// 时长 单位s

		public void getDate()
		{
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH-mm-ss");

			try
			{
				if (null != this.startTime)
				{
					startDate = format.parse(startTime);
				}
				if (null != this.endsTime)
				{
					endDate = format.parse(endsTime);
				}

				if (null != this.endDate && null != this.startDate)
				{
					time = (int) ((endDate.getTime() - startDate.getTime()) / 1000);
				}

			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				LoggerUtil.log(Level.WARNING, e.toString());
			}

		}

	}

	Vector<Character> worktype = new Vector<Character>();
	Vector<String> workname = new Vector<String>();

	// 工况名称，与id一一对应//工况id
	HashMap<Integer, String> workMap = new HashMap<Integer, String>();
	Vector<WorkMessage> workMessages = new Vector<OfflineHistorySearchPanel.WorkMessage>();
	HashSet<String> stationSet = new HashSet<String>();

	public OfflineHistorySearchPanel()
	{

	}

	public boolean init()
	{
		if (!loadSearchFile())
		{
			return false;
		}
		uiDesign();
		return true;
	}

	private void uiDesign()
	{
		this.setLayout(new BorderLayout());

		this.add(getTopPanel(), BorderLayout.NORTH);
		this.add(getJScrollPane(table), BorderLayout.CENTER);
		this.add(getButtomPanel(), BorderLayout.SOUTH);

	}

	private JPanel getTopPanel()
	{
		JPanel jPanel = new JPanel(new GridBagLayout());
		jPanel.setBackground(Color.white);
		
		JButton searchButton = new JButton(
				HduChartUtil.getResource("OfflineHistorySearch_Search"));
		searchButton.addActionListener(this);
		searchButton.setActionCommand(CMD_SELECT);

		JButton outButton = new JButton(HduChartUtil// 导出
				.getResource("OfflineHistorySearch_Out"));
		outButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				creatWord();
			}
		});

		sectionBox = new JComboBox();
		for (String s : stationSet)
		{
			sectionBox.addItem(s);
		}

		GridBagUtil.addBlankJLabel(jPanel, 0, 0, 1, 1);
		GridBagUtil.setLocation(jPanel, sectionBox, 1, 0, 1, 1, true);
		GridBagUtil.setLocation(jPanel, startTimeButton, 2, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(jPanel, 3, 0, 1, 1);
		GridBagUtil.setLocation(jPanel, endTimeButton, 4, 0, 1, 1, true);
		GridBagUtil.setLocation(jPanel, searchButton, 5, 0, 1, 1, true);
		GridBagUtil.setLocation(jPanel, outButton, 6, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(jPanel, 7, 0, 1, 1);
		return jPanel;
	}

	private JPanel getButtomPanel()
	{
		JPanel mainJPanel = new JPanel(new GridBagLayout());

		GridBagUtil.setLocation(mainJPanel, countPiePanel, 1, 0, 1, 1, true);
		GridBagUtil.setLocation(mainJPanel, timePiePanel, 3, 0, 1, 1, true);

		return mainJPanel;
	}

	private void creatWord()
	{
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil
				.getResource("Common_ChooseSavePath"));
		jFileChooser.setSelectedFile(new File("轴心轨迹图报告"));
		jFileChooser.setMultiSelectionEnabled(false);
		int returnVal = jFileChooser.showSaveDialog(jFileChooser);
		if (returnVal != JFileChooser.APPROVE_OPTION)// 判断对话框是否选择“取消”
		{
			savePath = null;
			return;
		} else
		{
			savePath = jFileChooser.getSelectedFile().getPath() + ".doc";
		}

		HduCreatWord hduCreatWord = new HduCreatWord("//wordModel", savePath,
				"axesorbitModel.ftl")
		{

			@Override
			public void getData(Map<String, Object> dataMap)
			{
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();
				// dataMap.put("image", ImageChange.getImageEncode(mainJpanel));
				// dataMap.put("xchannel", namex);
				// dataMap.put("ychannel", namey);
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month",
						String.valueOf(now.get(Calendar.MONTH) + 1));
				dataMap.put("date",
						String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String cmdString = e.getActionCommand();

		if (cmdString.equals(CMD_SELECT))
		{
			startSearch();
		}
	}

	/**
	 * 获取一个表格界面
	 * 
	 * @param jTable
	 *            需要被封装的表格
	 * @return 表格封装Jpane
	 */
	private JScrollPane getJScrollPane(JTable jTable)
	{

		Vector<String> title = new Vector<String>();

		title.add(HduChartUtil
				.getResource("OfflineHistorySearch_WorkingCondition"));
		title.add(HduChartUtil.getResource("OfflineHistorySearch_StartTime"));
		title.add(HduChartUtil.getResource("OfflineHistorySearch_EndTime"));

		for (WorkMessage wm : workMessages)
		{
			Vector<String> t = new Vector<String>();
			t.add(wm.typename);
			t.add(wm.startTime);
			t.add(wm.endsTime);
			tabledata.add(t);
		}

		model = new DefaultTableModel();
		model.setDataVector(tabledata, title);
		jTable.setModel(model);
		JScrollPane jscrollPane = new JScrollPane(jTable);
		// jscrollPane.setPreferredSize(new Dimension(100, 60));
		jscrollPane.setBackground(Color.white);
		jTable.setBackground(Color.white);

		return jscrollPane;
	}

	private boolean loadSearchFile()
	{

		String pathString = ConfigUtil
				.getPropertiesValue(ConfigUtil.KEY_WORKSPACE_ORIGINPATH);
		if (null == pathString)// 读取配置文件，如果没有则默认桌面
		{
			JOptionPane.showMessageDialog(this, "请先加载录波空间！");

			LoggerUtil.log(Level.WARNING, "history search file is not exists");
			return false;
		}
		pathString += ("\\" + filename);

		File file = new File(pathString);
		if (!file.exists())
		{
			LoggerUtil.log(Level.WARNING, "history search file is not exists");
			JOptionPane.showMessageDialog(this, "录波空间加载错误，请重新加载录波空间！");
			return false;
		}
		InputStreamReader isr;

		try
		{
			isr = new InputStreamReader(new FileInputStream(file), "gbk");

			BufferedReader br = new BufferedReader(isr);

			String temp;
			while (null != (temp = br.readLine()))
			{

				if (temp.isEmpty())
				{
					continue;
				}
				String[] split = temp.split("[,]");
				if (5 != split.length)// 基本判断
				{
					LoggerUtil.log(Level.WARNING, "work file format err");// 格式错误
					return false;
				}
				String station = split[0];
				String startTime = split[1];
				String endTime = split[2];
				int type = Integer.parseInt(split[3].split("x")[1]);
				String typename = split[4];

				stationSet.add(station);
				workMap.put(type, typename);

				WorkMessage msg = new WorkMessage();
				msg.type = type;
				msg.startTime = startTime;
				msg.endsTime = endTime;
				msg.stationName = station;
				msg.typename = typename;

				msg.getDate();
				workMessages.add(msg);

			}

			return true;

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			LoggerUtil.log(Level.SEVERE, e.toString());
		}

		return false;
	}

	// 根据条件过滤
	private Vector<WorkMessage> filterMessages(Vector<WorkMessage> wms,
			String stationName, Date startDate, Date endDate)
	{
		Vector<WorkMessage> reVector = new Vector<OfflineHistorySearchPanel.WorkMessage>();

		for (WorkMessage wm : wms)
		{
			if (wm.stationName.equals(stationName)
					&& !(wm.endDate.getTime() < startDate.getTime() || wm.startDate
							.getTime() > endDate.getTime()))
			{
				reVector.add(wm);
			}

		}

		return reVector;

	}

	// 查找数量
	private HashMap<String, Integer> searchCounts(Vector<WorkMessage> wms)
	{
		int[] count = new int[workMap.size()];
		int[] key = new int[workMap.size()];
		Iterator<Integer> it = workMap.keySet().iterator();

		for (int i = 0; i < count.length; i++)
		{
			count[i] = 0;
			key[i] = it.next();
		}

		for (WorkMessage wm : wms)
		{
			for (int i = 0; i < key.length; i++)
			{
				if (key[i] == wm.type)
				{
					count[i]++;
				}
			}
		}

		HashMap<String, Integer> retval = new HashMap<String, Integer>();
		for (int i = 0; i < count.length; i++)
		{
			retval.put(workMap.get(key[i]), count[i]);
		}

		return retval;
	}

	// 查询时长
	private HashMap<String, Integer> searchTimes(Vector<WorkMessage> wms)
	{
		int[] time = new int[workMap.size()];
		int[] key = new int[workMap.size()];
		Iterator<Integer> it = workMap.keySet().iterator();

		for (int i = 0; i < time.length; i++)
		{
			time[i] = 0;
			key[i] = it.next();
		}

		for (WorkMessage wm : wms)
		{
			for (int i = 0; i < key.length; i++)
			{
				if (key[i] == wm.type)
				{
					time[i] += wm.time;
				}
			}
		}

		HashMap<String, Integer> retval = new HashMap<String, Integer>();
		for (int i = 0; i < time.length; i++)
		{
			retval.put(workMap.get(key[i]), time[i]);
		}

		return retval;
	}

	private void startSearch()
	{
		Vector<WorkMessage> wMessages = filterMessages(workMessages,
				(String) sectionBox.getSelectedItem(),
				startTimeButton.getDate(), endTimeButton.getDate());
		HashMap<String, Integer> countHashMap = searchCounts(wMessages);
		HashMap<String, Integer> timesHashMap = searchTimes(wMessages);

		tableUpdate(wMessages);

		countPiePanel.update(countHashMap);
		timePiePanel.update(timesHashMap);

	}

	/**
	 * 表格的更新
	 * 
	 * @param jTable
	 *            需要跟新的表格
	 * @param property
	 *            更新的数据
	 */
	private void tableUpdate(Vector<WorkMessage> wMessages)
	{
		tabledata.clear();
		for (WorkMessage wm : wMessages)
		{
			Vector<String> t = new Vector<String>();
			t.add(wm.typename);
			t.add(wm.startTime);
			t.add(wm.endsTime);
			tabledata.add(t);
		}

		table.updateUI();

	}

	public static void main(String[] args)
	{
		JFrame jFrame = new JFrame();
		jFrame.setLayout(new BorderLayout());
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		OfflineHistorySearchPanel searchPanel = new OfflineHistorySearchPanel();
		jFrame.add(searchPanel, BorderLayout.CENTER);
		jFrame.setVisible(true);
	}

}
