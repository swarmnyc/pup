db.Lobbies.update(
   {},
   {$set:{StartTimeUtc:ISODate()}},
   {multi:true}
);