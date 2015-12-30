<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<c:set var="eblRowCount" value="${param.eblRowCount}" />
<!-- row number: <c:out value="${eblRowCount}"/> -->
<c:set var="eventCRFNum" value="${param.eventDefCRFNum}" />

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.DisplayStudySubjectRow" />
 <c:set var="eventCount" value="0"/>
 <c:forEach var="event" items="${currRow.bean.studyEvents}"> 
   <c:set var="eventCount" value="${eventCount+1}"/>
 </c:forEach>
 
 
 <c:choose> 
 <c:when test="${!empty currRow.bean.studyEvents}">
 <c:set var="count" value="0"/>
 <c:forEach var="event" items="${currRow.bean.studyEvents}"> 
     <!-- an event is a DisplayStudyEventBean,which has -->
     <!--StudyEventBean studyEvent; -->
     <!--ArrayList allEventCRFs; -->
     <!--StudySubjectBean studySubject;-->
     <!--havingEventCRF = false;-->   
     
 
    <tr valign="top">
    
     <c:if test="${count==0}">
      <td class="table_cell_left" rowspan="<c:out value="${eventCount}"/>"><c:out value="${currRow.bean.studySubject.label}"/>&nbsp;</td> 
      <td class="table_cell" rowspan="<c:out value="${eventCount}"/>" style="display: none" id="Groups_0_1_<c:out value="${eblRowCount+1}"/>">
       <c:choose>
        <c:when test="${currRow.bean.studySubject.status.id==1}">
        active
        </c:when>
        <c:otherwise>
        inactive
        </c:otherwise>
      </c:choose>
      </td> 
      <td class="table_cell" rowspan="<c:out value="${eventCount}"/>" style="display: none" id="Groups_0_2_<c:out value="${eblRowCount+1}"/>"><c:out value="${currRow.bean.studySubject.gender}"/>&nbsp;</td> 
      
      <c:set var="groupCount" value="3"/>
      <c:forEach var="group" items="${currRow.bean.studyGroups}">
         <td class="table_cell" rowspan="<c:out value="${eventCount}"/>" style="display: none" id="Groups_0_<c:out value="${groupCount}"/>_<c:out value="${eblRowCount+1}"/>"><c:out value="${group.studyGroupName}"/>&nbsp;</td>
       <c:set var="groupCount" value="${groupCount+1}"/>
      </c:forEach>           
    
    </c:if>
 
    
     
     <!-- <td class="table_cell"><c:out value="${count+1}"/></td>-->
      <td class="table_cell_shaded">  
        <c:import url="../submit/eventLayer.jsp">
           <c:param name="colCount" value="1"/>
           <c:param name="rowCount" value="${eblRowCount+count}"/>
           <c:param name="eventId" value="${event.studyEvent.id}"/>
           <c:param name="eventStatus" value="${event.studyEvent.subjectEventStatus.name}"/>
           <c:param name="eventName" value="${event.studyEvent.studyEventDefinition.name}"/>
           <c:param name="subjectId" value="${currRow.bean.studySubject.id}"/>
           <c:param name="subjectName" value="${currRow.bean.studySubject.label}"/>
         </c:import>    
      <c:choose>
        <c:when test="${event.studyEvent.subjectEventStatus.id==1}">  
             	                
           <img src="images/icon_Scheduled.gif"  border="0" style="position: relative; left: 7px;">	         
        
        </c:when>
         <c:when test="${event.studyEvent.subjectEventStatus.id==2}">
           <img src="images/icon_NotStarted.gif"  border="0" style="position: relative; left: 7px;">	         
        
        
        </c:when>
        <c:when test="${event.studyEvent.subjectEventStatus.id==3}">
          <img src="images/icon_InitialDE.gif"  border="0" style="position: relative; left: 7px;">	         
        
        
        </c:when>
        <c:when test="${event.studyEvent.subjectEventStatus.id==4}">
          <img src="images/icon_DEcomplete.gif"  border="0" style="position: relative; left: 7px;">	         
        
        </c:when>  
        <c:when test="${event.studyEvent.subjectEventStatus.id==5}">        
         <img src="images/icon_Stopped.gif"  border="0" style="position: relative; left: 7px;">	         
       
         
        </c:when>   
        <c:when test="${event.studyEvent.subjectEventStatus.id==6}">
         <img src="images/icon_Skipped.gif"  border="0" style="position: relative; left: 7px;">	         
        
        </c:when>
        <c:when test="${event.studyEvent.subjectEventStatus.id==7}">
         <img src="images/icon_Locked.gif"  border="0" style="position: relative; left: 7px;">	         
       
        </c:when>          
        </c:choose> 
          </a><img name="ExpandIcon_<c:out value="${currRow.bean.studySubject.label}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount+count}"/>" src="images/icon_blank.gif" width="15" height="15" style="position: relative; left: 8px;">            
        
       
      </td>
      <td class="table_cell"><fmt:formatDate value="${event.studyEvent.dateStarted}" pattern="MM/dd/yyyy"/>&nbsp;</td>
      <c:choose>
       
      <c:when test="${empty event.allEventCRFs}">
       <c:set var="edcNum" value="0"/>
       <c:forEach var="edc" items="${eventDefCRFs}">
         <td class="table_cell_shaded">
           <c:import url="../submit/eventCrfLayer.jsp">
		    <c:param name="colCount" value="${edcNum}"/>
            <c:param name="rowCount" value="${eblRowCount+count}"/>
            <c:param name="eventId" value="${event.studyEvent.id}"/>
            <c:param name="crfStatus" value="${'Not Started'}"/>
            <c:param name="crfId" value="${0}"/>   
            <c:param name="crfName" value="${edc.crf.name}"/>
            <c:param name="crfVersionId" value="${edc.defaultVersionId}"/> 
            <c:param name="edcId" value="${edc.id}"/>         
            <c:param name="subjectId" value="${currRow.bean.studySubject.id}"/>
            <c:param name="subjectName" value="${currRow.bean.studySubject.label}"/>
		  </c:import>
            <img name="CRFicon_<c:out value="${eblRowCount+count}"/>_<c:out value="${eventCRFNum-1}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/icon_NotStarted.gif" border="0">
           </a>
         </td>
         <c:set var="edcNum" value="${edcNum+1}"/>
         
       </c:forEach>
      </c:when> 
      <c:otherwise>
       <c:set var="crfCount" value="${0}"/>
      <c:forEach var="dec" items="${event.allEventCRFs}">   
        
      <td class="table_cell_shaded">
      <c:choose>
      <c:when test="${dec.eventCRF.id > 0}">
         <c:import url="../submit/eventCrfLayer.jsp">
		    <c:param name="colCount" value="${crfCount}"/>
            <c:param name="rowCount" value="${eblRowCount+count}"/>
            <c:param name="eventId" value="${event.studyEvent.id}"/>
            <c:param name="crfStatus" value="${dec.stage.name}"/>
            <c:param name="crfId" value="${dec.eventCRF.id}"/>   
            <c:param name="crfName" value="${dec.eventCRF.crf.name}"/>          
            <c:param name="subjectId" value="${currRow.bean.studySubject.id}"/>
            <c:param name="subjectName" value="${currRow.bean.studySubject.label}"/>
		  </c:import>
       <c:choose>		 
		 <c:when test="${dec.stage.name=='initial data entry'}">		  
		  <img name="CRFicon_<c:out value="${eblRowCount+count}"/>_<c:out value="${crfCount}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/CRF_status_icon_Started.gif" border="0">
		  </a>
		 </c:when>
		 <c:when test="${dec.stage.name=='initial data entry complete'}">
		  <img name="CRFicon_<c:out value="${eblRowCount+count}"/>_<c:out value="${crfCount}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/CRF_status_icon_InitialDEcomplete.gif" border="0">
		  </a>
		 </c:when>
		 <c:when test="${dec.stage.name=='double data entry'}">
		    <img name="CRFicon_<c:out value="${eblRowCount+count}"/>_<c:out value="${crfCount}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/CRF_status_icon_DDE.gif" border="0">
		   </a>
		 </c:when>
		 <c:when test="${dec.stage.name=='data entry complete'}">
		   <img name="CRFicon_<c:out value="${eblRowCount+count}"/>_<c:out value="${crfCount}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/CRF_status_icon_Complete.gif" border="0">
		   </a>
		 </c:when>
		 <c:when test="${dec.stage.name=='administrative editing'}">
		    <img name="CRFicon_<c:out value="${eblRowCount+count}"/>_<c:out value="${crfCount}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/CRF_status_icon_Complete.gif" border="0">
		   </a>
		 </c:when>
		 <c:when test="${dec.stage.name=='locked'}">
		   <img name="CRFicon_<c:out value="${eblRowCount+count}"/>_<c:out value="${crfCount}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/icon_Locked.gif" border="0">
		   </a>
		 </c:when>
		 <c:otherwise>
		    <img name="CRFicon_<c:out value="${eblRowCount+count}"/>_<c:out value="${crfCount}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/CRF_status_icon_Invalid.gif" border="0">
		   </a>
		 </c:otherwise>
		</c:choose>	
		
		</c:when>
		
		<c:otherwise>
		   <c:import url="../submit/eventCrfLayer.jsp">
		    <c:param name="colCount" value="${crfCount}"/>
            <c:param name="rowCount" value="${eblRowCount+count}"/>
            <c:param name="eventId" value="${event.studyEvent.id}"/>
            <c:param name="crfStatus" value="${'Not Started'}"/>
            <c:param name="crfId" value="${0}"/>  
            <c:param name="edcId" value="${dec.eventDefinitionCRF.id}"/> 
            <c:param name="crfVersionId" value="${dec.eventDefinitionCRF.defaultVersionId}"/>  
            <c:param name="crfName" value="${dec.eventDefinitionCRF.crf.name}"/>          
            <c:param name="subjectId" value="${currRow.bean.studySubject.id}"/>
            <c:param name="subjectName" value="${currRow.bean.studySubject.label}"/>
		  
		  </c:import>
           <img name="CRFicon_<c:out value="${eblRowCount+count}"/>_<c:out value="${crfCount}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/CRF_status_icon_Scheduled.gif" border="0">
           </a>
        </c:otherwise>
		
		</c:choose>
      </td>
      <c:set var="crfCount" value="${crfCount+1}"/>
      </c:forEach>   
          
      </c:otherwise>
      </c:choose>      
      
	 <c:if test="${count==0}">
      <td class="table_cell" rowspan="<c:out value="${eventCount}"/>">
      <table border="0" cellpadding="0" cellspacing="0">
      <tr>
      <td>
      <a href="ViewStudySubject?id=<c:out value="${currRow.bean.studySubject.id}"/>"
	  onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View" align="left" hspace="6"></a>
	  </td>	  
	    <c:if test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
	  <c:choose>	  
       <c:when test="${currRow.bean.studySubject.status.name !='removed'}">
        <td><a href="RemoveStudySubject?action=confirm&id=<c:out value="${currRow.bean.studySubject.id}"/>&subjectId=<c:out value="${currRow.bean.studySubject.subjectId}"/>&studyId=<c:out value="${currRow.bean.studySubject.studyId}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		</td>
		<td><a href="ReassignStudySubject?id=<c:out value="${currRow.bean.studySubject.id}"/>"
			onMouseDown="javascript:setImage('bt_Reassign1','images/bt_Reassign_d.gif');"
			onMouseUp="javascript:setImage('bt_Reassign1','images/bt_Reassign.gif');"><img 
			name="bt_Reassign1" src="images/bt_Reassign.gif" border="0" alt="Reassign" title="Reassign" align="left" hspace="6"></a>
		</td>        
       
       </c:when>
       <c:otherwise>
        <td>
          <a href="RestoreStudySubject?action=confirm&id=<c:out value="${currRow.bean.studySubject.id}"/>&subjectId=<c:out value="${currRow.bean.studySubject.subjectId}"/>&studyId=<c:out value="${currRow.bean.studySubject.studyId}"/>"
		   onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
		   onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
		   name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
		</td>
       </c:otherwise>
      </c:choose>
      </c:if>
      </tr>
      </table>
      </td>
     </c:if>
      <c:set var="count" value="${count+1}"/>  
   </tr>
   
    </c:forEach> 
    
   </c:when>
   
   <c:otherwise>
     <tr valign="top">
      <td class="table_cell_left"><c:out value="${currRow.bean.studySubject.label}"/>&nbsp;</td> 
      <td class="table_cell" style="display: none" id="Groups_0_1_<c:out value="${eblRowCount+1}"/>">
      <c:choose>
        <c:when test="${currRow.bean.studySubject.status.id==1}">
        active
        </c:when>
        <c:otherwise>
        inactive
        </c:otherwise>
      </c:choose>
      </td> 
      <td class="table_cell" style="display: none" id="Groups_0_2_<c:out value="${eblRowCount+1}"/>"><c:out value="${currRow.bean.studySubject.gender}"/>&nbsp;</td> 
      
      <c:set var="groupCount" value="3"/>
      <c:forEach var="group" items="${currRow.bean.studyGroups}">
         <td class="table_cell" style="display: none" id="Groups_0_<c:out value="${groupCount}"/>_<c:out value="${eblRowCount+1}"/>"><c:out value="${group.studyGroupName}"/>&nbsp;</td>
       <c:set var="groupCount" value="${groupCount+1}"/>
      </c:forEach>  
     <!-- <td class="table_cell">&nbsp;</td>-->
      <td class="table_cell_shaded">
        <c:import url="../submit/eventLayer.jsp">
           <c:param name="colCount" value="1"/>
           <c:param name="rowCount" value="${eblRowCount}"/>
           <c:param name="eventId" value="${event.studyEvent.id}"/>
           <c:param name="eventStatus" value="${'not scheduled'}"/>
           <c:param name="eventName" value="${studyEventDef.name}"/>
           <c:param name="subjectId" value="${currRow.bean.studySubject.id}"/>
           <c:param name="subjectName" value="${currRow.bean.studySubject.label}"/>
         </c:import>  
         <img src="images/icon_NotStarted.gif"  border="0" style="position: relative; left: 7px;">	         
         </a><img name="ExpandIcon_<c:out value="${currRow.bean.studySubject.label}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>" src="images/icon_blank.gif" width="15" height="15" style="position: relative; left: 8px;">            
        
      </td>  
      <td class="table_cell">&nbsp;</td>   
     <%-- <c:forEach begin="1" end="${eventCRFNum}">--%>
       <c:set var="edcNum" value="0"/>
      <c:forEach var="edc" items="${eventDefCRFs}">     
         <td class="table_cell_shaded">
            <c:import url="../submit/eventCrfLayer.jsp">
		    <c:param name="colCount" value="${edcNum}"/>
            <c:param name="rowCount" value="${eblRowCount}"/>
            <c:param name="eventId" value="${event.studyEvent.id}"/>
            <c:param name="crfStatus" value="${'Not Started'}"/>
            <c:param name="crfId" value="${0}"/>  
            <c:param name="edcId" value="${edc.id}"/>   
            <c:param name="crfVersionId" value="${edc.defaultVersionId}"/> 
            <c:param name="crfName" value="${edc.crf.name}"/>          
            <c:param name="subjectId" value="${currRow.bean.studySubject.id}"/>
            <c:param name="subjectName" value="${currRow.bean.studySubject.label}"/>
		  
		  </c:import>
            <img name="CRFicon_<c:out value="${eblRowCount}"/>_<c:out value="${eventCRFNum-1}"/>_<c:out value="${currRow.bean.studySubject.label}"/>" src="images/CRF_status_icon_Scheduled.gif" border="0">
         </a>
         </td>
         <c:set var="edcNum" value="${edcNum+1}"/>
        
       </c:forEach> 
      <td class="table_cell">
      <table border="0" cellpadding="0" cellspacing="0">
      <tr>
      <td>
      <a href="ViewStudySubject?id=<c:out value="${currRow.bean.studySubject.id}"/>"
	  onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View" align="left" hspace="6"></a>
	  </td>	 
	   
	  <c:if test="${userRole.role.name=='coordinator' || userRole.role.name=='director'}">
	  <c:choose>	  
       <c:when test="${currRow.bean.studySubject.status.name !='removed'}">
        <td><a href="RemoveStudySubject?action=confirm&id=<c:out value="${currRow.bean.studySubject.id}"/>&subjectId=<c:out value="${currRow.bean.studySubject.subjectId}"/>&studyId=<c:out value="${currRow.bean.studySubject.studyId}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		</td>
		<td><a href="ReassignStudySubject?id=<c:out value="${currRow.bean.studySubject.id}"/>"
			onMouseDown="javascript:setImage('bt_Reassign1','images/bt_Reassign_d.gif');"
			onMouseUp="javascript:setImage('bt_Reassign1','images/bt_Reassign.gif');"><img 
			name="bt_Reassign1" src="images/bt_Reassign.gif" border="0" alt="Reassign" title="Reassign" align="left" hspace="6"></a>
		</td>        
       
       </c:when>
       <c:otherwise>
        <td>
          <a href="RestoreStudySubject?action=confirm&id=<c:out value="${currRow.bean.studySubject.id}"/>&subjectId=<c:out value="${currRow.bean.studySubject.subjectId}"/>&studyId=<c:out value="${currRow.bean.studySubject.studyId}"/>"
		   onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
		   onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img 
		   name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="Restore" title="Restore" align="left" hspace="6"></a>
		</td>
       </c:otherwise>
      </c:choose>
      </c:if>
      
      </tr>
      </table>
      </td>
     </tr>
     
   </c:otherwise> 
   </c:choose>