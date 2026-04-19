<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="userSkillLevel"/>

    <xsl:output method="html" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Recipes Displayed with XSL</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px;">
                <div style="width: 80%; margin: 0 auto;">
                    <h1>Recipes Displayed with XSL</h1>

                    <p>
                        First user cooking skill level:
                        <strong><xsl:value-of select="$userSkillLevel"/></strong>
                    </p>

                    <xsl:for-each select="recipes/recipe">
                        <div>
                            <xsl:attribute name="style">
                                <xsl:text>border: 1px solid #ccc; border-radius: 10px; padding: 15px; margin-bottom: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); background-color: </xsl:text>
                                <xsl:choose>
                                    <xsl:when test="
                                        translate(normalize-space(difficultyLevel), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')
                                        =
                                        translate(normalize-space($userSkillLevel), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')
                                    ">#fff59d</xsl:when>
                                    <xsl:otherwise>#c8e6c9</xsl:otherwise>
                                </xsl:choose>
                                <xsl:text>;</xsl:text>
                            </xsl:attribute>

                            <h2><xsl:value-of select="title"/></h2>

                            <p>
                                <strong>ID:</strong>
                                <xsl:value-of select="@id"/>
                            </p>

                            <p><strong>Cuisine Types:</strong></p>
                            <ul>
                                <xsl:for-each select="cuisineTypes/cuisineType">
                                    <li><xsl:value-of select="."/></li>
                                </xsl:for-each>
                            </ul>

                            <p>
                                <strong>Difficulty Level:</strong>
                                <xsl:value-of select="difficultyLevel"/>
                            </p>
                        </div>
                    </xsl:for-each>
                </div>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>