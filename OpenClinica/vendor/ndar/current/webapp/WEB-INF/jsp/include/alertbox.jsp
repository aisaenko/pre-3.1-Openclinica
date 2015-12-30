<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<!-- Alert Box -->

		<!-- These DIVs define shaded box borders -->
			
			<c:choose>
			<c:when test="${userBean!= null && userBean.id>0}">             
            <jsp:include page="../include/showPageMessages.jsp" />
            </c:when>
            <c:otherwise> 
             <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="alertbox_center">
            You have logged out of the application. Please go to the <a href="MainMenu">Login Page</a> in order to re-enter OpenClinica.
            
				<br><br></div>

			</div></div></div></div></div></div></div></div>
            
            </c:otherwise>
            </c:choose>
       

	<!-- End Alert Box -->