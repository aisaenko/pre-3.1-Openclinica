<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="com.akazaresearch.tags" prefix="aka_frm" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="annotations" class="java.lang.String" />

<c:set var="currPage" value="" />
<c:set var="curCategory" value="" />
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
<div style="width:100%">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<%--${requestScope['new_table']}--%>
<c:choose>
<c:when test="${requestScope['new_table'] == true}">
  <aka_frm:tabletag />
</c:when>
<c:otherwise>

<!-- Table Contents -->
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<c:set var="displayItemNum" value="${0}" />
<c:set var="numOfTr" value="0"/>
<c:forEach var="displayItem" items="${section.items}" varStatus="itemStatus">
<c:if test="${currPage != displayItem.metadata.pageNumberLabel}">
  <tr class="aka_stripes">
    <td class="table_cell_left">
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr>
          <td width="5%" nowrap="nowrap">Section:</td>
          <td width="75%" style="padding-left: 12px;">
            <b>
              <c:if test="${section.section.parent.active}">
                <c:out value="${section.section.parent.title}" escapeXml="false"/> &gt;
              </c:if>
              <c:out value="${section.section.title}" escapeXml="false"/>
            </b>
          </td>
          <td width="20%" align="right" valign="top" nowrap="nowrap">
            <a name="item<c:out value="${displayItem.item.id}"/>">
              Form Page: <c:out value="${displayItem.metadata.pageNumberLabel}" />
            </a>

          </td>
        </tr>
        <c:if test='${section.section.subtitle != ""}'>
          <tr class="aka_stripes">
            <td>Subtitle:</td>
            <td> <c:out value="${section.section.subtitle}" escapeXml="false"/> </td>
          </tr>
        </c:if>
        <c:if test='${section.section.instructions != ""}'>
          <tr class="aka_stripes">
            <td width="5%" nowrap>Instructions:</td>
            <td width="75%" style="padding-left: 12px;"> <c:out value="${section.section.instructions}" escapeXml="false"/> </td>
          </tr>
        </c:if>
      </table>
    </td>
  </tr>
  <c:set var="currPage" value="${displayItem.metadata.pageNumberLabel}" />
</c:if>

<%-- SHOW THE PARENT FIRST --%>
<c:if test="${displayItem.metadata.parentId == 0}">
<!--ACCORDING TO COLUMN NUMBER, ARRANGE QUESTIONS IN THE SAME LINE-->

<c:if test="${displayItem.metadata.columnNumber <=1}">
  <c:if test="${numOfTr > 0 }">
    </tr>
    </table>
    </td>

    </tr>

  </c:if>
  <c:set var="numOfTr" value="${numOfTr+1}"/>
  <c:if test="${!empty displayItem.metadata.header}">
    <tr class="aka_stripes">
      <td class="table_cell_left aka_header_border" bgcolor="#F5F5F5"><b><c:out value=
        "${displayItem.metadata.header}" escapeXml="false" /></b></td>
    </tr>
  </c:if>
  <c:if test="${!empty displayItem.metadata.subHeader}">
    <tr class="aka_stripes">
      <td class="table_cell_left aka_header_border"><c:out value=
        "${displayItem.metadata.subHeader}" escapeXml="false" /></td>
    </tr>
  </c:if>
  <%--
    <c:if test="${!empty displayItem.item.description}">
     <tr>
     <td class="table_cell_left"><c:out value="${displayItem.item.description}" /></td>
      </tr>
   </c:if>
   --%>

  <tr>
  <td class="table_cell">
  <table border="0" width="100%">
  <tr>
  <td valign="top">
</c:if>

<c:if test="${displayItem.metadata.columnNumber >1}">
  <td valign="top">
