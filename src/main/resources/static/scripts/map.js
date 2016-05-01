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
	// var uCoordinates = null;
	var currentUserCoordinates = null;
	var userMarker = null;
	var eventMarker = null;
	var searchRadiusCircle = null;

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
			
			CurrentUserCoordinates = {
				latitude: currentPosition.coords.latitude,
				longitude: currentPosition.coords.longitude
			}

			map.setView([CurrentUserCoordinates.latitude, CurrentUserCoordinates.longitude], 10);

			if(typeof(successCallback) === "function") {
				successCallback(CurrentUserCoordinates);
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

	this.filter = function(hour, category, radius) {
		getNearByEvents(hour, category, radius)
	}

	this.performAction = function() {
		if(geolocationSupported) {
			// Get and process the user's current location.
			determineUserCoordinates(function(initialUserCoordinates) {
				currentUserCoordinates = initialUserCoordinates
				try {
					processUserCoordinates();
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

	function processUserCoordinates(hour, categories, radius) {
		if(CurrentUserCoordinates == null) {
			geolocationErrorCount++;

			if(geolocationErrorCount > 2) {
				throw new GeolocationFailure("Geolocation has consecutively failed at least 3 times.");
			}
		}
		else {
			geolocationErrorCount = 0;

			placeUserMarker();
			getNearByEvents(hour, categories, radius);
			currentUserCoordinates = CurrentUserCoordinates;
			if (gather.global.session.signedIn == true){
				joinedEvents();
				ownedEvents();
			}
		}
	}
 
	function placeUserMarker() {
		var markerPosition = new L.LatLng(CurrentUserCoordinates.latitude, CurrentUserCoordinates.longitude);

		if (eventSearchRadiusInMiles >= 50) {
				currentZoomLevel = 10;
			} else if (eventSearchRadiusInMiles >= 25) {
				currentZoomLevel = 10;
			} else if (eventSearchRadiusInMiles >= 10) {
				currentZoomLevel = 11;
			} else if (eventSearchRadiusInMiles >= 5) {
				currentZoomLevel = 12;
			} else {
				currentZoomLevel = 13;
			}

		map.setView([CurrentUserCoordinates.latitude, CurrentUserCoordinates.longitude], currentZoomLevel);

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

		var eventSearchRadiusInMeters = eventSearchRadiusInMiles * 1609.34;

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
			searchRadiusCircle.setRadius(eventSearchRadiusInMeters);
		}

		setUserMarkerPopup();
	}

	function setUserMarkerPopup() {
		var simpleUserMarkerHTML = $("#simple-user-marker-content-template").html();
		simpleUserMarkerHTML = sprintf(simpleUserMarkerHTML, CurrentUserCoordinates.latitude, CurrentUserCoordinates.longitude);

		userMarker.bindPopup(simpleUserMarkerHTML);
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

	function addOccurenceCallback(){
		occurrenceIndex += 1
		if (occurrenceIndex <= 4) {
			displayOccurrenceField(occurrenceIndex)
			$('#removeOccurrence').prop("disabled",false);
		}
		if (occurrenceIndex == 4) {
			$('#addOccurrence').prop("disabled",true);
		}	
	}
	$('#addOccurrence').on('click', occurrenceIndex, addOccurenceCallback);
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

				var allDateTime = '';
				refreshOccurrenceTimestamps(eventData);
				for(var i = 0; i < Math.min(eventData.occurrenceTimestamps.length,4); i++){
					var dateTime = new Date( eventData.occurrenceTimestamps[i] );
					var time = dateTime.toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});
					var date = dateTime.toLocaleDateString();
					dateTime = date + " " + time;
					addOccurenceCallback();
					var index=i+1;
					$('#event-occurrence'+index).val(dateTime);
				}
			}
		}
	}

	function refreshOccurrenceTimestamps(eventData){
		eventData.occurrenceTimestamps=[];
		for(var i = 0; i < eventData.occurrences.length; i++){
			var timestamp = eventData.occurrences[i].timestamp;
			eventData.occurrenceTimestamps.push(timestamp);
		}
		eventData.occurrenceTimestamps.sort();
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
			eventData.newEventFormData.eventOccurrences = occurrences.sort();
		} else {
			eventData.name = $("#event-name").val();
			eventData.description = $("#event-description").val();
			eventData.category.name = $("#event-category").val();	
			eventData.occurrenceTimestamps = occurrences.sort();
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
					storeEventFormData();
					submitEventForm();
					clearEventForm();
				}
				refreshEventGlobalVariables();
				refreshEventListAndMarkers();
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

	function submitEventForm() {
		var modalForm = $("#edit-event-modal");
		var eventDataID = modalForm.data("eventDataID");
		var newFlag = $("#edit-event-modal").attr('new')

		if (newFlag == 'true') {
			updateEvent(eventDataID, newFlag, function(event) {
				
				mapManager.discardNewEvent(eventDataID);

				modalForm.modal("hide");

				establishedEvents[event.id] = event;
				
				if (isNearby(event)) {
					gather.global.nearEvents.push(event);	
				} else {
					displayNewEventNotNearbyModal();
				}

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
				callerCoordinates: currentUserCoordinates
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


		refreshOccurrenceTimestamps(anEvent);
		var unixtime = mostRecentOccurrence(anEvent.occurrences)
		var datetime = new Date( unixtime );
		var time = datetime.toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});
		var date = datetime.toLocaleDateString();
		var timeDisplay = date + ', ' + time 
		var url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + eCoordinates.latitude + "," + eCoordinates.longitude + "&key=AIzaSyCh3wRAk3nGvfqUwC2SjkqVBX5AwUGh8KE"
		var full_address = ''
		$.ajax({
			    // async: false, //commented to enhance performance by 3 seconds!
			    url: url,
			    dataType: "json",
			    success: function(data) {
				    if (data.status == 'ZERO_RESULTS') {
						full_address = 'Address not found'

					} else if (data.status == 'OK') {
						// always return the first result which is most relevant.
						full_address = data.results[0].formatted_address;
					} else if (data.status == 'OVER_QUERY_LIMIT') {
						full_address = 'Our server has daily limited address query from Google.'
					}
					establishedEventHTML = sprintf(establishedEventHTML, anEvent.id, anEvent.name, anEvent.category.name, timeDisplay, full_address, distanceFromCaller, anEvent.description);
					eventMarker.bindPopup(establishedEventHTML, popupOptions);
				},
				error: function(jqXHR, textStatus, errorThrown) {
                    var responseMessage = $.parseJSON(jqXHR.responseText).message;
                    alert(responseMessage);
                }
			});	
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
	
	function getNearByEvents(hour, categories, radius) {
		if (typeof hour === "undefined" || hour === null) { 
		    hour = 730;
		}
		if (typeof categories === "undefined" || categories === null) { 
		    categories = [""]
		}
		if (typeof radius === "undefined" || radius === null) { 
		    radius = eventSearchRadiusInMiles
		} else {
			eventSearchRadiusInMiles = radius
		}

		var data = '  '

		data = data + '"latitude" : ' + CurrentUserCoordinates.latitude + ', '
		data = data + '"longitude" : ' + CurrentUserCoordinates.longitude + ', '
		data = data + '"radiusMi": ' + radius + ', '
		data = data + '"hour": ' + hour + ', '

		if (emptyStringArray(categories)) {
			data = data + '"categories": ['
			for (var i = 0; i < categories.length; i++) {
				if (categories[i] !== "") {
					data = data + '"' + categories[i] + '", '
				}
			}
			data = data.slice(0,-2)  // removing the last comma
			data = data + '], '
		}

		data = data.slice(0,-2)  // removing the last comma

		$.ajax({
		 	accepts: "application/json",
			type : "PUT",
			url : "rest/events",
			contentType: "application/json; charset=UTF-8",
			dataType: "json",
			data : '{'+data+'}',
			async: false,
			success : function(returnvalue) {
				signedIn = true;
				gather.global.nearEvents = returnvalue.results;
				placeUserMarker();
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
			async: false,
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
			async: false,
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

	this.determineCoordByZipCode = function(zipCode, showmap, defaultTimeWindow, categories, radius){
		// using Google API for zip search because mapbox is awfully inaccurate.
		// What Souhayl had was great but this is 100 times faster.
		if (typeof showmap === "undefined" || showmap === null) { 
			showmap = true; 
		}
		var url = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&components=country:US|postal_code:"+zipCode
		var flag = false
		$.ajax({
			async: false,
			url: url,
			dataType: "json",
			success: function(returnvalue) {
				if (returnvalue.status == 'ZERO_RESULTS') {
					flag = false;
				} else if (returnvalue.status == 'OK') {
					CurrentUserCoordinates = {
						latitude: returnvalue.results[0].geometry.location.lat,
						longitude: returnvalue.results[0].geometry.location.lng
					}
					currentUserCoordinates = CurrentUserCoordinates;
					if (showmap) {
						processUserCoordinates(defaultTimeWindow, categories, radius);
					}
					flag = true;
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
                var responseMessage = $.parseJSON(jqXHR.responseText).message;
                alert(responseMessage);
            }
		});
		return flag
	}
	
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
					refreshEventGlobalVariables();
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
				refreshEventGlobalVariables();
				refreshEventListAndMarkers();
			}, function(jqXHR, textStatus, errorThrown){
				var responseMessage = $.parseJSON(jqXHR.responseText).message;
				if(responseMessage != ""){
					
					displayEventLeaveErrorModal(responseMessage);
				}else{
					displayGeneralFailureModal();
				}
			});
		}
	}
	function displayEventLeaveErrorModal(message){
		$("#eventLeaveErrorMessage").html(message);
		$("#event-leave-error-modal").modal("show");
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

		response.fail(function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);

			if(typeof(failureCallback) === "function") {
				failureCallback(jqXHR, textStatus, errorThrown);
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
		for(var i = 0; i < arrayUserObj.length; i++){
			if(user_list == ''){
				user_list = arrayUserObj[i].displayName;
			}else{
				user_list = user_list + ', '+ arrayUserObj[i].displayName;
			}
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
			setupDisplayNamesAutocomplete("rest/registrants/displayname");
            console.log(JSON.stringify(gather.global.allDisplayName));
			if (typeof(eventData.id) === "number") {
				modalForm.modal("show");
				$('#event-participants').val(participant_list)
				$('#event-owners').val(owner_list)
			} else {
				displayGeneralFailureModal();
			}
			if(!gather.global.session.signedIn || !isCurrentUserOnwer(eventData.owners)){
				setParticipantsForm(true);
			}else{
				setParticipantsForm(false);
			}
		}
	}
	
	function setParticipantsForm(enable){
		$("#addParticipant").prop("disabled",enable);
		$("#addOwner").prop("disabled",enable);
		$('#event-participants').prop("disabled",enable);
		$('#event-owners').prop("disabled",enable);
		$('#search-display-name').prop("disabled",enable);
		$('#participant-save').prop("disabled",enable);
	}
	
	function setupDisplayNamesAutocomplete(url){	
	    $.ajax({
	        accepts: "application/json",
	        type : "GET",
	        url : url,
	        contentType: "application/json; charset=UTF-8",
	        success : function(returnvalue) {
	            var registrants=returnvalue.results;
	            for(var i=0;i<registrants.length; i++){
	            	gather.global.allDisplayName.push(registrants[i]);
	            }
	            if(returnvalue.next!=null){
	            	setupDisplayNamesAutocomplete(returnvalue.next)
	            }
	            var input = document.getElementById("search-display-name");
	            new Awesomplete(input, {
	    			list: gather.global.allDisplayName
	    		});
	        }
	    });	
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
			loadEventsFirstView(currentUserCoordinates);
			refreshEventMarkers(gather.global.nearEvents);
		} else if (gather.global.currentEventList == ViewingJoinedEvents){
			loadJoinedEvents(currentUserCoordinates);
			refreshEventMarkers(gather.global.joinedEvents);
		} else if(gather.global.currentEventList == ViewingOwnedEvents){
			loadOwnedEvents(currentUserCoordinates);
			refreshEventMarkers(gather.global.ownedEvents);
		} else {
			displayGeneralFailureModal();
		}		
	}
	
	function refreshEventGlobalVariables(){
		getNearByEvents(hour, categories, radius);
		joinedEvents();
		ownedEvents();
	}
	
	$('#showNearBy').on('click', function(){
		gather.global.currentEventList = ViewingNearByEvents;
		rightPaneSelect();
		getNearByEvents();
		refreshEventListAndMarkers();
	});
	
	$('#showJoined').on('click', function(){
		gather.global.currentEventList = ViewingJoinedEvents;
		rightPaneSelect();
		joinedEvents();
		refreshEventListAndMarkers();
	});
	
	$('#showOwned').on('click', function(){
		gather.global.currentEventList = ViewingOwnedEvents;
		rightPaneSelect();
		ownedEvents();
		refreshEventListAndMarkers();
	});
	
	$('#addParticipant').on('click', function(){
		var currentList = $('#event-participants').val();
		var listArray = currentList.split(", ");
		var selectedName=$("#search-display-name").val();
		if(contains(listArray,selectedName)){
			alert("Selected registrant is already a participant.");
		}else{
			$('#event-participants').val(currentList + ', '+selectedName);
		}
	});
	
	$('#addOwner').on('click', function(){
		var currentList = $('#event-owners').val();
		var listArray = currentList.split(", ");
		var selectedName=$("#search-display-name").val();
		if(contains(listArray,selectedName)){
			alert("Selected registrant is already an owner.");
		}else{
			$('#event-owners').val(currentList + ', '+selectedName);
		}
	});
	
	$('#participant-save').on(
			'click', function() {
				var ownerList = $('#event-owners').val();
				var ownerArray = ownerList.split(", ");
				var participantList = $('#event-participants').val();
				var participantArray = participantList.split(", ");
				var modalForm = $("#edit-participant-modal");
				var eventDataID = modalForm.data("eventDataID");
				updateParticipantsAndOwners(eventDataID, ownerArray, participantArray,
						function(event){
							$("#formParticipantFeedback").html("<b><u>"+event.name+"</u></b> updated successfully.");
							refreshEventGlobalVariables();
							refreshEventListAndMarkers();
						},
						function(jqXHR, textStatus, errorThrown){
							var responseMessage = $.parseJSON(jqXHR.responseText).message;
                            $('#formParticipantFeedback').html(responseMessage);
						});
				
	});
	
	$('#participant-close').on(
			'click', function() {
				$("#formParticipantFeedback").html("");
			});
			
	function updateParticipantsAndOwners(eventDataID, ownerArray, participantArray, successCallback, failureCallback) {
		var eventData = establishedEvents[eventDataID];
	 		var requestObject = {
	 			eventId: eventData.id,
				participants: participantArray,
				owners: ownerArray
			};
			var url = "rest/events/update"

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

		response.fail(function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);
			if(typeof(failureCallback) === "function") {
				failureCallback(jqXHR, textStatus, errorThrown);
			}
		});
	}

}

function contains(array, str) {
    var i = array.length;
    while (i--) {
       if (array[i].trim() === str.trim()) {
           return true;
       }
    }
    return false;
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