
/* Return true, if the browser used is IE6 or IE7. */
function detectIEWindows(userAgentString) {
  return ((userAgentString.indexOf("MSIE") != -1) &&
          (userAgentString.indexOf("Windows") != -1) &&
          (userAgentString.indexOf("compatible") != -1));
}
/* Return true, if the browser used is Firefox on Windows. */
function detectFirefoxWindows(userAgentString) {
  return ((userAgentString.indexOf("Mozilla") != -1) &&
          (userAgentString.indexOf("Windows") != -1) &&
          (userAgentString.indexOf("Firefox") != -1));
}
/*change a button to a new CSS class if the button is in a disabled state.
 THIS METHOD IS USED BY LINES 306 AND 1168 in the file repetition-model.js*/
function changeBtnDisabledState(buttonObj,cssOnStateClass,
                                cssDisabledClass,onState){
  if(buttonObj == null || buttonObj == undefined) return;
  if(cssOnStateClass == null || cssOnStateClass == undefined) return;
  if(cssDisabledClass == null || cssDisabledClass == undefined) return;

  //change to Prototype extended object so we can use Prototype methods
  buttonObj = $(buttonObj);
  //alert('buttonObj.disabled '+buttonObj.disabled);
  if(buttonObj.disabled && ! onState)  {
    buttonObj.removeClassName(cssOnStateClass);
    buttonObj.addClassName(cssDisabledClass);
  }
  if((! buttonObj.disabled) && onState)  {
    buttonObj.removeClassName(cssDisabledClass);
    buttonObj.addClassName(cssOnStateClass);
  }
}

/*A METHOD CALLED BY THE WEB 2.0 FORMS JS LIBRARY, LINE 898.
 The method clears all the values in a new row added by this library.  All of the
 inputs values are set to empty Strings, or checked inputs are unchecked. */
