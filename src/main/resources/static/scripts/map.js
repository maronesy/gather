function GeolocationFailure(message) {
	this.message = message;
}

function GeneralFailure(message) {
	this.message = message;
}

function MapManager(mapboxAccessToken, mapboxMapID) {

	var mapManager = this;

	L.mapbox.accessToken = mapboxAccessToken;
	
	var map = buildMap();

	var eventSearchRadiusInMiles = 10.0;
	var eventSearchRadiusInMeters = eventSearchRadiusInMiles * 1609.34;

	var currentUserCoordinates = null;
	var userMarker = null;
	var eventMarker = null;
	var searchRadiusCircle = null;
	var userCoordinates = null;

	var geolocationSupported = (navigator.geolocation ? true : false);

	var newEvents = [];

	var establishedEvents = [];
	var establishedJoinedEvents = [];
	var establishedOwnedEvents = [];

	function buildMap() {
		var mapOptions = {
			attributionControl: false,
			zoomControl: false
		};
		var map = L.mapbox.map("map-canvas", mapboxMapID, mapOptions);

		var zoomControlOptions = {
			position: "topright"
		};
		var zoomControl = new L.Control.Zoom(zoomControlOptions);
		zoomControl.addTo(map);

		var mapLegendHTML = document.getElementById("map-legend-container").innerHTML;
		map.legendControl.addLegend(mapLegendHTML);

		return map;
	}

	function determineUserCoordinates(successCallback, failureCallback) {
		navigator.geolocation.getCurrentPosition(function(currentPosition) {
			userCoordinates = {
				latitude: currentPosition.coords.latitude,
				longitude: currentPosition.coords.longitude
			}

			map.setView([userCoordinates.latitude, userCoordinates.longitude], 10);

			if(typeof(successCallback) === "function") {
				successCallback(userCoordinates);
			}
		}, function(error) {
			if(error.code == error.PERMISSION_DENIED) {
				if(typeof(failureCallback) === "function") {
					failureCallback();
				}
			}
			else {
				if(typeof(successCallback) === "function") {
					successCallback(null);
				}
			}
		});
	}

	this.performAction = function() {
		if(geolocationSupported) {
			// Get and process the user's current location.
			determineUserCoordinates(function(initialUserCoordinates) {
				try {
					processUserCoordinates(initialUserCoordinates);

					// Update every 10 seconds.
//					var updateIntervalID = setInterval(function() {
//
//						determineUserCoordinates(function(updatedUserCoordinates) {
//
//							try {
//								processUserCoordinates(updatedUserCoordinates);
//							}
//							catch(updatedException) {
//								clearInterval(updateIntervalID);
//
//								doStandardExceptionHandling(updatedException);
//							}
//						});
//					}, 10000);
				}
				catch(initialException) {
					doStandardExceptionHandling(initialException);
				}
			}, function() {
				displayGeolocationUnsupportedModal();
			});
		}
		else {
			displayGeolocationUnsupportedModal();
		}
	}

	var geolocationErrorCount = 0;

	function processUserCoordinates(uCoordinates) {
		userCoordinates = uCoordinates
		if(userCoordinates == null) {
			geolocationErrorCount++;

			if(geolocationErrorCount > 2) {
				throw new GeolocationFailure("Geolocation has consecutively failed at least 3 times.");
			}
		}
		else {
			geolocationErrorCount = 0;

			if(currentUserCoordinates === null || currentUserCoordinates.latitude !== userCoordinates.latitude || currentUserCoordinates.longitude !== userCoordinates.longitude) {
				var currentZoomLevel = map.getZoom();

				if(typeof(currentZoomLevel) !== "number") {
					currentZoomLevel = 13;
				}

				map.setView([userCoordinates.latitude, userCoordinates.longitude], currentZoomLevel);

				placeUserMarker(userCoordinates);
				getNearByEvents();
				currentUserCoordinates = userCoordinates;
				if (gather.global.session.signedIn == true){
					joinedEvents();
					ownedEvents();
				}
			}
		}
	}

	function placeUserMarker(userCoordinates) {
		var markerPosition = new L.LatLng(userCoordinates.latitude, userCoordinates.longitude);

		if(userMarker === null) {
			var iconOptions = {
				"marker-size": "large",
				"marker-symbol": "star",
				"marker-color": "#CC00FF"
			};

			var markerOptions = {
				icon: L.mapbox.marker.icon(iconOptions),
			};

			userMarker = L.marker(markerPosition, markerOptions);
			userMarker.addTo(map);
		}
		else {
			userMarker.setLatLng(markerPosition);
		}

		if(searchRadiusCircle === null) {
			var searchRadiusCircleOptions = {
				clickable: false,
				stroke: false,
				fillColor: "#40A3FE",
				fillOpacity: 0.2
			};

			searchRadiusCircle = L.circle(markerPosition, eventSearchRadiusInMeters, searchRadiusCircleOptions);
			searchRadiusCircle.addTo(map);
		}
		else {
			searchRadiusCircle.setLatLng(markerPosition);
		}

		setUserMarkerPopup(userCoordinates);
	}

	function setUserMarkerPopup(userCoordinates) {
		var simpleUserMarkerHTML = $("#simple-user-marker-content-template").html();
		simpleUserMarkerHTML = sprintf(simpleUserMarkerHTML, userCoordinates.latitude, userCoordinates.longitude);

		userMarker.bindPopup(simpleUserMarkerHTML);
	}


	function submitFeedbackForm() {
		var modalForm = $("#feedback-modal");
		var locationID = modalForm.data("locationID");

		submitFeedback(locationID, function() {
			modalForm.modal("hide");
		}, function() {
			modalForm.modal("hide");

			displayGeneralFailureModal();
		});
	}


	function isSameCoordinates(coordinates, anotherCoordinates) {
		var sameCoordinates = false;

		if(coordinates && anotherCoordinates) {
			if(coordinates.latitude === anotherCoordinates.latitude) {
				if(coordinates.longitude === anotherCoordinates.longitude) {
					sameCoordinates = true;
				}
			}
		}

		return sameCoordinates;
	}


	function buildOffsetMarkerCoordinates(referenceCoordinates) {
		var circlePosition = new L.LatLng(referenceCoordinates.latitude, referenceCoordinates.longitude);

		var mapZoomLevel = map.getZoom() + 1;

		var circleRadius = Math.min(scale(mapZoomLevel, 20, 15, 50, 400));
		var circle = L.circle(circlePosition, circleRadius);
		var circleBounds = circle.getBounds();

		var markerPosition = circleBounds.getNorthWest();

		var markerCoordinates = {
			latitude: markerPosition.lat,
			longitude: markerPosition.lng
		};

		return markerCoordinates;
	}

	function getContentTemplateClone(contentTemplateSelector) {
		var contentTemplate = $(contentTemplateSelector).children().eq(0);

		var contentTemplateClone = contentTemplate.clone();

		return contentTemplateClone;
	}

	function scale(value, baseMinimum, baseMaximum, limitMinimum, limitMaximum) {
		return ((limitMaximum - limitMinimum) * (value - baseMinimum) / (baseMaximum - baseMinimum)) + limitMinimum;
	}

	function generateElementID() {
		var possibleCharacters = "abcdef0123456789";

		var selectedCharacters = [];

		for(var index = 0; index < 32; index++) {
			var selectedCharacter = possibleCharacters.charAt(Math.floor(Math.random() * 16));
			selectedCharacters[index] = selectedCharacter;
		}

		var elementID = selectedCharacters.join("");

		return elementID;
	}

	function doStandardExceptionHandling(exception) {
		console.log(exception);

		if(exception instanceof GeolocationFailure) {
			displayGeolocationUnsupportedModal();
		}
		else {
			displayGeneralFailureModal();
		}
	}

	function displayGeolocationUnsupportedModal() {
		$("#geolocation-unsupported-modal").modal("show");
	}

	function displayGeneralFailureModal() {
		$("#general-failure-modal").modal("show");
	}

	$('#addEventBtn').on('click', function() {
		if(gather.global.session.signedIn){
			addNewEvent()
		}else{
			$("#anonymous-user-add-event-failure-modal").modal("show");
			$('#failureAddEventModalRegisterBtn').on('click', function() {
				$('#registerButton').trigger('click');
			});
		}
	});

	function addNewEvent() {

		if(currentUserCoordinates === null) {
			displayGeolocationUnsupportedModal();
		}
		else {

			var markerCoordinates = buildOffsetMarkerCoordinates(currentUserCoordinates);
			var markerPosition = new L.LatLng(markerCoordinates.latitude, markerCoordinates.longitude);

			var iconOptions = {
				"marker-size": "large",
				"marker-symbol": "star",
				"marker-color": "#419641"
			};

			var bounceOptions = {
				duration: 500,
				height: 100
			};

			var markerOptions = {
				draggable: true,
				icon: L.mapbox.marker.icon(iconOptions),
				bounceOnAdd: true,
				bounceOnAddOptions: bounceOptions
			};

			var popupOptions = {
				minWidth: 400
			};

			var eventMarker = L.marker(markerPosition, markerOptions);

			var newEventContent = getContentTemplateClone("#new-event-content-template");

			var newEventDataID = generateElementID();
			newEvents[newEventDataID] = {
				eventMarker: eventMarker
			}

			$(newEventContent).find("button").each(function(index) {
				$(this).attr("data-new-event-data-id", newEventDataID);
			});

			eventMarker.bindPopup(newEventContent[0], popupOptions);

			eventMarker.on("dragend", function(event) {
				eventMarker.openPopup();
	        });

			eventMarker.addTo(map);

			eventMarker.openPopup();
		}
	}

	function setUpCategoryOptions(){

		var newOptions={};
		var catArray = gather.global.categories;
		var $categories = $( "#event-category" );
		$categories.empty();
		for (var i = 0; i < catArray.length; i++) {
			newOptions[catArray[i].name] = catArray[i].name;
			$categories.append($("<option></option>")
				     .attr("value", catArray[i].name).text(catArray[i].name));
		}
	}

	var occurrenceIndex = 1

	$('#addOccurrence').on('click', occurrenceIndex, function() {
		occurrenceIndex += 1
		if (occurrenceIndex <= 4) {
			displayOccurrenceField(occurrenceIndex)
			$('#removeOccurrence').prop("disabled",false);
		}
		if (occurrenceIndex == 4) {
			$('#addOccurrence').prop("disabled",true);
		}		
	});
	$('#removeOccurrence').on('click', occurrenceIndex, function() {
		if (occurrenceIndex >= 1) {
			removeOccurrenceField(occurrenceIndex)
			$('#addOccurrence').prop("disabled",false);
			occurrenceIndex -= 1
		} 
		if (occurrenceIndex == 1) {
			$('#removeOccurrence').prop("disabled",true);
		}
	});

	function setUpOccurrence() {
		// reset the field 
		occurrenceIndex = 1
		$('#event-occurrence').html('')
		$('#removeOccurrence').prop("disabled",true);
		$('#addOccurrence').prop("disabled",false)
		displayOccurrenceField(occurrenceIndex)
	}

	function removeOccurrenceField(index) {
		var js_id = '#event-occurrence' + index
		$(js_id).remove()
	}

	function displayOccurrenceField(index) {
		var htmlID = 'event-occurrence' + index
		var jsID = '#event-occurrence' + index
		var occurrenceField = '<input style="margin-top:5px;" class="form-control" id="' + htmlID + '"/>'
		$('#event-occurrence').append(occurrenceField)
		$(jsID).datetimepicker({startDate:new Date().toLocaleDateString()});
	}

	this.discardNewEvent = function(newEventDataID) {
		var eventData = newEvents[newEventDataID];

		if(typeof(eventData) === "undefined") {
			displayGeneralFailureModal();
		}
		else {
			var eventMarker = eventData.eventMarker;
			map.removeLayer(eventMarker);

			delete newEvents[newEventDataID];
		}

	}

	this.editEvent = function(eventDataID, newFlag) {

		setUpCategoryOptions()
		setUpOccurrence()

		if (newFlag) {
			var eventData = newEvents[eventDataID];
		} else {
			var eventData = establishedEvents[eventDataID];
		}
		

		if(typeof(eventData) === "undefined") {
			displayGeneralFailureModal();
		}
		else {
			var modalForm = $("#edit-event-modal");
			modalForm.data("eventDataID", eventDataID);
			if (typeof(eventData.id) === "undefined") {
				modalForm.modal("show");
				$('#edit-event-modal').attr('new', newFlag)
				eventData.newEventFormData = {};
			} else if (typeof(eventData.id) === "number") {
				modalForm.modal("show");
				$('#edit-event-modal').attr('new', newFlag)
				$('#event-name').val(eventData.name)
				$('#event-description').val(eventData.description)
				$('#event-category').val(eventData.category.name)

				var allDateTime = ''
				removeOccurrenceField(1);
				for(i = 0; i < Math.min(eventData.occurrences.length,4); i++){
					var dateTime = new Date( eventData.occurrences[i].timestamp );
					var time = dateTime.toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});
					var date = dateTime.toLocaleDateString();
					allDateTime = allDateTime + date + " " + time
					var index=i+1;
					displayOccurrenceField(index);
					$('#event-occurrence'+index).val(allDateTime)
				}
			}
		}
	}

	function loadEventFormWithData() {
		var eventForm = getContentTemplateClone("#edit-event-modal");


	}

	function storeEventFormData() {
		var modalForm = $("#edit-event-modal");
		var eventDataID = modalForm.data("eventDataID");
		var newFlag = $("#edit-event-modal").attr('new')

		if (newFlag == 'false') {
			var eventData = establishedEvents[eventDataID];
		} else {
			var eventData = newEvents[eventDataID];
		}
		
		var occurrences=[];
		for(var i=1; i<=4; i++){
			var occurrence=$("#event-occurrence"+i).val();
			if(occurrence!=""&& typeof occurrence != "undefined" && occurrence != null){
				occurrences.push((new Date(occurrence).getTime()));
			}
		}
		if (eventData !== undefined && typeof(eventData.newEventFormData) !== "undefined") {
			eventData.newEventFormData.eventName = $("#event-name").val();
			eventData.newEventFormData.eventDescription = $("#event-description").val();
			eventData.newEventFormData.eventCategory = $("#event-category").val();
			eventData.newEventFormData.eventOccurrences = occurrences;
		} else {
			eventData.name = $("#event-name").val();
			eventData.description = $("#event-description").val();
			eventData.category.name = $("#event-category").val();	
			eventData.occurrenceTimestamps = occurrences;
		}
	}

	$('#event-save').on(
			'click', function() {
				var eventName = $("#event-name").val();
				var eventDescription = $("#event-description").val();
				var occurrences=[];
				for(var i=1; i<=4; i++){
					var occurrence=$("#event-occurrence"+i).val();
					if(occurrence!=""&& typeof occurrence != "undefined" && occurrence != null){
						occurrences.push((new Date(occurrence).getTime()));
					}
				}
				var eventCategory = $('#event-category').val();
				if (eventName == "" || eventDescription == "" || eventCategory == "" || occurrences.length==0) {
					$('#formEventFeedback').html('All the fields are required');
				} else if (validateEventDescription(eventDescription) == false) {
					$('#formEventFeedback').html('Event description must be between than 5 and 120 characters');
				} else {
					event.preventDefault();
					storeEventFormData();
					submitEventForm();
					clearEventForm();
				}
	});

	$('#event-close').on(
			'click', function() {
				clearEventForm();
	});

	function clearEventForm(){
		$("#event-name").val('');
		$("#event-description").val('');
		$('#event-category').val('');
		$('#formEventFeedback').html('');
		for(var i=1; i<=4; i++){
			var occurrence=$("#event-occurrence"+i).val();
			if(occurrence!=""&& typeof occurrence != "undefined" && occurrence != null){
				$("#event-occurrence"+i).val('');
			}
		}
	}

	function validateEventDescription(eventDescription){
		if (eventDescription.length < 5 || eventDescription.length > 120) {
			return false;
		} else {
			return true;
		}
	}


	//$('#event-category').selectmenu();

	function submitEventForm() {
		var modalForm = $("#edit-event-modal");
		var eventDataID = modalForm.data("eventDataID");
		var newFlag = $("#edit-event-modal").attr('new')

		if (newFlag == 'true') {
			updateEvent(eventDataID, newFlag, function(event) {
				
				mapManager.discardNewEvent(eventDataID);

				modalForm.modal("hide");

				establishedEvents[event.id] = event;
				
				//TODO: Added check if the new event is indeed nearby
				if (isNearby(event)) {
					gather.global.nearEvents.push(event);	
				} else {
					displayNewEventNotNearbyModal();
				}
				
				//loadEventsFirstView(currentUserCoordinates);

				placeEstablishedEventMarker(event, true);

				
				gather.global.joinedEvents.push(event);
				gather.global.ownedEvents.push(event);

				refreshEventListAndMarkers();

			}, function() {
				modalForm.modal("hide");
				displayGeneralFailureModal();
			});
		} else {
			updateEvent(eventDataID, newFlag, function(event) {
				refreshEventListAndMarkers();
				modalForm.modal("hide");
			}, function() {
				modalForm.modal("hide");
				displayGeneralFailureModal();
			});
		}
		

		
	}
	
	function isNearby(anEvent){
		var eCoordinates = {
				latitude: anEvent.location.latitude,
				longitude: anEvent.location.longitude
			}
		
		return eventSearchRadiusInMiles > distanceWithCoods(eCoordinates,currentUserCoordinates,'M');
	}
	
