using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.ApiControllers
{
    public class GameController : ApiController
    {
        private readonly IGameService _gameService;

        public GameController(IGameService gameService)
        {
            _gameService = gameService;
        }

        // GET: api/Game
        public IEnumerable<Game> Get([FromUri]GameFilter filter)
        {
            return _gameService.Filter(filter);
        }

        // GET: api/Game/5
        public Game Get(string id)
        {
            return _gameService.GetById(id);
        }
    }
}
