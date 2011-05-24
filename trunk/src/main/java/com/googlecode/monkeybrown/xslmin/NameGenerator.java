package com.googlecode.monkeybrown.xslmin;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NameGenerator
{
	private int idx = 0;
	
	/**
	 * 
	 * @param clashTest A printf formatted String, the name will be injected to check if the new name 
	 * 			clashes with any existing elements.
	 * 			e.g. //node()[@name='%s']
	 * @return A new name, unique according the the "clashes" xpath
	 * @throws XPathExpressionException
	 */
	public String getNextName(Node refNode, String clashTest) throws XPathExpressionException
	{
		String result;
		NodeList clashNodes;
		do
    	{
			result = toBase26(idx++);
			if(refNode != null)
			{
				clashNodes = XslMin.xpathUtils.executeQuery(refNode, String.format(clashTest, result));
			}
			else
			{
				clashNodes = XslMin.xpathUtils.executeQuery(String.format(clashTest, result));
			}
    	}
    	while(clashNodes.getLength() > 0);
		return result;
	}
	
	public void reset()
	{
		idx = 0;
	}
	
	private static String toBase26(int number)
	{
        number = Math.abs(number);
        String converted = "";
        // Repeatedly divide the number by 26 and convert the
        // remainder into the appropriate letter.
        do
        {
            int remainder = number % 26;
            converted = (char)(remainder + 'A') + converted;
            number = (number - remainder) / 26;
        } 
        while (number > 0);
 
        return converted;
    }
}
