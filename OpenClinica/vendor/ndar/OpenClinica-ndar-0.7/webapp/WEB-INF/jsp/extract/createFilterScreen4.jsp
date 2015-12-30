<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>

<h1><span class="title_extract">Create Filter: Specify Criteria <a href="javascript:openDocWindow('help/3_4_createFilter_Help.html#step4')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>
<P><jsp:include page="../include/showPageMessages.jsp"/></P>
<jsp:include page="createFilterBoxes.jsp">
	<jsp:param name="selectValue" value="1"/>
</jsp:include>
<form action="CreateFiltersTwo" name="cf2">
<input type="hidden" name="action" value="validatecriteria"/>


&nbsp;<b>Filter Preview:</b>&nbsp;&nbsp;
<c:out value='${cBean.name}'/>&nbsp;
<c:out value='${cvBean.name}'/><br/>
<br/>
&nbsp;<b>Filter Query Design:</b><br/>
&nbsp;<b>Logical Connector:</b>&nbsp;&nbsp;<select name="logical">
							<option value="and">and
							<option value="or">or
							</select><br/>
<table border="0" cellpadding="5">
<tr valign="top">
    <td class="text"><b>Parameter</b></td>    
    <td class="text"><b>Operator</b></td> 
    <td class="text"><b>Value</b></td>
    <td class="text"><b>Remove?</b></td>
</tr>
<c:forEach var='item' items='${questions}'>
<c:set var='response' value='${item.responseSet.options}'/>
<tr valign="top">
    <td class="text">
    		<c:out value='${item.questionNumberLabel}'/> 
			<c:out value='${item.header}'/> 
			<c:out value='${item.leftItemText}'/>
	</td> 
	<%-- if/else block to determine where we are searching --%>
	<c:choose>
		<%-- this should mean text --%>
		<c:when test='${item.responseSet.label=="Text"}'>
			<td class="text">
				<select name="operator:<c:out value='${item.id}'/>">
    			<option value="equal to" selected>equal to
    			<option value="like">contains the text
    			<option value="not like">does not contain the text
    			<option value="not equal to">not equal to
    			</select>
    		</td> 
    		<td class="text">
		    	<input type="text" name="value:<c:out value='${item.id}'/>"/>
    			&nbsp;
    			<c:out value='${item.rightItemText}'/>
    		</td>
		</c:when>
		<%-- this should mean a select dropdown --%>
		<c:otherwise>
			<td class="text"><select name="operator:<c:out value='${item.id}'/>">
    			<option value="equal to" selected>equal to
    			<option value="greater than">greater than
    			<option value="less than">less than
    			<option value="greater than or equal">greater than or equal
		    	<option value="less than or equal">less than or equal
    			<option value="not equal to">not equal to
		    	</select>
    		</td> 
		    <td class="text">
		    	<select name="value:<c:out value='${item.id}'/>">
    			<option value="" selected>
    			<option value="N/A">N/A
    			<option value="No Response">No Response
    			<c:forEach var='option' items='${response}'>
    				<option value="<c:out value='${option.value}'/>">
    				<c:out value='${option.text}'/> - <c:out value='${option.value}'/>
    			</c:forEach>
    			</select>
    			&nbsp;
    			<c:out value='${item.rightItemText}'/>
    		</td>
		</c:otherwise>
	</c:choose>		
			   
    <td class="text">
    	<input type="checkbox" value="remove" name="remove:<c:out value='${item.id}'/>"/>
    </td>
</tr>
</c:forEach>  
</table>
<center>
<input type="submit" name="submit" value="Add Additional Parameters" class="button_xlong"/><br/>
<input type="submit" name="submit" value="Specify Filter Metadata" class="button_xlong"/><br/>
</center>
</form>
<jsp:include page="../include/footer.jsp"/>