/**
 * REST call to update/create the event
 */
	function updateEvent(eventDataID, newFlag, successCallback, failureCallback) {
		if (newFlag == 'true') {
			var eventData = newEvents[eventDataID];
		} else {
			var eventData = establishedEvents[eventDataID];
		}
		

		var eventMarker = eventData.eventMarker;
		var markerPosition = eventMarker.getLatLng();
		var markerCoordinates = {
			latitude: markerPosition.lat,
			longitude: markerPosition.lng
		};

		if (newFlag == 'true') {
			var requestObject = {
				eventName: eventData.newEventFormData.eventName,
				eventCoordinates: markerCoordinates,
				eventDescription: eventData.newEventFormData.eventDescription,
				eventCategory: eventData.newEventFormData.eventCategory,
				eventOccurrences: eventData.newEventFormData.eventOccurrences,
				callerCoordinates: currentUserCoordinates
			};
			var url = "rest/events"
		} else {
	 		var requestObject = {
	 			eventId: eventData.id,
				eventName: eventData.name,
				eventCoordinates: markerCoordinates,
				eventDescription: eventData.description,
				eventCategory: eventData.category.name,
				eventOccurrences: eventData.occurrenceTimestamps,
				callerCoordinates: currentUserCoordinates,
				ownersToAdd: [],
				ownersToRemove: [],
				participantsToAdd: [],
				participantsToRemove: []
			};
			var url = "rest/events/update"
		}

		var requestData = JSON.stringify(requestObject);

		var requestOptions = {
			type: "POST",
			async: false,
			url: url,
			contentType: "application/json; charset=UTF-8",
			data: requestData,
			dataType: "json",
			timeout: 10000,
			success: function(returnvalue) {
				if(typeof(successCallback) === "function") {
					console.log(JSON.stringify(returnvalue.result));
					successCallback(returnvalue.result);
				}
			}
		};

		var response = $.ajax(requestOptions);

		response.fail(function(error) {
			console.log(error);
			if(typeof(failureCallback) === "function") {
				failureCallback();
			}
		});
	}

	function placeEstablishedEventMarker(anEvent, bounceOnAdd) {
		//console.log(JSON.stringify(anEvent))
		//var markerPosition = new L.LatLng(anEvent.coordinates.latitude, anEvent.coordinates.longitude);

		var eCoordinates = {
			latitude: anEvent.location.latitude,
			longitude: anEvent.location.longitude
		}
		var markerPosition = new L.LatLng(eCoordinates.latitude, eCoordinates.longitude);
		//var hotnessColor = determineHotnessColor(anEvent);

		var iconOptions = {
			"marker-size": "large",
			"marker-symbol": "star",
			//"marker-color": hotnessColor
		};

		var bounceOptions = {
			duration: 500,
			height: 100
		};

		var markerOptions = {
			icon: L.mapbox.marker.icon(iconOptions),
		};

		var markerOptions = {
			draggable: false,
			icon: L.mapbox.marker.icon(iconOptions),
			bounceOnAdd: bounceOnAdd,
			bounceOnAddOptions: bounceOptions
		};

		var popupOptions = {
			minWidth: 600
		};

		var eventMarker = L.marker(markerPosition, markerOptions);
		eventMarker.addTo(map);

		anEvent.eventMarker = eventMarker;
		if (isCurrentUserOnwer(anEvent.owners)){
			$('#removeEventBtn').show();
			$('#editEventBtn').show();
		}else{
			$('#removeEventBtn').hide();
			$('#editEventBtn').hide();
		}
		
		if (isUserParticipant(anEvent.participants)){
			$('#leaveEventBtn').show();
			$('#joinEventBtn').hide();
		}else{
			$('#leaveEventBtn').hide();
			$('#joinEventBtn').show();
		}
		
		var establishedEventContent = getContentTemplateClone("#established-event-content-template");

		$(establishedEventContent).find("button").each(function(index) {
			$(this).attr("data-event-id", anEvent.id);
		});

		//TODO: distance from caller should be calculated based on anEvent object
		var distanceFromCaller=distance(eCoordinates.latitude, eCoordinates.longitude,currentUserCoordinates.latitude, currentUserCoordinates.longitude,'M');
		var establishedEventHTML = establishedEventContent[0].outerHTML;


		var unixtime = anEvent.occurrences[0].timestamp;
		var datetime = new Date( unixtime );
		var time = datetime.toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});
		var date = datetime.toLocaleDateString();
		timeDisplay = date + ', ' + time 
		establishedEventHTML = sprintf(establishedEventHTML, anEvent.id, anEvent.name, anEvent.category.name, anEvent.description, timeDisplay, distanceFromCaller);

		eventMarker.bindPopup(establishedEventHTML, popupOptions);
	}

	function isCurrentUserOnwer(owners){
		var result=false;
		for(var i = 0; i < owners.length; i++){
			if(owners[i].displayName == gather.global.currentDisplayName){
				result = true;
			}
		}
		return result;
	}
	
	function isUserParticipant(participants){
		var result=false;
		for(var i = 0; i < participants.length; i++){
			if(participants[i].displayName == gather.global.currentDisplayName){
				result = true;
			}
		}
		return result;
	}
	
	function getNearByEvents() {
		var hour = 730;

		$.ajax({
		 	accepts: "application/json",
			type : "PUT",
			url : "rest/events",
			contentType: "application/json; charset=UTF-8",
			dataType: "json",
			data : '{ "latitude" : ' + userCoordinates.latitude + ', "longitude" : ' + userCoordinates.longitude + ', "radiusMi": ' + eventSearchRadiusInMiles + ', "hour": ' + hour + ' }',
			success : function(returnvalue) {
				signedIn = true;
				gather.global.nearEvents = returnvalue.results;
				
				refreshEventListAndMarkers();
			},
			error: function(jqXHR, textStatus, errorThrown) {
			    alert(errorThrown);
				if (errorThrown == "Found") {
					signedIn = true;
					alert("error")
					updateGreeting();
					headerSelect();
				} else {
					signedIn = false;
					headerSelect();
				}

			}
		});
	}
	
	function addAnEventTo(anEvent, events){
		placeEstablishedEventMarker(anEvent, true);
		events[anEvent.id] = anEvent;
	}
	
	function removeAnEventFrom(anEvent, events){
		var eventMarker = establishedEvents[anEvent.id].eventMarker;
		map.removeLayer(eventMarker);
		delete events[anEvent.id];
	}
	
	function refreshEventMarkers(events){
		//Remove current establishedEvents
		for(var eventId in establishedEvents){
			removeAnEventFrom(establishedEvents[eventId], establishedEvents);
		}
		
		//Re-Add events to map
		for(var i = 0; i < events.length; i++){		
			addAnEventTo(events[i],establishedEvents);
		}
	}
	
	function joinedEvents() {
		$.ajax({
		 	accepts: "application/json",
			type : "GET",
			url : "/rest/events/userJoined",
			contentType: "application/json; charset=UTF-8",
			success : function(returnvalue) {
				gather.global.joinedEvents = returnvalue.results;
			},
			error: function(jqXHR, textStatus, errorThrown) {
			    alert(errorThrown);
				if (errorThrown == "Found") {
					signedIn = true;
					alert("error")
					updateGreeting();
					headerSelect();
				} else {
					signedIn = false;
					headerSelect();
				}

			}
		});
	}

	function ownedEvents() {
		$.ajax({
		 	accepts: "application/json",
			type : "GET",
			url : "/rest/events/userOwned",
			contentType: "application/json; charset=UTF-8",
			success : function(returnvalue) {
				gather.global.ownedEvents = returnvalue.results;
			},
			error: function(jqXHR, textStatus, errorThrown) {
			    alert(errorThrown);
				if (errorThrown == "Found") {
					signedIn = true;
					alert("error")
					updateGreeting();
					headerSelect();
				} else {
					signedIn = false;
					headerSelect();
				}

			}
		});
	}

	this.determineCoordByZipCode = function(zipCode){
		// using Google API for zip search because mapbox is awfully inaccurate.
		// What Souhayl had was great but this is 100 times faster.
		var url = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&components=country:US|postal_code:"+zipCode
		$.get(url, function (data){
			if (data.status == 'ZERO_RESULTS') {
				return -1;
			} else if (data.status == 'OK') {
				uCoordinates = {
					latitude: data.results[0].geometry.location.lat,
					longitude: data.results[0].geometry.location.lng
				}
				processUserCoordinates(uCoordinates);
				return 0;
			}
		});
	}
	
	this.determineAddressByCoord = function(lat, lng){
		var url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=AIzaSyCh3wRAk3nGvfqUwC2SjkqVBX5AwUGh8KE"
		var full_address = ''
		$.ajax({
			  async: false,
			  url: url,
			  dataType: "json",
			  success: function(data) {
				  if (data.status == 'ZERO_RESULTS') {
						full_address = 'Address not found'
					} else if (data.status == 'OK') {
						// always return the first result which is most relevant.
						full_address = data.results[0].formatted_address;
					}
				}
			});	
		return full_address;
	}
	
