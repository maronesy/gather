$(document).ready(function() {
    loadCategoryForm();
    eventFilter();
    eventSortByDistance();
    eventSortByTime();
 });

function loadEventsFirstView(userCoordinates) {
	showFilterForm();
	$('#eventListTitle').text("Nearby Event List");
	var events = gather.global.nearEvents;
	calculateDistanceAndTime (events, userCoordinates);
	sortByDistanceLogic(events);
	appendToTable("eventTable", events, "around you");
}

function loadJoinedEvents(userCoordinates) {
	hideFilterForm();
	$('#eventListTitle').text("Joined Event List");
	var events = gather.global.joinedEvents;
	calculateDistanceAndTime (events, userCoordinates);
	sortByDistanceLogic(events);
	appendToTable("eventTable", events, "that you have joined");
}

function loadOwnedEvents(userCoordinates) {
	hideFilterForm();
	$('#eventListTitle').text("Owned Event List");
	var events = gather.global.ownedEvents;
	calculateDistanceAndTime (events, userCoordinates);
	sortByDistanceLogic(events);
	appendToTable("eventTable", events, "that you own");
}

function loadCategoryForm() {
	setUpCategoryOptions("", "#filterCategory")
}

function showFilterForm() {
	$('#filter').show()
	height = window.screen.availHeight - 250 - 125
	if (height > 800) {
        height = 800;
    }
	$('#eventList').css("height", height);
}

function hideFilterForm() {
	$('#filter').hide()
	height = window.screen.availHeight - 250
	if (height > 800) {
        height = 800;
    }
	$('#eventList').css("height", height);
}

function sortByAscendingTime(a, b){
  var aTime = a.recentEventTime;
  var bTime = b.recentEventTime; 
  return ((aTime < bTime) ? -1 : ((aTime > bTime) ? 1 : 0));
}

function sortByDescendingTime(a, b){
  var aTime = a.recentEventTime;
  var bTime = b.recentEventTime; 
  return ((aTime > bTime) ? -1 : ((aTime < bTime) ? 1 : 0));
}

function sortByTimeLogic(events) {
	var sortUpDown = $('#sortTime').attr('class');
	if (sortUpDown == "dropdown") {
		events.sort(sortByAscendingTime);
		$('#sortTime').attr('class', 'dropup');
	} else if (sortUpDown == "dropup") {
		events.sort(sortByDescendingTime);
		$('#sortTime').attr('class', 'dropdown');
	}
}

function eventSortByTime() {
	$('#sortTime').on('click', function() {
        if (gather.global.currentEventList == ViewingNearByEvents) {
        	events = gather.global.nearEvents;
        	sortByTimeLogic(events);
			appendToTable("eventTable", events, "around you");
		} else if (gather.global.currentEventList == ViewingJoinedEvents){
			events = gather.global.joinedEvents
			sortByTimeLogic(events);
			appendToTable("eventTable", events, "that you have joined");
		} else if(gather.global.currentEventList == ViewingOwnedEvents){
			events = gather.global.ownedEvents
			sortByTimeLogic(events);
			appendToTable("eventTable", events, "that you own");
		} else {
			displayGeneralFailureModal();
		}
    });
}

function sortByAscendingDistance(a, b){
  var aDistance = a.distance;
  var bDistance = b.distance; 
  return ((aDistance < bDistance) ? -1 : ((aDistance > bDistance) ? 1 : 0));
}

function sortByDescendingDistance(a, b){
  var aDistance = a.distance;
  var bDistance = b.distance; 
  return ((aDistance > bDistance) ? -1 : ((aDistance < bDistance) ? 1 : 0));
}

function sortByDistanceLogic(events) {
	var sortUpDown = $('#sortDistance').attr('class');
	if (sortUpDown == "dropdown") {
		events.sort(sortByAscendingDistance);
		$('#sortDistance').attr('class', 'dropup');
	} else if (sortUpDown == "dropup") {
		events.sort(sortByDescendingDistance);
		$('#sortDistance').attr('class', 'dropdown');
	}
}

function eventSortByDistance() {
	$('#sortDistance').on('click', function() {
        if (gather.global.currentEventList == ViewingNearByEvents) {
        	events = gather.global.nearEvents;
        	sortByDistanceLogic(events);
			appendToTable("eventTable", events, "around you");
		} else if (gather.global.currentEventList == ViewingJoinedEvents){
			events = gather.global.joinedEvents
			sortByDistanceLogic(events);
			appendToTable("eventTable", events, "that you have joined");
		} else if(gather.global.currentEventList == ViewingOwnedEvents){
			events = gather.global.ownedEvents
			sortByDistanceLogic(events);
			appendToTable("eventTable", events, "that you own");
		} else {
			displayGeneralFailureModal();
		}
    });
}

function eventFilter() {
	$('#filterSubmit').on('click', function() {
        var category = $("#filterCategory").val(); 
        var time = $("#filterTime").val();
        var radius = $("#filterRadius").val();
        if (category == "") {
        	category = null  // will use default values defined in getNearByEvents
        } else {
        	category = [category]  // category is always a list
        }
        if (time == "") {
        	time = null  // will use default values defined in getNearByEvents
        }
        if (radius == "") {
        	radius = null  // will use default values defined in getNearByEvents
        }
        mapManager.filter(time, category, radius)
    });
}

function mostRecentOccurrence(occurrences) {
	var timestamps = [];
	for (var i = 0; i < occurrences.length; i++) {
		var timestamp = occurrences[i].timestamp;
		timestamps.push(timestamp);
	}
	timestamps.sort();
	for (var j = 0; j < timestamps.length; j++) {
		if (timestamps[j] > Date.now()) {
			var unixtime = timestamps[j];
			return unixtime
		}
	}
}

function calculateDistanceAndTime (events, userCoordinates) {
	if (events != null) {
		if (events.length != 0) {
			for(i = 0; i < events.length; i++){
				var lat1 = events[i].location.latitude;
				var lon1 = events[i].location.longitude;
				var lat2 = parseFloat(userCoordinates.latitude);
				var lon2 = parseFloat(userCoordinates.longitude);
				var dist = distance(lat1, lon1, lat2, lon2, 'M').toFixed(1).toString();
				events[i].distance = parseFloat(dist);
				var unixtime = mostRecentOccurrence(events[i].occurrences)
				events[i].recentEventTime = unixtime;
			}
		}
	}
}

function appendToTable(tableClass, events, message){
	if (events != null) {
		if (events.length != 0) {
			$('.' + tableClass).html('');
			for(i = 0; i < events.length; i++){
				var eventId = events[i].id;
				var lat1 = events[i].location.latitude;
				var lon1 = events[i].location.longitude;
				var dist = events[i].distance
				var title = events[i].name;
				var category = events[i].category.name;
				var unixtime = events[i].recentEventTime
				var dateTime = new Date( unixtime );
				var time = dateTime.toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});
				var date = dateTime.toLocaleDateString();
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