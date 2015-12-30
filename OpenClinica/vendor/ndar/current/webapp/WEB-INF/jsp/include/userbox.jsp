<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<!-- User Box -->
 	<c:if test="${userBean != null && userBean.id > 0}">
	<div align="right" class="userbox">

	<!-- These DIVs define shaded box borders -->
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

			<div class="userboxtext_center">

	<!-- User Box contents -->

			<div class="userboxtext">		

				<b>User:</b>&nbsp; <c:out value="${userBean.name}" />
				<br>
				<c:choose>
				<c:when test='${(isAdminServlet == "admin" && userBean.techAdmin) || (!userRole.role.active && userBean.techAdmin)}'>
				<b>Technical Administrator</b>
	            </c:when>
	            <c:when test='${(isAdminServlet == "admin" && userBean.sysAdmin) || (!userRole.role.active && userBean.sysAdmin)}'>
				<b>Business Administrator</b>
	            </c:when>
	            
	            <c:otherwise>
				<b>Study:</b>&nbsp; <c:out value="${study.name}" />
				
				<br>
				<b>Protocol ID:</b>&nbsp; <c:out value="${study.identifier}" />
				<br>
				<b>Role:</b>&nbsp; <c:out value="${userRole.role.description}" />
				<br>
                </c:otherwise>
				</c:choose>
				<img src="images/UserBox_line.gif" width="161" height="1" vspace="4"><br>

				<div style="position: absolute; z-index: 3;">
				<span class="logout"><a href="ChangeStudy">Change Study/Site</a></span>
				<span class="logout"><a href="Logout">Logout</a></span>
	          	</div>
	          	<br>
				</div>

	<!-- End User Box contents -->

			<br></div>

		</div></div></div></div></div></div></div></div>

	</div>
  </c:if>
<!-- End User Box -->

				</td>
			</tr>
		</table>
<!-- End Header Table -->