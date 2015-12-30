<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
       
        A Study Event Definition describes a type of study event that takes place during a study. Please consult the <a href="http://www.cdisc.org/models/odm/v1.2/ODM1-2-0.html">Specification for the Operational Data Model (ODM)</a> for descriptions. 
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='definition' class='org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean'/>

<h1><span class="title_manage">
Create Study Event Definition for <c:out value="${study.name}"/>
</span></h1>
	<ol>
	<li> A <b>scheduled event</b> is one that is expected to occur for each subject as part of the ordinary progress of the study. </li>
	<li> An <b>unscheduled event</b> is not expected to occur, but may occur as circumstance dictates.</li>
	<li> Scheduled and unscheduled events typically occur at some particular time.</li>
	<li> A <b>common event</b> collects data forms, but is not expected to be associated with a particular time.</li>
	<li> The <b>Repeating flag</b> indicates that this type of study event can occur repeatedly within the containing study.</li>
	<li> The <b>Category attribute</b> is typically used to indicate the study phase appropriate to this type of study event. Examples might include Screening, PreTreatment, Treatment, and FollowUp. </li>
	</ol>
	<br>

* indicates required field.<br>
<form action="DefineStudyEvent" method="post">
<input type="hidden" name="action" value="next">
<input type="hidden" name="pageNum" value="1">
<div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">
  <tr valign="top"><td class="formlabel">Name:</td><td>  
  <div class="formfieldXL_BG"><input type="text" name="name" value="<c:out value="${definition.name}"/>" class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include>
  </td><td class="formlabel">*</td></tr>
  <tr valign="top"><td class="formlabel">Description:</td><td>  
  <div class="formtextareaXL4_BG">
  <textarea class="formtextareaXL4" name="description" rows="4" cols="50"><c:out value="${definition.description}"/></textarea>
  </div>  
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include>
  </td></tr>
 
 <tr valign="top"><td class="formlabel">Repeating:</td><td>
  <c:choose>
   <c:when test="${definition.repeating == true}">
    <input type="radio" checked name="repeating" value="1">Yes
    <input type="radio" name="repeating" value="0">No
   </c:when>
   <c:otherwise>
    <input type="radio" name="repeating" value="1">Yes
    <input type="radio" checked name="repeating" value="0">No
   </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="top"><td class="formlabel">Type:</td><td>
    <div class="formfieldXL_BG"><select name="type" class="formfieldXL">        
       <c:choose>
        <c:when test="${definition.type == 'common'}">          
         <option value="scheduled">scheduled
         <option value="unscheduled">unscheduled
         <option value="common" selected>common
        </c:when>        
        <c:when test="${definition.type == 'unscheduled'}">       
         <option value="scheduled">scheduled
         <option value="unscheduled" selected>unscheduled
         <option value="common">common
        </c:when>
        <c:otherwise>        
         <option value="scheduled" selected>scheduled
         <option value="unscheduled">unscheduled
         <option value="common">common
        </c:otherwise>
       </c:choose>       
    </select></div>
   </td></tr>
  
  <tr valign="top"><td class="formlabel">Category:</td><td>  
   <div class="formfieldXL_BG"><input type="text" name="category" value="<c:out value="${definition.category}"/>" class="formfieldXL"></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="category"/></jsp:include>
  </td></tr>
 

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
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

					<b>Workflow</b>

					</td>
				</tr>
			</table>
			</td>
			<td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif"></td>
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
	
							<span class="title_manage">			
						
							<b>Enter<br>Definition Name<br>& Description<br><br></b>
									
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_manage">
				             Add CRFs<br>to<br>Definition<br><br>
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>	
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_manage">
				             Edit Properties <br>for <br>Each CRF<br><br>
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>	
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_manage">
				             Confirm & Submit<br>Definition<br><br>
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
