package com.nari.slsd.hms.hdu.common.selecteddialog;



/**
 * 类名：ChannelMessages
 * 作用：选择框与图元信息交换类
 * @author HongLiChen
 */
public class ChannelMessages
{
	byte m_renewSec = 2;
	int size = 0;
	short[] channelID;
	String[] names;
	short[] KeyID;

	public ChannelMessages(byte m_renewSec, short[] channelID, String[] names,
			short[] keyID)
	{
		super();
		this.m_renewSec = m_renewSec;
		this.channelID = channelID;
		this.names = names;
		size = names.length;
		KeyID = keyID;

	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public String[] getNames()
	{
		return names;
	}

	public void setNames(String[] names)
	{
		this.names = names;
	}

	public byte getM_renewSec()
	{
		return m_renewSec;
	}

	public void setM_renewSec(byte m_renewSec)
	{
		this.m_renewSec = m_renewSec;
	}

	public short[] getChannelID()
	{
		return channelID;
	}

	public void setChannelID(short[] channelID)
	{
		this.channelID = channelID;
	}

	public short[] getKeyID()
	{
		return KeyID;
	}

	public void setKeyID(short[] keyID)
	{
		KeyID = keyID;
	}
	@Override
	public ChannelMessages clone() throws CloneNotSupportedException {
		return new ChannelMessages(m_renewSec, channelID, names, KeyID);
	}

}
