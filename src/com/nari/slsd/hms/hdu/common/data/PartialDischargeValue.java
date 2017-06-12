/**
 * 
 */
package com.nari.slsd.hms.hdu.common.data;

/**
 * Created :2014-12-2 下午4:01:18 
 * Describe :局部放电数据模型
 * Class : PartialDischargeValue.java
 * Designed by:YXQ
 *
 *
 */
public class PartialDischargeValue {
	
	private float nqnPlus;
	private float nqnMinus;
	private float qmPlus;
	private float qmMinus;
	private PlaneXY plusValue;
	private PlaneXY minusValue;

	public PartialDischargeValue(float nqnPlus, float nqnMinus, float qmPlus, float qmMinus,PlaneXY plusValue,PlaneXY minusValue) {
		setnqnPlus(nqnPlus);
		setnqnMinus(nqnMinus);
		setqmPlus(qmPlus);
		setqmMinus(qmMinus);
		setPlusValue(plusValue);
		setMinusValue(minusValue);
	}

	/**
	 * @return the plusValue
	 */
	public PlaneXY getPlusValue() {
		return plusValue;
	}

	/**
	 * @param plusValue the plusValue to set
	 */
	public void setPlusValue(PlaneXY plusValue) {
		this.plusValue = plusValue;
	}

	/**
	 * @return the minusValue
	 */
	public PlaneXY getMinusValue() {
		return minusValue;
	}

	/**
	 * @param minusValue the minusValue to set
	 */
	public void setMinusValue(PlaneXY minusValue) {
		this.minusValue = minusValue;
	}

	public float getnqnPlus() {
		return nqnPlus;
	}

	public void setnqnPlus(float nqnPlus) {
		this.nqnPlus = nqnPlus;
	}

	public float getnqnMinus() {
		return nqnMinus;
	}

	public void setnqnMinus(float nqnMinus) {
		this.nqnMinus = nqnMinus;
	}

	public float getqmPlus() {
		return qmPlus;
	}

	public void setqmPlus(float qmPlus) {
		this.qmPlus = qmPlus;
	}

	public float getqmMinus() {
		return qmMinus;
	}

	public void setqmMinus(float qmMinus) {
		this.qmMinus = qmMinus;
	}

}
