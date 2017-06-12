package com.nari.slsd.hms.hdu.common.algorithm;

import com.nari.slsd.hms.hdu.common.data.Complex;
import com.nari.slsd.hms.hdu.common.data.NetSwingBean;

public class Calculate extends CalResult {

	/**
	 * 计算， 默认就是数据输入 计算峰峰值，计算频谱 不计算超重量
	 * 
	 * @param datas
	 *            数据
	 * @param indexs
	 *            键向点
	 */
	public Calculate(float[] datas, int[] indexs) {
		this(datas, indexs, true, true, false);
	}

	/**
	 * 
	 * @param datas
	 *            数据
	 * @param indexs
	 *            键向点
	 * @param isCalSwing
	 *            是否计算超重量
	 */
	public Calculate(float[] datas, int[] indexs, boolean isCalFFT) {
		this(datas, indexs, true, isCalFFT, false);
	}

	/**
	 * 
	 * @param datas
	 *            数据
	 * @param indexs
	 *            键向点
	 * @param isCalPeak
	 *            是否计算峰峰值
	 * @param isCalFFT
	 *            是否计算FFT
	 * @param isCalSwing
	 *            是否计算超重量
	 * 
	 */
	public Calculate(float[] datas, int[] indexs, boolean isCalPeak,
			boolean isCalFFT, boolean isCalSwing) {

		// fft需要2的阶层的数据，所以补零
		size = datas.length;
		int n = (int) Math.ceil(Math.log((double) size) / Math.log(2));
		int length = (int) Math.pow(2, n);

		complexs = new Complex[length];

		this.datas = datas;
		this.indexs = indexs;

		for (int i = 0; i < size; i++) {
			complexs[i] = new Complex(datas[i], 0);
		}
		for (int j = size; j < length; j++) {
			complexs[j] = new Complex(0, 0);
		}

		if (isCalPeak)
			CalWavePeak();
		if (isCalFFT)
			CalFFT();
		if (isCalSwing)
			ComputeTotalSwing(datas, indexs);// 计算超重量

		// 计算最大的模，用于归一化在姿态图上的显示
		moduleMAX = Math.max(Math.abs(dataMax), Math.abs(dataMin));
	}

	/**
	 * 计算FFT
	 * 
	 * @param datas
	 * @param isCalFFT
	 *            一般都设置为true
	 */
	public Calculate(float[] datas, boolean isCalFFT) {
		// fft需要2的阶层的数据，所以补零
		int length = datas.length;
		int n = (int) Math.ceil(Math.log((double) length) / Math.log(2));
		size = (int) Math.pow(2, n);

		complexs = new Complex[size];

		this.datas = datas;

		for (int i = 0; i < length; i++) {
			complexs[i] = new Complex(datas[i], 0);
		}
		for (int j = length; j < size; j++) {
			complexs[j] = new Complex(0, 0);
		}
		if (isCalFFT)
			CalFFT();
	}

	// ����ļӷ�
	public static Complex complexAdd(Complex complexA, Complex complexB) {
		return new Complex(complexA.getReal() + complexB.getReal(),
				complexA.getImage() + complexB.getImage());
	}

	// ����ļ���
	public static Complex complexSubstract(Complex complexA, Complex complexB) {
		return new Complex(complexA.getReal() - complexB.getReal(),
				complexA.getImage() - complexB.getImage());
	}

	public static Complex complexMultiply(Complex complexA, Complex complexB) {// ����ļӷ�
		return new Complex(complexA.getReal() * complexB.getReal()
				- complexB.getImage() * complexA.getImage(), complexA.getReal()
				* complexB.getImage() + complexA.getImage()
				* complexB.getReal());
	}
	
	public static Complex complexDivise(Complex complexA, Complex complexB) {// ����ļӷ�
		Complex temp = new Complex(complexA.getReal() * complexB.getReal()
				+ complexB.getImage() * complexA.getImage(), complexA.getReal()
				* complexB.getImage() - complexA.getImage()
				* complexB.getReal());
		return new Complex(temp.getReal()/getCompexAmplify(complexB), temp.getImage()/getCompexAmplify(complexB));
	}

	
	public static float getCompexAmplify(Complex input){
		return (float) (input.getReal()*input.getReal()+input.getImage()*input.getImage());
	}
	// ����������ʽ
	public String complexToString(Complex complex) {
		String str = "";
		if (complex.getImage() < 0) {
			return complex.getReal() + complex.getImage() + "*i";
		} else {
			return complex.getReal() + "+" + complex.getImage() + "*i";
		}
	}

