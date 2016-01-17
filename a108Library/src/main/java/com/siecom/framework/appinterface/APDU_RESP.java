package com.siecom.framework.appinterface;

public class APDU_RESP
{
  public short LenOut;
  public byte[] DataOut = new byte[2048];
  public byte SWA;
  public byte SWB;

  public APDU_RESP()
  {
  }

  public APDU_RESP(short LenOut, byte[] DataOut, byte SWA, byte SWB)
  {
    this.LenOut = LenOut;
    this.DataOut = DataOut;
    this.SWA = SWA;
    this.SWB = SWB;
  }
}