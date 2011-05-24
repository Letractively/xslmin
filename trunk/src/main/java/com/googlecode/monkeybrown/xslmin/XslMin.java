/**
 * 
 */
package com.googlecode.monkeybrown.xslmin;

import javax.xml.xpath.XPathExpressionException;

/**
 * TODO normalise doc
 * @author Rick Brown
 */
public class XslMin
{
	public static XpathUtils xpathUtils;
	/**
	 * @param args first arg: input xsl path, second arg: output xsl path
	 */
	public static void main(String[] args)
	{
		try
		{
			if(args.length == 2)
			{
				String inputXslPath = args[0];
				String outputXslPath = args[1];
				System.out.println(String.format("Begining minification, input %s output: %s", inputXslPath, outputXslPath));
				XslMin.minify(inputXslPath, outputXslPath);
				System.out.println("Finished minification");
			}
			else
			{
				System.out.println("Usage: java -jar xslmin.jar inputXslPath outputXslPath");
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @param inputXslPath
	 * @param outputXslPath
	 * @throws XPathExpressionException
	 */
	private static void minify(String inputXslPath, String outputXslPath) throws XPathExpressionException
	{
		xpathUtils = new XpathUtils(inputXslPath);
		(new XslNamedTemplateRenamer()).rename();
		(new XslLocalVariableRenamer()).rename();
		(new XslLocalParamRenamer()).rename();
		(new XslGlobalVariableRenamer()).rename();
		XslElementCollapser.rewriteElements();
		xpathUtils.createMinifiedFile(outputXslPath);
	}
}