function clearInputElementValues(trElement) {
  if(! trElement)  { return; }
  //if(! ((typeof trElement) == object))
  var _trElement = $(trElement);
  var tdElements = _trElement.getElementsBySelector('td');
  //variables representing all inputs, selects, textareas, and options
  //in the new row
  var inputs;
  var selects;
  var textareas;
  var options;
  if(tdElements){
    for(var i = 0; i < tdElements.length; i++) {
      if(tdElements[i]) {
        inputs = tdElements[i].getElementsBySelector('input');
        selects= tdElements[i].getElementsBySelector('select');
        textareas = tdElements[i].getElementsBySelector('textarea');
        if(inputs) {
          for(var j = 0; j < inputs.length; j++){
            if(inputs[j]){
              if(inputs[j].getAttribute("type") &&
                 (inputs[j].getAttribute("type").indexOf("checkbox") != -1 ||
                  inputs[j].getAttribute("type").indexOf("radio") != 1)){
                inputs[j].removeAttribute("checked");
                inputs[j].checked=false;
              }
              if(inputs[j].getAttribute("type") &&
                 inputs[j].getAttribute("type").indexOf("text") != -1) {
                inputs[j].setAttribute("value","");
              }

            }
          }
        }//end if inputs
        if(selects) {
          for(var h = 0; h < selects.length; h++){
            if(selects[h]){
              options = selects[h].getElementsBySelector("option");
              if(options){
                for(var k = 0; k < options.length; k++){
                  if(options[k]) {
                      options[k].selected=false;
                  }

                }
                 // for IE6
              if(detectIEWindows(navigator.userAgent)){
                  for(var p = 0; p < options.length; p++){
                    opt=document.createElement("option");
                    opt.selected=false;
                    opt.setAttribute("value",options[p].getAttribute("value"));
                    opt.innerHTML=options[p].innerHTML;
                    $(options[p]).remove();
                    selects[h].appendChild(opt);
                  }
               }
              }
             // selects[h].selectedIndex=0;
            }
          }

        }//end if selects
        if(textareas) {
          for(var m = 0; m < textareas.length; m++){
            if(textareas[m]) {
              textareas[m].innerHTML="";
            }
          }
        }
      }//end   if(tdElements[i])
    }
  }
}
/* Check radio buttons or checkboxes if the browser is IE6 or IE7, and
the input element's 'checked' attribute equals 'checked''. */
/*function enableDefaultCheckedElements(formObj){
  if(formObj == null || formObj == undefined) { return; }
  var radiosAndChecks = $(formObj).getElementsBySelector('input[type="radio"]','input[type="checkbox"]');
  for(var i = 0; i <= radiosAndChecks.length; i++) {
    if(radiosAndChecks[i] == null || radiosAndChecks[i] == undefined){
      continue;
    }
    var chked = radiosAndChecks[i].getAttribute("checked");
    //IE6 interprets the chked return value as a boolean; FF as a String (correctly)
    //like "checked"  -- "checked".indexOf(chked) != -1
    if(chked) {
      radiosAndChecks[i].defaultChecked=true;
    }
  }
}*/
/* Dynamically add a new parameter to a form. */
function submitWithNewParam(formElement,paramName,paramValue) {
  if(formElement == null || formElement == undefined) { return; }
  var hiddenElement = document.createElement("input");
  hiddenElement.setAttribute("type","hidden");
  hiddenElement.setAttribute("name",paramName);
  hiddenElement.setAttribute("value",paramValue);
  formElement.appendChild(hiddenElement);
  formElement.submit();

}
/* Change text in the sections tab of a data entry page, in order to show the
number of completed and total items in a form.*/
function updateTabs(tabId){
  /*updateTabs(<c:out value='${tabId}'/>) */
  if(tabId < 1)   { return; }
  var secNum = "secNumItemsCom"+(tabId-1);
  var tabSpan= $(secNum);
  if(tabSpan != null) {
    var totalFields = getDisplayedFormFields("mainForm");
    var itemCount = getItemCount("mainForm");
    var filledFldsCnt = calcFilledFormFields(totalFields);
    //(childTxt.substring(childTxt.indexOf("/"))
    tabSpan.childNodes[0].data="("+filledFldsCnt+"/"+itemCount+")";
  }
}
/* show or hide using prototype */
function show(objId){
  $(objId).show();
}
function hide(objId){
  $(objId).hide();
}
/* Taking care of IE6 bug vis a vis the repetition model JavaScript library. If
a radio button is clicked, it's sibling radios are unchecked.  The radioObject parameter is the
radio input element DOM object;  the configuration refers to the Strings "vertical"  or
"horizontal".  If the radio buttons have a horizontal configuration, then they are each locate din a different
TD tag, and the JavaScript has to iterate the DOM differently in order to uncheck the right radio button.
*/
function unCheckSiblings(radioObject,
                         configuration){
  var allSibs;
  if(configuration == null || configuration == undefined) { return;}
  if(radioObject == null || radioObject == undefined) { return;}

  if(configuration.indexOf('horizontal') == -1)  {
    allSibs =$(radioObject).siblings();
    if(allSibs == null || allSibs == undefined) { return;}

    for(var i = 0; i < allSibs.length; i++){
      if(allSibs[i].tagName.indexOf("INPUT") != -1 &&
         allSibs[i].getAttribute('type').indexOf('radio') != -1){
        allSibs[i].checked=false;
      }
    }
  } else {
    var name = radioObject.getAttribute("name");
    //Get radio elements in adjacent TD cells that have the same name
    //then uncheck them
    var allTDs = $(radioObject).up().siblings();
    var _elements;
    if(allTDs)  {
      for(var j = 0; j < allTDs.length; j++){
        if(allTDs[j])   {
          if($(allTDs[j]).childElements)  {
            _elements=$(allTDs[j]).childElements();
          } else {
            continue;
          }
          if(_elements)  {
            for(var k = 0; k < _elements.length; k++){
              if(_elements[k] && _elements[k].tagName.indexOf("INPUT") != -1 &&
                 _elements[k].getAttribute('type').indexOf('radio') != -1 &&
                 _elements[k].getAttribute('name') &&
                 _elements[k].getAttribute('name').indexOf(name) != -1){
                _elements[k].checked=false;
              }
            }
          }
        }  //for j=0
      }
    }//outer if

  }

}
/*A utility function that is used in the counting of items in a form. When using
the repetition model javascript library, a lot of input fields are generated that are
hidden, and shouldn't be counted as legitimate items when item-counting. This method
determines whether an input field is contained within one of these hidden rows, and
returns true if it is.*/
function isPartOfHiddenRepeater(inputObject){
  if(inputObject == null || inputObject == undefined){ return false;}
  var parentTr;
  var count=0;
  do {
    if(count == 0){
      parentTr = inputObject.parentNode;
      count++;
    } else {
      parentTr = parentTr.parentNode;
    }
  } while(parentTr.nodeName.indexOf("TR") == -1 && parentTr.nodeName.indexOf("tr") == -1)
  return (parentTr.style.display.indexOf("none") != -1);
}
/* count the number of items in a displayed form, for the purpose of displaying this
number on section tabs*/
function getItemCount(formObjectId){
  var theForm = document.getElementById(formObjectId);
  var itemCount=0;
  //var allFormItemsArr=new Array();
  var inputFields = $(theForm).getElementsBySelector('input[type="text"]');
  var checkFields = $(theForm).getElementsBySelector('input[type="checkbox"]');
  var radioFields = $(theForm).getElementsBySelector('input[type="radio"]');
  var selFields = theForm.getElementsByTagName("select");
  var txtAreaFields = theForm.getElementsByTagName("textarea");
  var typ;
  var _nodeName;
  var inputName;
  var currentName;

  for(var i = 0; i < inputFields.length; i++) {
    inputName=inputFields[i].getAttribute('name');

    if( inputName != null && inputName != undefined &&
        inputName.indexOf('interviewer') == -1 &&
        inputName.indexOf('interviewDate') == -1 &&
        (! isPartOfHiddenRepeater(inputFields[i])))  {
      itemCount++;
    }
  }
  for(var j = 0; j < selFields.length; j++) {
    inputName=selFields[j].getAttribute('name');
    if(inputName != null && inputName != undefined &&
       (inputName.indexOf('sectionName') != -1 ||
        inputName.indexOf('sectionSelect') != -1))  {
      continue;
    }
    if( ! isPartOfHiddenRepeater(selFields[j]))  {
      itemCount++;
    }
  }
  for(var h = 0; h < txtAreaFields.length; h++) {

    if( ! isPartOfHiddenRepeater(txtAreaFields[h]))  {
      itemCount++;
    }
  }
  //only count a group of sibling checkboxes as 1 item
  itemCount += countRelevantCheckRadios(checkFields);
  itemCount += countRelevantCheckRadios(radioFields);

  return itemCount;

}
/*A utility function that iterates an Array of checkboxes or radio buttons, and only counts
 one of a group of checkboxes or radio buttons as one item (i.e., 3 radio buttons displayed
 together = 1 item). The method is used as part of code that counts the numbe rof items
 in a form.*/
