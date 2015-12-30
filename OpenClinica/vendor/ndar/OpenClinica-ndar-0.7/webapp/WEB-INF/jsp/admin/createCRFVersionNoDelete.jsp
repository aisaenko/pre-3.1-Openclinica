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


<jsp:useBean scope='session' id='itemsHaveData' class='java.util.ArrayList'/>
<jsp:useBean scope='session' id='eventsForVersion' class='java.util.ArrayList'/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>
<c:choose>
<c:when test="${userBean.sysAdmin}">
<h1><span class="title_Admin">
</c:when>
<c:otherwise>
<h1><span class="title_manage">
</c:otherwise> 
</c:choose>
Create a new CRF version - Remove Previous Same Version Error
</span></h1>


 <c:if test="${not empty eventsForVersion}">
 <span class="alert">The previous CRF version has associated study event data and cannot be deleted. 
  Please <a href="CreateCRFVersion?crfId=<c:out value="${version.crfId}"/>">Go Back</a> and upload another version.</span>
 <div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
   <tr valign="top">        
      <td class="table_header_column_top">Event ID</td>     
      <td class="table_header_column_top">Owner</td>          
    </tr>  
  <c:forEach var="event" items="${eventsForVersion}">     
  <tr valign="top">        
      <td class="table_cell"><c:out value="${event.id}"/></td>     
      <td class="table_cell"><c:out value="${event.owner.name}"/></td>          
    </tr>  
 </c:forEach>
 </table>
 <br>
</div>

</div></div></div></div></div></div></div></div>
</div>
 </c:if>
 <br><br>
 <c:if test="${!empty itemsHaveData}">
 <span class="alert">Some items in the previous version have related data and cannot be removed.
 <br>Please <a href="CreateCRFVersion?crfId=<c:out value="${version.crfId}"/>">go back</a> and upload another version 
 <br>or change the following item names in your spreadsheet.</span>
 <div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" >
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
   <tr valign="top">        
      <td class="table_header_column_top">Item ID</td>
      <td class="table_header_column_top">Name</td>
      <td class="table_header_column_top">Owner</td>          
    </tr>  
  <c:forEach var="item" items="${itemsHaveData}">     
  <tr valign="top">        
      <td class="table_cell"><c:out value="${item.id}"/></td>
      <td class="table_cell"><c:out value="${item.name}"/></td>
      <td class="table_cell"><c:out value="${item.owner.name}"/></td>          
    </tr>  
 </c:forEach>
 
</table>
<br>
<br>
</div>

</div></div></div></div></div></div></div></div>
</div>
</c:if>
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