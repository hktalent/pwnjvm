package ysoserial.payloads.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//import javax.xml.bind.DatatypeConverter;

/*
 * 编码：javax.xml.bind.DatatypeConverter.printBase64Binary("xxxx".getBytes())
 * 解码：new String(javax.xml.bind.DatatypeConverter.parseBase64Binary("eHh4eA=="))
 * */
public class MyTest {
	public static String getBase64(String szName) {
		String s = "", szNm = szName.replace('.', '/') + ".class";
		try {
			InputStream stream = MyTest.class.getClassLoader().getResourceAsStream(szNm);
			ByteArrayOutputStream bi = new ByteArrayOutputStream();
			byte[] xx = new byte[1024];
			int i = 0;
			while (-1 < (i = stream.read(xx, 0, 1024))) {
				bi.write(xx, 0, i);
			}
//			sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
			s = javax.xml.bind.DatatypeConverter.printBase64Binary(bi.toByteArray());
		} catch (Exception e) {
		}
		return s.replaceAll("\n", "").replaceAll("\r", "");
	}
//	    public static void doTest(){
//	    	

	public static void getHexStr(String szName) throws Exception {
		String szNm = szName.replace('.', '/') + ".class";
		InputStream in = MyTest.class.getClassLoader().getResourceAsStream(szNm);
		byte[] data = toByteArray(in);
		in.close();
		System.out.println(bytesToHexString(data, data.length));
	}

	public static byte[] toByteArray(InputStream in) throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 4];
		int n = 0;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}

	public static String bytesToHexString(byte[] bArray, int length) {
		StringBuffer sb = new StringBuffer(length);
		String sTemp;
		for (int i = 0; i < length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	public static void main(String[] args) {
//		System.out.println(javax.xml.bind.DatatypeConverter.printBase64Binary("xxxx".getBytes()));
		
//		System.out.println(new String(javax.xml.bind.DatatypeConverter.parseBase64Binary("eHh4eA==")));
		System.out.println(getBase64(Svlt.class.getName()));
		
	}

}
