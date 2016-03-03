$(document).ready(function(){
	tableInteractions();
	locateMe();
	enterZip();
});

function tableInteractions(){
	$('.star').on('click', function () {
      $(this).toggleClass('star-checked');
    });

    $('.ckbox label').on('click', function () {
      $(this).parents('tr').toggleClass('selected');
    });

    $('.btn-filter').on('click', function () {
      var $target = $(this).data('target');
      if ($target != 'all') {
        $('.table tr').css('display', 'none');
        $('.table tr[data-status="' + $target + '"]').fadeIn('slow');
      } else {
        $('.table tr').css('display', 'none').fadeIn('slow');
      }
    });
}

function locateMe(){
    $('#locateMe').on('click',function() { 
    	mapManager.performAction();
     });
}

function enterZip(){
    $('#enterZip').on('click',function() { 
    	var zipcode = $('#zipcode').val();
    	mapManager.determineCoordByZipCode(zipcode);
     });
}

