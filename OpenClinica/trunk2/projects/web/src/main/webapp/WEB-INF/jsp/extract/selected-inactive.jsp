<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/> 

<span class="table_title"><fmt:message key="subject_attributes" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
<tr>
 <th><fmt:message key="subject_status" bundle="${resword}"/></th>  
 <th><fmt:message key="subject_unique_ID" bundle="${resword}"/></th>      
 <th><fmt:message key="secondary_ID" bundle="${resword}"/></th>  

    <th>
   <c:choose>
    <c:when test="${study.studyParameterConfig.collectDob != '2'}">
        <fmt:message key="date_of_birth" bundle="${resword}"/>
    </c:when>
    <c:otherwise>
            <fmt:message key="year_of_birth" bundle="${resword}"/>
    </c:otherwise>
   </c:choose>
    </th>  
 <th><fmt:message key="gender" bundle="${resword}"/></th>  
 </tr>
 <tr>
    <td class="table_cell"><c:choose>
     <c:when test="${newDataset.showSubjectStatus}">
       <input type="checkbox" checked name="subj_status" value="yes" disabled="disabled">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="subj_status" value="yes" disabled="disabled">
     </c:otherwise>
    </c:choose>    
   </th>     

   <td class="table_cell">
   <c:choose>
     <c:when test="${newDataset.showSubjectUniqueIdentifier}">
       <input type="checkbox" checked name="unique_identifier" value="yes" disabled="disabled">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="unique_identifier" value="yes" disabled="disabled">
     </c:otherwise>
    </c:choose>
   </td> 

     <td class="table_cell">
     <c:choose>
       <c:when test="${newDataset.showSubjectSecondaryId}">
         <input type="checkbox" checked name="subj_secondary_id" value="yes" disabled="disabled">
       </c:when>
       <c:otherwise>
         <input type="checkbox" name="subj_secondary_id" value="yes" disabled="disabled">
       </c:otherwise>
      </c:choose>
     </td>

    <td class="table_cell">
        <c:choose>
            <c:when test="${newDataset.showSubjectDob}">
                <input type="checkbox" checked name="dob" value="yes" disabled="disabled">
            </c:when>
            <c:otherwise>
                <input type="checkbox" name="dob" value="yes" disabled="disabled">
            </c:otherwise>
       </c:choose>
    </td>
 <td class="table_cell">
  <c:choose>
     <c:when test="${newDataset.showSubjectGender}">
       <input type="checkbox" checked name="gender" value="yes" disabled="disabled">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="gender" value="yes" disabled="disabled">
     </c:otherwise>
   </c:choose> 
 </td>
</tr>
</table>
<br>
<span class="table_title"><fmt:message key="event_attributes" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
<tr>
 <th><fmt:message key="event_location" bundle="${resword}"/></th>
 <th><fmt:message key="start_date" bundle="${resword}"/></th>  
 <th><fmt:message key="end_date" bundle="${resword}"/></th>  
 <th><fmt:message key="status" bundle="${resword}"/></th>  
 <th><fmt:message key="subject_age_at_event" bundle="${resword}"/></th>  
 </tr>
 <tr>
 <td class="table_cell"><c:choose>
     <c:when test="${newDataset.showEventLocation}">
       <input type="checkbox" checked name="location" value="yes" disabled="disabled">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="location" value="yes" disabled="disabled">
     </c:otherwise>
    </c:choose>    
   </td>   
   <td class="table_cell">
   <c:choose>
     <c:when test="${newDataset.showEventStart}">
       <input type="checkbox" checked name="start" value="yes" disabled="disabled">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="start" value="yes" disabled="disabled">
     </c:otherwise>
    </c:choose>
   </td>  
   <td class="table_cell"> 
     <c:choose>
     <c:when test="${newDataset.showEventEnd}">
       <input type="checkbox" checked name="end" value="yes" disabled="disabled">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="end" value="yes" disabled="disabled">
     </c:otherwise>
    </c:choose>   
   </td>
 <td class="table_cell">
 <c:choose>
     <c:when test="${newDataset.showEventStatus}">
       <input type="checkbox" checked name="event_status" value="yes" disabled="disabled">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="event_status" value="yes" disabled="disabled">
     </c:otherwise>
   </c:choose>  
 </td> 
   <td class="table_cell"> 
     <c:choose>
     <c:when test="${newDataset.showSubjectAgeAtEvent}">
       <input type="checkbox" checked name="age_at_event" value="yes" disabled="disabled">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="age_at_event" value="yes" disabled="disabled">
     </c:otherwise>
    </c:choose>   
   </td>
</tr>
</table>
<br>

<span class="table_title_extract"><fmt:message key="CRF_attributes" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
<tr>
 <th><fmt:message key="CRF_version" bundle="${resword}"/></th>  
 <th><fmt:message key="interviewer_name" bundle="${resword}"/></th>  
 <th><fmt:message key="interview_date" bundle="${resword}"/></th>  
 <th><fmt:message key="CRF_status" bundle="${resword}"/></th>  
 </tr>
 <tr>
 <td class="table_cell"><c:choose>
     <c:when test="${newDataset.showCRFversion}">
       <input type="checkbox" checked name="crf_version" value="yes" disabled="disabled">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="crf_version" value="yes" disabled="disabled">
     </c:otherwise>
    </c:choose>    
   </td>   
   <td class="table_cell">
   <c:choose>
     <c:when test="${newDataset.showCRFinterviewerName}">
       <input type="checkbox" checked name="interviewer" value="yes" disabled="disabled">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="interviewer" value="yes" disabled="disabled">
     </c:otherwise>
    </c:choose>
   </td>  
   <td class="table_cell"> 
     <c:choose>
     <c:when test="${newDataset.showCRFinterviewerDate}">
       <input type="checkbox" checked name="interviewer_date" value="yes" disabled="disabled">  
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="interviewer_date" value="yes" disabled="disabled">
     </c:otherwise>
    </c:choose>   
   </td>
 <td class="table_cell">
 <c:choose>
     <c:when test="${newDataset.showCRFstatus}">
       <input type="checkbox" checked name="crf_status" value="yes" disabled="disabled">
     </c:when>
     <c:otherwise>
       <input type="checkbox" name="crf_status" value="yes" disabled="disabled">
     </c:otherwise>
   </c:choose>  
 </td>
