<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.DisplayStudyEventRow" />   
   <tr>       
     <td class="table_cell_left"><c:out value="${currRow.bean.studyEvent.studyEventDefinition.name}"/></td>
     <td class="table_cell"><fmt:formatDate value="${currRow.bean.studyEvent.dateStarted}" pattern="MM/dd/yyyy"/>
     </td>
     <%--<td class="table_cell"><fmt:formatDate value="${currRow.bean.studyEvent.updatedDate}" pattern="MM/dd/yyyy"/>
       <br>
       <c:if test="${currRow.bean.studyEvent.updater.name != null && currRow.bean.studyEvent.updater.name !=''}">
       (<c:out value="${currRow.bean.studyEvent.updater.name}"/>)
       </c:if>
       &nbsp;
     </td>--%>
     <td class="table_cell"><c:out value="${currRow.bean.studyEvent.location}"/></td> 
     <td class="table_cell"><c:out value="${currRow.bean.studyEvent.subjectEventStatus.name}"/></td>         
     <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
		<tr>
		<td>
        <a href="EnterDataForStudyEvent?eventId=<c:out value="${currRow.bean.studyEvent.id}"/>"
		onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
		onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
		<td>
        <a href="UpdateStudyEvent?event_id=<c:out value="${currRow.bean.studyEvent.id}"/>&ss_id=<c:out value="${studySub.id}"/>"
		onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
		onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img 
		name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6"></a>
		</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
		<td>
		<c:choose>
         <c:when test="${currRow.bean.studyEvent.status.name !='removed'}">
           <c:if test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
           <a href="RemoveStudyEvent?action=confirm&id=<c:out value="${currRow.bean.studyEvent.id}"/>&studySubId=<c:out value="${studySub.id}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
           </c:if>
         </c:when>
         <c:otherwise>
          <c:if test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
           <a href="RestoreStudyEvent?action=confirm&id=<c:out value="${currRow.bean.studyEvent.id}"/>&studySubId=<c:out value="${studySub.id}"/>"
			onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
			name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
		 </c:if>
         </c:otherwise>
        </c:choose>
      
		</td>
       </tr>
      </table>  
     </td>
     <td class="table_cell_nopadding">
      <c:choose>
	  <c:when test="${empty currRow.bean.uncompletedCRFs && empty currRow.bean.displayEventCRFs}">
		No CRFs
	  </c:when>
	  <c:otherwise>
	 
      <table border="0" cellpadding="0" cellspacing="0">
	
	  <c:forEach var="dedc" items="${currRow.bean.uncompletedCRFs}">
	
		<c:set var="getQuery" value="eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${currRow.bean.studyEvent.id}&subjectId=${studySub.subjectId}&eventCRFId=${dedc.eventCRF.id}" />
			
			<tr valign="top">
				
			  <td class="table_cell" width="180"><c:out value="${dedc.edc.crf.name}" /></td>
				
				<form name="startForm<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>" action="InitialDataEntry?<c:out value="${getQuery}"/>" method="POST">
				<td class="table_cell" with="100">
				 <input type="hidden" name="crfVersionId" value="<c:out value="${dedc.edc.defaultVersionId}"/>">
				 
				  <c:set var="versionCount" value="0"/>
				  <c:forEach var="version" items="${dedc.edc.versions}"> 
				    <c:set var="versionCount" value="${versionCount+1}"/>
				  </c:forEach>
				  
				 <c:choose>
				 <c:when test="${versionCount<=1}">
				   <c:forEach var="version" items="${dedc.edc.versions}"> 
				     <c:out value="${version.name}"/>
				   </c:forEach>
				 </c:when>
				 <c:otherwise>
				<select name="versionId<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>" onchange="javascript:changeQuery<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>();">  
				 <c:forEach var="version" items="${dedc.edc.versions}">   
				 <c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${currRow.bean.studyEvent.id}&subjectId=${studySub.subjectId}" />       
                 <c:choose>
                  <c:when test="${dedc.edc.defaultVersionId==version.id}">
                  <option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
                  </c:when>
                  <c:otherwise>
                    <option value="<c:out value="${version.id}"/>"><c:out value="${version.name}"/>
                  </c:otherwise>
                  </c:choose>        
             
                 </c:forEach>
                 </select>
                 
                 <SCRIPT LANGUAGE="JavaScript">  
                 function changeQuery<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>() {
                  var qer = document.startForm<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>.versionId<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>.value;
                  document.startForm<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>.crfVersionId.value=qer;   
                                                    
                 }
                </SCRIPT>	
                 </c:otherwise>
                 </c:choose>
                 
				</td>			
				
				<td class="table_cell" bgcolor="#F5F5F5" align="center" width="20"><img src="images/icon_NotStarted.gif" alt="Not Started" title="Not Started"></td>
				<td class="table_cell" width="80">&nbsp;</td>
				<td>
				<table border="0" cellpadding="0" cellspacing="0">
	            <tr valign="top">
				<td class="table_cell" width="140">				
				
				 <a href="#" onclick="javascript:document.startForm<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>.submit();"
				  onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
				  onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"><img 
				  name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="Enter Data" title="Enter Data" align="left" hspace="6">
				</a>	
				</form>	
				</td>
				<td class="table_cell">
		         <a href="ViewSectionDataEntry?crfVersionId=<c:out value="${dedc.edc.defaultVersionId}"/>&tabId=1"
			     onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			     onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		         name="bt_View1" src="images/bt_View.gif" border="0" alt="View Default" title="View Default" align="left" hspace="6"></a>
		        </td>
		        <td class="table_cell">
		         <a href="javascript:openDocWindow('PrintCRF?id=<c:out value="${dedc.edc.defaultVersionId}"/>')"
			     onMouseDown="javascript:setImage('bt_Print1','images/bt_Print_d.gif');"
			     onMouseUp="javascript:setImage('bt_Print1','images/bt_Print.gif');"><img 
		         name="bt_Print1" src="images/bt_Print.gif" border="0" alt="Print Default" title="Print Default" align="left" hspace="6"></a>
		        </td>
				</tr>
				</table>
				</td>
			</tr>
			
			
	</c:forEach>
	<c:forEach var="dec" items="${currRow.bean.displayEventCRFs}">
	<tr>
		<td class="table_cell" width="180"><c:out value="${dec.eventCRF.crf.name}" /></td>
		<td class="table_cell" width="100"><c:out value="${dec.eventCRF.crfVersion.name}" /></td>
		<td class="table_cell" bgcolor="#F5F5F5" align="center" width="20">
		<c:choose>		 
		 <c:when test="${dec.stage.name=='initial data entry'}">
		  <img src="images/icon_InitialDE.gif" alt="Initial Data Entry" title="Initial Data Entry">
		 </c:when>
		 <c:when test="${dec.stage.name=='initial data entry complete'}">
		  <img src="images/icon_InitialDEcomplete.gif" alt="Initial Data Entry Complete" title="Initial Data Entry Complete">
		 </c:when>
		 <c:when test="${dec.stage.name=='double data entry'}">
		    <img src="images/icon_DDE.gif" alt="Double Data Entry" title="Double Data Entry">
		 </c:when>
		 <c:when test="${dec.stage.name=='data entry complete'}">
		   <img src="images/icon_DEcomplete.gif" alt="Data Entry Complete" title="Data Entry Complete">
		 </c:when>
		 <c:when test="${dec.stage.name=='administrative editing'}">
		    <img src="images/icon_AdminEdit.gif" alt="Administrative Editing" title="Administrative Editing">
		 </c:when>
		 <c:when test="${dec.stage.name=='locked'}">
		   <img src="images/icon_Locked.gif" alt="Locked" title="Locked">
		 </c:when>
		 <c:otherwise>
		    <img src="images/icon_Invalid.gif" alt="Invalid" title="Invalid">
		 </c:otherwise>
		</c:choose>	
		
		</td>
		<td class="table_cell" width="80">		
		<c:if test="${dec.eventCRF.updatedDate != null}">
		 <fmt:formatDate value="${dec.eventCRF.updatedDate}" pattern="MM/dd/yyyy"/><br>
		</c:if>
		<c:choose>
		  <c:when test="${dec.eventCRF.updater.name == null}">
		    (<c:out value="${dec.eventCRF.owner.name}"/>)
		  </c:when>
		  <c:otherwise>
		   (<c:out value="${dec.eventCRF.updater.name}"/>)
		  </c:otherwise>
		 </c:choose>
		</td>
		
		<td class="table_cell" width="140">
		 <table border="0" cellpadding="0" cellspacing="0">
	     <tr valign="top">
	     <td>
			<c:if test="${dec.eventCRF.status.name!='removed'}">
			<c:if test="${dec.continueInitialDataEntryPermitted}">
			<!-- <a href="TableOfContents?action=ide_c&ecid=<c:out value="${dec.eventCRF.id}" />"
				onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
				onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"><img 
				name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="Continue Entering Data" title="Continue Entering Data" align="left" hspace="6"></a>					
			-->	
		    <a href="InitialDataEntry?eventCRFId=<c:out value="${dec.eventCRF.id}" />"
				onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
				onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"><img 
				name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="Continue Entering Data" title="Continue Entering Data" align="left" hspace="6"></a>					
					
			</c:if>
			<c:if test="${dec.startDoubleDataEntryPermitted}">
			<!--
			<a href="TableOfContents?action=dde_s&ecid=<c:out value="${dec.eventCRF.id}" />"
				onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
				onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"><img 
				name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="Begin Double Data Entry" title="Begin Double Data Entry" align="left" hspace="6"></a>					
			-->	
				<a href="DoubleDataEntry?eventCRFId=<c:out value="${dec.eventCRF.id}" />"
				onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
				onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"><img 
				name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="Begin Double Data Entry" title="Begin Double Data Entry" align="left" hspace="6"></a>					
				
			</c:if>
			<c:if test="${dec.continueDoubleDataEntryPermitted}">
			  <a href="DoubleDataEntry?eventCRFId=<c:out value="${dec.eventCRF.id}" />"
				onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
				onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"><img 
				name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="Begin Double Data Entry" title="Begin Double Data Entry" align="left" hspace="6"></a>						
				
			</c:if>

			<c:if test="${dec.performAdministrativeEditingPermitted}">
			 <!--<a href="TableOfContents?action=ae&ecid=<c:out value="${dec.eventCRF.id}" />"
				onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
				onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"><img 
				name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="Administrative Editing" title="Administrative Editing" align="left" hspace="6"></a>
			 -->
			 <a href="AdministrativeEditing?eventCRFId=<c:out value="${dec.eventCRF.id}" />"
				onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
				onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"><img 
				name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="Administrative Editing" title="Administrative Editing" align="left" hspace="6">
				</a>
			</c:if>

			<c:if test="${dec.locked}">
				locked
			</c:if>
			</c:if>			
		</td>
	     <td>
	     <!--
		 <a href="ViewEventCRFContent?id=<c:out value="${studySub.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>&eventId=<c:out value="${currRow.bean.studyEvent.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		 -->
		 <a href="ViewSectionDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
		 
		 </td>
		 <td>
		 <a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>')"
			onMouseDown="javascript:setImage('bt_Print1','images/bt_Print_d.gif');"
			onMouseUp="javascript:setImage('bt_Print1','images/bt_Print.gif');"><img 
		    name="bt_Print1" src="images/bt_Print.gif" border="0" alt="Print" title="Print" align="left" hspace="6"></a>
		 </td>
		<c:choose>
		<c:when test="${dec.eventCRF.status.name !='removed'}">
		 <c:if test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
		  <td><a href="RemoveEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySub.id}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		 </td> 
		 </c:if>
		</c:when>
		<c:otherwise>
		 <c:if test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
		  <td><a href="RestoreEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySub.id}"/>"
			onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
			name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
		  </td>	
		 </c:if>	
		</c:otherwise>
		</c:choose>
		<c:if test="${userBean.sysAdmin}">
		<td>
		 <a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${studySub.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>"
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
    </table>
	</c:otherwise>
    </c:choose>
        </td>
       
        
    </tr>