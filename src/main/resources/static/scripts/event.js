$(document).ready(function() {
    eventFilter();
    loadFilterForm();
 });

function loadEventsFirstView(userCoordinates) {
	$('#eventListTitle').text("Nearby Event List");
	var events = gather.global.nearEvents
	appendToTable("eventTable", events, userCoordinates, "around you");
}

function loadJoinedEvents(userCoordinates) {
	$('#eventListTitle').text("Joined Event List");
	var events = gather.global.joinedEvents;
	appendToTable("eventTable", events, userCoordinates, "that you have joined");
}

function loadOwnedEvents(userCoordinates) {
	$('#eventListTitle').text("Owned Event List");
	var events = gather.global.ownedEvents;
	appendToTable("eventTable", events, userCoordinates, "that you own");
}

function loadFilterForm() {
	setUpCategoryOptions(0, "#filterCategory")
}

function eventFilter() {
	$('#filterSubmit').on('click', function() {
        var email = $("#inputEmail").val();
        var password = $("#inputPassword1").val();
        var confirmPassword = $("#inputPassword2").val();
        var displayName = $("#inputDisplayName").val();
        var registerBox = $('#registerFormSubmit').attr('href');
        var formId = '#formFeedback'
        var emailStatus = false
        var passwordStatus = false
        var displayNameStatus = false
        if (displayName == "" || password == "" || confirmPassword == "" || email == "") {
            $(formId).html('All the fields are required');
        } else {
            emailStatus = validateEmail(formId, email)
            passwordStatus = validatePassword(formId, password, confirmPassword)
            displayNameStatus = validateDisplayName(formId, displayName)
        }
        if (emailStatus && passwordStatus && displayNameStatus) {
            $.ajax({
                    accepts: "application/json",
                    type : "POST",
                    url : "/rest/registrants",
                    contentType: "application/json; charset=UTF-8",
                    dataType: "json",
                    beforeSend: function() {
                        $('#loading').show();
                    },
                    data : '{ \
                        "email" : ' + email + ', \
                        "password" : ' + password + ', \
                        "displayName" : ' + displayName + ' \
                    }',
                    complete: function() {
                        $('#loading').hide();
                    },
                    success : function(returnvalue) {
                        if (returnvalue.status == 0) {
                            $(formId).css("color", "green")
                            $(formId).html('Registration Success!');
                            $(registerBox).fadeOut(100);
                            $('#mask , .register-popup').fadeOut(300, function() {
                                $('#mask').remove();
                            });
                            resetRegisterFields();
                            gather.global.session.signedIn = true
                            gather.global.currentDisplayName = displayName;
                            updateGreeting();
                            headerSelect();
                            window.location.href = "/"
                        } else {
                                $(formId).html(returnvalue.message);
                        }
                    },
                    error : function(jqXHR, textStatus, errorThrown) {
                        var responseMessage = $.parseJSON(jqXHR.responseText).message;
                        $('#formFeedback').html(responseMessage);
                    }
                });
        }
    });
}

function appendToTable(tableClass, events, userCoordinates, message){
	if (events != null) {
		if (events.length != 0) {
			$('.' + tableClass).html('');
			for(i = 0; i < events.length; i++){
				var eventId = events[i].id;
				var lat1 = events[i].location.latitude;
				var lon1 = events[i].location.longitude;
				var lat2 = parseFloat(userCoordinates.latitude);
				var lon2 = parseFloat(userCoordinates.longitude);
				var dist = distance(lat1, lon1, lat2, lon2, 'M').toFixed(1).toString();
				var title = events[i].name;
				var category = events[i].category.name;
				var unixtime = events[i].occurrences[0].timestamp;
				var dateTime = new Date( unixtime );
				var time = dateTime.toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});
				var date = dateTime.toLocaleDateString();
				// provide address by latlng
				var address = mapManager.determineAddressByCoord(lat1, lon1)
				var description = events[i].description;
				$('.' + tableClass).append(
					'<tr style="cursor: pointer;" onclick="mapManager.showPop('+ eventId +');">' +
						'<td colspan="3">  ' +
							'<div class="media event-card"> ' +
								'<div class="list-header">' +
								'<h4 class="list-title">'+ title +'</h4>' +
								'</div>' +
								'<div class="media-body">' +
								// 	'<span class="pull-right"></span>' +
								// 	'<p style="display:none;" eventId="' + eventId + '"></p>' +
									'<p class="list-description"><span class="glyphicon-space glyphicon glyphicon-play"></span>'+ category +'</p>' +
									'<p class="pull-left list-description"><span class="glyphicon-space glyphicon glyphicon-calendar"></span>'+ date + ', ' + time +'</p>' +
								// 	'<p class="list-description"><span class="glyphicon-space glyphicon glyphicon-map-marker"></span>'+ address +'</p>' +
									'<p class="pull-right list-description"><span class="glyphicon-space glyphicon glyphicon-map-marker"></span>'+ dist +' mi</p>' +
								// 	'<p class="list-description"><span class="glyphicon-space glyphicon glyphicon-info-sign"></span>'+ description +'</p>' +
								'</div>' +
							'</div>' +
						'</td>' +
					'</tr>'
				);
			}
		} else {
			$('.' + tableClass).html('');
			$('.' + tableClass).append(
				'<tr> ' +
					'<td colspan="3">  ' +
						'<div class="media event-card"> ' +
							'<div class="media-body">' +
								'<h4 class="list-title">There are no events '+ message +' :(</h4>' +
							'</div>' +
						'</div>' +
					'</td>' +
				'</tr>'
			);
		}
	}
}