<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%--
Usage:

jsp:include page=""
jsp:param name="selectStudyEvents" value="1"
/jsp:include

--%>
<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_extract">
					<c:choose>
						<c:when test='${param.selectStudyEvents=="1"}'>
 						<b>Select Items<br> or<br>Event/Subject<br> Attributes </b>
						</c:when>
						<c:otherwise>
 						Select Items<br> or<br>Event/Subject<br> Attributes
						</c:otherwise> 
					</c:choose>
					<br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>
			<td><img src="images/arrow.gif"></td>
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_extract">
					<c:choose>
						<c:when test='${param.longitudinalScope=="1"}'>
 						<b>Define<br> Temporal<br> Scope</b>
						</c:when>
						<c:otherwise>
 						Define<br> Temporal<br> Scope
						</c:otherwise> 
					</c:choose>
					
					
					<br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>
			<td><img src="images/arrow.gif"></td>
			<!--<td>-->

			<!-- These DIVs define shaded box borders -->
			<!--	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">-->

			<!--	<div class="textbox_center" align="center">-->

			<!--	<span class="title_extract">-->
			<%--		<c:choose>
						<c:when test='${param.chooseFilter=="1"}'>
 						<b>Choose<br> Filter</b>
						</c:when>
						<c:otherwise>
 						Choose<br> Filter
						</c:otherwise> 
					</c:choose>
			--%>
			<!--	<br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>-->
			<!--<td><img src="images/arrow.gif"></td>-->
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_extract">
					<c:choose>
						<c:when test='${param.specifyMetadata=="1"}'>
 						<b>Specify<br> Dataset<br> Properties</b>
						</c:when>
						<c:otherwise>
 						Specify<br> Dataset<br> Properties
						</c:otherwise> 
					</c:choose>
					
					<br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>
			<td><img src="images/arrow.gif"></td>
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_extract">
					<c:choose>
						<c:when test='${param.saveAndExport=="1"}'>
						<b>Save<br> And<br> Export</b>
						</c:when>
						<c:otherwise>
 						Save<br> And<br> Export
						</c:otherwise> 
					</c:choose>
					
					
					<br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>
		</tr>
	</table>

	<br>