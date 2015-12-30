<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.admin.CRFRow" />
 <c:set var="count" value="${currRow.bean.versionNumber}"/>
 <c:set var="count" value="${count+1}"/>
 
<tr valign="top" bgcolor="#F5F5F5">        
      <td rowspan="<c:out value="${count}"/>" class="table_cell_left"><c:out value="${currRow.bean.name}"/></td>
      <td rowspan="<c:out value="${count}"/>" class="table_cell"><fmt:formatDate value="${currRow.bean.updatedDate}" pattern="MM/dd/yyyy"/>&nbsp;</td>
      <td rowspan="<c:out value="${count}"/>" class="table_cell"><c:out value="${currRow.bean.updater.name}"/>&nbsp;</td>
      <td class="table_cell">(original)</td>
      <td class="table_cell"><fmt:formatDate value="${currRow.bean.createdDate}" pattern="MM/dd/yyyy"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.owner.name}"/></td>      
      <td class="table_cell"><c:out value="${currRow.bean.status.name}"/></td>
      <td class="table_cell">&nbsp;</td>
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
        <tr>
         <td><a href="ViewCRF?crfId=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		</td>  
		<c:choose>     
        <c:when test="${currRow.bean.status.name=='available'}">
        <c:if test="${userBean.sysAdmin || ((userRole.role.name=='director' ||userRole.role.name=='coordinator') && userBean.name==currRow.bean.owner.name)}">
         <td>
          <a href="InitUpdateCRF?crfId=<c:out value="${currRow.bean.id}"/>"
		  onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
		  onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
		  name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a>
		 </td>
         <td><a href="RemoveCRF?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		 </td>	        
        </c:if>
        <td><a href="InitCreateCRFVersion?crfId=<c:out value="${currRow.bean.id}"/>&name=<c:out value="${currRow.bean.name}"/>"
			onMouseDown="javascript:setImage('bt_NewVersion1','images/bt_NewVersion_d.gif');"
			onMouseUp="javascript:setImage('bt_NewVersion1','images/bt_NewVersion.gif');"><img 
			name="bt_NewVersion1" src="images/bt_NewVersion.gif" border="0" alt="Create New Version" title="Create New Version" align="left" hspace="6"></a>
		</td>
		</c:when>
		<c:otherwise>
		  <td><a href="RestoreCRF?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
			name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
		 </td>   
		</c:otherwise>
		</c:choose>
      </tr>
	  </table> 
     </td>    
   </tr> 
 
   <c:forEach var ="version" items="${currRow.bean.versions}">   
   <tr valign="top">
    <td class="table_cell"><c:out value="${version.name}"/></td>   
    <td class="table_cell"><fmt:formatDate value="${version.createdDate}" pattern="MM/dd/yyyy"/></td>
    <td class="table_cell"><c:out value="${version.owner.name}"/></td>    
    <td class="table_cell"><c:out value="${version.status.name}"/></td>
    <td class="table_cell">
      <c:choose>
      <c:when test="${version.downloadable}">
        <a href="DownloadVersionSpreadSheet?crfId=<c:out value="${currRow.bean.id}"/>&crfVersionName=<c:out value="${version.name}"/>"
			onMouseDown="javascript:setImage('bt_Download1','images/bt_Download_d.gif');"
			onMouseUp="javascript:setImage('bt_Download1','images/bt_Download.gif');"><img 
			name="bt_Download1" src="images/bt_Download.gif" border="0" alt="Download SpreadSheet" title="Download SpreadSheet" align="left" hspace="6">
	    </a>
     </c:when>
     <c:otherwise>
     n/a
     </c:otherwise>
     </c:choose>
    </td>
    <td class="table_cell">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
        <td>
        <!--
        <a href="ViewTableOfContent?crfVersionId=<c:out value="${version.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		-->
		<a href="ViewSectionDataEntry?crfVersionId=<c:out value="${version.id}"/>&tabId=1"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		
		
		</td>       
      <c:if test="${userBean.sysAdmin || ((userRole.role.name=='director' ||userRole.role.name=='coordinator') && userBean.name==version.owner.name)}">
       <c:choose>
       <c:when test="${version.status.name=='available'}">
       <td><a href="RemoveCRFVersion?action=confirm&id=<c:out value="${version.id}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		 </td>
		</c:when> 
		<c:otherwise>
		 <td><a href="RestoreCRFVersion?action=confirm&id=<c:out value="${version.id}"/>"
			onMouseDown="javascript:setImage('bt_Restor1','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore1','images/bt_Restore.gif');"><img 
			name="bt_Restore1" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
		 </td> 
		</c:otherwise>	
	   </c:choose>		   
      </c:if>
       <c:if test="${userBean.sysAdmin}">
        <td><a href="DeleteCRFVersion?action=confirm&verId=<c:out value="${version.id}"/>"
			onMouseDown="javascript:setImage('bt_Delete1','images/bt_Delete_d.gif');"
			onMouseUp="javascript:setImage('bt_Delete1','images/bt_Delete.gif');"><img 
			name="bt_Delete1" src="images/bt_Delete.gif" border="0" alt="Delete" title="Delete" align="left" hspace="6"></a>
		 </td>
       </c:if>
      </tr>
     </table> 
    </td>
   </tr>  
   </c:forEach>
  <tr><td class="table_divider" colspan="9">&nbsp;</td></tr>
  