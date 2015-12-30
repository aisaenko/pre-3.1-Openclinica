<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<c:set var="eblRowCount" value="${param.eblRowCount}" />
<c:set var="showSchedule" value="${param.showSchedule}" />
<c:set var="showDataEntry" value="${param.showDataEntry}" />
<!--row number: <c:out value="${eblRowCount}"/> -->

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
         <tr valign="top">
         <td>       
<%-- start if show schedule --%>
      <c:choose>
        <c:when test="${showSchedule!='no'}">  
           <c:import url="../submit/eventLayer.jsp">
           <c:param name="colCount" value="${count}"/>
           <c:param name="rowCount" value="${eblRowCount}"/>
           <c:param name="eventId" value="${event.id}"/>
           <c:param name="eventStatus" value="${event.subjectEventStatus.name}"/>
           <c:param name="summaryDataEntryStatus" value="${event.summaryDataEntryStatus.name}"/>
           <c:param name="eventName" value="${event.studyEventDefinition.name}"/>
           <c:param name="eventDefId" value="${event.studyEventDefinition.id}"/>          
           <c:param name="subjectId" value="${currRow.bean.studySubject.id}"/>
           <c:param name="subjectName" value="${currRow.bean.studySubject.label}"/>
         </c:import>
        <c:choose>
        <c:when test="${event.subjectEventStatus.id==1}">  
         
	      <img src="images/icon_NotStarted.gif"  border="0" style="position: relative; left: 7px;" alt="proposed" title="" />	         
         </c:when>
        <c:when test="${event.subjectEventStatus.id==2}">  
         
	      <img src="images/icon_Scheduled.gif"  border="0" style="position: relative; left: 7px;" alt="scheduled" title="" />	         
        
         </c:when>
        <c:when test="${event.subjectEventStatus.id==3}">
         
         <img src="images/icon_InitialDE.gif"  border="0" style="position: relative; left: 7px;" alt="in progress" title="" />     
        
        </c:when>
        <c:when test="${event.subjectEventStatus.id==4}">
         
         <img src="images/icon_DEcomplete.gif" border="0" style="position: relative; left: 7px;" alt="completed" title="" />   
        
        </c:when>  
        <c:when test="${event.subjectEventStatus.id==5}">  
         
	      <img src="images/icon_Stopped.gif"  border="0" style="position: relative; left: 7px;" alt="canceled / no reschedule" title="" />	         
       
         </c:when>
        <c:when test="${event.subjectEventStatus.id==6}">  
         
	      <img src="images/icon_Stopped.gif"  border="1" style="position: relative; left: 7px;" alt="canceled / reschedule" title="" />	         
        
         </c:when>
        <c:when test="${event.subjectEventStatus.id==7}">
         
         <img src="images/icon_Skipped.gif" border="0" style="position: relative; left: 7px;" alt="no show / no reschedule" title="" />   
        </c:when>
        <c:when test="${event.subjectEventStatus.id==8}">
         
         <img src="images/icon_Skipped.gif" border="1" style="position: relative; left: 7px;" alt="no show / reschedule" title="" />   
        
        </c:when>
        <c:when test="${event.subjectEventStatus.id==9}">
        
         <img src="images/icon_Locked.gif" border="0" style="position: relative; left: 7px;" alt="locked" title="" />   
        
        </c:when>    
        </c:choose> 
         </a><img name="ExpandIcon_<c:out value="${currRow.bean.studySubject.label}"/>_<c:out value="${count}"/>_<c:out value="${eblRowCount}"/>" src="images/icon_blank.gif" width="15" height="15" style="position: relative; left: 8px;" alt="" />      
       <br />
        </c:when>  
      </c:choose>
<%-- end if show schedule --%>
<%-- start if show data entry --%>
          <c:choose>
        <c:when test="${showDataEntry=='yes'}">  
          <c:choose>
        <c:when test="${event.summaryDataEntryStatus.id==1 && event.subjectEventStatus.id>0}">  
         
	      <img src="images/icon_NotStarted.gif"  border="0" style="position: relative; left: 7px;" alt="Summary Data Entry: not started" title="Summary Data Entry: not started" />	         
         </c:when>
        <c:when test="${event.summaryDataEntryStatus.id==2 && event.subjectEventStatus.id>0}">  
         
	      <img src="images/icon_InitialDE.gif"  border="0" style="position: relative; left: 7px;" alt="Summary Data Entry: started" title="Summary Data Entry: started" />	         
        
         </c:when>
        <c:when test="${event.summaryDataEntryStatus.id==3 && event.subjectEventStatus.id>0}">
         
         <img src="images/icon_DEcomplete.gif"  border="0" style="position: relative; left: 7px;" alt="Summary Data Entry: completed" title="Summary Data Entry: completed" />     
        
        </c:when>
        </c:choose> 
        </c:when>  
      </c:choose>
<%-- end if show data entry --%>
<%-- start if show schedule, but not data entry --%>
      <c:choose>
        <c:when test="${showSchedule!='no' && showDataEntry!='yes' && event.subjectEventStatus.id > 0}">  
<fmt:formatDate value="${event.dateStarted}" pattern="MM/dd/yyyy"/>
        </c:when>  
      </c:choose>
<%-- end if show schedule, but not data entry --%>
<%-- start if show data entry, but not schedule
      <c:choose>
        <c:when test="${showSchedule=='no' && showDataEntry=='yes' && event.subjectEventStatus.id > 0 && event.summaryDataEntryStatus.id>0}">  
<br />4/16/07
        </c:when>  
      </c:choose> --%>
<%-- end if show data entry, but not schedule --%>
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
	  name="bt_View1" src="images/bt_View.gif" border="0" alt="View" title="View" align="left" hspace="6" /></a>
	  </td>	  
      </tr>
      </table>&nbsp;
      </td>
   </tr>
   