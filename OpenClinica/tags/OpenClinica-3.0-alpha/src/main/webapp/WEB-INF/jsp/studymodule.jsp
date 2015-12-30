<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="pagemessage"/>

<html>
  <head><title>Study Module Prototype</title>
      <style type="text/css">
          .contenttable{
                  border-left: 1px solid #ccc;
                  border-top: 1px solid #ccc;

          }
          .contenttable tbody tr td, .contenttable thead td {
                  border-right: 1px solid #ccc;
                  border-bottom: 1px solid #ccc;
                     border-left: 0px solid #ccc;
                  border-top: 0px solid #ccc;

                  vertical-align:top;
                  text-align: left;
                  padding: 4px;
              }
      </style>
  </head>
  <body>
  <form action="studymodule" method="post">
  <div style="border: 1px solid #ccc; width:70%; padding-left:5px">
      <p>
          <fmt:message key="study_module_description_1" bundle="${pagemessage}">
              <fmt:param value="<img src='../images/bt_Edit.gif'/>"/>
              <fmt:param value="<img src='../images/bt_Details.gif'/>"/>
          </fmt:message>
      </p>
      <p>
          <fmt:message key="study_module_description_2" bundle="${pagemessage}"/>
      </p>
  </div>
  &nbsp;&nbsp;&nbsp;
  <table width="70%" class="contenttable" cellspacing="0" cellpadding="2">
      <thead>
        <td width="20"></td>
        <td width="220">TASK</td>
        <td width="120">STATUS</td>
        <td width="70">#'s of</td>
        <td width="110">Mark Complete</td>
        <td >Actions</td>
      </thead>
      <tbody>
        <tr>
            <td>1</td>
            <c:url var="studyUrl" value="/CreateStudy"/>
            <td>Create Study</td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.study == 3}">
                        Completed
                    </c:when>
                    <c:otherwise>
                        In Progress
                    </c:otherwise>
                </c:choose>
            </td>
            <td>n/a</td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.study == 3}">
                        n/a
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="study" value="3"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <c:url var="studyListUrl" value="/ListStudy"/>
            <td><a href="${studyListUrl}">
                <img src="../images/bt_Edit.gif" border="0"/></a></td>
        </tr>
        <tr>
            <td>2</td>
            <c:url var="crfUrl" value="/CreateCRFVersion"/>
            <td>Create CRF</td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.crf == 1}">
                        Not Started
                    </c:when>
                    <c:when test="${studyModuleStatus.crf == 2}">
                        In Progress
                    </c:when>
                    <c:otherwise>
                        Completed
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:out value="${crfCount}"/>
            </td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.crf == 3}">
                        n/a
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="crf" value="3"/>
                    </c:otherwise>
                </c:choose>

            </td>
            <c:url var="crfListUrl" value="/ListCRF"/>
            <c:url var="crfCreateUrl" value="/CreateCRFVersion"/>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.crf == 1}">
                        <a href="${crfCreateUrl}"><img src="../images/bt_Edit.gif" border="0"/></a>
                    </c:when>
                    <c:otherwise>
                        <a href="${crfListUrl}"><img src="../images/bt_Edit.gif" border="0"/></a>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td>
                3
            </td>
            <c:url var="eventUrl" value="/DefineStudyEvent"/>
            <td>Create Event Definitions</td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.eventDefinition == 1}">
                        Not Started
                    </c:when>
                    <c:when test="${studyModuleStatus.eventDefinition == 2}">
                        In Progress
                    </c:when>
                    <c:otherwise>
                        Completed
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:out value="${eventDefinitionCount}"/>
            </td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.eventDefinition == 3}">
                        n/a
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="eventDefinition" value="3"/>
                    </c:otherwise>
                </c:choose>

            </td>
            <c:url var="edListUrl" value="/ListEventDefinition"/>
            <td><a href="${edListUrl}"><img src="../images/bt_Edit.gif" border="0"/></a></td>
        </tr>
        <tr>
            <td>
                4
            </td>
            <c:url var="subGroupUrl" value="/CreateSubjectGroupClass"/>
            <td>Create Subject Group Classes</td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.subjectGroup == 1}">
                        Not Started
                    </c:when>
                    <c:when test="${studyModuleStatus.subjectGroup == 2}">
                        In Progress
                    </c:when>
                    <c:otherwise>
                        Completed
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:out value="${subjectGroupCount}"/>
            </td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.subjectGroup == 3}">
                        n/a
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="subjectGroup" value="3"/>
                    </c:otherwise>
                </c:choose>
                
            </td>
            <c:url var="crfListUrl" value="/ListCRF"/>
            <td><a href="${subGroupUrl}"><img src="../images/create_new.gif" border="0"/></a></td>
        </tr>
        <tr>
            <td>5</td>
            <c:url var="ruleUrl" value=""></c:url>
            <td>Create Rules</td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.rule == 1}">
                        Not Started
                    </c:when>
                    <c:when test="${studyModuleStatus.rule == 2}">
                        In Progress
                    </c:when>
                    <c:otherwise>
                        Completed
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:out value="${ruleCount}"/>
            </td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.rule == 3}">
                        n/a
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="rule" value="3"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <td></td>
        </tr>
      </tbody>
  </table>
  <br>
  <br>
  <c:if test="${studyModuleStatus.study == 3 && studyModuleStatus.crf == 3 && studyModuleStatus.eventDefinition == 3 && studyModuleStatus.subjectGroup && studyModuleStatus.rule}">
  <table width="70%" class="contenttable" cellspacing="0" cellpadding="2">
      <thead>

        <td width="20"></td>
        <td width="220">TASK</td>
        <td width="120">STATUS</td>
        <td width="70">#'s of</td>
        <td width="110">Mark Complete</td>
        <td>Actions</td>
      </thead>
      <tbody>
        <tr>
            <td>6</td>
            <c:url var="subGroupUrl" value="/CreateSubStudy"/>
            <td>Create Sites</td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.site == 1}">
                        Not Started
                    </c:when>
                    <c:when test="${studyModuleStatus.site == 2}">
                        In Progress
                    </c:when>
                    <c:otherwise>
                        Completed
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:out value="${siteCount}"/>
            </td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.site == 3}">
                        n/a
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="site" value="3"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <td><a href="${subGroupUrl}"><img src="../images/create_new.gif" border="0"/></a></td>
        </tr>
      </tbody>
  </table>
  <br>
  <br>

  <table width="70%" class="contenttable" cellspacing="0" cellpadding="2">
      <thead>
        <td width="20"></td>
        <td width="220">TASK</td>
        <td width="120">STATUS</td>
        <td width="70">#'s of</td>
        <td width="110">Mark Complete</td>
        <td>Actions</td>
      </thead>
      <tbody>
        <tr>
            <td>7</td>
            <c:url var="assignUrl" value="/AssignUserToStudy"/>
            <td>Assign Users</td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.users == 1}">
                        Not Started
                    </c:when>
                    <c:when test="${studyModuleStatus.users == 2}">
                        In Progress
                    </c:when>
                    <c:otherwise>
                        Completed
                    </c:otherwise>
                </c:choose>

            </td>
            <td>
                <c:out value="${userCount}"/>
            </td>
            <td>
                <c:choose>
                    <c:when test="${studyModuleStatus.users == 3}">
                        n/a
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="users" value="3"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <td><a href="${assignUrl}"><img src="../images/create_new.gif" border="0"/></a></td>
        </tr>

      </tbody>
  </table>
  </c:if>
  <div>
      <input type="submit" name="submitEvent" value="Save" class="button_long">
      <input type="button" name="cancel" value="Cancel" class="button_long">
  </div>
</form>  
</body>
</html>