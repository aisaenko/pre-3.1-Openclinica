<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<html>
<head>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
<script language="JavaScript" src="includes/global_functions_javascript.js"></script>
<style type="text/css">

.popup_BG { background-image: url(images/main_BG.gif);
	background-repeat: repeat-x;
	background-position: top;
	background-color: #FFFFFF;
	}


</style>

</head>
<body  style="margin: 12px;">
<script language="JavaScript">
       <!--
         function leftnavExpand(strLeftNavRowElementName){

	       var objLeftNavRowElement;

           objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
           if (objLeftNavRowElement != null) {
             if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; } 
	           objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";		
	         }
           }

       //-->
 </script>    
 <jsp:include page="../include/alertbox.jsp"/>

     <h1><span class="title_manage"><fmt:message key="item_meta_global_att" bundle="${resword}"/></span><h1>
<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
 <tr valign="top" ><td class="table_header_column"><fmt:message key="CRF_name" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${crf.name}"/>
   </td></tr>
  <tr valign="top" ><td class="table_header_column"><fmt:message key="item_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${item.name}"/>
   </td></tr>
  <tr valign="top" ><td class="table_header_column"><fmt:message key="OID" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${item.oid}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${item.description}"/>&nbsp;
  </td></tr> 
  <tr valign="top"><td class="table_header_column"><fmt:message key="data_type" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${item.dataType.name}"/>&nbsp;
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="units" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${item.units}"/>&nbsp;
  </td></tr> 
  <tr valign="top"><td class="table_header_column"><fmt:message key="PHI" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:choose>
    <c:when test="${item.phiStatus}">
      <fmt:message key="yes" bundle="${resword}"/>
    </c:when>
    <c:otherwise>
      <fmt:message key="no" bundle="${resword}"/>
    </c:otherwise>
  </c:choose>
  
  </td></tr> 
</table>

<br>

<h2><span class="table_title_manage"><fmt:message key="item_meta_crf_att" bundle="${resword}"/></span></h2>
<p><fmt:message key="click_on_each_CRF_version_link" bundle="${restext}"/></p>
<c:set var="versionCount" value="0"/>

 <c:forEach var="versionItem" items="${versionItems}">
  
  <a href="javascript:leftnavExpand('leftnavSubRow_SubSection<c:out value="${versionCount}"/>');" class="anchor_page_section_oc">
    <img id="excl_overview" src="images/bt_Collapse.gif" border="0">
    <span class="section_link">
    <c:out value="${versionItem.crfName}"/>&nbsp;<c:out value="${versionItem.crfVersionName}"/></span></a>
<div id="leftnavSubRow_SubSection<c:out value="${versionCount}"/>" style="">

<table cellpadding="0" border="0" class="shaded_table table_first_column_w30 cell_borders fcolumn">
   	      <tr>

	       <td class="table_header_column_top"><fmt:message key="left_item_text" bundle="${resword}"/></td>
           <td class="table_header_column_top"><fmt:message key="right_item_text" bundle="${resword}"/></td>   
           <td class="table_header_column_top"><fmt:message key="default_value" bundle="${resword}"/></td>
  		   <td class="table_header_column_top"><fmt:message key="response_layout" bundle="${resword}"/></td>
  		   <td class="table_header_column_top"><fmt:message key="response_type" bundle="${resword}"/></td>
           <td class="table_header_column_top"><fmt:message key="response_label" bundle="${resword}"/></td>
           <td class="table_header_column_top"><fmt:message key="response_options" bundle="${resword}"/>/   
           <fmt:message key="response_values" bundle="${resword}"/></td>

           <td class="table_header_column_top"><fmt:message key="section_label" bundle="${resword}"/></td>
           <td class="table_header_column_top"><fmt:message key="group_name" bundle="${resword}"/></td>
           <td class="table_header_column_top"><fmt:message key="validation_label" bundle="${resword}"/></td>
           <td class="table_header_column_top"><fmt:message key="validation_error_mgs" bundle="${resword}"/></td>

           <td class="table_header_column_top"><fmt:message key="required" bundle="${resword}"/></td>
          </tr>
          <tr valign="top">            
           <td class="table_cell"><c:out value="${versionItem.leftItemText}"/>&nbsp;</td>
           <td class="table_cell"><c:out value="${versionItem.rightItemText}"/>&nbsp;</td>           
           <td class="table_cell"><c:out value="${versionItem.defaultValue}"/>&nbsp;</td>             
           <td class="table_cell"><c:out value="${versionItem.responseLayout}"/>&nbsp;</td>     
           <td class="table_cell"><c:out value="${versionItem.responseSet.responseType.name}"/>&nbsp;</td>
           <td class="table_cell"><c:out value="${versionItem.responseSet.label}"/>&nbsp;</td>
           <td class="table_cell">
            <c:forEach var="option" items="${versionItem.responseSet.options}" varStatus="status">
                <c:out value="${option.text}"/>
            |
                <c:out value="${option.value}"/>
                <c:if test="${!status.last}">
                <br>
                </c:if>

            </c:forEach>&nbsp;
           <td class="table_cell"><c:out value="${section.label}"/>&nbsp;</td>   
              <c:choose>
               <c:when test="${versionItem.groupLabel != 'Ungrouped'}">
                   <td class="table_cell"><c:out value="${versionItem.groupLabel}"/></td>
               </c:when>
               <c:otherwise>
                   <td class="table_cell"><c:out value=""/>&nbsp;</td>
               </c:otherwise>
               </c:choose>

           <td class="table_cell"><c:out value="${ifmdBean.regexp}"/>&nbsp;</td>   
           <td class="table_cell"><c:out value="${ifmdBean.regexpErrorMsg}"/>&nbsp;</td>   

           <td class="table_cell">
            <c:choose>
             <c:when test="${versionItem.required==true}">
               <fmt:message key="yes" bundle="${resword}"/> 
             </c:when> 
             <c:otherwise>
               No
             </c:otherwise> 
            </c:choose>&nbsp;
            </td> 
           </tr>          
	     </table>
	     </div>
  
  <br>
  <c:set var="versionCount" value="${versionCount+1}"/>
 </c:forEach>
 
</body>
</html>