</tr>
</table>
 
<br>
<span class="table_title "><fmt:message key="subject_group_attributes" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
<tr>
<td></td>
<th><fmt:message key="subject_group_name" bundle="${resword}"/></th>  
<th><fmt:message key="subject_group_type" bundle="${resword}"/></th>  
<th><fmt:message key="status" bundle="${resword}"/></th>  
<th><fmt:message key="subject_assignment" bundle="${resword}"/></th>  
</tr>
<%-- five columns --%>

   <c:forEach var="sgclass" items="${allSelectedGroups}">
   <tr>
	<c:choose>
   		<c:when test="${sgclass.selected}">
 			<td class="table_cell"><input type=checkbox checked name="groupSelected<c:out value="${sgclass.id}"/>" value="yes" disabled="disabled">
	 	</c:when>
 		<c:otherwise>
 	 		<td class="table_cell"><input type=checkbox name="groupSelected<c:out value="${sgclass.id}"/>" value="yes" disabled="disabled">
	 	</c:otherwise>
 	</c:choose>
		<td class="table_cell"><c:out value="${sgclass.name}"/></th>  
		<td class="table_cell"><c:out value="${sgclass.groupClassTypeName}"/></td>
		<td class="table_cell"><c:out value="${sgclass.status.name}"/></td>
		<td class="table_cell"><c:out value="${sgclass.subjectAssignment}"/></td>
	</tr>
	</c:forEach>

</table>



<br>
<span class="table_title_extract"><fmt:message key="CRF_data" bundle="${resword}"/></span>
<table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
 <tr>
    <th>&nbsp;</th>
    <th><fmt:message key="name" bundle="${resword}"/></th>            
    <th><fmt:message key="description" bundle="${resword}"/></th>   
    <th><fmt:message key="event" bundle="${resword}"/></th>   
    <th><fmt:message key="CRF" bundle="${resword}"/></th>   
    <th><fmt:message key="version2" bundle="${resword}"/></th>  
    <th><fmt:message key="data_type" bundle="${resword}"/></th>  
    <th><fmt:message key="units" bundle="${resword}"/></th>      
    <th><fmt:message key="response_label" bundle="${resword}"/></th>  
    <th><fmt:message key="PHI" bundle="${resword}"/></th>    
    
  </tr>	
<c:set var="count" value="0"/>
<c:forEach var='item' items='${allSelectedItems}'>  
  <tr>
   <td class="table_cell">   
   <c:choose>
    <c:when test="${item.selected}">
      <input type="checkbox" name="itemSelected<c:out value="${count}"/>" checked value="yes" disabled="disabled">
    </c:when>
    <c:otherwise>
      <input type="checkbox" name="itemSelected<c:out value="${count}"/>" value="yes" disabled="disabled">
    </c:otherwise>
   </c:choose>
   </td>
   <td class="table_cell">
       <a href="javascript: openDocWindow('ViewItemDetail?itemId=<c:out value='${item.id}'/>&itemName=<c:out value='${item.name}'/>')">
           <c:out value="${item.name}"/></a>
   </td>
   <td class="table_cell"><c:out value="${item.description}"/>&nbsp;</td>
   <td class="table_cell">
    <input type="hidden" name="itemDefName<c:out value="${count}"/>" value="<c:out value="${item.defName}"/>">
   <c:out value="${item.defName}"/>&nbsp;
   </td>
   <td class="table_cell">
   <input type="hidden" name="itemCrfName<c:out value="${count}"/>" value="<c:out value="${item.crfName}"/>">
   <c:out value="${item.crfName}"/>&nbsp;
   </td>
   <td class="table_cell">
      <c:forEach var="meta" items="${item.itemMetas}" varStatus="status">
        <c:choose>
          <c:when test="${status.last}">
           <c:out value="${meta.crfVersionName}"/>
          </c:when>
          <c:otherwise>
           <c:out value="${meta.crfVersionName}"/>,<br>
          </c:otherwise> 
        </c:choose> 
      </c:forEach>&nbsp;
    </td>
   <td class="table_cell"><c:out value="${item.dataType.name}"/>&nbsp;</td>    
   <td class="table_cell"><c:out value="${item.units}"/>&nbsp;</td>
  
    <td class="table_cell">
      <c:forEach var="meta" items="${item.itemMetas}" varStatus="status">
        <c:choose>
          <c:when test="${status.last}">
           <c:out value="${meta.responseSet.label}"/>
          </c:when>
          <c:otherwise>
            <c:out value="${meta.responseSet.label}"/>,<br>
          </c:otherwise> 
        </c:choose> 
      </c:forEach>&nbsp;
    </td>  
    <td class="table_cell">
     <c:choose>
      <c:when test="${item.phiStatus}">
        <fmt:message key="yes" bundle="${resword}"/>
      </c:when>
      <c:otherwise>
        <fmt:message key="no" bundle="${resword}"/>
      </c:otherwise>
    </c:choose>
   </td>  
   
  </tr>
  <c:set var="count" value="${count+1}"/>
</c:forEach>
</table>
