// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Base64codec.java
package smtpMessage;


public class Base64codec
{

    public static String encode(String s)
    {
        return encode(s.getBytes());
    }

    public Base64codec()
    {
    }

    /** Encode a byte[] as a Base64 (see RFC1521, Section 5.2) String. */
    private static final char[] base64 = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
        'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

	private static final char pad = '=';
    
//	public static String toBase64(byte[] b)
    public static String encode(byte b[])
    {
        StringBuffer sb = new StringBuffer();
        for (int ptr = 0; ptr < b.length; ptr += 3)
        {
            sb.append(base64[(b[ptr] >> 2) & 0x3F]);
            if (ptr + 1 < b.length)
            {
                sb.append(base64[((b[ptr] << 4) & 0x30) | ((b[ptr + 1] >> 4) & 0x0F)]);
                if (ptr + 2 < b.length)
                {
                    sb.append(base64[((b[ptr + 1] << 2) & 0x3C) | ((b[ptr + 2] >> 6) & 0x03)]);
                    sb.append(base64[b[ptr + 2] & 0x3F]);
                }
                else
                {
                    sb.append(base64[(b[ptr + 1] << 2) & 0x3C]);
                    sb.append(pad);
                }
            }
            else
            {
                sb.append(base64[((b[ptr] << 4) & 0x30)]);
                sb.append(pad);
                sb.append(pad);
            }
        }
        return sb.toString();
    }

}
