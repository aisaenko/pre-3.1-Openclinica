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
<jsp:useBean scope='request' id='eventDefinitionCRFs' class='java.util.ArrayList'/>
<h1><span class="title_manage">Confirm Event Definition Creation</span></h1>

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
						Edit Properties <br>for <br>Each CRF<br><br>
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
						<b>Confirm & Submit<br>Definition<br><br></b>
						</span>

						</div>

					</div></div></div></div></div></div></div></div>

				</td>				
			
				
				<td></td>
			</tr>
		</table>

		<br>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column">Name:</td><td class="table_cell">  
  <c:out value="${definition.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">Description:</td><td class="table_cell">  
  <c:out value="${definition.description}"/>&nbsp;
  </td></tr>
 
 <tr valign="top"><td class="table_header_column">Repeating:</td><td class="table_cell">
  <c:choose>
   <c:when test="${definition.repeating == true}"> Yes </c:when>
   <c:otherwise> No </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column">Type:</td><td class="table_cell">
    <c:out value="${definition.type}"/>
   </td></tr>
  
  <tr valign="top"><td class="table_header_column">Category:</td><td class="table_cell">  
  <c:out value="${definition.category}"/>&nbsp;
  </td></tr>
  </table>
 </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 
 <span class="table_title_manage">CRFs</span>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">               
    <td class="table_header_row_left">Name</td>   
    <td class="table_header_row">Required</td>     
    <td class="table_header_row">Double Data Entry</td>         
    <!-- <td class="table_header_row">Enforce Decision Conditions</td>-->  
    <td class="table_header_row">Default Version</td>
    <td class="table_header_row">Null Values</td>   
  </tr>   
 <c:forEach var ="crf" items="${eventDefinitionCRFs}">   
   <tr>            
    <td class="table_cell_left"><c:out value="${crf.crfName}"/></td>      
    <td class="table_cell">
    <c:choose>
    <c:when test="${crf.requiredCRF == true}"> Yes </c:when>
     <c:otherwise> No </c:otherwise>
    </c:choose>
   </td>
     
    <td class="table_cell">
     <c:choose>
      <c:when test="${crf.doubleEntry == true}"> Yes </c:when>
      <c:otherwise> No </c:otherwise>
     </c:choose>
    </td>       
   
         
    <%--<td class="table_cell">
     <c:choose>
      <c:when test="${crf.decisionCondition == true}"> Yes </c:when>
      <c:otherwise> No </c:otherwise>
     </c:choose>
   </td>--%>
  
   <td class="table_cell">  
    <c:out value="${crf.defaultVersionName}"/>     
  </td>   
  
   <td class="table_cell">   
    <c:out value="${crf.nullValues}"/>&nbsp;     
  </td>  
  </tr>            
   
 </c:forEach>
 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>   
    <td><form action="DefineStudyEvent" method="POST">
         <input type="hidden" name="action" value="submit"> 
         <input type="hidden" name="nextAction" value="1"> 
         <input type="submit" name="submit" value="Cancel" class="button_medium">
        </form>
    </td>
    <td><form action="DefineStudyEvent" method="POST">
         <input type="hidden" name="action" value="submit"> 
         <input type="hidden" name="nextAction" value="2"> 
         <input type="submit" name="submit" value="Confirm and Finish" class="button_long">
        </form>
    </td>
    <td><form action="DefineStudyEvent" method="POST">
         <input type="hidden" name="action" value="submit"> 
         <input type="hidden" name="nextAction" value="3"> 
         <input type="submit" name="submit" value="Confirm and Create Another Definition" class="button_xlong">
        </form>
    </td>
    </tr>  
   </table>
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
				             Edit Properties <br>for <br>Each CRF<br><br>
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
				             <b>Confirm & Submit<br>Definition</b>
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
