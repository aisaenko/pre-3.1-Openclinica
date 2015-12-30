<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="annotations" class="java.lang.String" />

<script language="JavaScript" type="text/javascript">
<!--
function checkSectionStatus() {  
    
    objImage=document.getElementById('status_top');
    //alert(objImage.src);
    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) { 
       return confirm('You have unsaved data. Are you sure you want to go to another section without saving?');
    }   
     
    return true;
}


function checkEntryStatus(strImageName) {
   objImage = MM_findObj(strImageName);
   //alert(objImage.src);
    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) { 
       return confirm('You have unsaved data. Are you sure you want to exit without saving?');
    } 
    return true;
}
//-->
</script>


<c:set var="stage" value="${param.stage}"/>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<div style="width:100%">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" style="display: all">
<!--
	<tr>
		<td>Section:</td>
		<td>
			<b>
			<c:if test="${section.section.parent.active}">
				<c:out value="${section.section.parent.title}" escapeXml="false"/> &gt;
			</c:if>
			<c:out value="${section.section.title}" escapeXml="false"/>
			</b>
		</td>
	</tr>
	<c:if test='${section.section.subtitle != ""}'>
	<tr>
		<td>&nbsp;Subtitle:</td>
		<td> <c:out value="${section.section.subtitle}" escapeXml="false"/> </td>
	</tr>
	</c:if>
	<c:if test='${section.section.instructions != ""}'>
	<tr>
		<td>&nbsp;Instructions:</td>
		<td> <c:out value="${section.section.instructions}" escapeXml="false"/> </td>
	</tr>
	</c:if>
  -->
	<!--
	<tr>
		<td colspan="2">
			<a name="pages">Pages:</a>
			<c:set var="currPage" value="" />
			<c:forEach var="displayItem" items="${section.items}">
				<c:if test="${currPage != displayItem.metadata.pageNumberLabel}">
					<a href="#item<c:out value="${displayItem.item.id}"/>"><c:out value="${displayItem.metadata.pageNumberLabel}" escapeXml="false"/></a>
					<c:set var="currPage" value="${displayItem.metadata.pageNumberLabel}" />
				</c:if>
			</c:forEach>
		</td>
	</tr>
	-->
</table>

<c:set var="currPage" value="" />
<c:set var="curCategory" value="" />


<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" style="display: all">
<c:set var="displayItemNum" value="${0}" />
<c:set var="itemNum" value="${0}" />
<c:set var="numOfTr" value="0"/>
<c:set var="numOfDate" value="1"/>
<c:if test='${section.section.title != ""}'>
	<tr>
		<td class="table_cell_left"><b>Title:&nbsp;<c:out value="${section.section.title}" escapeXml="false"/></b> </td>
	</tr>
 </c:if>
 <c:if test='${section.section.subtitle != ""}'>
	<tr>
		<td class="table_cell_left">Subtitle:&nbsp;<c:out value="${section.section.subtitle}" escapeXml="false"/> </td>
	</tr>
 </c:if>
  <c:if test='${section.section.instructions != ""}'>
	<tr>
		<td class="table_cell_left">Instructions:&nbsp;<c:out value="${section.section.instructions}" escapeXml="false"/> </td>
	</tr>
  </c:if>
