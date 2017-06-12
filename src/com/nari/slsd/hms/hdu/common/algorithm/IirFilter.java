
package com.nari.slsd.hms.hdu.common.algorithm;

import java.awt.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * FIR滤波器
 * @author YXQ
 * 
 *
 */
public class IirFilter {
	
//	private float[] a;
//	private float[] a;
	private int sampleFre;
	private float[] input;
	public IirFilter(int sampleFre,float input[]){
		this.sampleFre=sampleFre;
		this.input=input.clone();
	}

	/**
	 * 滤波器设计
	 * @param band 滤波类型
	 * @param ns 阶数
	 * @param n 几个基本型
	 * @param f1 低通频率，低通和带阻时；
	 * @param f2 低通频率，高通和带通时
	 * @param f3 高通频率，低通，高通和带通时
	 * @param f4 高通频率，带阻时
	 * @param db 衰减系数
	 * @param b 分母系数
	 * @param a 分子系数
	 * @return
	 */
	public  float[] iirFilterDesign(int band, int ns, int n, float f1, float f2,
			float f3, float f4, float db, float b[], float a[]) {
		
		float[] output;
		output=input.clone();
		int k;
		float omega, lamda, epslon, fl = 0, fh = 0;
		float[] d=new float[5];
		float[] c=new float[5];
		if ((band == 1) || (band == 4)) {
			fl = f1;
		}
		if ((band == 2) || (band == 3)) {
			fl = f2;
		}
		if (band <= 3) {
			fh = f3;
		}
		if (band == 4) {
			fh = f4;
		}

		switch (band) {
		case 1:
		case 2: {
			omega = warp(f2) / warp(f1);
		}
			break;
		case 3: {
			omega = omin(bpsub(warp(f1), fh, fl), bpsub(warp(f4), fh, fl));
		}
			break;
		case 4: {
			omega = omin(1.0 / bpsub(warp(f1), fh, fl),
					1.0 / bpsub(warp(f4), fh, fl));
		}
			lamda = (float) Math.pow(10.0, (db / 20.0));
			epslon = lamda / cosh1(2 * ns * cosh1(omega));
		}

		for (k = 0; k < ns; k++) {
			bwtf(2 * ns, k, 4, d, c);
//			fblt(d, c, n, band, fl, fh, b[k * (n + 1) + 0], a[k * (n + 1) + 0]);
			fblt(d, c, n, band, fl, fh, b, a,k * (n + 1) + 0);
		}
		for(int i=0;i<ns;i++)
		{
			if(output==null)
			{
				return null;
			}
			output=filter(b, a, n,i*(n+1) ,output);
		}
		return output;
	}

	protected  float cosh1(float x) {
		float z;
		z = (float) Math.log(x + Math.sqrt(x * x - 1.0));
		return z;
	}

	protected  float warp(float f) {
		float pi, z;
//		pi = 4.0 * Math.atan(1.0);
		z = (float) Math.tan(Math.PI * f);
		return z;
	}

	protected float bpsub(float om, float fh, float fl) {
		float z;
		z = (om * om - warp(fh) * warp(fl)) / ((warp(fh) - warp(fl)) * om);
		return z;
	}

	protected float omin(double om1, double om2) {
		double z, z1, z2;
		z1 = Math.abs(om1);
		z2 = Math.abs(om2);
		z = (z1 < z2) ? z1 : z2;
		return (float)z;
	}

	protected void bwtf(int ln, int k, int n, float d[], float c[]) {
		int i;
		float pi, tmp;
//		pi = 4.0 * Math.atan(1.0);
		d[0] = 1;
		c[0] = 1;
		for (i = 1; i <= n; i++) {
			d[i] = 0;
			c[i] = 0;
		}
		tmp = (float) ((k + 1) - (ln + 1.0) / 2.0);
		if (tmp == 0.0) {
			c[1] = 1;
		} else {
			c[1] = (float) (-2.0 * Math.cos((2 * (k + 1) + ln - 1) * Math.PI / (2 * ln)));
			c[2] = 1;
		}
	}

