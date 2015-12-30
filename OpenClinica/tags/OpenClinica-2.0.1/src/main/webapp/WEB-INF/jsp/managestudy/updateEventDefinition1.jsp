<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
<jsp:useBean scope='session' id='eventDefinitionCRFs' class='java.util.ArrayList'/>

<h1><span class="title_manage">
Update Study Event Definition
</span></h1>
	<ol>
	<li> A <b>scheduled event</b> is one that is expected to occur for each subject as part of the ordinary progress of the study. </li>
	<li> An <b>unscheduled event</b> is not expected to occur, but may occur as circumstance dictates.</li>
	<li> Scheduled and unscheduled events typically occur at some particular time.</li>
	<li> A <b>common event</b> collects data forms, but is not expected to be associated with a particular time.</li>
	<li> The <b>Repeating flag</b> indicates that this type of study event can occur repeatedly within the containing study.</li>
	<li> The <b>Category attribute</b> is typically used to indicate the study phase appropriate to this type of study event. Examples might include Screening, PreTreatment, Treatment, and FollowUp. </li>
	</ol>
<P>* indicates required field.</P>

<form action="UpdateEventDefinition" method="post">
<input type="hidden" name="action" value="confirm">
 <div style="width: 600px">
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
   <div class="formfieldXL_BG"> <select name="type" class="formfieldXL">        
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
<br>
<div class="table_title_manage">CRFs</div>
 <div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr>
	<td class="table_cell" colspan="4" align="right">
    <a href="AddCRFToDefinition">Add a New CRF</a>
	</td>
  </tr> 
  <c:set var="count" value="0"/>
  <c:forEach var="edc" items="${eventDefinitionCRFs}"> 
  
  
   <input type="hidden" name="id<c:out value="${count}"/>" value="<c:out value="${edc.id}"/>">
   <input type="hidden" name="crfId<c:out value="${count}"/>" value="<c:out value="${edc.crfId}"/>"> 
   <c:set var="status" value="0"/>  
    <c:if test="${edc.status.id==1}"> <c:set var="status" value="1"/> </c:if> 
  
   <tr valign="top" bgcolor="#F5F5F5">              
    <td class="table_header_column" colspan="3"><c:out value="${edc.crfName}"/></td> 
    <td class="table_cell">
     <table border="0" cellpadding="0" cellspacing="0">
	    <tr>	     
        <c:choose>
          <c:when test="${status==1}">         
           <td><a href="RemoveCRFFromDefinition?id=<c:out value="${edc.crfId}"/>"
			onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img 
			name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="Remove" title="Remove" align="left" hspace="6"></a>
		 </td>          
         </c:when>
         <c:otherwise>
          <td><a href="RestoreCRFFromDefinition?id=<c:out value="${edc.crfId}"/>"
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
   <c:if test="${status==1}">  
   <tr valign="top">   
     
    <td class="table_cell">Required:
    <c:choose>
    <c:when test="${edc.requiredCRF == true}"> 
      <input type="checkbox" checked name="requiredCRF<c:out value="${count}"/>" value="yes">
    </c:when>
     <c:otherwise>
      <input type="checkbox" name="requiredCRF<c:out value="${count}"/>" value="yes">
     </c:otherwise>
    </c:choose>
   </td>
     
    <td class="table_cell">Double Data Entry:
     <c:choose>
      <c:when test="${edc.doubleEntry == true}">
       <input type="checkbox" checked name="doubleEntry<c:out value="${count}"/>" value="yes">
      </c:when>
      <c:otherwise>
        <input type="checkbox" name="doubleEntry<c:out value="${count}"/>" value="yes">
      </c:otherwise>
     </c:choose>
    </td>        
         
    <%--<td class="table_cell">Enforce Decision Conditions:
     <c:choose>
      <c:when test="${edc.decisionCondition == true}">
        <input type="checkbox" checked name="decisionCondition<c:out value="${count}"/>" value="yes">
       </c:when>
      <c:otherwise>
        <input type="checkbox" name="decisionCondition<c:out value="${count}"/>" value="yes">
      </c:otherwise>
     </c:choose>
   </td>--%>
  
   <td class="table_cell" colspan="2">Default Version:    
    <select name="defaultVersionId<c:out value="${count}"/>">
     <c:forEach var="version" items="${edc.versions}"> 
      <c:choose>
       <c:when test="${edc.defaultVersionId == version.id}">
        <option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>  
       </c:when>
       <c:otherwise>
         <option value="<c:out value="${version.id}"/>"><c:out value="${version.name}"/> 
       </c:otherwise>
      </c:choose>     
     </c:forEach>   
   </select>   
  </td></tr>   
  <tr valign="top">              
    <td class="table_cell" colspan="4"><a href="nullValue.htm" target="def_win" onClick="openDefWindow('nullValue.htm'); return false;">Null Values</a>:&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:openDocWindow('help/glossary.html')"><img src="images/bt_Help_Manage.gif" alt="Help" title="Help" border="0"></a><c:out value="${edc.nullValues}"/></td> 
   </tr>
   
 <c:forEach var="nv" items="${edc.nullFlags}"> 
  <c:if test="${nv.key == 'NI' && nv.value == '1'}"> 
   <c:set var="hasNI" value="1"/>
  </c:if>
   <c:if test="${nv.key == 'NA' && nv.value == '1'}"> 
   <c:set var="hasNA" value="1"/>
  </c:if>
   <c:if test="${nv.key == 'UNK' && nv.value == '1'}"> 
   <c:set var="hasUNK" value="1"/>
  </c:if>
   <c:if test="${nv.key == 'NASK' && nv.value == '1'}"> 
   <c:set var="hasNASK" value="1"/>
  </c:if>
   <c:if test="${nv.key == 'ASKU' && nv.value == '1'}"> 
   <c:set var="hasASKU" value="1"/>
  </c:if>
   <c:if test="${nv.key == 'NAV' && nv.value == '1'}"> 
   <c:set var="hasNAV" value="1"/>
  </c:if>
   <c:if test="${nv.key == 'OTH' && nv.value == '1'}"> 
   <c:set var="hasOTH" value="1"/>
  </c:if>
   <c:if test="${nv.key == 'PINF' && nv.value == '1'}"> 
   <c:set var="PINF" value="1"/>
  </c:if>
  <c:if test="${nv.key == 'NINF' && nv.value == '1'}"> 
   <c:set var="hasNINF" value="1"/>
  </c:if>
   <c:if test="${nv.key == 'MSK' && nv.value == '1'}"> 
   <c:set var="hasMSK" value="1"/>
  </c:if>
   <c:if test="${nv.key == 'NP' && nv.value == '1'}"> 
   <c:set var="hasNP" value="1"/>
  </c:if>  
 </c:forEach>
 
  
   <tr valign="top">
     
     <td class="table_cell">  
       <c:choose>
       <c:when test="${hasNI == 1}">
        NI<input type="checkbox" checked name="ni<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        NI<input type="checkbox" name="ni<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>     
   
   
    <td class="table_cell">  
       <c:choose>
       <c:when test="${hasNA == 1}">
        NA<input type="checkbox" checked name="na<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        NA<input type="checkbox" name="na<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>     
  

    <td class="table_cell">  
       <c:choose>
       <c:when test="${hasUNK == 1}">
        UNK<input type="checkbox" checked name="unk<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        UNK<input type="checkbox" name="unk<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>     
 
    <td class="table_cell">  
      <c:choose>
       <c:when test="${hasNASK == 1}">
        NASK<input type="checkbox" checked name="nask<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        NASK<input type="checkbox" name="nask<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>    
    </tr>
    <tr>   
  
    <td class="table_cell">  
      <c:choose>
       <c:when test="${hasASKU== 1}">
        ASKU<input type="checkbox" checked name="asku<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        ASKU<input type="checkbox" name="asku<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>     
  
  
    <td class="table_cell">  
       <c:choose>
       <c:when test="${hasNAV == 1}">
        NAV<input type="checkbox" checked name="nav<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        NAV<input type="checkbox" name="nav<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>      
 
    <td class="table_cell">  
      <c:choose>
       <c:when test="${hasOTH == 1}">
        OTH<input type="checkbox" checked name="oth<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        OTH<input type="checkbox" name="oth<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>      
 
    <td class="table_cell">  
       <c:choose>
       <c:when test="${hasPINF == 1}">
        PINF<input type="checkbox" checked name="pinf<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        PINF<input type="checkbox" name="pinf<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>     
 
    </tr>
    <tr>
    <td class="table_cell">  
     <c:choose>
       <c:when test="${hasNINF == 1}">
        NINF<input type="checkbox" checked name="ninf<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        NINF<input type="checkbox" name="ninf<c:out value="${count}"/>" value="yes">
      </c:otherwise>
     </c:choose>
    </td>     
 
    <td class="table_cell">  
       <c:choose>
       <c:when test="${hasMSK == 1}">
        MSK<input type="checkbox" checked name="msk<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        MSK<input type="checkbox" name="msk<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>     

    <td class="table_cell">  
       <c:choose>
       <c:when test="${hasNP == 1}">
        NP<input type="checkbox" checked name="np<c:out value="${count}"/>" value="yes">
       </c:when>
       <c:otherwise>
        NP<input type="checkbox" name="np<c:out value="${count}"/>" value="yes">
      </c:otherwise>
      </c:choose>
    </td>     

   </tr>  
    <tr><td class="table_divider" colspan="4">&nbsp;</td></tr>     
  </c:if> 
   <c:set var="count" value="${count+1}"/>
 </c:forEach> 
 
           



</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<input type="submit" name="Submit" value="Confirm" class="button_medium">
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
