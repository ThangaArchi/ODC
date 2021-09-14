
var selectedList;
var availableList;


var whtSpEnds = new RegExp("^\\s*|\\s*$", "g");
var whtSpMult = new RegExp("\\s\\s+", "g");

function normalizeString(s) {

  s = s.replace(whtSpMult, " ");  // Collapse any multiple whites space.
  s = s.replace(whtSpEnds, "");   // Remove leading or trailing white space.

  return s;
}


function getElementByName(name) {
  
   elms = document.getElementsByName(name);
   if (elms && elms.length > 0) {
      return elms[0];
   }
   return null;
}


function createListObjects(){
    clearthis();
    availableList = getElementByName("savedList");
    selectedList = getElementByName("sendtoList"); 
}



function addEvent(elm, evType, fn, useCapture)
{
  if (elm.addEventListener){
   
    elm.addEventListener(evType, fn, useCapture);
    return true;
  } else if (elm.attachEvent){
  
    var r = elm.attachEvent("on"+evType, fn);
    return r;
  } else {
    alert("Handler could not be removed");
  }
} 

function deSelSelectedList()
{ 
   selectedList.selectedIndex = -1;
}

function deSelAvailableList()
{  
    availableList.selectedIndex = -1;   
}



function viewGroupInformation()
{
var toJSPVar=getListSelectedListItem();


if (toJSPVar.indexOf("[GROUP]") == 0 )
{ 
  modalWin(toJSPVar);
}
if (toJSPVar.indexOf("[USER]") == 0 )
{ 
  modalWinErrMess();
}
if (toJSPVar.indexOf("nogroup") == 0 )
{ 
  modalWinErrMess();
}


}

function modalWin(toJSPVar) { 

if (window.showModalDialog) { 
window.showModalDialog("jsp/GroupInfo.jsp?jspVAR="+toJSPVar,"name","dialogWidth:400px;dialogHeight:350px;center:yes;resizable:yes"); 

} else { 

var x=(screen.width-350)/2;
var y=(screen.height-255)/2;
window.open('jsp/GroupInfo.jsp?jspVAR='+toJSPVar,'name','height=255,width=350,toolbar=no,directories=no,status=no,menubar=no,screenX='+x+',screenY='+y+',scrollbars=no,resizable=yes,modal=yes'); 

} 
} 

function modalWinErrMess() { 

if (window.showModalDialog) { 
window.showModalDialog("jsp/GroupsInfoError.jsp","name","dialogWidth:400px;dialogHeight:350px;center:yes;resizable:yes"); 

} else { 

var x=(screen.width-350)/2;
var y=(screen.height-255)/2;
window.open("jsp/GroupsInfoError.jsp",'name','height=255,width=350,toolbar=no,directories=no,status=no,menubar=no,screenX='+x+',screenY='+y+',scrollbars=no,resizable=yes,modal=yes'); 

} 
} 

function getListSelectedListItem()
{

 
 var selItem=new String('nogroup');
 var addIndex;
 addIndex = availableList.selectedIndex;
  if(addIndex < 0)
  {
    addIndex = selectedList.selectedIndex;
    if(addIndex < 0)
    {
      return selItem;
    }
    else 
    {
     selItem=selectedList.item(addIndex).value;
     return selItem;
    }
    return selItem;
  }
  else 
   {
     selItem=availableList.item(addIndex).value;
     return selItem;
    }
}








function delRecipient()
{
  
	var selIndex = selectedList.selectedIndex;
	if(selIndex < 0)
      return;
     selectedList.remove(selIndex);
     selectNone(selectedList,availableList);
	 setSize(availableList,selectedList);
   	 updateAclField();
}


function checkIfAttributeUsrOrGrpExistsInSelectedList(userOrgroup)
 {
  var returnValue = false;
   
   var len = selectedList.length;
 	 for(i=0; i<len; i++){
       
        var listitem=normalizeString(selectedList.item(i).value);
        userOrgroup=normalizeString(userOrgroup);
        
        if ( userOrgroup == listitem )
          returnValue = true;
    }
   
  return returnValue;
 }
 
 function checkIfAttributeUsrOrGrpExistsInAvailableList(userOrgroup)
 {
  var returnValue = false;
   
   var len = availableList.length;
 	 for(i=0; i<len; i++){
       
        var listitem=normalizeString(availableList.item(i).value);
        userOrgroup=normalizeString(userOrgroup);
        
        if ( userOrgroup == listitem )
          returnValue = true;
    }
   
  return returnValue;
 }



