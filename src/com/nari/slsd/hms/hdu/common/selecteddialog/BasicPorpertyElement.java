package com.nari.slsd.hms.hdu.common.selecteddialog;

import java.io.Serializable;

import org.jdom2.Element;

public abstract class BasicPorpertyElement implements Serializable
{
	protected long id;
	protected String name;

	/**
	 * 平台父节点ID
	 */
	protected long pId;
	/**
	 * 平台ID
	 */
	protected long imcId;

	public BasicPorpertyElement()
	{

	}

	public BasicPorpertyElement(Element channelElement)
	{
	}


	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return this.getName();
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}


	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getpId()
	{
		return pId;
	}

	public void setpId(long pId)
	{
		this.pId = pId;
	}

	public long getImcId()
	{
		return imcId;
	}

	public void setImcId(long imcId)
	{
		this.imcId = imcId;
	}

	

	
}
