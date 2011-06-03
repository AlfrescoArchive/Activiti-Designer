<xsl:stylesheet 
	version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:msxsl="urn:schemas-microsoft-com:xslt">
	
	<xsl:output method="html" encoding="UTF-8"/>
	
	<xsl:key name="cat" match="category" use="@name"/>
	
	<xsl:template match="/site">

		<html>
			<head>
				<title>
					<xsl:value-of select="description/@name" />
				</title>
				<style type="text/css">
					@import url("web/assets/styles/site.css");
				</style>
			</head>
			<body>
					
				<div id="header">
					<div class="contained">
						<div class="logo">
							<a title="Activiti Logo" href="index.html">
								<img style="border: medium none;" alt="Activiti Logo" src="web/assets/images/activiti_logo.png" />
							</a>
							<br/>
						</div>
					</div>
				</div>
					
				<div id="body">
					
					<div class="contained">
						<h1>
							<xsl:value-of select="description/@name" />
						</h1>
						
						<p>
							<xsl:value-of select="description"/>
						</p>
						
						<xsl:for-each select="category-def">
							<xsl:sort select="@label" order="ascending" case-order="upper-first" />
							<xsl:sort select="@name" order="ascending" case-order="upper-first" />
							
							<xsl:if test="count(key('cat',@name)) != 0">
								
								<xsl:variable name="featuresCount" select="count(preceding-sibling::feature/category[@name = current()/@name])" />
								
								<h2>
									<xsl:text>Category: </xsl:text>
									<xsl:value-of select="@label" />
									<xsl:text> [id: </xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>]</xsl:text>
								</h2>
								
								<p>
									<xsl:value-of select="description/text()" />
									<xsl:text> Contains </xsl:text>
									<xsl:value-of select="$featuresCount" />
									<xsl:text> feature</xsl:text>
									<xsl:if test="$featuresCount &gt; 1" />
									<xsl:text>.</xsl:text>
								</p>
								
								<table width="100%" border="0" cellspacing="1" cellpadding="2">
									
									<thead>
										<tr>
											<th>
												ID
											</th>
											<th>
												Version
											</th>
											<th>
												Download
											</th>
										</tr>
									</thead>
									
									<tbody>
										
										<xsl:for-each select="preceding-sibling::feature/category[@name = current()/@name]/parent::feature">
											<xsl:sort select="@version" order="ascending" />
											<xsl:sort select="@id" order="ascending"	case-order="upper-first" />
											
											<tr>
												<xsl:choose>
													<xsl:when test="(position() mod 2 = 1)">
														<xsl:attribute name="class">alternate1</xsl:attribute>
													</xsl:when>
													<xsl:otherwise>
														<xsl:attribute name="class">alternate2</xsl:attribute>
													</xsl:otherwise>
												</xsl:choose>
												<td>
													<xsl:value-of select="@id"/>
												</td>
												<td>
													<xsl:value-of select="@version"/>
												</td>
												<td>
													<a href="{@url}" title="Download the file">
														<img src="web/assets/images/drive_web.png" alt="download" />
														<xsl:text> Download</xsl:text>
													</a>
												</td>
												
											</tr>
											
										</xsl:for-each>
									</tbody>
								</table>
							</xsl:if>
						</xsl:for-each>
					</div>
				</div>
				
				<div id="footer">
					<div class="contained">
						<p>
							<xsl:value-of select="description/@name" />
						</p>
					</div>
				</div>
					
						
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