function countRelevantCheckRadios(checkRadArray){
  var itemCount=0;
  var inputName="";
  var  currentName="";
  for (var i = 0; i <= checkRadArray.length; i++) {
    if(checkRadArray[i] == undefined) { continue;}
    inputName=checkRadArray[i].getAttribute('name');
    if(i == 0){
      currentName = inputName;   }

    if(inputName.indexOf('markComplete') == -1 &&
       (! isPartOfHiddenRepeater(checkRadArray[i]))) {
      if(i == 0){
        itemCount++;
      } else {
        //If the checkbox has a different name then it's not a sibling
        if(inputName.indexOf(currentName) == -1)  {
          itemCount++;
          currentName = inputName;
        }
      }
    }
  }
  return itemCount;
}
/* When counting items or values for the section tabs, determine whether an item
is a select element that is used for the general UI, and not for a specific item. Return true
if the select element is used for UI manipulation, and not for an item.*/
function isUnrelatedSelectElement(selectElObject) {
  if(selectElObject == null || selectElObject == undefined)  { return true;}
  var val = selectElObject.value;
  if(val != null && val != undefined && val.indexOf("(select)") != -1 ) { return true;}

  var OptionsArr = $(selectElObject).getElementsBySelector("option");
  var tmp;
  for(var i = 0; i <= OptionsArr.length;i++) {
    if(OptionsArr[i] == null || OptionsArr[i] == undefined) { continue;}
    if(OptionsArr[i].childNodes){
      tmp = OptionsArr[i].childNodes[0].data;
    }
    if(tmp == null || tmp == undefined) { continue;}
    if(OptionsArr[i].selected && tmp.indexOf("(select)") != -1 ){
      return true;
    }
  }
  return false;

}
/* For the purpose of updating section tabs, count the number of items that are used
in a form, and return the number.*/
function getDisplayedFormFields(formObjectId) {
  //"mainForm"
  var theForm = document.getElementById(formObjectId);
  var valueCount=0;
  var allFormFieldsArr=new Array();
  var inputFields = theForm.getElementsByTagName("input");
  var selFields = theForm.getElementsByTagName("select");
  var txtAreaFields = theForm.getElementsByTagName("textarea");
  var typ;
  var _nodeName;
  var inputName;

  for(var i = 0; i < inputFields.length; i++) {
    typ=inputFields[i].getAttribute('type');
    inputName=inputFields[i].getAttribute('name');
    if(inputName == null || inputName == undefined) {
      inputName="";
    }

    if( typ != null && (typ.indexOf('text') != -1 || typ.indexOf('checkbox') != -1 ||
                        typ.indexOf('radio') != -1) &&
        inputName.indexOf('markComplete') == -1 &&
        inputName.indexOf('interviewer') == -1 &&
        inputName.indexOf('interviewDate') == -1 &&
        (! isPartOfHiddenRepeater(inputFields[i])))  {
      allFormFieldsArr.push(inputFields[i]);
    }
  }
  for(var j = 0; j < selFields.length; j++) {
    inputName=selFields[j].getAttribute('name');
    if(inputName != null &&
       (inputName.indexOf('sectionName') != -1 ||
        inputName.indexOf('sectionSelect') != -1))  {
      continue;
    }
    if( (! isPartOfHiddenRepeater(selFields[j])) &&
        (! isUnrelatedSelectElement(selFields[j])))  {

      allFormFieldsArr.push(selFields[j]);
    }
  }
  for(var h = 0; h < txtAreaFields.length; h++) {

    if( ! isPartOfHiddenRepeater(txtAreaFields[h]))  {
      allFormFieldsArr.push(txtAreaFields[h]);
    }
  }
  return allFormFieldsArr;
}
/* For the purpose of updating section tabs, count the number of input fields
that have a value, then return this number.*/
function calcFilledFormFields(formFieldsArray) {
  //var theForm = document.getElementById("mainForm");
  var valueCount=0;
  var typ;
  var _nodeName;
  var formValue;

  //Check for valid values
  for(var i = 0; i < formFieldsArray.length; i++) {
    typ=formFieldsArray[i].getAttribute('type');
    _nodeName = formFieldsArray[i].nodeName;
    formValue= formFieldsArray[i].value;
    //take care of selects
    if(_nodeName != null && _nodeName != undefined &&
       (_nodeName.indexOf("SELECT") != -1) &&
       formValue != null && formValue.length > 0 &&
       formValue.indexOf('(select)') == -1){
      // check option nodes here as well to make sure that
      //(select) nodes are not selected
      valueCount++;
      continue;
    }
    //take care of textareas
    if(_nodeName != null && _nodeName != undefined &&
       (_nodeName.indexOf("TEXTAREA") != -1) &&
       formValue != null && formValue != undefined &&
       formValue.length > 0 ){
      // alert("val:"+_nodeName);
      valueCount++;
      continue;
    }
    //take care of text inputs
    if(typ != null && typ != undefined  &&
       (typ.indexOf('checkbox') == -1 &&
        typ.indexOf('radio') == -1) &&
       _nodeName.indexOf("SELECT") == -1 &&
       _nodeName.indexOf("TEXTAREA") == -1 &&
       formValue != null && formValue.length > 0 ) {
      //alert("val text:"+_nodeName);
      valueCount++;
      continue;
    }
    if(isCheckedRadioOrCheckbox(formFieldsArray[i])){
      valueCount++;
    }
  }
  return valueCount;
}