function delAttribute(){
   var selIndex = selectedList.selectedIndex;
   if(selIndex < 0)
      return;

    //  check if an items already appended in a list not to be added....
  	 var dupExists=checkIfAttributeUsrOrGrpExistsInAvailableList(selectedList.options.item(selIndex).value);
 	 if ( dupExists == false )
  	 { 
	   availableList.appendChild(selectedList.options.item(selIndex))
	   selectNone(selectedList,availableList);
	   setSize(availableList,selectedList);
	   updateAclField();
	 }else
	 {
	   selectedList.remove(selIndex);
	   selectNone(selectedList,availableList);
	   setSize(availableList,selectedList);
	 } 
	 
}

function addAttribute(){
   
 
   var addIndex = availableList.selectedIndex;
   if(addIndex < 0)
      return;
    
      // check if an items already appended in a list not to be added....
  	 var dupExists=checkIfAttributeUsrOrGrpExistsInSelectedList(availableList.options.item(addIndex).value);
 	 if ( dupExists == false )
  	 {        
	   selectedList.appendChild(availableList.options.item(addIndex));
	   selectNone(selectedList,availableList);
	   setSize(selectedList,availableList);
	   updateAclField();
	 }else
	 {
	   availableList.remove(addIndex);
	   selectNone(selectedList,availableList);
	   setSize(selectedList,availableList);
	 } 
}

function setTop(top){
	document.getElementById
      ('someLayer').style.top = top;
}
function setLayerTop(lyr,top){
	lyr.style.top = top;
}

function setSize(list1,list2){
    list1.size = getSize(list1);
    list2.size = getSize(list2);
}

function selectNone(list1,list2){
    list1.selectedIndex = -1;
    list2.selectedIndex = -1;
    addIndex = -1;
    selIndex = -1;
}


function getSize(list){
    /* Mozilla ignores whitespace, 
       IE doesn't - count the elements 
       in the list */
    var len = list.childNodes.length;
    var nsLen = 0;
    //nodeType returns 1 for elements
    for(i=0; i<len; i++){
        if(list.childNodes.item(i).nodeType==1)
            nsLen++;
    }
    if(nsLen<2)
        return 2;
    else
        return nsLen;
}

function delAll(){
    var len = selectedList.length -1;
    for(i=len; i>=0; i--){
        availableList.appendChild(selectedList.item(i));
    }
    selectNone(selectedList,availableList);
    setSize(selectedList,availableList);
    
}

function addAll(){
    var len = availableList.length -1;
    for(i=len; i>=0; i--){
        selectedList.appendChild(availableList.item(i));
    }
    selectNone(selectedList,availableList);
    setSize(selectedList,availableList);
    
}




