package com.nari.slsd.hms.hdu.common.selecteddialog;

import org.jdom2.Element;

/**
 * @author HongLiChen
 *	模拟量实体类
 */
public class AnlogPorpertyElement extends BasicPorpertyElement
{
	protected int address;
	
	public AnlogPorpertyElement()
	{
	}
	
	public AnlogPorpertyElement(Element channelElement)
	{
		name = channelElement.getAttributeValue("name");
		pId = Long.parseLong(channelElement
				.getAttributeValue("pId"));
		id = Long.parseLong(channelElement.getAttributeValue("id"));
		address=Integer.parseInt(channelElement.getAttributeValue("pointoffset")); 
	}
	public int getAddress()
	{
		return address;
	}
}
