<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%--<jsp:useBean scope="session" id="panel" class="org.akaza.openclinica.view.StudyInfoPanel" />--%>


<!-- Sidebar Contents after alert-->

	
<c:choose>
 <c:when test="${userBean != null && userBean.id>0}">	
	<tr id="sidebar_Info_open">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Info_open'); leftnavExpand('sidebar_Info_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Info</b>

		<div class="sidebar_tab_content">

 <%-- begin standard study info --%>
			<span style="color: #789EC5">
 <%--	<c:if test="${panel.studyInfoShown}">--%>
	<b>Study:</b>&nbsp;  
	<c:choose>
	<c:when test="${study.parentStudyId>0}">
	 <a href="ViewSite?id=<c:out value="${study.id}"/>">
	</c:when>
	<c:otherwise>
	 <a href="ViewStudy?id=<c:out value="${study.id}"/>&viewFull=yes">
	</c:otherwise>
	</c:choose>
	<c:out value="${study.name}"/></a>

	<br><br>	
	
	<b>Subject:</b>&nbsp; 
	 <c:out value="${studySubject.label}"/>
	<br><br>

	<b>Study Event</b>: &nbsp;
	<c:choose>
	 <c:when test="${toc != null}">	 
	  <a href="EnterDataForStudyEvent?eventId=<c:out value="${toc.studyEvent.id}"/>"><c:out value="${toc.studyEventDefinition.name}"/></a>
	 </c:when>
	 <c:otherwise>
	  <c:out value="${studyEvent.studyEventDefinition.name}"/>	   
	 </c:otherwise>
	</c:choose>
	<br><br>
	
     <b>Location</b>: 
     <c:choose>
	 <c:when test="${toc != null}">	 
	   <c:out value="${toc.studyEvent.location}"/>
	 </c:when>
	 <c:otherwise>
	  <c:out value="${studyEvent.location}"/>	   
	 </c:otherwise>
	</c:choose>    
     <br><br>
     
     
     <b>Start Date</b>:
     <c:choose>
	 <c:when test="${toc != null}">	 
	    <fmt:formatDate value="${toc.studyEvent.dateStarted}" pattern="MM/dd/yyyy"/> 
   	 </c:when>
	 <c:otherwise>
	  <fmt:formatDate value="${studyEvent.dateStarted}" pattern="MM/dd/yyyy"/>    
	 </c:otherwise>
	 </c:choose>
      <br><br>
      
    
	<c:if test="${toc != null}">	 
     <b>CRF</b>: 
      <a href="ViewCRF?crfId=<c:out value="${toc.crf.id}"/>"> <c:out value="${toc.crf.name}"/> <c:out value="${toc.crfVersion.name}"/> </a>
      <br><br>
    </c:if>  
    
  <%-- </c:if>--%>
 			</span>
 <script language="JavaScript" type="text/javascript">
       <!--
         function leftnavExpand(strLeftNavRowElementName){

	       var objLeftNavRowElement;

           objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
           if (objLeftNavRowElement != null) {
             if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; } 
	           objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";		
	         }
           }

       //-->
     </script>  
     
   	</div>

	</td>
	</tr>
	<tr id="sidebar_Info_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Info_open'); leftnavExpand('sidebar_Info_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Info</b>

		</td>
	</tr>
   
  
  
  <c:if test="${panel.submitDataModule}">    
  <tr id="sidebar_StudyEvents_open">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_StudyEvents_open'); leftnavExpand('sidebar_StudyEvents_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>
		<b>Study Events</b>

		<div class="sidebar_tab_content">  
           <c:import url="../include/submitDataSide.jsp"/>	
     	</div>

		</td>
	</tr>
	
	<tr id="sidebar_StudyEvents_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_StudyEvents_open'); leftnavExpand('sidebar_StudyEvents_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>StudyEvents</b>

		</td>
	</tr>
  </c:if> 
  <c:if test="${panel.iconInfoShown}">
	 <c:import url="../include/sideIconsDataEntry.jsp"/>
	</c:if>
</table>	
 	
</c:when>
<c:otherwise>
    <br><br>
	<a href="MainMenu">Login</a>	
	<br><br>
	<!-- <a href="RequestAccount">Request an Account</a> -->
	<br><br>
	<!-- <a href="RequestPassword">Forgot Password?</a> -->
</c:otherwise>
</c:choose>


<!-- End Sidebar Contents -->

				<br><img src="images/spacer.gif" width="120" height="1" alt="">

				</td>
				<td class="content" valign="top">

