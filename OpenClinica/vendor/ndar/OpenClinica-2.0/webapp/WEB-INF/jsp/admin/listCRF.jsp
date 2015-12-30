<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:choose>
<c:when test="${userBean.sysAdmin}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-header.jsp"/>
</c:otherwise> 
</c:choose>
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
        The Case Report Form (CRF) Library shows all CRFs available for use in studies. CRFs may have one or more versions, each with its own version name. You may add any existing CRF to a Study Event Definition in your study. You may also create a new OpenClinica CRF or CRF version using the Excel template provided below and uploading it to the system.

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
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.core.EntityBeanTable'/>
<c:choose>
<c:when test="${userBean.sysAdmin}">
 <h1><span class="title_Admin">Administer Case Report Forms (CRFs) <a href="javascript:openDocWindow('help/5_4_administerCRF_Help.html')"><img src="images/bt_Help_Admin.gif" border="0" alt="Help" title="Help"></a>
</c:when>
<c:otherwise>
 <h1><span class="title_manage">Manage Case Report Forms (CRFs) <a href="javascript:openDocWindow('help/4_6_viewCRF_Help.html')"><img src="images/bt_Help_Admin.gif" border="0" alt="Help" title="Help"></a>
</c:otherwise> 
</c:choose>
</span></h1>


<p>You can download a blank OpenClinica CRF Excel spreadsheet template <a href="DownloadVersionSpreadSheet?template=1"><b>here</b></a>.</p>
<p>You may also download a set of example CRFs and instructions from the OpenClinica.org portal (OpenClinica.org user account required) <a href="http://www.openclinica.org/entities/entity_details.php?eid=151" target="_blank">here</a>.</p>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showCRFRow.jsp" /></c:import>
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
	
							<c:choose>
                             <c:when test="${userBean.sysAdmin}">
                               <span class="title_admin">						
							     <a href="AdminSystem">Business Admin</a>
                             </c:when>
                             <c:otherwise>
							    <span class="title_manage">							
							    <a href="ManageStudy">Manage Study</a>
					        </c:otherwise>
					        </c:choose>
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							 <c:choose>
                             <c:when test="${userBean.sysAdmin}">
                               <span class="title_admin">						
							     <b>Administer CRFs</b>
                             </c:when>
                             <c:otherwise>
							    <span class="title_manage">			
					
							     <b>Manage CRFs</b>
					        </c:otherwise>
					        </c:choose>
							
							
					
				
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
