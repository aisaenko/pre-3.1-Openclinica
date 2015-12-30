<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<c:set var="eblRowCount" value="${param.eblRowCount}" />
<!-- row number: <c:out value="${eblRowCount}"/> -->

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.bean.managestudy.DisplayStudySubjectRow" />
  <tr valign="top">
      <!--<td class="table_cell"><c:out value="${currRow.bean.studySubject.uniqueIdentifier}"/></td>-->
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
     
      <!--<td class="table_cell"><fmt:formatDate value="${currRow.bean.studySubject.enrollmentDate}" pattern="MM/dd/yyyy"/>&nbsp;</td>-->
     
      <c:set var ="prevDefId" value="1"/>
      <c:set var="currDefId" value="1"/>
      <c:set var ="repeatNum" value="0"/>
      <c:set var="count" value="${0}"/>
      <c:forEach var="event" items="${currRow.bean.studyEvents}">           
        <td class="table_cell">     
        <table border="0" cellpadding="0" cellspacing="0">
         <tr vlign="top">
         <td>
          <c:import url="../submit/eventLayer.jsp">
           <c:param name="colCount" value="${count}"/>
           <c:param name="rowCount" value="${eblRowCount}"/>
           <c:param name="eventId" value="${event.id}"/>
           <c:param name="eventStatus" value="${event.subjectEventStatus.name}"/>
           <c:param name="eventName" value="${event.studyEventDefinition.name}"/>
           <c:param name="eventDefId" value="${event.studyEventDefinition.id}"/>
           <c:param name="subjectId" value="${currRow.bean.studySubject.id}"/>
           <c:param name="subjectName" value="${currRow.bean.studySubject.label}"/>
         </c:import>
          <c:choose>
        <c:when test="${event.subjectEventStatus.id==1}">  
         
	      <img src="images/icon_Scheduled.gif"  border="0" style="position: relative; left: 7px;">	         
         </c:when>
        <c:when test="${event.subjectEventStatus.id==2}">
         
         <img src="images/icon_NotStarted.gif"  border="0" style="position: relative; left: 7px;">     
        
        </c:when>
        <c:when test="${event.subjectEventStatus.id==3}">
       
         <img src="images/icon_InitialDE.gif"  border="0" style="position: relative; left: 7px;">     
        
        </c:when>
        <c:when test="${event.subjectEventStatus.id==4}">
         
         <img src="images/icon_DEcomplete.gif" border="0" style="position: relative; left: 7px;">   
        
        </c:when>  
        <c:when test="${event.subjectEventStatus.id==5}">  
            
         <img src="images/icon_Stopped.gif" border="0" style="position: relative; left: 7px;"> 
        
        </c:when>   
        <c:when test="${event.subjectEventStatus.id==6}">
         
         <img src="images/icon_Skipped.gif" border="0" style="position: relative; left: 7px;">   
        
        </c:when>
        <c:when test="${event.subjectEventStatus.id==7}">
        
         <img src="images/icon_Locked.gif" border="0" style="position: relative; left: 7px;">   
        
        </c:when>    
        </c:choose> 
         </a><img name="ExpandIcon_<c:out value="${currRow.bean.studySubject.label}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>" src="images/icon_blank.gif" width="15" height="15" style="position: relative; left: 8px;">      
       
        </td>
                
         <c:if test="${event.repeatingNum>1}">
         <td vlign="top">
          &nbsp;(<c:out value="${event.repeatingNum}"/>)
         </td>
         </c:if>
         </tr>
         </table>&nbsp;
        </td>
     
       <c:set var="count" value="${count+1}"/>
       </c:forEach>
	    
      <td class="table_cell">
      <table border="0" cellpadding="0" cellspacing="0">
      <tr>
      <td>
      <a href="ViewStudySubject?id=<c:out value="${currRow.bean.studySubject.id}"/>"
	  onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
	  onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6"></a>
	  </td>
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
      </tr>
      </table>
      </td>
   </tr>
   
