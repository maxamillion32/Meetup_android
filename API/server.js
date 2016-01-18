var mongoose = require('mongoose');
var models = require ('./db');
var app = require('./config');

mongoose.connect('mongodb://localhost:27017');

var db = mongoose.connection;

var middleware =
	function(req, res, next) {
		models.meeting.findById(id(req.body._id), function(err, meetup) {
			if(err || !meetup)
				return next(new Error());
			next();
		})
	}

//TODO: Better error handling

//TODO: Clean this file up, move routing and
//database operations to separate files.
//This file should only really
//contain imports and the server.

db.on('error', function (err) {
console.log('connection error', err);
});
db.once('open', function () {
console.log('connected.');
});

//Middleware for unexisting meetup checks and error handling.
app.use('/meetup/edit',		middleware);
app.use('/meetup/delete',	middleware);
app.use('/meetup/invite',	middleware);
app.use('/user/attendance',	middleware);
app.use('/meetup/get',		middleware);

app.post('/meetup/delete', function(req, res) {
	models.meeting.findByIdAndRemove(id(req.body._id),
		function(err, meetup) {
			models.user.update(
				{ 'meetings._id': mongoose.Types.ObjectId(id(req.body._id)) },
				{ $pull:
					{
						'meetings': {
							'_id': mongoose.Types.ObjectId(id(req.body._id))
						}
					}
				}, {multi: true},
				function(err){
					if(err) return console.log(err)
					res.status(200).json({ name: meetup.name })
				}
			)
		}
	)
});

app.post('/meetup/get', function(req, res) {
	models.meeting
		.findOne({_id: id(req.body._id)})
		.populate({
			path: 'users',
			select: 'name meetings._id meetings.attendance'
		})
		.exec(function(err, meetup) {
			if(err) return console.log(err);
			res.status(200).json(filterUsersData(meetup));
		})
})

app.post('/user/meetups', function(req, res) {
	models.user
		.findOne({_id : id(req.body._id)})
		.populate({
			path: 'meetings._id',
			select: 'name description users'
		})
		.exec(function(err, user) {
			if(err) return console.log(err);
			res.status(200).json(user);
		})
});

app.post('/user/attendance', function(req, res) {
	models.user
		.update(
			{
				_id: id(req.body.uid),
				'meetings._id': mongoose.Types.ObjectId(req.body._id)
			},
			{
				$set: {
					'meetings.$.attendance': req.body.attendance
				}
			},
			function(err) {
				if(err) return console.log(err);
				models.meeting
					.findOne({_id: id(req.body._id)})
					.populate({
						path: 'users',
						select: 'name meetings._id meetings.attendance'
					})
					.exec(function(err, meetup) {
						if(err) return console.log(err);
						res.status(200).json(filterUsersData(meetup));
					})
			})
});

app.post('/meetup/invite', function(req, res) {
	models.user.findOneAndUpdate(
		{_email: req.body._email, 'meetings._id': {$ne: id(req.body._id)}},
		{$push: {
				'meetings' : {
					_id : id(req.body._id)
				}
			}
		}, { new: true },
		function(err, user) {
			if(err) return console.log(err);
			if(user) {
				models.meeting.findByIdAndUpdate(
					id(req.body._id),
					{ $addToSet:{
							users: user._id
						}
					},
					{ new: true }
				)
				.populate({
					path: 'users',
					select: 'name meetings._id meetings.attendance'
				})
				.exec(function(err, meetup) {
					if(err) console.log(err);
					res.status(200).json(filterUsersData(meetup));
				});
			}
			else res.status(200).json({});
		}
	)
});

app.post('/meetup/create', function(req, res, next) {
		models.meeting.create(
			{ users: [id(req.body._id)] },
			function(err, meetup) {
				if(err) return console.log(err);
				models.user.update(
					{ _id: id(req.body._id) },
					{$addToSet: {
						'meetings' :
							{
								attendance: 'yes',
								_id: meetup._id
							}
						}
					},
					function(err) {
						if(err) return console.log(err);
						req.meetup = meetup;
						next();
					}
				)
			}
		)
	},
	function(req, res) {
		models.meeting
			.findOne({_id: id(req.meetup._id)})
			.populate({
				path: 'users',
				select: 'name meetings._id meetings.attendance'
			})
			.exec(function(err, meetup) {
				if(err) return console.log(err);
				res.status(200).json(filterUsersData(meetup));
			})
	}
);

app.post('/meetup/edit', function(req, res) {
	var dataToUpdate = req.body.date ?
		withDate(req.body) :
		{
			description : req.body.description,
			name 		: req.body.name
		};

	models.meeting.findByIdAndUpdate(id(req.body._id), dataToUpdate,
		{ new: true },
		function(err, meetup) {
			if(err) console.log(err);
			res.status(200).json(dateInMillSecs(meetup));
		}
	)
});

app.post('/user/entry', function(req, res) {
	models.user.findOne(req.body,
		function(err, person) {
			if(err) return console.log(err);
			if(!person) {
				models.user.create(req.body,
					function(err, newuser) {
						if(err) return console.log(err);
						res.status(200).json(newuser);
					}
				);
			}
			else res.status(200).json(person);
		})
});

app.use(function(err, req, res, next) {
  console.error("error");
  res.status(404).send('Not Found. This item has been deleted');
});

app.listen(80, "0.0.0.0");

function filterUsersData(meetup) {
	meetup.users = meetup.users.map(function(user) {
		return user.meetings = user.meetings.filter(function(usrMeetup) {
			return String(usrMeetup._id) === String(meetup._id);
		})
	})
	return dateInMillSecs(meetup);
}


//Will not be needed in serving our web client.
function dateInMillSecs(meetup) {
	var millTo = meetup.date.to.getTime();
	var millFrom = meetup.date.from.getTime();
	return {
		_id  :			meetup._id,
		name : 			meetup.name,
		description : 	meetup.description,
		users :			meetup.users,
		date : {
			from : 	millFrom,
			to: 	millTo
		}
	};
}

//This function is made purely for the purpose of
//parsing date objects from the request body,
//sent to the API by our Android client.
//Will not be needed in serving our web client.
function withDate(body) {
	var shortFrom = body.date.from;
	var shortTo = body.date.to;
	var init = new Date();

	var _from = shortFrom.date ?
		new Date(
			shortFrom.date.yr, shortFrom.date.mon,
			shortFrom.date.d, shortFrom.time.hrs,
			shortFrom.time.mins
		) :
		init.setHours(shortFrom.time.hrs, shortFrom.time.mins, null);

	var _to = shortTo.date ?
		new Date(
			shortTo.date.yr, shortTo.date.mon,
			shortTo.date.d, shortTo.time.hrs,
			shortTo.time.mins
		) :
		init.setHours(shortTo.time.hrs, shortTo.time.mins, null);
	return {
		description : body.description,
		name 		: body.name,
		date		: { from: _from, to: _to}
	}
}

function id(id) {
	return mongoose.Types.ObjectId(id);
}
