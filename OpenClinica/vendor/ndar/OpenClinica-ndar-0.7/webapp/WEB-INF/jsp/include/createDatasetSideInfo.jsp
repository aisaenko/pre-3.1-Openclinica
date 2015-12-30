<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%--<jsp:useBean scope="session" id="panel" class="org.akaza.openclinica.view.StudyInfoPanel" />--%>


<!-- Sidebar Contents after alert-->

	
<c:choose>
 <c:when test="${userBean != null && userBean.id>0}">	
	<tr id="sidebar_Info_open">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Info_open'); leftnavExpand('sidebar_Info_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10" alt="-" title=""></a>

		<b>Info</b>
   
		<div class="sidebar_tab_content">

	  <c:if test="${panel.createDataset}">   
			<span style="color: #789EC5">

        <c:import url="../include/createDatasetSide.jsp"/>
			</span>
      </c:if>  
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
     
   	</div>

	</td>
	</tr>
	<tr id="sidebar_Info_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Info_open'); leftnavExpand('sidebar_Info_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10" alt="v" title=""></a>

		<b>Info</b>

		</td>
	</tr>   
  
  
  
  <c:if test="${panel.iconInfoShown}">
	 <c:import url="../include/sideIcons.jsp"/>
	</c:if>
</table>	
 	
</c:when>
<c:otherwise>
    <br><br>
	<a href="MainMenu">Login</a>	
	<br><br>
	<!-- <a href="RequestAccount">Request an Account</a> -->
	<br><br>
	<!-- <a href="RequestPassword">Forgot Password?</a> -->
</c:otherwise>
</c:choose>


<!-- End Sidebar Contents -->

				<br><img src="images/spacer.gif" width="120" height="1" alt="">

				</td>
				<td class="content" valign="top">