<c:forEach var="displayItem" items="${section.items}" varStatus="itemStatus">
    <c:if test="${displayItemNum ==0}">
    <!-- always show the button and page above the first item--> 
    <!-- to handle the case of no pageNumLabel for all the items-->   
	<tr>
		<td class="table_cell_top" column span="2">
			<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 6px;">
			 <tr>				
		        
				<td class="table_cell_left" valign="bottom" nowrap style="padding-right: 50px">
					
					<%--
					 <a name="item<c:out value="${displayItem.item.id}"/>">
				     Page: <c:out value="${displayItem.metadata.pageNumberLabel}" escapeXml="false"/>
			        </a>	
			        --%>
					<a name="top">Page: <c:out value="${displayItem.metadata.pageNumberLabel}" escapeXml="false"/></a>
				</td>
				<td align="right" valign="bottom">
				  <table border="0" cellpadding="0" cellspacing="0">
							<tr>
		<c:choose>
								<c:when test="${stage !='adminEdit' && section.lastSection}">
								<td valign="bottom"><input type="checkbox" name="markComplete" value="Yes"
								onClick='return confirm("Marking this CRF complete will finalize data entry. You will no longer be able to add or modify data unless the CRF is reset by an administrator. If Double Data Entry is required, you or another user may need to complete this CRF again before it is verified as complete. Are you sure you want to mark this CRF complete?");'>
								</td>
								<td valign="bottom" nowrap>&nbsp; Mark Complete &nbsp;&nbsp;&nbsp;</td>
		</c:when>
		<c:otherwise>
								 <td colspan="2">&nbsp;</td>
		</c:otherwise>
		</c:choose>
								<td valign="bottom"><input type="checkbox" name="runScoring" value="Yes"
								onClick='return confirm("Scoring this CRF may result in fees due per scoring execution to the form publisher.  Are you sure you want to score this CRF now?");'>
								</td>
								<td valign="bottom" nowrap>&nbsp; Score Now &nbsp;&nbsp;&nbsp;</td>
								<%--
								<c:if test="${!section.firstSection}">
								  <td><input type="submit" name="submittedPrev" value="Back" class="button_medium"  onClick="return confirm('Would you like to save before continuing to another section?');"/></td>
							   </c:if>	
							   --%>
								<td><input type="submit" name="submittedResume" value="Save" class="button_medium" /></td>
								<%--
								<c:choose>
								 <c:when test="${!section.lastSection}">
								  <td><input type="submit" name="submittedNext" value="Next" class="button_medium" onClick="return confirm('Would you like to save before continuing to another section?');"/></td>
								 </c:when>
								 <c:otherwise>
								  <td>&nbsp;</td>
								 </c:otherwise>
								</c:choose>
								--%>
								<td><input type="submit" name="submittedExit" value="Exit" class="button_medium" onClick="return checkEntryStatus('DataStatus_top');" /></td>
									
								<td valign="bottom"><img name="DataStatus_top" id="status_top" src="images/icon_UnchangedData.gif"></td>
							
							</tr>
				  </table>
	           </td>	
			   </tr>
		   </table>
		</td>
	</tr>
    </c:if>
	<c:if test="${currPage != displayItem.metadata.pageNumberLabel && displayItemNum >0}">
	<!-- show page number and buttons -->	
	<tr>
		<td class="table_cell_top" column span="2">
			<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 6px;">
			 <tr>
				
				<td class="table_cell_left" valign="bottom" nowrap style="padding-right: 50px">
					
			<a name="item<c:out value="${displayItem.item.id}"/>">
				Page: <c:out value="${displayItem.metadata.pageNumberLabel}" escapeXml="false"/>
			</a>			
		</td>
				<td align="right" valign="bottom">
				  <table border="0" cellpadding="0" cellspacing="0">
							<tr>
							  <c:choose>
								<c:when test="${stage !='adminEdit' && section.lastSection}">
								<td valign="bottom"><input type="checkbox" name="markComplete" value="Yes" 
								onClick='return confirm("Marking this CRF complete will finalize data entry. You will no longer be able to add or modify data unless the CRF is reset by an administrator. If Double Data Entry is required, you or another user may need to complete this CRF again before it is verified as complete. Are you sure you want to mark this CRF complete?");'>
								</td>
								<td valign="bottom" nowrap>&nbsp; Mark Complete &nbsp;&nbsp;&nbsp;</td>
								</c:when>
								<c:otherwise>
								 <td colspan="2">&nbsp;</td>
								</c:otherwise>
							  </c:choose>	
								<td valign="bottom"><input type="checkbox" name="runScoring" value="Yes"
								onClick='return confirm("Scoring this CRF may result in fees due per scoring execution to the form publisher.  Failure to score will erase any previously recorded scores.  Are you sure you want to score this CRF now?");'>
								</td>
								<td valign="bottom" nowrap>&nbsp; Score Now &nbsp;&nbsp;&nbsp;</td>
							  <%--	
							   <c:if test="${!section.firstSection}">
								  <td><input type="submit" name="submittedPrev" value="Back" class="button_medium"  onClick="return confirm('Would you like to save before continuing to another section?');"/></td>
	</c:if>
								--%>
								<td><input type="submit" name="submittedResume" value="Save" class="button_medium" /></td>

							<%--	
								<c:choose>
								 <c:when test="${!section.lastSection}">
								  <td><input type="submit" name="submittedNext" value="Next" class="button_medium" onClick="return confirm('Would you like to save before continuing to another section?');"/></td>
								 </c:when>
								 <c:otherwise>
								  <td>&nbsp;</td>
								 </c:otherwise>
								</c:choose>
								
								  --%>
								<td><input type="submit" name="submittedExit" value="Exit" class="button_medium" onClick="return checkEntryStatus('DataStatus_top');" /></td>
						
								<%--<td valign="bottom"><img name="DataStatus_top" src="images/icon_UnchangedData.gif"></td>--%>
	      </tr>
				  </table>
	           </td>	
	    </tr>
		   </table>
		</td>
	</tr>
	<!-- end of page number and buttons-->	
	
	  </c:if>
	
	 <c:set var="currPage" value="${displayItem.metadata.pageNumberLabel}" />

	<%-- SHOW THE PARENT FIRST --%>
	<c:if test="${displayItem.metadata.parentId == 0}">	  
	
	<!--ACCORDING TO COLUMN NUMBER, ARRANGE QUESTIONS IN THE SAME LINE-->	
		
	<c:if test="${displayItem.metadata.columnNumber <=1}">
	  <c:if test="${numOfTr > 0 }">
	        </tr>
           </table>
         </td>
	    
	   </tr>
	  
	  </c:if>
	  <c:set var="numOfTr" value="${numOfTr+1}"/>	 
	    <c:if test="${!empty displayItem.metadata.header}">
	    <tr>	 
		    <td class="table_cell_left" bgcolor="#F5F5F5"><b><c:out value="${displayItem.metadata.header}" escapeXml="false"/></b></td>
	      </tr>
	   </c:if>
	   <c:if test="${!empty displayItem.metadata.subHeader}">
	    <tr>
		  <td class="table_cell_left"><c:out value="${displayItem.metadata.subHeader}" escapeXml="false"/></td>
	    </tr>
	   </c:if>
	    <tr>	 
	    <td class="table_cell_left">
	      <table border="0" >
              <tr>
                <td valign="top">
	</c:if>
	
	<c:if test="${displayItem.metadata.columnNumber >1}">	
      <td valign="top">
     </c:if>
			<table border="0">
				<tr>
					<td valign="top"><c:out value="${displayItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
					<td valign="top"><c:out value="${displayItem.metadata.leftItemText}" escapeXml="false"/></td>
					<td valign="top">
						<%-- display the HTML input tag --%>
						<c:set var="displayItem" scope="request" value="${displayItem}" />
						<c:import url="../submit/showItemInput.jsp">
						  <c:param name="key" value="${numOfDate}" />
						  <c:param name="tabNum" value="${itemNum}"/>
						</c:import>
						<br /><c:import url="../showMessage.jsp"><c:param name="key" value="input${displayItem.item.id}" /></c:import>
					</td>
					<c:if test='${displayItem.item.units != ""}'>
					<td valign="top">
						<c:out value="(${displayItem.item.units})" escapeXml="false"/>
					</td>
					</c:if>
					<td valign="top"><c:out value="${displayItem.metadata.rightItemText}" escapeXml="false" /></td>
				</tr>
			</table>
		</td>
	   <c:if test="${itemStatus.last}">
	      </tr>
           </table>
         </td>
	    
	   </tr>
	   </c:if>

		<c:if test="${displayItem.numChildren > 0}">
	<tr>
		<%-- indentation --%>
		<!--<td class="table_cell">&nbsp;</td>-->
		<%-- NOW SHOW THE CHILDREN --%>
		
		<td class="table_cell">
			<table border="0">
				<c:set var="notFirstRow" value="${0}" />
				<c:forEach var="childItem" items="${displayItem.children}">
					

					<c:set var="currColumn" value="${childItem.metadata.columnNumber}" />
					<c:if test="${currColumn == 1}">
						<c:if test="${notFirstRow != 0}">
				</tr>
						</c:if>
				<tr>
						<c:set var="notFirstRow" value="${1}" />
						<%-- indentation --%>
		           <td valign="top">&nbsp;</td>
					</c:if>
				<%--
						this for loop "fills in" columns left blank
						e.g., if the first childItem has column number 2, and the next one has column number 5,
						then we need to insert one blank column before the first childItem, and two blank columns between the second and third children
					--%>
					<c:forEach begin="${currColumn}" end="${childItem.metadata.columnNumber}">
					<td valign="top">&nbsp;</td>
					</c:forEach>
					
					<td valign="top">
						<table border="0">
							<tr>
								<td valign="top"><c:out value="${childItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
								<td valign="top"><c:out value="${childItem.metadata.leftItemText}" escapeXml="false"/></td>
								<td valign="top">
									<%-- display the HTML input tag --%>
									<c:set var="itemNum" value="${itemNum + 1}" />
									<c:set var="displayItem" scope="request" value="${childItem}" />
									<c:import url="../submit/showItemInput.jsp" >
									  <c:param name="key" value="${numOfDate}" />
									  <c:param name="tabNum" value="${itemNum}"/>
									</c:import>
									<br /><c:import url="../showMessage.jsp"><c:param name="key" value="input${childItem.item.id}" /></c:import>
								</td>
							<c:if test='${childItem.item.units != ""}'>
								<td valign="top"> <c:out value="(${childItem.item.units})" escapeXml="false"/> </td>
							</c:if>
								<td valign="top"> <c:out value="${childItem.metadata.rightItemText}" escapeXml="false"/> </td>
							</tr>
						</table>
					</td>
				</c:forEach>
				</tr>
			</table>
		</td>
	</tr>
		</c:if>
	</c:if>
	<c:set var="displayItemNum" value="${displayItemNum + 1}" />
	<c:set var="itemNum" value="${itemNum + 1}" />