function isCheckedRadioOrCheckbox(inputObject){
  if(inputObject == null || inputObject == undefined)  { return false; }
  var typ=inputObject.getAttribute('type');
  if(typ != null && (typ.indexOf('checkbox') != -1 ||
                     typ.indexOf('radio') != -1)){

    return inputObject.checked;

  }

  return false;
}
/* Only display the confirm dialogue box if the checkbox was checked
 when the user clicked it; then uncheck the checkbox if the user chooses "cancel"
 in the confirm dialogue. */
function displayMessageFromCheckbox(checkboxObject){
  if(checkboxObject != null && checkboxObject.checked){
    var bool =  confirm(
      "Marking this CRF complete will finalize data entry. You will no longer be able to add or modify data unless the CRF is reset by an administrator. If Double Data Entry is required, you or another user may need to complete this CRF again before it is verified as complete. Are you sure you want to mark this CRF complete?");
    if(! bool) { checkboxObject.checked=false; }
  }
}
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
  var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
  if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
  d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function layersShowOrHide() {
  var arrayArgs = layersShowOrHide.arguments;
  var objLayer;
  //e.g., visibility
  var strShowOrHide = arrayArgs[0];
  var i;

  for (i=1;i<=arrayArgs.length-1;i++) {
    if ((objLayer=MM_findObj(arrayArgs[i])) !=null) {
      // for IE and NS compatibility
      if (objLayer.style) { objLayer = objLayer.style; }
      objLayer.visibility = strShowOrHide;
    }
  }
}

