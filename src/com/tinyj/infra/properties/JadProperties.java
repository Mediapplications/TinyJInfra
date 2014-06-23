package com.tinyj.infra.properties;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.jar.Attributes;

public class JadProperties extends java.util.Properties
{
	
	/**
	 * temp serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	/** A table of hex digits 
	 *
	 * This variable is taken 'AS IS' from the java.util.Properties class, as it is
	 * needed by the <i>toHex</i> but it is originally private.
	 */
	
    private static final char[] hexDigit = {
	'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };
    
    
    
    /**
     * This method returns the an <i>java.util.jar.Attributes</i> instance with all the properties of this JadProperties instance.
     * It is for backwards compatibility with the <i>Manifest</i> class
     * 
     * @return this JadProperties instance
     */
    public Attributes getMainAttributes()
    {
    	Attributes attributes = new Attributes();
    	attributes.putAll(this);
    	return attributes;
    }
    
    
    
    /**
     * This method calls the <i>store()</i> method.
     * It is for backwards compatibility with the <i>Manifest</i> class
     * 
     * @param aOutputStream an output stream to write to
     * 
     * @throws IOException if an I/O error has occurred
     */
    public void write(OutputStream aOutputStream)
    	throws IOException
    {
    	store(aOutputStream, null);
    }
    
    
	/**
	 * This method is taken 'AS IS' from the java.util.Properties class, as it is
	 * needed by the <i>store</i> but it is originally private.
	 *
	 * Convert a nibble to a hex character
     * @param	nibble	the nibble to convert.
     */
	private static char toHex(int nibble) 
	{
		return hexDigit[(nibble & 0xF)];
    }

    
	
	/**
	 	* This method does the same as the original Properties' <i>store()</i>
	 	* method, but just replaces the '=' sign as a separator with a ': ' (colon and space)
	 	* so it will consist with the JAD file structure.
	 	* It also ignores the <i>comments</i> argument (and removes any comment writing to
	 	* the JAD file) as JAD files cannot contain comments
	 	* 
	 	* @see java.util.Properties
	 	* 
		* @param   out      an output stream.
	    * @param   comments   a description of the property list.
	    * @exception  IOException if writing this property list to the specified
	    *             output stream throws an <tt>IOException</tt>.
	    * @exception  ClassCastException  if this <code>Properties</code> object
	    *             contains any keys or values that are not <code>Strings</code>.
	    * @exception  NullPointerException  if <code>out</code> is null.
	    */
	   	public synchronized void store(OutputStream out, String comments)
	   		throws IOException
	   	{
	       BufferedWriter awriter;
	       awriter = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));
	       //awriter = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
	       //awriter = new BufferedWriter(new OutputStreamWriter(out));

	       
	       //******* we supress any comments functionality *******
	       //if (comments != null)
	       //    writeln(awriter, "#" + comments);
	       //writeln(awriter, "#" + new Date().toString());
	       
	       for (Enumeration e = keys(); e.hasMoreElements();) 
	       {
	           String key = (String)e.nextElement();
	           String val = (String)get(key);
	           //we do not call to 'saveConvert'as is messes up the certificate
	           //key = saveConvert(key, true);
	
		    /* No need to escape embedded and trailing spaces for value, hence
		     * pass false to flag.
		     */
	           //we do not call to 'saveConvert'as is messes up the certificate
	           //val = saveConvert(val, false);
	           writeln(awriter, key + ": " + val);
	       }
	       awriter.flush();
	   	}
	   
	   		   
	   
	   /**
	    * PAY ATTENTION !!! - we do not call this method from the "store" method.
	    * 
	     * This method is taken 'AS IS' from the java.util.Properties class, as it is
	     * needed by the <i>store</i> but it is originally private.
	     * 
	     * Converts unicodes to encoded &#92;uxxxx and escapes
	     * special characters with a preceding slash
	     */
	    private String saveConvert(String theString, boolean escapeSpace) 
	    {
	        int len = theString.length();
	        int bufLen = len * 2;
	        if (bufLen < 0) {
	            bufLen = Integer.MAX_VALUE;
	        }
	        StringBuffer outBuffer = new StringBuffer(bufLen);

	        for(int x=0; x<len; x++) {
	            char aChar = theString.charAt(x);
	            // Handle common case first, selecting largest block that
	            // avoids the specials below
	            if ((aChar > 61) && (aChar < 127)) {
	                if (aChar == '\\') {
	                    outBuffer.append('\\'); outBuffer.append('\\');
	                    continue;
	                }
	                outBuffer.append(aChar);
	                continue;
	            }
	            switch(aChar) {
			case ' ':
			    if (x == 0 || escapeSpace) 
				outBuffer.append('\\');
			    outBuffer.append(' ');
			    break;
	                case '\t':outBuffer.append('\\'); outBuffer.append('t');
	                          break;
	                case '\n':outBuffer.append('\\'); outBuffer.append('n');
	                          break;
	                case '\r':outBuffer.append('\\'); outBuffer.append('r');
	                          break;
	                case '\f':outBuffer.append('\\'); outBuffer.append('f');
	                          break;
	                case '=': // Fall through
	                case ':': // Fall through
	                case '#': // Fall through
	                case '!':
	                    outBuffer.append('\\'); outBuffer.append(aChar);
	                    break;
	                default:
	                    if ((aChar < 0x0020) || (aChar > 0x007e)) {
	                        outBuffer.append('\\');
	                        outBuffer.append('u');
	                        outBuffer.append(toHex((aChar >> 12) & 0xF));
	                        outBuffer.append(toHex((aChar >>  8) & 0xF));
	                        outBuffer.append(toHex((aChar >>  4) & 0xF));
	                        outBuffer.append(toHex( aChar        & 0xF));
	                    } else {
	                        outBuffer.append(aChar);
	                    }
	            }
	        }
	        return outBuffer.toString();
	    }
	    
	    
	    /**
	     * This method is taken 'AS IS' from the java.util.Properties class, as it is
	     * needed by the <i>store</i> but it is originally private.
	     */
	    private static void writeln(BufferedWriter bw, String s) throws IOException 
	    {
	        bw.write(s);
	        bw.newLine();
	    }
}
