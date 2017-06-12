package com.nari.slsd.hms.hdu.common.data;

//弯曲量
public class NetSwingBean
{
	private float m_fAmptitude;// 弯曲量
	private float m_fPhase;// 弯曲角
	
	public NetSwingBean(float amptitude, float phase)
	{
		m_fAmptitude = amptitude;
		m_fPhase = phase;
	}
	public NetSwingBean()
	{
		this(0, 0);
	}
	
	public float getAmptitude()
	{
		return m_fAmptitude;
	}
	public float getPhase()
	{
		return m_fPhase;
	}
	public void setAmptitude(float d)
	{
		m_fAmptitude = d;
	}
	public void setPhase(float d)
	{
		m_fPhase = d;
	}
}
