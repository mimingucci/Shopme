$(document).ready(function(){
	$("#buttonAdd2Cart").on("click", function(){
		addToCart();
	})
})

function addToCart(){
	var quantity = $("#quantity" + productId).val();
	var url=contextPath+"cart/add/"+productId+"/"+quantity;
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response){
		showModalDialog("Shopping Cart", response);
	}).fail(function(){
		showErrorModal("Error while adding product to shopping cart.");
	})
}