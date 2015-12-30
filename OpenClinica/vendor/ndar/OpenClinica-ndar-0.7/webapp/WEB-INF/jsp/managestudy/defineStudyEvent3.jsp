<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/managestudy-header.jsp"/>
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

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='definition' class='org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean'/>
<h1><span class="title_manage">Define Study Event - selected CRF(s) - select default version</span></h1>


<form action="DefineStudyEvent" method="post">
<input type="hidden" name="action" value="confirm">
<div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <c:set var="count" value="0"/>
 
 <c:forEach var ="crf" items="${definition.crfs}">
   <input type="hidden" name="crfId<c:out value="${count}"/>" value="<c:out value="${crf.id}"/>"> 
   <input type="hidden" name="crfName<c:out value="${count}"/>" value="<c:out value="${crf.name}"/>">  
   
   <tr valign="top" bgcolor="#F5F5F5">       
    <td class="table_header_column" colspan="4"><c:out value="${crf.name}"/></td>      
  </tr>
    
   <tr valign="top">   
     
   <td class="table_cell">Required:<input type="checkbox" checked name="requiredCRF<c:out value="${count}"/>" value="yes"></td>
     
   <td class="table_cell">Double Data Entry:<input type="checkbox" name="doubleEntry<c:out value="${count}"/>" value="yes"></td>       
            
  <%-- <td class="table_cell">Enforce Decision Conditions:<input type="checkbox" name="decisionCondition<c:out value="${count}"/>"  checked value="yes"></td>--%>
  
  <td class="table_cell" colspan="2">Default Version:
    
    <select name="defaultVersionId<c:out value="${count}"/>">
     <c:forEach var="version" items="${crf.versions}"> 
      <option value="<c:out value="${version.id}"/>"><c:out value="${version.name}"/>       
     </c:forEach>   
   </select>
  </td></tr>     
  <br/>
  <tr valign="top">       
    <td class="table_header_column" colspan="4">Choose Null Values (<a href="nullValue.htm" target="def_win" onClick="openDefWindow('nullValue.htm'); return false;">What is Null Value?</a>)</td>      
  </tr>        
  
  <tr valign="top">   
     
   <td class="table_cell">NI<input type="checkbox" name="ni<c:out value="${count}"/>" value="yes"></td>
     
   <td class="table_cell">NA<input type="checkbox" name="na<c:out value="${count}"/>" value="yes"></td>       
            
   <td class="table_cell">UNK<input type="checkbox" name="unk<c:out value="${count}"/>" value="yes"></td>
  
   <td class="table_cell">NASK<input type="checkbox" name="nask<c:out value="${count}"/>" value="yes"></td>
   </tr>
   <tr valign="top">   
     
   <td class="table_cell">ASKU<input type="checkbox" name="asku<c:out value="${count}"/>" value="yes"></td>
     
   <td class="table_cell">NAV<input type="checkbox" name="nav<c:out value="${count}"/>" value="yes"></td>       
            
   <td class="table_cell">OTH<input type="checkbox" name="oth<c:out value="${count}"/>" value="yes"></td>
  
   <td class="table_cell">PINF<input type="checkbox" name="pinf<c:out value="${count}"/>" value="yes"></td>
   </tr>
   <tr valign="top">   
     
   <td class="table_cell">NINF<input type="checkbox" name="ninf<c:out value="${count}"/>" value="yes"></td>
     
   <td class="table_cell">MSK<input type="checkbox" name="msk<c:out value="${count}"/>" value="yes"></td>       
            
   <td class="table_cell">NP<input type="checkbox" name="np<c:out value="${count}"/>" value="yes"></td>
  
   <td class="table_cell">&nbsp;</td>
   </tr>
  <c:set var="count" value="${count+1}"/>
   <tr><td class="table_divider" colspan="4">&nbsp;</td></tr>
 </c:forEach>
 
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
	
							<span class="title_manage">			
						
							Enter<br>Definition Name<br>& Description<br><br>
									
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
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
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_manage">
				             <b>Edit Properties <br>for <br>Each CRF<br><br></b>
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>	
						<td><img src="images/arrow.gif" alt="==>" title=""></td>
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
