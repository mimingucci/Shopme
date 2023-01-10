 minusButton=$(".linkMinus");
 plusButton=$(".linkPlus");
 quantityInput=$("#quantity"+${productId});

$(document).ready(function(){
	$(".linkMinus").on("click", function(event){
		event.preventDefault();
		console.log("1");
	})
	plusButton.on("click", function(evt){
		evt.preventDefault();
		alert("2");
	})
})
