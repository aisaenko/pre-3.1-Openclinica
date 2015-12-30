<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<html>
<head>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
<script language="JavaScript" src="includes/global_functions_javascript.js" type="text/javascript"></script>
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

<h1><span class="title_extract">Create Dataset: View Selected Items <a href="javascript:openDocWindow('help/3_2_createDataset_Help.html#step1')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<P><jsp:include page="../include/alertbox.jsp"/></P>

<p>Below you can view all items selected for inclusion in the dataset. </p>

<c:if test="${!empty allSelectedItems}">
<p><b>Show the following items in this dataset:</b></p>

<span class="table_title_extract">Subject and Event Attributes</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td class="table_header_column_top">Event Location</td>
 <td class="table_header_column_top">Start Date</td>
 <td class="table_header_column_top">End Date</td>
 <td class="table_header_column_top"> 
   <c:choose>
     <c:when test="${study.studyParameterConfig.collectDob == '1'}">
     Date Of Birth
    </c:when>
    <c:when test="${study.studyParameterConfig.collectDob == '3'}">
     Date Of Birth
    </c:when>
    <c:otherwise>
     Year of Birth
    </c:otherwise> 
   </c:choose>
 </td>
 <td class="table_header_column_top">Gender</td>
 </tr>
 <tr>
 <td class="table_cell"><c:choose>
     <c:when test="${newDataset.showEventLocation}">
       yes 
     </c:when>
     <c:otherwise>
       no
     </c:otherwise>
    </c:choose>    
   </td>   
   <td class="table_cell">
   <c:choose>
     <c:when test="${newDataset.showEventStart}">
       yes 
     </c:when>
     <c:otherwise>
       no
     </c:otherwise>
    </c:choose>
   </td>  
   <td class="table_cell"> 
     <c:choose>
     <c:when test="${newDataset.showEventEnd}">
       yes
     </c:when>
     <c:otherwise>
       no
     </c:otherwise>
    </c:choose>   
   </td>
 <td class="table_cell">
 <c:choose>
     <c:when test="${newDataset.showSubjectDob}">
       yes
     </c:when>
     <c:otherwise>
       no
     </c:otherwise>
   </c:choose>  
 </td>
 <td class="table_cell">
  <c:choose>
     <c:when test="${newDataset.showSubjectGender}">
       yes
     </c:when>
     <c:otherwise>
       no
     </c:otherwise>
   </c:choose> 
  
 </td>
</tr>

</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
<br>

<span class="table_title_extract">CRF Data</span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
    <td class="table_header_column_top">Name</td>          
    <td class="table_header_column_top">Description</td> 
    <td class="table_header_column_top">Event</td> 
    <td class="table_header_column_top">CRF</td> 
    <td class="table_header_column_top">Version(s)</td>
    <td class="table_header_column_top">Data Type</td>
    <td class="table_header_column_top">Units</td>    
    <td class="table_header_column_top">Response Label</td>
    <td class="table_header_column_top">PHI</td>   
   
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
        Yes
      </c:when>
      <c:otherwise>
        No
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