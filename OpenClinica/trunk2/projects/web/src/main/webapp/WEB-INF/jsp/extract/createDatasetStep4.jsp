<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


<jsp:include page="../include/extract-header.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>



		<b><fmt:message key="instructions" bundle="${resword}"/></b>



		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>



		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<c:if test="${newDataset.id>0}">
		<div class="sidebar_tab_content">
		<P><fmt:message key="enter_dataset_properties_be_descriptive" bundle="${restext}"/> <font color="red"><fmt:message key="name_description_required" bundle="${restext}"/></font></P>
		<p><fmt:message key="copy_dataset_by_change_name" bundle="${restext}"/></p>
		</div>
		</c:if>
		</td>
  </tr>

<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<jsp:useBean scope='request' id='statuses' class='java.util.ArrayList' />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="dsName" value="${newDataset.name}" />
<c:set var="dsDesc" value="${newDataset.description}" />
<c:set var="itemStatusId" value="${newDataset.datasetItemStatus.id}"/>
<c:set var="dsStatusId" value="${0}" />
<c:set var="mdvOID" value="${mdvOID}"/>
<c:set var="mdvName" value="${mdvName}"/>
<c:set var="mdvPrevStudy" value="${mdvPrevStudy}"/>
<c:set var="mdvPrevOID" value="${mdvPrevOID}"/>

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
	<c:if test='${presetValue.key == "mdvOID"}'>
		<c:set var="mdvOID" value="${presetValue.value}"/>
	</c:if>
	<c:if test='${presetValue.key == "mdvName"}'>
		<c:set var="mdvName" value="${presetValue.value}"/>
	</c:if>
	<c:if test='${presetValue.key == "mdvPrevStudy"}'>
		<c:set var="mdvPrevStudy" value="${presetValue.value}"/>
	</c:if>
	<c:if test='${presetValue.key == "mdvPrevOID"}'>
		<c:set var="mdvPrevOID" value="${presetValue.value}"/>
	</c:if>
</c:forEach>

<h1><span class="title_manage">
<c:choose>
<c:when test="${newDataset.id>0}">
<fmt:message key="edit_dataset" bundle="${resword}"/> - <fmt:message key="specify_dataset_properties" bundle="${resword}"/> <a href="javascript:openDocWindow('help/4_7_editDataset_Help.html#step3')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
: <c:out value="${newDataset.name}"/>
</c:when>
<c:otherwise><fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="specify_dataset_properties" bundle="${resword}"/> <a href="javascript:openDocWindow('help/4_2_createDataset_Help.html#step4')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
</c:otherwise>
</c:choose>
</span></h1>



<!--
<p>Please enter the dataset properties in the fields below.  Be descriptive.  <font color="red">All fields are required.</font></p>
-->

<c:if test="${newDataset.id<=0}">
<P><div class="indicates_field"><span class="indicates_required_field">*</span><fmt:message key="indicates_required_field" bundle="${resword}"/></div></P>
<fmt:message key="enter_dataset_properties_be_descriptive" bundle="${restext}"/> </c:if>

<form action="CreateDataset" method="post">
<input type="hidden" name="action" value="specifysubmit"/>

<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 table_first_column_w30">
	<tr>

		<td class="formlabel"><fmt:message key="name" bundle="${resword}"/>:</td>

		<td><div><input type="text" name="dsName"  class="formfieldL" size="30" value="<c:out value='${dsName}' />"/>
		<c:if test="${newDataset.id>0}"><fmt:message key="change_dataset_name_create_copy" bundle="${restext}"/></c:if>
	<span class="indicates_required_field">*</span>
		</div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dsName"/></jsp:include>

		</td>

	</tr>

	<tr>

		<td class="formlabel"><fmt:message key="description" bundle="${resword}"/>:</td>

		<td><div><textarea  class="formtextarea" name="dsDesc" style="height: 80px; width: 357px;"><c:out value="${dsDesc}" /></textarea>
