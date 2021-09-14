//addEvent(window, "load", clearthis);
addEvent(window, "load", updateAclField);
addEvent(window, "load", updateAclFieldEditor);
addEvent(window, "load", initGroupSharing);
var selectedList;
var availableList;



var selectedListEditor;
var availableListEditor;



// Regular expressions for normalizing white space.
var whtSpEnds = new RegExp("^\\s*|\\s*$", "g");
var whtSpMult = new RegExp("\\s\\s+", "g");


function addEvent(elm, evType, fn, useCapture)
{
  if (elm.addEventListener){
    //alert("NS6 and Mozilla event"); 
    elm.addEventListener(evType, fn, useCapture);
    return true;
  } else if (elm.attachEvent){
    //alert("IE5+ event"); 
    var r = elm.attachEvent("on"+evType, fn);
    return r;
  } else {
    alert("Handler could not be removed");
  }
} 

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
    availableList = getElementByName("savedList");
    selectedList = getElementByName("sendtoList");
    availableListEditor = getElementByName("savedListEditor");
    selectedListEditor = getElementByName("sendtoListEditor");  
}

function deSelSelectedList()
{ 
   selectedList.selectedIndex = -1;
}

function deSelAvailableList()
{  
    availableList.selectedIndex = -1;   
}

function deSelSelectedListEditor()
{ 
   selectedListEditor.selectedIndex = -1;
  
}