// 	this.determineCoordByZipCode = function(zipCode) {

// 		console.log("The user denied the request for geolocation.");
//     	var httpRequest = new XMLHttpRequest();
//         httpRequest.open("GET", 'zipcode.csv', false);
//         httpRequest.send(null);
//         //alert( httpRequest.responseText );
//         CSVContents = httpRequest.responseText;
//         //console.log($.csv.toObjects(CSVContents));
// //        var zipcode = prompt('Please enter your Zip','Zip Code');
// //        if (zipcode == null || zipcode == "") {
// //            alert("you did not enter a zip please try again");
// //            zipcode = prompt('Please enter your Zip','Zip Code');
// //        }
//         var zipList = $.csv.toObjects(CSVContents);

//         console.log(zipList);

//         var uCoordinates = null

// //        jQuery.grep(zipList, function( zip, i ) {
// //        	if (zip == zipCode) {
// //        		var i = zipList.index(zipCode)
// //        		uCoordinates = {
// //      				latitude: zipList[i].latitude,
// //    				longitude: zipList[i].longitude
// //    				};
// //        	}
// //        });

//         for (var i in zipList) {
//         	if(zipList[i].zip == zipCode){
// 				uCoordinates = {
// 				latitude: zipList[i].latitude,
// 				longitude: zipList[i].longitude
// 				}
//         	}
//         }
//         if (uCoordinates == null) {
//         	return -1;
//         } else {
//         	processUserCoordinates(uCoordinates);
//         	return 0;
//         }
//     }

	function displayGeolocationUnsupportedModal() {
		$("#geolocation-unsupported-modal").modal("show");
	}

	function displayGeneralFailureModal() {
		$("#general-failure-modal").modal("show");
	}
	
	function displayNewEventNotNearbyModal() {
		$("#new-event-not-nearby").modal("show");
	}

	this.showPop = function(eventId) {
		
		var myEvent = establishedEvents[eventId];
		
		eMarker = myEvent.eventMarker;
		eMarker.openPopup();
	}

	this.joinEvent = function(eventID) {
		var establishedEvent = establishedEvents[eventID];

		if(typeof(establishedEvent) === "undefined") {
			displayGeneralFailureModal();
		}
		else {

			if (gather.global.session.signedIn == false){
				$("#anonymous-user-join-event-failure-modal").modal("show");
				$('#failureJoinEventModalRegisterBtn').on('click', function() {
					$('#registerButton').trigger('click');
				});
			}else {
				establishedEvent.eventMarker.closePopup();

				doJoinEvent(eventID, function(updatedEvent) {
					$("#event-join-modal").modal("show");
					gather.global.joinedEvents.push(updatedEvent);
					refreshEventListAndMarkers();
				}, function() {
					displayGeneralFailureModal();
				});
			}
		}
	}

	function doJoinEvent(eventID, successCallback, failureCallback) {

		var requestOptions = {
			type: "POST",
			url: "/rest/events/join",
			contentType: "application/json; charset=UTF-8",
			data: '{ "eventId" : ' + eventID +' }',
			dataType: "json",
			timeout: 10000,
			success: function(returnvalue) {
				if(typeof(successCallback) === "function") {
					successCallback(returnvalue.result);	
				}
				
			}
		};

		var response = $.ajax(requestOptions);

		response.fail(function(error) {
			console.log(error);

			if(typeof(failureCallback) === "function") {
				failureCallback();
			}
		});
	}
	
	this.leaveEvent = function(eventID) {
		var establishedEvent = establishedEvents[eventID];

		if(typeof(establishedEvent) === "undefined") {
			displayGeneralFailureModal();
		}
		else {
			establishedEvent.eventMarker.closePopup();
			
			doLeaveEvent(eventID, function(updatedEvent) {
				$("#event-leave-modal").modal("show");
				var indexLeft = gather.global.joinedEvents.map(function(x) {return x.id; }).indexOf(updatedEvent.id);
				if (indexLeft > -1) {
					gather.global.joinedEvents.splice(indexLeft, 1);
				}				
				refreshEventListAndMarkers();
			}, function() {
				displayGeneralFailureModal();
			});
		}
	}
	
	function doLeaveEvent(eventID, successCallback, failureCallback) {
		
		var requestOptions = {
			type: "POST",
			url: "/rest/events/leave",
			contentType: "application/json; charset=UTF-8",
			data: '{ "eventId" : ' + eventID +' }',
			dataType: "json",
			timeout: 10000,
			success: function(returnvalue) {
				if(typeof(successCallback) === "function") {
					successCallback(returnvalue.result);	
				}
			}
		};

		var response = $.ajax(requestOptions);

		response.fail(function(error) {
			console.log(error);

			if(typeof(failureCallback) === "function") {
				failureCallback();
			}
		});
	}
	
	this.removeEvent = function(eventID) {
		var establishedEvent = establishedEvents[eventID];

		if(typeof(establishedEvent) === "undefined") {
			displayGeneralFailureModal();
		}
		else {

			if (gather.global.session.signedIn == false){
				displayGeneralFailureModal();

			}else {
				establishedEvent.eventMarker.closePopup();

				doRemoveEvent(eventID, function(updatedEvent) {
					$("#event-removed-modal").modal("show");

					var indexJoined = gather.global.joinedEvents.map(function(x) {return x.id; }).indexOf(updatedEvent.id);
					var indexOwned = gather.global.ownedEvents.map(function(x) {return x.id; }).indexOf(updatedEvent.id);
					var indexNearby = gather.global.nearEvents.map(function(x) {return x.id; }).indexOf(updatedEvent.id);
					
					if (indexJoined > -1) {
						gather.global.joinedEvents.splice(indexJoined, 1);
					}	
					if (indexOwned > -1) {
						gather.global.ownedEvents.splice(indexOwned, 1);
					}	
					if (indexNearby > -1) {
						gather.global.nearEvents.splice(indexNearby, 1);
					}	
					
					refreshEventListAndMarkers();
				}, function() {
					displayGeneralFailureModal();
				});
			}
		}
	}

	function doRemoveEvent(eventID, successCallback, failureCallback) {

		var requestOptions = {
			type: "POST",
			url: "/rest/events/remove",
			contentType: "application/json; charset=UTF-8",
			data: '{ "eventId" : ' + eventID +' }',
			dataType: "json",
			timeout: 10000,
			success: function(returnvalue) {
				if(typeof(successCallback) === "function") {
					successCallback(returnvalue.result);
				}
			}
		};

		var response = $.ajax(requestOptions);

		response.fail(function(error) {
			console.log(error);

			if(typeof(failureCallback) === "function") {
				failureCallback();
			}
		});
	}

	function createCommaList(arrayUserObj) {
		user_list = ''
		for(i = 0; i < arrayUserObj.length; i++){
			user_list = user_list + arrayUserObj[i].displayName + ', '
		}
		if(i > 1) {
			// removing comma at the end
			user_list = user_list.slice(0, -2)
		}
		return user_list
	}

	this.listPart = function(eventID) {

		var eventData = establishedEvents[eventID];

		if (typeof(eventData) === "undefined") {
			displayGeneralFailureModal();
		} else {
			var modalForm = $("#edit-participant-modal");
			modalForm.data("eventDataID", eventID);
			owner_list = createCommaList(eventData.owners);
			participant_list = createCommaList(eventData.participants);

			if (typeof(eventData.id) === "number") {
				modalForm.modal("show");
				$('#event-participants').val(participant_list)
				$('#event-owners').val(owner_list)
			} else {
				displayGeneralFailureModal();
			}
		}
	}
	
	function updateEventMarker(eventMarker, coordinates, iconOptions) {
		if(coordinates !== null && typeof(coordinates) === "object") {
			var eventPosition = new L.LatLng(coordinates.latitude, coordinates.longitude);
			eventMarker.setLatLng(eventPosition);
		}

		if(iconOptions !== null && typeof(iconOptions) === "object") {
			var icon = new L.mapbox.marker.icon(iconOptions);
			eventMarker.setIcon(icon);
		}

		eventMarker.update();
	}
	
	function refreshEventListAndMarkers(){		
		if (gather.global.currentEventList == ViewingNearByEvents){
			loadEventsFirstView(userCoordinates);
			refreshEventMarkers(gather.global.nearEvents);
		} else if (gather.global.currentEventList == ViewingJoinedEvents){
			loadJoinedEvents(userCoordinates);
			refreshEventMarkers(gather.global.joinedEvents);
		} else if(gather.global.currentEventList == ViewingOwnedEvents){
			loadOwnedEvents(userCoordinates);
			refreshEventMarkers(gather.global.ownedEvents);
		} else {
			displayGeneralFailureModal();
		}		
	}
	
	$('#showNearBy').on('click', function(){
		gather.global.currentEventList = ViewingNearByEvents;
		rightPaneSelect();
		getNearByEvents();
		refreshEventListAndMarkers();
	})
	
	$('#showJoined').on('click', function(){
		gather.global.currentEventList = ViewingJoinedEvents;
		rightPaneSelect();
		joinedEvents();
		refreshEventListAndMarkers();
	})
	
	$('#showOwned').on('click', function(){
		gather.global.currentEventList = ViewingOwnedEvents;
		rightPaneSelect();
		ownedEvents();
		refreshEventListAndMarkers();
	})
}

function distance(lat1, lon1, lat2, lon2, unit) {
	var radlat1 = Math.PI * lat1/180
	var radlat2 = Math.PI * lat2/180
	var theta = lon1-lon2
	var radtheta = Math.PI * theta/180
	var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
	dist = Math.acos(dist)
	dist = dist * 180/Math.PI
	dist = dist * 60 * 1.1515
	if (unit=="K") { dist = dist * 1.609344 }
	if (unit=="N") { dist = dist * 0.8684 }
	return dist
}

function distanceWithCoods(cood1, cood2, unit) {
	var lat1 = cood1.latitude;
	var lat2 = cood2.latitude;
	var lon1 = cood1.longitude;
	var lon2 = cood2.longitude;
	return distance(lat1, lon1, lat2, lon2, unit) ;
}