	/**
	 * rfft计算，输入的数据可以不为2的阶层
	 * 
	 * @param value
	 *            ，实数输入，实数（幅值）输出
	 * 
	 * @return
	 */
	public static Complex[] rfft_xyaxis(float value[]) {
		float length = value.length;
		float n = (int) Math.ceil(Math.log((double) length) / Math.log(2));
		length = (float) Math.pow(2, n);
		Complex[] inputComplex = new Complex[(int) length];
		for (int i = 0; i < value.length; i++) {
			inputComplex[i] = new Complex(value[i], 0);
		}
		for (int j = value.length; j < length; j++) {
			inputComplex[j] = new Complex(0, 0);
		}

		Complex out[] = FFT.fft(inputComplex);
		return out;
//		float A_value[] = new float[out.length / 2];
//		for (int j = 0; j < out.length / 2; j++) {
//			A_value[j] = (float) ((Math.sqrt(out[j].getReal()
//					* out[j].getReal() + out[j].getImage() * out[j].getImage()))
//					/ out.length * 2);
//		}
//		return A_value;
	}

	/**
	 * fft计算，输入的数据可以不为2的阶层
	 * 
	 * @param value
	 *            ,复数输入，复数输出
	 * 
	 * @return
	 */
	public static Complex[] fft_xyaxis(Complex value[]) {
		double length = value.length;
		double n = (int) Math.ceil(Math.log((double) length) / Math.log(2));
		length = Math.pow(2, n);
		Complex[] inputComplex = new Complex[(int) length];
		for (int i = 0; i < value.length; i++) {
			inputComplex[i] = value[i];
		}
		for (int j = value.length; j < length; j++) {
			inputComplex[j] = new Complex(0, 0);
		}

		Complex out[] = FFT.fft(inputComplex);// 转换完的点阵
		return out;

	}

	/**
	 * irfft计算，傅里叶逆变换，输入的数据可以不为2的阶层
	 * 
	 * @param value
	 *            ,复数输入，实数输出
	 * 
	 * @return
	 */
	public static float[] irfft_xyaxis(Complex value[]) {
		float length = value.length;
		float n = (int) Math.ceil(Math.log((float) length) / Math.log(2));
		length = (float) Math.pow(2, n);
		Complex[] inputComplex = new Complex[(int) length];
		for (int i = 0; i < value.length; i++) {
			inputComplex[i] = value[i];
		}
		for (int j = value.length; j < length; j++) {
			inputComplex[j] = new Complex(0, 0);
		}

		Complex out[] = FFT.inverse_FFT(inputComplex);// 转换完的点阵
		float A_value[] = new float[out.length];
		for (int j = 0; j < out.length; j++) {
			A_value[j] = (float) out[j].getReal();
		}
		return A_value;

	}

	/**
	 * 计算距离一个数最接近的2的指数次的数值 比如 输入 10 输出 8 输入20输出 16
	 * 
	 * @param length
	 * @return
	 */
	public static int findMaxPow2(int num) {
		int N = num;
		int temp = 2;
		while ((temp <<= 1) < N)
			;
		N = temp >> 1;
		return N;
	}

	// 有阈值0.01
	protected void findMax2(float[] x) {
		if (x.length == 0) {
			return;
		}
		first_index = 0;
		second_index = 0;
		for (int i = 0; i < x.length; i++) {
			if (x[i] - x[first_index] > 0.01) {
				second_index = first_index;
				first_index = i;
			}
		}
	}

	/**
	 * 
	 * @param x
	 */
	protected void findMax(float[] x) {
		if (x.length == 0) {
			return;
		}
		first_index = 1;
		second_index = 1;
		for (int i = 1; i < x.length; i++) {
			if (x[first_index] < x[i]) {
				second_index = first_index;
				first_index = i;
			}
		}
	}

	/**
	 * 求x数组的最大最小值
	 * 
	 * @param x
	 *            float数组
	 * @return 最大x[1] 最小值x[0]
	 */
	public static float[] findMaxMin(float[] x) {
		// 第一个是最小值，第二个是最大值
		float m[] = { Float.MAX_VALUE, Float.MIN_VALUE };

		for (int i = 0; i < x.length; i++) {
			m[0] = Math.min(m[0], x[i]);
			m[1] = Math.max(m[1], x[i]);
		}
		return m;
	}

	/**
	 * 计算峰峰值
	 * 
	 * @param datas
	 *            一路数据
	 */
	protected void CalWavePeak() {
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;

		for (int i = 0; i < datas.length; i++) {
			max = (float) Math.max(max, datas[i]);
			min = (float) Math.min(min, datas[i]);
		}

		this.dataMax = max;
		this.dataMin = min;
		this.peak = max - min;
	}

