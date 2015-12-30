<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/admin-header.jsp"/>
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


<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope ="request" id="interPurposeMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="allocationMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="maskingMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="controlMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="assignmentMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="endpointMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="interTypeMap" class="java.util.HashMap"/>
<jsp:useBean scope ="session" id="interventions" class="java.util.ArrayList"/>
<jsp:useBean scope ="request" id="interventionError" class="java.lang.String"/>
<h1><span class="title_Admin">
Update Study Details,Continue...
</span></h1>
<span class="title_Admin"><p><b>SECTION B: Study Status and Design- Design Details - Interventional</b></p></span>
<P>* indicates required field.</P>
<form action="UpdateStudy" method="post">
<input type="hidden" name="action" value="next">
<input type="hidden" name="pageNum" value="3">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0">
  <tr valign="top"><td class="formlabel">Purpose:</td><td>
  <c:set var="purpose1" value="${newStudy.purpose}"/> 
  <div class="formfieldXL_BG"><select name="purpose" class="formfieldXL">
   <c:forEach var="purpose" items="${interPurposeMap}">    
       <c:choose>
        <c:when test="${purpose1 == purpose.key}">   
         <option value="<c:out value="${purpose.key}"/>" selected><c:out value="${purpose.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${purpose.key}"/>"><c:out value="${purpose.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="purpose"/></jsp:include></td></tr>
  
  <tr valign="top"><td class="formlabel">Allocation:</td><td>
   <c:set var="allocation1" value="${newStudy.allocation}"/> 
  <div class="formfieldXL_BG"><select name="allocation" class="formfieldXL">
   <option value="">-Select-</option>
   <c:forEach var="allocation" items="${allocationMap}">    
       <c:choose>
        <c:when test="${allocation1 == allocation.key}">   
         <option value="<c:out value="${allocation.key}"/>" selected><c:out value="${allocation.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${allocation.key}"/>"><c:out value="${allocation.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select></div>
  </td></tr>
  
  <tr valign="top"><td class="formlabel"><a href="definitions.html#PrimaryId" target="def_win" onClick="openDefWindow('definitions.html#PrimaryId'); return false;">Masking:</td><td>
  <c:set var="masking1" value="${newStudy.masking}"/>
  <div class="formfieldXL_BG"><select name="masking" class="formfieldXL">
   <option value="">-Select-</option>
   <c:forEach var="masking" items="${maskingMap}">    
       <c:choose>
        <c:when test="${masking1 == masking.key}">   
         <option value="<c:out value="${masking.key}"/>" selected><c:out value="${masking.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${masking.key}"/>"><c:out value="${masking.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select></div>
  </td></tr>
  
  <tr valign="top"><td class="formlabel">Control:</td><td>
   <c:set var="control1" value="${newStudy.control}"/>
   <div class="formfieldXL_BG"><select name="control" class="formfieldXL">
   <option value="">-Select-</option>
    <c:forEach var="control" items="${controlMap}">    
       <c:choose>
        <c:when test="${control1 == control.key}">   
         <option value="<c:out value="${control.key}"/>" selected><c:out value="${control.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${control.key}"/>"><c:out value="${control.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select></div>
 </td></tr>  
   
  <tr valign="top"><td class="formlabel">Assignment:</td><td>
  <c:set var="assignment1" value="${newStudy.assignment}"/>
   <div class="formfieldXL_BG"><select name="assignment" class="formfieldXL">
   <option value="">-Select-</option>
    <c:forEach var="assignment" items="${assignmentMap}">    
       <c:choose>
        <c:when test="${assignment1 == assignment.key}">   
         <option value="<c:out value="${assignment.key}"/>" selected><c:out value="${assignment.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${assignment.key}"/>"><c:out value="${assignment.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select></div>
  </td></tr> 
  
  <tr valign="top"><td class="formlabel">Endpoint:</td><td>
   <c:set var="endpoint1" value="${newStudy.endpoint}"/>
   <div class="formfieldXL_BG"><select name="endpoint" class="formfieldXL">
   <option value="">-Select-</option>
    <c:forEach var="endpoint" items="${endpointMap}">    
       <c:choose>
        <c:when test="${endpoint1 == endpoint.key}">   
         <option value="<c:out value="${endpoint.key}"/>" selected><c:out value="${endpoint.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${endpoint.key}"/>"><c:out value="${endpoint.value}"/>      
        </c:otherwise>
       </c:choose> 
    </c:forEach>   
  </select></div>
  </td></tr>
  
  <tr valign="top"><td class="formlabel">Intervention(s):<br> (one name per line)</td><td>
   <c:set var="count" value="0"/>  
   <c:forEach var ="intervention" items="${interventions}">
     Type :
     <c:set var="type1" value="${intervention.type}"/>
      <select name="interType<c:out value="${count}"/>">
       <option value="">-Select-</option>
       <c:forEach var="type" items="${interTypeMap}">    
        <c:choose>
        <c:when test="${type1 == type.key}">   
         <option value="<c:out value="${type.key}"/>" selected><c:out value="${type.value}"/>
        </c:when>
        <c:otherwise>
         <option value="<c:out value="${type.key}"/>"><c:out value="${type.value}"/>      
        </c:otherwise>
        </c:choose> 
       </c:forEach>   
      </select>     
     Name: <input type="text" name="interName<c:out value="${count}"/>" value="<c:out value="${intervention.name}"/>" size="30">
      <br>  
     <c:set var="count" value="${count+1}"/>
   </c:forEach>
   <c:if test="${count < 9}">
    <c:forEach begin="${count}" end="9">
     Type:
      <select name="interType<c:out value="${count}"/>">
        <option value="">-Select-</option>
        <c:forEach var="type" items="${interTypeMap}">         
         <option value="<c:out value="${type.key}"/>"><c:out value="${type.value}"/>      
        </c:forEach>   
      </select> &nbsp;   
     Name: <input type="text" name="interName<c:out value="${count}"/>" value="" size="30"><br>
    <c:set var="count" value="${count+1}"/>
    </c:forEach>
   </c:if>
    <br>
   <span class="alert"><c:out value="${interventionError}"/></span>  


</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<input type="submit" name="Submit" value="Continue" class="button_medium"> 
</form>
<br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
	<tr>
		<td id="sidebar_Workflow_closed" style="display: none">
		<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/tab_Workflow_closed.gif" border="0"></a>
	</td>
	<td id="sidebar_Workflow_open" style="display: all">
	<table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
		<tr>
			<td class="workflowBox_T" valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="workflow_tab">
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

					<b>Workflow</b>

					</td>
				</tr>
			</table>
			</td>
			<td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif" alt=""></td>
		</tr>
		<tr>
			<td colspan="2" class="workflowbox_B">
			<div class="box_R"><div class="box_B"><div class="box_BR">
				<div class="workflowBox_center">


		<!-- Workflow items -->

				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
							<div class="textbox_center" align="center">
	
							<span class="title_admin">					
						
							Study<br>Title &<br>Description<br><br>
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							<b>Status<br>&<br>Design</b>
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					        Conditions<br> &<br>Eligibility<br><br>
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							 Specify <br>Facility
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							 Other<br>Related<br>Information
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				        <!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					         Study Parameter<br> Configuration										
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">
				
					
							 Confirm & Submit<br>Study
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						
					</tr>
				</table>


		<!-- end Workflow items -->

				</div>
			</div></div></div>
			</td>
		</tr>
	</table>			
	</td>
   </tr>
</table>

<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
