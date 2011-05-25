<?xml version="1.0"?>
<!-- 
	This is used in the xslmin junit tests.
	Basically don't mess around with existing parts of it.
	If you need a particular case tested that is not part of the existing XSLT 
	then add new templates / global vars etc after existing ones.
	
	Note that there are some non-standard attributes added to the XSL. These are
	to help getting handles on specific elements for the tests.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:variable name="globalShadow" handle="def1">
		<xsl:value-of select="barfoo"/><!-- Must remain first global var -->
	</xsl:variable>
	<xsl:param name="globalParamShadow"><!-- Must remain first global param -->
		<xsl:value-of select="foobart"/>
	</xsl:param>
	
	<xsl:template name="sed"><!-- Must remain the FIRST template and must NOT have a match attribute -->
		<xsl:param name="localA" select="'localA'"/><!-- This must remain the first local param in the whole stylesheet-->
		<xsl:variable name="localB"><!-- This must remain the first local variable in the whole stylesheet-->
			<xsl:text>localB</xsl:text>
		</xsl:variable>
		<xsl:variable name="localAandB" select="concat($localA,$localB)"/>
	</xsl:template>

	<xsl:template match="/">
	  <xsl:element name="div">
	  	<xsl:variable name="local1">
	  		<xsl:text>Mr Local Variable</xsl:text>
	  	</xsl:variable>
		<xsl:variable name="globalShadow">
			<xsl:value-of select="foobar"/>
		</xsl:variable>
		<xsl:value-of select="concat($local1, $globalShadow)" handle="noref1"/>
	  </xsl:element>
	</xsl:template>
	
	<xsl:template name="fred" match="span">
		<xsl:param name="local1"/>
		<xsl:element name="bar">
			<xsl:attribute name="kung">fu</xsl:attribute>
			<xsl:attribute name="abc">
				<xsl:text>def</xsl:text>
			</xsl:attribute>
		</xsl:element>
		<div>
			<xsl:value-of select="concat($local1, $globalShadow)" handle="ref1"/>
		</div>
	</xsl:template>
	
	<xsl:template name="ned" match="elephant" handle="tmpl1">
		<xsl:variable name="local2" handle="partialMatchA">
			<xsl:text>Mr Local Variable the Second</xsl:text>
		</xsl:variable>
		<xsl:variable name="local3">
			<xsl:text>Mr Local Variable the Third</xsl:text>
		</xsl:variable>
		<xsl:variable name="local2and3" select="concat($local2,$local3)"/>
		<xsl:call-template name="sed">
			<xsl:with-param name="localA">
				<xsl:value-of select="$local2and3" handle="partialMatchA"/><!-- The value in the select must not be changed, it must start with the name of a local variable above -->
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="foot">
		<xsl:variable name="tex">
			<xsl:text>ABCD</xsl:text>
		</xsl:variable>
	  	<xsl:variable name="mex">
			<xsl:text>EFGH</xsl:text>
		</xsl:variable>
		<xsl:variable name="tex-mex">
			<xsl:text>IJKL</xsl:text>
		</xsl:variable>
		<xsl:variable name="tex_mex">
			<xsl:text>IJKL</xsl:text>
		</xsl:variable>
		<xsl:variable name="mex_tex">
			<xsl:text>MNOP</xsl:text>
		</xsl:variable>
		<xsl:value-of select="concat($tex,$mex,$tex-mex,$tex_mex,$mex_tex)" handle="varMatchA"/>
		<xsl:value-of select="concat('$tex','$mex','$tex-mex','$tex','-mex','tex_mex','mex_tex','tex')" handle="varMatchB"/>
	</xsl:template>
	
	<xsl:template match="div">
	  <xsl:element name="{$globalShadow}">
	  	<xsl:attribute name="fu">
	  		<xsl:text>kung</xsl:text>
	  	</xsl:attribute>
	  </xsl:element>
	</xsl:template>
	
	<xsl:template match="nose" handle="collapse1">
		<!-- 
			The attributes on this element are hardcoded as expected results in JUnit tests
			so be careful when changing anything.
		 -->
		<xsl:param name="aParam"/>
		<xsl:element name="mouth"><!-- This must remain the first xsl:element in this template -->
			<xsl:attribute name="alpha">
				<xsl:text>text</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="numeric">
				<xsl:text>0101</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="var">
				<xsl:value-of select="$aParam"/>
			</xsl:attribute>
			<xsl:attribute name="func">
				<xsl:value-of select="concat($aParam, '-suffix')"/>
			</xsl:attribute>
			<xsl:attribute name="xpath">
				<xsl:value-of select="@shape"/>
			</xsl:attribute>
			<xsl:attribute name="textNode">text</xsl:attribute>
			<xsl:attribute name="mix">
				txtNode-
				<xsl:text>text-</xsl:text>
				<xsl:value-of select="$aParam"/>
			</xsl:attribute>
			<xsl:attribute name="noCollapse1">
				<xsl:if test="@shape='pointy'">
					<xsl:text>haha</xsl:text>
				</xsl:if>
				<xsl:if test="@shape='flat'">
					<xsl:text>hoho</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="@shape='round'">
				<xsl:attribute name="button">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
