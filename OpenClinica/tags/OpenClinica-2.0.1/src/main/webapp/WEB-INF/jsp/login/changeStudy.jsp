<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/home-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
	
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="request" id="studies" class="java.util.ArrayList"/>
<jsp:useBean scope="session" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>

<h1>Change Your Current Study <a href="javascript:openDocWindow('help/1_3_changeStudy_Help.html')"><img src="images/bt_Help.gif" border="0" alt="Help" title="Help"></a></h1>

<c:choose>
 <c:when test="${study != null && study.id>0}">
  <p>Your current Active Study is <c:out value="${study.name}"/>, 
   <c:choose>
    <c:when test="${userRole.role.name !='invalid'}">
     with a role of <c:out value="${userRole.role.description}"/>.
    </c:when>
    <c:otherwise>
     but no role.
    </c:otherwise>
   </c:choose>
 </c:when>
<c:otherwise>
 <p> You currently don't have any Active Study.</p>
</c:otherwise>
</c:choose>


<c:choose>
<c:when test="${!empty studies}">
<form action="ChangeStudy" method="post">
<input type="hidden" name="action" value="confirm">
<p>Please choose a study in the following list:</P>
    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
     <table border="0" cellpadding="0" cellspacing="0" width="100%">
       <c:forEach var="studyRole" items="${studies}">   	 
        <c:choose> 	 
         <c:when test="${study.id == studyRole.studyId}">     
             
           <c:choose>
            <c:when test="${studyRole.parentStudyId > 0}">
               <c:if test="${studyRole.role.name != 'invalid'}">
                 <tr>
                   <td class="table_cell">&nbsp;&nbsp;<img src="images/bullet.gif">
                   <input type="radio" checked name="studyId" value="<c:out value="${studyRole.studyId}"/>">
                   <c:out value="${studyRole.studyName}"/> (<c:out value="${studyRole.role.description}"/>)</td>
                 </tr>
               </c:if>
            </c:when>
            <c:otherwise>
                <c:if test="${studyRole.role.name != 'invalid'}">
                 <tr>
                 <td class="table_cell"><input type="radio" checked name="studyId" value="<c:out value="${studyRole.studyId}"/>">
                 <b><c:out value="${studyRole.studyName}"/> (<c:out value="${studyRole.role.description}"/>)</b></td>
                 </tr>
               </c:if>
                <c:if test="${studyRole.role.name == 'invalid'}">
                 <tr><td class="table_cell"><b>&nbsp;<c:out value="${studyRole.studyName}"/></b></td></tr>
               </c:if>
            </c:otherwise>
           </c:choose>	 
         
          	 
         </c:when> 	 
         <c:otherwise> 	 
          <c:choose>
            <c:when test="${studyRole.parentStudyId > 0}">
               <c:if test="${studyRole.role.name != 'invalid'}">
                 <tr>
                  <td class="table_cell">&nbsp;&nbsp;<img src="images/bullet.gif"><input type="radio" name="studyId" value="<c:out value="${studyRole.studyId}"/>">
                  <c:out value="${studyRole.studyName}"/> (<c:out value="${studyRole.role.description}"/>)</td>
                 </tr>
               </c:if>
            </c:when>
            <c:otherwise>
                <c:if test="${studyRole.role.name != 'invalid'}">
                 <tr>
                  <td class="table_cell"><input type="radio" name="studyId" value="<c:out value="${studyRole.studyId}"/>">
                  <b><c:out value="${studyRole.studyName}"/> (<c:out value="${studyRole.role.description}"/>)</b></td>
                 </tr>
               </c:if>
                <c:if test="${studyRole.role.name == 'invalid'}">
                 <tr><td class="table_cell"><b>&nbsp;<c:out value="${studyRole.studyName}"/></b></td></tr>
               </c:if>
            </c:otherwise>
           </c:choose>	  	 
         </c:otherwise> 	 
        </c:choose>         
      
     </c:forEach> 	 
   
    <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyId"/></jsp:include> 
   </table>
   <br>
	</div>
	</div></div></div></div></div></div></div></div>
  <br>
  <p><input type="submit" name="Submit" value="Change Study" class="button_long"></p>


</form>
</c:when>
<c:otherwise>
  <p><i>No other studies and roles available.</i> <a href="MainMenu">Go Back</a></p>
</c:otherwise>
</c:choose>
<jsp:include page="../include/footer.jsp"/>
