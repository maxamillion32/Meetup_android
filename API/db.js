var mongoose = require('mongoose')
  , Schema = mongoose.Schema

var user = Schema({
	name		: String,
	_email		: { type: String, unique: true },
	meetings	: [
		{
			attendance	: { type: String, default: 'undecided' },
			_id			: { type: Schema.Types.ObjectId, ref: 'meeting' }
		}
	]
});

var meeting = Schema({
	name		: { type: String, 	default: 'An Unnamed Meetup' },
	description	: { type:String, 	default: 'No Description Available' },
	date		: {
		from	: { type: Date, default: Date.now },
		to		: { type: Date, default: Date.now }
	},
	users		: [
		{
			type: Schema.Types.ObjectId, ref: 'user'
		}
	]
});

module.exports.user    = mongoose.model('user', user);
module.exports.meeting = mongoose.model('meeting', meeting);