	protected void fblt(float d[], float c[], int n, int band, float fln,
			float fhn, float b[], float a[],int start) {
		int i, k, m, n1, n2, ls;
		float pi, w, w0, w1, w2, tmp, tmpd, tmpc;
		float[] work;
		pi = (float) (4.0 * Math.atan(1.0));
//		w1=0.07980143;
		w1=(float) Math.tan(pi * fln);
//		w1 = (float) Math.abs(Math.tan(3.14 * fln*2));
		for (i = n; i >= 0; i--) {
			if ((c[i] != 0.0) || (d[i] != 0.0)) {
				break;
			}
		}
		
			m = i;
			switch (band) {
			case 1:
			case 2: {
//				n2 = m*2;
				n2 = m;
				n1 = n2 + 1;
				if (band == 2) {
					for (i = 0; i <= m / 2; i++) {
						tmp = d[i];
						d[i] = d[m - i];
						d[m - i] = tmp;
						tmp = c[i];
						c[i] = c[m - i];
						c[m - i] = tmp;
					}
				}
				for (i = 0; i <= m; i++) {
					d[i] = (float) (d[i] / Math.pow(w1, i));
					c[i] = (float) (c[i] / Math.pow(w1, i));
				}
				break;
			}
			case 3:
			case 4: {
//				n2=m;
				n2 = 2 * m;
				n1 = n2 + 1;
				work = new float[n1 * n1 ];
				w2=(float) Math.tan(pi * fhn);
//				w2 =(float) Math.abs(Math.tan(3.14 * fhn*2)) ;
				w = w2 - w1;
				w0 = w1 * w2;
				if (band == 4) {
					for (i = 0; i <= m / 2; i++) {
						tmp = d[i];
						d[i] = d[m - i];
						d[m - i] = tmp;
						tmp = c[i];
						c[i] = c[m - i];
						c[m - i] = tmp;
					}
				}
				for (i = 0; i <= n2; i++) {
					work[0 * n1 + i] = 0;
					work[1 * n1 + i] = 0;
				}
				for (i = 0; i <= m; i++) {
					tmpd = (float) (d[i] * Math.pow(w, m - i));
					tmpc = (float) (c[i] * Math.pow(w, m - i));
					for (k = 0; k <= i; k++) {
						ls = m + i - 2 * k;
						tmp = combin(i, i)
								/ (combin(k, k) * combin(i - k, i - k));
						work[0 * n1 + ls] += tmpd * Math.pow(w0, k) * tmp;
						work[1 * n1 + ls] += tmpc * Math.pow(w0, k) * tmp;
					}
				}
				for (i = 0; i <= n2; i++) {
					d[i] = work[0 * n1 + i];
					c[i] = work[1 * n1 + i];
				}
			}
			}
			bilinear(d, c, b, a, n,start);
	}

	protected float combin(int i1, int i2) {
		int i;
		float s;
		s = 1;
		if (i2 == 0) {

			return s;
		}
		for (i = i1; i > (i1 - i2); i--) {
			s *= i;
		}
		return s;
	}

	protected void bilinear(float d[], float c[], float b[], float a[], int n,int start) {
		int i, j, n1;
		float sum, atmp, scale = 0;
		float[] temp;
		n1 = n + 1;
		temp = new float[n1 * n1 ];
		for (j = 0; j <= n; j++) {
			temp[j * n1 + 0] = 1;
		}
		sum = 1;
		for (i = 1; i <= n; i++) {
			sum = sum * (float) (n - i + 1) / (float) i;
			temp[0 * n1 + i] = sum;
		}
		for (i = 1; i <= n; i++) {
			for (j = 1; j <= n; j++) {
				temp[j * n1 + i] = temp[(j - 1) * n1 + i]
						- temp[j * n1 + i - 1] - temp[(j - 1) * n1 + i - 1];
			}
		}
		for (i = n+start; i >= start; i--) {
			b[i] = 0;
			atmp = 0;
			for (j = start; j <= n+start; j++) {
				b[i] = b[i] + temp[(j-start) * n1 + (i-start)] * d[j-start];
				atmp = atmp + temp[(j-start) * n1 + (i-start)] * c[j-start];
			}
			scale = atmp;
			if (i != start) {
				a[i] = atmp;
			}
		}
		for (i = start; i <= n+start; i++) {
			b[i] = b[i] / scale;
			a[i] = a[i] / scale;
		}
		a[start] = 1;
	}
	
	/**
	 * 滤波过程
	 * @param b 分母系数
	 * @param a 分子系数
	 * @param order 阶数
	 * @param start 开始的位置
	 * @param input 输入待滤波的数据
	 * @return 滤波以后的结果
	 */
	protected float[] filter(float b[],float a[],int order,int start,float input[])
	{
		int length=input.length;
		float[] output=new float[length];
		output=input.clone();
		float[] px=new float[order+1];
		float[] py=new float[order+1];
		for(int k=0;k<length;k++){
			px[0]=output[k];
			output[k]=0;
			for(int i=start;i<=order+start;i++){
				output[k]=(float) (output[k]+b[i]*px[i-start]);
			}
			for(int i=start+1;i<=order+start;i++){
				output[k]=(float) (output[k]-a[i]*py[i-start]);
			}
			if(Math.abs(output[k])>1.0e10){
				
				return null;
			}
			for(int i=order+start;i>=start+1;i--){
				px[i-start]=px[i-start-1];
			}
			for(int i=order+start;i>=start+2;i--){
				py[i-start]=py[i-start-1];
			}
			py[1]=output[k];
		}
		return output;
	}

}