function setImage(strImageName, strImageFullPath) {
  var objImage;

  objImage = MM_findObj(strImageName);
  if (objImage != null) { objImage.src = strImageFullPath; }
}

function popUp(strFileName, strTarget) {
  window.open(strFileName, strTarget, 'menubar=yes,toolbar=no,scrollbars=yes,resizable,width=700,height=450,screenX=0,screenY=0');
}

function newImage(arg) {
  if (document.images) {
    rslt = new Image();
    rslt.src = arg;
    return rslt;
  }
}

function changeImages() {
  if (document.images && (preloadFlag == true)) {
    for (var i=0; i<changeImages.arguments.length; i+=2) {
      document[changeImages.arguments[i]].src = changeImages.arguments[i+1];
    }
  }
}
var preloadFlag = false;
function preloadImages() {
  if (document.images) {
    bt_GO_h = newImage("/images/bt_GO_d.gif");
    preloadFlag = true;

  }
}

var isDOM = (document.getElementById ? true : false);
var isIE4 = ((document.all && !isDOM) ? true : false);
var isNS4 = (document.layers ? true : false);
function getRef(id) {
  if (isDOM) return document.getElementById(id);
  if (isIE4) return document.all[id];
  if (isNS4) return document.layers[id];
}
function getSty(id) {
  return (isNS4 ? getRef(id) : getRef(id).style);
}



function MM_jumpMenu(targ,selObj,restore){ //v3.0
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
}


/* Specifies the period of time between updates:
    month - once a month
    date - once per every day of the month (repeats the next month)
    weekday - once per every day of the week (repeats the next week)
    hour - once per hour (repeats the next day)
    request - once per browser request (default)
*/

var updatePeriods = new Array("month","date","weekday","hour","request")

// Invoked to display rotated HTML content in a Web page. The period
// argument should be an element of the updatePeriods array.

function displayRotatedContent(period) {
  var updatePeriod = -1
  for(var i=0;i<content.length;++i) {
    if(period.toLowerCase() == updatePeriods[i].toLowerCase()) {
      updatePeriod = i
      break
    }
  }
  var s = selectHTML(updatePeriod)
  document.write(s)
}

function selectHTML(updatePeriod) {
  var n = 0
  var max = content.length
  var d = new Date()
  switch(updatePeriod) {
    case 0: // Month (0 - 11)
      n = d.getMonth()
      break
    case 1: // Date (1 - 31 scaled to 0 - 30)
      n = d.getDate() - 1
      break
    case 2: // Weekday (0 - 6)
      n = d.getDay()
      break
    case 3: // Hour (0 - 23)
      n = d.getHours()
      break
    case 4: // Request (Default)
    default:
      n = selectRandom(max)
  }
  n %= max
  return content[n]
}

// Select a random integer that is between 0 (inclusive) and max (exclusive)
function selectRandom(max) {
  var r = Math.random()
  r *= max
  r = parseInt(r)
  if(isNaN(r)) r = 0
  else r %= max
  return r
}

//parts added 12-18-03, tbh
function confirmSaveAndContinue () {

  var yesno = confirm("Your data will now be saved to the database. This may take a minute or two; \nplease be patient and do not attempt to reload or make changes to the page. \nClick 'OK' to continue or 'Cancel' to return to the page without saving.","");
  //if (yesno == true) { alert ("OK was chosen"); } else { alert("Cancel was chosen"); }
  return yesno;
}

function disableAllButtons (theform) {

  if (document.all || document.getElementById) {
    for (i = 0; i < theform.length; i++) {
      var tempobj = theform.elements[i];
      if (tempobj.type.toLowerCase() == "submit" || tempobj.type.toLowerCase() == "reset") {
        tempobj.disabled = true;
      }
    }
  }

  //alert ("function has ended. getting ready to return");

  return true;
}

function submitFormDataConfirm (theform) {

  if (confirmSaveAndContinue()) {
    return disableAllButtons(theform);
  } else {
    return false;
  }
}

