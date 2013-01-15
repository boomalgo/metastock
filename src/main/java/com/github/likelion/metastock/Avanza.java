package com.github.likelion.metastock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;


public class Avanza {
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		String cookie = login("user", "password");

		Portfolio portfolio = new Portfolio();
		Stock stock = new Stock("Fingerpring Cards B", "FING-B");
		
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		String today = sdf.format(cal.getTime());
		
		stock.firstDate = today;
		stock.lastDate = today;
		HttpsURLConnection c = (HttpsURLConnection)new URL("https://www.avanza.se/aza/aktieroptioner/kurslistor/avslut.jsp?orderbookId=5468").openConnection();
		c.setInstanceFollowRedirects(true);
		c.setUseCaches(false);
		c.setRequestProperty("Cookie", cookie);
		InputStream is = (InputStream)c.getContent();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			for(;;) {
				String line = br.readLine();
				if(line == null)
					break;
				if(line.contains("<TD class='neutral'>")) {
					String delim = "<TD class='neutral'>";
					int p = line.indexOf(delim) + delim.length();
					String time = line.substring(p, p+5);
					p = line.indexOf(delim, p+5) + delim.length();
					int q = line.indexOf("</TD>", p);
					float course = Float.valueOf(line.substring(p, q).replaceAll(",", "."));
					p = line.indexOf(delim, q) + delim.length();
					q = line.indexOf("</TD>", p);
					float volume = Float.valueOf(line.substring(p, q).replaceAll(",", "."));
					System.out.println(time+" - "+course+" - "+volume+"\r");
					
					IntradayPoint point = new IntradayPoint();
					point.date = today;
					point.time = time;
					point.open = course;
					point.high = course;
					point.low = course;
					point.close = course;
					point.volume = volume;
					stock.points.add(0, point);
				}
			}
			stock.normalizeIntraday();
			System.out.println(stock);
			portfolio.addStock(stock);
			portfolio.save("intraday");
		} finally {
			is.close();
		}
	}

	public static String login(String username, String password) throws MalformedURLException, IOException {
		HttpsURLConnection c = (HttpsURLConnection)new URL("https://www.avanza.se/aza/login/login.jsp").openConnection();
		c.setInstanceFollowRedirects(false);
		c.setRequestMethod("POST");
		c.setDoInput(true);
		c.setDoOutput(true);
		c.setUseCaches(false);
		c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		String s = "redirect&username="+URLEncoder.encode(username, "UTF-8")+"&password="+URLEncoder.encode(password, "UTF-8")+"&service=M&msgId=-1";
		c.setRequestProperty("Content-Length", ""+s.getBytes().length);
		OutputStream printout = c.getOutputStream();
		printout.write(s.getBytes());
		printout.close();
		List<String> cookies = c.getHeaderFields().get("Set-Cookie");
		String cookie = "";
		for(String q : cookies) {
			if(cookie.length()!=0)
				cookie += ", ";
			StringTokenizer st = new StringTokenizer(q, "=;");
			cookie += st.nextToken();
			cookie += "=";
			cookie += st.nextToken();
		}
		return cookie;
	}
	
}