/**
 * 
 */
package com.nari.slsd.hms.hdu.common.algorithm;

import org.jfree.util.Log;

import com.nari.slsd.hd.tools.CommonUtil;
import com.nari.slsd.hms.hdu.common.data.Complex;

/**
 * Created :2014-11-21 ����9:37:45 Describe : Class : FilterFir.java love you
 * 
 * 
 */
public class FirFilter {

	/**
	 * 滤波器设计
	 * @param n 阶数
	 * @param band 滤波类型
	 * @param fln 低通频率
	 * @param fhn 高通频率
	 * @param wn 窗函数类型
	 * @param input 输入数据
	 * @return
	 */
	public float[] FirWin(int n, int band, float fln, float fhn, int wn, float input[]) {
		// AFX_MANAGE_STATE(AfxGetStaticModuleState());
		// n=20;
		// fln=0.04;
		// fhn=1;
		float[] h = new float[n + 1];
		int n2, mid;
		float s, pi, wc1, wc2 = 0, beta, delay;
		float[] output;
		// float window();
		beta = 0;
		if (wn == 7) {
			System.out.println("input beta parameter of Kaiser window(3<beta<10)\n");
			// printf("input beta parameter of Kaiser window(3<beta<10)\n");
			// scanf("%lf",&beta);
		}
		pi = (float) (4.0 * Math.atan(1.0));
		if (n % 2 == 0) {
			n2 = n / 2 - 1;
			mid = 1;
		} else {
			n2 = n / 2;
			mid = 0;
		}
		delay = (float) (n / 2.0);
		wc1 = (float) (2.0 * pi * fln);
		if(band==2){
			wc1 = (float) (2.0 * pi * fhn);}
		if (band >= 3)
			wc2 = (float) (2.0 * Math.PI * fhn);
		switch (band) {
		case 1: {
			for (int i = 0; i <= n2; i++) {
				s = i - delay;
				h[i] = (float) ((Math.sin(wc1 * s) / (pi * s)) * window(wn, n + 1, i, beta));
				h[n - i] = h[i];
			}
			if (mid == 1)
				h[n / 2] = wc1 / pi;
			break;
		}

		case 2: {
			for (int i = 0; i <= n2; i++) {
				s = i - delay;
				h[i] = (float) ((Math.sin(pi * s) - Math.sin(wc1 * s)) / (pi * s));
				h[i] = h[i] * window(wn, n + 1, i, beta);
				h[n - i] = h[i];
			}
			if (mid == 1)
				h[n / 2] = (float) (1.0 - wc1 / pi);
			break;
		}
		case 3: {
			for (int i = 0; i <= n2; i++) {
				s = i - delay;
				h[i] = (float) ((Math.sin(wc2 * s) - Math.sin(wc1 * s)) / (pi * s));
				h[i] = h[i] * window(wn, n + 1, i, beta);
				h[n - i] = h[i];
			}
			if (mid == 1)
				h[n / 2] = (wc2 - wc1) / pi;
			break;
		}
		case 4: {
			for (int i = 0; i <= n2; i++) {
				s = i - delay;
				h[i] = (float) ((Math.sin(wc1 * s) + Math.sin(pi * s) - Math.sin(wc2 * s)) / (pi * s));
				h[i] = h[i] * window(wn, n + 1, i, beta);
				h[n - i] = h[i];
			}
			if (mid == 1)
				h[n / 2] = (wc1 + pi - wc2) / pi;
			break;
		}
		default:
			break;
		}
		// float ad=0.0; //��һ��
		// int ii;
		// for (ii=0;ii<n+1;ii++)
		// {
		// ad+=h[ii];
		// }
		// for (ii=0;ii<n+1;ii++)
		// {
		// h[ii]=h[ii]/ad;
		// }

		output = convol(h, input);

		return output;
	}

	public float window(int type, int n, int i, float beta) {
		int k;
		float pi, w;
		// float kaiser();
		pi = (float) (4.0 * Math.atan(1.0));
		w = 1;
		switch (type) {
		case 1: {
			w = 1;
			break;
		}
		case 2: {
			k = (n - 2) / 10;
			if (i <= k)
				w = (float) (0.5 * (1.0 - Math.cos(i * pi / (k + 1))));
			if (i > n - k - 2)
				w = (float) (0.5 * (1.0 - Math.cos((n - i - 1) * pi / (k + 1))));
			break;
		}
		case 3: {
			w = (float) (1.0 - Math.abs(1.0 - 2 * i / (n - 1.0)));
			break;
		}
		case 4: {
			w = (float) (0.5 * (1.0 - Math.cos(2 * pi * i / (n - 1))));
			break;
		}
		case 5: {
			w = (float) (0.54 - 0.46 * (Math.cos(2 * pi * i / (n - 1))));
			break;
		}
		case 6: {
			w = (float) (0.42 - 0.5 * Math.cos(2 * pi * i / (n - 1)) + 0.08 * Math.cos(4 * i * pi / (n - 1)));
			break;
		}
		case 7: {
			w = kaiser(i, n, beta);
			break;
		}
		default:
			break;
		}
		return w;
	}

	static float kaiser(int i, int n, float beta) {
		float a, w, a2, b1, b2, beta1;
		// float bessel0();
		b1 = bessel0(beta);
		a = (float) (2.0 * i / (float) (n - 1) - 1.0);
		a2 = a * a;
		beta1 = (float) (beta * Math.sqrt(1.0 - a2));
		b2 = bessel0(beta1);
		w = b2 / b1;
		return (w);
	}

	static float bessel0(float x) {
		int i;
		float d, y, d2, sum;
		y = (float) (x / 2.0);
		d = 1;
		sum = 1;
		for (i = 1; i <= 25; i++) {
			d = d * y / i;
			d2 = d * d;
			sum = sum + d2;
			if (d2 < sum * (1.0e-8))
				break;
		}
		return (sum);
	}

	/**
	 * 滤波过程，采用快速卷积
	 * @param 滤波器系数
	 * @param input 带=待滤波的数据
	 * @return 滤波以后结果
	 */
	
	private float[] convol(float h[], float input[]) {

		Complex[] h1;
		Complex[] input1;
		Complex[] hFft;
		Complex[] inputFft;
		float[] ifftOutput;
		float[] output;
		output = new float[input.length];
		float n = (int) Math.ceil(Math
				.log((float) (h.length + input.length - 1)) / Math.log(2));

		int length = (int) Math.pow(2, n);
		h1 = new Complex[length];
		input1 = new Complex[length];
		for (int j = 0; j < h.length; j++) {
			h1[j] = new Complex(h[j], 0);
		}
		for (int j = 0; j < input.length; j++) {
			input1[j] = new Complex(input[j], 0);
		}
		for (int j = h.length; j < length; j++) {
			h1[j] = new Complex(0, 0);
		}
		for (int j = input.length; j < length; j++) {
			input1[j] = new Complex(0, 0);
		}
		hFft = Calculate.fft_xyaxis(h1);
		inputFft = Calculate.fft_xyaxis(input1);
		for (int j = 0; j < length; j++) {
			hFft[j] = Calculate.complexMultiply(hFft[j], inputFft[j]);
		}
		ifftOutput = Calculate.irfft_xyaxis(hFft);
		for (int k = 0; k < output.length; k++) {
			output[k] = ifftOutput[k];
		}
		return output;
	}
}