	/**
	 * 计算峰峰值
	 * 
	 * @param datas
	 *            一路数据
	 * @return [0]最大值， [1]最小值， [2]峰峰值
	 */
	public static float[] CalWavePeak(float[] datas) {
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		float ret[] = { 0, 0, 0 };

		for (int i = 0; i < datas.length; i++) {
			max = (float) Math.max(max, datas[i]);
			min = (float) Math.min(min, datas[i]);
		}

		ret[0] = max;
		ret[1] = min;
		ret[2] = max - min;
		return ret;
	}

	/**
	 * 计算峰峰值
	 * 
	 * @param datas
	 *            一路数据
	 */
	protected void CalWavePeak(float[] datas, int[] indexs) {
		float max_sum = 0;
		float min_sum = 0;

		for (int j = 0; j < indexs.length - 1; j++) {
			float max = Float.MIN_VALUE;
			float min = Float.MAX_VALUE;
			for (int i = indexs[j]; i < indexs[j + 1]; i++) {
				max = (float) Math.max(max, datas[i]);
				min = (float) Math.min(min, datas[i]);
			}
			max_sum += max;
			min_sum += min;
		}
		max_sum /= (indexs.length - 1);
		min_sum /= (indexs.length - 1);

		this.dataMax = max_sum;
		this.dataMin = min_sum;
		this.peak = max_sum - min_sum;
	}

	/**
	 * 计算幅值和相位
	 */
	protected void CalFFT() {
		// FFT计算，统计最大幅值和得到对应的相位
		complexs = FFT.fft(complexs);

		float vlaue = 0;// 幅值
		float phase = 0;// 对应最大幅值的相位

		FFTAbs = new float[size / 2];
		FFTPhase = new float[size / 2];

		for (int i = 0; i < size / 2; i++) {
			FFTAbs[i] = (float) complexs[i].abs() / size * 2;
			FFTPhase[i] = (float) (complexs[i].phase() / Math.PI * 180);

			if (vlaue < FFTAbs[i]) {
				vlaue = FFTAbs[i];
				phase = FFTPhase[i];
			}
		}
		FFTAbs[0] /= 2;
		findMax2(FFTAbs);
		this.amplitude1X = vlaue;
		this.phase1X = phase;

	}

	// 全摆度、净全摆度计算
	public int ComputeTotalSwing(float[] datas, int[] phaseIndex) {
		int flag = 0;
		int index[] = { 0, 0 };

		if (phaseIndex.length < 2)// 键相点个数不足标识一个周期
		{
			System.out
					.println("hdu.posture.data.DataCalculate:ComputeTotalSwing length less 2");
			phaseIndex = new int[2];
			phaseIndex[0] = 0;
			phaseIndex[1] = 1023;// 无键相信号,采用默认键相信号处理
		}
		int nLen = phaseIndex[1] - phaseIndex[0] + 1;// 计算一周的长度
		index[1] = phaseIndex[1];
		index[0] = phaseIndex[0];
		if (nLen % 2 != 0) {
			index[1] -= 1;// 保证截取的数据段长度为偶数
			nLen--;
		}

		if (index[0] < 0 || index[1] >= datas.length) {
			System.out
					.println("hdu.posture.data.DataCalculate:ComputeTotalSwing out limit");
			return -3;
		}
		// 1. 截取数据，分出前半周和后半周数据
		// 2. 计算前半周和后半周之差的最大值（模）
		int nHalf = nLen / 2;

		if (datas.length > nHalf) {
			System.out
					.println("hdu.posture.data.DataCalculate:ComputeTotalSwing out limit");
			return -3;
		}
		float max = Math.abs(datas[nHalf] - datas[0]);
		float tmp = 0.0f;
		int nMaxIndex = 0;
		for (int i = 0; i < nHalf; i++) {
			tmp = Math.abs(datas[nHalf + i] - datas[i]);
			if (tmp > max) {
				nMaxIndex = i;
				max = tmp;
			}
		}
		// 3. 返回结果(摆度可能为负，应根据x,y的正负确定方位)
		// 此处还需要再做调整
		this.TotalSwing = max;

		// 单侧摆度大的方位作为最大全摆度的方位
		if (Math.abs(datas[nMaxIndex]) > Math.abs(datas[nHalf + nMaxIndex])) {
			this.OverAngle = (float) (nMaxIndex * 360.0 / (nLen - 1));
			this.LossAngle = (float) (180 + nMaxIndex * 360.0 / (nLen - 1));

		} else {
			this.LossAngle = (float) (nMaxIndex * 360.0 / (nLen - 1));
			this.OverAngle = (float) (180 + nMaxIndex * 360.0 / (nLen - 1));

		}

		return flag;
	}

