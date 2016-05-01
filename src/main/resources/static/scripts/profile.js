$(document).ready(function() {
	loadProfilePage();
	rightPaneSelect();
	loadProfilePasswordForm();
	submitProfile();
	controlCategory();
});

var categoryIndex = 1;

function rightPaneSelect() {
	$("#profile").hide();
	$("#map").show();
}

function loadProfilePage() {
	$('#myProfile').on('click', function() {
		$("#profile").show();
		$("#map").hide();
		$.ajax({
			async: false,
		 	accepts: "application/json",
			type : "PUT",
			url : "rest/registrants/info",
			contentType: "application/json; charset=UTF-8",
			dataType: "json",
			data : '{}',
			success : function(returnvalue) {
				if (returnvalue.status == 0) {
					var displayName = returnvalue.result.displayName
					var defaultZip = returnvalue.result.defaultZip
					var defaultTimeWindow = returnvalue.result.defaultTimeWindow
					var showEventsAroundZipCode = returnvalue.result.showEventsAroundZipCode
					var categories = returnvalue.result.preferences
					var defaultRadius = returnvalue.result.defaultRadiusMi

					availableHours = [1, 2, 3, 4, 8, 12, 24, 72, 168, 336, 730, 2190, 8760]
					var ind = $.inArray(defaultTimeWindow, availableHours)
					if (ind == -1) {
						defaultTimeWindow = 10
					}

					availableRadius = [2, 5, 10, 25, 50]
					var ind = $.inArray(defaultRadius, availableRadius)
					if (ind == -1) {
						defaultRadius = 10
					}

					$("#profileDisplayName").val(displayName)
					$("#profileZipCode").val(defaultZip)
					$("#profileTimeWindow").val(defaultTimeWindow)
					$("#profileRadius").val(defaultRadius)
					$('#zipCodeCheckbox').prop('checked', showEventsAroundZipCode);
					$("#profile").show();
					$("#map").hide();
					updateProfileHeader();
					$("#profileCategories").empty();
					if (categories.length == 0) {
						setUpCategory(1)
					} else {
						setUpCategory(categories.length)
						for (var i = 1; i < categories.length+1; i++) {
							var htmlID = "#profileCategories" + i
							$(htmlID).val(categories[i-1])
						}
					}
				} else {
					$(formId).html(returnvalue.message);
				}
			},
	        error: function(jqXHR, textStatus, errorThrown) {
	            var responseMessage = $.parseJSON(jqXHR.responseText).message;
	            alert(responseMessage);
	    		sessionCheck();
        }
		});
	});

	$('#profileBack').on('click', function() {
		$("#profile").hide();
		$("#map").show();
	});
}

function setUpCategory(index) {
	// reset the field 
	categoryIndex = index
	$('#profileMyCategories').html('')
	if (index == 1) {
		$('#removeCategory').prop("disabled",true);
		$('#addCategory').prop("disabled",false);
	} else if (index == 4) {
		$('#removeCategory').prop("disabled",false);
		$('#addCategory').prop("disabled",true);
	} else {
		$('#removeCategory').prop("disabled",false);
		$('#addCategory').prop("disabled",false);
	}
	for (var i = 1; i < index+1; i++) {
		displayCategoryField(i)
	}
	
}

function controlCategory() {
	$('#addCategory').on('click', categoryIndex, function (){
		categoryIndex += 1
		if (categoryIndex <= 4) {
			displayCategoryField(categoryIndex)
			$('#removeCategory').prop("disabled",false);
		}
		if (categoryIndex == 4) {
			$('#addCategory').prop("disabled",true);
		}	
	});

	$('#removeCategory').on('click', categoryIndex, function() {
		if (categoryIndex >= 1) {
			removeCategoryField(categoryIndex)
			$('#addCategory').prop("disabled",false);
			categoryIndex -= 1
		} 
		if (categoryIndex == 1) {
			$('#removeCategory').prop("disabled",true);
		}
	});
}

function setUpCategoryOptions(index, formID) {
	var newOptions={};
	var catArray = gather.global.categories;
	var htmlID = formID + index
	$(htmlID).empty();
	$(htmlID).append($("<option></option>")
			     .attr("value", "").text("Category"));
	for (var i = 0; i < catArray.length; i++) {
		newOptions[catArray[i].name] = catArray[i].name;
		$(htmlID).append($("<option></option>")
			     .attr("value", catArray[i].name).text(catArray[i].name));
	}
}

function displayCategoryField(index) {
	var htmlID = 'profileCategories' + index
	var divID = 'profileCategoriesDiv' + index
	var jsID = '#profileCategories' + index
	var categoryField = '<div id="' + divID + '"><select style="margin-bottom: 5px; width:100%;" class="form-control" id="' + htmlID + '"/><div>'
	$('#profileCategories').append(categoryField)
	setUpCategoryOptions(index, "#profileCategories");
}

function removeCategoryField(index) {
	var jsID = '#profileCategoriesDiv' + index
	$(jsID).remove()
}

