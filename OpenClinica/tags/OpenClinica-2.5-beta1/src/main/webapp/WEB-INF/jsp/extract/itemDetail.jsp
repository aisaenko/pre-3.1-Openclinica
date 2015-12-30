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
<body class="popup_BG">
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
 <table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
 <tr valign="top">
	<td background="images/popup_BG.gif" ><img src="images/popup_OC.gif"></td>	
  </tr>
 <tr valign="top"> 
 <td>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
  <tr valign="top" ><td class="table_header_column"><fmt:message key="item_name" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${item.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${item.description}"/>&nbsp;
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
</div>

</div></div></div></div></div></div></div></div>
</div>
<br>

<span class="table_title_extract"><fmt:message key="item_metadata" bundle="${resword}"/></span>
<p><fmt:message key="click_on_each_CRF_version_link" bundle="${restext}"/></p>
<c:set var="versionCount" value="0"/>

 <c:forEach var="versionItem" items="${versionItems}">
  
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr>
	 <td valign="top" class="leftmenu"><a href="javascript:leftnavExpand('leftnavSubRow_SubSection<c:out value="${versionCount}"/>'); 
	   javascript:setImage('ExpandGroup<c:out value="${versionCount}"/>','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup<c:out value="${versionCount}"/>" src="images/bt_Collapse.gif" border="0"></a></td>
	 <td valign="top" class="leftmenu"><a href="javascript:leftnavExpand('leftnavSubRow_SubSection<c:out value="${versionCount}"/>'); 
	     javascript:setImage('ExpandGroup<c:out value="${versionCount}"/>','images/bt_Expand.gif');"><b><c:out value="${versionItem.crfName}"/>&nbsp;<c:out value="${versionItem.crfVersionName}"/></b></a>
	 </td>
   </tr>  
   <tr id="leftnavSubRow_SubSection<c:out value="${versionCount}"/>" style="display: all" valign="top">
	 <td colspan="2">
	   <div style="width: 600px">
         <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

         <div class="textbox_center" align="center">
	     <table border="0" cellpadding="0" cellspacing="0">
	      <tr>

			<td class="table_header_column_top"><fmt:message key="group_name" bundle="${resword}"/></td>
	       <td class="table_header_column_top"><fmt:message key="left_item_text" bundle="${resword}"/></td>  
           <td class="table_header_column_top"><fmt:message key="right_item_text" bundle="${resword}"/></td>   
           <td class="table_header_column_top"><fmt:message key="default_value" bundle="${resword}"/></td>
  		   <td class="table_header_column_top"><fmt:message key="response_layout" bundle="${resword}"/></td>
           <td class="table_header_column_top"><fmt:message key="response_label" bundle="${resword}"/></td>   
           <td class="table_header_column_top"><fmt:message key="response_options" bundle="${resword}"/></td>   
           <td class="table_header_column_top"><fmt:message key="response_values" bundle="${resword}"/></td>   
           <td class="table_header_column_top"><fmt:message key="required" bundle="${resword}"/></td>   
          </tr>
          <tr valign="top">            
           <c:choose>
		    <c:when test="${versionItem.groupLabel != 'Ungrouped'}">
		    	<td class="table_cell"><c:out value="${versionItem.groupLabel}"/></td> 
		    </c:when>
		    <c:otherwise>
		    	<td class="table_cell"><c:out value=""/></td> 
		    </c:otherwise>
		    </c:choose>              
           <td class="table_cell"><c:out value="${versionItem.leftItemText}"/>&nbsp;</td>  
           <td class="table_cell"><c:out value="${versionItem.rightItemText}"/>&nbsp;</td>           
           <td class="table_cell"><c:out value="${versionItem.defaultValue}"/>&nbsp;</td>             
           <td class="table_cell"><c:out value="${versionItem.responseLayout}"/>&nbsp;</td>     
           <td class="table_cell"><c:out value="${versionItem.responseSet.label}"/>&nbsp;</td>   
           <td class="table_cell">
            <c:forEach var="option" items="${versionItem.responseSet.options}" varStatus="status">
             <c:choose>
              <c:when test="${status.last}">
                <c:out value="${option.text}"/>
              </c:when>
              <c:otherwise>
                <c:out value="${option.text}"/>,
              </c:otherwise> 
             </c:choose>             
            </c:forEach>&nbsp;
           </td>  
           <td class="table_cell">
            <c:forEach var="option" items="${versionItem.responseSet.options}" varStatus="status">
             <c:choose>
              <c:when test="${status.last}">
                <c:out value="${option.value}"/>
              </c:when>
              <c:otherwise>
                <c:out value="${option.value}"/>,
              </c:otherwise> 
             </c:choose>               
            </c:forEach>&nbsp;
           </td>   
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

         </div></div></div></div></div></div></div></div>
         </div>
	  </td>
	</tr>
  
  </table>
  
  <br>
  <c:set var="versionCount" value="${versionCount+1}"/>
 </c:forEach>
 </td>
 </tr>
</table>
</body>
</html>

