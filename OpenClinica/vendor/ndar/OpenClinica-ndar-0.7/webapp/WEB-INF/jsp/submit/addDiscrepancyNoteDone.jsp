<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean scope='request' id='strResStatus' class='java.lang.String' />
<jsp:useBean scope='request' id='writeToDB' class='java.lang.String' />

<html>
<head>
<title>OpenClinica - Add Discrepancy Note</title>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
<script language="JavaScript" src="includes/global_functions_javascript.js" type="text/javascript"></script>

<script language="JavaScript" src="includes/CalendarPopup.js" type="text/javascript"></script>
<style type="text/css">

.popup_BG { background-image: url(images/main_BG.gif);
	background-repeat: repeat-x;
	background-position: top;
	background-color: #FFFFFF;
	}


</style>
</head>
<body class="popup_BG" onLoad="javascript:setImageInParentWin('flag_<c:out value="${discrepancyNote.field}"/>','images/icon_Note.gif');">
<div class="table_title_Submit">Add Discrepancy Note</div>
<div class="alert">    
<c:forEach var="message" items="${pageMessages}">
 <c:out value="${message}" escapeXml="false"/> 
</c:forEach>
</div>
<div class="alert">  
 ATTENTION: You must complete and submit the form in the main window to add this note to the database. Otherwise your note will not be saved.
</div>
    <table border="0">       
     
        <c:if test="${hasNotes == 'yes'}">
        <tr valign="top">
            <td colspan="2"><a href="ViewDiscrepancyNote?id=<c:out value="${discrepancyNote.entityId}"/>&name=<c:out value="${discrepancyNote.entityType}"/>&field=<c:out value="${discrepancyNote.field}"/>&column=<c:out value="${discrepancyNote.column}"/>">
            view parent and related notes(s)</a> 
           </td>         
        </tr>
       </c:if> 
    </table>
    <table border="0"> 
     <tr>     
      <td><input type="submit" name="B1" value="Close" class="button_medium" onclick="javascript:window.close();"></td> 
    </tr>
    </table>  
</html>