</c:forEach>
</table>

<script type="text/javascript">
<!--
	function onChangeFunc() {
		var xhr;
<c:forEach var="skipRule" items="${section.skipRules}">
		if (<c:forEach var="triggerItem" items="${skipRule.triggerItems}">this.name == 'input<c:out value="${triggerItem.id}"/>' || </c:forEach> false) {
			if (<c:forEach var="jsItem" items="${skipRule.jsExpression}"><c:choose><c:when test="${jsItem.class.name == 'org.akaza.openclinica.bean.submit.ItemBean'}">this.form.input<c:out value="${jsItem.id}"/>.value</c:when><c:otherwise><c:out value="${jsItem}" escapeXml="false"/></c:otherwise></c:choose></c:forEach>) {
			  if (!(<c:forEach var="jsItem" items="${skipRule.jsExpression}"><c:choose><c:when test="${jsItem.class.name == 'org.akaza.openclinica.bean.submit.ItemBean'}">oldValueinput<c:out value="${jsItem.id}"/></c:when><c:otherwise><c:out value="${jsItem}" escapeXml="false"/></c:otherwise></c:choose></c:forEach>)) {
	<c:forEach var="assignment" items="${skipRule.assignments}">
				if (this.form.input<c:out value="${assignment.key.id}"/>.value != '' && this.form.input<c:out value="${assignment.key.id}"/>.value != '<c:out value="${assignment.value}"/>') {
					xhr = new XMLHttpRequest();
					xhr.open('POST', 'CreateDiscrepancyNote?submitted=1&name=itemData&column=value&parentId=0&id=0&field=input<c:out value="${assignment.key.id}"/>&writeToDB=0&description=Skip+Rule+Change&typeId=1&resStatusId=1&detailedDes=Skip+rule+automatically+changed+this+old+value: ' + escape(this.form.input<c:out value="${assignment.key.id}"/>.value) + '&B1=Submit');
					xhr.send(null);
					this.form.flag_input<c:out value="${assignment.key.id}"/>.src='images/icon_Note.gif';
				}
				oldValueinput<c:out value="${assignment.key.id}"/>=this.form.input<c:out value="${assignment.key.id}"/>.value;
				this.form.input<c:out value="${assignment.key.id}"/>.value='<c:out value="${assignment.value}"/>';
				this.form.input<c:out value="${assignment.key.id}"/>.disabled=true;
	</c:forEach>
			  }
			} else if (<c:forEach var="jsItem" items="${skipRule.jsExpression}"><c:choose><c:when test="${jsItem.class.name == 'org.akaza.openclinica.bean.submit.ItemBean'}">oldValueinput<c:out value="${jsItem.id}"/></c:when><c:otherwise><c:out value="${jsItem}" escapeXml="false"/></c:otherwise></c:choose></c:forEach>) {
	<c:forEach var="assignment" items="${skipRule.assignments}">
				this.form.input<c:out value="${assignment.key.id}"/>.value=oldValueinput<c:out value="${assignment.key.id}"/>;
				this.form.input<c:out value="${assignment.key.id}"/>.disabled=false;
	</c:forEach>
			}
			<c:forEach var="triggerItem" items="${skipRule.triggerItems}">
			oldValueinput<c:out value="${triggerItem.id}"/>=this.form.input<c:out value="${triggerItem.id}"/>.value;
			</c:forEach>
		}
</c:forEach>
		return true;
	}
