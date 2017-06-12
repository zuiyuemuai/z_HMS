package com.nari.slsd.hms.hmsApp;

import java.awt.Container;

import com.nari.slsd.hd.common.FuncActionItem;
import com.nari.slsd.hd.common.IRunEntry;
import com.nari.slsd.hd.common.IStaticMsgCard;
import com.nari.slsd.hms.hdu.serverManger.view.NodeMange;

public class NodeManageServer implements IRunEntry {

	@Override
	public IStaticMsgCard getPanelMessageCard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "状态监测系统配置";
	}

	@Override
	public Object invoke(Container arg0, FuncActionItem arg1) {
		NodeMange frame = new NodeMange();
		frame.setVisible(true);
		return frame;
	}

}
