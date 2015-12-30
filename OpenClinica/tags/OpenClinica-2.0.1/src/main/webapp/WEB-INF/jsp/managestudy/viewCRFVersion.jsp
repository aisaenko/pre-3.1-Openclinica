<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:choose>
<c:when test="${userBean.sysAdmin}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-header.jsp"/>
</c:otherwise> 
</c:choose>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
        
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='sections' class='java.util.ArrayList'/>
<c:choose>
<c:when test="${userBean.sysAdmin}">
<h1><span class="title_Admin">View CRF Version Details </span></h1>
</c:when>
<c:otherwise>
<h1><span class="title_manage">View CRF Version Details </span></h1>
</c:otherwise> 
</c:choose>

<c:forEach var="section" items="${sections}">
<br>
<c:choose>
 <c:when test="${userBean.sysAdmin}">
  <span class="table_title_Admin">
 </c:when>
 <c:otherwise>
  <span class="table_title_manage">
 </c:otherwise>
</c:choose>
Section </span>
<div style="width: 700px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
 
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
  <td class="table_header_row_left">Section Name</td>
  <td class="table_header_row">Title</td> 
  <td class="table_header_row">Subtitle</td>  
  <td class="table_header_row">Instructions</td>
  <td class="table_header_row">Page Number Label</td>
 </tr>
  <tr valign="top">
   <td class="table_cell_left"><c:out value="${section.name}"/></td>
   <td class="table_cell"><c:out value="${section.title}"/></td>
   <td class="table_cell"><c:out value="${section.subtitle}"/>&nbsp;</td>
   <td class="table_cell"><c:out value="${section.instructions}"/>&nbsp;</td>
   <td class="table_cell"><c:out value="${section.pageNumberLabel}"/>&nbsp;</td>
  </tr> 
  </table>
  </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<c:choose>
 <c:when test="${userBean.sysAdmin}">
  <span class="table_title_Admin">
 </c:when>
 <c:otherwise>
  <span class="table_title_manage">
 </c:otherwise>
</c:choose>
Items</span>
<div style="width: 100%">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">             
    <td class="table_header_column_top">Name</td> 
    <td class="table_header_column_top">Description</td> 
    <td class="table_header_column_top">Units</td> 
    <td class="table_header_column_top">PHI Status</td> 
    <td class="table_header_column_top">Item Data Type</td> 
    <td class="table_header_column_top">Left Item Text</td>  
    <td class="table_header_column_top">Right Item Text</td>   
    <td class="table_header_column_top">Response Label</td>   
    <td class="table_header_column_top">Response Options</td>   
    <td class="table_header_column_top">Required</td>   
        
  </tr>        
  <c:forEach var ="item" items="${section.items}">   
   <tr valign="top">             
    <td class="table_cell"><c:out value="${item.name}"/></td> 
    <td class="table_cell"><c:out value="${item.description}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${item.units}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${item.phiStatus}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${item.dataType.description}"/>&nbsp;</td>
    <td class="table_cell"><c:out value="${item.itemMeta.leftItemText}"/></td>  
    <td class="table_cell"><c:out value="${item.itemMeta.rightItemText}"/>&nbsp;</td>   
    <td class="table_cell"><c:out value="${item.itemMeta.responseSet.label}"/></td>   
    <td class="table_cell">
    <c:set var="optionSize" value="0"/> 
    <c:forEach var="option" items="${item.itemMeta.responseSet.options}">
      <c:set var="optionSize" value="${optionSize+1}"/> 
    </c:forEach>
    <c:forEach var="option" items="${item.itemMeta.responseSet.options}">
     	<c:choose>
     		<c:when test="${optionSize > 1}">
     			<c:out value="${option.text}"/>,
     		</c:when>
     		<c:otherwise>
      			<c:out value="${option.text}"/>	 
      		</c:otherwise>		
      	</c:choose>
      	<c:set var="optionSize" value="${optionSize-1}"/>
    </c:forEach>&nbsp;
    </td>  
     <td class="table_cell">
     <c:choose>
      <c:when test="${item.itemMeta.required==true}">
       Yes 
      </c:when> 
      <c:otherwise>
        No
      </c:otherwise> 
      </c:choose>
      </td> 
  </tr>          
   
 </c:forEach>
 </table>
 </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
</c:forEach>    

 <p><a href="ListCRF">Go Back to CRF List</a></p>
     
<c:choose>
  <c:when test="${userBean.sysAdmin}">
  <c:import url="../include/workflow.jsp">
   <c:param name="module" value="admin"/> 
  </c:import>
 </c:when>
  <c:otherwise>
   <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/> 
  </c:import>
  </c:otherwise> 
 </c:choose> 

<jsp:include page="../include/footer.jsp"/>
