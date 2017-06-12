package com.nari.slsd.hms.hdu.common.algorithm;

import com.nari.slsd.hms.hdu.common.data.Complex;


public class FFT
{
	// compute the FFT of x[], assuming its length is a power of 2
	public static Complex[] fft(Complex[] x)
	{
		int N = x.length;

		// base case
		if (N == 1)
			return new Complex[] { x[0] };
		
		// radix 2 Cooley-Tukey FFT
		if (N % 2 != 0)
		{
			throw new RuntimeException("N is not a power of 2");	
		}

		// fft of even terms
		Complex[] even = new Complex[N / 2];
		for (int k = 0; k < N / 2; k++)
		{
			even[k] = x[2 * k];
		}
		Complex[] q = fft(even);

		// fft of odd terms
		Complex[] odd = even; // reuse the array
		for (int k = 0; k < N / 2; k++)
		{
			odd[k] = x[2 * k + 1];
		}
		Complex[] r = fft(odd);

		// combine
		Complex[] y = new Complex[N];
		for (int k = 0; k < N / 2; k++)
		{
			double kth = -2 * k * Math.PI / N;
			Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
			y[k] = q[k].plus(wk.times(r[k]));
			y[k + N / 2] = q[k].minus(wk.times(r[k]));
		}
		return y;
	}

	// compute the inverse FFT of x[], assuming its length is a power of 2
	public static Complex[] ifft(Complex[] x)
	{
		int N = x.length;
		Complex[] y = new Complex[N];

		// take conjugate
		for (int i = 0; i < N; i++)
		{
			y[i] = x[i].conjugate();
		}

		// compute forward FFT
		y = fft(y);

		// take conjugate again
		for (int i = 0; i < N; i++)
		{
			y[i] = y[i].conjugate();
		}

		// divide by N
		for (int i = 0; i < N; i++)
		{
			y[i] = y[i].times(1.0 / N);
		}

		return y;

	}

	// compute the circular convolution of x and y
	public static Complex[] cconvolve(Complex[] x, Complex[] y)
	{

		// should probably pad x and y with 0s so that they have same length
		// and are powers of 2
		if (x.length != y.length)
		{
			throw new RuntimeException("Dimensions don't agree");
		}

		int N = x.length;

		// compute FFT of each sequence
		Complex[] a = fft(x);
		Complex[] b = fft(y);

		// point-wise multiply
		Complex[] c = new Complex[N];
		for (int i = 0; i < N; i++)
		{
			c[i] = a[i].times(b[i]);
		}

		// compute inverse FFT
		return ifft(c);
	}

	// compute the linear convolution of x and y
	public static Complex[] convolve(Complex[] x, Complex[] y)
	{
		Complex ZERO = new Complex(0, 0);

		Complex[] a = new Complex[2 * x.length];
		for (int i = 0; i < x.length; i++)
			a[i] = x[i];
		for (int i = x.length; i < 2 * x.length; i++)
			a[i] = ZERO;

		Complex[] b = new Complex[2 * y.length];
		for (int i = 0; i < y.length; i++)
			b[i] = y[i];
		for (int i = y.length; i < 2 * y.length; i++)
			b[i] = ZERO;

		return cconvolve(a, b);
	}

	// display an array of Complex numbers to standard output
	public static void show(Complex[] x, String title)
	{
		System.out.println(title);
		System.out.println("-------------------");
		for (int i = 0; i < x.length; i++)
		{
			System.out.println(x[i]);
		}
		System.out.println();
	}
	