function showSelected(){
  
var optionList = getElementByName("savedList").options;
    var data = '';
    var len = optionList.length;
    for(i=0; i<len; i++){
        data += ',';
        data += optionList.item(i).value;
    }
    alert(data);
}


 function callutiljs(){

 var sel = document.getElementById("testexpire"); 
 var user_input=sel.options[sel.selectedIndex].value;
 document.WebDboxCreateSentPkgForm.expire.value=user_input; 
 
 }
 
 
 function checkIfAttributeUsrOrGrpExists(userOrgroup)
 {
  var returnValue = false;
   
   var len = selectedList.length;
 	 for(i=0; i<len; i++){
       
        var listitem=normalizeString(selectedList.item(i).value);
        userOrgroup=normalizeString(userOrgroup);       
        
        if ( userOrgroup == listitem )
          returnValue = true;
    }
   
  return returnValue;
 }
 
 
 
 
 function appendAttribute()
 {
 	
	if ( document.WebDboxCreateSentPkgForm.typeOfId.value == 'none' ) 
	alert("Please select a recipient type !")
	else {
		 var itemval=document.WebDboxCreateSentPkgForm.iccid.value;
		 
		 var item=itemval.toLowerCase();
		 if ( item != "" ){
		 var val=document.WebDboxCreateSentPkgForm.typeOfId.value+' '+item; //default val of typeOfId is none
	   
	   
	  	 //check if an items already appended in a list not to be added....
	  	 var dupExists=checkIfAttributeUsrOrGrpExists(val);
	 	 if ( dupExists == false )
	  	 {	selectedList.options[selectedList.options.length] = new Option(val,val); 
	 	    updateAclField();
	 	 }
	 	 WebDboxCreateSentPkgForm.iccid.value=""

	 	 
	 	}
	 	
	  	 	
 	}
   
 }
 
 
 
 function updateAclField()
 {
 			
 	   
 	
                var optionList = getElementByName("sendtoList").options;
 	 	var data = ''; // need to clear duplicates...
		var datum = ''; // need to clear duplicates...    
	    var len = optionList.length;
	    
	    if ( len == 0 )
	    {
	       document.WebDboxCreateSentPkgForm.aclList.value='empty';
	       document.WebDboxCreateSentPkgForm.aclListRcpt.value='empty';
	       return;
	    
	    }
	    
	    for(i=0; i<len; i++){
	        data += ',';
	        data += optionList.item(i).text;
	        var strObj = new String(optionList.item(i).text);
	       
	        if ( strObj.indexOf("[USER]") == 0 )
	        {
	           var newString=strObj.replace("[USER] ","_U_")
	           
	            datum += ',';
	            datum += newString;
	        }
	        if ( strObj.indexOf("[GROUP]") == 0 )
	        {
	           var newString=strObj.replace("[GROUP] ","_G_")
	          
	            datum += ',';
	            datum += newString;
	        }
	      
	    }
	    
	    
	    
	    document.WebDboxCreateSentPkgForm.aclList.value=data;
	    document.WebDboxCreateSentPkgForm.aclListRcpt.value=datum;
 
 
 }
 
 
 
 function addFilesToPackage()
 {
 	
 	document.WebDboxCreateSentPkgForm.addFiles.value='true';
 	document.WebDboxCreateSentPkgForm.submit();
 
 
 }

function submitCreateSentPkgOper()
{

updateAclField();
document.WebDboxCreateSentPkgForm.submit();
}


function radIccId_onclick(radIndex)
{
   var returnValue = true;
  
   if (radIndex == 0 )
   {
  	 
     document.WebDboxCreateSentPkgForm.radIccId[radIccIdIndex].value='user';	
     var val=document.WebDboxCreateSentPkgForm.radIccId[radIccIdIndex].value;
     alert(val);
   }
   if (radIndex == 1 )
   {
  	 
     document.WebDboxCreateSentPkgForm.radIccId[radIccIdIndex].value='group';
     var val=document.WebDboxCreateSentPkgForm.radIccId[radIccIdIndex].value;
     alert(val);
   }
   
  
      radIccIdIndex = radIndex;
  
   return returnValue;
}



 var radIccIdIndex = 0;
function checkRadUserIdChoice(radIndex)
{

  
  var returnValue = true;
	
  if (radIndex ==0)
   {
		if ( document.WebDboxCreateSentPkgForm.addIccIdUser.checked == true ){
			document.WebDboxCreateSentPkgForm.addIccIdGroup.checked = false;			
			document.WebDboxCreateSentPkgForm.typeOfId.value=document.WebDboxCreateSentPkgForm.addIccIdUser.value
		}
  }
   if (radIndex == 1)
   {
		if ( document.WebDboxCreateSentPkgForm.addIccIdGroup.checked == true ){
   		    document.WebDboxCreateSentPkgForm.addIccIdUser.checked = false;			
			document.WebDboxCreateSentPkgForm.typeOfId.value=document.WebDboxCreateSentPkgForm.addIccIdGroup.value
		}
   }
   
  
      radIccIdIndex = radIndex;
  
   return returnValue;
	
}

function clearthis()
{	
    	document.WebDboxCreateSentPkgForm.addIccIdUser.checked = true;
	document.WebDboxCreateSentPkgForm.typeOfId.value=document.WebDboxCreateSentPkgForm.addIccIdUser.value
	document.WebDboxCreateSentPkgForm.addIccIdGroup.checked = false;
}