function submitFormReportCheck (theformlist) {
  var number = 0;
  for (i = 0; i < theformlist.length; i++) {
    if (theformlist[i].selected) number++;
  }
  //if (isNaN(number)) number = 0;
  if (number > 50) {
    alert("You are only allowed to choose up to a maximum of fifty (50) variables.  You have picked "+number+".  Please go back to the form and remove some of your selections.  For Data Dumps of more than 50 variables, please contact your Project Administrator or DBA.");
    return false;
  } else {
    return true;
  }
}


//---------------------added by jxu,10-15-2004------------------------

//-------------------------------------------------------------------------
// Function: setfocus
//
// Set the focus to the first form element.
//-------------------------------------------------------------------------

function setFocus() {

  var finished = false;
  var index = 0;
  if (document.forms[0] != null)
  {
    while ( finished == false )
    {
      if (document.forms[0].elements[index].type != 'hidden')
      {
        document.forms[0].elements[index].focus();
        finished = true;
      }

      index++;
    }
  }
}

//----------------------------------------------------
function trimString (str) {
  str = this != window? this : str;
  return str.replace(/^\s+/g, '').replace(/\s+$/g, '');
}


//-------------------------------------------------------------------------
// Function: getQueryVariable
//
// returns the value of a key/value pair from the page's URL 'GET' parameters
//-------------------------------------------------------------------------

function getQueryVariable(variable) {
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
    if (pair[0] == variable) {
      return unescape(pair[1]);
    }
  }
  return '';
}

//-------------------------------------------------------------------------
// Function: getName
//
// returns the 'id' attribute of a DOM element by its ID
//-------------------------------------------------------------------------

function getName(spanId) {

  obj = getRef(spanId);
  str = obj.getAttribute('id');
  str = trimString(str);
  return str;

}

//-------------------------------------------------------------------------
// Function: getContent
//
// returns the html/text content of a DOM element by its ID
//-------------------------------------------------------------------------

function getContent(spanId) {
  obj = getRef(spanId);
  str = obj.innerHTML;
  return str;
}

//-------------------------------------------------------------------------
// Function: openDNoteWindow
//
// Pops up a new browser window for discrepancy notes, including the validation error message text if applicable
//-------------------------------------------------------------------------

function openDNoteWindow(inURL, spanID,strErrMsg) {

  if (spanID) {
    //strErrMsg = getContent(spanID);
    // add the error message to the URL
    // encode it using 'escape'
    if (inURL.match(/\?/)) {
      if (inURL.match(/\?$/)) {
        newURL = inURL + 'strErrMsg=' + escape(strErrMsg);
      } else {
        newURL = inURL + '&strErrMsg=' + escape(strErrMsg);
      }
    } else {
      newURL = inURL + '?strErrMsg=' + escape(strErrMsg);
    }
  } else {
    newURL = inURL;
  }
  openNewWindow(newURL,
    'dnote_win',
    'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
    'dnote');

}
//--------------------------------------
//--pop up a window which is smaller 
//------------------------------------------
function openDSNoteWindow(inURL, spanID) {

  if (spanID) {
    strErrMsg = getContent(spanID);
    // add the error message to the URL
    // encode it using 'escape'
    if (inURL.match(/\?/)) {
      if (inURL.match(/\?$/)) {
        newURL = inURL + 'strErrMsg=' + escape(strErrMsg);
      } else {
        newURL = inURL + '&strErrMsg=' + escape(strErrMsg);
      }
    } else {
      newURL = inURL + '?strErrMsg=' + escape(strErrMsg);
    }
  } else {
    newURL = inURL;
  }
  openNewWindow(newURL,
    'dnote_win',
    'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
    'dsnote');

}


function openVNoteWindow(inURL) {

  openNewWindow(inURL,
    'def_win',
    'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
    'dnote');

}


//-------------------------------------------------------------------------
// Function: openNewWindow
//
// Pops up a new browser window containing the definitions page, and scrolls
//     to the correct spot
//-------------------------------------------------------------------------

function openDefWindow(inURL) {

  openNewWindow(inURL,
    'def_win',
    'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
    'small');

}

//-------------------------------------------------------------------------
// Function: openNctEntryWindow
//
// Pops up a new browser window containing the NCT Entry screen
//-------------------------------------------------------------------------

