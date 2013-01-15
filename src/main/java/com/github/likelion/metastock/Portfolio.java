package com.github.likelion.metastock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class Portfolio {

	public HashMap<String, Stock> stocks = new HashMap<String, Stock>();

	public void addStock(Stock stock) {
		stocks.put(stock.stockSymbol, stock);
	}
	
	public void removeStock(String stockSymbol) {
		stocks.remove(stockSymbol);
	}

	public final byte[] getMasterBytes() {
		byte[] out = MetaStock.intToShort(stocks.size());
		out = MetaStock.append(out, MetaStock.intToShort(stocks.size()));
		out = MetaStock.append(out, new byte[49]);
		int i = 1;
		for(Stock s : stocks.values()) {
			s.fileNum = i++;
			out = MetaStock.append(out, s.getMasterBytes());
		}
		return out;
	}
	
	public final byte[] getEMasterBytes() {
		byte[] out = MetaStock.intToShort(stocks.size());
		out = MetaStock.append(out, MetaStock.intToShort(stocks.size()));
		out = MetaStock.append(out, new byte[188]);
		int i = 1;
		for(Stock s : stocks.values()) {
			s.fileNum = i++;
			out = MetaStock.append(out, s.getEMasterBytes());
		}
		return out;
	}

	public final void save(String path) throws IOException {
		FileOutputStream master = new FileOutputStream(new File(path, "MASTER"));
		master.write(getMasterBytes());
		master.close();
		FileOutputStream emaster = new FileOutputStream(new File(path, "EMASTER"));
		emaster.write(getEMasterBytes());
		emaster.close();
		int i = 1;
		for(Stock s : stocks.values()) {
			FileOutputStream data = new FileOutputStream(new File(path, "F"+(i++)+".DAT"));
			data.write(s.getDataBytes());
			data.close();
		}
	}
	
}