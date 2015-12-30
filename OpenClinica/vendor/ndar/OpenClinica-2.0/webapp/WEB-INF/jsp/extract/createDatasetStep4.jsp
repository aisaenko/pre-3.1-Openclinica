<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>


<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>

<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='statuses' class='java.util.ArrayList' />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="dsName" value="" />
<c:set var="dsDesc" value="" />
<c:set var="dsStatusId" value="${0}" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "dsName"}'>
		<c:set var="dsName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dsDesc"}'>
		<c:set var="dsDesc" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dsStatusId"}'>
		<c:set var="dsStatusId" value="${presetValue.value}" />
	</c:if>
</c:forEach>



<h1><span class="title_extract">Create Dataset: Specify Dataset Properties <a href="javascript:openDocWindow('help/3_2_createDataset_Help.html#step4')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>

<%--
<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="specifyMetadata" value="1"/>
</jsp:include>
--%>

<!--
<p>Please enter the dataset properties in the fields below.  Be descriptive.  <font color="red">All fields are required.</font></p>
-->
<p>Please enter the dataset properties in the fields below.  Be descriptive.  <font color="red">Name and Description fields are required.</font></p>


<form action="CreateDataset" method="post">
<input type="hidden" name="action" value="specifysubmit"/>

<table>
	<tr>
		<td>Name:</td>
		<td><input type="text" name="dsName" size="30" value="<c:out value='${dsName}' />"/>
		<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dsName"/></jsp:include>
		</td>
	</tr>
	<tr>
		<td>Description:</td>
		<td><textarea name="dsDesc" cols="40" rows="4"><c:out value="${dsDesc}" /></textarea>
		<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dsDesc"/></jsp:include>
		</td>
	</tr>

	<tr>
	<td colspan="2">
	<br><br><br><br>	    
 Meanwhile, if you choose to output your data in CDISC ODM XML format, you may like to set some properties for your CDISC ODM XML data file. Currently, 
the available properties include MetaDataVersion ODM ID, MetaDataVersion Name, Previous Study ODM ID and Previous MetaDataVersion ODM ID which 
you like to include in your ODM output file.  The inputs for CDISC ODM XML format are optional. If you leave them empty, default value will be used, that is,
MetaDataVersion ODM ID="v1.0.0", MetaDataVersion Name="Version 1.0.0", and no include in this file. If you input previous study ODM ID, you have to input
previous MetaDataVersion ODM ID, otherwise no include in you ODM output file. If you only input previous MetaDataVersion ODM ID, then current study ODM ID will be 
include in your ODM output file. 
	<br><br>	
	</td>
	<br><br>
	</tr>
	
	<tr>
		<td>MetaDataVersion ODM ID:          </td>
		<td>
		<input type="text" name="mdvOID" size="25" value="<c:out value='${mdvOID}' />"/>
		<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvOID"/></jsp:include>
		</td>
	</tr>
	<tr>
		<td>MetaDataVersion Name:            </td>
		<td>
		<input type="text" name="mdvName" size="25" value="<c:out value='${mdvName}' />"/>
		<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvName"/></jsp:include>
		</td>
	</tr>
	<tr>
		<td>Previous Study ODM ID:            </td>
		<td>
		<input type="text" name="mdvPrevStudy" size="25" value="<c:out value='${mdvPrevStudy}' />"/>
		<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvPrevStudy"/></jsp:include>
		</td>
	</tr>
	<tr>
		<td>Previous MetaDataVersion ODM ID:   </td>
		<td>
		<input type="text" name="mdvPrevOID" size="25" value="<c:out value='${mdvPrevOID}' />"/>
		<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvPrevOID"/></jsp:include>
		</td>
	</tr>

	<%--<tr>
		<td>Status:</td>
		<td><i>Set to Active</i>--%>
		<%--
			<select name="dsStatus">
				<option value="0">-- Select Status --</option>
			<c:forEach var="status" items="${statuses}">
				<c:choose>
					<c:when test="${dsStatusId == status.id}">
						<option value="<c:out value='${status.id}' />" selected><c:out value="${status.name}" /></option>
					</c:when>
					<c:otherwise>
						<option value="<c:out value='${status.id}' />"><c:out value="${status.name}" /></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			</select>
			--%>
			
		<%--</td>
	</tr>--%>
	<tr>
		<td class="text" colspan="2" align="left">
		<input type="hidden" name="dsStatus" value="1"/>
			<input type="submit" name="remove" value="Proceed to Confirm and Save" class="button_xlong"/>		
		</td>
	</tr>
</table>
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
	
							<span class="title_extract">
				
					
						 Select Items<br> or<br> Event/Subject Attributes
					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_extract">
				
					       Define<br> Temporal<br> Scope
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_extract">
				
					        <b>Specify<br> Dataset<br> Properties</b>
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_extract">
				
					       	Save<br> And<br> Export
				
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