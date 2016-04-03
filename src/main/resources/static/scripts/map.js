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
			var userCoordinates = {
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
			//determineCoordByZipCode();
		}
	}

	var geolocationErrorCount = 0;

	function processUserCoordinates(userCoordinates) {
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
				getNearByEvents(userCoordinates);
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

			setUpCategoryOptions()

			eventMarker.addTo(map);

			eventMarker.openPopup();
		}
	}
    
	function setUpCategoryOptions(){

		var newOptions={};
		var catArray = gather.global.categories;
		var $categories = $( "#new-event-category" );
		$categories.empty();
		for (var i = 0; i < catArray.length; i++) {
			newOptions[catArray[i].name] = catArray[i].name;
			$categories.append($("<option></option>")
				     .attr("value", catArray[i].name).text(catArray[i].name));
		}
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

	this.editNewEvent = function(newEventDataID) {
		var eventData = newEvents[newEventDataID];

		if(typeof(eventData) === "undefined") {
			displayGeneralFailureModal();
		}
		else {
			displayEditNewEventModal(newEventDataID);
		}
	}
	
	function displayEditNewEventModal(newEventDataID) {
		var modalForm = $("#edit-new-event-modal");
		modalForm.data("newEventDataID", newEventDataID);

		var eventData = newEvents[newEventDataID];
		if(typeof(eventData.newEventFormData) === "undefined") {
			eventData.newEventFormData = {};
		}

		modalForm.on("show.bs.modal", function(event) {
			//alert("lord");
			//loadNewEventFormData();
		});

		modalForm.on("hidden.bs.modal", function(event) {
			//alert("store")
			//storeNewEventFormData();
		});

		modalForm.modal("show");
	}

	function loadNewEventFormData() {
		var modalForm = $("#edit-new-event-modal");
		var newEventDataID = modalForm.data("newEventDataID");

		var eventData = newEvents[newEventDataID];

		$("#new-event-name").val(eventData.newEventFormData.eventName);
		$("#new-event-description").val(eventData.newEventFormData.eventDescription);
		$("#new-event-category").val(eventData.newEventFormData.eventCategory);
		$("#new-event-time").val(eventData.newEventFormData.eventTime);
	}

	function storeNewEventFormData() {
		var modalForm = $("#edit-new-event-modal");
		var newEventDataID = modalForm.data("newEventDataID");

		var eventData = newEvents[newEventDataID];

		if(eventData !== undefined) {
			eventData.newEventFormData.eventName = $("#new-event-name").val();
			eventData.newEventFormData.eventDescription = $("#new-event-description").val();
			eventData.newEventFormData.eventCategory = $("#new-event-category").val();
			eventData.newEventFormData.eventTime = $("#new-event-time").val();
		}
	}
	
	$("body").on("submit", "#new-event-form", function(event) {
		event.preventDefault();

		//alert("about to submit the event form!")
		storeNewEventFormData();

		submitNewEventForm();
	});
	
	$('#new-event-time').datetimepicker();
	//$('#new-event-category').selectmenu();
	
	function submitNewEventForm() {
		var modalForm = $("#edit-new-event-modal");
		var newEventDataID = modalForm.data("newEventDataID");
		
		createNewEvent(newEventDataID, function(newEventResponse) {
			newEvent = newEventResponse.result;
			mapManager.discardNewEvent(newEventDataID);

			modalForm.modal("hide");

			establishedEvents[newEvent.id] = newEvent;

			gather.global.nearEvents.push(newEvent);
			loadEventsFirstView(currentUserCoordinates);
			
			placeEstablishedEventMarker(newEvent, true);
			
			//TODO event card not implemented, we currently have event list only
			//addEventCard(newEvent, true);
			//updateEventCountTitle();
			
		}, function() {
			modalForm.modal("hide");
			displayGeneralFailureModal();
		});
	}
/**
 * REST call to create the event
 */
	function createNewEvent(newEventDataID, successCallback, failureCallback) {
		var eventData = newEvents[newEventDataID];

		var eventMarker = eventData.eventMarker;
		var markerPosition = eventMarker.getLatLng();
		var markerCoordinates = {
			latitude: markerPosition.lat,
			longitude: markerPosition.lng
		};

		var utc = (new Date(eventData.newEventFormData.eventTime).getTime());
		var requestObject = {
			eventName: eventData.newEventFormData.eventName,
			eventCoordinates: markerCoordinates,
			eventDescription: eventData.newEventFormData.eventDescription,
			eventCategory: eventData.newEventFormData.eventCategory,
			eventTime: utc,
			callerCoordinates: currentUserCoordinates
		};

		var requestData = JSON.stringify(requestObject);
		
		var requestOptions = {
			type: "POST",
			url: "rest/events",
			contentType: "application/json; charset=UTF-8",
			data: requestData,
			dataType: "json",
			timeout: 10000,
			success: function(result) {
				if(typeof(successCallback) === "function") {
					successCallback(result);
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
		console.log(JSON.stringify(anEvent))
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
		}else{
			$('#removeEventBtn').hide();
		}
		var establishedEventContent = getContentTemplateClone("#established-event-content-template");
		
		$(establishedEventContent).find("button").each(function(index) {
			$(this).attr("data-event-id", anEvent.id);
		});
		
		//TODO: distance from caller should be calculated based on anEvent object
		var distanceFromCaller=distance(eCoordinates.latitude, eCoordinates.longitude,currentUserCoordinates.latitude, currentUserCoordinates.longitude,'M');
		var establishedEventHTML = establishedEventContent[0].outerHTML; 

		timeDisplay = new Date(anEvent.occurrences[0].timestamp);
		establishedEventHTML = sprintf(establishedEventHTML, anEvent.name, anEvent.category.name, anEvent.description, timeDisplay, distanceFromCaller);

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
	
	function getNearByEvents(userCoordinates) {
		var radiusMi = 15;
		var hour = 24;

		$.ajax({
		 	accepts: "application/json",
			type : "PUT",
			url : "rest/events",
			contentType: "application/json; charset=UTF-8",
			dataType: "json",
			data : '{ "latitude" : ' + userCoordinates.latitude + ', "longitude" : ' + userCoordinates.longitude + ', "radiusMi": ' + radiusMi + ', "hour": ' + hour + ' }',
			success : function(returnvalue) {
				signedIn = true;
				gather.global.nearEvents = returnvalue.results;

				console.log(JSON.stringify(gather.global.nearEvents))
				for(var i = 0; i < gather.global.nearEvents.length; i++){
//					alert(gather.global.nearEvents[i].location.latitude);
//					alert(gather.global.nearEvents[i].location.longitude);
					var eCoordinates = {
							latitude: gather.global.nearEvents[i].location.latitude,
							longitude: gather.global.nearEvents[i].location.longitude
							}
					console.log(JSON.stringify(gather.global.nearEvents[i]));
					placeEstablishedEventMarker(gather.global.nearEvents[i], true);
					
					establishedEvents[gather.global.nearEvents[i].id] = gather.global.nearEvents[i];
					
				}
				loadEventsFirstView(userCoordinates);
			},
			error: function(jqXHR, textStatus, errorThrown) {
//			    alert(jqXHR.status);
//			    alert(textStatus);
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
	
	function joinedEvents() {
		$.ajax({
		 	accepts: "application/json",
			type : "GET",
			url : "/rest/events/userJoined",
			contentType: "application/json; charset=UTF-8",
			success : function(returnvalue) {
				gather.global.joinedEvents = returnvalue.results;
				for(var i = 0; i < gather.global.joinedEvents.length; i++){
					establishedEvents[gather.global.joinedEvents[i].id] = gather.global.joinedEvents[i];			
				}
				loadJoinedEvents();
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
	
//	function joinedEvents() {
//		//alert(gather.global.email)
//		var nEvents = gather.global.nearEvents;
//		for (var i = 0; i < nEvents.length; i++){
//			alert(nEvents[i].participants[0].email);
//			if (nEvents[i].participants[i].email == gather.global.email) {
//				gather.global.joinedEvents.push(nEvents[i]);
//			}
//		}
//	}
	


	function ownedEvents() {
		$.ajax({
		 	accepts: "application/json",
			type : "GET",
			url : "/rest/events/userOwned",
			contentType: "application/json; charset=UTF-8",
			success : function(returnvalue) {
				gather.global.ownedEvents = returnvalue.results;
				for(var i = 0; i < gather.global.ownedEvents.length; i++){
					establishedEvents[gather.global.ownedEvents[i].id] = gather.global.ownedEvents[i];			
				}
				loadOwnedEvents();
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
	
	this.determineCoordByZipCode = function(zipCode) {
		
		console.log("The user denied the request for geolocation.");
    	var httpRequest = new XMLHttpRequest();
        httpRequest.open("GET", 'zipcode.csv', false);
        httpRequest.send(null);
        //alert( httpRequest.responseText );
        CSVContents = httpRequest.responseText;
        //console.log($.csv.toObjects(CSVContents));
//        var zipcode = prompt('Please enter your Zip','Zip Code');
//        if (zipcode == null || zipcode == "") {
//            alert("you did not enter a zip please try again");
//            zipcode = prompt('Please enter your Zip','Zip Code');
//        }
        var zipList = $.csv.toObjects(CSVContents);
        
        console.log(zipList);
        
        var uCoordinates = null
        
//        jQuery.grep(zipList, function( zip, i ) {
//        	if (zip == zipCode) {
//        		var i = zipList.index(zipCode)
//        		uCoordinates = {
//      				latitude: zipList[i].latitude,
//    				longitude: zipList[i].longitude
//    				};
//        	} 
//        });
        
        for (var i in zipList) {
        	if(zipList[i].zip == zipCode){
				uCoordinates = {
				latitude: zipList[i].latitude,
				longitude: zipList[i].longitude
				}
        	}
        }
        if (uCoordinates == null) {
        	return -1;
        } else {
        	processUserCoordinates(uCoordinates);
        	return 0;
        }
        //alert(uCoordinates.latitude + uCoordinates.longitude);
    }
	
	function displayGeolocationUnsupportedModal() {
		$("#geolocation-unsupported-modal").modal("show");
	}

	function displayGeneralFailureModal() {
		$("#general-failure-modal").modal("show");
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
			success: function(response) {
				if(typeof(successCallback) === "function") {
					successCallback(response.result);
					
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
					var eventMarker = establishedEvent.eventMarker;
					map.removeLayer(eventMarker);
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
			success: function(response) {
				if(typeof(successCallback) === "function") {
					successCallback(response.result);
					
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
}

function determineCoordByZipCode1(zipCode) {
	
	console.log("The user denied the request for geolocation.");
	var httpRequest = new XMLHttpRequest();
    httpRequest.open("GET", 'zipcode.csv', false);
    httpRequest.send(null);
    //alert( httpRequest.responseText );
    CSVContents = httpRequest.responseText;
    var zipList = $.csv.toObjects(CSVContents);
    
    console.log(zipList);
    
    var uCoordinates = null
    
    for (var i in zipList) {
    	if(zipList[i].zip == zipCode){
			uCoordinates = {
			latitude: zipList[i].latitude,
			longitude: zipList[i].longitude
			}
    	}
    }
    if (uCoordinates == null) {
    	return -1;
    } else {
    	return 0;
    }
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