function openNctEntryWindow(inURL) {

  openNewWindow(inURL,
    '_blank',
    'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
    'medium');

}

//-------------------------------------------------------------------------
// Function: openDocWindow
//
// Pops up a new browser window containing a document, such as the 
// PRS Reference Guide.
//-------------------------------------------------------------------------

function openDocWindow(inURL) {

  openNewWindow(inURL,
    '_blank',
    'directories=no,location=no,menubar=yes,scrollbars=yes,toolbar=no,status=no,resizable=yes',
    'medium');

}

//-------------------------------------------------------------------------
// Function: openNewWindow
//
// Pops up a new browser window containing the definitions page, and scrolls
//     to the correct spot
//-------------------------------------------------------------------------

function openNewWindow(inURL, name, features, windowSize) {

  // Add check for browser capability
  var old_browser = true;
  if (window.screen != null) old_browser = false;

  if (features == "") {
    features = "toolbar=yes,directories=yes,location=1,status=yes,menubar=yes,scrollbars=yes,resizable=yes";
  }

  var height=250;
  var width=350;
  var screenHeight = 480;
  var screenWidth = 640;

  if(windowSize == 'small')
  {
    height = 150;
    width = 200;
  }
  if(windowSize == 'medium')
  {
    height = 300;
    width = 500;
  }
  if(windowSize == 'dnote')
  {
    height = 250;
    width = 200;
  }
  if(windowSize == 'dsnote')
  {
    height = 200;
    width = 200;
  }



  if (window.screen != null)
  {
    screenHeight = window.screen.height;
    screenWidth = window.screen.width;
  }

  if (screenWidth > 640)
  {
    width = width + (screenWidth - 640)*.50;
  }

  if(screenHeight > 480)
  {
    height = height + (screenHeight - 480)*.50;
  }

  features += ",width=" + width + ",height=" + height;

  var docView = window.open (inURL, name, features);

  docView.focus();


}


//-------------------------------------------------------------------------
// Function: MM_findObjInParentWin
//
// Finds the specified object in the parent window if it exists
//     Must be called from within a popup window opened by a parent window
//-------------------------------------------------------------------------

function MM_findObjInParentWin(strParentWinImageName) { //v4.0
  var objImage;

  if (window.opener && !window.opener.closed) {
    objImage = MM_findObj(strParentWinImageName, window.opener.document);
  }

  return objImage;
}

//-------------------------------------------------------------------------
// Function: setImageInParentWin
//
// Sets/changes the source file of an image in a parent window
//     Must be called from within a popup window that was opened by the parent window
//-------------------------------------------------------------------------

function setImageInParentWin(strParentWinImageName, strParentWinImageFullPath) {
  var objImage;

  if (window.opener && !window.opener.closed) {

    objImage = MM_findObjInParentWin(strParentWinImageName);
    if (objImage != null) { objImage.src = strParentWinImageFullPath; }

  }
}


// new functions for revised View Subjects screen 6-12-06

function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
  var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
  if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
  d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function setImage(strImageName, strImageFullPath) {
  var objImage;

  objImage = MM_findObj(strImageName);
  if (objImage != null) { objImage.src = strImageFullPath; }
}

function leftnavExpand(strLeftNavRowElementName){

  var objLeftNavRowElement;

  objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
  if (objLeftNavRowElement != null) {
    if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
    objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
  }
}






function layersShowOrHide() {
  var arrayArgs = layersShowOrHide.arguments;
  var objLayer;
  var strShowOrHide = arrayArgs[0];
  var i;

  for (i=1;i<=arrayArgs.length-1;i++) {
    if ((objLayer=MM_findObj(arrayArgs[i]))!=null) {
      // for IE and NS compatibility
      if (objLayer.style) { objLayer = objLayer.style; }
      objLayer.visibility = strShowOrHide;
    }
  }
}


/* 
Functions that swaps images.  These functions were generated by Dreamweaver, but are
not used by e-guana.
*/
function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
  if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

var isDOM = (document.getElementById ? true : false);
var isIE4 = ((document.all && !isDOM) ? true : false);
var isNS4 = (document.layers ? true : false);
function getRef(id) {
  if (isDOM) return document.getElementById(id);
  if (isIE4) return document.all[id];
  if (isNS4) return document.layers[id];
}
function getSty(id) {
  return (isNS4 ? getRef(id) : getRef(id).style);
}





