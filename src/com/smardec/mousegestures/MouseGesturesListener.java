// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MouseGesturesListener.java

package com.smardec.mousegestures;


public interface MouseGesturesListener
{

	public abstract void processGesture(String s);

	public abstract void gestureMovementRecognized(String s);
}
