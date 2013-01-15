package com.github.likelion.metastock;

public class DailyPoint implements StockPoint {

	public String date; // YYMMDD
	public float open;
	public float high;
	public float low;
	public float close;
	public float volume;
	public float op_int;

	public byte[] getBytes() {
		byte[] out = MetaStock.dateToMBFByte(date);
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(open));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(high));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(low));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(close));
		out = MetaStock.append(out, MetaStock.ieeeFloatToMBFByte(volume));
		out = MetaStock.append(out, new byte[4]);
		return out;
	}
	
	public String toString() {
		return date + " open="+open+", high="+high+", low="+low+", close="+close+", volume="+(long)volume;
	}
	
}