
<!-- Alert Box -->

		

	<%
    String action = request.getParameter("action");
    if (action!=null) {
    if (action.equals("errorLogin")) { %>
    <!-- These DIVs define shaded box borders -->
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

				<div class="alertbox_center">
     Your Username and Password combination could not be found.
     Please try again. If you continue to have trouble, please 
     click "Forgot Password" or contact the Administrator.
     <br><br></div>

	</div></div></div></div></div></div></div></div>
     
    
      <%
     } 
     
}

%>           
         <jsp:include page="../include/showPageMessages.jsp" />
    		
	
	<!-- End Alert Box -->