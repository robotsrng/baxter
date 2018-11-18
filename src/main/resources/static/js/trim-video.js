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

function reloadTable(){

}

function eventFire(){
	var sliders = document.getElementsByClassName("js-slider");
	$(".js-slider").trigger('change');
}

$(eventFire());
