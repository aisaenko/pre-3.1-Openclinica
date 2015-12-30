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

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">		  
        
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='displayItemData' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='studySubId' type='java.lang.String'/>
<c:choose>
<c:when test="${userBean.sysAdmin}">
  <h1><span class="title_Admin">
</c:when>
<c:otherwise>
  <h1><span class="title_manage">
</c:otherwise>
</c:choose>
View Event CRF Properties: <c:out value="${studySub.label}"/> - <c:out value="${crf.name}"/></span></h1>
<c:forEach var="section" items="${sections}">
 <div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
  <td class="table_header_column_top">Section Name</td>
  <td class="table_header_column_top">Title</td> 
  <td class="table_header_column_top">Subtitle</td>  
  <td class="table_header_column_top">Instructions</td>
  <td class="table_header_column_top">Page Number Label</td>
 </tr>
  <tr valign="top">
   <td class="table_cell"><c:out value="${section.name}"/></td>
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
<div style="width: 100%">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">             
    <td class="table_header_column_top">Name</td> 
    <td class="table_header_column_top">Description</td> 
    <td class="table_header_column_top">Units</td> 
    <td class="table_header_column_top">PHI Status</td> 
    <td class="table_header_column_top">Page Number Label</td>
    <td class="table_header_column_top">Question Number Label</td> 
    <td class="table_header_column_top">Left Item Text</td>  
    <td class="table_header_column_top">Right Item Text</td>   
    <td class="table_header_column_top">Response Label</td>   
    <td class="table_header_column_top">Response Value</td>   
    <td class="table_header_column_top">Required</td>         
  </tr>        
  
 <c:forEach var="did" items="${section.items}">
   <tr valign="top">             
    <td class="table_cell"><c:out value="${did.item.name}"/></td> 
    <td class="table_cell"><c:out value="${did.item.description}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${did.item.units}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${did.item.phiStatus}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${did.metadata.pageNumberLabel}"/>&nbsp;</td>
    <td class="table_cell"><c:out value="${did.metadata.questionNumberLabel}"/>&nbsp;</td> 
    <td class="table_cell"><c:out value="${did.metadata.leftItemText}"/></td>  
    <td class="table_cell"><c:out value="${did.metadata.rightItemText}"/>&nbsp;</td>   
    <td class="table_cell"><c:out value="${did.metadata.responseSet.label}"/>&nbsp;</td>   
    <td class="table_cell"><c:out value="${did.data.value}"/>&nbsp;</td>  
    <td class="table_cell">
     <c:choose>
      <c:when test="${did.metadata.required==true}">
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
<br>
   
<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
 </c:import>

<jsp:include page="../include/footer.jsp"/>
