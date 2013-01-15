package com.github.likelion.metastock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;


public class History {

	public static void main(String[] args) throws MalformedURLException, IOException {
		
		String[] stocks = new String[]{"ABB", "ASSA-B", "ACTI", "AZN", "AXIS", "PXXS-SDB", "IS", "ENQ", "ENRO", "ERIC-B", "FING-B",
				                       "G5EN", "HUSQ-B", "IMPC", "KARO", "KOPY", "LUMI-SDB", "LUPE", "NOKI-SEK", "PREC", "RUSF",
				                       "SAAB-B", "SAS", "SCV-B", "STE-R", "TEL2-B", "TLSN", "VOLV-B", "VNIL-SDB", "BEO-SDB", "TAGR",
				                       "CLS-B", "PRIC-B", "CAG"};
		Portfolio p = new Portfolio();
		String rtq = "1\r\n"+stocks.length+"\r\nC:\\MetaStock\\Sweden\\OMX\r\nYahoo USA (real-time, subscription needed)\r\n"+
		"Tick\r\n09:00\r\n17:30\r\nNo user name required.\r\n\r\n0\r\n\r\n";
		
	    int c = 1;
		for(String stock : stocks) {
			System.out.print("Processing ("+(c++)+"/"+stocks.length+") - ["+stock+"]... ");
			String name = getName(stock);
			rtq += name+"\r\n"+stock+".ST\r\n"+stock+".ST\r\n";
			final Stock s = new Stock(getName(stock), stock);
			System.out.print(s.stockName+"... ");
			s.fileType = true;
			s.timeFrame = 'D';
			final String[] dates = new String[2];
			
			parseCSV("http://hopey.netfonds.no/paperhistory.php?paper="+stock+".ST&csv_format=csv",
					 new CSVParserInterpreter() {
						@Override
						public boolean interpretLine(String line, int lineNumber) {
							if(lineNumber == 1)
								return true;
							DailyPoint i = new DailyPoint();
							StringTokenizer st = new StringTokenizer(line, ",");
							i.date = st.nextToken().substring(2);
							//int y = Integer.valueOf(i.date.substring(0,2));
							//int m = Integer.valueOf(i.date.substring(2,4));
							//if(y<9 || y>50 || y==9&&m<7)
								//return true;
							if(dates[0] == null)
								dates[0] = i.date;
							dates[1] = i.date;
							st.nextToken();
							st.nextToken();
							i.open = Float.valueOf(st.nextToken());
							i.high = Float.valueOf(st.nextToken());
							i.low = Float.valueOf(st.nextToken());
							i.close = Float.valueOf(st.nextToken());
							i.volume = Float.valueOf(st.nextToken());
							s.points.add(0, i);
							return true;
						}
					 });
			s.firstDate = dates[1];
			s.lastDate = dates[0];
			p.addStock(s);
			System.out.println("("+s.firstDate+"-"+s.lastDate+") done.");
		}
		p.save("history");
		
		FileOutputStream rtqfile = new FileOutputStream(new File("history", "OMX.rtq"));
		rtqfile.write(rtq.getBytes());
		rtqfile.close();
	}
	
	public static final String getName(String symbol) throws IOException {
		InputStream is = (InputStream)new URL("http://hopey.netfonds.no/about.php?paper="+symbol+".ST").getContent();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			for(;;) {
				String line = br.readLine();
				if(line != null) {
					String c = "<a href=\"ppaper.php?paper="+symbol+".ST\">";
					int i = line.indexOf(c);
					if(i>=0)
						return line.substring(i+c.length(), line.indexOf("</a>"));
				} else
					return symbol;
			}
		} finally {
			is.close();
		}
	}

	interface CSVParserInterpreter {
		boolean interpretLine(String line, int lineNumber);
	}
	
	public static final void parseCSV(String url, CSVParserInterpreter interpreter) throws MalformedURLException, IOException {
		InputStream is = (InputStream)new URL(url).getContent();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			int i = 1;
			for(;;) {
				String line = br.readLine();
				if(line == null || !interpreter.interpretLine(line, i++)) {
					return;
				}
			}
		} finally {
			is.close();
		}
	}

}