<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<script language="JavaScript">
        function reportBug() {
            var bugtrack = "https://www.openclinica.com/OpenClinica/bug.php?version=<fmt:message key="version_number" bundle="${resword}"/>&user=";
            var user= "<c:out value="${userBeanURL.userName}"/>";
            bugtrack = bugtrack + user+ "&url=" + window.location.href;
            openDocWindow(bugtrack);
        }
        function confirmCancel(pageName){
            var confirm1 = confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>');
            if(confirm1){
                window.location = pageName;
            }
        }
        function confirmExit(pageName){
            var confirm1 = confirm('<fmt:message key="sure_to_exit" bundle="${resword}"/>');
            if(confirm1){
                window.location = pageName;
            }
        }
        function confirmExitAction( pageName, contextPath){
            var confirm1 = confirm('<fmt:message key="sure_to_exit" bundle="${resword}"/>');
            if(confirm1){
            	 var tform = document.forms["fr_cancel_button"];
            	tform.action=contextPath+"/"+pageName;
            	tform.submit();

            }
        }
        function goBack(){
            var confirm1 = confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>');
            if(confirm1){
                return history.go(-1);
            }
        }
        function lockedCRFAlert(userName){
            alert('<fmt:message key="CRF_unavailable" bundle="${resword}"/>'+'\n'
                    +'          '+userName+' '+'<fmt:message key="Currently_entering_data" bundle="${resword}"/>'+'\n'
                    +'<fmt:message key="Leave_the_CRF" bundle="${resword}"/>');
            return false;
        }
</script>

<c:set var="profilePage" value="${param.profilePage}"/>
<!--  If Controller Spring based append ../ to urls -->
<c:set var="urlPrefix" value=""/>
<c:set var="requestFromSpringController" value="${param.isSpringController}" />
<c:if test="${requestFromSpringController == '1' }">
    <c:set var="urlPrefix" value="../"/>
</c:if>
<c:if test="${requestFromSpringController == '2' }">
    <c:set var="urlPrefix" value="../../"/>
</c:if>

<!-- Main Navigation -->
     <div class="oc_nav">
        <div id="StudyInfo">
            <c:choose>
                <c:when test='${study.parentStudyId > 0}'>
                    <b><a href="${urlPrefix}ViewStudy?studyId=${study.parentStudyId}&viewFull=yes" title="<c:out value='${study.parentStudyName}'/>" alt="<c:out value='${study.parentStudyName}'/>" ><c:out value="${study.abbreviatedParentStudyName}" /></a>
                    :&nbsp;<a href="${urlPrefix}ViewSite?id=${study.id}" title="<c:out value='${study.name}'/>" alt="<c:out value='${study.name}'/>"><c:out value="${study.abbreviatedName}" /></a></b>
                </c:when>
                <c:otherwise>
                    <b><a href="${urlPrefix}ViewStudy?studyId=${study.id}&viewFull=yes" title="<c:out value='${study.name}'/>" alt="<c:out value='${study.name}'/>"><c:out value="${study.abbreviatedName}" /></a></b>
                </c:otherwise>
            </c:choose>
            (<c:out value="${study.abbreviatedIdentifier}" />)&nbsp;&nbsp;|&nbsp;&nbsp;
            <a href="${urlPrefix}ChangeStudy"><fmt:message key="change_study_site" bundle="${resworkflow}"/></a>
        </div>
        <div id="UserInfo">
            <a href="${urlPrefix}UpdateProfile"><b><c:out value="${userBeanURL.userName}" /></b> (<c:out value="${userBeanURL.currentRole.i18nDescription}" />)&nbsp;
				<c:set var="formatLocale"><fmt:message key="locale_string" bundle="${resformat}"/></c:set>
				
			   <c:choose>
                    <c:when test="${formatLocale == null}">
                        en
                    </c:when>
                    <c:otherwise>
                        <c:out value="${formatLocale}"/>
                    </c:otherwise>
                </c:choose>
            </a>&nbsp;|&nbsp;
            <a href="${urlPrefix}j_spring_security_logout"><fmt:message key="log_out" bundle="${resword}"/></a>
        </div>
        <br/><br style="line-height: 4px;"/>
        <div class="box_T1"><div class="box_L1"><div class="box_R1"><div class="box_B1"><div class="box_TL1"><div class="box_TR1"><div class="box_BL1"><div class="box_BR1">

            <div class="navbox_center">
                <!-- Top Navigation Row -->
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td>
                            <div id="bt_Home" class="nav_bt"><div><div><div>
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <td>
                                        <ul>
										<c:forEach  var="menuItem" items="${pageMenu.navBar}" >
										<c:set var="inputName" value="${menuItem.itemKey}" />
				
