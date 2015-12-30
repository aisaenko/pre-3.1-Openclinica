<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
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


<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='definition' class='org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean'/>
<jsp:useBean scope='request' id='eventDefinitionCRFs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='defSize' type='java.lang.Integer'/>

<h1><span class="title_manage">View Event Definition </span></h1>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">   
  <tr valign="top"><td class="table_header_column">Name:</td><td class="table_cell">  
  <c:out value="${definition.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">Description:</td><td class="table_cell">  
  <c:out value="${definition.description}"/>&nbsp;
  </td></tr>
 
 <tr valign="top"><td class="table_header_column">Repeating:</td><td class="table_cell">
  <c:choose>
   <c:when test="${definition.repeating == true}"> Yes </c:when>
   <c:otherwise> No </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Type:</td><td class="table_cell">
    <c:out value="${definition.type}"/>
   </td></tr>
  
  <tr valign="top"><td class="table_header_column">Category:</td><td class="table_cell">  
  <c:out value="${definition.category}"/>&nbsp;
  </td></tr>
  </table>
  </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<c:if test="${!empty eventDefinitionCRFs}">
<div class="table_title_manage">CRFs</div>
<p>Click the "up/down" arrow icons in the "Order" column in the following table to change the order of all CRFs.</p>
<div style="width: 700px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">


<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
 <tr valign="top"> 
    <td class="table_header_row_left">Order</td>              
    <td class="table_header_row">Name</td>   
    <td valign="top" class="table_header_row">Required</td>     
    <td valign="top" class="table_header_row">Double Data Entry</td>         
    <!-- <td valign="top" class="table_header_row">Enforce Decision Conditions</td>-->  
    <td valign="top" class="table_header_row">Default Version</td>
    <td valign="top" class="table_header_row">Null Values</td>
    <td valign="top" class="table_header_row">Status</td>
    <td valign="top" class="table_header_row">Actions</td>
  </tr>             
 
  <c:set var="prevCrf" value=""/>
  <c:set var="count" value="0"/>
  <c:set var="last" value="${defSize-1}"/>
 <c:forEach var ="crf" items="${eventDefinitionCRFs}" varStatus="status">
  <c:choose>
    <c:when test="${count == last}">
      <c:set var="nextCrf" value="${eventDefinitionCRFs[count]}"/>
    </c:when>  
    <c:otherwise> 
     <c:set var="nextCrf" value="${eventDefinitionCRFs[count+1]}"/>
    </c:otherwise>
  </c:choose>
   <tr valign="top">
    <td class="table_cell_left">
      <c:choose>
        <c:when test="${status.first}">
          <c:choose>
           <c:when test="${defSize>1}">
            <a href="ChangeDefinitionCRFOrdinal?current=<c:out value="${nextCrf.id}"/>&previous=<c:out value="${crf.id}"/>&id=<c:out value="${definition.id}"/>"><img src="images/bt_sort_descending.gif" border="0" alt="move down" title="move down"/></a>
           </c:when>
           <c:otherwise>
             &nbsp;
           </c:otherwise>
          </c:choose>         
        </c:when>
        <c:when test="${status.last}">
           <a href="ChangeDefinitionCRFOrdinal?current=<c:out value="${crf.id}"/>&previous=<c:out value="${prevCrf.id}"/>&id=<c:out value="${definition.id}"/>"><img src="images/bt_sort_ascending.gif" alt="move up" title="move up" border="0"/></a>         
        </c:when>
        <c:otherwise>
          <a href="ChangeDefinitionCRFOrdinal?current=<c:out value="${crf.id}"/>&previous=<c:out value="${prevCrf.id}"/>&id=<c:out value="${definition.id}"/>"><img src="images/bt_sort_ascending.gif" alt="move up" title="move up" border="0" /></a>
          <a href="ChangeDefinitionCRFOrdinal?previous=<c:out value="${crf.id}"/>&current=<c:out value="${nextCrf.id}"/>&id=<c:out value="${definition.id}"/>"><img src="images/bt_sort_descending.gif" alt="move down" title="move up" border="0" /></a>
        </c:otherwise>
      </c:choose>
    </td>             
    <td class="table_cell"><c:out value="${crf.crfName}"/></td> 
    <td class="table_cell">
    <c:choose>
    <c:when test="${crf.requiredCRF == true}"> Yes </c:when>
     <c:otherwise> No </c:otherwise>
    </c:choose>
   </td>
     
    <td class="table_cell">
     <c:choose>
      <c:when test="${crf.doubleEntry == true}"> Yes </c:when>
      <c:otherwise> No </c:otherwise>
     </c:choose>
    </td>         
         
    <%--<td class="table_cell">
     <c:choose>
      <c:when test="${crf.decisionCondition == true}"> Yes </c:when>
      <c:otherwise> No </c:otherwise>
     </c:choose>
   </td>--%>
  
   <td class="table_cell">   
    <c:out value="${crf.defaultVersionName}"/>     
   </td>
   <td class="table_cell"> 
    <c:out value="${crf.nullValues}"/> &nbsp;    
  </td>          
  
   <td class="table_cell"><c:out value="${crf.status.name}"/></td> 
   <td class="table_cell">
     <table border="0" cellpadding="0" cellspacing="0">
	  <tr>       
        <td>
          <a href="ViewTableOfContent?crfVersionId=<c:out value="${crf.defaultVersionId}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="View CRF Version" title="View CRF Version" align="left" hspace="6"></a>
		</td>
		<c:if test="${crf.status.id==1 && crf.owner.id==userBean.id}">
		<td>
		 <a href="InitUpdateCRF?crfId=<c:out value="${crf.crfId}"/>"
			onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
			name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit CRF" title="Edit CRF" align="left" hspace="6"></a>
		  
		</td>
		</c:if>		
	  </tr>
	 </table> 	
   </td>
   </tr>
   <c:set var="prevCrf" value="${crf}"/>
   <c:set var="count" value="${count+1}"/>
 </c:forEach>
 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
</c:if>
<br>
<p><a href="ListEventDefinition">Go Back to Definition List</a></p>  
 
 <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/> 
 </c:import>
 
   
<jsp:include page="../include/footer.jsp"/>
