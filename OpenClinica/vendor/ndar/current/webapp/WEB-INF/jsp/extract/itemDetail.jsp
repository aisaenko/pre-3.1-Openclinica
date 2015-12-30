<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<html>
<head>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
<script language="JavaScript" src="includes/global_functions_javascript.js" type="text/javascript"></script>
<style type="text/css">

.popup_BG { background-image: url(images/main_BG.gif);
	background-repeat: repeat-x;
	background-position: top;
	background-color: #FFFFFF;
	}


</style>

</head>
<body class="popup_BG">
<script language="JavaScript" type="text/javascript">
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
  <tr valign="top" ><td class="table_header_column">Item Name:</td><td class="table_cell">  
  <c:out value="${item.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column">Description:</td><td class="table_cell">  
  <c:out value="${item.description}"/>&nbsp;
  </td></tr> 
  <tr valign="top"><td class="table_header_column">Units:</td><td class="table_cell">  
  <c:out value="${item.units}"/>&nbsp;
  </td></tr> 
  <tr valign="top"><td class="table_header_column">PHI:</td><td class="table_cell">  
  <c:choose>
    <c:when test="${item.phiStatus}">
      Yes
    </c:when>
    <c:otherwise>
     No
    </c:otherwise>
  </c:choose>
  
  </td></tr> 
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br>

<span class="table_title_extract">Item Metadata</span>
<p>Click on each CRF version link below to see the corresponding item metadata table.</p>
<c:set var="versionCount" value="0"/>

 <c:forEach var="versionItem" items="${versionItems}">
  
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr>
	 <td valign="top" class="leftmenu"><a href="javascript:leftnavExpand('leftnavSubRow_SubSection<c:out value="${versionCount}"/>'); 
	   setImage('ExpandGroup<c:out value="${versionCount}"/>','images/bt_Collapse.gif');"><img 
	     name="ExpandGroup<c:out value="${versionCount}"/>" src="images/bt_Expand.gif" border="0"></a></td>
	 <td valign="top" class="leftmenu"><a href="javascript:leftnavExpand('leftnavSubRow_SubSection<c:out value="${versionCount}"/>'); 
	     setImage('ExpandGroup<c:out value="${versionCount}"/>','images/bt_Collapse.gif');"><b><c:out value="${versionItem.crfName}"/>&nbsp;<c:out value="${versionItem.crfVersionName}"/></b></a>
	 </td>
   </tr>  
   <tr id="leftnavSubRow_SubSection<c:out value="${versionCount}"/>" style="display: none" valign="top">
	 <td colspan="2">
	   <div style="width: 600px">
         <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

         <div class="textbox_center" align="center">
	     <table border="0" cellpadding="0" cellspacing="0">
	      <tr>
	       <td class="table_header_column_top">Left Item Text</td>  
           <td class="table_header_column_top">Right Item Text</td>   
           <td class="table_header_column_top">Response Label</td>   
           <td class="table_header_column_top">Response Options</td>   
           <td class="table_header_column_top">Response Values</td>   
           <td class="table_header_column_top">Required</td>   
          </tr>
          <tr valign="top">           
           <td class="table_cell"><c:out value="${versionItem.leftItemText}"/></td>  
           <td class="table_cell"><c:out value="${versionItem.rightItemText}"/>&nbsp;</td>   
           <td class="table_cell"><c:out value="${versionItem.responseSet.label}"/></td>   
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
               Yes 
             </c:when> 
             <c:otherwise>
               No
             </c:otherwise> 
            </c:choose>
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

