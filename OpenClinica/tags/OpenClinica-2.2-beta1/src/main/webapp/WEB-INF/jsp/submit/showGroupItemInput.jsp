<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />
<jsp:useBean scope="request" id="responseOptionBean" class="org.akaza.openclinica.bean.submit.ResponseOptionBean" />
<jsp:useBean scope='request' id='formMessages' class='java.util.HashMap'/>

<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" />
<c:set var="itemId" value="${displayItem.item.id}" />
<c:set var="numOfDate" value="${param.key}" />
<c:set var="isLast" value="${param.isLast}" />
<c:set var="repeatParentId" value="${param.repeatParentId}" />
<c:set var="rowCount" value="${param.rowCount}" />
<c:set var="inputName" value="${repeatParentId}_[${repeatParentId}]input${itemId}" />
<c:set var="parsedInputName" value="${repeatParentId}_${rowCount}input${itemId}" />
<c:set var="isHorizontal" value="${param.isHorizontal}" />
<c:set var="defValue" value="${param.defaultValue}" />


<c:if test="${isLast == false}">
  <c:set var="inputName" value="${repeatParentId}_${rowCount}input${itemId}" />
</c:if>

<%-- for tab index. must start from 1, not 0--%>
<c:set var="tabNum" value="${param.tabNum+1}" />

<%-- text input value --%>
<c:choose>
  <c:when test="${empty displayItem.metadata.responseSet.value}">
    <c:set var="inputTxtValue" value="${defValue}"/>
  </c:when>
  <c:otherwise>
    <c:set var="inputTxtValue" value="${displayItem.metadata.responseSet.value}"/>
  </c:otherwise>
</c:choose>

<c:forEach var="frmMsg" items="${formMessages}">
  <c:if test="${frmMsg.key eq parsedInputName}">
    <c:set var="isInError" value="${true}" />
    <c:set var="errorTxtMessage" value="${frmMsg.value}" />
  </c:if>
</c:forEach>

<c:if test='${inputType == "text"}'>
  <%-- add for error messages --%>
  <label for="<c:out value="${parsedInputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>" />
    </c:when>
    <c:otherwise>
      <input id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange=
        "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>" />
    </c:otherwise>
  </c:choose>
  <c:if test="${displayItem.item.itemDataTypeId==9}"><!-- date type-->
    <A HREF="#" onClick="var sib = getSib(this.previousSibling);cal1.select(sib,'anchor<c:out value="${inputName}"/>','MM/dd/yyyy'); return false;" TITLE="cal1.select(document.forms[0].<c:out value="${parsedInputName}"/>,'anchor<c:out value="${inputName}"/>','MM/dd/yyyy'); return false;" NAME="anchor<c:out value="${inputName}"/>" ID="anchor<c:out value="${inputName}"/>"><img src="images/bt_Calendar.gif" alt="Format: MM/DD/YYYY" title="Format: MM/DD/YYYY" border="0" /></a>
    <c:set var="numOfDate" value="${numOfDate+1}"/>
  </c:if>
</c:if>
<c:if test='${inputType == "textarea"}'>
  <label for="<c:out value="${parsedInputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><textarea class="aka_input_error" id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="<c:out value="${inputName}"/>" rows="5" cols="40"><c:out value="${inputTxtValue}"/></textarea>
    </c:when>
    <c:otherwise>
      <textarea id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="<c:out value="${inputName}"/>" rows="5" cols="40"><c:out value="${inputTxtValue}"/></textarea>
    </c:otherwise>
  </c:choose>
</c:if>
<c:if test='${inputType == "checkbox"}'>
  <c:if test="${! isHorizontal}">
    <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
      <c:choose>
        <c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
        <c:when test="${option.text eq inputTxtValue}"><c:set var="checked" value="checked" />
        </c:when>
        <c:otherwise><c:set var="checked" value="" /></c:otherwise>
      </c:choose>
      <label for="<c:out value="${parsedInputName}"/>"></label>
      <c:choose>
        <c:when test="${isInError}">
          <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <br/>
        </c:when>
        <c:otherwise>
          <input id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <br/>
        </c:otherwise>
      </c:choose>
    </c:forEach>
  </c:if>
  <c:if test="${isHorizontal}">
    <%-- only one respOption displayed here, one per TD cell --%>
    <c:choose>
      <c:when test="${responseOptionBean.selected}"><c:set var="checked" value="checked" /></c:when>
      <c:when test="${responseOptionBean.text eq inputTxtValue}"><c:set var="checked" value="checked" />
      </c:when>
      <c:otherwise><c:set var="checked" value="" /></c:otherwise>
    </c:choose>
    <label for="<c:out value="${parsedInputName}"/>"></label>
    <c:choose>
      <c:when test="${isInError}">
        <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:when>
      <c:otherwise>
        <input id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:otherwise>
    </c:choose>

  </c:if>
</c:if>
<c:if test='${inputType == "radio"}'>
  <c:if test="${! isHorizontal}">
    <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
      <c:choose>
        <c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
        <c:when test="${option.text eq inputTxtValue}"><c:set var="checked" value="checked" />
      </c:when>
        <c:otherwise><c:set var="checked" value="" /></c:otherwise>
      </c:choose>
      <label for="<c:out value="${parsedInputName}"/>"></label>
      <c:choose>
        <c:when test="${isInError}">
          <!-- this.className='changedField';-->
          <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'vertical');}" onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /><c:if test="${! isHorizontal}"><c:out value="${option.text}" /></c:if> <br/>
        </c:when>
        <c:otherwise>
          <input id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'vertical');}" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:if test="${! isHorizontal}"><c:out value="${option.text}" /></c:if> <br/>
        </c:otherwise>
      </c:choose>
    </c:forEach>
  </c:if>
  <c:if test="${isHorizontal}">
    <c:choose>
      <c:when test="${responseOptionBean.selected}"><c:set var="checked" value="checked" /></c:when>
      <c:when test="${responseOptionBean.text eq inputTxtValue}"><c:set var="checked" value="checked" />
      </c:when>
      <c:otherwise><c:set var="checked" value="" /></c:otherwise>
    </c:choose>
    <%-- Only have one of these per radio button--%>
    <label for="<c:out value="${parsedInputName}"/>"></label>
    <c:choose>
      <c:when test="${isInError}">
        <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'horizontal');}" onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:when>
      <c:otherwise>
        <input id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'horizontal');}" onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:otherwise>
    </c:choose>
  </c:if>