function deSelAvailableListEditor()
{  
   availableListEditor.selectedIndex = -1;
  
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




function delRecipientEditor()
{
  
	var selIndex = selectedListEditor.selectedIndex;
	if(selIndex < 0)
      return;
     selectedListEditor.remove(selIndex);
     selectNone(selectedListEditor,availableListEditor);
	 setSize(availableListEditor,selectedListEditor);
   	 updateAclFieldEditor();
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
 
 function checkIfAttributeUsrOrGrpExistsInSelectedListEditor(userOrgroup)
 {
  var returnValue = false;
   
   var len = selectedListEditor.length;
 	 for(i=0; i<len; i++){
       
        var listitem=normalizeString(selectedListEditor.item(i).value);
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


function checkIfAttributeUsrOrGrpExistsInAvailableListEditor(userOrgroup)
 {
  var returnValue = false;
   
   var len = availableListEditor.length;
 	 for(i=0; i<len; i++){
       
        var listitem=normalizeString(availableListEditor.item(i).value);
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

function delAttributeEditor(){
   var selIndex = selectedListEditor.selectedIndex;
   if(selIndex < 0)
      return;
      
      //  check if an items already appended in a list not to be added....
  	 var dupExists=checkIfAttributeUsrOrGrpExistsInAvailableListEditor(selectedListEditor.options.item(selIndex).value);
 	 if ( dupExists == false )
  	 { 
		   availableListEditor.appendChild(selectedListEditor.options.item(selIndex))
		   selectNone(selectedListEditor,availableListEditor);
		   setSize(availableListEditor,selectedListEditor);
		   updateAclFieldEditor();
	 }else
	 {
	   selectedListEditor.remove(selIndex);
	   selectNone(selectedListEditor,availableListEditor);
	   setSize(availableListEditor,selectedListEditor);
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

function addAttributeEditor(){
   var addIndex = availableListEditor.selectedIndex;
   if(addIndex < 0)
      return;
      // check if an items already appended in a list not to be added....
  	 var dupExists=checkIfAttributeUsrOrGrpExistsInSelectedListEditor(availableListEditor.options.item(addIndex).value);
 	 if ( dupExists == false )
  	 {  
	   selectedListEditor.appendChild(availableListEditor.options.item(addIndex));
	   selectNone(selectedListEditor,availableListEditor);
	   setSize(selectedListEditor,availableListEditor);
	   updateAclFieldEditor();
	 }else
	 {
	   availableListEditor.remove(addIndex);
	   selectNone(selectedListEditor,availableListEditor);
	   setSize(selectedListEditor,availableListEditor);
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
function delAllEditor(){
    var len = selectedListEditor.length -1;
    for(i=len; i>=0; i--){
        availableListEditor.appendChild(selectedListEditor.item(i));
    }
    selectNone(selectedListEditor,availableListEditor);
    setSize(selectedListEditor,availableListEditor);
    
}



function addAll(){
    var len = availableList.length -1;
    for(i=len; i>=0; i--){
        selectedList.appendChild(availableList.item(i));
    }
    selectNone(selectedList,availableList);
    setSize(selectedList,availableList);
    
}

function addAllEditor(){
    var len = availableListEditor.length -1;
    for(i=len; i>=0; i--){
        selectedListEditor.appendChild(availableListEditor.item(i));
    }
    selectNone(selectedListEditor,availableListEditor);
    setSize(selectedListEditor,availableListEditor);
    
}



function checkDup()
{
	var item=document.WebDboxCreateGroupForm.iccid.value;
   
	var optionList = getElementByName("savedList").options;
   
    var len = optionList.length;
    for(i=0; i<len; i++){
       
        var val = optionList.item(i).value
        if ( item == val )
        {

          return true;
        }
    }
    
	
	return false
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
 document.WebDboxCreateGroupForm.expire.value=user_input; 
 
 }
 
 
 function checkIfAttributeUsrExists(userOrgroup)
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
 
 function checkIfAttributeUsrExistsEditor(userOrgroup)
 {
  var returnValue = false;
   
   var len = selectedListEditor.length;
 	 for(i=0; i<len; i++){
       
        var listitem=normalizeString(selectedListEditor.item(i).value);
        userOrgroup=normalizeString(userOrgroup);
        
        if ( userOrgroup == listitem )
          returnValue = true;
    }
   
  return returnValue;
 }
 
 
 
 function appendAttribute()
 {
 	
		 var itemval=document.WebDboxCreateGroupForm.iccid.value;
		 var item=itemval.toLowerCase();
		 
		 if ( item != "" ){
		 var val="[USER]"+' '+item; //default val of typeOfId is none
	   
	  	 // need to check if an items already appended in a list not to be added....
		  	 var dupExists=checkIfAttributeUsrExists(val);
		 	 if ( dupExists == false )
		  	 {
	  	 selectedList.options[selectedList.options.length] = new Option(val,val); 
	 	 updateAclField();
	
		 	 }
	 	 WebDboxCreateGroupForm.iccid.value=""
	 	 }
 	
   
 }
 
 function appendAttributeEditor()
 {
 	
		 var itemval=document.WebDboxCreateGroupForm.iccidEditor.value;
		 var item=itemval.toLowerCase();
		 
		 if ( item != "" ){
			 var val="[USER]"+' '+item; //default val of typeOfId is none
		   
		  	 // need to check if an items already appended in a list not to be added....
		  	 var dupExists=checkIfAttributeUsrExistsEditor(val);
		 	 if ( dupExists == false )
		  	 {
		  	 selectedListEditor.options[selectedListEditor.options.length] = new Option(val,val); 
		 	 updateAclFieldEditor();
	
		 	 }
	 	 	 WebDboxCreateGroupForm.iccidEditor.value=""
 	     }
   

 }
 
 function updateAclField()
 {
 			
 	
 		var optionList = getElementByName("sendtoList").options;
 	 	var data = ''; // need to clear duplicates...
		var datum = ''; // need to clear duplicates...    
	    var len = optionList.length;
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
	   
	 
	    
	    document.WebDboxCreateGroupForm.aclList.value=data;
	    document.WebDboxCreateGroupForm.aclListRcpt.value=datum;
 
 
 }
 
  function updateAclFieldEditor()
 {
 			
 	
 		//var optionList = document.getElementById("sendtoList").options;
        var optionList = getElementByName("sendtoListEditor").options;
 	 	var data = ''; // need to clear duplicates...
		var datum = ''; // need to clear duplicates...    
	    var len = optionList.length;
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
	   
	 
	    
	    document.WebDboxCreateGroupForm.aclListEditor.value=data;
	    document.WebDboxCreateGroupForm.aclListRcptEditor.value=datum;
 
 
 }
 
 function addFilesToPackage()
 {
 	
 	document.WebDboxCreateGroupForm.addFiles.value='true';
 	document.WebDboxCreateGroupForm.submit();
 
 
 }

function submitCreatGroupOper()
{

document.WebDboxCreateGroupForm.submit();
}




function updateGroupOper()
{
 document.WebDboxCreateGroupForm.action='WebDboxGroupEdit.do';
 document.WebDboxCreateGroupForm.submit();      
 
}


function loadGroupDetails(control)
{
  var grpToEdit = document.WebDboxCreateGroupForm.groupCreateOrEditOptions.value;
  
  
  
  if (grpToEdit != 'Create new group...')
  {
      document.WebDboxCreateGroupForm.typeOfId.value=grpToEdit;  
      document.WebDboxCreateGroupForm.action='WebDboxFwdCreateGroupPage.do';
      document.WebDboxCreateGroupForm.submit();      
    
  } else
  {
      document.WebDboxCreateGroupForm.typeOfId.value='Create new group...'; 
       document.WebDboxCreateGroupForm.action='WebDboxFwdCreateGroupPage.do';
      document.WebDboxCreateGroupForm.submit();      
  }
      

}


var radIccIdIndex = 0;

var groupOwnLisValue=false;
var groupMemLisValue=false;

function initGroupSharing()
{
groupOwnLisValue=true;
groupMemLisValue=false;

var editvisi=document.WebDboxCreateGroupForm.editvisibility.value;
var editlisi=document.WebDboxCreateGroupForm.editlistablility.value;


if ( editvisi == 'EDIT_VIS_GROUP_SCOPE_OWNER' )
{
   
  document.WebDboxCreateGroupForm.groupOwnVis.checked=true;
  document.WebDboxCreateGroupForm.groupMemVis.checked=false; 
 
}
if ( editvisi == 'EDIT_VIS_GROUP_SCOPE_MEMBER' )
{
  document.WebDboxCreateGroupForm.groupOwnVis.checked=false;
  document.WebDboxCreateGroupForm.groupMemVis.checked=true;
  
}
if ( editlisi == 'EDIT_LIS_GROUP_SCOPE_OWNER' )
{
  document.WebDboxCreateGroupForm.groupOwnLis.checked=true;
  document.WebDboxCreateGroupForm.groupMemLis.checked=false;  
  
}
if ( editlisi == 'EDIT_LIS_GROUP_SCOPE_MEMBER' )
{
  document.WebDboxCreateGroupForm.groupOwnLis.checked=false;
  document.WebDboxCreateGroupForm.groupMemLis.checked=true;  
  
}
if ( editvisi == 'TEST_VIS_GROUP_SCOPE_OWNER' )
{
  document.WebDboxCreateGroupForm.groupOwnVis.checked=true;
   document.WebDboxCreateGroupForm.groupMemVis.checked=false;
   
}
if ( editlisi == 'TEST_LIS_GROUP_SCOPE_OWNER' )
{
   document.WebDboxCreateGroupForm.groupOwnLis.checked=true;
   document.WebDboxCreateGroupForm.groupMemLis.checked=false;
  
}

}

function updateGroupShare()
{

 if (document.WebDboxCreateGroupForm.groupOwnVis.checked == true )
 {
   document.WebDboxCreateGroupForm.visibility.value='VIS_GROUP_SCOPE_OWNER'
 }
 if (document.WebDboxCreateGroupForm.groupMemVis.checked == true )
 {
   document.WebDboxCreateGroupForm.visibility.value='VIS_GROUP_SCOPE_MEMBER'
 }
 if (document.WebDboxCreateGroupForm.groupOwnLis.checked == true )
 {
   document.WebDboxCreateGroupForm.listablility.value='LIS_GROUP_SCOPE_OWNER'
 }
 if (document.WebDboxCreateGroupForm.groupMemLis.checked == true )
 {
   document.WebDboxCreateGroupForm.listablility.value='LIS_GROUP_SCOPE_MEMBER'
 }


}



function checkRadUserIdChoice(radIndex)
{

  
  var returnValue = true;
	
//visibility = GROUP_SCOPE_OWNER	
  if (radIndex == 0)
   {        
		if ( document.WebDboxCreateGroupForm.groupOwnVis.checked == true ){
			document.WebDboxCreateGroupForm.groupMemVis.checked = false;			
			document.WebDboxCreateGroupForm.groupOwnLis.checked=groupOwnLisValue;
			document.WebDboxCreateGroupForm.groupMemLis.checked=groupMemLisValue;
			
			document.WebDboxCreateGroupForm.groupOwnLis.disabled=true;
			document.WebDboxCreateGroupForm.groupMemLis.disabled=true;
			updateGroupShare();
		}
  }
  
  //visibility = GROUP_SCOPE_MEMBER
   if (radIndex == 1)
   {
		if ( document.WebDboxCreateGroupForm.groupMemVis.checked == true ){
   		    document.WebDboxCreateGroupForm.groupOwnVis.checked = false;			
			document.WebDboxCreateGroupForm.groupOwnLis.checked=groupOwnLisValue;				
			document.WebDboxCreateGroupForm.groupMemLis.checked=groupMemLisValue;
			
			document.WebDboxCreateGroupForm.groupOwnLis.disabled=false;					
			document.WebDboxCreateGroupForm.groupMemLis.disabled=false;
			updateGroupShare();
		}
   }
   
   //listability = GROUP_SCOPE_OWNER
   if (radIndex == 2)
   {
		if ( document.WebDboxCreateGroupForm.groupOwnLis.checked == true ){   
		    		    		
			document.WebDboxCreateGroupForm.groupMemLis.checked=false;			
			document.WebDboxCreateGroupForm.groupMemLis.disabled=false;
			groupOwnLisValue=true;
            groupMemLisValue=false;
			updateGroupShare();
		}
   }
   
    //listability = GROUP_SCOPE_MEMBER
   if (radIndex == 3)
   {
		
		if ( document.WebDboxCreateGroupForm.groupMemLis.checked == true ){   		    		
			document.WebDboxCreateGroupForm.groupOwnLis.checked=false;			
			document.WebDboxCreateGroupForm.groupOwnLis.disabled=false;
			
			groupMemLisValue=true;
			groupOwnLisValue=false;
			updateGroupShare();
            
		}
   }
  
      radIccIdIndex = radIndex;
  
   return returnValue;
	
}