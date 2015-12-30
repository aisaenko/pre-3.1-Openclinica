<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">  
    var cal1 = new CalendarPopup("testdiv1");
  </SCRIPT>
  </table>
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr id="AddSubjectRow1" style="display:none">
		<form id="subjectForm" name="subjectForm" action="AddNewSubject" method="post">
		   
			<td class="table_cell_left" colspan="2" nowrap>
			  <jsp:include page="../include/showSubmitted.jsp" />
			  <input type="hidden" name="addWithEvent" value="1"/>
				<c:choose>
			      <c:when test="${study.studyParameterConfig.subjectIdGeneration =='auto non-editable'}">
			      Study Subject ID:
			       <input type="text" value="<c:out value="${label}"/>" size="5" class="formfield" disabled>
			       <input type="hidden" name="label" value="<c:out value="${label}"/>">
			      </c:when>
			      <c:when test="${study.studyParameterConfig.subjectIdGeneration =='auto editable'}">
			      Study Subject ID:
			       <input type="text" name="label" value="<c:out value="${label}"/>" size="5" class="formfield">
			      </c:when>
			      <c:otherwise>
			        <!--<input type="text" name="label" value="<c:out value="${label}"/>" size="5" class="formfield">-->
			        <input type="text" name="label" value="Study Subject ID" size="12" class="formfield"><span class="formlabel">*</span>
			      </c:otherwise>
			    </c:choose>
			</td>
			
			<c:choose>
			<c:when test="${study.studyParameterConfig.genderRequired !='false'}">						
			<td valign="top" class="table_cell" nowrap>
			<select name="gender" class="formfield">
	
				<option value="">Gender:</option>
				<option value="m">Male</option>
				<option value="f">Female</option>
			</select><span class="formlabel">*</span></td>
			</c:when>
			<c:otherwise>
		    	<input type="hidden" name="gender" value="">
			</c:otherwise>
			</c:choose> 
			
			<c:set var="count" value="0"/>
			<c:forEach var="groupClass" items="${studyGroupClasses}">			
					
			<td valign="top" class="table_cell" nowrap>
	
			  <select name="studyGroupId<c:out value="${count}"/>" class="formfield">
	
				<option value=""><c:out value="${groupClass.name}"/>:</option>
				 <c:forEach var="studyGroup" items="${groupClass.studyGroups}">
				   <option value="<c:out value="${studyGroup.id}"/>"><c:out value="${studyGroup.name}"/></option>
				 </c:forEach>
			  </select>
			</td>
				<c:set var="count" value="${count+1}"/>	
			</c:forEach>
					
			<td valign="top" colspan="6" class="table_cell" nowrap>
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td valign="top">
	
					<select name="studyEventDefinition" class="formfield">
	                   
						<option value="">Event:</option>
						<c:forEach var="event" items="${allDefsArray}">
						  <option value="<c:out value="${event.id}"/>"><c:out value="${event.name}"/></option>
						</c:forEach>											
						</select>
					</td>
					<td valign="top" align="right">
					 <input type="text" name="location" size="8" value="Location" class="formfield" />
					</td>
					<td valign="top">
					<input type="text" name="enrollmentDate" size="15" value="Event: MM/DD/YYYY" class="formfield" />
					</td>				
				     <td valign="top">*<A HREF="#" onClick="cal1.select(document.subjectForm.enrollmentDate,'anchor1','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.subjectForm.enrollmentDate,'anchor1','MM/dd/yyyy'); return false;" NAME="anchor1" ID="anchor1"><img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a> 
					</td>
				</tr>
			</table>
			</td>
			<td valign="top" align="right" class="table_cell" nowrap>
				<input type="submit" name="addSubject" value="Add" class="button_search" /></td>
			</td>
			</td>
		</tr>
		<tr id="AddSubjectRow2" style="display:none">
			<td valign="top" align="left" colspan="2" class="table_cell_left" nowrap>
			 <c:choose>
	           <c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='required'}">
			    &nbsp;<input type="text" name="uniqueIdentifier" value="Person ID" size="12" class="formfield"><span class="formlabel">*</span>
			   </c:when>
			   <c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='optional'}">
			    &nbsp;<input type="text" name="uniqueIdentifier" value="Person ID" size="12" class="formfield">
			   </c:when>
			   <c:otherwise>
			    &nbsp;<input type="hidden" name="uniqueIdentifier" value="">
			   </c:otherwise>
			 </c:choose>  
			</td>
			<td valign="top" align="left" colspan="3" class="table_cell" nowrap>
			<table border="0">
				<tr>
					<td valign="top" align="right">
					 <c:choose>
	                    <c:when test="${study.studyParameterConfig.collectDob == '1'}">
						&nbsp;<input type="text" name="dob" size="20" value="DOB: MM/DD/YYYY" class="formfield"><span class="formlabel">*</span>
						</td>
						 <td valign="top" align="left"><a href="#" onClick="cal1.select(document.subjectForm.dob,'anchor2','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.subjectForm.dob,'anchor2','MM/dd/yyyy'); return false;" NAME="anchor2" ID="anchor2"> <img src="images/bt_Calendar.gif" alt="Show Calendar" title="Show Calendar" border="0" /></a>
					    </c:when>
					    <c:when test="${study.studyParameterConfig.collectDob == '2'}">
					     &nbsp;<input type="text" name="yob" size="15" value="YOB: YYYY" class="formfield" /><span class="formlabel">*</span>
					    </c:when>
					    <c:otherwise>
					      &nbsp;<input type="hidden" name="dob" value="" />
					    </c:otherwise>
					  </c:choose>
					    
					
					</td>
				</tr>
			</table>
			</td>
			<td valign="top" nowrap align="right" colspan="8" class="table_cell"><a href="AddNewSubject?instr=1">Enter Full Record Details</a></td>
		</tr>
	    </form>
	   
								
	 
										
		<!-- End Data -->
					
				
			
							
		</table>

		<!-- End Table Contents -->			
		</td>

	</tr>
	
		
		
 

  </table>
	
			
	
 <!-- End Table 0 -->	
</div>
</div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>


<DIV ID="testdiv1" STYLE="position:absolute;z-index:5;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
