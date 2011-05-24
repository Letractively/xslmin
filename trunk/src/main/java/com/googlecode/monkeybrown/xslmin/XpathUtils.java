package com.googlecode.monkeybrown.xslmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utilities to help with executing XPATH queries for xslmin
 * @author Rick Brown
 */
public class XpathUtils
{
	private Document doc;
	private XPath xpath;
	
	public XpathUtils(String uri)
	{
		doc = loadXmlDoc(uri);
	}
	
	public Document loadXmlDoc(InputStream stream)
	{
		Document result = null;
		try
		{
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setExpandEntityReferences(false);
			domFactory.setIgnoringComments(true);//strips comments
//			domFactory.setIgnoringElementContentWhitespace(true);//would be nice if it worked
		    domFactory.setNamespaceAware(true);
		    DocumentBuilder builder = domFactory.newDocumentBuilder();
		    result = builder.parse(stream);
		    XPathFactory factory = XPathFactory.newInstance();
		    xpath = factory.newXPath();
		    xpath.setNamespaceContext(new XslNamespaceContext());
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	public Document loadXmlDoc(String uri)
	{
		Document result = null;
		try
		{
			File file = new File(uri);
			if(file.exists())
			{
					result = loadXmlDoc(new FileInputStream(file));
				
			}
			else
			{
				throw new IOException("File does not exist: " + file.getAbsolutePath());
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public NodeList executeQuery(String query) throws XPathExpressionException
	{
		return executeQuery(doc, query);
	}
	
	public NodeList executeQuery(Node context, String query) throws XPathExpressionException
	{
		return (NodeList) executeQuery(context, query, XPathConstants.NODESET);
	}
	
	public Object executeQuery(Node context, String query, QName returnType) throws XPathExpressionException
	{
		XPathExpression expr = xpath.compile(query);
		return expr.evaluate(context, returnType);
	}
	
	public void createMinifiedFile(String path)
	{
		xmlToFile(doc, path);
	}
	
	private Transformer getTransformer()
	{
		Transformer transformer;
		try
		{
			Document xslt = loadXmlDoc(XpathUtils.class.getResourceAsStream("/xslmin.xsl"));
			TransformerFactory tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer(new DOMSource(xslt));
		}
		catch (TransformerConfigurationException e)
		{
			transformer = null;
			e.printStackTrace();
		}
		return transformer;
	}
	
	private void xmlToFile(Node node, String path)
	{
		try
		{
			Transformer transformer = getTransformer();
		    DOMSource source = new DOMSource(node);
		    StreamResult result = new StreamResult(new File(path));
		    transformer.transform(source, result);
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		} 
	}
}
