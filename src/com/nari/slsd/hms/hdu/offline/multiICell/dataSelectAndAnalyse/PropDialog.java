package com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 获取资源信息对话框的基类
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public abstract class PropDialog extends JDialog implements ActionListener
{
	public static Font titlefont = new Font("微软雅黑", Font.BOLD, 18);
	public static Font textfont = new Font("微软雅黑", Font.BOLD, 12);

	// protected static ResourceBundle res = ResourceBundleWrapper
	// .getBundle(PropertiesPATH.LocalizationBundle);

	final protected String CancelCommand = "cancel";
	final protected String CommitCommand = "commit";
	protected String item[];

	protected Vector<WorkSpaceProp> workSpaceProps;

	protected Vector<JComboBox> jComboBoxs = new Vector<JComboBox>();

	protected abstract void JcomBoxsInit();// 对于jComboBoxs的初始化

	protected abstract void JcomBoxsSelectHandle(JComboBox choise);// 当选择改变时

	public abstract void CommitHandle(Vector<JComboBox> boxs);// 当被确认时被触发

	public abstract void CommitHandle(JTable table);// 当被确认时被触发

	protected abstract void IntemInit();// 对于item表项的初始化

	protected JScrollPane jScrollPane;
	// public abstract void CancelHandle();
	protected JTable table;

	public PropDialog(int xsize, int ysize)
	{
		this(null, xsize, ysize);
	}

	public PropDialog(Vector<WorkSpaceProp> workSpaceProps, int xsize, int ysize)
	{
		this.workSpaceProps = workSpaceProps;

		this.setModal(true);

		table = new JTable();

		this.setSize(xsize, ysize);
		this.setLocation(500, 250);
		this.setLayout(new BorderLayout());
		IntemInit();

		JButton cancelButton = new JButton(
				HduChartUtil.getResource("Common_Cancle"));
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(CancelCommand);
		JButton commitButton = new JButton(
				HduChartUtil.getResource("Common_Ensure"));
		commitButton.addActionListener(this);
		commitButton.setActionCommand(CommitCommand);

		JPanel panel = new JPanel(new BorderLayout());

		jScrollPane = getJScrollPane(table, workSpaceProps);
		panel.add(jScrollPane, BorderLayout.CENTER);

		// 失去焦点后提交数据
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		JPanel buttonJPanel = new JPanel(new GridBagLayout());
		GridBagUtil.addBlankJLabel(buttonJPanel, 0, 0, 5, 1);
		GridBagUtil.setLocation(buttonJPanel, commitButton, 1, 0, 1, 1, true);
		GridBagUtil.setLocation(buttonJPanel, cancelButton, 2, 0, 1, 1, true);

		this.add(buttonJPanel, BorderLayout.SOUTH);
		this.add(panel, BorderLayout.CENTER);
	}

	/**
	 * 获取一个表格界面
	 * 
	 * @param jTable
	 *            需要被封装的表格
	 * @return 表格封装Jpane
	 */
	protected JScrollPane getJScrollPane(JTable jTable,
			Vector<WorkSpaceProp> workSpaceProps)
	{

		Vector<String> title = new Vector<String>();
		Vector<Vector<String>> data = new Vector<Vector<String>>();

		title.add(HduChartUtil.getResource("Common_Name"));
		title.add(HduChartUtil.getResource("Common_Message"));

		WorkSpaceEditor editor = null;

		if (workSpaceProps != null && !workSpaceProps.isEmpty())
		{
			editor = new WorkSpaceEditor(workSpaceProps);
		}

		for (int i = 0; i < item.length; i++)
		{
			Vector<String> t = new Vector<String>();
			t.add(item[i]);
			if (jComboBoxs.size() != 0)
				t.add((String) jComboBoxs.get(i).getSelectedItem());
			else
				t.add("no data");
			data.add(t);
		}

		DefaultTableModel model = new DefaultTableModel();
		model.setDataVector(data, title);
		jTable.setModel(model);

		jTable.setFont(textfont);

		JScrollPane jscrollPane = new JScrollPane(jTable);

		jscrollPane.setPreferredSize(new Dimension(100, 60));

		if (workSpaceProps != null && !workSpaceProps.isEmpty())
		{
			jTable.getColumnModel().getColumn(1).setCellEditor(editor);
		}
		return jscrollPane;
	}

	// 设置表格大小
	public void setTableSize(int x, int y)
	{
		jScrollPane.setPreferredSize(new Dimension(x, y));
	}

	protected class WorkSpaceEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener
	{

		private Component currentEditorComp = null;

		public WorkSpaceEditor(Vector<WorkSpaceProp> workSpaceProps)
		{
			JcomBoxsInit();

			for (JComboBox jComboBox : jComboBoxs)
			{
				jComboBox.addActionListener(this);
				jComboBox.setFont(textfont);
			}

		}

		// 双击
		public boolean isCellEditable(EventObject e)
		{
			if (e instanceof MouseEvent)
			{
				MouseEvent me = (MouseEvent) e;
				if (me.getClickCount() >= 2)
				{
					return true;
				}
				return false;
			}
			return true;
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column)
		{

			if (1 == column)
			{
				currentEditorComp = jComboBoxs.get(row);
				jComboBoxs.get(row).setSelectedItem(value);
			}

			return currentEditorComp;
		}

		public Object getCellEditorValue()
		{
			for (JComboBox jComboBox : jComboBoxs)
			{
				if (currentEditorComp == jComboBox)
				{
					return jComboBox.getSelectedItem();
				}
			}

			return "No data";

		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			JcomBoxsSelectHandle((JComboBox) e.getSource());
		}

	}

	protected class NoWorkSpaceEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener
	{

		private Component currentEditorComp = null;

		public NoWorkSpaceEditor()
		{
			JcomBoxsInit();

			for (JComboBox jComboBox : jComboBoxs)
			{
				jComboBox.addActionListener(this);
				jComboBox.setFont(textfont);
			}

		}

		// 双击
		public boolean isCellEditable(EventObject e)
		{
			if (e instanceof MouseEvent)
			{
				MouseEvent me = (MouseEvent) e;
				if (me.getClickCount() >= 2)
				{
					return true;
				}
				return false;
			}
			return true;
		}

		public Object getCellEditorValue()
		{
			for (JComboBox jComboBox : jComboBoxs)
			{
				if (currentEditorComp == jComboBox)
				{
					return jComboBox.getSelectedItem();
				}
			}

			return "No data";

		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			JcomBoxsSelectHandle((JComboBox) e.getSource());
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column)
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String command = e.getActionCommand();

		if (command.equals(CancelCommand))
		{
			// CancelHandle();
			setVisible(false);

		} else if (command.equals(CommitCommand))
		{

			CommitHandle(jComboBoxs);
			CommitHandle(table);
			setVisible(false);
		}

	}

}
