package com.github.likelion.metastock;

import java.util.Vector;


public class Stock {

	public int fileNum;
	public boolean fileType = false; // false = 7 fields, true = 5 fields
	public String stockName;
	public String firstDate; // YYMMDD
	public String lastDate; // YYMMDD
	public char timeFrame = 'I';
	public int timeBase = 0;
	public String stockSymbol;
	public boolean autoRun = false;

	public int maxRecords = 0; // 0 = unlimited
	public int lastRecord = 1; // starts with 2
	public Vector<StockPoint> points = new Vector<StockPoint>();

	public Stock(String stockName, String stockSymbol) {
		this.stockName = stockName;
		this.stockSymbol = stockSymbol;
	}
	
	public byte[] getMasterBytes() {
		byte[] out = new byte[] { (byte)fileNum,
				                  0x65, 0x00,
				                  (byte)(fileType?28:32),
				                  (byte)(fileType?7:8),
				                  0x00, 0x00 };
		out = MetaStock.append(out, MetaStock.stringToByte(stockName, (byte)0x20, 16));
		out = MetaStock.append(out, new byte[]{ 0x00, (byte)(fileType?0x00:0x48) /*CT_v2_8_flag*/ });
		out = MetaStock.append(out, MetaStock.dateToMBFByte(firstDate));
		out = MetaStock.append(out, MetaStock.dateToMBFByte(lastDate));
		out = MetaStock.append(out, new byte[]{ (byte)timeFrame });
		out = MetaStock.append(out, MetaStock.intToShort(timeBase));
		out = MetaStock.append(out, MetaStock.stringToByte(stockSymbol, (byte)0x20, 14));
		out = MetaStock.append(out, new byte[]{ 0x20, (byte)(autoRun?'*':' '), 0x00 });
		return out;
	}
	
	public byte[] getEMasterBytes() {
		byte[] out = new byte[] { 0x36, 0x36,
				                  (byte)fileNum,
				                  0x00, 0x00, 0x00,
				                  (byte)(fileType?7:8),
				                  0x00, 0x00,
				                  (byte)(autoRun?'*':' '),
				                  0x00 };
		out = MetaStock.append(out, MetaStock.stringToByte(stockSymbol, (byte)0x00, 14));
		out = MetaStock.append(out, new byte[7]);
		out = MetaStock.append(out, MetaStock.stringToByte(stockName, (byte)0x00, 16));
		out = MetaStock.append(out, new byte[12]);
		out = MetaStock.append(out, new byte[]{ (byte)timeFrame });
		out = MetaStock.append(out, new byte[3]);
		out = MetaStock.append(out, MetaStock.dateToMBFByte(firstDate));
		out = MetaStock.append(out, new byte[4]);
		out = MetaStock.append(out, MetaStock.dateToMBFByte(lastDate));
		out = MetaStock.append(out, new byte[116]);
		return out;
	}
	
	public byte[] getDataBytes() {
		byte[] out = MetaStock.intToShort(0);
		out = MetaStock.append(out, MetaStock.intToShort(points.size()+1));
		out = MetaStock.append(out, new byte[fileType?24:28]);
		for(StockPoint p : points)
			out = MetaStock.append(out, p.getBytes());
		return out;
	}
	
	public void normalizeIntraday() {
		Vector<StockPoint> npoints = new Vector<StockPoint>();
		IntradayPoint last = null;
		for(StockPoint p : points) {
			if(!(p instanceof IntradayPoint))
				return;
			IntradayPoint ip = (IntradayPoint)p;
			if(last!=null) {
				if(last.date.equals(ip.date) && last.time.equals(ip.time)) {
					last.close = ip.close;
					last.low = Math.min(Math.min(last.low, ip.low), Math.min(ip.open, ip.close));
					last.high = Math.max(Math.max(last.high, ip.high), Math.max(ip.open, ip.close));
					last.volume += ip.volume;
					continue;
				}
				ip.open = last.close;
				ip.low = Math.min(ip.low, ip.open);
				ip.high = Math.max(ip.high, ip.open);
			}
			last = ip;
			npoints.add(ip);
		}
		points = npoints;
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer();
		for(StockPoint p : points) {
			out.append(p);
			out.append("\n");
		}
		return out.toString();
	}
	
}