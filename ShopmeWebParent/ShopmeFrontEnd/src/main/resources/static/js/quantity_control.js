minusButton = $(".linkMinus");
plusButton = $(".linkPlus");

$(document).ready(function() {
	$(".linkMinus").on("click", function(event) {
		event.preventDefault();
		var productId = $(this).attr("pid");
		var quantityItem = $("#quantity" + productId);
		if (parseInt(quantityItem.val()) - 1 < 1) {
			alert("Minimum quantity is default 1");

		} else {
			$("#quantity" + productId).val(parseInt(quantityItem.val()) - 1);
		}

	})
	$(".linkPlus").on("click", function(evt) {
		evt.preventDefault();
		var productId = $(this).attr("pid");
		var quantityItem = $("#quantity" + productId);
		if (parseInt(quantityItem.val()) + 1 > 5) {
			alert("Maximum quantity is default 5");

		} else {
			var newQuantity = parseInt(quantityItem.val()) + 1;
			$("#quantity" + productId).val(newQuantity);
		}
	})
})
