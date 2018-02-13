<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dp="http://www.datapower.com/extensions"
  xmlns:json="http://www.ibm.com/xmlns/prod/2009/jsonx"
  xmlns:dpconfig="http://www.datapower.com/param/config" extension-element-prefixes="dp" exclude-result-prefixes="dp dpconfig">
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="/json:object">
        <xsl:variable name="result">

          <dp:sql-execute source="'SQLDataSource'" statement="'{call {{signature}} }'">
            <arguments>
            {{#In}}
              <argument type="{{typeName}}" mode="INPUT">
                <xsl:value-of select="/json:object/json:string[@name='{{columnName}}']"/>
              </argument>
              {{/In}}
            </arguments>
          </dp:sql-execute>

        </xsl:variable>
        <xsl:choose>
          <xsl:when test="$result/sql[@result = 'error']">
            <dp:reject>
              <xsl:value-of select="$result/sql/message" />
            </dp:reject>
          </xsl:when>
          <xsl:when test="$result/sql/resultSet=''">
            <!--Formato cuando no existen Registros-->
            <dp:reject>Sin Registros</dp:reject>
          </xsl:when>
          <xsl:otherwise>
            <!-- format result -->
            <xsl:for-each select="$result/sql/resultSet/row">
              <json:object xsi:schemaLocation="http://www.datapower.com/schemas/json jsonx.xsd"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:json="http://www.ibm.com/xmlns/prod/2009/jsonx">
                <xsl:for-each select="column">
                  <json:string>
                    <xsl:attribute name="name">
                      <xsl:value-of select="name" />
                    </xsl:attribute>
                    <xsl:value-of select= "normalize-space(value)" />
                  </json:string>
                </xsl:for-each>
              </json:object>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
