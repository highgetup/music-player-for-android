package com.xrlfy.musicplayer.util;

import java.math.BigDecimal;

public class FileUtil {

	public static String getSizeString(long size){
		int roundingMode=BigDecimal.ROUND_HALF_UP;
		String str="";
		if(size<1024){
			str=size+" B";
		}else if(size<1048576){
			str=new BigDecimal(((double)size)/1024d).setScale(2, roundingMode).doubleValue()+" KB";
		}else if(size<1073741824){
			str=new BigDecimal(((double)size)/1048576d).setScale(2, roundingMode).doubleValue()+" MB";
		}else if(size<1099511627776l){
			str=new BigDecimal(((double)size)/1073741824d).setScale(3, roundingMode).doubleValue()+" GB";
		}else{
			str=new BigDecimal(((double)size)/1099511627776d).setScale(4, roundingMode).doubleValue()+" TB";
		}
		return str;
	}
}