	/**
	 * 计算净全摆度 外部调用，传入两个面的X通道信息，
	 * 
	 * @param Data
	 *            一个平面的x轴数据
	 * @param UpData
	 *            上面一个平面的X轴数据
	 * @param phaseIndex
	 *            键向信号
	 * @param phaseCount
	 * @return 计算得到的摆度
	 */
	public static NetSwingBean ComputeNetSwing(float[] Data, float[] UpData,
			int[] phaseIndex) {
		NetSwingBean bean = new NetSwingBean();
		int index[] = { 0, 0 };

		if (phaseIndex.length < 2)// 键相点个数不足标识一个周期
		{
			System.out
					.println("hdu.posture.data.DataCalculate:ComputeNetSwing length less 2");
			phaseIndex = new int[2];
			phaseIndex[0] = 0;
			phaseIndex[1] = 1023;// 无键相信号,采用默认键相信号处理
		}
		int nLen = phaseIndex[1] - phaseIndex[0] + 1;// 计算一周的长度
		index[1] = phaseIndex[1];
		index[0] = phaseIndex[0];
		if (nLen % 2 != 0) {
			index[1] -= 1;// 保证截取的数据段长度为偶数
			nLen--;
		}

		if (index[0] < 0 || index[1] >= Data.length) {
			System.out
					.println("hdu.posture.data.DataCalculate:ComputeNetSwing out limit");
			return bean;
		}
		// 1. 截取数据，分出前半周和后半周数据
		// 2. 计算前半周和后半周之差的最大值（模）
		int nHalf = nLen / 2;

		float max = Math.abs(Math.abs(Data[nHalf] - UpData[nHalf])
				- Math.abs(Data[0] - UpData[0]));
		float tmp = 0.0f;
		int nMaxIndex = 0;
		for (int i = 0; i < nHalf; i++) {
			// 摆度值可正可负
			tmp = Math.abs((Data[nHalf + i] - UpData[nHalf + i])
					- (Data[i] - UpData[i]));
			if (tmp > max) {
				nMaxIndex = i;
				max = tmp;
			}
		}

		// 3. 返回结果
		bean.setAmptitude(max);// 最大净全摆度

		// 单侧净摆度大的方位作为最大净全摆度的方位
		if (Math.abs(Data[nMaxIndex] - UpData[nMaxIndex]) > Math.abs(Data[nHalf
				+ nMaxIndex]
				- UpData[nHalf + nMaxIndex])) {
			bean.setPhase((float) (nMaxIndex * 360.0 / (nLen - 1)));

		} else {
			bean.setPhase((float) (180 + nMaxIndex * 360.0 / (nLen - 1)));
		}

		return bean;
	}

	
	/**
	 * 整周期FFT
	 * 
	 * @param datas
	 *            
	 */
	public static float[] CNiWaveFraqView(float p1[],int sampleFre) {
		int n1, n2;
		double dt1;
		float T;

		n1 = p1.length;
		n2 = (int) Math.pow(2.0, (int) (Math.log((float) n1) / Math.log(2.0)));
		float[] p2 = new float[n2];
		dt1 = 1.0 / (float) sampleFre;
		T = (float) p1.length / (float) sampleFre;

		int jj;
		float t, dt2;

		dt2 = T / (float) n2;
		jj = 0;
		for (int i = 0; i < n2; i++) {
			t = dt2 * (float) i;
			for (int j = jj; j < n1; j++) {
				if (t >= dt1 * j && t <= dt1 * j + dt1) {
					p2[i] = (float) (p1[j] + (p1[j + 1] - p1[j]) / dt1
							* (t - dt1 * (float) j));
					jj = j;
					break;
				}
			}
		}
		return p2;
	}
	
	/**
	 * 
	 * @param data 输入数据
	 * @return 得到平均值
	 */
	public static float getAve(float[] data)
	{
		float sum = 0;
		for (int i = 0; i < data.length; i++)
		{
			sum += data[i];
		}
		return sum/data.length;
	}
	
	/**
	 * 
	 * @param input 输入数据
	 * @return 得到有效值
	 */
	public static float RMS(float input[])
	{
//		float[] output = new float[input.length - width];
		float output;
		float sum=0;
		for(int i=0;i<input.length;i++)
		{
			sum+=input[i]*input[i];
		}
		output=(float) Math.sqrt(sum/input.length);
		return output;
	}
	
