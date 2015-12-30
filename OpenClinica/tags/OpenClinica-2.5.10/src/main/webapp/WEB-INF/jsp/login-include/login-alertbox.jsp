<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!-- Alert Box -->

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/> 

<% 
    String action = request.getParameter("action");
    if (action!=null) {
    if (action.equals("errorLogin")) { 
%>
    <!-- These DIVs define shaded box borders -->
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

				<div class="alertbox_center">
				<fmt:message key="password_failed" bundle="${restext}"/>
    
     <br><br></div>

	</div></div></div></div></div></div></div></div>
     
    
      <%
     } 
     
}

%>           
         <jsp:include page="../include/showPageMessages.jsp" />
    		
	
	<!-- End Alert Box -->
