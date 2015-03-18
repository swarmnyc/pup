﻿using System.Collections.Generic;
using System.Linq;
using MongoDB;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public interface IGameService : IMongoService<Game>
    {
        IQueryable<Game> Filter(GameFilter filter);
    }
}