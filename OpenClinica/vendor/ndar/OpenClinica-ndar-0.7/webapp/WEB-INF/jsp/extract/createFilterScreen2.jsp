<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/extract-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>

<h1><span class="title_extract">Create Filters: Instructions <a href="javascript:openDocWindow('help/3_4_createFilter_Help.html')"><img src="images/bt_Help_Extract.gif" border="0" alt="Help" title="Help"></a></span></h1>
<P>Throughout the next few screens you will create a new filter to use with the 
datasets. </P>

<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>

			<!-- These DIVs define shaded box borders -->
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

					<div class="textbox_center" align="center">

					<span class="title_submit">
					<b>Select<br> CRF</b><br><br>
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
					<b>Select<br> Section</b><br><br>
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
					<b>Select<br> Parameters</b><br><br>
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
					<b>Specify<br> Criteria</b><br><br>
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
					<b>Repeat<br> As<br> Necessary</b><br><br>
					</span>

					</div>

				</div></div></div></div></div></div></div></div>

			</td>
		</tr>
	</table>

	<br>
	<P>Create a filter to limit the subjects you retrieve as
	part of a dataset.  The filter allows you to select specific data elements
	from CRF data and describe the criteria to limit by.  You will proceed through
	the following steps:</P>
<ol>
<li>Select CRF

<li>Select Section.

<li>Select Parameters

<li>Select connector, values and operators for all parameters.

<li>Repeat steps 1-4 as many times as needed. 
</ol>
<form action="CreateFiltersTwo" type="post">
<input type="hidden" name="action" value="begin"/>
<input type="submit" value="Proceed to Create Filter" class="button_xlong"/>
</form>
<jsp:include page="../include/footer.jsp"/>