document.addEventListener("DOMContentLoaded", function(){

let reportForm = document.getElementById("reportForm");

if(reportForm){
reportForm.addEventListener("submit", function(e){
e.preventDefault();
alert("✅ Disaster report submitted successfully!");
reportForm.reset();
});
}

let helpForm = document.getElementById("helpForm");

if(helpForm){
helpForm.addEventListener("submit", function(e){
e.preventDefault();
alert("✅ Help request submitted successfully!");
helpForm.reset();
});
}

});