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

	var foodLocationSearchRadiusInMiles = 2.0;
	var foodLocationSearchRadiusInMeters = foodLocationSearchRadiusInMiles * 1609.34;

	var currentUserCoordinates = null;
	var userMarker = null;
	var searchRadiusCircle = null;

	var geolocationSupported = (navigator.geolocation ? true : false);

	var newFoodLocations = [];

	var establishedFoodLocations = [];

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

			if(typeof(successCallback) === "function") {
				successCallback(userCoordinates);
			}
		}, function(error) {
			if(error.code == error.PERMISSION_DENIED) {
				//determineCoordByZipCode();
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

				currentUserCoordinates = userCoordinates;
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

			searchRadiusCircle = L.circle(markerPosition, foodLocationSearchRadiusInMeters, searchRadiusCircleOptions);
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
        
        for (i in zipList) {
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
}