<li><a href="${urlPrefix}<c:out value="${menuItem.itemUrl}"/>">
               			<fmt:message key="${inputName}" bundle="${resword}"/>
               			</a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
			</c:forEach>
										 
                                        <li id="nav_Tasks" style="position: relative; z-index: 3;">
                                            <a href="#" onmouseover="setNav('nav_Tasks');" id="nav_Tasks_link"><fmt:message key="nav_tasks" bundle="${resword}"/> 
                                                <img src="${urlPrefix}images/bt_Tasks_pulldown.gif" alt="Tasks" border="0"/></a>
                                        </li>
                                        </ul>
                                    </td>
                                    <td align="right" style="font-weight: normal;">
                                        
                                        <form METHOD="GET" action="${urlPrefix}ListStudySubjects" onSubmit=" if (document.forms[0]['findSubjects_f_studySubject.label'].value == '<fmt:message key="study_subject_ID" bundle="${resword}"/>') { document.forms[0]['findSubjects_f_studySubject.label'].value=''}">
                                            <a href="javascript:reportBug()"><fmt:message key="openclinica_report_issue" bundle="${resword}"/></a>&nbsp;|&nbsp; 
                                            <a href="javascript:openDocWindow('<c:out value="${sessionScope.supportURL}" />')"><fmt:message key="openclinica_feedback" bundle="${resword}"/></a>&nbsp;&nbsp;
                                            <input type="text" name="findSubjects_f_studySubject.label" onblur="if (this.value == '') this.value = '<fmt:message key="study_subject_ID" bundle="${resword}"/>'" onfocus="if (this.value == '<fmt:message key="study_subject_ID" bundle="${resword}"/>') this.value = ''" value="<fmt:message key="study_subject_ID" bundle="${resword}"/>" class="navSearch" />
                                            <input type="hidden" name="navBar" value="yes"/>
                                            <input type="submit" value="<fmt:message key="go" bundle="${resword}"/>"  class="navSearchButton"/>
                                        </form>
                                        
                                    </td>
                                </tr>
                            </table>
                            </div></div></div></div>
                        </td>
                    </tr>
                                    </table>
            </div>
            <!-- End shaded box border DIVs -->
        </div></div></div></div></div></div></div></div></div>


            </td>
        </tr>
    </table>
    <!-- NAVIGATION DROP-DOWN -->


<div id="nav_hide" style="position: absolute; left: 0px; top: 0px; visibility: hidden; z-index: 2; width: 100%; height: 400px;">
    
<a href="#" onmouseover="hideSubnavs();"><img src="${urlPrefix}images/spacer.gif" alt="" width="1000" height="400" border="0"/></a>
</div>      

    
    </div>
    <img src="${urlPrefix}images/spacer.gif" width="596" height="1"><br>
<!-- End Main Navigation -->
<div id="subnav_Tasks" class="dropdown">
    <div class="dropdown_BG">
        
    	<c:forEach  var="taskMenuSection" items="${pageMenu.taskBar}" >
		<c:set var="isLeftColumn" value="${true}" />	
		
    	 <div class="taskGroup"><c:out value="${taskMenuSection.sectionName }"/></div>
			<c:forEach  var="menuItem" items="${taskMenuSection.sectionMenuItems}" varStatus="counter" begin="0" step="1">
			
    		<c:choose>
	          <c:when test="${isLeftColumn }">
	            	<div class="taskLeftColumn">
	          </c:when>
	          <c:otherwise>
	            	<div class="taskRightColumn">
	          </c:otherwise>
	        </c:choose>

              <div class="taskLink"><a href="${urlPrefix}<c:out value="${menuItem.itemUrl}"/>">
               			<c:out value="${menuItem.itemKey}"/></a> </div>
               			<c:set var="isLeftColumn" value="${!isLeftColumn}" />	
				
   </div>           
			</c:forEach>
			
			<br clear="all">	
		</c:forEach>	
		</div></div>
    </div>
</div>