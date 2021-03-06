<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>	
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>


<jsp:include page="../include/managestudy-header.jsp"/>


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

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='definition' class='org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean'/>
<jsp:useBean scope='request' id='eventDefinitionCRFs' class='java.util.ArrayList'/>
<h1><span class="title_manage"><fmt:message key="confirm_event_definition_creation"  bundle="${resword}"/></span></h1>

<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${definition.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${definition.description}"/>&nbsp;
  </td></tr>
 
 <tr valign="top"><td class="table_header_column"><fmt:message key="repeating" bundle="${resword}"/>:</td><td class="table_cell">
  <c:choose>
   <c:when test="${definition.repeating == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
   <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="type" bundle="${resword}"/>:</td><td class="table_cell">
    <fmt:message key="${definition.type}" bundle="${resword}"/>
   </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="category" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${definition.category}"/>&nbsp;
  </td></tr>
  </table>
 
<br>
 
 <span class="table_title_manage"><fmt:message key="CRFs" bundle="${resword}"/></span>
 <table cellpadding="0" border="0" class="shaded_table cell_borders  hrow">
 <tr valign="top">               
    <th><fmt:message key="name" bundle="${resword}"/></th>   
    <th><fmt:message key="required" bundle="${resword}"/></th>     
    <th><fmt:message key="double_data_entry" bundle="${resword}"/></th>
    <th><fmt:message key="password_required" bundle="${resword}"/></th>
    <th><fmt:message key="hidden_crf" bundle="${resword}"/></th>   
    <!-- <th><fmt:message key="enforce_decision_conditions" bundle="${resword}"/></td>-->  
    <th><fmt:message key="default_version" bundle="${resword}"/></th>
    <th><fmt:message key="sdv_option" bundle="${resword}"/></th>  
    <th><fmt:message key="null_values" bundle="${resword}"/></th>    
  </tr>   
 <c:forEach var ="crf" items="${eventDefinitionCRFs}">   
   <tr>            
    <td class="table_cell_left"><c:out value="${crf.crfName}"/></td>      
    <td class="table_cell">
    <c:choose>
    <c:when test="${crf.requiredCRF == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
     <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
    </c:choose>
   </td>
     
    <td class="table_cell">
     <c:choose>
      <c:when test="${crf.doubleEntry == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
      <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
     </c:choose>
    </td>       

    <td class="table_cell">
     <c:choose>
      <c:when test="${crf.electronicSignature == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
      <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
     </c:choose>
    </td>

    <td class="table_cell">
     <c:choose>
      <c:when test="${crf.hideCrf == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
      <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
     </c:choose>
    </td>
  
  
   <td class="table_cell">  
    <c:out value="${crf.defaultVersionName}"/>     
  </td>  
  
  <td class="table_cell">   
    <fmt:message key="${crf.sourceDataVerification.description}" bundle="${resterm}"/> 
  </td>   
  
   <td class="table_cell">   
    <c:out value="${crf.nullValues}"/>&nbsp;      
  </td> 
  </tr>            
   
 </c:forEach>
 
</table>

<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>   
    <td><form action="DefineStudyEvent" method="POST">
         <input type="hidden" name="actionName" value="submit">
         <input type="hidden" name="nextAction" value="2">
         <input type="submit" name="submit" value="<fmt:message key="confirm_and_finish" bundle="${resword}"/>" class="button_xlong">
        </form>
    </td>
    <td><form action="DefineStudyEvent" method="POST">
         <input type="hidden" name="actionName" value="submit">
         <input type="hidden" name="nextAction" value="3">
         <input type="submit" name="submit" value="<fmt:message key="confirm_and_create_another_definition" bundle="${resword}"/>" class="button_xlong">
        </form>
    </td>
    <td><form action="DefineStudyEvent" method="POST">
         <input type="hidden" name="actionName" value="submit">
         <input type="hidden" name="nextAction" value="1">
         <input type="submit" name="submit" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium">
        </form>
    </td>
    </tr>  
   </table>
   <br><br>

<!-- EXPANDING WORKFLOW BOX -->



<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
