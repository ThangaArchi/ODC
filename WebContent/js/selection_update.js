function selectAllBoxes(control,prefix,count) {
	if (control.checked) {
		for (i=0;i<count;i++) {
			box=document.getElementById(prefix+i);
			box.checked=true;
		}
	}
}
function updateSelect(id,prefix,count) {
	var control=document.getElementById(id);
	if (!control.checked) return;
	for (i=0;i<count;i++) {
		box=document.getElementById(prefix+i);
		if (!box.checked) {
		  control.checked=false;
		  break;
		}
	}
}