<c:forEach var="skipRule" items="${section.skipRules}">
	<c:forEach var="triggerItem" items="${skipRule.triggerItems}">
	document.forms[0].input<c:out value="${triggerItem.id}"/>.onchange=onChangeFunc;
	oldValueinput<c:out value="${triggerItem.id}"/>=document.forms[0].input<c:out value="${triggerItem.id}"/>.value;
	</c:forEach>
	<c:forEach var="assignment" items="${skipRule.assignments}">
	oldValueinput<c:out value="${assignment.key.id}"/>='';
	</c:forEach>
</c:forEach>
// -->
</script>

<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 6px;">
	<tr>
		<td valign="bottom" nowrap style="padding-right: 50px">
			<a href="#top">&nbsp;&nbsp;Return to top</a>
		</td>
		<td align="right" valign="bottom">
			<table border="0" cellpadding="0" cellspacing="0">
			  <tr>
                <c:choose>
				<c:when test="${stage !='adminEdit' && section.lastSection}">
		        <td valign="bottom">
		               <input type="checkbox" name="markComplete" value="Yes" 
						onClick='return confirm("Marking this CRF complete will finalize data entry. You will no longer be able to add or modify data unless the CRF is reset by an administrator. If Double Data Entry is required, you or another user may need to complete this CRF again before it is verified as complete. Are you sure you want to mark this CRF complete?");'>
				</td>
		        <td valign="bottom" nowrap>&nbsp; Mark Complete &nbsp;&nbsp;&nbsp;</td>
		        </c:when>
		        <c:otherwise>
				  <td colspan="2">&nbsp;</td>
				</c:otherwise>
				</c:choose>
								<td valign="bottom"><input type="checkbox" name="runScoring" value="Yes"
								onClick='return confirm("Scoring this CRF may result in fees due per scoring execution to the form publisher.  Failure to score will erase any previously recorded scores.  Are you sure you want to score this CRF now?");'>
								</td>
								<td valign="bottom" nowrap>&nbsp; Score Now &nbsp;&nbsp;&nbsp;</td>
				<%--
				 <c:if test="${!section.firstSection}">
				   <td><input type="submit" name="submittedPrev" value="Back" class="button_medium"  onClick="return confirm('Would you like to save before continuing to another section?');"/></td>
				</c:if>	
				--%>
		        <td><input type="submit" name="submittedResume" value="Save" class="button_medium" /></td>
                <%--
		        <c:choose>
				 <c:when test="${!section.lastSection}">
				  <td><input type="submit" name="submittedNext" value="Next" class="button_medium" onClick="return confirm('Would you like to save before continuing to another section?');"/></td>
				 </c:when>
				 <c:otherwise>
				  <td>&nbsp;</td>
				 </c:otherwise>
				</c:choose>
				--%>
				<td><input type="submit" name="submittedExit" value="Exit" class="button_medium" onClick="return checkEntryStatus('DataStatus_bottom');" /></td>
								
		        <td valign="bottom"><img name="DataStatus_bottom" src="images/icon_UnchangedData.gif">&nbsp;</td>
			  
			    
			   </tr>
			</table>
		</td>
	</tr>
</table>

<!-- End Table Contents -->

</div>
</div></div></div></div></div></div></div></div>
</div>

  </td>
</tr>
</table>

<br>

<!--
<div style="width: 100%">

<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">


<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td valign="top" class="table_header_column_top">Comments or annotations:</td>
	</tr>
	<tr>
		<td valign="top" class="table_cell_top">
			<textarea name="annotations" rows="8" cols="50"><c:out value="${annotations}" /></textarea>
		</td>
	</tr>
</table>

</div>
</div></div></div></div></div></div></div></div>
</div>
-->