</c:if>
<c:if test='${inputType == "single-select"}'>
  <label for="<c:out value="${parsedInputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><select class="aka_input_error" id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="<c:out value="${inputName}"/>" class="formfield"></select>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${displayItem.metadata.defaultValue != '' && displayItem.metadata.defaultValue != null}">
          <c:set var="printDefault" value="true"/>
        </c:when>
        <c:otherwise><c:set var="printDefault" value="false"/></c:otherwise>
      </c:choose>
      <c:set var="selectedOption" value="-1"/>
      <c:set var="count" value="0"/>
      <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
        <c:if test="${option.selected}"><c:set var="selectedOption" value="${count}" /></c:if>
        <c:if test="${printDefault=='true'}">
          <c:if test="${displayItem.metadata.defaultValue == option.text || displayItem.metadata.defaultValue == option.value}">
            <c:set var="printDefault" value="false"/>
            <c:if test="${selectedOption==-1}"><c:set var="selectedOption" value="${count}"/></c:if>
          </c:if>
        </c:if>
        <c:set var="count" value="${count+1}"/>
      </c:forEach>
      <select id="<c:out value="${parsedInputName}"/>" tabindex="<c:out value="${tabNum}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="<c:out value="${inputName}"/>" class="formfield">
        <c:choose>
          <c:when test="${printDefault == 'true'}">
            <c:set var="count" value="0"/>
            <option value="<c:out value="" />" <c:out value=""/> ><c:out value="${displayItem.metadata.defaultValue}" /></option>
            <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
              <c:choose>
                <c:when test="${count==selectedOption}"><c:set var="checked" value="selected" /></c:when>
                <c:otherwise><c:set var="checked" value="" /></c:otherwise>
              </c:choose>
              <option value="<c:out value="${option.value}" />" <c:out value="${checked}"/> ><c:out value="${option.text}" /></option>
              <c:set var="count" value="${count+1}"/>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <c:set var="count" value="0"/>
            <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
              <c:choose>
                <c:when test="${count==selectedOption}"><c:set var="checked" value="selected" /></c:when>
                <c:otherwise><c:set var="checked" value="" /></c:otherwise>
              </c:choose>
              <option value="<c:out value="${option.value}" />" <c:out value="${checked}"/> ><c:out value="${option.text}" /></option>
              <c:set var="count" value="${count+1}"/>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </select>
    </c:otherwise>
  </c:choose>
</c:if>
<c:if test='${inputType == "multi-select"}'>
  <label for="<c:out value="${parsedInputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><select  class="aka_input_error" id="<c:out value="${parsedInputName}"/>" multiple  tabindex=
      "<c:out value="${tabNum}"/>" name="<c:out value="${inputName}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');">
    </c:when>
    <c:otherwise>
      <select id="<c:out value="${parsedInputName}"/>" multiple  tabindex=
      "<c:out value="${tabNum}"/>" name="<c:out value="${inputName}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');">
    </c:otherwise>
  </c:choose>
  <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
    <c:choose>
      <c:when test="${option.selected}"><c:set var="checked" value="selected" /></c:when>
      <c:when test="${option.text eq inputTxtValue}"><c:set var="checked" value="selected" />
      </c:when>
      <c:otherwise><c:set var="checked" value="" /></c:otherwise>
    </c:choose>
    <option value="<c:out value="${option.value}" />" <c:out value="${checked}"/> ><c:out value="${option.text}" /></option>
  </c:forEach>
  </select>
</c:if>
<c:if test="${displayItem.metadata.required}">
  <span class="alert">*</span>
</c:if>
<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
  <c:choose>
    <c:when test="${displayItem.numDiscrepancyNotes > 0}">
      <c:set var="imageFileName" value="icon_Note" />
    </c:when>
    <c:otherwise>
      <c:set var="imageFileName" value="icon_noNote" />
    </c:otherwise>
  </c:choose>
  <a tabindex="<c:out value="${tabNum + 1000}"/>" href="#" onClick=
    "openDNoteWindow('CreateDiscrepancyNote?id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${parsedInputName}"/>&column=value','spanAlert-<c:out value="${parsedInputName}"/>','<c:out value="${errorTxtMessage}"/>'); return false;"
    ><img name="flag_<c:out value="${parsedInputName}"/>" src=
    "images/<c:out value="${imageFileName}"/>.gif" border="0" alt="Discrepancy Note" title="Discrepancy Note"
    ></a>
</c:if>
<%-- we won't need this if we're not embedding error messages
<br><c:import url="../showMessage.jsp"><c:param name="key" value=
              "${inputName}" /></c:import>    --%>