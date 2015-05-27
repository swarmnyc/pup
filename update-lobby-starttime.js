db.Lobbies.update(
   {},
   {$set:{StartTimeUtc:ISODate("2016-01-01T01:00:00.000Z")}},
   {multi:true}

);