package com.shhn.pmm.util;

import cn.emay.slf4j.Logger;
import cn.emay.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class TokenCode {
    private static Logger logger = LoggerFactory.getLogger(TokenCode.class);

    private static final String key0 = "72ace7b11f694a6fa1429ae1112d431f";
    private final static Charset charset = Charset.forName("UTF-8");


    public static String Encode(String enc) {
        Charset charset = Charset.forName("UTF-8");
        byte[] keyBytes = key0.getBytes(charset);
        byte[] b = enc.getBytes(charset);
        for (int i = 0, size = b.length; i < size; i++) {
            for (byte keyBytes0 : keyBytes) {
                b[i] = (byte) (b[i] ^ keyBytes0);
            }
        }
        return new String(b);
    }

    public static String Decode(String dec) {
        byte[] keyBytes = key0.getBytes(charset);
        byte[] e = dec.getBytes(charset);
        byte[] dee = e;
        for (int i = 0, size = e.length; i < size; i++) {
            for (byte keyBytes0 : keyBytes) {
                e[i] = (byte) (dee[i] ^ keyBytes0);
            }
        }
        return new String(e);
    }

//	public static void main(String[] args) {
//		String s = "http://172.16.10.215:8080/interface/getRequisitionMoneyRecord";
//		String enc = Encode(s);
//		String dec = Decode(enc);
//		System.out.println(enc);
//		System.out.println(dec);
//	}
}