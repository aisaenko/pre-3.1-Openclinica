<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
		Study Events and CRFs for each event that you selected for the dataset are shown on the left side in the Task Pane. 

        You may click on each column name link to see the full metadata for that item.
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Instructions</b>

		</td>
  </tr>

<jsp:include page="../include/extractDataSideInfo.jsp"/>



<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="dataset" class="org.akaza.openclinica.bean.extract.DatasetBean"/>
<%--<jsp:useBean scope="request" id="extractBean" class="org.akaza.openclinica.bean.extract.ExtractBean"/>--%>

<h1><span class="title_extract">Download Data: <c:out value="${dataset.name}"/> <a href="javascript:openDocWindow('help/3_5_exportDatasets_Help.html')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0">

  <tr valign="top" ><td class="table_header_column_top">Dataset Name:</td><td class="table_cell_top">
  <c:out value="${dataset.name}"/>
  </td></tr>
  <tr valign="top" ><td class="table_header_column">Dataset Description:</td><td class="table_cell">
  <c:out value="${dataset.description}"/>
  </td></tr>
  <tr valign="top" ><td class="table_header_column">Study Name:</td><td class="table_cell">  
  <c:out value="${extractBean.parentStudyName}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">Protocol ID:</td><td class="table_cell">  
  <c:out value="${extractBean.parentProtocolId}"/>
  </td></tr> 
  <tr valign="top"><td class="table_header_column">Date:</td><td class="table_cell">  
  <fmt:formatDate value="${extractBean.dateCreated}" pattern="MM/dd/yyyy"/>
  </td></tr> 
  <tr valign="top"><td class="table_header_column">Subjects:</td><td class="table_cell">  
  <c:out value="${extractBean.numSubjects}"/>
  </td></tr> 
</table>
</div>

</div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>

<br/>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0">
  <tr valign="top">
    <td class="table_header_row_left">Subject Unique ID</td>   
    <td class="table_header_row">Protocol ID_Site ID</td>
    <c:if test="${dataset.showSubjectDob}">    
      <c:choose>
        <c:when test="${study.studyParameterConfig.collectDob =='1'}">
         <td class="table_header_row">Date Of Birth</td>
        </c:when>
        <c:when test="${study.studyParameterConfig.collectDob =='3'}">
         <td class="table_header_row">Date Of Birth</td>
        </c:when>
        <c:otherwise>
          <td class="table_header_row">Year Of Birth</td>
        </c:otherwise>
      </c:choose>
    </c:if>
    <c:if test="${dataset.showSubjectGender}">      
       <td class="table_header_row">Gender</td>      
    </c:if>
    <c:forEach var="eventHeader" items="${extractBean.eventHeaders}">
       <td class="table_header_row"><c:out value="${eventHeader}"/>&nbsp;</td>
      </c:forEach>
    <c:forEach var="itemName" items="${extractBean.itemNames}">
      <td class="table_header_row"><a href="javascript: openDocWindow('ViewItemDetail?itemId=<c:out value="${itemName.item.id}"/>')"><c:out value="${itemName.itemHeaderName}"/></a></td>
    </c:forEach>
   </tr>   
   <c:forEach var="displayItem" items="${extractBean.rowValues}">
     <tr valign="top">
      <td class="table_cell_left"><c:out value="${displayItem.subjectName}"/></td>
      <td class="table_cell"><c:out value="${displayItem.studyLabel}"/></td>  
      <c:if test="${dataset.showSubjectDob}"> 
       <td class="table_cell"><c:out value="${displayItem.subjectDob}"/></td>
       </c:if>   
       <c:if test="${dataset.showSubjectGender}">      
         <td class="table_cell"><c:out value="${displayItem.subjectGender}"/></td>      
       </c:if>
      <c:forEach var="eventValue" items="${displayItem.eventValues}">
       <td class="table_cell"><c:out value="${eventValue}"/>&nbsp;</td>
      </c:forEach>
      <c:forEach var="itemValue" items="${displayItem.itemValues}">
       <td class="table_cell"><c:out value="${itemValue}"/>&nbsp;</td>
      </c:forEach>
     </tr>
   </c:forEach>  
</table>
</div>
</div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>


<jsp:include page="../include/footer.jsp"/>