function gotopage(){



  if(document.jumpform.category.options[document.jumpform.category.selectedIndex].value != ""){



    document.location.href = document.jumpform.category.options[document.jumpform.category.selectedIndex].value;}



}




// new functions for View Subjects status menus 9-13-06

function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
  var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
  if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
  d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function setImage(strImageName, strImageFullPath) {
  var objImage;

  objImage = MM_findObj(strImageName);
  if (objImage != null) { objImage.src = strImageFullPath; }
}

function leftnavExpand(strLeftNavRowElementName){

  var objLeftNavRowElement;

  objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
  if (objLeftNavRowElement != null) {
    if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
    objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
  }
}






function layersShowOrHide() {
  var arrayArgs = layersShowOrHide.arguments;
  var objLayer;
  var strShowOrHide = arrayArgs[0];
  var i;

  for (i=1;i<=arrayArgs.length-1;i++) {
    if ((objLayer=MM_findObj(arrayArgs[i]))!=null) {
      // for IE and NS compatibility
      if (objLayer.style) { objLayer = objLayer.style; }
      objLayer.visibility = strShowOrHide;
    }
  }
}


/* 
Functions that swaps images.  These functions were generated by Dreamweaver, but are
not used by e-guana.
*/
function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
  if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

var isDOM = (document.getElementById ? true : false);
var isIE4 = ((document.all && !isDOM) ? true : false);
var isNS4 = (document.layers ? true : false);
function getRef(id) {
  if (isDOM) return document.getElementById(id);
  if (isIE4) return document.all[id];
  if (isNS4) return document.layers[id];
}
function getSty(id) {
  return (isNS4 ? getRef(id) : getRef(id).style);
}





function gotopage(){



  if(document.jumpform.category.options[document.jumpform.category.selectedIndex].value != ""){



    document.location.href = document.jumpform.category.options[document.jumpform.category.selectedIndex].value;}



}


// new functions for revised View Subjects screen 9-13-06


function getObject( obj ) {

  // step 1
  if ( document.getElementById ) {
    obj = document.getElementById( obj );

    // step 2
  } else if ( document.all ) {
    obj = document.all.item( obj );

    //step 3
  } else {
    obj = null;
  }

  //step 4
  return obj;
}

function LockObject( obj, e ) {

  // step 1
  var tempX = 0;
  var tempY = 0;
  var offsetx = -17;
  var offsety = -15;
  var objHolder = obj;

  // step 2
  obj = getObject( obj );
  if (obj==null) return;

  // step 3
  if (!e) var e = window.event;
  if (e.pageX || e.pageY) 	{
		tempX = e.pageX;
		tempY = e.pageY;
  }
  else if (e.clientX || e.clientY) 	{
		tempX = e.clientX + document.body.scrollLeft
			+ document.documentElement.scrollLeft;
		tempY = e.clientY + document.body.scrollTop
			+ document.documentElement.scrollTop;
  }

  // step 4
  if (tempX < 0){tempX = 0}
  if (tempY < 0){tempY = 0}

  // step 5
  obj.style.top  = (tempY + offsety) + 'px';
  obj.style.left = (tempX + offsetx) + 'px';

  // step 6
  displayObject( objHolder, true );
}



function moveObject( obj, e ) {

  // step 1
  var tempX = 0;
  var tempY = 0;
  var offsetx = -2;
  var offsety = 10;
  var objHolder = obj;

  // step 2
  obj = getObject( obj );
  if (obj==null) return;

  // step 3
  if (!e) var e = window.event;
  if (e.pageX || e.pageY) 	{
		tempX = e.pageX;
		tempY = e.pageY;
  }
  else if (e.clientX || e.clientY) 	{
		tempX = e.clientX + document.body.scrollLeft
			+ document.documentElement.scrollLeft;
		tempY = e.clientY + document.body.scrollTop
			+ document.documentElement.scrollTop;
  }

  // step 4
  if (tempX < 0){tempX = 0}
  if (tempY < 0){tempY = 0}

  // step 5
  obj.style.top  = (tempY + offsety) + 'px';
  obj.style.left = (tempX + offsetx) + 'px';

  // step 6
  displayObject( objHolder, true );
}



function displayObject( obj, show ) {

  // step 1
  obj = getObject( obj );
  if (obj==null) return;

  // step 2
  obj.style.display = show ? 'block' : 'none';
  obj.style.visibility = show ? 'visible' : 'hidden';
}






