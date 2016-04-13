function loadEventsFirstView(userCoordinates) {
	var events = gather.global.nearEvents
	appentToTable("eventTable", events, userCoordinates, "around you");
}

function loadJoinedEvents(userCoordinates) {
	var events = gather.global.joinedEvents;
	appentToTable("eventTable", events, userCoordinates, "that you have joined");
}

function loadOwnedEvents(userCoordinates) {
	var events = gather.global.ownedEvents;
	appentToTable("eventTable", events, userCoordinates, "that you own");
}

function appentToTable(tableClass, events, userCoordinates, message){
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
//				var rating = events[i].feedbacks.rating
				var category = events[i].category.name;
				var unixtime = events[i].occurrences[0].timestamp;
				var datetime = new Date( unixtime );
				var time = datetime.toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});
				var date = datetime.toLocaleDateString();
				var city = events[i].location.city;
				var streetAddress = events[i].location.streetAddr;
				var state = events[i].location.state;
				var zipCode = events[i].location.zipCode;
				var address = streetAddress + ', ' + city + ', ' + state + ' ' + zipCode;
				var description = events[i].description;
				$('.' + tableClass).append(
					'<tr style="cursor: pointer;" onclick="mapManager.showPop('+ eventId +');">' +
						'<td colspan="3">  ' +
							'<div class="media event-card"> ' +
								'<a href="#" class="pull-left"> ' +
									'<img height="60px;" width="60px;" src="http://content.sportslogos.net/logos/27/1756/full/yp7ll78otycmyef0hqma49n1a.gif" class="media-photo"/> ' +
								'</a>' +
								'<div class="media-body">' +
									'<span class="pull-right">'+ dist +' mi</span>' +
									'<span style="margin-right:40px;" class="pull-right">'+ '[rating]' +'</span>' +
									'<h4 class="list-title">'+ title +'</h4>' +
									'<span class="pull-right"></span>' +
									'<p style="display:none;" eventId="' + eventId + '"></p>' +
									'<p class="list-description">Category: '+ category +'</p>' +
									'<p class="list-description">Date: '+ date + ', ' + time +'</p>' +
									'<p class="list-description">Place: '+ address +'</p>' +
									'<p class="list-description">Description: '+ description +'</p>' +
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