</c:if>
<table border="0">
  <tr>
      <%--  <td style="width: 20px;" valign="top"><c:out value=
     "${displayItem.metadata.questionNumberLabel}" /></td>      <%--  <td valign="top" class="text_block"><c:out value=
                 "${displayItem.metadata.questionNumberLabel}" /> <c:out value="${displayItem.metadata.leftItemText}" escapeXml="false"/></td>
             <td style="width: 200px;" valign="top">--%>
    <td valign="top" class="aka_ques_block"><c:out value="${displayItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
    <td valign="top" class="aka_text_block"><c:out value="${displayItem.metadata.leftItemText}" escapeXml="false"/></td>
    <td valign="top" nowrap="nowrap">
        <%-- display the HTML input tag --%>
      <c:set var="displayItem" scope="request" value="${displayItem}" />
      <c:import url="../submit/showItemInput.jsp" />
        <%--    <c:import url="../showMessage.jsp"><c:param name="key" value="input${displayItem.item.id}" /></c:import> --%>
    </td>
    <c:if test='${displayItem.item.units != ""}'>
      <td valign="top">
        <c:out value="(${displayItem.item.units})" escapeXml="false"/>
      </td>
    </c:if>
    <td valign="top"><c:out value="${displayItem.metadata.rightItemText}" escapeXml="false"/></td>
  </tr>
    <%--try this, displaying error messages in their own row--%>
  <tr>
    <td valign="top" colspan="4" style="text-align:right">
      <c:import url="../showMessage.jsp"><c:param name="key" value=
        "input${displayItem.item.id}" /></c:import> </td>
  </tr>
</table>
</td>
<c:if test="${itemStatus.last}">
  </tr>
  </table>
  </td>

  </tr>
</c:if>

<c:if test="${displayItem.numChildren > 0}">
  <tr>
  <%-- indentation --%>
  <!--<td class="table_cell">&nbsp;</td>-->
  <%-- NOW SHOW THE CHILDREN --%>

  <td class="table_cell">
  <table border="0">
  <c:set var="notFirstRow" value="${0}" />
  <c:forEach var="childItem" items="${displayItem.children}">


    <c:set var="currColumn" value="${childItem.metadata.columnNumber}" />
    <c:if test="${currColumn == 1}">
      <c:if test="${notFirstRow != 0}">
        </tr>
      </c:if>
      <tr>
      <c:set var="notFirstRow" value="${1}" />
      <%-- indentation --%>
      <td valign="top">&nbsp;</td>
    </c:if>
    <%--
              this for loop "fills in" columns left blank
              e.g., if the first childItem has column number 2, and the next one has column number 5,
              then we need to insert one blank column before the first childItem, and two blank columns between the second and third children
          --%>
    <c:forEach begin="${currColumn}" end="${childItem.metadata.columnNumber}">
      <td valign="top">&nbsp;</td>
    </c:forEach>

    <td valign="top">
      <table border="0">
        <tr>
            <%--<td style="width: 20px;" valign="top"></td>--%>
          <td valign="top" class="aka_ques_block"><c:out value="${childItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
          <td valign="top" class="aka_text_block"><c:out value="${childItem.metadata.leftItemText}" escapeXml="false"/></td>
                <%--<td style="width: 200px;" valign="top">--%>
          <td valign="top" nowrap="nowrap">
              <%-- display the HTML input tag --%>
            <c:set var="displayItem" scope="request" value="${childItem}" />
            <c:import url="../submit/showItemInput.jsp" />
          </td>
          <c:if test='${childItem.item.units != ""}'>
            <td valign="top"> <c:out value="(${childItem.item.units})" escapeXml="false"/> </td>
          </c:if>
          <td valign="top"> <c:out value="${childItem.metadata.rightItemText}" escapeXml="false"/> </td>
        </tr>
        <tr>
          <td valign="top" colspan="4" style="text-align:right">
            <c:import url="../showMessage.jsp"><c:param name="key" value=
              "input${displayItem.item.id}" /></c:import> </td>
        </tr>
      </table>
    </td>
  </c:forEach>
  </tr>
  </table>
  </td>
  </tr>
</c:if>
</c:if>
<c:set var="displayItemNum" value="${displayItemNum + 1}" />
</c:forEach>
</table>

<!-- End Table Contents -->
</c:otherwise>
</c:choose>

</div>
</div></div></div></div></div></div></div></div>
</div>

</td>
</tr>
</table>