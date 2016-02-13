'use strict';

const React = require('react');
const client = require('./client');

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {registereds: []};
	}

	componentDidMount() {
		client({method: 'GET', path: '/api/registereds'}).done(response => {
			this.setState({registereds: response.entity._embedded.registereds});
		});
	}

	render() {
		return (
			<RegisteredList registereds={this.state.registereds}/>
		)
	}
}

class RegisteredList extends React.Component{
	render() {
		var registereds = this.props.registereds.map(registered =>
			<Registered key={registered._links.self.href} registered={registered}/>
		);
		return (
			<table>
				<tr>
					<th>First Name</th>
					<th>Last Name</th>
					<th>Age</th>
				</tr>
				{registereds}
			</table>
		)
	}
}

class Registered extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.registered.firstName}</td>
				<td>{this.props.registered.lastName}</td>
				<td>{this.props.registered.age}</td>
			</tr>
		)
	}
}

React.render(
	<App />,
	document.getElementById('react')
)