	public static Complex[] PInverse_FFT(Complex[] inputComplex)
	{
		int n = inputComplex.length;
		if (n == 1)
		{
			return inputComplex;
		}
		Complex w = new Complex(Math.cos((2 * Math.PI) / n),
				Math.sin((2 * Math.PI) / n));
		Complex x = new Complex(1, 0);
		Complex outputComplex[] = new Complex[n];

		Complex inputComplex_even[] = new Complex[n / 2];// ���ż���±���ֵ
		Complex inputComplex_odd[] = new Complex[n / 2];// ��������±���ֵ

		int j = 0, k = 0;
		for (int i = 0; i < inputComplex.length; i++)
		{// ��ż���
			if (i % 2 == 0)
			{
				inputComplex_even[j] = inputComplex[i];
				j++;
			} else
			{
				inputComplex_odd[k] = inputComplex[i];
				k++;
			}
		}

		Complex y_even[] = PInverse_FFT(inputComplex_even);
		Complex y_odd[] = PInverse_FFT(inputComplex_odd);

		for (int m = 0; m <= ((n / 2) - 1); m++)
		{
			outputComplex[m] = Calculate.complexAdd(y_even[m],
					Calculate.complexMultiply(x, y_odd[m]));
			outputComplex[m + (n / 2)] = Calculate.complexSubstract(y_even[m],
					Calculate.complexMultiply(x, y_odd[m]));
			x = Calculate.complexMultiply(x, w);
		}
		return outputComplex;
	}

	public static Complex[] inverse_FFT(Complex[] inputComplex)
	{
		Complex[] output1 = PInverse_FFT(inputComplex);
		for (int i = 0; i < inputComplex.length; i++)
		{
			output1[i].setReal(output1[i].getReal() / (output1.length));
			output1[i].setImage(output1[i].getImage() / (output1.length));
		}
		return output1;
	}

	public static Complex[][] fft2(Complex[][] inputComplex2)
	{

		Complex outputComplex2[][] = new Complex[inputComplex2.length][inputComplex2.length];
		Complex interComplex = new Complex(0, 0);
		for (int i = 0; i < inputComplex2.length; i++)// �б任
		{
			outputComplex2[i] = fft(inputComplex2[i]);

		}

		for (int i = 0; i < outputComplex2.length; i++)// ����ת��
		{
			for (int j = 0; j < i; j++)
			{
				interComplex = outputComplex2[i][j];
				outputComplex2[i][j] = outputComplex2[j][i];
				outputComplex2[j][i] = interComplex;
			}
		}

		for (int i = 0; i < inputComplex2.length; i++)// �ٴ��б任
		{
			outputComplex2[i] = fft(outputComplex2[i]);
		}
		for (int i = 0; i < inputComplex2.length; i++)// ����ת��
		{
			for (int j = 0; j < i; j++)
			{
				interComplex = outputComplex2[i][j];
				outputComplex2[i][j] = outputComplex2[j][i];
				outputComplex2[j][i] = interComplex;
			}
		}
		return outputComplex2;
	}

	public static Complex[][] inverse_fft2(Complex[][] inputComplex2)
	{

		Complex outputComplex2[][] = new Complex[inputComplex2.length][inputComplex2.length];
		Complex interComplex = new Complex(0, 0);
		for (int i = 0; i < inputComplex2.length; i++)// �б任
		{
			outputComplex2[i] = inverse_FFT(inputComplex2[i]);

		}

		for (int i = 0; i < outputComplex2.length; i++)// ����ת��
		{
			for (int j = 0; j < i; j++)
			{
				interComplex = outputComplex2[i][j];
				outputComplex2[i][j] = outputComplex2[j][i];
				outputComplex2[j][i] = interComplex;
			}
		}

		for (int i = 0; i < inputComplex2.length; i++)// �ٴ��б任
		{
			outputComplex2[i] = inverse_FFT(outputComplex2[i]);
		}
		for (int i = 0; i < inputComplex2.length; i++)// ����ת��
		{
			for (int j = 0; j < i; j++)
			{
				interComplex = outputComplex2[i][j];
				outputComplex2[i][j] = outputComplex2[j][i];
				outputComplex2[j][i] = interComplex;
			}
		}
		return outputComplex2;
	}
}
