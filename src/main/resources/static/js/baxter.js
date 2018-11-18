function showVal(outputBox, newVal){
	document.getElementById(outputBox).innerHTML=newVal;
}
function showValStart(outputBox, newVal){
	document.getElementById(outputBox).innerHTML=newVal;
}
function showValEnd(outputBox, newVal){
	document.getElementById(outputBox).innerHTML=newVal;
}

function submitTrimForm(formName){
	document.getElementById(formName).submit();
}

function renameFile(filepath, username){
	var newName = prompt("Please enter new filename", "");
	if (newName == null || newName == "") {
		console.log("bad info");
	} else {
		var changeNameData = {filepath : filepath, username : username, newName : newName}
		$.ajax({
			type: "POST",
			url: "/baxt/rename-file",
			data: changeNameData,
			success: function (result) {
				console.log(5 + 6);
			},
			error: function (result) {
				// do something.
			}
		});
	}
}