	/**
	 * 
	 * @param datas 输入数据
	 * @return 得到中间数
	 */
	public static float MID(float datas[]) {
		float mid;
		float output=0;
		float[] input=datas.clone();
		int len=input.length;
		for (int m = 0; m < len; m++) {
			for (int n = 0; n < len - m - 1; n++) {
				if (input[n] > input[n + 1]) {
					mid = input[n + 1];
					input[n + 1] = input[n];
					input[n] = mid;
				}
			}
		}
		if (len / 2 == 0) {
			output = (input[len / 2] + input[len / 2 + 1]) / 2;
		} else {
			output = input[(len+1) / 2];
		}
		
		return output;
	}
	
	/**
	 * 将数据从小到大排列
	 * @param datas 输入数据
	 *  
	 */
	public static void orderMintoMax(float datas[]) {
		float mid;
		int len=datas.length;
		for (int m = 0; m < len; m++) {
			for (int n = 0; n < len - m - 1; n++) {
				if (datas[n] > datas[n + 1]) {
					mid = datas[n + 1];
					datas[n + 1] = datas[n];
					datas[n] = mid;
				}
			}
		}

	}
	
	/**
	 * 
	 * @param datas 输入数据
	 * @deprecated 将数据从大到小排列
	 */
	public static int[] orderMaxtoMin(float datas[]) {
		int indexArray[] = new int[datas.length];
		float tempArray[] = datas.clone();
		for (int i = 0; i < indexArray.length; i++)
		{
			indexArray[i] = i;
		}
		float mid;
		int indexmid;
		int len=tempArray.length;
		for (int m = 0; m < len; m++) {
			for (int n = 0; n < len - m - 1; n++) {
				if (tempArray[n] < tempArray[n + 1]) {
					indexmid = indexArray[n+1];
					mid = tempArray[n + 1];
					
					tempArray[n + 1] = tempArray[n];
					indexArray[n+1] = indexArray[n];
					
					tempArray[n] = mid;
					indexArray[n] = indexmid;
					
				}
			}
		}
		return indexArray;

	}
	
	/**
	 *  在一个序列中，找到某个数，并返回位置
	 * @param datas 输入数据
	 * @param value 需要找的目标值
	 * @return 得到目标值所在的位置
	 * 
	 */
	public static int findValue(float datas[],float value) {
		int location=0;
		for(int i=0;i<datas.length;i++){
			if(Math.abs(value-datas[i])<0.000001)
			{
				location=i;
				break;
			}
		}
		return location;
	}
	
	/**
	 * 
	 * @param input 输入数据
	 * @return 得到相位值
	 */
	public static float getPhase(Complex input) {
		float phase;
		phase = (float) (input.phase()*180/(double)Math.PI);
//		phase=(float) Math.atan(input.getImage()/(double)input.getReal());
//		phase = (float) (phase *180/(double)Math.PI);
		return phase;
	}
	
	/**
	 * 在一个序列中，找出离某个数最接近的数，并返回位置
	 * @param shuzu 输入序列
	 * @param value 目标值
	 * @return 得到最接近的位置
	 * 
	 */
	public static int getNearPosition(float shuzu[], float value)
	{
		float temp = Math.abs(value - shuzu[0]);
		int j = 0;
		for (int i = 1; i < shuzu.length; i++)
		{
			if (Math.abs(value - shuzu[i]) - temp < 0.00001)
			{
				temp = value - shuzu[i];
				j = i;
			}
			if (value < shuzu[i])
				break;
		}
		return j;
	}
	
	
	/**
	 * 
	 * @param amplify 幅值
	 * @param phase 相位
	 * @return 得到复数值
	 */
	public static Complex getComplex(float amplify,float phase){
		float real = (float) (amplify * Math.cos(phase));
		float image = (float) (amplify * Math.sin(phase));
		return new Complex(real, image);
	}
	
	/**
	 * 
	 * @param v0 配重前
	 * @param v1 v1试重后
	 * @param q1 为输入的试重块
	 * @return 应加试重块
	 * @deprecated 动平衡配重算法
	 */
	public static Complex dynamicBalance(Complex v0,Complex v1,Complex q1){
		Complex outPut;
		Complex Denominator = complexSubstract(v0, v1);//分母
		Complex numerator = complexMultiply(v0,q1);//分子
		outPut = complexDivise(numerator, Denominator);
		return outPut;
	}
}
