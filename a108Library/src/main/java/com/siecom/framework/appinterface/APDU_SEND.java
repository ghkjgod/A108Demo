package com.siecom.framework.appinterface;

public class APDU_SEND
{
  public byte[] Command = new byte[4];
  public byte[] DataIn = new byte[2048];
  public short Lc;
  public short Le;

  public APDU_SEND()
  {
  }

  public APDU_SEND(byte[] Command, short Lc, byte[] DataIn, short Le)
  {
    this.Command = Command;
    this.Lc = Lc;
    this.DataIn = DataIn;
    this.Le = Le;
  }
}