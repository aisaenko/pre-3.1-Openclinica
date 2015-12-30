<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
	
<html>
<head>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
<script language="JavaScript" src="includes/global_functions_javascript.js"></script>
<style type="text/css">

.popup_BG { background-image: url(images/main_BG.gif);
	background-repeat: repeat-x;
	background-position: top;
	background-color: #FFFFFF;
	}


</style>

</head>
<body class="popup_BG">

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="eventlist" class="java.util.HashMap"/>

<h1><span class="title_extract"><fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="view_selected_items" bundle="${resword}"/> <a href="javascript:openDocWindow('help/3_2_createDataset_Help.html#step1')"><img src="images/bt_Help_Extract.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>

<P><jsp:include page="../include/alertbox.jsp"/></P>

<p><fmt:message key="can_view_items_selected_inclusion" bundle="${restext}"/></p>

<c:if test="${!empty allSelectedItems}">
<p><b><fmt:message key="show_items_this_dataset" bundle="${restext}"/></b></p>

<span class="table_title_extract"><fmt:message key="subject_and_event_attributes" bundle="${resword}"/></span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td class="table_header_column_top"><fmt:message key="event_location" bundle="${resword}"/></td>
 <td class="table_header_column_top"><fmt:message key="start_date" bundle="${resword}"/></td>
 <td class="table_header_column_top"><fmt:message key="end_date" bundle="${resword}"/></td>
 <td class="table_header_column_top"> 
   <c:choose>
     <c:when test="${study.studyParameterConfig.collectDob == '1'}">
     <fmt:message key="date_of_birth" bundle="${resword}"/>
    </c:when>
    <c:when test="${study.studyParameterConfig.collectDob == '3'}">
     <fmt:message key="date_of_birth" bundle="${resword}"/>
    </c:when>
    <c:otherwise>
     <fmt:message key="year_of_birth" bundle="${resword}"/>
    </c:otherwise> 
   </c:choose>
 </td>
 <td class="table_header_column_top"><fmt:message key="gender" bundle="${resword}"/></td>
 </tr>
 <tr>
 <td class="table_cell"><c:choose>
     <c:when test="${newDataset.showEventLocation}">
       <fmt:message key="yes" bundle="${resword}"/>
     </c:when>
     <c:otherwise>
       <fmt:message key="no" bundle="${resword}"/>
     </c:otherwise>
    </c:choose>    
   </td>   
   <td class="table_cell">
   <c:choose>
     <c:when test="${newDataset.showEventStart}">
       <fmt:message key="yes" bundle="${resword}"/>
     </c:when>
     <c:otherwise>
       <fmt:message key="no" bundle="${resword}"/>
     </c:otherwise>
    </c:choose>
   </td>  
   <td class="table_cell"> 
     <c:choose>
     <c:when test="${newDataset.showEventEnd}">
       <fmt:message key="yes" bundle="${resword}"/>
     </c:when>
     <c:otherwise>
       <fmt:message key="no" bundle="${resword}"/>
     </c:otherwise>
    </c:choose>   
   </td>
 <td class="table_cell">
 <c:choose>
     <c:when test="${newDataset.showSubjectDob}">
       <fmt:message key="yes" bundle="${resword}"/>
     </c:when>
     <c:otherwise>
       <fmt:message key="no" bundle="${resword}"/>
     </c:otherwise>
   </c:choose>  
 </td>
 <td class="table_cell">
  <c:choose>
     <c:when test="${newDataset.showSubjectGender}">
       <fmt:message key="yes" bundle="${resword}"/>
     </c:when>
     <c:otherwise>
       <fmt:message key="no" bundle="${resword}"/>
     </c:otherwise>
   </c:choose> 
  
 </td>
</tr>

</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
<br>

<span class="table_title_extract"><fmt:message key="CRF_data" bundle="${resword}"/></span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
    <td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/></td>          
    <td class="table_header_column_top"><fmt:message key="description" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="event" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="CRF" bundle="${resword}"/></td> 
    <td class="table_header_column_top"><fmt:message key="version2" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="data_type" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="units" bundle="${resword}"/></td>    
    <td class="table_header_column_top"><fmt:message key="response_label" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="PHI" bundle="${resword}"/></td>   
   
  </tr>	
<c:set var="count" value="0"/>
<c:forEach var='item' items='${allSelectedItems}'>  
  <tr>
   <td class="table_cell"><c:out value="${item.name}"/></td>
   <td class="table_cell"><c:out value="${item.description}"/>&nbsp;</td>
   <td class="table_cell"><c:out value="${item.defName}"/>&nbsp;</td>
   <td class="table_cell"><c:out value="${item.crfName}"/>&nbsp;</td>
    <td class="table_cell">
      <c:forEach var="meta" items="${item.itemMetas}" varStatus="status">
        <c:choose>
          <c:when test="${status.last}">
           <c:out value="${meta.crfVersionName}"/>
          </c:when>
          <c:otherwise>
           <c:out value="${meta.crfVersionName}"/>,<br>
          </c:otherwise> 
        </c:choose> 
      </c:forEach>
    </td>  
   <td class="table_cell"><c:out value="${item.dataType.name}"/>&nbsp;</td>
   <td class="table_cell"><c:out value="${item.units}"/>&nbsp;</td>
   <td class="table_cell">
     <c:choose>
      <c:when test="${item.phiStatus}">
        <fmt:message key="yes" bundle="${resword}"/>
      </c:when>
      <c:otherwise>
        <fmt:message key="no" bundle="${resword}"/>
      </c:otherwise>
    </c:choose>
   </td>
  
    <td class="table_cell">
      <c:forEach var="meta" items="${item.itemMetas}" varStatus="status">
        <c:choose>
          <c:when test="${status.last}">
           <c:out value="${meta.responseSet.label}"/>
          </c:when>
          <c:otherwise>
            <c:out value="${meta.responseSet.label}"/>,<br>
          </c:otherwise> 
        </c:choose> 
      </c:forEach>
    </td>  
   
   
  </tr>
  <c:set var="count" value="${count+1}"/>
</c:forEach>
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
</c:if>
</body>
</html>
