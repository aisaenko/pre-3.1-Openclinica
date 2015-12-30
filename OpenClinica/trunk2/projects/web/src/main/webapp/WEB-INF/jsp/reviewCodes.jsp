<%@ page import="org.akaza.openclinica.bean.core.Status"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>


<jsp:include page="include/managestudy_top_pages.jsp"/>


<!-- move the alert message to the sidebar-->
<jsp:include page="include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

        <div class="sidebar_tab_content">

            <fmt:message key="design_implement_sdv" bundle="${restext}"/>

        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

    </td>
</tr>
<jsp:include page="include/sideInfo.jsp"/>
<link rel="stylesheet" href="../includes/jmesa/jmesa.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery.jmesa.js"></script>
<%-- view all subjects starts here --%>
<script type="text/javascript">

    function onInvokeAction(id,action) {
        setExportToLimit(id, '');
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        //location.href = '${pageContext.request.contextPath}/ViewCRF?module=manage&crfId=' + '${crf.id}&' + parameterString;
    }
	
	function checkCRFLocked(ecId, url){
		//alert(url);
        //jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
           // if(data == 'true'){
                //window.location = url;
           // }else{
               // alert(data);return false;
            //}
        //});
		window.location = url;
    }
    function checkCRFLockedInitial(ecId, formName){
	
        if(ecId==0) {formName.submit(); return;} 
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                formName.submit();
            }else{
                alert(data);
            }
        });
    }
	
	function openBrowsePopup(targetField)
	{
		//alert('inside openBrowsePopup');
		var w = window.open('../coding_dict_popup.html','browse dictionary','width=610,height=550,scrollbars=1');
		w.targetField = targetField; //create target field variable in popup window with the passed targetField as value
		w.focus();
		return false;
	}

	function setTargetField(targetField, code)
	{
		//alert('inside setTargetField, targetField:' + targetField + ', code: ' + code);
		if (targetField)
		{
		//alert('targetField exists');
		var field = document.getElementById(targetField);
		//alert('targetField value before: ' + field.value);
		//targetField.value = code;
		field.value = code;
		//alert('targetField value after: ' + field.value);
		//document.getElementById(targetField).value = code;
		//alert(document.getElementById(targetField));
		
		
		}
		window.focus();
	}
</script></div>

<h1><span class="title_manage">
<fmt:message key="coding_review_title" bundle="${resword}"/>
    <!--  <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/openclinica-user-guide/monitor-and-manage-data')">
        <img src="../images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a>
</span></h1>-->
<br>
<div id="bp_quick_jump"></div>
<script type="text/javascript">
    var BP_ontology_id = "1422";
	//var BP_include_definitions = true;	
</script>
<script src="../includes/bioPortal_quickJump.js" type="text/javascript" charset="utf-8">
</script>


<br>


