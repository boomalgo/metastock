package com.github.likelion.metastock;

import java.io.IOException;
import java.util.Arrays;

public class MetaStock {

	public static void main(String[] args) throws IOException {
		Stock s = new Stock("SPY*60", "SPY*60");
		s.firstDate = "080617";
		s.lastDate = "080617";
		
		IntradayPoint i = new IntradayPoint();
		i.date = "080617";
		i.time = "09:05";
		i.open = 10;
		i.high = 40;
		i.low = 5;
		i.close = 25;
		i.volume = 15;
		s.points.add(i);
		
		i = new IntradayPoint();
		i.date = "080617";
		i.time = "09:06";
		i.open = 25;
		i.high = 40;
		i.low = 20;
		i.close = 35;
		i.volume = 17;
		s.points.add(i);
		
		Portfolio p = new Portfolio();
		p.addStock(s);
		p.save(".");
	}
	
	public static final byte[] intToShort(int i) {
		return new byte[] { (byte)(i & 0xFF), (byte)((i & 0xFF00) >> 8) };
	}
	
	public static final byte[] append(byte[] original, byte[] appended) {
		if(appended.length == 0)
			return original;
		byte[] out = new byte[original.length+appended.length];
		if(original.length > 0)
			System.arraycopy(original, 0, out, 0, original.length);
		System.arraycopy(appended, 0, out, original.length, appended.length);
 		return out;
	}

	public static final float mbfByteToIeeeFloat(byte[] bytes) {
        final int BYTE_MASK = 0x0ff;
        final int MANTISSA_MASK = 0x007fffff;
        final int EXPONENT_MASK = 0x0ff; 
        final int SIGN_MASK = 0x080; 
        int intOne = (int) (bytes[0] & BYTE_MASK);
        int intTwo = (int) (bytes[1] & BYTE_MASK);
        int intThree = (int) (bytes[2] & BYTE_MASK);
        int intFour = (int) (bytes[3] & BYTE_MASK);
        int msf = intFour << 24 | intThree << 16 | intTwo << 8 | intOne;
        int mantissa = (msf & MANTISSA_MASK);
        int exponent = ((msf >> 24) & EXPONENT_MASK) - 2;
        int sign = (msf >> 16) & SIGN_MASK;
        mantissa |= exponent << 23 | sign << 24;
        float result = Float.intBitsToFloat(mantissa);
        return result < 0 ? 0 : result;    
    }

    public static final byte[] ieeeFloatToMBFByte(float value) {
        final int IEEE_SIGNAL_MASK = 0x80000000;
        final int IEEE_EXPONENT_MASK = 0x7f800000;
        final int IEEE_MANTISSA_MASK = 0x007fffff;
        int intIeee = Float.floatToIntBits(value);
        int ieeeSignal = intIeee & IEEE_SIGNAL_MASK;
        int ieeeExponent = intIeee & IEEE_EXPONENT_MASK;
        int ieeeMantissa = intIeee & IEEE_MANTISSA_MASK;
        int intMbf = Float.floatToIntBits(0f);
        intMbf = (ieeeExponent << 1) | (ieeeSignal >>> 8) | ieeeMantissa;
        return new byte[] {
                (byte)((intMbf >> 0) & 0xff),
                (byte)((intMbf >> 8) & 0xff),
                (byte)((intMbf >> 16) & 0xff),
                (byte)(((intMbf >> 24) & 0xff) + 2)          
        };    
    }

    public static final byte[] dateToMBFByte(String date) { // YYMMDD
    	return ieeeFloatToMBFByte(1000000 + Integer.valueOf(date.substring(0, 2))*10000 +
    			                            Integer.valueOf(date.substring(2, 4))*100 +
    			                            Integer.valueOf(date.substring(4)));
    }

    public static final byte[] dateToIeeeByte(String date) { // YYMMDD
    	return ieeeFloatToMBFByte(1000000 + Integer.valueOf(date.substring(0, 2))*10000 +
    			                            Integer.valueOf(date.substring(2, 4))*100 +
    			                            Integer.valueOf(date.substring(4)));
    }

    public static final byte[] timeToMBFByte(String time) { // HH:MM
    	return ieeeFloatToMBFByte(Integer.valueOf(time.substring(0, 2))*10000 +
    			                  Integer.valueOf(time.substring(3, 5))*100);
    }
    
    public static final byte[] stringToByte(String string, byte pad, int length) {
    	if(string.length()<length) {
	    	byte[] padding = new byte[length - string.length()];
	    	Arrays.fill(padding, pad);
	    	return append(string.getBytes(), padding);
    	} else
    		return string.substring(0, string.length()>16?16:string.length()).getBytes();
    }

}