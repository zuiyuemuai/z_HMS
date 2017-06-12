package com.nari.slsd.hms.hdu.offline;

//
public interface OffLineICellInterface
{
	/**
	 * 图元初始化
	 */
	public abstract void init();
	/**
	 * 图元关闭资源（主要是线程）
	 */
	public abstract void close();
}