<table border="1">
	<tr align="center">
		<td bgcolor="#789EC5">ID</td>
		<td bgcolor="#789EC5">Study Subject ID</td>
		<td bgcolor="#789EC5">Event Name</td>
		<td bgcolor="#789EC5">Event Ordinal</td>
		<td bgcolor="#789EC5">CRF Name</td>
		<td bgcolor="#789EC5">CRF Version Name</td>
		<td bgcolor="#789EC5">Item Group</td>
		<td bgcolor="#789EC5">Group Ordinal</td>
		<td bgcolor="#789EC5">Verbatim Term Name</td>
		<td bgcolor="#789EC5">Verbatim Term Value</td>
		<!-- <td bgcolor="#789EC5">Coding Status</td> -->		
		<td bgcolor="#789EC5">Coding Status</td>
		<td bgcolor="#789EC5">Code Item Name</td>
		<td bgcolor="#789EC5">Code Value</td>
		<td bgcolor="#789EC5">Code Details</td>
		<td bgcolor="#789EC5">Actions</td>
		<!-- <td bgcolor="#789EC5">Action</td> -->
	</tr>
	<!-- <form name='reviewCodesForm' action="${pageContext.request.contextPath}/pages/searchExactMatch">
		<input type="hidden" name="redirection" value="reviewCodes">
		<input type="hidden" name="studyId" value="${param.studyId}"> 
	<tr>
		<td>101</td>
		<td>ABC Medical Test event</td>
		
		<td><a href="#" onclick="checkCRFLocked('1', '${pageContext.request.contextPath}/InitialDataEntry?eventCRFId=1&id=1&exitTo=pages/reviewCodes?studyId=1');">Test</a>
		
		</td>
		<td>symptom</td>
		<td><font color="#0000FF" ><c:out value="${codingStatus}"/></font></td>		
		<td><input type="text" name="verbatimTerm" value="${codedTermBean.PT}"/></td>
		<td>
			<c:if test="${codedTermBean.conceptId != null}"> 
				<font color="#990066" >CODE:</font> <c:out value="${codedTermBean.conceptId}"/><br>
				<font color="#990066" >SOC:</font> <c:out value="${codedTermBean.SOC}"/><br>
				<font color="#990066" >HLGT:</font> <c:out value="${codedTermBean.HLGT}"/><br>
				<font color="#990066" >HTL:</font> <c:out value="${codedTermBean.HLT}"/>
			</c:if>	
		</td>
		<td><input type="submit" class="button_medium"value="AutoCode"/></td>
	</tr>
	 </form> -->
	 <form id="codeReview" action="#">
		<c:forEach items="${codeItemDataList}" var="codeItemData" varStatus="i">
	 	<tr>
		<td><c:out value="${i.count}" /> </td>
		<td><c:out value="${codeItemData.studySubjectId}" /> </td>
	 	<td><c:out value="${codeItemData.eventName}" /> </td>
	 	<td><c:out value="${codeItemData.eventOrdinal}" /> </td>
	 	<td><c:out value="${codeItemData.crfName}" /> </td>
	 	<td><c:out value="${codeItemData.crfVersionName}" /> </td>
	 	<td><c:out value="${codeItemData.groupName}" /> </td>
	 	<td><c:out value="${codeItemData.groupOrdinal}" /> </td>
	 	<td>
	 		<c:if test="${empty codeItemData.verbatimTermItemName}">
	 			<c:out value="" />
	 		</c:if>
	 		<c:if test="${!empty codeItemData.verbatimTermItemName}">
	 			<c:out value="${codeItemData.verbatimTermItemName}" />
	 		</c:if>	 
	 	</td>		
	 	
		<c:if test="${empty codeItemData.verbatimTermItemValue}">
			<td>
			&nbsp;
			</td>
		</c:if>
		<c:if test="${!empty codeItemData.verbatimTermItemValue}">
			<td>
			<c:out value="${codeItemData.verbatimTermItemValue}" />
			</td>
		</c:if>
	 	
	 	<td><c:out value="${codeItemData.status}" /></td>
	 	<td><c:out value="${codeItemData.codeItemName}" /> </td>
	 	 
		<c:if test="${empty codeItemData.codeItemValue}">
			<td>
			&nbsp;
			</td>
		</c:if>
		<c:if test="${!empty codeItemData.codeItemValue}">
			<td>
			<c:out value="${codeItemData.codeItemValue}" />
			</td>
		</c:if>
	 	
		<c:if test="${empty codeItemData.codeRootPath}">
			<td>
			&nbsp;
			</td>
		</c:if>
		<c:if test="${!empty codeItemData.codeRootPath}">
			<td>
			<c:out value="${codeItemData.codeRootPath}" />
			</td>
		</c:if>
		
		<td>
			<!--<a href="javascript: openDocWindow('../test.html')">
                Browse</a>-->
				
				<c:set var="varName" value="code${i.count}" />
				
				<input type="text" name="${varName}" id="${varName}"/>
				<a href="#" onclick="return openBrowsePopup('<c:out value="${varName}"/>', document.getElementById('<c:out value="${varName}"/>'))">browse</a>
		</td>
		</tr>
	 </c:forEach> 
	 </form>
	 </table>	
<br>

	








<%-- view all subjects ends here --%>



<jsp:include page="include/footer.jsp"/>