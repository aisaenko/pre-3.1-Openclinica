<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_submit">
					<c:choose>
					<c:when test='${param.selectCrf=="1"}'>
 						<b>Select<br> CRF</b>
						</c:when>
						<c:otherwise>
 						Select<br> CRF
						</c:otherwise> 
					</c:choose>
					<br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>
			<td><img src="images/arrow.gif" alt="==>" title=""></td>
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_submit">
					<c:choose>
					<c:when test='${param.selectSection=="1"}'>
 						<b>Select<br> Section</b>
						</c:when>
						<c:otherwise>
 						Select<br> Section
						</c:otherwise> 
					</c:choose>
					<br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>
			<td><img src="images/arrow.gif" alt="==>" title=""></td>
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_submit">
					<c:choose>
					<c:when test='${param.selectParameters=="1"}'>
 						<b>Select<br> Parameters</b>
						</c:when>
						<c:otherwise>
 						Select<br> Parameters
						</c:otherwise> 
					</c:choose>
					<br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>
			<td><img src="images/arrow.gif" alt="==>" title=""></td>
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_submit">
					<c:choose>
					<c:when test='${param.selectValue=="1"}'>
 						<b>Specify<br> Criteria</b>
						</c:when>
						<c:otherwise>
 						Specify<br> Criteria
						</c:otherwise> 
					</c:choose>
					<br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>
			<td><img src="images/arrow.gif" alt="==>" title=""></td>
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_submit">
					<c:choose>
					<c:when test='${param.save=="1"}'>
 						<b>Save<br> And<br> Exit</b>
						</c:when>
						<c:otherwise>
 						Save<br> And<br> Exit
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