function submitProfile() {
	$('#profileSubmit').on('click', function() {
		var displayName = $("#profileDisplayName").val()
		var oldPassword = $('#profileCurrentPassword').val()
		var password = $("#profileNewPassword1").val()
		var confirmPassword = $("#profileNewPassword2").val()
		var defaultZipCode = $("#profileZipCode").val()
		var defaultTimeWindow = $("#profileTimeWindow").val()
		var defaultRadius = $("#profileRadius").val()
		var categories = [];
		var zipCodeCheck = $('#zipCodeCheckbox').is(':checked')
		for (var i = 1; i < categoryIndex+1; i++) {
			var selectID = '#profileCategories' + i
			categories.push($(selectID).val())
		}

		var formId = '#profileFeedback'
		var updateData = '  ' // do not remove the extra space here, because we slice later

		$('#profileSaving').show().delay(100);
		if (gather.global.currentDisplayName != displayName && displayName != '') {
			if (validateDisplayName(formId, displayName)) {
				updateData = updateData + '"displayName":"' + displayName + '", '
			} else {
				$('#profileSaving').hide();
				$(formId).css("color", "red")
				$(formId).html("Invalid display name")
				return
			}
		}

		if (password != '' ) {
			if (validatePassword(formId, password, confirmPassword)) {
				updateData = updateData + '"password":"' + password + '", '
				updateData = updateData + '"oldPassword":"' + oldPassword + '", '
			} else {
				$('#profileSaving').hide();
				$(formId).css("color", "red")
				$(formId).html("Invalid password")
				return
			}
		}


		if (defaultZipCode != '') {
			if (validateZipCode(formId, defaultZipCode, false)) {
				updateData = updateData + '"defaultZip":' + defaultZipCode + ', '
			} else {
				$('#profileSaving').hide();
				$(formId).css("color", "red")
				$(formId).html("Invalid zip code")
				return
			}
		}

		// index to hours
		availableHours = ["1", "2", "3", "4", "8", "12", "24", "72", "168", "336", "730", "2190", "8760"]
		var ind = $.inArray(defaultTimeWindow, availableHours)

		if (defaultTimeWindow != '') {
			if (ind != -1) {
				updateData = updateData + '"defaultTimeWindow":' + defaultTimeWindow + ', '
			} else {
				$('#profileSaving').hide();
				$(formId).css("color", "red")
				$(formId).html("Invalid time window")
				return
			}
		}

		availableRadius = ["2", "5", "10", "25", "50"]
		var ind = $.inArray(defaultTimeWindow, availableHours)

		if (defaultRadius != '') {
			if (ind != -1) {
				updateData = updateData + '"defaultRadiusMi":' + defaultRadius + ', '
			} else {
				$('#profileSaving').hide();
				$(formId).css("color", "red")
				$(formId).html("Invalid radius")
				return
			}
		}

		updateData = updateData + '"showEventsAroundZipCode":' + zipCodeCheck + ', '

		if (emptyStringArray(categories)) {
			updateData = updateData + '"preferences": ['
			for (var i = 0; i < categories.length; i++) {
				if (categories[i] !== "") {
					updateData = updateData + '"' + categories[i] + '", '
				}
			}
			updateData = updateData.slice(0,-2)  // removing the last comma
			updateData = updateData + '], '
		}

		updateData = updateData.slice(0,-2)  // removing the last comma

		$.ajax({
			async: false,
		 	accepts: "application/json",
			type : "PUT",
			url : "rest/registrants/update",
			contentType: "application/json; charset=UTF-8",
			dataType: "json",
			data : '{'+updateData+'}',
			complete: function() {
                $('#profileSaving').hide();
            },
			success : function(returnvalue) {
				if (returnvalue.status == 0) {
					$(formId).css("color", "green")
					$(formId).html('Profile update successful!').show().delay(3000).hide(1000)
					if (gather.global.currentDisplayName != displayName) {
						gather.global.currentDisplayName = displayName
						updateProfileHeader();
					}
					sessionCheck();
				} else {
					$(formId).css("color", "red")
					$(formId).html(returnvalue.message)
				}
				$("#profileDoNotChangePassword").trigger('click');
			},
			error : function(jqXHR, textStatus, errorThrown) {
                var responseMessage = $.parseJSON(jqXHR.responseText).message;
                $(formId).html(responseMessage).show().delay(3000).hide(1000);
                $("#profileDoNotChangePassword").trigger('click');
            }
		});

	});

	$('#profileBack').on('click', function() {
		$("#profile").hide();
		$("#profileFeedback").hide();
		$("#profileDoNotChangePassword").trigger('click');
		userFrontPage();
		$("#map").show();
	});
}

function emptyStringArray(my_arr){
   for(var i=0;i<my_arr.length;i++) {
       if (my_arr[i] !== "")  {
       	    return true;
       }   
   }
    return false;
}

function loadProfilePasswordForm() {
	$('#profileChangePassword').on('click', function() {
		$(".profilePasswordForm").show("slow");
	});
	$('#profileDoNotChangePassword').on('click', function() {
		$(".profilePasswordForm").hide("slow");
		$("#profileCurrentPassword").val('');
		$("#profileNewPassword1").val('');
		$("#profileNewPassword2").val('');
	});
}

function updateProfileHeader(){
	$("#profileName").html('').html(gather.global.currentDisplayName + "'s Profile");
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
