var dropdownBrands = $("#brand");
var dropdownCategories = $("#category");
var matches = dropdownBrands.val().match(/(\d+)/);
$(document).ready(function() {
	
	$("#shortDescription").richText();
	$("#fullDescription").richText();
	
	dropdownBrands.change(function() {
		dropdownCategories.empty();
		getCategories();
	});	
	
	getCategoriesForNewForm();

});

function getCategoriesForNewForm() {
	catIdField = $("#categoryId");
	editMode = false;
	
	if (catIdField.length) {
		editMode = true;
	}
	
	if (!editMode) getCategories();
}

function getCategories() {
	var brandId = matches[0]; 
	var url =  + "/brands/" + brandId + "/categories";
	
	$.get(url, function(responseJson) {
		$.each(responseJson, function(index, category) {
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
		});			
	});
}

function checkUnique(form) {
	productId = $("#id").val();
	productName = $("#name").val();
	
	csrfValue = $("input[name='_csrf']").val();
	
	params = {id: productId, name: productName, _csrf: csrfValue};
	
	$.post(checkUniqueUrl, params, function(response) {
		if (response == "OK") {
			form.submit();
		} else if (response == "Duplicate") {
			showWarningModal("There is another product having the name " + productName);	
		} else {
			showErrorModal("Unknown response from server");
		}
		
	}).fail(function() {
		showErrorModal("Could not connect to the server");
	});
	
	return false;
}	