package com.github.likelion.metastock;

public class IntradayPoint implements StockPoint {

	public String date; // YYMMDD
	public String time; // HH:MM
	public float open;
	public float high;
	public float low;
	public float close;
	public float volume;
	public float op_int;

	public byte[] getBytes() {
		byte[] out = MetaStock.dateToMBFByte(date);
		out = MetaStock.append(out, MetaStock.timeToMBFByte(time));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(open));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(high));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(low));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(close));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(volume));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(op_int));
		return out;
	}
	
	public String toString() {
		return date + " " + time + " open="+open+", high="+high+", low="+low+", close="+close+", volume="+(long)volume+", o/i="+op_int;
	}

}