<span class="indicates_required_field">*</span></div>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dsDesc"/></jsp:include>

		</td>

	</tr>

<tr>
	<td class="formlabel"><fmt:message key="item_status" bundle="${resword}"/>:</td>
	<td>
		<c:choose>
		<c:when test="${newDataset.id<=0 || itemStatusId==1}">
 		<input type="radio" name="itemStatus" value="1" checked><fmt:message key="completed_items" bundle="${resterm}"/>
   		<br><input type="radio" name="itemStatus" value="2"><fmt:message key="non_completed_items" bundle="${resterm}"/>
   		<br><input type="radio" name="itemStatus" value="3"><fmt:message key="completed_and_non_completed_items" bundle="${resterm}"/>
   		</c:when>
   			<c:when test="${itemStatusId==2}">
   		<input type="radio" name="itemStatus" value="1"><fmt:message key="completed_items" bundle="${resterm}"/>
   		<br><input type="radio" name="itemStatus" value="2" checked> <fmt:message key="non_completed_items" bundle="${resterm}"/>
   		<br><input type="radio" name="itemStatus" value="3"> <fmt:message key="completed_and_non_completed_items" bundle="${resterm}"/>
   		</c:when>
   			<c:when test="${itemStatusId==3}">
   		 <input type="radio" name="itemStatus" value="1"> <fmt:message key="completed_items" bundle="${resterm}"/>
   		<br><input type="radio" name="itemStatus" value="2"> <fmt:message key="non_completed_items" bundle="${resterm}"/>
   		<br><input type="radio" name="itemStatus" value="3" checked> <fmt:message key="completed_and_non_completed_items" bundle="${resterm}"/>
   		</c:when>
   		</c:choose>
   </td>
	</tr>
	</table>

	<br><br><br><br>

 <fmt:message key="long_note1" bundle="${restext}"/>

 <fmt:message key="long_note2" bundle="${restext}"/>

 <fmt:message key="long_note3" bundle="${restext}"/>

 <fmt:message key="long_note4" bundle="${restext}"/>

 <fmt:message key="long_note5" bundle="${restext}"/>

 <fmt:message key="long_note6" bundle="${restext}"/>

	<br><br>

	<table  cellpadding="2" border="0">
	<tr>

		<td><fmt:message key="metadataversion_ODM_ID" bundle="${resword}"/>:          </td>

		<td><div>
		<input type="text" name="mdvOID" size="25" value="<c:out value='${mdvOID}' />"/></div>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvOID"/></jsp:include>
		</td>
	</tr>
	<tr>

		<td><fmt:message key="metadataversion_name" bundle="${resword}"/>:            </td>

		<td>

		<div><input type="text" name="mdvName" size="25" value="<c:out value='${mdvName}' />"/>

		</div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvName"/></jsp:include>

		</td>

	</tr>

	<tr>

		<td><fmt:message key="previous_study_ODM_ID" bundle="${resword}"/>:            </td>

		<td><div>

		<input type="text" name="mdvPrevStudy" size="25" value="<c:out value='${mdvPrevStudy}' />"/>

		</div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvPrevStudy"/></jsp:include>

		</td>

	</tr>

	<tr>

		<td><fmt:message key="previous_metadataversion_ODM_ID" bundle="${resword}"/>:   </td>

		<td><div>

		<input type="text" name="mdvPrevOID" size="25" value="<c:out value='${mdvPrevOID}' />"/>

		</div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvPrevOID"/></jsp:include>

		</td>

	</tr>
</table>
<br>

<table border="0">	
	<tr>

		<td >

		<input type="hidden" name="dsStatus" value="1"/>

			<input type="submit" name="remove" value=" <fmt:message key="continue" bundle="${resword}"/>" class="button_medium"/>
          </td><td>  <input type="button" onclick="confirmCancel('ViewDatasets');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>

        </td>

	</tr>

</table>

</form>

<br><br>





<jsp:include page="../include/footer.jsp"/>
