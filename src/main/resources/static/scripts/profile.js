$(document).ready(function() {
	loadProfilePage();
	leftPaneSelect();
	loadProfilePasswordForm();
}); 

function leftPaneSelect() {
	$("#profile").hide();
	$("#map").show();
}

function loadProfilePage() {
	$('#myProfile').on('click', function() {
		$("#profile").show();
		$("#map").hide();
		updateProfileHeader();
	});
	$('#profileBack').on('click', function() {
		$("#profile").hide();
		$("#map").show();
	});
}

function loadProfilePasswordForm() {
	$('#profileChangePassword').on('click', function() {
		$(".profilePasswordForm").show("slow");
	});
	$('#profileDoNotChangePassword').on('click', function() {
		$(".profilePasswordForm").hide("slow");
		$("#profileCurrentPassword").value("");
		$("#profileNewPassword").value("");
		$("#profileConfirmPassword").value("");
		$("#profilePasswordFeedback").value("");
	});
}

function updateProfileHeader(){
	$("#profileName").html(gather.global.currentDisplayName + "'s Profile");
}

function loadChangePassword() {
	$('#profileChangePassword').on('click', function() {
		$("#profile").show();
		$("#map").hide();
		updateProfileHeader();
	});
	$('#profileChangePassword').on('click', function() {
		$("#profile").hide();
		$("#map").show();
	});
}
